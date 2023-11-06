package net.foulest.repairkit.util;

import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

import static net.foulest.repairkit.RepairKit.launchApplication;
import static net.foulest.repairkit.util.CommandUtil.runCommand;

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
                ex.printStackTrace();
            }
        });
        return button;
    }

    /**
     * Creates an application button.
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
                ex.printStackTrace();
            }
        });
        return button;
    }

    /**
     * Creates a link button without tooltip text.
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
     */
    public static JLabel createLabel(@NonNull String labelText, @NonNull Color textColor,
                                     int x, int y, int width, int height) {
        JLabel label = new JLabel(labelText);
        label.setForeground(textColor);
        label.setBounds(x, y, width, height);
        return label;
    }
}
