package net.foulest.repairkit.util;

import lombok.extern.java.Log;
import net.foulest.repairkit.RepairKit;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for sending messages.
 *
 * @author Foulest
 * @project RepairKit
 */
@Log
public final class MessageUtil {

    /**
     * Prints a detailed message of an exception as a warning to the console, including its type,
     * message, cause, and the location where it occurred without printing the full stack trace.
     *
     * @param ex The exception to print.
     */
    public static void printException(@NotNull Throwable ex) {
        // Basic exception details
        StringBuilder message = new StringBuilder("An error occurred: ");
        message.append(ex.getClass().getName()); // Type of the exception
        message.append(": ").append(ex.getLocalizedMessage()); // Message of the exception

        // Cause of the exception
        Throwable cause = ex.getCause();
        if (cause != null) {
            message.append(" | Caused by: ").append(cause.getClass().getName());
            message.append(": ").append(cause.getLocalizedMessage());
        }

        // Location where the exception was thrown
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        if (stackTraceElements.length > 0) {
            StackTraceElement element = stackTraceElements[0]; // Getting the first element of the stack trace
            message.append(" | At: ").append(element.getClassName()); // Class name
            message.append(".").append(element.getMethodName()); // Method name
            message.append("(").append(element.getFileName()); // File name
            message.append(":").append(element.getLineNumber()).append(")"); // Line number
        }

        // Logging the detailed exception message
        log.warning(message.toString());
    }

    /**
     * Sends a debug message to the console and writes it
     * to the log file if debug mode is enabled.
     *
     * @param message The message to send.
     */
    public static void debug(String message) {
        if (RepairKit.isDebugMode()) {
            log.info(message);

            // Writes the message to the log file
            FileUtil.writeToLogFile(message);
        }
    }
}
