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

import lombok.Cleanup;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Utility class for running commands.
 *
 * @author Foulest
 */
@Data
public class CommandUtil {

    /**
     * Runs a command.
     *
     * @param command Command to run.
     * @param async   Whether to run the command asynchronously.
     */
    public static void runCommand(String command, boolean async) {
        @NotNull Runnable commandRunner = () -> {
            try {
                @NotNull ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
                processBuilder.redirectErrorStream(true);
                @NotNull Process process = processBuilder.start();

                process.onExit().thenRun(() -> {
                    try {
                        process.waitFor();
                    } catch (InterruptedException ex) {
                        DebugUtil.warn("Failed to wait for command: " + command, ex);
                        Thread.currentThread().interrupt();
                    }
                }).join();
            } catch (IOException ex) {
                DebugUtil.warn("Failed to run command: " + command, ex);
            }
        };

        if (async) {
            DebugUtil.debug("Running command async: " + command);
            CompletableFuture.runAsync(commandRunner);
        } else {
            DebugUtil.debug("Running command: " + command);
            CompletableFuture.runAsync(commandRunner).join();
        }
    }

    /**
     * Runs a command.
     *
     * @param command      Command to run.
     * @param async        Whether to run the command asynchronously.
     * @param lineConsumer Consumer to consume the output of the command.
     */
    static void runCommand(String command, boolean async,
                           @NotNull LineConsumer lineConsumer) {
        @NotNull Runnable commandRunner = () -> {
            try {
                @NotNull ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
                processBuilder.redirectErrorStream(true);
                @NotNull Process process = processBuilder.start();

                @Cleanup InputStream inputStream = process.getInputStream();

                try (@NotNull BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    String line;

                    while (true) {
                        line = bufferedReader.readLine();

                        if (line == null) {
                            break;
                        }

                        lineConsumer.consume(line);
                    }
                }

                process.onExit().thenRun(() -> {
                    try {
                        process.waitFor();
                    } catch (InterruptedException ex) {
                        DebugUtil.warn("Failed to wait for command: " + command, ex);
                        Thread.currentThread().interrupt();
                    }
                }).join();
            } catch (IOException ex) {
                DebugUtil.warn("Failed to run command: " + command, ex);
                Thread.currentThread().interrupt();
            }
        };

        if (async) {
            DebugUtil.debug("Running command async: " + command);
            CompletableFuture.runAsync(commandRunner);
        } else {
            DebugUtil.debug("Running command: " + command);
            CompletableFuture.runAsync(commandRunner).join();
        }
    }

    /**
     * Runs a PowerShell command.
     *
     * @param command Command to run.
     * @param async   Whether to run the command asynchronously.
     */
    public static void runPowerShellCommand(String command, boolean async) {
        runCommand("PowerShell -ExecutionPolicy Unrestricted -Command \"" + command + "\"", async);
    }

    /**
     * Runs a PowerShell command.
     *
     * @param command Command to run.
     * @param async   Whether to run the command asynchronously.
     */
    private static void runPowerShellCommand(String command, boolean async,
                                             @NotNull LineConsumer lineConsumer) {
        runCommand("PowerShell -ExecutionPolicy Unrestricted -Command \"" + command + "\"", async, lineConsumer);
    }

    /**
     * Runs a command and returns the output.
     *
     * @param command The command to run.
     * @param display Whether to display the output.
     * @param async   Whether to run the command asynchronously.
     * @return The output of the command.
     */
    public static @NotNull List<String> getCommandOutput(String command, boolean display, boolean async) {
        @NotNull List<String> output = new ArrayList<>();

        runCommand(command, async, line -> {
            output.add(line);

            if (display && !line.trim().isEmpty()) {
                DebugUtil.debug("Command output: " + line);
            }
        });
        return output.isEmpty() ? Collections.singletonList("") : output;
    }

    /**
     * Runs a PowerShell command and returns the output.
     *
     * @param command The command to run.
     * @param display Whether to display the output.
     * @param async   Whether to run the command asynchronously.
     * @return The output of the command.
     */
    public static @NotNull List<String> getPowerShellCommandOutput(String command, boolean display, boolean async) {
        @NotNull List<String> output = new ArrayList<>();

        runPowerShellCommand(command, async, line -> {
            output.add(line);

            if (display && !line.trim().isEmpty()) {
                DebugUtil.debug("Command output: " + line);
            }
        });
        return output.isEmpty() ? Collections.singletonList("") : output;
    }

    /**
     * Consumer for consuming lines.
     */
    @FunctionalInterface
    public interface LineConsumer {

        /**
         * Consumes a line.
         *
         * @param line The line to consume.
         */
        void consume(String line);
    }
}
