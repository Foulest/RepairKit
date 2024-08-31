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
package net.foulest.repairkit.panels;

import com.sun.jna.platform.win32.WinReg;
import net.foulest.repairkit.RepairKit;
import net.foulest.repairkit.util.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Useful Programs panel (Page 1).
 *
 * @author Foulest
 */
public class UsefulPrograms1 extends JPanel {

    /**
     * Creates the Useful Programs (Page 1) panel.
     */
    public UsefulPrograms1() {
        // Sets the panel's layout to null.
        DebugUtil.debug("Setting the Useful Programs (Page 1) panel layout to null...");
        setLayout(null);

        // Creates the title label.
        DebugUtil.debug("Creating the Useful Programs (Page 1) title label...");
        JLabel titleLabel = SwingUtil.createLabel("Useful Programs",
                new Rectangle(20, 15, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 18)
        );
        add(titleLabel);

        // Creates the page label.
        DebugUtil.debug("Creating the Useful Programs (Page 1) page label...");
        JLabel pageLabel = SwingUtil.createLabel("(Page 1/2)",
                new Rectangle(172, 15, 69, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 14)
        );
        add(pageLabel);

        // Adds the components to the panel.
        DebugUtil.debug("Adding components to the Useful Programs (Page 1) panel...");

        // Creates tasks for the executor.
        List<Runnable> tasks = Arrays.asList(
                this::setupCPUZ,
                this::setupHWMonitor,
                this::setupAutoruns,
                this::setupProcessExplorer,
                this::setupTreeSize,
                this::setupEverything,
                this::setupCrystalDiskInfo,
                this::setupEmsisoftScan,
                this::setupCrystalDiskMark,
                this::setupSophosScan,
                this::setupBlueScreenView,
                this::setupWingetAutoUpdate
        );

        // Executes tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);

        // Sets the panel's border.
        DebugUtil.debug("Setting the Useful Programs (Page 1) panel border...");
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Adds a label for the previous page button.
        JLabel previousPage = SwingUtil.createLabel("<",
                new Rectangle(250, 21, 22, 22),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 25)
        );
        previousPage.setForeground(Color.LIGHT_GRAY);
        add(previousPage);

        // Adds a label for the next page button.
        JLabel nextPage = SwingUtil.createLabel(">",
                new Rectangle(270, 21, 22, 22),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 25)
        );
        nextPage.setForeground(Color.DARK_GRAY);
        nextPage.addMouseListener(SwingUtil.createPageButtonLabel("Useful Programs (Page 2)"));
        add(nextPage);
    }

    /**
     * Sets up the CPU-Z section.
     */
    private void setupCPUZ() {
        int baseHeight = 55;
        int baseWidth = 20;

        // Adds a title label for CPU-Z.
        DebugUtil.debug("Creating the CPU-Z title label...");
        JLabel title = SwingUtil.createLabel("CPU-Z",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for CPU-Z.
        DebugUtil.debug("Creating the CPU-Z description label...");
        JLabel description = SwingUtil.createLabel("Version: 2.10.0",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for CPU-Z.
        DebugUtil.debug("Setting up the CPU-Z icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/CPU-Z.png"), this);

        // Adds a button to launch CPU-Z.
        DebugUtil.debug("Creating the CPU-Z launch button...");
        JButton appButton = SwingUtil.createActionButton("Launch CPU-Z",
                "Displays system hardware information.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches CPU-Z.
                    SwingUtil.launchApplication("CPU-Z.7z", "\\CPU-Z.exe",
                            true, FileUtil.tempDirectory.getPath());
                }
        );
        add(appButton);
    }

    /**
     * Sets up the HWMonitor section.
     */
    private void setupHWMonitor() {
        int baseHeight = 150;
        int baseWidth = 20;

        // Adds a title label for the HWMonitor.
        DebugUtil.debug("Creating the HWMonitor title label...");
        JLabel title = SwingUtil.createLabel("HWMonitor",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for HWMonitor.
        DebugUtil.debug("Creating the HWMonitor description label...");
        JLabel description = SwingUtil.createLabel("Version: 1.54.0",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for HWMonitor.
        DebugUtil.debug("Setting up the HWMonitor icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/HWMonitor.png"), this);

        // Adds a button to launch HWMonitor.
        DebugUtil.debug("Creating the HWMonitor launch button...");
        JButton appButton = SwingUtil.createActionButton("Launch HWMonitor",
                "Displays hardware voltages & temperatures.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches HWMonitor.
                    SwingUtil.launchApplication("HWMonitor.7z", "\\HWMonitor.exe",
                            true, FileUtil.tempDirectory.getPath());
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Autoruns section.
     */
    private void setupAutoruns() {
        int baseHeight = 245;
        int baseWidth = 20;

        // Adds a title label for Autoruns.
        DebugUtil.debug("Creating the Autoruns title label...");
        JLabel title = SwingUtil.createLabel("Autoruns",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Autoruns.
        DebugUtil.debug("Creating the Autoruns description label...");
        JLabel description = SwingUtil.createLabel("Version: 14.11",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Autoruns.
        DebugUtil.debug("Setting up the Autoruns icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Autoruns.png"), this);

        // Adds a button to launch HWMonitor.
        DebugUtil.debug("Creating the Autoruns launch button...");
        JButton appButton = SwingUtil.createActionButton("Launch Autoruns",
                "Displays startup items.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Sets registry keys for Autoruns.
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "CheckVirusTotal", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "EulaAccepted", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "HideEmptyEntries", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "HideMicrosoftEntries", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "HideVirusTotalCleanEntries", 0);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "HideWindowsEntries", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "ScanOnlyPerUserLocations", 0);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "SubmitUnknownImages", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "VerifyCodeSignatures", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns\\VirusTotal", "VirusTotalTermsAccepted", 1);

                    // Unzips and launches Autoruns.
                    SwingUtil.launchApplication("Autoruns.7z", "\\Autoruns.exe",
                            true, FileUtil.tempDirectory.getPath());
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Process Explorer section.
     */
    private void setupProcessExplorer() {
        int baseHeight = 340;
        int baseWidth = 20;

        // Adds a title label for Process Explorer.
        DebugUtil.debug("Creating the Process Explorer title label...");
        JLabel title = SwingUtil.createLabel("Process Explorer",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Process Explorer.
        DebugUtil.debug("Creating the Process Explorer description label...");
        JLabel description = SwingUtil.createLabel("Version: 17.06",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Process Explorer.
        DebugUtil.debug("Setting up the Process Explorer icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/ProcessExplorer.png"), this);

        // Adds a button to launch Process Explorer.
        DebugUtil.debug("Creating the Process Explorer launch button...");
        JButton appButton = SwingUtil.createActionButton("Launch Process Explorer",
                "Displays system processes.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Sets registry keys for Process Explorer.
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "ConfirmKill", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "EulaAccepted", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "VerifySignatures", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "VirusTotalCheck", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "VirusTotalSubmitUnknown", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer\\VirusTotal", "VirusTotalTermsAccepted", 1);

                    // Unzips and launches Process Explorer.
                    SwingUtil.launchApplication("ProcessExplorer.7z", "\\ProcessExplorer.exe",
                            true, FileUtil.tempDirectory.getPath());
                }
        );
        add(appButton);
    }

    /**
     * Sets up the TreeSize section.
     */
    private void setupTreeSize() {
        int baseHeight = 55;
        int baseWidth = 250;

        // Adds a title label for TreeSize.
        DebugUtil.debug("Creating the TreeSize title label...");
        JLabel title = SwingUtil.createLabel("TreeSize",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for TreeSize.
        DebugUtil.debug("Creating the TreeSize description label...");
        JLabel description = SwingUtil.createLabel("Version: 4.6.3.520",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for TreeSize.
        DebugUtil.debug("Setting up the TreeSize icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/TreeSize.png"), this);

        // Adds a button to launch TreeSize.
        DebugUtil.debug("Creating the TreeSize launch button...");
        JButton appButton;
        if (RepairKit.isOutdatedOperatingSystem()) {
            appButton = SwingUtil.createActionButton("Launch TreeSize",
                    "Displays system files organized by size.",
                    new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                    new Color(200, 200, 200), () -> {
                        SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                        JOptionPane.showMessageDialog(null, ConstantUtil.OUTDATED_OS_MESSAGE, ConstantUtil.OUTDATED_OS_TITLE, JOptionPane.ERROR_MESSAGE);
                    }
            );
        } else {
            appButton = SwingUtil.createActionButton("Launch TreeSize",
                    "Displays system files organized by size.",
                    new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                    new Color(200, 200, 200), () -> {
                        // Unzips and launches TreeSize.
                        SwingUtil.launchApplication("TreeSize.7z", "\\TreeSize.exe",
                                true, FileUtil.tempDirectory.getPath());
                    }
            );
        }
        add(appButton);
    }

    /**
     * Sets up the Everything section.
     */
    private void setupEverything() {
        int baseHeight = 150;
        int baseWidth = 250;

        // Adds a title label for Everything.
        DebugUtil.debug("Creating the Everything title label...");
        JLabel title = SwingUtil.createLabel("Everything",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Everything.
        DebugUtil.debug("Creating the Everything description label...");
        JLabel description = SwingUtil.createLabel("Version: 1.4.1.1026",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Everything.
        DebugUtil.debug("Setting up the Everything icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Everything.png"), this);

        // Adds a button to launch Everything.
        DebugUtil.debug("Creating the Everything launch button...");
        JButton appButton = SwingUtil.createActionButton("Launch Everything",
                "Displays all files on your system.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches Everything.
                    SwingUtil.launchApplication("Everything.7z", "\\Everything.exe",
                            true, FileUtil.tempDirectory.getPath());
                }
        );
        add(appButton);
    }

    /**
     * Sets up the CrystalDiskInfo section.
     */
    private void setupCrystalDiskInfo() {
        int baseHeight = 245;
        int baseWidth = 250;

        // Adds a title label for CrystalDiskInfo.
        DebugUtil.debug("Creating the CrystalDiskInfo title label...");
        JLabel title = SwingUtil.createLabel("CrystalDiskInfo",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for CrystalDiskInfo.
        DebugUtil.debug("Creating the CrystalDiskInfo description label...");
        JLabel description = SwingUtil.createLabel("Version: 9.4.3",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for CrystalDiskInfo.
        DebugUtil.debug("Setting up the CrystalDiskInfo icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/CrystalDiskInfo.png"), this);

        // Adds a button to launch CrystalDiskInfo.
        DebugUtil.debug("Creating the CrystalDiskInfo launch button...");
        JButton appButton = SwingUtil.createActionButton("Launch CrystalDiskInfo",
                "Displays system storage information.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches CrystalDiskInfo.
                    SwingUtil.launchApplication("CrystalDiskInfo.7z", "\\CrystalDiskInfo.exe",
                            true, FileUtil.tempDirectory.getPath());
                }
        );
        add(appButton);
    }

    /**
     * Sets up the CrystalDiskMark section.
     */
    private void setupCrystalDiskMark() {
        int baseHeight = 340;
        int baseWidth = 250;

        // Adds a title label for CrystalDiskMark.
        DebugUtil.debug("Creating the CrystalDiskMark title label...");
        JLabel title = SwingUtil.createLabel("CrystalDiskMark",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for CrystalDiskMark.
        DebugUtil.debug("Creating the CrystalDiskMark description label...");
        JLabel description = SwingUtil.createLabel("Version: 8.0.5",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for CrystalDiskMark.
        DebugUtil.debug("Setting up the CrystalDiskMark icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/CrystalDiskMark.png"), this);

        // Adds a button to launch CrystalDiskMark.
        DebugUtil.debug("Creating the CrystalDiskMark launch button...");
        JButton appButton = SwingUtil.createActionButton("Launch CrystalDiskMark",
                "Tests system storage performance.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches CrystalDiskMark.
                    SwingUtil.launchApplication("CrystalDiskMark.7z", "\\CrystalDiskMark.exe",
                            true, FileUtil.tempDirectory.getPath());
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Emsisoft Scan section.
     */
    private void setupEmsisoftScan() {
        int baseHeight = 55;
        int baseWidth = 480;

        // Adds a title label for Emsisoft Scan.
        DebugUtil.debug("Creating the Emsisoft Scan title label...");
        JLabel title = SwingUtil.createLabel("Emsisoft Scan",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Emsisoft Scan.
        DebugUtil.debug("Creating the Emsisoft Scan description label...");
        JLabel description = SwingUtil.createLabel(ConstantUtil.VERSION_AUTO_UPDATED,
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Emsisoft Scan.
        DebugUtil.debug("Setting up the Emsisoft Scan icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Emsisoft.png"), this);

        // Adds a button to launch Emsisoft Scan.
        DebugUtil.debug("Creating the Emsisoft Scan launch button...");
        JButton appButton;
        if (RepairKit.isOutdatedOperatingSystem()) {
            appButton = SwingUtil.createActionButton("Launch Emsisoft Scan",
                    "Scans for malware with Emsisoft.",
                    new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                    new Color(200, 200, 200), () -> {
                        SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                        JOptionPane.showMessageDialog(null, ConstantUtil.OUTDATED_OS_MESSAGE, ConstantUtil.OUTDATED_OS_TITLE, JOptionPane.ERROR_MESSAGE);
                    }
            );
        } else {
            appButton = SwingUtil.createActionButton("Launch Emsisoft Scan",
                    "Scans for malware with Emsisoft.",
                    new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                    new Color(200, 200, 200), () -> {
                        // Unzips and launches Emsisoft Scan.
                        SwingUtil.launchApplication("Emsisoft.7z", "\\Emsisoft.exe",
                                true, FileUtil.tempDirectory.getPath());
                    }
            );
        }
        add(appButton);
    }

    /**
     * Sets up the Sophos Scan section.
     */
    private void setupSophosScan() {
        int baseHeight = 150;
        int baseWidth = 480;

        // Adds a title label for Sophos Scan.
        DebugUtil.debug("Creating the Sophos Scan title label...");
        JLabel title = SwingUtil.createLabel("Sophos Scan",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Sophos Scan.
        DebugUtil.debug("Creating the Sophos Scan description label...");
        JLabel description = SwingUtil.createLabel(ConstantUtil.VERSION_AUTO_UPDATED,
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Sophos Scan.
        DebugUtil.debug("Setting up the Sophos Scan icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Sophos.png"), this);

        // Adds a button to launch Sophos Scan.
        DebugUtil.debug("Creating the Sophos Scan launch button...");
        JButton appButton = SwingUtil.createActionButton("Launch Sophos Scan",
                "Scans for malware with Sophos.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Sets registry keys for Sophos Scan.
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\SophosScanAndClean", "Registered", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\SophosScanAndClean", "NoCookieScan", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\SophosScanAndClean", "EULA37", 1);

                    // Unzips and launches Sophos Scan.
                    try (InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("bin/Sophos.7z")) {
                        FileUtil.saveFile(Objects.requireNonNull(input), FileUtil.tempDirectory + "\\Sophos.7z", true);
                        FileUtil.unzipFile(FileUtil.tempDirectory + "\\Sophos.7z", FileUtil.tempDirectory.getPath());
                        CommandUtil.runCommand("start \"\" \"" + FileUtil.tempDirectory + "\\Sophos.exe\"", true);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
        );
        add(appButton);
    }

    /**
     * Sets up the BlueScreenView section.
     */
    private void setupBlueScreenView() {
        int baseHeight = 245;
        int baseWidth = 480;

        // Adds a title label for BlueScreenView.
        DebugUtil.debug("Creating the BlueScreenView title label...");
        JLabel title = SwingUtil.createLabel("BlueScreenView",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for BlueScreenView.
        DebugUtil.debug("Creating the BlueScreenView description label...");
        JLabel description = SwingUtil.createLabel("Version: 1.55",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for BlueScreenView.
        DebugUtil.debug("Setting up the BlueScreenView icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/BlueScreenView.png"), this);

        // Adds a button to launch BlueScreenView.
        DebugUtil.debug("Creating the BlueScreenView launch button...");
        JButton appButton = SwingUtil.createActionButton("Launch BlueScreenView",
                "Displays system crash information.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches BlueScreenView.
                    SwingUtil.launchApplication("BlueScreenView.7z", "\\BlueScreenView.exe",
                            true, FileUtil.tempDirectory.getPath());
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Winget-AutoUpdate section.
     */
    private void setupWingetAutoUpdate() {
        int baseHeight = 340;
        int baseWidth = 480;

        // Adds a title label for Winget-AutoUpdate.
        DebugUtil.debug("Creating the Winget-AutoUpdate title label...");
        JLabel title = SwingUtil.createLabel("Winget-AutoUpdate",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Winget-AutoUpdate.
        DebugUtil.debug("Creating the Winget-AutoUpdate description label...");
        JLabel description = SwingUtil.createLabel("Version: 1.20.2",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for TrafficLight.
        DebugUtil.debug("Setting up the Winget-AutoUpdate icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Winget-AutoUpdate.png"), this);

        // Adds a button to launch Winget-AutoUpdate.
        DebugUtil.debug("Creating the Winget-AutoUpdate launch button...");
        JButton appButton = SwingUtil.createActionButton("Launch Winget-AutoUpdate",
                "Automatically updates Windows Package Manager.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Enables Windows Script Host for Winget-AutoUpdate.
                    DebugUtil.debug("Enabling Windows Script Host for Winget-AutoUpdate...");
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows Script Host\\Settings", "Enabled", 1);

                    try (InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("bin/WAU.7z")) {
                        // Saves and unzips the Winget-AutoUpdate files.
                        DebugUtil.debug("Extracting Winget-AutoUpdate files...");
                        FileUtil.saveFile(Objects.requireNonNull(input), FileUtil.tempDirectory + "\\WAU.7z", true);
                        FileUtil.unzipFile(FileUtil.tempDirectory + "\\WAU.7z", FileUtil.tempDirectory.getPath());

                        // Prompt the user if they want to check for updates on logon, or just once.
                        int option = JOptionPane.showOptionDialog(null,
                                "Would you like Winget-AutoUpdate to automatically update your programs?",
                                "Winget-AutoUpdate", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                new String[]{"Yes", "No"}, "Yes");

                        // Silently installs Winget-AutoUpdate.
                        DebugUtil.debug("Silently installing Winget-AutoUpdate...");
                        CommandUtil.runCommand("PowerShell -ExecutionPolicy Bypass \""
                                + FileUtil.tempDirectory + "\\Winget-AutoUpdate-Install.ps1\" -Silent"
                                + (option == JOptionPane.YES_OPTION
                                ? " -UpdatesAtLogon -UpdatesInterval Daily"
                                : " -UpdatesInterval Never")
                                + " -NotificationLevel Full"
                                + " -StartMenuShortcut -DoNotUpdate", false);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    // Launches Winget-AutoUpdate.
                    DebugUtil.debug("Running update check with Winget-AutoUpdate...");
                    CommandUtil.runCommand("C:\\Windows\\system32\\wscript.exe"
                            + " \"C:\\ProgramData\\Winget-AutoUpdate\\Invisible.vbs\" \"powershell.exe"
                            + " -NoProfile -ExecutionPolicy Bypass -File"
                            + " \"\"\"C:\\ProgramData\\Winget-AutoUpdate\\user-run.ps1\"\"", false);
                }
        );
        add(appButton);
    }
}
