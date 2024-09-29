/*
 * RepairKit - an all-in-one Java-based Windows repair and maintenance toolkit.
 * Copyright (C) 2024 Foulest (https://github.com/Foulest)
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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.foulest.repairkit.RepairKit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility class for file operations.
 *
 * @author Foulest
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtil {

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
                Path sourcePath = Paths.get(fileZip);
                Path targetPath = Paths.get(fileDest);

                try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(sourcePath))) {
                    ZipEntry zipEntry = zis.getNextEntry();

                    while (zipEntry != null) {
                        DebugUtil.debug("Opening zip entry: " + zipEntry.getName());
                        Path newPath = targetPath.resolve(zipEntry.getName()).normalize();

                        // Check for path traversal vulnerabilities
                        if (!newPath.startsWith(targetPath)) {
                            throw new IOException("Bad zip entry (potential path traversal): " + zipEntry.getName());
                        }

                        if (zipEntry.isDirectory()) {
                            DebugUtil.debug("Creating directory: " + newPath);
                            Files.createDirectories(newPath);
                        } else {
                            DebugUtil.debug("Creating file: " + newPath);
                            Files.createDirectories(newPath.getParent());

                            DebugUtil.debug("Copying file: " + newPath);
                            Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                        }

                        DebugUtil.debug("Closing zip entry: " + zipEntry.getName());
                        zipEntry = zis.getNextEntry();
                    }
                }
            } catch (IOException ex) {
                DebugUtil.warn("Failed to unzip file: " + fileZip + " to " + fileDest, ex);
            }
        } else {
            try (InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("bin/7zr.exe")) {
                saveFile(Objects.requireNonNull(input), tempDirectory + "\\7zr.exe", true);
                CommandUtil.getCommandOutput("\"" + tempDirectory + "\\7zr.exe\" x \"" + fileZip + "\"" + " -y -o\"" + fileDest, true, false);
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
    public static void saveFile(InputStream input, String path, boolean replaceOldFile) {
        DebugUtil.debug("Saving file: " + path);
        Path savedFilePath = Paths.get(path);

        try {
            if (Files.exists(savedFilePath)) {
                if (replaceOldFile) {
                    DebugUtil.debug("Deleting old file: " + savedFilePath);
                    Files.delete(savedFilePath);
                } else {
                    return;
                }
            }

            if (!Files.exists(savedFilePath.getParent())) {
                DebugUtil.debug("Creating directories: " + savedFilePath.getParent());
                Files.createDirectories(savedFilePath.getParent());
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
        return new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource(path)));
    }

    /**
     * Gets a file from the config directory.
     * If it doesn't exist, it will be saved from the resources.
     *
     * @param fileName The name of the file to get.
     * @return The file.
     */
    public static File getConfigFile(String fileName) {
        File file = new File(System.getProperty("user.dir") + "/config/" + fileName);

        if (!file.exists()) {
            try (InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("config/" + fileName)) {
                saveFile(Objects.requireNonNull(input), System.getProperty("user.dir") + "\\" + "config\\" + fileName, false);
                file = new File(System.getProperty("user.dir") + "/config/" + fileName);
            } catch (IOException ex) {
                DebugUtil.warn("Failed to get config file: " + fileName, ex);
            }
        }
        return file;
    }
}
