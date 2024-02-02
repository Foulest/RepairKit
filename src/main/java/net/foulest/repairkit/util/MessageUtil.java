package net.foulest.repairkit.util;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for sending messages.
 *
 * @author Foulest
 * @project RepairKit
 */
public final class MessageUtil {

    public static Logger logger = Logger.getLogger("RepairKit");

    /**
     * Logs a message to the console.
     *
     * @param level   The level to log the message at.
     * @param message The message to log.
     */
    public static void log(Level level, String message) {
        logger.log(level, message);
    }

    /**
     * Prints an exception's message as a warning to the console.
     *
     * @param ex The exception to print.
     */
    public static void printException(@NotNull Throwable ex) {
        logger.log(Level.WARNING, "An error occurred: " + ex.getLocalizedMessage()
                + " (Caused by: " + ex.getCause() + ")");
    }
}
