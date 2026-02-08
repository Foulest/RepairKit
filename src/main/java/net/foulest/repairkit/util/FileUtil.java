/*
 * RepairKit - an all-in-one Java-based Windows repair and maintenance toolkit.
 * Copyright (C) 2026 Foulest (https://github.com/Foulest)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package net.foulest.repairkit.util;

import lombok.Data;
import net.foulest.repairkit.RepairKit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility class for file operations.
 *
 * @author Foulest
 */
@Data
public class FileUtil {

    /**
     * The temporary directory for the program.
     */
    public static final File tempDirectory = new File(System.getenv("TEMP") + "\\RepairKit");

    /**
     * Unzips a file.
     *
     * @param fileZip  The file to unzip.
     * @param fileDest The destination to unzip the file to.
     */
    @SuppressWarnings("ThrowCaughtLocally")
    public static void unzipFile(String fileZip, String fileDest) {
        fileZip = fileZip.replace("%temp%", System.getenv("TEMP"));
        fileDest = fileDest.replace("%temp%", System.getenv("TEMP"));

        DebugUtil.debug("Unzipping file: " + fileZip + " to " + fileDest);

        if (fileZip.endsWith(".zip")) {
            try {
                @NotNull Path sourcePath = Paths.get(fileZip);
                @NotNull Path targetPath = Paths.get(fileDest);

                try (@NotNull ZipInputStream zis = new ZipInputStream(Files.newInputStream(sourcePath))) {
                    @Nullable ZipEntry zipEntry = zis.getNextEntry();

                    while (zipEntry != null) {
                        @NotNull String entryName = zipEntry.getName();
                        DebugUtil.debug("Opening zip entry: " + entryName);
                        @NotNull Path newPath = targetPath.resolve(entryName).normalize();

                        // Check for path traversal vulnerabilities
                        if (!newPath.startsWith(targetPath)) {
                            throw new IOException("Bad zip entry (potential path traversal): " + entryName);
                        }

                        if (zipEntry.isDirectory()) {
                            DebugUtil.debug("Creating directory: " + newPath);
                            Files.createDirectories(newPath);
                        } else {
                            DebugUtil.debug("Creating file: " + newPath);
                            Path parent = newPath.getParent();
                            Files.createDirectories(parent);

                            DebugUtil.debug("Copying file: " + newPath);
                            Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                        }

                        DebugUtil.debug("Closing zip entry: " + entryName);
                        zipEntry = zis.getNextEntry();
                    }
                }
            } catch (IOException ex) {
                DebugUtil.warn("Failed to unzip file: " + fileZip + " to " + fileDest, ex);
            }
        } else {
            try (@Nullable InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("bin/7zr.exe")) {
                if (input == null) {
                    JOptionPane.showMessageDialog(null,
                            "Failed to load 7-Zip file.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                saveFile(input, tempDirectory + "\\7zr.exe", true);
                CommandUtil.getCommandOutput("\"" + tempDirectory + "\\7zr.exe\" x \"" + fileZip + "\"" + " -y -o\"" + fileDest, false, false);
            } catch (IOException ex) {
                DebugUtil.warn("Failed to unzip file: " + fileZip + " to " + fileDest, ex);
            }
        }
    }

    /**
     * Saves a file.
     *
     * @param input          The input stream to save.
     * @param path           The path to save the file to.
     * @param replaceOldFile Whether to replace the old file.
     */
    public static void saveFile(@NotNull InputStream input, @NotNull String path, boolean replaceOldFile) {
        DebugUtil.debug("Saving file: " + path);
        @NotNull Path savedFilePath = Paths.get(path);

        try {
            if (Files.exists(savedFilePath)) {
                if (replaceOldFile) {
                    DebugUtil.debug("Deleting old file: " + savedFilePath);
                    Files.delete(savedFilePath);
                } else {
                    return;
                }
            }

            Path parent = savedFilePath.getParent();

            if (!Files.exists(parent)) {
                DebugUtil.debug("Creating directories: " + parent);
                Files.createDirectories(parent);
            }

            DebugUtil.debug("Copying file: " + savedFilePath);
            Files.copy(input, savedFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            DebugUtil.warn("Failed to save file: " + path, ex);
        }
    }

    /**
     * Gets an image icon from a path.
     *
     * @param path The path to get the image icon from.
     * @return The image icon.
     */
    @Contract("_ -> new")
    public static @NotNull ImageIcon getImageIcon(String path) {
        DebugUtil.debug("Getting image icon: " + path);
        ClassLoader classLoader = RepairKit.class.getClassLoader();
        @Nullable URL resource = classLoader.getResource(path);

        if (resource == null) {
            JOptionPane.showMessageDialog(null,
                    "Failed to load image icon: " + path,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return new ImageIcon();
        }
        return new ImageIcon(resource);
    }

    /**
     * Gets a file from the config directory.
     * If it doesn't exist, it will be saved from the resources.
     *
     * @param fileName The name of the file to get.
     * @return The file.
     */
    public static @NotNull File getConfigFile(String fileName) {
        @NotNull File file = new File(System.getProperty("user.dir") + "/config/" + fileName);

        if (!file.exists()) {
            try (@Nullable InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("config/" + fileName)) {
                if (input == null) {
                    JOptionPane.showMessageDialog(null,
                            "Failed to load config file: " + fileName,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return new File(System.getProperty("user.dir") + "/config/" + fileName);
                }

                saveFile(input, System.getProperty("user.dir") + "\\" + "config\\" + fileName, false);
                file = new File(System.getProperty("user.dir") + "/config/" + fileName);
            } catch (IOException ex) {
                DebugUtil.warn("Failed to get config file: " + fileName, ex);
            }
        }
        return file;
    }
}
