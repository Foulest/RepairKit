/*
 * RepairKit - an all-in-one Java-based Windows repair and maintenance toolkit.
 * Copyright (C) 2024 Foulest (https://github.com/Foulest)
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
package net.foulest.repairkit;

import lombok.Getter;
import lombok.Setter;
import net.foulest.repairkit.panels.AutomaticRepairs;
import net.foulest.repairkit.panels.SystemShortcuts;
import net.foulest.repairkit.panels.UsefulPrograms1;
import net.foulest.repairkit.panels.UsefulPrograms2;
import net.foulest.repairkit.util.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Main class for the program.
 *
 * @author Foulest
 */
public final class RepairKit extends JFrame {

    @Getter
    @Setter
    private static JPanel mainPanel;

    @Getter
    private static boolean safeMode;

    @Getter
    private static boolean outdatedOperatingSystem;

    /**
     * The main method of the program.
     *
     * @param args The program's arguments.
     */
    public static void main(String[] args) {
        try {
            // Checks if RepairKit is running as administrator.
            DebugUtil.debug("Checking if RepairKit is running as administrator...");
            if (CommandUtil.getCommandOutput("net session", false, false).toString().contains("Access is denied.")) {
                SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                JOptionPane.showMessageDialog(null,
                        "Please run RepairKit as an administrator.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                return;
            }

            // Creates the new log file.
            DebugUtil.createLogFile(args);

            // Checks if RepairKit is running in the temp directory.
            DebugUtil.debug("Checking if RepairKit is running in the temp directory...");
            @NotNull String path = FileUtil.tempDirectory.getPath();

            if (System.getProperty("user.dir").equalsIgnoreCase(path)) {
                SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                JOptionPane.showMessageDialog(null, ConstantUtil.BAD_FILE_LOCATION, "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                return;
            }

            // Checks for incompatibility issues.
            DebugUtil.debug("Checking for incompatibility issues...");
            checkOperatingSystemCompatibility();

            // Checks for Medal.
            if (!safeMode) {
                DebugUtil.debug("Checking for Medal...");
                checkForMedal();
            }

            // Deletes pre-existing RepairKit files.
            DebugUtil.debug("Deleting pre-existing RepairKit files...");
            CommandUtil.runCommand("rd /s /q \"" + path + "\"", false);

            // Deletes RepairKit files on shutdown.
            DebugUtil.debug("Deleting RepairKit files on shutdown...");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                DebugUtil.debug("Shutting down RepairKit...");
                CommandUtil.runCommand("rd /s /q \"" + path + "\"", false);
            }));

            // Checks for updates.
            DebugUtil.debug("Checking for updates...");
            UpdateUtil.checkForUpdates();

            // Launches the program.
            DebugUtil.debug("Launching the program...");

            SwingUtilities.invokeLater(() -> {
                try {
                    new RepairKit().setVisible(true);
                } catch (RuntimeException ex) {
                    DebugUtil.warn("Failed to set the program visible", ex);
                    StackTraceElement[] stackTrace = ex.getStackTrace();
                    DebugUtil.debug(Arrays.toString(stackTrace));
                }
            });
        } catch (HeadlessException ex) {
            DebugUtil.warn("Failed to launch the program", ex);
            StackTraceElement[] stackTrace = ex.getStackTrace();
            DebugUtil.debug(Arrays.toString(stackTrace));
        }
    }

    /**
     * Creates a new instance of the program.
     */
    private RepairKit() {
        try {
            // Sets the window properties.
            DebugUtil.debug("Setting up the window properties...");
            setTitle("RepairKit");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setPreferredSize(new Dimension(758, 550));
            setResizable(false);

            // Initialize the panels.
            DebugUtil.debug("Initializing the Automatic Repairs panel...");
            @NotNull AutomaticRepairs automaticRepairs = new AutomaticRepairs();
            DebugUtil.debug("Initializing the Useful Programs (Page 1) panel...");
            @NotNull UsefulPrograms1 usefulPrograms1 = new UsefulPrograms1();
            DebugUtil.debug("Initializing the Useful Programs (Page 2) panel...");
            @NotNull UsefulPrograms2 usefulPrograms2 = new UsefulPrograms2();
            DebugUtil.debug("Initializing the System Shortcuts panel...");
            @NotNull SystemShortcuts systemShortcuts = new SystemShortcuts();

            // Creates the main panel.
            DebugUtil.debug("Creating the main panel...");
            setMainPanel(new JPanel(new CardLayout()));
            mainPanel.add(automaticRepairs, "Automatic Repairs");
            mainPanel.add(usefulPrograms1, "Useful Programs (Page 1)");
            mainPanel.add(usefulPrograms2, "Useful Programs (Page 2)");
            mainPanel.add(systemShortcuts, "System Shortcuts");

            // Creates the banner panel.
            DebugUtil.debug("Creating the banner panel...");
            @NotNull JPanel bannerPanel = createBannerPanel();

            // Adds the panels to the main panel.
            DebugUtil.debug("Adding the panels to the main panel...");
            add(bannerPanel, BorderLayout.PAGE_START);
            add(mainPanel, BorderLayout.CENTER);

            // Packs and centers the frame.
            DebugUtil.debug("Packing and centering the frame...");
            pack();
            setLocationRelativeTo(null);
        } catch (RuntimeException ex) {
            DebugUtil.warn("Failed to create a new instance of the program", ex);
            StackTraceElement[] stackTrace = ex.getStackTrace();
            DebugUtil.debug(Arrays.toString(stackTrace));
        }
    }

    /**
     * Creates the banner panel.
     *
     * @return The banner panel.
     */
    @NotNull
    private JPanel createBannerPanel() {
        // Creates the banner panel.
        @NotNull JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setLayout(null);
        bannerPanel.setBackground(new Color(0, 120, 215));
        int width = getWidth();
        bannerPanel.setPreferredSize(new Dimension(width, 60));

        // Creates the RepairKit icon image.
        DebugUtil.debug("Creating the RepairKit icon image...");
        @NotNull ImageIcon imageIcon = FileUtil.getImageIcon("icons/RepairKit.png");
        @NotNull JLabel iconLabel = new JLabel(imageIcon);
        iconLabel.setBounds(10, 10, 40, 40);
        bannerPanel.add(iconLabel);
        iconLabel.repaint();

        // Creates the primary banner label.
        DebugUtil.debug("Creating the primary banner label...");
        @NotNull JLabel bannerLabelPrimary = SwingUtil.createLabel("RepairKit",
                new Rectangle(60, 6, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 22)
        );
        bannerLabelPrimary.setForeground(Color.WHITE);
        bannerPanel.add(bannerLabelPrimary);

        // Creates the secondary banner label.
        DebugUtil.debug("Creating the secondary banner label...");
        @NotNull JLabel bannerLabelSecondary = SwingUtil.createLabel("by Foulest",
                new Rectangle(60, 31, 70, 20),
                new Font(ConstantUtil.ARIAL, Font.PLAIN, 14)
        );
        bannerLabelSecondary.setForeground(Color.WHITE);
        bannerLabelSecondary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bannerLabelSecondary.addMouseListener(SwingUtil.createHyperlinkLabel(bannerLabelSecondary, "https://github.com/Foulest"));
        bannerPanel.add(bannerLabelSecondary);

        // Creates the version info label.
        DebugUtil.debug("Creating the version info label...");
        @NotNull JLabel versionInfo = SwingUtil.createLabel("Version:",
                new Rectangle(675, 5, 60, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 14)
        );
        versionInfo.setForeground(Color.WHITE);
        bannerPanel.add(versionInfo);

        // Creates the version number label.
        DebugUtil.debug("Creating the version number label...");
        @NotNull JLabel versionNumber = SwingUtil.createLabel(UpdateUtil.getVersionFromProperties(),
                new Rectangle(700, 25, 35, 30),
                new Font(ConstantUtil.ARIAL, Font.PLAIN, 14)
        );
        versionNumber.setForeground(Color.WHITE);
        versionNumber.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        versionNumber.addMouseListener(SwingUtil.createHyperlinkLabel(versionNumber, "https://github.com/Foulest/RepairKit/releases"));
        bannerPanel.add(versionNumber);

        // Creates the Automatic Repairs button.
        DebugUtil.debug("Creating the Automatic Repairs button...");
        @NotNull JButton automaticRepairs = SwingUtil.createPanelButton("Automatic Repairs", "Automatic Repairs", new Rectangle(175, 10, 150, 40));
        bannerPanel.add(automaticRepairs);

        // Creates the Useful Programs button.
        DebugUtil.debug("Creating the Useful Programs button...");
        @NotNull JButton usefulPrograms = SwingUtil.createPanelButton("Useful Programs", "Useful Programs (Page 1)", new Rectangle(325, 10, 150, 40));
        bannerPanel.add(usefulPrograms);

        // Creates the System Shortcuts button.
        DebugUtil.debug("Creating the System Shortcuts button...");
        @NotNull JButton systemShortcuts = SwingUtil.createPanelButton("System Shortcuts", "System Shortcuts", new Rectangle(475, 10, 150, 40));
        bannerPanel.add(systemShortcuts);
        return bannerPanel;
    }

    /**
     * Checks if the user's operating system is supported.
     */
    private static void checkOperatingSystemCompatibility() {
        String osName = System.getProperty("os.name");

        // Checks if the operating system is 32-bit.
        DebugUtil.debug("Checking if the operating system is 32-bit...");
        if (!System.getProperty("os.arch").contains("64")) {
            SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
            JOptionPane.showMessageDialog(null,
                    """
                            Your operating system is 32-bit.\
                            This program is designed for 64-bit operating systems.\
                            Please upgrade to a 64-bit operating system to use this program.""",
                    ConstantUtil.INCOMPATIBLE_OS_TITLE, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }

        // Checks if the operating system is outdated (older than Windows 10).
        DebugUtil.debug("Checking if the operating system is outdated...");
        if (!osName.equalsIgnoreCase("Windows 10")
                && !osName.equalsIgnoreCase("Windows 11")
                && !osName.equalsIgnoreCase("Windows Server 2025")
                && !osName.equalsIgnoreCase("Windows Server 2022")
                && !osName.equalsIgnoreCase("Windows Server 2019")
                && !osName.equalsIgnoreCase("Windows Server 2016")) {
            if (osName.contains("Windows")) {
                SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                JOptionPane.showMessageDialog(null,
                        "Your operating system, " + osName + ", "
                                + "is outdated and no longer supported."
                                + "\nFeatures of this program may not work correctly or at all.",
                        ConstantUtil.OUTDATED_OS_TITLE, JOptionPane.ERROR_MESSAGE);
                outdatedOperatingSystem = true;
            } else {
                SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                JOptionPane.showMessageDialog(null,
                        "Your operating system, " + osName + ", "
                                + "is outdated, unknown, or not Windows based."
                                + "\nThis program is designed for Windows 10 and 11."
                                + "\nPlease upgrade to a supported operating system to use this program.",
                        ConstantUtil.INCOMPATIBLE_OS_TITLE, JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                return;
            }
        }

        // Checks if the system is booting in Safe Mode.
        DebugUtil.debug("Checking if the system is booting in Safe Mode...");
        if (!CommandUtil.getPowerShellCommandOutput("Get-CimInstance -ClassName Win32_ComputerSystem"
                        + " | Select-Object -ExpandProperty BootupState",
                false, false).toString().contains("Normal")) {
            SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
            JOptionPane.showMessageDialog(null,
                    "Your system is booting in Safe Mode."
                            + "\nFeatures of this program may not work correctly or at all.",
                    ConstantUtil.SAFE_MODE_TITLE, JOptionPane.ERROR_MESSAGE);
            safeMode = true;
        }
    }

    /**
     * Checks if Medal is installed.
     * Medal causes issues with Desktop Window Manager.
     */
    private static void checkForMedal() {
        if (ProcessUtil.isProcessRunning("medal.exe")) {
            SoundUtil.playSound(ConstantUtil.WARNING_SOUND);
            JOptionPane.showMessageDialog(null,
                    """
                            Warning: Medal is installed and running on your system.\
                            Medal causes issues with Desktop Windows Manager, which affects system performance.\
                            Finding an alternative to Medal, such as ShadowPlay or AMD ReLive, is recommended.""",
                    "Software Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
