/*
 * RepairKit - an all-in-one Java-based Windows repair and maintenance toolkit.
 * Copyright (C) 2026 Foulest (https://github.com/Foulest)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package net.foulest.repairkit.util;

import lombok.Data;
import net.foulest.repairkit.RepairKit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

/**
 * Utility class for Swing operations.
 *
 * @author Foulest
 */
@Data
public class SwingUtil {

    /**
     * Creates an action button without a tooltip.
     *
     * @param buttonText      Text to display on the button.
     * @param bounds          Bounds of the button.
     * @param backgroundColor Background color of the button.
     * @param action          Action to run when the button is clicked.
     * @return The created button.
     * @see #createActionButton(String, String, Rectangle, Color, Runnable)
     */
    public static @NotNull JButton createActionButton(String buttonText, @NotNull Rectangle bounds,
                                                      Color backgroundColor, @NotNull Runnable action) {
        return createActionButton(buttonText, "", bounds, backgroundColor, action);
    }

    /**
     * Creates an action button.
     *
     * @param buttonText      Text to display on the button.
     * @param toolTipText     Text to display when hovering over the button.
     * @param bounds          Bounds of the button.
     * @param backgroundColor Background color of the button.
     * @param action          Action to run when the button is clicked.
     * @return The created button.
     */
    @SuppressWarnings("NestedMethodCall")
    public static @NotNull JButton createActionButton(String buttonText, @NotNull String toolTipText, @NotNull Rectangle bounds,
                                                      Color backgroundColor, @NotNull Runnable action) {
        @NotNull JButton button = new JButton(buttonText);

        if (!toolTipText.isEmpty()) {
            button.setToolTipText(toolTipText);
        }

        button.setBounds(bounds);
        button.setBackground(backgroundColor);

        button.addActionListener(actionEvent -> new Thread(() -> {
            try {
                action.run();
            } catch (RuntimeException ex) {
                DebugUtil.warn("Failed to run action", ex);
            }
        }).start());
        return button;
    }

    /**
     * Launches an application without launch arguments.
     *
     * @param appResource    The name of the application's resource.
     * @param appExecutable  The name of the application's executable.
     * @param isZipped       Whether the application is zipped or not.
     * @param extractionPath The path to extract the application to.
     * @see #launchApplication(String, String, CharSequence, boolean, String)
     */
    public static void launchApplication(String appResource, String appExecutable,
                                         boolean isZipped, @NotNull String extractionPath) {
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
    @SuppressWarnings("SameParameterValue")
    private static void launchApplication(String appResource, String appExecutable,
                                          @NotNull CharSequence launchArgs, boolean isZipped, @NotNull String extractionPath) {
        @NotNull Path path = Paths.get(extractionPath, appExecutable);

        synchronized (RepairKit.class) {
            if (!Files.exists(path)) {
                try (@Nullable InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("bin/" + appResource)) {
                    if (input == null) {
                        JOptionPane.showMessageDialog(null,
                                "Failed to load application: " + appResource,
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    FileUtil.saveFile(input, FileUtil.tempDirectory + "\\" + appResource, false);
                } catch (IOException ex) {
                    DebugUtil.warn("Failed to save application: " + appResource, ex);
                }

                if (isZipped) {
                    FileUtil.unzipFile(FileUtil.tempDirectory + "\\" + appResource, extractionPath);
                }
            }
        }

        boolean launchArgsEmpty = launchArgs.isEmpty();
        CommandUtil.runCommand(path + (launchArgsEmpty ? "" : " " + launchArgs), true);
    }

    /**
     * Sets up an application icon.
     *
     * @param baseHeight The base height of the panel.
     * @param baseWidth  The base width of the panel.
     * @param imageIcon  The icon to display.
     * @param panel      The panel to add the icon to.
     */
    public static void setupAppIcon(int baseHeight, int baseWidth,
                                    @NotNull ImageIcon imageIcon,
                                    @NotNull JPanel panel) {
        Image scaledImage = imageIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);

        @NotNull JLabel iconLabel = new JLabel(imageIcon);
        iconLabel.setBounds(baseWidth, baseHeight + 7, 35, 35);
        panel.add(iconLabel);
        iconLabel.repaint();
    }

    /**
     * Creates a hyperlink label that opens a URL when clicked.
     *
     * @param label The label to make a hyperlink.
     * @param url   The URL to open when the label is clicked.
     * @return The created mouse adapter.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull MouseAdapter createHyperlinkLabel(@NotNull JLabel label, @NotNull String url) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException | URISyntaxException ex) {
                    DebugUtil.warn("Failed to open URL: " + url, ex);
                }
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                // Underlines the label text when the mouse enters.
                Font font = label.getFont();
                Map<TextAttribute, ?> attributes = font.getAttributes();
                @NotNull Map<TextAttribute, Object> attributeMap = new HashMap<>(attributes);
                attributeMap.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                Font derivedFont = font.deriveFont(attributeMap);
                label.setFont(derivedFont);
            }

            @Override
            public void mouseExited(MouseEvent event) {
                // Removes the underline when the mouse exits.
                Font font = label.getFont();
                Map<TextAttribute, ?> attributes = font.getAttributes();
                @NotNull Map<TextAttribute, Object> attributeMap = new HashMap<>(attributes);
                attributeMap.put(TextAttribute.UNDERLINE, -1);
                Font derivedFont = font.deriveFont(attributeMap);
                label.setFont(derivedFont);
            }
        };
    }

    /**
     * Creates a panel button (located at the top of the panel).
     *
     * @param buttonName The name of the button.
     * @param panelName  The name of the panel to switch to.
     * @param bounds     The bounds of the button.
     * @return The created button.
     */
    @SuppressWarnings("NestedMethodCall")
    public static @NotNull JButton createPanelButton(String buttonName, String panelName, @NotNull Rectangle bounds) {
        @NotNull JButton button = new JButton(buttonName);
        button.setBounds(bounds);
        button.setBackground(new Color(0, 120, 215));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        button.setFont(new Font(ConstantUtil.ARIAL, Font.BOLD, 14));

        button.addActionListener(actionEvent -> new Thread(() -> {
            try {
                performPanelButtonAction(panelName);
            } catch (RuntimeException ex) {
                DebugUtil.warn("Failed to switch panel: " + panelName, ex);
            }
        }).start());
        return button;
    }

    /**
     * Performs an action when a panel button is clicked.
     *
     * @param name The name of the panel to switch to.
     */
    @SuppressWarnings("NestedMethodCall")
    private static void performPanelButtonAction(String name) {
        CardLayout cardLayout = (CardLayout) RepairKit.getMainPanel().getLayout();
        SwingUtilities.invokeLater(() -> cardLayout.show(RepairKit.getMainPanel(), name));
    }

    /**
     * Creates a label.
     *
     * @param text   The text to display on the label.
     * @param bounds The bounds of the label.
     * @param font   The font of the label.
     * @return The created label.
     */
    public static @NotNull JLabel createLabel(String text, @NotNull Rectangle bounds, Font font) {
        @NotNull JLabel label = new JLabel(text);
        label.setBounds(bounds);
        label.setFont(font);
        return label;
    }
}
