package net.foulest.repairkit.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessUtilTest {

    @Test
    void isServiceRunning() {
        // Test with a service that should always be running on Windows
        assertTrue(ProcessUtil.isServiceRunning("SysMain"));

        // Test with a service that should not exist
        assertFalse(ProcessUtil.isServiceRunning("NonExistentService"));
    }

    @Test
    void isProcessRunning() {
        // Test with a process that should always be running on Windows
        assertTrue(ProcessUtil.isProcessRunning("wininit.exe"));

        // Test with a process that should not exist
        assertFalse(ProcessUtil.isProcessRunning("NonExistentProcess.exe"));
    }
}
