package net.foulest.repairkit.util;

import lombok.NonNull;

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
    public static void log(@NonNull Level level, @NonNull String message) {
        logger.log(level, message);
    }
}
