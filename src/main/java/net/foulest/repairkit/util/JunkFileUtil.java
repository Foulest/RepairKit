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
import net.foulest.repairkit.util.config.ConfigLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Utility class for removing junk files from the system.
 *
 * @author Foulest
 */
@Data
public class JunkFileUtil {

    // File extensions to scan for
    // Note: These are protected and can't be modified in the config files
    private static @NotNull Set<String> JUNK_FILE_EXTENSIONS = Set.of(
            "\\.tmp$",
            "\\.temp$",
            "\\.old$",
            "\\.old\\.log$",
            "\\.old\\.txt$",
            "\\.old\\.ver$",
            "\\.dmp$",
            "\\.ds\\_store$",
            "\\.hprof$"
    );

    // Paths to exclude from scanning
    // Note: These are protected and can't be modified in the config files
    private static @NotNull Set<Path> EXCLUDED_PATHS = Set.of(
            Path.of("C:\\Windows\\System32")
    );

    /**
     * Checks for junk files on the system.
     */
    @SuppressWarnings({"unchecked", "NestedMethodCall"})
    public static void removeJunkFiles() {
        // Gets the file extensions to scan for from the config file.
        @NotNull ConfigLoader configLoader = new ConfigLoader(FileUtil.getConfigFile("junk_files.json"));
        Map<String, Object> junkFilesConfig = configLoader.getConfig().get("junkFiles");

        // Returns if the config file is missing.
        if (junkFilesConfig == null) {
            return;
        }

        // Returns if the feature is disabled.
        if (junkFilesConfig.get("enabled") != null
                && !junkFilesConfig.get("enabled").equals(Boolean.TRUE)) {
            return;
        }

        // Gets the file extensions to scan for from the config file.
        Object fileExtensions = junkFilesConfig.get("fileExtensions");
        if (fileExtensions != null && !((Collection<String>) fileExtensions).isEmpty()) {
            JUNK_FILE_EXTENSIONS = Set.copyOf((Collection<String>) fileExtensions);
        }

        // Gets the paths to exclude from scanning from the config file.
        if (junkFilesConfig.get("excludedPaths") != null
                && !((Collection<String>) junkFilesConfig.get("excludedPaths")).isEmpty()) {
            @NotNull Set<Path> excludedPaths = new HashSet<>();

            // Replaces environment variables in the paths and adds them to the set.
            for (@NotNull String path : (Iterable<String>) junkFilesConfig.get("excludedPaths")) {
                @NotNull String fixedPath = path.replace("%temp%", System.getenv("TEMP"))
                        .replace("%USERPROFILE%", System.getenv("USERPROFILE"));
                try {
                    excludedPaths.add(Paths.get(fixedPath).toAbsolutePath().normalize());
                } catch (InvalidPathException ipe) {
                    DebugUtil.warn("Invalid excluded path in config: " + fixedPath, ipe);
                }
            }

            // Adds the excluded paths to the set without overwriting the default excluded paths.
            EXCLUDED_PATHS.addAll(excludedPaths);
        }

        // Empties the Recycle Bin.
        if (junkFilesConfig.get("emptyRecycleBin") != null
                && junkFilesConfig.get("emptyRecycleBin").equals(Boolean.TRUE)) {
            CommandUtil.runPowerShellCommand("Clear-RecycleBin -Force -ErrorAction SilentlyContinue", false);
        }

        // Deletes files in the Temp directory older than one day.
        if (junkFilesConfig.get("cleanUserTempFiles") != null
                && junkFilesConfig.get("cleanUserTempFiles").equals(Boolean.TRUE)) {
            CommandUtil.runPowerShellCommand("Get-ChildItem -Path $env:TEMP -Recurse | Where-Object { $_.LastWriteTime -lt (Get-Date).AddDays(-1) } | Remove-Item -Recurse -Force -ErrorAction SilentlyContinue", false);
        }

        // Deletes files in the Windows temp directory.
        if (junkFilesConfig.get("cleanSystemTempFiles") != null
                && junkFilesConfig.get("cleanSystemTempFiles").equals(Boolean.TRUE)) {
            CommandUtil.runPowerShellCommand("Get-ChildItem -Path $env:windir\\Temp -Recurse | Remove-Item -Recurse -Force -ErrorAction SilentlyContinue", false);
        }

        // Deletes files using the Everything Command Line tool.
        if (junkFilesConfig.get("cleanWithEverything") != null
                && junkFilesConfig.get("cleanWithEverything").equals(Boolean.TRUE)) {
            // Checks if Everything was already running.
            boolean everythingRunningBefore = ProcessUtil.isProcessRunning("Everything-RepairKit.exe");

            // Quietly extracts and launches Everything.
            @NotNull String path = FileUtil.tempDirectory.getPath();
            SwingUtil.launchApplication("Everything.7z", "\\Everything-RepairKit.exe", "-startup", true, path);

            // Extracts the Everything Command Line tool and runs it to delete junk files.
            try (@Nullable InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("bin/es.exe")) {
                if (input == null) {
                    JOptionPane.showMessageDialog(null,
                            "Failed to load Everything Command Line file.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Saves the Everything Command Line tool to the temp directory.
                FileUtil.saveFile(input, FileUtil.tempDirectory + "\\es.exe", true);

                long last24Hours = Instant.now().minus(24, ChronoUnit.HOURS).toEpochMilli();

                for (String extension : JUNK_FILE_EXTENSIONS) {
                    // Gets the list of files to delete for each extension.
                    List<String> files = CommandUtil.getCommandOutput("\"" + FileUtil.tempDirectory + "\\es.exe\" -r " + extension, false, false);

                    for (String file : files) {
                        if (!file.contains(":\\")) {
                            continue;
                        }

                        @NotNull Path filePath = Paths.get(file);

                        // Check if the path is in the excluded paths set
                        if (EXCLUDED_PATHS.stream().anyMatch(excludedPath -> filePath.toAbsolutePath().normalize().startsWith(excludedPath))) {
                            continue;
                        }

                        // Checks if the file is a regular file before attempting to delete it
                        try {
                            BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);

                            // Ignores files accessed in the last 24 hours.
                            if (attrs.lastAccessTime().toMillis() > last24Hours) {
                                continue;
                            }

                            // Only attempts to delete regular files, not directories or symbolic links.
                            if (attrs.isRegularFile()) {
                                try {
                                    Files.delete(filePath);
                                    DebugUtil.debug("Deleted junk file: " + filePath);
                                } catch (IOException ex) {
                                    DebugUtil.warn("Failed to delete file: " + file, ex);
                                }
                            }
                        } catch (IOException ex) {
                            DebugUtil.warn("Failed to read attributes for file: " + file, ex);
                        }
                    }
                }
            } catch (IOException ex) {
                DebugUtil.warn("Failed to extract Everything Command Line file.", ex);
            }

            // Kills the Everything process if it wasn't running before, otherwise leaves it running.
            if (!everythingRunningBefore) {
                ProcessUtil.killProcess("Everything-RepairKit.exe");
            }
        }
    }
}
