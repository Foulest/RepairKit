package net.foulest.repairkit.util;

import net.foulest.repairkit.RepairKit;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static net.foulest.repairkit.util.CommandUtil.runCommand;
import static net.foulest.repairkit.util.FileUtil.*;

public class SwingUtil {

    /**
     * Adds components to a panel.
     *
     * @param panel      Panel to add components to.
     * @param components Components to add.
     */
    public static void addComponents(JPanel panel, Component @NotNull ... components) {
        for (Component component : components) {
            panel.add(component);
        }
    }

    /**
     * Creates an action button.
     *
     * @param buttonText  Text to display on the button.
     * @param toolTipText Text to display when hovering over the button.
     * @param action      Action to run when the button is clicked.
     */
    public static @NotNull JButton createActionButton(String buttonText, String toolTipText, Runnable action) {
        JButton button = new JButton(buttonText);
        button.setToolTipText(toolTipText);
        button.setBackground(new Color(200, 200, 200));

        button.addActionListener(actionEvent -> {
            try {
                action.run();
            } catch (Exception ex) {
                MessageUtil.printException(ex);
            }
        });
        return button;
    }

    /**
     * Creates an application button without launch arguments.
     *
     * @param buttonText    Text to display on the button.
     * @param toolTipText   Text to display when hovering over the button.
     * @param appResource   Resource to extract.
     * @param appExecutable Executable to run.
     * @param isZipped      Whether the resource is zipped.
     */
    public static @NotNull JButton createAppButton(String buttonText, String toolTipText,
                                                   String appResource, String appExecutable,
                                                   boolean isZipped, String extractionPath) {
        return createAppButton(buttonText, toolTipText, appResource, appExecutable, "", isZipped, extractionPath);
    }

    /**
     * Creates an application button.
     *
     * @param buttonText     Text to display on the button.
     * @param toolTipText    Text to display when hovering over the button.
     * @param appResource    Resource to extract.
     * @param appExecutable  Executable to run.
     * @param isZipped       Whether the resource is zipped.
     * @param extractionPath Path to extract the resource to.
     * @param launchArgs     Arguments to launch the application with.
     */
    public static @NotNull JButton createAppButton(String buttonText, String toolTipText,
                                                   String appResource, String appExecutable,
                                                   String launchArgs, boolean isZipped, String extractionPath) {
        JButton button = new JButton(buttonText);
        button.setToolTipText(toolTipText);
        button.setBackground(new Color(200, 200, 200));

        button.addActionListener(actionEvent -> {
            try {
                launchApplication(appResource, appExecutable, launchArgs, isZipped, extractionPath);
            } catch (Exception ex) {
                MessageUtil.printException(ex);
            }
        });
        return button;
    }

    /**
     * Creates a link button without tooltip text.
     *
     * @param buttonText Text to display on the button.
     * @param command    Command to run when the button is clicked.
     */
    public static @NotNull JButton createLinkButton(String buttonText, String command) {
        JButton button = new JButton(buttonText);
        button.setToolTipText("");
        button.setBackground(new Color(200, 200, 200));
        button.addActionListener(actionEvent -> runCommand(command, true));
        return button;
    }

    /**
     * Creates a link button.
     *
     * @param buttonText  Text to display on the button.
     * @param toolTipText Text to display when hovering over the button.
     */
    public static @NotNull JButton createLinkButton(String buttonText, String toolTipText, String command) {
        JButton button = new JButton(buttonText);
        button.setToolTipText(toolTipText);
        button.setBackground(new Color(200, 200, 200));
        button.addActionListener(actionEvent -> runCommand(command, true));
        return button;
    }

    /**
     * Creates a label.
     *
     * @param labelText Text to display on the label.
     * @param textColor Color of the text.
     * @param x         X position of the label.
     * @param y         Y position of the label.
     * @param width     Width of the label.
     * @param height    Height of the label.
     */
    public static @NotNull JLabel createLabel(String labelText, Color textColor,
                                              int x, int y, int width, int height) {
        JLabel label = new JLabel(labelText);
        label.setForeground(textColor);
        label.setBounds(x, y, width, height);
        return label;
    }

    /**
     * Launches an application without launch arguments.
     *
     * @param appResource    The name of the application's resource.
     * @param appExecutable  The name of the application's executable.
     * @param isZipped       Whether the application is zipped or not.
     * @param extractionPath The path to extract the application to.
     */
    public static void launchApplication(String appResource, String appExecutable,
                                         boolean isZipped, String extractionPath) {
        launchApplication(appResource, appExecutable, "", isZipped, extractionPath);
    }

    /**
     * Launches an application.
     *
     * @param appResource    The name of the application's resource.
     * @param appExecutable  The name of the application's executable.
     * @param isZipped       Whether the application is zipped or not.
     * @param extractionPath The path to extract the application to.
     * @param launchArgs     The arguments to launch the application with.
     */
    public static void launchApplication(String appResource, String appExecutable,
                                         String launchArgs, boolean isZipped, String extractionPath) {
        Path path = Paths.get(extractionPath, appExecutable);

        if (!Files.exists(path)) {
            try (InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/" + appResource)) {
                saveFile(Objects.requireNonNull(input), appResource, false);
            } catch (IOException ex) {
                MessageUtil.printException(ex);
            }

            if (isZipped) {
                unzipFile(tempDirectory + "\\" + appResource, extractionPath);
            }
        }

        runCommand(path + (launchArgs.isEmpty() ? "" : " " + launchArgs), true);
    }
}
