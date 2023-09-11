package net.foulest.repairkit.util;

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
    public static void addComponents(JPanel panel, Component... components) {
        for (Component component : components) {
            panel.add(component);
        }
    }

    /**
     * Creates an action button.
     */
    public static JButton createActionButton(String buttonText, String toolTipText, Runnable action) {
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
    public static JButton createAppButton(String buttonText, String toolTipText, String appResource,
                                          String appExecutable, boolean isZipped, String extractionPath) {
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
     * Creates a link button.
     */
    public static JButton createLinkButton(String buttonText, String toolTipText, String command, String progressText) {
        JButton button = new JButton(buttonText);
        button.setToolTipText(toolTipText);
        button.setBackground(new Color(200, 200, 200));

        button.addActionListener(actionEvent -> runCommand(command, true));
        return button;
    }

    /**
     * Creates a label.
     */
    public static JLabel createLabel(String labelText, Color textColor, int x, int y, int width, int height) {
        JLabel label = new JLabel(labelText);
        label.setForeground(textColor);
        label.setBounds(x, y, width, height);
        return label;
    }

    /**
     * Sleeps the current thread for the specified amount of time.
     *
     * @param millis Time to sleep in milliseconds.
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }
}
