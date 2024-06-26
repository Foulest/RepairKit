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
import net.foulest.repairkit.panels.UsefulPrograms;
import net.foulest.repairkit.util.DebugUtil;
import net.foulest.repairkit.util.UpdateUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static net.foulest.repairkit.util.CommandUtil.getCommandOutput;
import static net.foulest.repairkit.util.CommandUtil.runCommand;
import static net.foulest.repairkit.util.ConstantUtil.*;
import static net.foulest.repairkit.util.DebugUtil.debug;
import static net.foulest.repairkit.util.FileUtil.getImageIcon;
import static net.foulest.repairkit.util.FileUtil.tempDirectory;
import static net.foulest.repairkit.util.ProcessUtil.isProcessRunning;
import static net.foulest.repairkit.util.SoundUtil.playSound;
import static net.foulest.repairkit.util.SwingUtil.*;
import static net.foulest.repairkit.util.UpdateUtil.getVersionFromProperties;

public class RepairKit extends JFrame {

    @Getter
    @Setter
    private static JPanel mainPanel;
    @Getter
    private static boolean safeMode = false;
    @Getter
    private static boolean outdatedOperatingSystem = false;
    @Getter
    private static boolean windowsUpdateInProgress = false;

    /**
     * The main method of the program.
     *
     * @param args The program's arguments.
     */
    public static void main(String[] args) {
        try {
            // Checks if RepairKit is running as administrator.
            debug("Checking if RepairKit is running as administrator...");
            if (getCommandOutput("net session", false, false).toString().contains("Access is denied.")) {
                playSound(ERROR_SOUND);
                JOptionPane.showMessageDialog(null,
                        "Please run RepairKit as an administrator.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                return;
            }

            // Creates the new log file.
            DebugUtil.createLogFile(args);

            // Checks if RepairKit is running in the temp directory.
            debug("Checking if RepairKit is running in the temp directory...");
            if (System.getProperty("user.dir").equalsIgnoreCase(tempDirectory.getPath())) {
                playSound(ERROR_SOUND);
                JOptionPane.showMessageDialog(null, BAD_FILE_LOCATION, "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                return;
            }

            // Checks for incompatibility issues.
            debug("Checking for incompatibility issues...");
            checkOperatingSystemCompatibility();

            // Checks for Windows Update and Medal.
            if (!safeMode) {
                debug("Checking for Windows Update and Medal...");
                checkForWindowsUpdate();
                checkForMedal();
            }

            // Deletes pre-existing RepairKit files.
            debug("Deleting pre-existing RepairKit files...");
            runCommand("rd /s /q \"" + tempDirectory.getPath() + "\"", false);

            // Deletes RepairKit files on shutdown.
            debug("Deleting RepairKit files on shutdown...");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                debug("Shutting down RepairKit...");
                runCommand("rd /s /q \"" + tempDirectory.getPath() + "\"", false);
            }));

            // Launches the program.
            debug("Launching the program...");

            SwingUtilities.invokeLater(() -> {
                try {
                    new RepairKit().setVisible(true);
                } catch (Exception ex) {
                    debug("Exception in SwingUtilities.invokeLater: " + ex.getMessage());
                    debug(Arrays.toString(ex.getStackTrace()));
                }
            });
        } catch (Exception ex) {
            debug("Exception in main: " + ex.getMessage());
            debug(Arrays.toString(ex.getStackTrace()));
        }
    }

    /**
     * Creates a new instance of the program.
     */
    public RepairKit() {
        try {
            // Sets the window properties.
            debug("Setting up the window properties...");
            setTitle("RepairKit");
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setPreferredSize(new Dimension(758, 550));
            setResizable(false);

            // Initialize the panels.
            debug("Initializing the Automatic Repairs panel...");
            AutomaticRepairs automaticRepairs = new AutomaticRepairs();
            debug("Initializing the Useful Programs panel...");
            UsefulPrograms usefulPrograms = new UsefulPrograms();
            debug("Initializing the System Shortcuts panel...");
            SystemShortcuts systemShortcuts = new SystemShortcuts();

            // Creates the main panel.
            debug("Creating the main panel...");
            setMainPanel(new JPanel(new CardLayout()));
            mainPanel.add(automaticRepairs, "Automatic Repairs");
            mainPanel.add(usefulPrograms, "Useful Programs");
            mainPanel.add(systemShortcuts, "System Shortcuts");

            // Creates the banner panel.
            debug("Creating the banner panel...");
            JPanel bannerPanel = createBannerPanel();

            // Adds the panels to the main panel.
            debug("Adding the panels to the main panel...");
            add(bannerPanel, BorderLayout.NORTH);
            add(mainPanel, BorderLayout.CENTER);

            // Checks for updates.
            debug("Checking for updates...");
            UpdateUtil.checkForUpdates();

            // Packs and centers the frame.
            debug("Packing and centering the frame...");
            pack();
            setLocationRelativeTo(null);
        } catch (Exception ex) {
            debug("Exception in RepairKit constructor: " + ex.getMessage());
            debug(Arrays.toString(ex.getStackTrace()));
        }
    }

    /**
     * Creates the banner panel.
     *
     * @return The banner panel.
     */
    @NotNull
    public JPanel createBannerPanel() {
        // Creates the banner panel.
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setLayout(null);
        bannerPanel.setBackground(new Color(0, 120, 215));
        bannerPanel.setPreferredSize(new Dimension(getWidth(), 60));

        // Creates the RepairKit icon image.
        debug("Creating the RepairKit icon image...");
        ImageIcon imageIcon = getImageIcon("icons/RepairKit.png");
        JLabel iconLabel = new JLabel(imageIcon);
        iconLabel.setBounds(10, 10, 40, 40);
        bannerPanel.add(iconLabel);
        iconLabel.repaint();

        // Creates the primary banner label.
        debug("Creating the primary banner label...");
        JLabel bannerLabelPrimary = createLabel("RepairKit",
                new Rectangle(60, 6, 200, 30),
                new Font(ARIAL, Font.BOLD, 22)
        );
        bannerLabelPrimary.setForeground(Color.WHITE);
        bannerPanel.add(bannerLabelPrimary);

        // Creates the secondary banner label.
        debug("Creating the secondary banner label...");
        JLabel bannerLabelSecondary = createLabel("by Foulest",
                new Rectangle(60, 31, 100, 20),
                new Font(ARIAL, Font.PLAIN, 14)
        );
        bannerLabelSecondary.setForeground(Color.WHITE);
        bannerLabelSecondary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bannerLabelSecondary.addMouseListener(createHyperlinkLabel(bannerLabelSecondary, "https://github.com/Foulest"));
        bannerPanel.add(bannerLabelSecondary);

        // Creates the version info label.
        debug("Creating the version info label...");
        JLabel versionInfo = createLabel("Version:",
                new Rectangle(675, 5, 60, 30),
                new Font(ARIAL, Font.BOLD, 14)
        );
        versionInfo.setForeground(Color.WHITE);
        bannerPanel.add(versionInfo);

        // Creates the version number label.
        debug("Creating the version number label...");
        JLabel versionNumber = createLabel(getVersionFromProperties(),
                new Rectangle(700, 25, 50, 30),
                new Font(ARIAL, Font.PLAIN, 14)
        );
        versionNumber.setForeground(Color.WHITE);
        versionNumber.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        versionNumber.addMouseListener(createHyperlinkLabel(versionNumber, "https://github.com/Foulest/RepairKit/releases"));
        bannerPanel.add(versionNumber);

        // Creates the Automatic Repairs button.
        debug("Creating the Automatic Repairs button...");
        JButton automaticRepairs = createPanelButton("Automatic Repairs", new Rectangle(175, 10, 150, 40));
        bannerPanel.add(automaticRepairs);

        // Creates the Useful Programs button.
        debug("Creating the Useful Programs button...");
        JButton usefulPrograms = createPanelButton("Useful Programs", new Rectangle(325, 10, 150, 40));
        bannerPanel.add(usefulPrograms);

        // Creates the System Shortcuts button.
        debug("Creating the System Shortcuts button...");
        JButton systemShortcuts = createPanelButton("System Shortcuts", new Rectangle(475, 10, 150, 40));
        bannerPanel.add(systemShortcuts);
        return bannerPanel;
    }

    /**
     * Checks if the user's operating system is supported.
     */
    private static void checkOperatingSystemCompatibility() {
        String osName = System.getProperty("os.name");

        // Checks if the operating system is 32-bit.
        debug("Checking if the operating system is 32-bit...");
        if (!System.getProperty("os.arch").contains("64")) {
            playSound(ERROR_SOUND);
            JOptionPane.showMessageDialog(null,
                    """
                            Your operating system is 32-bit.\
                            This program is designed for 64-bit operating systems.\
                            Please upgrade to a 64-bit operating system to use this program.""",
                    INCOMPATIBLE_OS_TITLE, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }

        // Checks if the operating system is outdated (older than Windows 10).
        debug("Checking if the operating system is outdated...");
        if (!osName.equalsIgnoreCase("Windows 10")
                && !osName.equalsIgnoreCase("Windows 11")) {
            if (osName.equalsIgnoreCase("Windows 8.1")
                    || osName.equalsIgnoreCase("Windows 8")
                    || osName.equalsIgnoreCase("Windows 7")
                    || osName.equalsIgnoreCase("Windows Vista")
                    || osName.equalsIgnoreCase("Windows XP")) {
                playSound(ERROR_SOUND);
                JOptionPane.showMessageDialog(null,
                        "Your operating system, " + osName + ", "
                                + "is outdated and no longer supported."
                                + "\nFeatures of this program may not work correctly or at all.",
                        OUTDATED_OS_TITLE, JOptionPane.ERROR_MESSAGE);
                outdatedOperatingSystem = true;
            } else {
                playSound(ERROR_SOUND);
                JOptionPane.showMessageDialog(null,
                        "Your operating system, " + osName + ", "
                                + "is outdated, unknown, or not Windows based."
                                + "\nThis program is designed for Windows 10 and 11."
                                + "\nPlease upgrade to a supported operating system to use this program.",
                        INCOMPATIBLE_OS_TITLE, JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                return;
            }
        }

        // Checks if the system is booting in Safe Mode.
        debug("Checking if the system is booting in Safe Mode...");
        if (getCommandOutput("wmic COMPUTERSYSTEM GET BootupState",
                false, false).toString().contains("safe")) {
            playSound(ERROR_SOUND);
            JOptionPane.showMessageDialog(null,
                    "Your system is booting in Safe Mode."
                            + "\nFeatures of this program may not work correctly or at all.",
                    SAFE_MODE_TITLE, JOptionPane.ERROR_MESSAGE);
            safeMode = true;
        }
    }

    /**
     * Checks if Windows Update is running.
     * Windows Update causes problems with DISM.
     */
    private static void checkForWindowsUpdate() {
        if (isProcessRunning("WmiPrvSE.exe")
                && isProcessRunning("TiWorker.exe")
                && isProcessRunning("TrustedInstaller.exe")
                && isProcessRunning("wuauclt.exe")) {
            windowsUpdateInProgress = true;
            playSound(WARNING_SOUND);
            JOptionPane.showMessageDialog(null, "Windows Update is running on your system."
                            + "\nCertain tweaks will not be applied until the Windows Update is finished."
                    , "Software Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Checks if Medal is installed.
     * Medal causes issues with Desktop Window Manager.
     */
    private static void checkForMedal() {
        if (isProcessRunning("medal.exe")) {
            playSound(WARNING_SOUND);
            JOptionPane.showMessageDialog(null,
                    """
                            Warning: Medal is installed and running on your system.\
                            Medal causes issues with Desktop Windows Manager, which affects system performance.\
                            Finding an alternative to Medal, such as ShadowPlay or AMD ReLive, is recommended.""",
                    "Software Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
