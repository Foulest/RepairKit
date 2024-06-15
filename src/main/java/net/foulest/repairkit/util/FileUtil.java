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
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

        try {
            Path sourcePath = Paths.get(fileZip);
            Path targetPath = Paths.get(fileDest);

            try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(sourcePath))) {
                ZipEntry zipEntry = zis.getNextEntry();

                while (zipEntry != null) {
                    Path newPath = targetPath.resolve(zipEntry.getName()).normalize();

                    // Check for path traversal vulnerabilities
                    if (!newPath.startsWith(targetPath)) {
                        throw new IOException("Bad zip entry (potential path traversal): " + zipEntry.getName());
                    }

                    if (zipEntry.isDirectory()) {
                        Files.createDirectories(newPath);
                    } else {
                        Files.createDirectories(newPath.getParent());
                        Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                    }

                    zipEntry = zis.getNextEntry();
                }
            }
        } catch (IOException ex) {
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
        Path savedFilePath = Paths.get(path);

        try {
            if (Files.exists(savedFilePath)) {
                if (replaceOldFile) {
                    Files.delete(savedFilePath);
                } else {
                    return;
                }
            }

            if (!Files.exists(savedFilePath.getParent())) {
                Files.createDirectories(savedFilePath.getParent());
            }

            Files.copy(input, savedFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
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
        return new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource(path)));
    }

    /**
     * Gets the version of the program.
     *
     * @return The version of the program.
     */
    public static String getVersionFromProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = RepairKit.class.getResourceAsStream("/version.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
                return properties.getProperty("version");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "Unknown";
    }
}
