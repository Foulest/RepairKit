package net.foulest.repairkit.util;

import static net.foulest.repairkit.util.CommandUtil.getCommandOutput;

public class ProcessUtil {

    /**
     * Checks if a service is running.
     *
     * @param serviceName Name of the service to check.
     * @return Whether the service is running.
     */
    public static boolean isServiceRunning(String serviceName) {
        return getCommandOutput("sc query " + serviceName,
                false, false).toString().contains("RUNNING");
    }

    /**
     * Checks if a process is running.
     *
     * @param processName Name of the process to check.
     * @return Whether the process is running.
     */
    public static boolean isProcessRunning(String processName) {
        return getCommandOutput("tasklist /FI \"IMAGENAME eq " + processName,
                false, false).toString().contains(processName);
    }
}
