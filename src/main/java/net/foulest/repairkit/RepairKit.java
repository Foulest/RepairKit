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
package net.foulest.repairkit;

import com.sun.jna.platform.win32.WinReg;
import lombok.Getter;
import net.foulest.repairkit.panels.AutomaticRepairs;
import net.foulest.repairkit.panels.SystemShortcuts;
import net.foulest.repairkit.panels.UsefulPrograms;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static net.foulest.repairkit.util.CommandUtil.getCommandOutput;
import static net.foulest.repairkit.util.CommandUtil.runCommand;
import static net.foulest.repairkit.util.FileUtil.*;
import static net.foulest.repairkit.util.ProcessUtil.isProcessRunning;
import static net.foulest.repairkit.util.RegistryUtil.setRegistryIntValue;
import static net.foulest.repairkit.util.SoundUtil.playSound;
import static net.foulest.repairkit.util.SwingUtil.*;

public class RepairKit extends JFrame {

    // TODO: Adjust RepairKit's code to scale with high DPI displays.

    public static JPanel mainPanel;
    @Getter
    private static boolean safeMode = false;
    @Getter
    private static boolean outdatedOperatingSystem = false;
    @Getter
    private static boolean windowsUpdateInProgress = false;

    /**
     * Creates a new instance of the program.
     */
    public RepairKit() {
        // Sets the window properties.
        setTitle("RepairKit");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(758, 550));
        setResizable(false);

        // Initialize the panels.
        AutomaticRepairs automaticRepairs = new AutomaticRepairs();
        UsefulPrograms usefulPrograms = new UsefulPrograms();
        SystemShortcuts systemShortcuts = new SystemShortcuts();

        // Creates the main panel.
        mainPanel = new JPanel(new CardLayout());
        mainPanel.add(automaticRepairs, "Automatic Repairs");
        mainPanel.add(usefulPrograms, "Useful Programs");
        mainPanel.add(systemShortcuts, "System Shortcuts");

        // Creates the banner panel.
        JPanel bannerPanel = createBannerPanel();

        // Adds the panels to the main panel.
        add(bannerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Packs and centers the frame.
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Creates the banner panel.
     *
     * @return The banner panel.
     */
    private @NotNull JPanel createBannerPanel() {
        // Creates the banner panel.
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setLayout(null);
        bannerPanel.setBackground(new Color(0, 120, 215));
        bannerPanel.setPreferredSize(new Dimension(getWidth(), 60));

        // Creates the RepairKit icon image.
        ImageIcon imageIcon = getImageIcon("icons/RepairKit.png");
        Image scaledImage = imageIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        JLabel iconLabel = new JLabel(imageIcon);
        iconLabel.setBounds(10, 10, 40, 40);
        bannerPanel.add(iconLabel);
        iconLabel.repaint();

        // Creates the primary banner label.
        JLabel bannerLabelPrimary = createLabel("RepairKit",
                new Rectangle(60, 6, 200, 30),
                new Font("Arial", Font.BOLD, 22)
        );
        bannerLabelPrimary.setForeground(Color.WHITE);
        bannerPanel.add(bannerLabelPrimary);

        // Creates the secondary banner label.
        JLabel bannerLabelSecondary = createLabel("by Foulest",
                new Rectangle(60, 31, 100, 20),
                new Font("Arial", Font.PLAIN, 14)
        );
        bannerLabelSecondary.setForeground(Color.WHITE);
        bannerLabelSecondary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bannerLabelSecondary.addMouseListener(createHyperlinkLabel(bannerLabelSecondary, "https://github.com/Foulest"));
        bannerPanel.add(bannerLabelSecondary);

        // Creates the version info label.
        JLabel versionInfo = createLabel("Version:",
                new Rectangle(675, 5, 60, 30),
                new Font("Arial", Font.BOLD, 14)
        );
        versionInfo.setForeground(Color.WHITE);
        bannerPanel.add(versionInfo);

        // Creates the version number label.
        JLabel versionNumber = createLabel(getVersionFromProperties(),
                new Rectangle(700, 25, 50, 30),
                new Font("Arial", Font.PLAIN, 14)
        );
        versionNumber.setForeground(Color.WHITE);
        versionNumber.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        versionNumber.addMouseListener(createHyperlinkLabel(versionNumber, "https://github.com/Foulest/RepairKit"));
        bannerPanel.add(versionNumber);

        // Creates the Automatic Repairs button.
        JButton automaticRepairs = createPanelButton("Automatic Repairs", new Rectangle(175, 10, 150, 40));
        bannerPanel.add(automaticRepairs);

        // Creates the Useful Programs button.
        JButton usefulPrograms = createPanelButton("Useful Programs", new Rectangle(325, 10, 150, 40));
        bannerPanel.add(usefulPrograms);

        // Creates the System Shortcuts button.
        JButton systemShortcuts = createPanelButton("System Shortcuts", new Rectangle(475, 10, 150, 40));
        bannerPanel.add(systemShortcuts);
        return bannerPanel;
    }

    /**
     * The main method of the program.
     *
     * @param args The program's arguments.
     */
    public static void main(String[] args) {
        // Checks for incompatibility issues.
        checkOperatingSystemCompatibility();

        // Checks for Windows Update and Medal.
        if (!safeMode) {
            checkForWindowsUpdate();
            checkForMedal();
        }

        // Deletes pre-existing RepairKit files.
        runCommand("rd /s /q \"" + tempDirectory.getPath() + "\"", false);

        // Deletes RepairKit files on shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                runCommand("rd /s /q \"" + tempDirectory.getPath() + "\"", false))
        );

        // Sets up necessary app registry keys.
        setAppRegistryKeys();

        // Launches the program.
        SwingUtilities.invokeLater(() -> new RepairKit().setVisible(true));
    }

    /**
     * Checks if the user's operating system is supported.
     */
    private static void checkOperatingSystemCompatibility() {
        String osName = System.getProperty("os.name");

        // Checks if the operating system is 32-bit.
        if (!System.getProperty("os.arch").contains("64")) {
            playSound("win.sound.hand");
            JOptionPane.showMessageDialog(null,
                    "Your operating system is 32-bit."
                            + "\nThis program is designed for 64-bit operating systems."
                            + "\nPlease upgrade to a 64-bit operating system to use this program."
                    , "Incompatible Operating System", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }

        // Checks if the operating system is outdated (older than Windows 10).
        if (!osName.equalsIgnoreCase("Windows 10")
                && !osName.equalsIgnoreCase("Windows 11")) {
            if (osName.equalsIgnoreCase("Windows 8.1")
                    || osName.equalsIgnoreCase("Windows 8")
                    || osName.equalsIgnoreCase("Windows 7")
                    || osName.equalsIgnoreCase("Windows Vista")
                    || osName.equalsIgnoreCase("Windows XP")) {
                playSound("win.sound.hand");
                JOptionPane.showMessageDialog(null,
                        "Your operating system, " + osName + ", "
                                + "is outdated and no longer supported."
                                + "\nFeatures of this program may not work correctly or at all."
                        , "Outdated Operating System", JOptionPane.ERROR_MESSAGE);
                outdatedOperatingSystem = true;
            } else {
                playSound("win.sound.hand");
                JOptionPane.showMessageDialog(null,
                        "Your operating system, " + osName + ", "
                                + "is outdated, unknown, or not Windows based."
                                + "\n"
                        , "Incompatible Operating System", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                return;
            }
        }

        // Checks if the system is booting in Safe Mode.
        if (getCommandOutput("wmic COMPUTERSYSTEM GET BootupState",
                false, false).toString().contains("safe")) {
            playSound("win.sound.hand");
            JOptionPane.showMessageDialog(null,
                    "Your system is booting in Safe Mode."
                            + "\nFeatures of this program may not work correctly or at all."
                    , "Safe Mode Detected", JOptionPane.ERROR_MESSAGE);
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
            playSound("win.sound.asterisk");
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
            playSound("win.sound.asterisk");
            JOptionPane.showMessageDialog(null,
                    "Warning: Medal is installed and running on your system."
                            + "\nMedal causes issues with Desktop Windows Manager, which affects system performance."
                            + "\nFinding an alternative to Medal, such as ShadowPlay or AMD ReLive, is recommended.",
                    "Software Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Sets necessary app registry keys.
     */
    private static void setAppRegistryKeys() {
        // Autoruns
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "CheckVirusTotal", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "EulaAccepted", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "HideEmptyEntries", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "HideMicrosoftEntries", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "HideVirusTotalCleanEntries", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "HideWindowsEntries", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "ScanOnlyPerUserLocations", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "SubmitUnknownImages", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "VerifyCodeSignatures", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns\\VirusTotal", "VirusTotalTermsAccepted", 1);

        // Process Explorer
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "ConfirmKill", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "EulaAccepted", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "VerifySignatures", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "VirusTotalCheck", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "VirusTotalSubmitUnknown", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer\\VirusTotal", "VirusTotalTermsAccepted", 1);

        // Sophos Scan & Clean
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\SophosScanAndClean", "Registered", 1);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\SophosScanAndClean", "NoCookieScan", 1);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\SophosScanAndClean", "EULA37", 1);
    }
}
