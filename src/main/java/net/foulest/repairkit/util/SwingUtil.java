package net.foulest.repairkit.util;

import lombok.NonNull;
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
     * Creates an action button without a tooltip.
     *
     * @param buttonText Text to display on the button.
     * @param action    Action to run when the button is clicked.
     */
    public static @NonNull JButton createActionButton(String buttonText, Runnable action) {
        JButton button = new JButton(buttonText);
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
                ex.printStackTrace();
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
    public static @NotNull JButton createAppButton(String buttonText, @NotNull String toolTipText,
                                                   String appResource, String appExecutable,
                                                   String launchArgs, boolean isZipped, String extractionPath) {
        JButton button = new JButton(buttonText);

        if (!toolTipText.isEmpty()) {
            button.setToolTipText(toolTipText);
        }

        button.setBackground(new Color(200, 200, 200));

        button.addActionListener(actionEvent -> {
            try {
                launchApplication(appResource, appExecutable, launchArgs, isZipped, extractionPath);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return button;
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

        synchronized (RepairKit.class) {
            if (!Files.exists(path)) {
                try (InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("bin/" + appResource)) {
                    saveFile(Objects.requireNonNull(input), appResource, false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if (isZipped) {
                    unzipFile(tempDirectory + "\\" + appResource, extractionPath);
                }
            }
        }

        runCommand(path + (launchArgs.isEmpty() ? "" : " " + launchArgs), true);
    }

    /**
     * Sets up an application icon.
     *
     * @param baseHeight  The base height of the panel.
     * @param baseWidth   The base width of the panel.
     * @param imageIcon   The icon to display.
     * @param panel       The panel to add the icon to.
     */
    public static void setupAppIcon(int baseHeight, int baseWidth,
                                    @NotNull ImageIcon imageIcon,
                                    @NotNull JPanel panel) {
        Image scaledImage = imageIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        JLabel iconLabel = new JLabel(imageIcon);
        iconLabel.setBounds(baseWidth, baseHeight + 7, 35, 35);
        panel.add(iconLabel);
        iconLabel.repaint();
    }

    public static void setLabelProperties(@NotNull JLabel label, String text, Color foreground, Font font,
                                          int x, int y, int width, int height) {
        label.setText(text);
        label.setForeground(foreground);
        label.setFont(font);
        label.setBounds(x, y, width, height);
    }
}
