package net.foulest.repairkit.util;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

import static net.foulest.repairkit.RepairKit.*;
import static net.foulest.repairkit.util.CommandUtil.runCommand;

public class SwingUtil {

    public static final String htmlFormat = "<html><body><p style='width: %dpx'>Progress: %s</p></body></html>";

    /**
     * Updates the progress label.
     *
     * @param text Text to display.
     */
    public static void updateProgressLabel(String text) {
        SwingUtilities.invokeLater(() -> {
            labelProgress.setText(String.format(htmlFormat, 250, text));
            frame.update(frame.getGraphics());
        });
    }

    /**
     * Updates the progress label with a delay to clear it.
     *
     * @param text Text to display.
     * @param clearDelay Delay in milliseconds to clear the label.
     */
    public static void updateProgressLabel(String text, long clearDelay) {
        updateProgressLabel(text);

        if (clearDelay > 0) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    updateProgressLabel("");
                }
            }, clearDelay);
        }
    }

    /**
     * Adds components to a panel.
     *
     * @param panel Panel to add components to.
     * @param components Components to add.
     */
    public static void addComponents(JPanel panel, Component... components) {
        for (Component component : components) {
            panel.add(component);
        }
    }

    /**
     * Shows an error dialog.
     *
     * @param ex Exception to show.
     * @param identifier Identifier for the error.
     */
    public static void showErrorDialog(Exception ex, String identifier) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error: " + identifier, JOptionPane.ERROR_MESSAGE);
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
                showErrorDialog(ex, "Action Button");
            }
        });

        return button;
    }

    /**
     * Creates an application button.
     */
    public static JButton createAppButton(String buttonText, String toolTipText, String appName, String appResource,
                                           String appExecutable, boolean isZipped, String extractionPath) {
        JButton button = new JButton(buttonText);
        button.setToolTipText(toolTipText);
        button.setBackground(new Color(200, 200, 200));

        button.addActionListener(actionEvent -> {
            try {
                launchApplication(appName, appResource, appExecutable, isZipped, extractionPath);
            } catch (Exception ex) {
                showErrorDialog(ex, "App Button");
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

        button.addActionListener(actionEvent -> {
            updateProgressLabel(progressText, 3000);
            runCommand(command, true);
        });

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
