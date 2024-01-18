package net.foulest.repairkit.util;

import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class CommandUtil {

    /**
     * Runs a command.
     *
     * @param command Command to run.
     * @param async   Whether to run the command asynchronously.
     */
    public static void runCommand(@NonNull String command, boolean async) {
        Runnable commandRunner = () -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
                processBuilder.redirectErrorStream(true);

                Process process = processBuilder.start();
                process.waitFor();
            } catch (IOException | InterruptedException ex) {
                Thread.currentThread().interrupt();
                MessageUtil.log(Level.WARNING, "Failed to run command: " + command);
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
     * Runs a command and returns the output.
     *
     * @param command Command to run.
     * @param display Whether to display the output.
     * @param async   Whether to run the command asynchronously.
     * @return The output of the command.
     */
    public static List<String> getCommandOutput(@NonNull String command, boolean display, boolean async) {
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
     * Runs a command.
     *
     * @param command      Command to run.
     * @param async        Whether to run the command asynchronously.
     * @param lineConsumer Consumer to consume the output of the command.
     */
    private static void runCommand(@NonNull String command, boolean async,
                                   @NonNull LineConsumer lineConsumer) {
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
                MessageUtil.log(Level.WARNING, "Failed to run command: " + command);
                ex.printStackTrace();
            }
        };

        if (async) {
            CompletableFuture.runAsync(commandRunner);
        } else {
            commandRunner.run();
        }
    }

    @FunctionalInterface
    private interface LineConsumer {

        void consume(String line);
    }
}
