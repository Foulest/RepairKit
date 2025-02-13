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

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Utility class for debugging.
 *
 * @author Foulest
 */
@Data
public class DebugUtil {

    private static final Path logFile = Paths.get(System.getenv("TEMP") + "\\RepairKit.log");
    private static final Lock lock = new ReentrantLock();

    /**
     * Prints a debug message to a log file.
     *
     * @param message The message to print.
     */
    public static void debug(@NotNull String message) {
        // Writes the message to the console.
        System.out.println(message);

        @NotNull String timeStampedMessage = "[" + LocalTime.now() + "] " + message + "\n";

        // Writes the message to the log file.
        lock.lock();
        try {
            Files.writeString(logFile, timeStampedMessage, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Prints a warning message to a log file.
     *
     * @param message The message to print.
     * @param ex      The exception to print.
     */
    public static void warn(@NotNull String message, @NotNull Exception ex) {
        Throwable cause = ex.getCause();
        String exMessage = ex.getMessage();
        String causeMessage = cause.getMessage();
        StackTraceElement[] stackTrace = ex.getStackTrace();

        debug("[WARNING] " + message
                + (exMessage == null ? "" : " (Message: " + exMessage + ")")
                + " (Cause: " + causeMessage + ")"
                + (stackTrace == null ? "" : " (Stack Trace: " + Arrays.toString(stackTrace) + ")")
        );
    }

    /**
     * Creates a log file in the user's temp directory.
     *
     * @param args The command line arguments.
     */
    public static void createLogFile(String[] args) {
        try {
            // Deletes the old log file.
            CommandUtil.runCommand("del /f /q \"" + System.getenv("TEMP") + "\\RepairKit.log\"", false);

            // Creates a new log file.
            if (!Files.exists(logFile)) {
                Files.createFile(logFile);
            }

            // Writes the initial message to the log file.
            printSystemInfo(args);
        } catch (IOException ex) {
            warn("Failed to create log file", ex);
        }
    }

    /**
     * Prints system information to the log file.
     *
     * @param args The command line arguments.
     */
    private static void printSystemInfo(String[] args) {
        debug("Starting RepairKit with arguments: \"" + String.join(" ", args) + "\"");

        @NotNull List<String> securitySoftware = CommandUtil.getPowerShellCommandOutput("Get-CimInstance -Namespace"
                        + " root/SecurityCenter2 -ClassName AntivirusProduct | Select-Object -ExpandProperty displayName",
                false, false);

        @NotNull DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ROOT);
        @NotNull String formattedDate = LocalDate.now().format(dateFormatter);

        @NotNull List<String> cpuInfo = CommandUtil.getCommandOutput("wmic cpu get name,NumberOfCores,NumberOfLogicalProcessors", false, false);
        @NotNull List<String> memoryInfo = CommandUtil.getCommandOutput("wmic memorychip get capacity", false, false);
        @NotNull List<String> gpuInfo = CommandUtil.getCommandOutput("wmic path win32_VideoController get name", false, false);

        debug("");
        debug("RepairKit Version: " + UpdateUtil.getVersionFromProperties());
        debug("System Date: " + formattedDate);
        debug("");
        debug("OS Information");

        debug("- Operating System: " + System.getProperty("os.name") + " ("
                + System.getProperty("os.version") + " - " + System.getProperty("os.arch") + ")");

        debug("- Java Version: " + System.getProperty("java.version") + " ("
                + System.getProperty("java.vendor") + ")");

        debug("- Java Home: " + System.getProperty("java.home"));
        debug("- User Directory: " + System.getProperty("user.dir"));
        debug("- Temp Directory: " + System.getenv("TEMP"));

        boolean empty = securitySoftware.isEmpty();
        debug("- Security Software: " + (empty ? "No Antivirus Found" : String.join(", ", securitySoftware)));

        debug("");
        debug("Hardware Information");

        debug(formatCpuInfo(cpuInfo));
        debug(formatMemoryInfo(memoryInfo));
        debug(formatGpuInfo(gpuInfo));

        debug("- Network Connection: " + UpdateUtil.CONNECTED_TO_INTERNET);
        debug("");
    }

    /**
     * Formats the CPU information.
     *
     * @param cpuInfo The CPU information.
     * @return The formatted CPU information.
     */
    private static @NotNull String formatCpuInfo(@NotNull Iterable<String> cpuInfo) {
        for (@NotNull String line : cpuInfo) {
            if (line.trim().isEmpty() || line.contains("Name")) {
                continue;
            }

            String @NotNull [] parts = line.trim().split("\\s{2,}");

            if (parts.length == 3) {
                return "- CPU: " + parts[0] + " (" + parts[1] + "c, " + parts[2] + "t)";
            }
        }
        return "- CPU: Information not available";
    }

    /**
     * Formats the memory information.
     *
     * @param memoryInfo The memory information.
     * @return The formatted memory information.
     */
    private static @NotNull String formatMemoryInfo(@NotNull Iterable<String> memoryInfo) {
        long totalMemory = 0;

        for (@NotNull String line : memoryInfo) {
            @NotNull String trim = line.trim();

            if (trim.contains("No Instance(s) Available.")) {
                return "- Memory: Information not available";
            }

            if (trim.isEmpty() || line.contains("Capacity")) {
                continue;
            }

            try {
                totalMemory += Long.parseLong(trim);
            } catch (NumberFormatException ex) {
                warn("Failed to parse memory info", ex);
            }
        }

        long totalMemoryGB = totalMemory / (1024 * 1024 * 1024);
        return "- Memory: " + totalMemoryGB / 2 + " GB available (" + totalMemoryGB + " GB total)";
    }

    /**
     * Formats the GPU information.
     *
     * @param gpuInfo The GPU information.
     * @return The formatted GPU information.
     */
    private static @NotNull String formatGpuInfo(@NotNull Iterable<String> gpuInfo) {
        for (@NotNull String line : gpuInfo) {
            if (line.trim().isEmpty() || line.contains("Name")) {
                continue;
            }
            return "- GPU: " + line.trim();
        }
        return "- GPU: Information not available";
    }
}
