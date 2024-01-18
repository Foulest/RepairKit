package net.foulest.repairkit.util;

import lombok.NonNull;
import net.foulest.repairkit.RepairKit;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;

import static net.foulest.repairkit.util.CommandUtil.runCommand;
import static net.foulest.repairkit.util.FileUtil.*;

public class SwingUtil {

    /**
     * Adds components to a panel.
     *
     * @param panel      Panel to add components to.
     * @param components Components to add.
     */
    public static void addComponents(@NonNull JPanel panel, @NonNull Component... components) {
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
    public static JButton createActionButton(@NonNull String buttonText, @NonNull String toolTipText,
                                             @NonNull Runnable action) {
        JButton button = new JButton(buttonText);
        button.setToolTipText(toolTipText);
        button.setBackground(new Color(200, 200, 200));

        button.addActionListener(actionEvent -> {
            try {
                action.run();
            } catch (Exception ex) {
                MessageUtil.log(Level.WARNING, "Failed to run action: " + action);
                ex.printStackTrace();
            }
        });
        return button;
    }

    /**
     * Creates an application button.
     *
     * @param buttonText    Text to display on the button.
     * @param toolTipText   Text to display when hovering over the button.
     * @param appResource   Resource to extract.
     * @param appExecutable Executable to run.
     * @param isZipped      Whether the resource is zipped.
     */
    public static JButton createAppButton(@NonNull String buttonText, @NonNull String toolTipText,
                                          @NonNull String appResource, @NonNull String appExecutable,
                                          boolean isZipped, @NonNull String extractionPath) {
        JButton button = new JButton(buttonText);
        button.setToolTipText(toolTipText);
        button.setBackground(new Color(200, 200, 200));

        button.addActionListener(actionEvent -> {
            try {
                launchApplication(appResource, appExecutable, isZipped, extractionPath);
            } catch (Exception ex) {
                MessageUtil.log(Level.WARNING, "Failed to launch application: " + appExecutable);
                ex.printStackTrace();
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
    public static JButton createLinkButton(@NonNull String buttonText,
                                           @NonNull String command) {
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
    public static JButton createLinkButton(@NonNull String buttonText,
                                           @NonNull String toolTipText,
                                           @NonNull String command) {
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
    public static JLabel createLabel(@NonNull String labelText, @NonNull Color textColor,
                                     int x, int y, int width, int height) {
        JLabel label = new JLabel(labelText);
        label.setForeground(textColor);
        label.setBounds(x, y, width, height);
        return label;
    }

    /**
     * Launches an application.
     *
     * @param appResource    The name of the application's resource.
     * @param appExecutable  The name of the application's executable.
     * @param isZipped       Whether the application is zipped or not.
     * @param extractionPath The path to extract the application to.
     */
    public static void launchApplication(@NonNull String appResource, @NonNull String appExecutable,
                                         boolean isZipped, @NonNull String extractionPath) {
        Path path = Paths.get(extractionPath, appExecutable);

        if (!Files.exists(path)) {
            InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/" + appResource);
            saveFile(Objects.requireNonNull(input), appResource, false);

            if (isZipped) {
                unzipFile(tempDirectory + "\\" + appResource, extractionPath);
            }
        }

        runCommand(path.toString(), true);
    }
}
