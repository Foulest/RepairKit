package net.foulest.repairkit.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.foulest.repairkit.util.SwingUtil.showErrorDialog;
import static net.foulest.repairkit.util.SwingUtil.updateProgressLabel;

public class CommandUtil {

    public static void runCommand(String command, boolean async) {
        Runnable commandRunner = () -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                process.waitFor();
            } catch (IOException | InterruptedException ex) {
                showErrorDialog(ex, "Run Command");
                Thread.currentThread().interrupt();
            }
        };

        if (async) {
            CompletableFuture.runAsync(commandRunner);
        } else {
            commandRunner.run();
        }
    }

    public static void displayCommandOutput(String command, boolean async) {
        runCommand(command, async, line -> {
            if (!line.trim().isEmpty()) {
                updateProgressLabel(line);
            }
        });
    }

    public static List<String> getCommandOutput(String command, boolean display, boolean async) {
        List<String> output = new ArrayList<>();
        runCommand(command, async, line -> {
            output.add(line);
            if (display && !line.trim().isEmpty()) {
                updateProgressLabel(line);
            }
        });
        return output.isEmpty() ? Collections.singletonList("") : output;
    }

    private static void runCommand(String command, boolean async, LineConsumer lineConsumer) {
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
                showErrorDialog(ex, "Run Command Line Consumer");
                Thread.currentThread().interrupt();
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
