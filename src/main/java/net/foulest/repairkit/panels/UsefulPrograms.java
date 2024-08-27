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
 * Useful Programs panel.
 *
 * @author Foulest
 */
public class UsefulPrograms extends JPanel {

    /**
     * Creates the Useful Programs panel.
     */
    public UsefulPrograms() {
        // Sets the panel's layout to null.
        DebugUtil.debug("Setting the Useful Programs panel layout to null...");
        setLayout(null);

        // Creates the title label.
        DebugUtil.debug("Creating the Useful Programs title label...");
        JLabel titleLabel = SwingUtil.createLabel("Useful Programs",
                new Rectangle(20, 15, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 18)
        );
        add(titleLabel);

        // Adds the components to the panel.
        DebugUtil.debug("Adding components to the Useful Programs panel...");

        // Creates tasks for the executor.
        List<Runnable> tasks = Arrays.asList(
                this::setupCPUZ,
                this::setupHWMonitor,
                this::setupAutoruns,
                this::setupProcessExplorer,
                this::setupTreeSize,
                this::setupEverything,
                this::setupFanControl,
                this::setupEmsisoftScan,
                this::setupNVCleanstall,
                this::setupSophosScan,
                this::setupUBlockOrigin,
                this::setupTrafficLight
        );

        // Executes tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);

        // Sets the panel's border.
        DebugUtil.debug("Setting the Useful Programs panel border...");
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
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
     * Sets up the FanControl section.
     */
    private void setupFanControl() {
        int baseHeight = 245;
        int baseWidth = 250;

        // Adds a title label for FanControl.
        DebugUtil.debug("Creating the FanControl title label...");
        JLabel title = SwingUtil.createLabel("FanControl",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for FanControl.
        DebugUtil.debug("Creating the FanControl description label...");
        JLabel description = SwingUtil.createLabel(ConstantUtil.VERSION_AUTO_UPDATED,
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for FanControl.
        DebugUtil.debug("Setting up the FanControl icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/FanControl.png"), this);

        // Adds a button to launch FanControl.
        DebugUtil.debug("Creating the FanControl launch button...");
        JButton appButton = SwingUtil.createActionButton("Launch FanControl",
                "Allows control over system fans.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                        JOptionPane.showMessageDialog(null, ConstantUtil.OUTDATED_OS_MESSAGE, ConstantUtil.OUTDATED_OS_TITLE, JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (RepairKit.isSafeMode()) {
                        SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                        JOptionPane.showMessageDialog(null, ConstantUtil.SAFE_MODE_MESSAGE, ConstantUtil.SAFE_MODE_TITLE, JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        String fanControlPath = CommandUtil.getCommandOutput("wmic process where name=\"FanControl.exe\""
                                + " get ExecutablePath /value", false, false).toString();
                        fanControlPath = fanControlPath.replace("[, , , , ExecutablePath=", "");
                        fanControlPath = fanControlPath.replace(", , , , , , , ]", "");

                        // If FanControl is running, launch the existing instance.
                        // Otherwise, extract it and launch it.
                        if (ProcessUtil.isProcessRunning("FanControl.exe")) {
                            CommandUtil.runCommand("start \"\" \"" + fanControlPath + "\"", false);
                        } else {
                            SwingUtil.launchApplication("FanControl.7z", "\\FanControl.exe",
                                    true, System.getenv("APPDATA") + "\\FanControl");
                        }
                    } catch (RuntimeException ex) {
                        ex.printStackTrace();
                    }
                }
        );
        add(appButton);
    }

    /**
     * Sets up the NVCleanstall section.
     */
    private void setupNVCleanstall() {
        int baseHeight = 340;
        int baseWidth = 250;

        // Adds a title label for NVCleanstall.
        DebugUtil.debug("Creating the NVCleanstall title label...");
        JLabel title = SwingUtil.createLabel("NVCleanstall",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for NVCleanstall.
        DebugUtil.debug("Creating the NVCleanstall description label...");
        JLabel description = SwingUtil.createLabel("Version: 1.16.0",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for NVCleanstall.
        DebugUtil.debug("Setting up the NVCleanstall icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/NVCleanstall.png"), this);

        // Adds a button to launch NVCleanstall.
        DebugUtil.debug("Creating the NVCleanstall launch button...");
        JButton appButton = SwingUtil.createActionButton("Launch NVCleanstall",
                "A lightweight NVIDIA graphics card driver updater.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                        JOptionPane.showMessageDialog(null, ConstantUtil.OUTDATED_OS_MESSAGE, ConstantUtil.OUTDATED_OS_TITLE, JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try (InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("bin/NVCleanstall.7z")) {
                        FileUtil.saveFile(Objects.requireNonNull(input), FileUtil.tempDirectory + "\\NVCleanstall.7z", true);
                        FileUtil.unzipFile(FileUtil.tempDirectory + "\\NVCleanstall.7z", FileUtil.tempDirectory.getPath());
                        CommandUtil.runCommand(FileUtil.tempDirectory + "\\NVCleanstall.exe", true);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
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
     * Sets up the uBlock Origin section.
     */
    private void setupUBlockOrigin() {
        int baseHeight = 245;
        int baseWidth = 480;

        // Adds a title label for uBlock Origin.
        DebugUtil.debug("Creating the uBlock Origin title label...");
        JLabel title = SwingUtil.createLabel("uBlock Origin",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for uBlock Origin.
        DebugUtil.debug("Creating the uBlock Origin description label...");
        JLabel description = SwingUtil.createLabel(ConstantUtil.VERSION_AUTO_UPDATED,
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for uBlock Origin.
        DebugUtil.debug("Setting up the uBlock Origin icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/uBlockOrigin.png"), this);

        // Adds a button to launch uBlock Origin.
        DebugUtil.debug("Creating the uBlock Origin launch button...");
        JButton appButton = SwingUtil.createActionButton("Launch uBlock Origin",
                "Link to the ad-blocker browser extension.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://ublockorigin.com", true)
        );
        add(appButton);
    }

    /**
     * Sets up the TrafficLight section.
     */
    private void setupTrafficLight() {
        int baseHeight = 340;
        int baseWidth = 480;

        // Adds a title label for TrafficLight.
        DebugUtil.debug("Creating the TrafficLight title label...");
        JLabel title = SwingUtil.createLabel("TrafficLight",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for TrafficLight.
        DebugUtil.debug("Creating the TrafficLight description label...");
        JLabel description = SwingUtil.createLabel(ConstantUtil.VERSION_AUTO_UPDATED,
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for TrafficLight.
        DebugUtil.debug("Setting up the TrafficLight icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/TrafficLight.png"), this);

        // Adds a button to launch TrafficLight.
        DebugUtil.debug("Creating the TrafficLight launch button...");
        JButton appButton = SwingUtil.createActionButton("Launch TrafficLight",
                "Link to BitDefender's TrafficLight extension.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://bitdefender.com/solutions/trafficlight.html", true)
        );
        add(appButton);
    }
}
