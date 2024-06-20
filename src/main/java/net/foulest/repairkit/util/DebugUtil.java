package net.foulest.repairkit.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static net.foulest.repairkit.util.CommandUtil.getCommandOutput;
import static net.foulest.repairkit.util.UpdateUtil.CONNECTED_TO_INTERNET;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DebugUtil {

    private static final Path logFile = Paths.get(System.getenv("TEMP") + "\\RepairKit.log");
    private static final ReentrantLock lock = new ReentrantLock();

    static {
        // Ensure the log file exists when the class is loaded.
        createLogFile();
    }

    /**
     * Prints a debug message to a log file.
     *
     * @param message The message to print.
     */
    public static void debug(@NotNull String message) {
        // Writes the message to the console.
        System.out.println(message);

        String timeStampedMessage = "[" + LocalTime.now() + "] " + message + "\n";

        // Writes the message to the log file.
        lock.lock();
        try {
            Files.write(logFile, timeStampedMessage.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void printSystemInfo(String[] args) {
        debug("Starting RepairKit with arguments: \"" + String.join(" ", args) + "\"");

        List<String> securitySoftware = CommandUtil.getPowerShellCommandOutput("Get-CimInstance -Namespace"
                        + " root/SecurityCenter2 -ClassName AntivirusProduct | Select-Object -ExpandProperty displayName",
                false, false);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String formattedDate = LocalDate.now().format(dateFormatter);

        List<String> cpuInfo = getCommandOutput("wmic cpu get name,NumberOfCores,NumberOfLogicalProcessors", false, false);
        List<String> memoryInfo = getCommandOutput("wmic memorychip get capacity", false, false);
        List<String> gpuInfo = getCommandOutput("wmic path win32_VideoController get name", false, false);

        debug("");
        debug("RepairKit Version: " + UpdateUtil.getLatestReleaseVersion());
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
        debug("- Security Software: " + (securitySoftware.isEmpty() ? "No Antivirus Found" : String.join(", ", securitySoftware)));

        debug("");
        debug("Hardware Information");

        debug(formatCpuInfo(cpuInfo));
        debug(formatMemoryInfo(memoryInfo));
        debug(formatGpuInfo(gpuInfo));

        debug("- Network Connection: " + CONNECTED_TO_INTERNET);
        debug("");
    }

    public static void createLogFile() {
        try {
            if (!Files.exists(logFile)) {
                Files.createFile(logFile);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static @NotNull String formatCpuInfo(@NotNull List<String> cpuInfo) {
        for (String line : cpuInfo) {
            if (line.trim().isEmpty() || line.contains("Name")) {
                continue;
            }

            String[] parts = line.trim().split("\\s{2,}");

            if (parts.length == 3) {
                return "- CPU: " + parts[0] + " (" + parts[1] + "c, " + parts[2] + "t)";
            }
        }
        return "- CPU: Information not available";
    }

    private static @NotNull String formatMemoryInfo(@NotNull List<String> memoryInfo) {
        long totalMemory = 0;

        for (String line : memoryInfo) {
            if (line.trim().isEmpty() || line.contains("Capacity")) {
                continue;
            }

            try {
                totalMemory += Long.parseLong(line.trim());
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }

        long totalMemoryGB = totalMemory / (1024 * 1024 * 1024);
        return "- Memory: " + totalMemoryGB / 2 + " GB available (" + totalMemoryGB + " GB total)";
    }

    private static @NotNull String formatGpuInfo(@NotNull List<String> gpuInfo) {
        for (String line : gpuInfo) {
            if (line.trim().isEmpty() || line.contains("Name")) {
                continue;
            }
            return "- GPU: " + line.trim();
        }
        return "- GPU: Information not available";
    }
}
