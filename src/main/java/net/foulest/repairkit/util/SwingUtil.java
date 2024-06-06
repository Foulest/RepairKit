/*
  RepairKit - an all-in-one Java-based Windows repair and maintenance toolkit.
  Copyright (C) 2024 Foulest (https://github.com/Foulest)

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package net.foulest.repairkit.util;

import lombok.NonNull;
import net.foulest.repairkit.RepairKit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.foulest.repairkit.RepairKit.mainPanel;
import static net.foulest.repairkit.util.CommandUtil.runCommand;
import static net.foulest.repairkit.util.FileUtil.*;

public class SwingUtil {

    /**
     * Creates an action button without a tooltip.
     *
     * @param buttonText Text to display on the button.
     * @param action    Action to run when the button is clicked.
     */
    public static @NonNull JButton createActionButton(String buttonText, Rectangle bounds,
                                                      Color backgroundColor, Runnable action) {
        JButton button = new JButton(buttonText);
        button.setBounds(bounds);
        button.setBackground(backgroundColor);

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
    public static @NotNull JButton createActionButton(String buttonText, String toolTipText, Rectangle bounds,
                                                      Color backgroundColor, Runnable action) {
        JButton button = new JButton(buttonText);
        button.setToolTipText(toolTipText);
        button.setBounds(bounds);
        button.setBackground(backgroundColor);

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
                                                   Rectangle bounds, Color color,
                                                   String appResource, String appExecutable,
                                                   boolean isZipped, String extractionPath) {
        return createAppButton(buttonText, toolTipText, bounds, color, appResource, appExecutable, "", isZipped, extractionPath);
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
                                                   Rectangle bounds, Color color,
                                                   String appResource, String appExecutable,
                                                   String launchArgs, boolean isZipped, String extractionPath) {
        JButton button = new JButton(buttonText);

        if (!toolTipText.isEmpty()) {
            button.setToolTipText(toolTipText);
        }

        button.setBounds(bounds);
        button.setBackground(color);

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

    /**
     * Creates a hyperlink label that opens a URL when clicked.
     *
     * @param label The label to make a hyperlink.
     * @param URL  The URL to open when the label is clicked.
     * @return The created mouse adapter.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull MouseAdapter createHyperlinkLabel(JLabel label, String URL) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                try {
                    Desktop.getDesktop().browse(new URI(URL));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                // Underlines the label text when the mouse enters.
                Font font = label.getFont();
                Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                label.setFont(font.deriveFont(attributes));
            }

            @Override
            public void mouseExited(MouseEvent event) {
                // Removes the underline when the mouse exits.
                Font font = label.getFont();
                Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
                attributes.put(TextAttribute.UNDERLINE, -1);
                label.setFont(font.deriveFont(attributes));
            }
        };
    }

    /**
     * Creates a panel button (located at the top of the panel).
     *
     * @param name   The name of the button.
     * @param bounds The bounds of the button.
     * @return The created button.
     */
    public static @NotNull JButton createPanelButton(String name, Rectangle bounds) {
        JButton button = new JButton(name);
        button.setBounds(bounds);
        button.setBackground(new Color(0, 120, 215));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        button.setFont(new Font("Arial", Font.BOLD, 14));

        button.addActionListener(actionEvent -> {
            CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
            cardLayout.show(mainPanel, name);
        });
        return button;
    }

    /**
     * Creates a label.
     *
     * @param text The text to display on the label.
     * @param bounds The bounds of the label.
     * @param font The font of the label.
     * @return The created label.
     */
    public static @NotNull JLabel createLabel(String text, Rectangle bounds, Font font) {
        JLabel label = new JLabel(text);
        label.setBounds(bounds);
        label.setFont(font);
        return label;
    }
}
