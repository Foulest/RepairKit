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

import static net.foulest.repairkit.util.DebugUtil.debug;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {

    public static final File tempDirectory = new File(System.getenv("TEMP") + "\\RepairKit");

    /**
     * Unzips a file.
     *
     * @param fileZip  The file to unzip.
     * @param fileDest The destination to unzip the file to.
     */
    public static void unzipFile(String fileZip, String fileDest) {
        fileZip = fileZip.replace("%temp%", System.getenv("TEMP"));
        fileDest = fileDest.replace("%temp%", System.getenv("TEMP"));

        debug("Unzipping file: " + fileZip + " to " + fileDest);

        try {
            Path sourcePath = Paths.get(fileZip);
            Path targetPath = Paths.get(fileDest);

            try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(sourcePath))) {
                ZipEntry zipEntry = zis.getNextEntry();

                while (zipEntry != null) {
                    debug("Opening zip entry: " + zipEntry.getName());
                    Path newPath = targetPath.resolve(zipEntry.getName()).normalize();

                    // Check for path traversal vulnerabilities
                    if (!newPath.startsWith(targetPath)) {
                        throw new IOException("Bad zip entry (potential path traversal): " + zipEntry.getName());
                    }

                    if (zipEntry.isDirectory()) {
                        debug("Creating directory: " + newPath);
                        Files.createDirectories(newPath);
                    } else {
                        debug("Creating file: " + newPath);
                        Files.createDirectories(newPath.getParent());

                        debug("Copying file: " + newPath);
                        Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                    }

                    debug("Closing zip entry: " + zipEntry.getName());
                    zipEntry = zis.getNextEntry();
                }
            }
        } catch (IOException ex) {
            debug("[WARN] Failed to unzip file: " + fileZip + " to " + fileDest);
            ex.printStackTrace();
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
        debug("Saving file: " + path);
        Path savedFilePath = Paths.get(path);

        try {
            if (Files.exists(savedFilePath)) {
                if (replaceOldFile) {
                    debug("Deleting old file: " + savedFilePath);
                    Files.delete(savedFilePath);
                } else {
                    return;
                }
            }

            if (!Files.exists(savedFilePath.getParent())) {
                debug("Creating directories: " + savedFilePath.getParent());
                Files.createDirectories(savedFilePath.getParent());
            }

            debug("Copying file: " + savedFilePath);
            Files.copy(input, savedFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            debug("[WARN] Failed to save file: " + path);
            ex.printStackTrace();
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
        debug("Getting image icon: " + path);
        return new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource(path)));
    }
}
