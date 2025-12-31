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
import java.util.Iterator;
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

        debug("- CPU: " + formatCpuInfo(CommandUtil.getPowerShellCommandOutput("Get-CimInstance -ClassName Win32_Processor"
                + " | ForEach-Object {$_.Name; $_.NumberOfCores; $_.NumberOfLogicalProcessors}", false, false)));

        String memoryInfo = CommandUtil.getPowerShellCommandOutput("(Get-CimInstance Win32_PhysicalMemory"
                + " | Measure-Object -Property capacity -Sum).sum /1gb", false, false).toString();
        memoryInfo = memoryInfo.replace("[", "").replace("]", "");
        debug("- Memory: " + memoryInfo + " GB");

        String gpuInfo = CommandUtil.getPowerShellCommandOutput("Get-CimInstance -ClassName Win32_VideoController"
                + " | Select-Object -ExpandProperty Name", false, false).toString();
        gpuInfo = gpuInfo.replace("[", "").replace("]", "");
        debug("- GPU: " + gpuInfo);

        debug("- Network Connection: " + UpdateUtil.CONNECTED_TO_INTERNET);
        debug("");
    }

    /**
     * Formats the CPU information.
     *
     * @param cpuInfo The CPU information (Name, Cores, Threads on separate lines).
     * @return The formatted CPU information.
     */
    private static @NotNull String formatCpuInfo(@NotNull Iterable<String> cpuInfo) {
        String cpuName = null;
        String numberOfCores = null;
        String numberOfThreads = null;

        Iterator<String> iterator = cpuInfo.iterator();
        int lineCount = 0;

        while (iterator.hasNext()) {
            String line = iterator.next().trim();

            if (!line.isEmpty()) {
                lineCount++;

                if (lineCount == 1) {
                    cpuName = line;
                } else if (lineCount == 2) {
                    numberOfCores = line;
                } else if (lineCount == 3) {
                    numberOfThreads = line;
                }
            }
        }

        if (cpuName != null && numberOfCores != null && numberOfThreads != null) {
            return cpuName + " (" + numberOfCores + "c, " + numberOfThreads + "t)";
        } else {
            return "Information not available";
        }
    }
}
