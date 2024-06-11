package net.foulest.repairkit.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandUtilTest {

    @Test
    void runCommand() {
        assertDoesNotThrow(() -> CommandUtil.runCommand("echo Hello, world!", false));
        assertDoesNotThrow(() -> CommandUtil.runCommand("echo Hello, world!", true));
    }

    @Test
    void runPowerShellCommand() {
        assertDoesNotThrow(() -> CommandUtil.runPowerShellCommand("Write-Output 'Hello, world!'", false));
        assertDoesNotThrow(() -> CommandUtil.runPowerShellCommand("Write-Output 'Hello, world!'", true));
    }

    @Test
    void getCommandOutput() {
        List<String> output = CommandUtil.getCommandOutput("echo Hello, world!", false, false);
        assertEquals(1, output.size());
        assertEquals("Hello, world!", output.get(0));

        output = CommandUtil.getCommandOutput("nonexistentcommand", false, false);
        assertEquals(2, output.size());
        assertTrue(output.get(0).contains("is not recognized as an internal or external command"));
    }

    @Test
    void getPowerShellCommandOutput() {
        List<String> output = CommandUtil.getPowerShellCommandOutput("Write-Output 'Hello, world!'", false, false);
        assertEquals(1, output.size());
        assertEquals("Hello, world!", output.get(0));

        output = CommandUtil.getPowerShellCommandOutput("nonexistentcommand", false, false);
        assertEquals(9, output.size());
        assertTrue(output.get(0).contains("is not recognized as the name of a cmdlet"));
    }
}
