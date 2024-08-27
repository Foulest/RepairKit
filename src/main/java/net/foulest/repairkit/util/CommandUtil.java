/*
 * RepairKit - an all-in-one Java-based Windows repair and maintenance toolkit.
 * Copyright (C) 2024 Foulest (https://github.com/Foulest)
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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public final class CommandUtil {

    /**
     * Runs a command.
     *
     * @param command Command to run.
     * @param async   Whether to run the command asynchronously.
     */
    public static void runCommand(String command, boolean async) {
        Runnable commandRunner = () -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
                processBuilder.redirectErrorStream(true);

                Process process = processBuilder.start();
                process.waitFor();
            } catch (IOException | InterruptedException ex) {
                Thread.currentThread().interrupt();
                ex.printStackTrace();
            }
        };

        if (async) {
            DebugUtil.debug("Running command async: " + command);
            CompletableFuture.runAsync(commandRunner);
        } else {
            DebugUtil.debug("Running command: " + command);
            commandRunner.run();
        }
    }

    /**
     * Runs a command.
     *
     * @param command      Command to run.
     * @param async        Whether to run the command asynchronously.
     * @param lineConsumer Consumer to consume the output of the command.
     */
    private static void runCommand(String command, boolean async,
                                   LineConsumer lineConsumer) {
        Runnable commandRunner = () -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
                processBuilder.redirectErrorStream(true);

                Process process = processBuilder.start();

                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;

                    while (true) {
                        line = bufferedReader.readLine();

                        if (line == null) {
                            break;
                        }

                        lineConsumer.consume(line);
                    }
                }

                process.waitFor();
            } catch (IOException | InterruptedException ex) {
                Thread.currentThread().interrupt();
                ex.printStackTrace();
            }
        };

        if (async) {
            DebugUtil.debug("Running command async: " + command);
            CompletableFuture.runAsync(commandRunner);
        } else {
            DebugUtil.debug("Running command: " + command);
            commandRunner.run();
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
                                             LineConsumer lineConsumer) {
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
        List<String> output = new ArrayList<>();

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
        List<String> output = new ArrayList<>();

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
