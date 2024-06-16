package net.foulest.repairkit.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.util.FormatUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalTime;
import java.util.concurrent.locks.ReentrantLock;

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

        debug("");
        debug("OS Information");
        debug("- Operating System: " + System.getProperty("os.name") + " ("
                + System.getProperty("os.version") + " - " + System.getProperty("os.arch") + ")");
        debug("- Java Version: " + System.getProperty("java.version") + " ("
                + System.getProperty("java.vendor") + ")");
        debug("- Java Home: " + System.getProperty("java.home"));
        debug("- User Directory: " + System.getProperty("user.dir"));
        debug("- Temp Directory: " + System.getenv("TEMP"));

        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor processor = hal.getProcessor();
        GlobalMemory memory = hal.getMemory();

        debug("");
        debug("Hardware Information");

        debug("- CPU: " + processor.getProcessorIdentifier().getName().trim() + " ("
                + processor.getPhysicalProcessorCount() + "c, "
                + processor.getLogicalProcessorCount() + "t)");

        for (GraphicsCard card : hal.getGraphicsCards()) {
            debug("- GPU: " + card.getName().trim() + " (" + FormatUtil.formatBytes(card.getVRam()) + ")");
        }

        debug("- Memory: " + FormatUtil.formatBytes(memory.getAvailable())
                + " / " + FormatUtil.formatBytes(memory.getTotal()));

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
}
