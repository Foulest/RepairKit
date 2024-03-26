package net.foulest.repairkit.util;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommandUtil {

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
            CompletableFuture.runAsync(commandRunner);
        } else {
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

                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
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
            CompletableFuture.runAsync(commandRunner);
        } else {
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
    public static void runPowerShellCommand(String command, boolean async,
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
                System.out.println(line);
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
                System.out.println(line);
            }
        });
        return output.isEmpty() ? Collections.singletonList("") : output;
    }

    @FunctionalInterface
    public interface LineConsumer {

        void consume(String line);
    }
}
