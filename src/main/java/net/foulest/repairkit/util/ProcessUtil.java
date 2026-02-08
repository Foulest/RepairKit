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

/**
 * Utility class for process operations.
 *
 * @author Foulest
 */
@Data
public class ProcessUtil {

    /**
     * Checks if a service is running.
     *
     * @param serviceName - Name of the service to check.
     * @return - Whether the service is running.
     */
    public static boolean isServiceRunning(String serviceName) {
        return CommandUtil.getCommandOutput("sc query \"" + serviceName + "\"",
                false, false).toString().contains("RUNNING");
    }

    /**
     * Checks if a process is running.
     *
     * @param processName - Name of the process to check.
     * @return - Whether the process is running.
     */
    public static boolean isProcessRunning(@NotNull CharSequence processName) {
        return CommandUtil.getCommandOutput("tasklist /FI \"IMAGENAME eq " + processName + "\"",
                false, false).toString().contains(processName);
    }

    /**
     * Kills a process by name.
     *
     * @param processName - Name of the process to kill.
     */
    static void killProcess(@NotNull CharSequence processName) {
        CommandUtil.runCommand("taskkill /F /IM \"" + processName + "\"", false);
    }
}
