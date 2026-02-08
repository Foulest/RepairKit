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
package net.foulest.repairkit.panels;

import com.sun.jna.platform.win32.WinReg;
import net.foulest.repairkit.RepairKit;
import net.foulest.repairkit.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

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
        @NotNull JLabel titleLabel = SwingUtil.createLabel("Useful Programs",
                new Rectangle(20, 15, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 18)
        );
        add(titleLabel);

        // Creates the page label.
        DebugUtil.debug("Creating the Useful Programs (Page 1) page label...");
        @NotNull JLabel pageLabel = SwingUtil.createLabel("(Page 1/2)",
                new Rectangle(172, 15, 69, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 14)
        );
        add(pageLabel);

        // Adds the components to the panel.
        DebugUtil.debug("Adding components to the Useful Programs (Page 1) panel...");

        // Creates tasks for the executor.
        @NotNull List<Runnable> tasks = Arrays.asList(
                this::setupCPUZ,
                this::setupHWMonitor,
                this::setupEmsisoftScan,
                this::setupSophosScan,

                this::setupTreeSize,
                this::setupEverything,
                this::setupCrystalDiskInfo,
                this::setupCrystalDiskMark,

                this::setupAutoruns,
                this::setupProcessExplorer,
                this::setupProcessMonitor,
                this::setupBlueScreenView
        );

        // Executes tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);

        // Sets the panel's border.
        DebugUtil.debug("Setting the Useful Programs (Page 1) panel border...");
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Adds a label for the previous page button.
        @NotNull JLabel previousPage = SwingUtil.createLabel("<",
                new Rectangle(250, 21, 22, 22),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 25)
        );
        previousPage.setForeground(Color.LIGHT_GRAY);
        add(previousPage);

        // Adds a label for the next page button.
        @NotNull JLabel nextPage = SwingUtil.createLabel(">",
                new Rectangle(270, 21, 22, 22),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 25)
        );
        nextPage.setForeground(Color.DARK_GRAY);
        add(nextPage);

        // Adds a button under the next page label.
        @NotNull JButton nextPageButton = SwingUtil.createPanelButton("",
                "Useful Programs (Page 2)",
                new Rectangle(267, 20, 22, 23)
        );
        nextPageButton.setOpaque(false);
        nextPageButton.setContentAreaFilled(false);
        add(nextPageButton);
    }

    /**
     * Sets up the CPU-Z section.
     */
    private void setupCPUZ() {
        int baseHeight = 55;
        int baseWidth = 20;

        // Adds a title label for CPU-Z.
        DebugUtil.debug("Creating the CPU-Z title label...");
        @NotNull JLabel title = SwingUtil.createLabel("CPU-Z",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for CPU-Z.
        DebugUtil.debug("Creating the CPU-Z description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Version: 2.18.0",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for CPU-Z.
        DebugUtil.debug("Setting up the CPU-Z icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/CPU-Z.png"), this);

        // Adds a button to launch CPU-Z.
        DebugUtil.debug("Creating the CPU-Z launch button...");
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch CPU-Z",
                "Displays system hardware information.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches CPU-Z.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("CPU-Z.7z", "\\CPU-Z.exe", true, path);
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
        @NotNull JLabel title = SwingUtil.createLabel("HWMonitor",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for HWMonitor.
        DebugUtil.debug("Creating the HWMonitor description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Version: 1.61.0",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for HWMonitor.
        DebugUtil.debug("Setting up the HWMonitor icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/HWMonitor.png"), this);

        // Adds a button to launch HWMonitor.
        DebugUtil.debug("Creating the HWMonitor launch button...");
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch HWMonitor",
                "Displays hardware voltages & temperatures.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches HWMonitor.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("HWMonitor.7z", "\\HWMonitor.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Emsisoft Scan section.
     */
    private void setupEmsisoftScan() {
        int baseHeight = 245;
        int baseWidth = 20;

        // Adds a title label for Emsisoft Scan.
        DebugUtil.debug("Creating the Emsisoft Scan title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Emsisoft Scan",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Emsisoft Scan.
        DebugUtil.debug("Creating the Emsisoft Scan description label...");
        @NotNull JLabel description = SwingUtil.createLabel(ConstantUtil.VERSION_AUTO_UPDATED,
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
                        @NotNull String path = FileUtil.tempDirectory.getPath();
                        SwingUtil.launchApplication("Emsisoft.7z", "\\Emsisoft.exe", true, path);
                    }
            );
        }
        add(appButton);
    }

    /**
     * Sets up the Sophos Scan section.
     */
    private void setupSophosScan() {
        int baseHeight = 340;
        int baseWidth = 20;

        // Adds a title label for Sophos Scan.
        DebugUtil.debug("Creating the Sophos Scan title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Sophos Scan",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Sophos Scan.
        DebugUtil.debug("Creating the Sophos Scan description label...");
        @NotNull JLabel description = SwingUtil.createLabel(ConstantUtil.VERSION_AUTO_UPDATED,
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Sophos Scan.
        DebugUtil.debug("Setting up the Sophos Scan icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Sophos.png"), this);

        // Adds a button to launch Sophos Scan.
        DebugUtil.debug("Creating the Sophos Scan launch button...");
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch Sophos Scan",
                "Scans for malware with Sophos.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Sets registry keys for Sophos Scan.
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\SophosScanAndClean", "Registered", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\SophosScanAndClean", "NoCookieScan", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\SophosScanAndClean", "EULA37", 1);

                    // Unzips and launches Sophos Scan.
                    try (@Nullable InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("bin/Sophos.7z")) {
                        if (input == null) {
                            JOptionPane.showMessageDialog(null,
                                    "Failed to load Sophos file.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        FileUtil.saveFile(input, FileUtil.tempDirectory + "\\Sophos.7z", true);
                        @NotNull String path = FileUtil.tempDirectory.getPath();
                        FileUtil.unzipFile(FileUtil.tempDirectory + "\\Sophos.7z", path);

                        CommandUtil.runCommand("start \"\" \"" + FileUtil.tempDirectory + "\\Sophos.exe\"", true);
                    } catch (IOException ex) {
                        DebugUtil.warn("Failed to unzip file: Sophos.7z", ex);
                    }
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
        @NotNull JLabel title = SwingUtil.createLabel("TreeSize",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for TreeSize.
        DebugUtil.debug("Creating the TreeSize description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Version: 4.6.3.520",
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
                        @NotNull String path = FileUtil.tempDirectory.getPath();
                        SwingUtil.launchApplication("TreeSize.7z", "\\TreeSize.exe", true, path);
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
        @NotNull JLabel title = SwingUtil.createLabel("Everything",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Everything.
        DebugUtil.debug("Creating the Everything description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Version: 1.5.0.1404a",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Everything.
        DebugUtil.debug("Setting up the Everything icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Everything.png"), this);

        // Adds a button to launch Everything.
        DebugUtil.debug("Creating the Everything launch button...");
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch Everything",
                "Displays all files on your system.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches Everything.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("Everything.7z", "\\Everything-RepairKit.exe", true, path);
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
        @NotNull JLabel title = SwingUtil.createLabel("CrystalDiskInfo",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for CrystalDiskInfo.
        DebugUtil.debug("Creating the CrystalDiskInfo description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Version: 9.7.2",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for CrystalDiskInfo.
        DebugUtil.debug("Setting up the CrystalDiskInfo icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/CrystalDiskInfo.png"), this);

        // Adds a button to launch CrystalDiskInfo.
        DebugUtil.debug("Creating the CrystalDiskInfo launch button...");
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch CrystalDiskInfo",
                "Displays system storage information.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches CrystalDiskInfo.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("CrystalDiskInfo.7z", "\\CrystalDiskInfo.exe", true, path);
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
        @NotNull JLabel title = SwingUtil.createLabel("CrystalDiskMark",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for CrystalDiskMark.
        DebugUtil.debug("Creating the CrystalDiskMark description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Version: 9.0.1",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for CrystalDiskMark.
        DebugUtil.debug("Setting up the CrystalDiskMark icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/CrystalDiskMark.png"), this);

        // Adds a button to launch CrystalDiskMark.
        DebugUtil.debug("Creating the CrystalDiskMark launch button...");
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch CrystalDiskMark",
                "Tests system storage performance.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches CrystalDiskMark.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("CrystalDiskMark.7z", "\\CrystalDiskMark.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Autoruns section.
     */
    private void setupAutoruns() {
        int baseHeight = 55;
        int baseWidth = 480;

        // Adds a title label for Autoruns.
        DebugUtil.debug("Creating the Autoruns title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Autoruns",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Autoruns.
        DebugUtil.debug("Creating the Autoruns description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Version: 14.11",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Autoruns.
        DebugUtil.debug("Setting up the Autoruns icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Autoruns.png"), this);

        // Adds a button to launch HWMonitor.
        DebugUtil.debug("Creating the Autoruns launch button...");
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch Autoruns",
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
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("Autoruns.7z", "\\Autoruns.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Process Explorer section.
     */
    private void setupProcessExplorer() {
        int baseHeight = 150;
        int baseWidth = 480;

        // Adds a title label for Process Explorer.
        DebugUtil.debug("Creating the Process Explorer title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Process Explorer",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Process Explorer.
        DebugUtil.debug("Creating the Process Explorer description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Version: 17.06",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Process Explorer.
        DebugUtil.debug("Setting up the Process Explorer icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/ProcessExplorer.png"), this);

        // Adds a button to launch Process Explorer.
        DebugUtil.debug("Creating the Process Explorer launch button...");
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch Process Explorer",
                "Displays system processes.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Sets registry keys for Process Explorer.
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "ConfirmKill", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "EulaAccepted", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "VerifySignatures", 1);
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer\\VirusTotal", "VirusTotalTermsAccepted", 1);

                    // Unzips and launches Process Explorer.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("ProcessExplorer.7z", "\\ProcessExplorer.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Process Monitor section.
     */
    private void setupProcessMonitor() {
        int baseHeight = 245;
        int baseWidth = 480;

        // Adds a title label for Process Monitor.
        DebugUtil.debug("Creating the Process Monitor title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Process Monitor",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Process Monitor.
        DebugUtil.debug("Creating the Process Monitor description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Version: 4.01",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Process Monitor.
        DebugUtil.debug("Setting up the Process Monitor icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/ProcessMonitor.png"), this);

        // Adds a button to launch Process Monitor.
        DebugUtil.debug("Creating the Process Monitor launch button...");
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch Process Monitor",
                "Monitors system processes.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Sets registry keys for Process Monitor.
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Monitor", "EulaAccepted", 1);

                    // Unzips and launches Process Monitor.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("ProcessMonitor.7z", "\\ProcessMonitor.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the BlueScreenView section.
     */
    private void setupBlueScreenView() {
        int baseHeight = 340;
        int baseWidth = 480;

        // Adds a title label for BlueScreenView.
        DebugUtil.debug("Creating the BlueScreenView title label...");
        @NotNull JLabel title = SwingUtil.createLabel("BlueScreenView",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for BlueScreenView.
        DebugUtil.debug("Creating the BlueScreenView description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Version: 1.55",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for BlueScreenView.
        DebugUtil.debug("Setting up the BlueScreenView icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/BlueScreenView.png"), this);

        // Adds a button to launch BlueScreenView.
        DebugUtil.debug("Creating the BlueScreenView launch button...");
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch BlueScreenView",
                "Displays system crash information.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches BlueScreenView.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("BlueScreenView.7z", "\\BlueScreenView.exe", true, path);
                }
        );
        add(appButton);
    }
}
