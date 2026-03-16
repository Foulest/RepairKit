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
 * Useful Programs panel.
 * Hosts all program entries across two navigable pages.
 *
 * @author Foulest
 */
public class UsefulPrograms extends JPanel {

    // Grid layout constants
    private static final int[] COLUMN_X = {20, 250, 480};
    private static final int ROW_START_Y = 55;
    private static final int ROW_SPACING = 95;
    private static final int ROWS = 4;

    // The current page. Defaults to 1.
    private int currentPage = 1;

    /**
     * Returns the base X position for the given slot index.
     * Slots fill left-to-right, top-to-bottom (0-indexed).
     */
    private static int slotX(int slot) {
        return COLUMN_X[slot / ROWS];
    }

    /**
     * Returns the base Y position for the given slot index.
     * Slots fill left-to-right, top-to-bottom (0-indexed).
     */
    private static int slotY(int slot) {
        return ROW_START_Y + (slot % ROWS) * ROW_SPACING;
    }

    /**
     * Creates the Useful Programs panel and displays page 1.
     */
    public UsefulPrograms() {
        setLayout(null);
        showPage(1);
    }

    /**
     * Clears the panel and renders the given page.
     *
     * @param page The page to display.
     */
    private void showPage(int page) {
        currentPage = page;
        removeAll();

        // Creates the title label.
        @NotNull JLabel titleLabel = SwingUtil.createLabel("Useful Programs",
                new Rectangle(20, 15, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 18)
        );
        add(titleLabel);

        // Creates the page label.
        @NotNull JLabel pageLabel = SwingUtil.createLabel("(Page " + page + "/3)",
                new Rectangle(172, 15, 69, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 14)
        );
        add(pageLabel);

        // Previous page button ("<")
        @NotNull JLabel previousPage = SwingUtil.createLabel("<",
                new Rectangle(250, 21, 22, 22),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 25)
        );
        previousPage.setForeground(page == 1 ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        add(previousPage);

        if (page > 1) {
            @NotNull JButton previousPageButton = new JButton();
            previousPageButton.setBounds(246, 20, 22, 23);
            previousPageButton.setOpaque(false);
            previousPageButton.setContentAreaFilled(false);
            previousPageButton.setBorderPainted(false);
            previousPageButton.addActionListener(e -> showPage(page - 1));
            add(previousPageButton);
        }

        // Next page button (">")
        @NotNull JLabel nextPage = SwingUtil.createLabel(">",
                new Rectangle(270, 21, 22, 22),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 25)
        );
        nextPage.setForeground(page == 3 ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        add(nextPage);

        if (page < 3) {
            @NotNull JButton nextPageButton = new JButton();
            nextPageButton.setBounds(267, 20, 22, 23);
            nextPageButton.setOpaque(false);
            nextPageButton.setContentAreaFilled(false);
            nextPageButton.setBorderPainted(false);
            nextPageButton.addActionListener(e -> showPage(page + 1));
            add(nextPageButton);
        }

        // Sets the panel's border.
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Sets up all the pages.
        if (page == 1) {
            setupPage1();
        } else if (page == 2) {
            setupPage2();
        } else if (page == 3) {
            setupPage3();
        }

        // Revalidates and paints.
        revalidate();
        repaint();
    }

    private void setupPage1() {
        // noinspection NestedMethodCall
        @NotNull List<Runnable> tasks = Arrays.asList(
                // Hardware Information
                () -> setupCPUZ(0, 0),
                () -> setupHWMonitor(1, 1),
                () -> setupHWiNFO(2, 2),
                () -> setupHeavyLoad(3, 3),

                // File Utilities
                () -> setupWizTree(4, 4),
                () -> setupEverything(5, 5),

                // Malware Scanners
                () -> setupEmsisoftScan(6, 6),
                () -> setupSophosScan(7, 7),

                // Disk Utilities
                () -> setupCrystalDiskInfo(8, 8),
                () -> setupCrystalDiskMark(9, 9),
                () -> setupRecuva(10, 10),
                () -> setupDiskGenius(11, 11)
        );

        TaskUtil.executeTasks(tasks);
    }

    private void setupPage2() {
        // noinspection NestedMethodCall
        @NotNull List<Runnable> tasks = Arrays.asList(
                // File Tools, cont.
                () -> setupRufus(0, 0),

                // Process Tools
                () -> setupAutoruns(1, 1),
                () -> setupProcessExplorer(2, 2),
                () -> setupProcessMonitor(3, 3),

                // Networking Tools
                () -> setupTCPView(4, 4),
                () -> setupWinMTR(5, 5),

                // Nirsoft
                () -> setupBlueScreenView(6, 6),
                () -> setupRegScanner(7, 7),
                () -> setupUSBDeview(8, 8),
                () -> setupUSBLogView(9, 9),

                // Graphics Card Utilities
                () -> setupNVCleanstall(10, 10),
                () -> setupDDU(11, 11)
        );

        TaskUtil.executeTasks(tasks);
    }

    private void setupPage3() {
        // noinspection NestedMethodCall
        @NotNull List<Runnable> tasks = Arrays.asList(
                // Shortcuts: Windows Tools
                () -> setup7Zip(0, 0),
                () -> setupNotepadPlusPlus(1, 1),
                () -> setupTwinkleTray(2, 2),
                () -> setupFanControl(3, 3),

                // Shortcuts: Security Tools
                () -> setupBitwarden(4, 4),
                () -> setupSophosHome(5, 5),
                () -> setupUBlockOrigin(6, 6),
                () -> setupOsprey(7, 7)
        );

        TaskUtil.executeTasks(tasks);
    }

    /**
     * Sets up the CPU-Z section.
     */
    private void setupCPUZ(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for CPU-Z.
        @NotNull JLabel title = SwingUtil.createLabel("CPU-Z",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for CPU-Z.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 2.19.0",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for CPU-Z.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/CPU-Z.png"), this);

        // Adds a button to launch CPU-Z.
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
    private void setupHWMonitor(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for HWMonitor.
        @NotNull JLabel title = SwingUtil.createLabel("HWMonitor",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for HWMonitor.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 1.62.0",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for HWMonitor.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/HWMonitor.png"), this);

        // Adds a button to launch HWMonitor.
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
     * Sets up the HWiNFO section.
     */
    private void setupHWiNFO(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for HWiNFO.
        @NotNull JLabel title = SwingUtil.createLabel("HWiNFO",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for HWiNFO.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 8.44-5935",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for HWiNFO.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/HWiNFO.png"), this);

        // Adds a button to launch HWiNFO.
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch HWiNFO",
                "Displays detailed hardware information.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches HWiNFO.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("HWiNFO.7z", "\\HWiNFO.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the HeavyLoad section.
     */
    private void setupHeavyLoad(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for HeavyLoad.
        @NotNull JLabel title = SwingUtil.createLabel("HeavyLoad",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for HeavyLoad.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 4.0.0.400",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for HeavyLoad.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/HeavyLoad.png"), this);

        // Adds a button to launch HeavyLoad.
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch HeavyLoad",
                "Stresses system components to test stability.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches HeavyLoad.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("HeavyLoad.7z", "\\HeavyLoad.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Rufus section.
     */
    private void setupRufus(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for Rufus.
        @NotNull JLabel title = SwingUtil.createLabel("Rufus",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Rufus.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 4.13.2316",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Rufus.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Rufus.png"), this);

        // Adds a button to launch Rufus.
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch Rufus",
                "Creates bootable USB drives.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches Rufus.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("Rufus.7z", "\\rufus.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the WizTree section.
     */
    private void setupWizTree(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for WizTree.
        @NotNull JLabel title = SwingUtil.createLabel("WizTree",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for WizTree.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 4.30",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for WizTree.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/WizTree.png"), this);

        // Adds a button to launch WizTree.
        JButton appButton;
        if (RepairKit.isOutdatedOperatingSystem()) {
            appButton = SwingUtil.createActionButton("Launch WizTree",
                    "Displays system files organized by size.",
                    new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                    new Color(200, 200, 200), () -> {
                        SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                        JOptionPane.showMessageDialog(null, ConstantUtil.OUTDATED_OS_MESSAGE, ConstantUtil.OUTDATED_OS_TITLE, JOptionPane.ERROR_MESSAGE);
                    }
            );
        } else {
            appButton = SwingUtil.createActionButton("Launch WizTree",
                    "Displays system files organized by size.",
                    new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                    new Color(200, 200, 200), () -> {
                        // Unzips and launches WizTree.
                        @NotNull String path = FileUtil.tempDirectory.getPath();
                        SwingUtil.launchApplication("WizTree.7z", "\\WizTree.exe", true, path);
                    }
            );
        }
        add(appButton);
    }

    /**
     * Sets up the Emsisoft Scan section.
     */
    private void setupEmsisoftScan(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for Emsisoft Scan.
        @NotNull JLabel title = SwingUtil.createLabel("Emsisoft Scan",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Emsisoft Scan.
        @NotNull JLabel description = SwingUtil.createLabel(ConstantUtil.VERSION_AUTO_UPDATED,
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Emsisoft Scan.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Emsisoft.png"), this);

        // Adds a button to launch Emsisoft Scan.
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
    private void setupSophosScan(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for Sophos Scan.
        @NotNull JLabel title = SwingUtil.createLabel("Sophos Scan",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Sophos Scan.
        @NotNull JLabel description = SwingUtil.createLabel(ConstantUtil.VERSION_AUTO_UPDATED,
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Sophos Scan.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Sophos.png"), this);

        // Adds a button to launch Sophos Scan.
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
     * Sets up the CrystalDiskInfo section.
     */
    private void setupCrystalDiskInfo(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for CrystalDiskInfo.
        @NotNull JLabel title = SwingUtil.createLabel("CrystalDiskInfo",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for CrystalDiskInfo.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 9.8.0",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for CrystalDiskInfo.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/CrystalDiskInfo.png"), this);

        // Adds a button to launch CrystalDiskInfo.
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
    private void setupCrystalDiskMark(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for CrystalDiskMark.
        @NotNull JLabel title = SwingUtil.createLabel("CrystalDiskMark",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for CrystalDiskMark.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 9.0.2",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for CrystalDiskMark.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/CrystalDiskMark.png"), this);

        // Adds a button to launch CrystalDiskMark.
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
    private void setupAutoruns(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for Autoruns.
        @NotNull JLabel title = SwingUtil.createLabel("Autoruns",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Autoruns.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 14.11",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Autoruns.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Autoruns.png"), this);

        // Adds a button to launch Autoruns.
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
    private void setupProcessExplorer(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for Process Explorer.
        @NotNull JLabel title = SwingUtil.createLabel("Process Explorer",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Process Explorer.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 17.06",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Process Explorer.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/ProcessExplorer.png"), this);

        // Adds a button to launch Process Explorer.
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
    private void setupProcessMonitor(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for Process Monitor.
        @NotNull JLabel title = SwingUtil.createLabel("Process Monitor",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Process Monitor.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 4.01",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Process Monitor.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/ProcessMonitor.png"), this);

        // Adds a button to launch Process Monitor.
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
     * Sets up the TCPView section.
     */
    private void setupTCPView(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for TCPView.
        @NotNull JLabel title = SwingUtil.createLabel("TCPView",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for TCPView.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 4.19",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for TCPView.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/TCPView.png"), this);

        // Adds a button to launch TCPView.
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch TCPView",
                "Displays active network connections.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches TCPView.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("TCPView.7z", "\\TCPView.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the WinMTR section.
     */
    private void setupWinMTR(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for WinMTR.
        @NotNull JLabel title = SwingUtil.createLabel("WinMTR",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for WinMTR.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 1.0.0",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for WinMTR.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/WinMTR.png"), this);

        // Adds a button to launch WinMTR.
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch WinMTR",
                "Traces network routes to diagnose connectivity issues.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches WinMTR.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("WinMTR.7z", "\\WinMTR.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the RegScanner section.
     */
    private void setupRegScanner(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for RegScanner.
        @NotNull JLabel title = SwingUtil.createLabel("RegScanner",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for RegScanner.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 2.75",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for RegScanner.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/RegScanner.png"), this);

        // Adds a button to launch RegScanner.
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch RegScanner",
                "Scans the Windows registry for specific values.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches RegScanner.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("RegScanner.7z", "\\RegScanner.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the USBDeview section.
     */
    private void setupUSBDeview(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for USBDeview.
        @NotNull JLabel title = SwingUtil.createLabel("USBDeview",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for USBDeview.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 3.07",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for USBDeview.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/USBDeview.png"), this);

        // Adds a button to launch USBDeview.
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch USBDeview",
                "Displays information about USB devices connected to the system.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches USBDeview.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("USBDeview.7z", "\\USBDeview.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the USBLogView section.
     */
    private void setupUSBLogView(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for USBLogView.
        @NotNull JLabel title = SwingUtil.createLabel("USBLogView",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for USBLogView.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 1.26",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for USBLogView.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/USBLogView.png"), this);

        // Adds a button to launch USBLogView.
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch USBLogView",
                "Displays logs of USB device connections and disconnections.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches USBLogView.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("USBLogView.7z", "\\USBLogView.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the BlueScreenView section.
     */
    private void setupBlueScreenView(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for BlueScreenView.
        @NotNull JLabel title = SwingUtil.createLabel("BlueScreenView",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for BlueScreenView.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 1.55",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for BlueScreenView.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/BlueScreenView.png"), this);

        // Adds a button to launch BlueScreenView.
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

    /**
     * Sets up the Everything section.
     */
    private void setupEverything(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for Everything.
        @NotNull JLabel title = SwingUtil.createLabel("Everything",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Everything.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 1.5.0.1404a",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Everything.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Everything.png"), this);

        // Adds a button to launch Everything.
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
     * Sets up the NVCleanstall section.
     */
    private void setupNVCleanstall(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for NVCleanstall.
        @NotNull JLabel title = SwingUtil.createLabel("NVCleanstall",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for NVCleanstall.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 1.19.0",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for NVCleanstall.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/NVCleanstall.png"), this);

        // Adds a button to launch NVCleanstall.
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch NVCleanstall",
                "A lightweight NVIDIA graphics card driver updater.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                        JOptionPane.showMessageDialog(null, ConstantUtil.OUTDATED_OS_MESSAGE, ConstantUtil.OUTDATED_OS_TITLE, JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Unzips and launches NVCleanstall.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("NVCleanstall.7z", "\\NVCleanstall.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Display Driver Uninstaller (DDU) section.
     */
    private void setupDDU(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for DDU.
        @NotNull JLabel title = SwingUtil.createLabel("DDU",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for DDU.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 18.1.4.1",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for DDU.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/DDU.png"), this);

        // Adds a button to launch DDU.
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch DDU",
                "Display Driver Uninstaller for NVIDIA, AMD, and Intel.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches DDU.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("DDU.7z", "\\Display Driver Uninstaller.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Recuva section.
     */
    private void setupRecuva(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for Recuva.
        @NotNull JLabel title = SwingUtil.createLabel("Recuva",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Recuva.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 1.54.120",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Recuva.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Recuva.png"), this);

        // Adds a button to launch Recuva.
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch Recuva",
                "Data recovery software for deleted files.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches Recuva.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("Recuva.7z", "\\recuva.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the DiskGenius section.
     */
    private void setupDiskGenius(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for DiskGenius.
        @NotNull JLabel title = SwingUtil.createLabel("DiskGenius",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for DiskGenius.
        @NotNull JLabel description = SwingUtil.createLabel("Version: 6.1.1.1742",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for DiskGenius.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/DiskGenius.png"), this);

        // Adds a button to launch DiskGenius.
        @NotNull JButton appButton = SwingUtil.createActionButton("Launch DiskGenius",
                "Disk partition management and migration tool.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches DiskGenius.
                    @NotNull String path = FileUtil.tempDirectory.getPath();
                    SwingUtil.launchApplication("DiskGenius.7z", "\\DiskGenius.exe", true, path);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the 7-Zip section.
     */
    private void setup7Zip(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for 7-Zip.
        @NotNull JLabel title = SwingUtil.createLabel("7-Zip",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for 7-Zip.
        @NotNull JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for 7-Zip.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/7-Zip.png"), this);

        // Adds a button to launch 7-Zip.
        @NotNull JButton appButton = SwingUtil.createActionButton("Visit 7-Zip",
                "Free and open-source file archiver.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://7-zip.org/download.html", true)
        );
        add(appButton);
    }

    /**
     * Sets up the Notepad++ section.
     */
    private void setupNotepadPlusPlus(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for Notepad++.
        @NotNull JLabel title = SwingUtil.createLabel("Notepad++",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Notepad++.
        @NotNull JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Notepad++.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Notepad++.png"), this);

        // Adds a button to launch Notepad++.
        @NotNull JButton appButton = SwingUtil.createActionButton("Visit Notepad++",
                "Free and open-source text editor.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://notepad-plus-plus.org/downloads", true)
        );
        add(appButton);
    }

    /**
     * Sets up the Bitwarden section.
     */
    private void setupBitwarden(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for Bitwarden.
        @NotNull JLabel title = SwingUtil.createLabel("Bitwarden",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Bitwarden.
        @NotNull JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Bitwarden.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Bitwarden.png"), this);

        // Adds a button to launch Bitwarden.
        @NotNull JButton appButton = SwingUtil.createActionButton("Visit Bitwarden",
                "Free and open-source password manager.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://bitwarden.com/download/#downloads-web-browser", true)
        );
        add(appButton);
    }

    /**
     * Sets up the Sophos Home section.
     */
    private void setupSophosHome(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for Sophos Home.
        @NotNull JLabel title = SwingUtil.createLabel("Sophos Home",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Sophos Home.
        @NotNull JLabel description = SwingUtil.createLabel("Price: $5/month",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Sophos Home.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Sophos.png"), this);

        // Adds a button to launch Sophos Home.
        @NotNull JButton appButton = SwingUtil.createActionButton("Visit Sophos Home",
                "Award-winning system protection software.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://home.sophos.com", true)
        );
        add(appButton);
    }

    /**
     * Sets up the uBlock Origin section.
     */
    private void setupUBlockOrigin(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for uBlock Origin.
        @NotNull JLabel title = SwingUtil.createLabel("uBlock Origin",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for uBlock Origin.
        @NotNull JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for uBlock Origin.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/uBlockOrigin.png"), this);

        // Adds a button to launch uBlock Origin.
        @NotNull JButton appButton = SwingUtil.createActionButton("Visit uBlock Origin",
                "Browser extension for content-filtering.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://ublockorigin.com", true)
        );
        add(appButton);
    }

    /**
     * Sets up the Osprey section.
     */
    private void setupOsprey(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for Osprey.
        @NotNull JLabel title = SwingUtil.createLabel("Osprey",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Osprey.
        @NotNull JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Osprey.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Osprey.png"), this);

        // Adds a button to launch Osprey.
        @NotNull JButton appButton = SwingUtil.createActionButton("Visit Osprey",
                "Browser extension for safe browsing.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://osprey.ac", true)
        );
        add(appButton);
    }

    /**
     * Sets up the Twinkle Tray section.
     */
    private void setupTwinkleTray(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for Twinkle Tray.
        @NotNull JLabel title = SwingUtil.createLabel("Twinkle Tray",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Twinkle Tray.
        @NotNull JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Twinkle Tray.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/TwinkleTray.png"), this);

        // Adds a button to launch Twinkle Tray.
        @NotNull JButton appButton = SwingUtil.createActionButton("Visit Twinkle Tray",
                "Monitor brightness control for Windows.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://twinkletray.com", true)
        );
        add(appButton);
    }

    /**
     * Sets up the FanControl section.
     */
    private void setupFanControl(int x, int y) {
        int baseWidth = slotX(x);
        int baseHeight = slotY(y);

        // Adds a title label for FanControl.
        @NotNull JLabel title = SwingUtil.createLabel("FanControl",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for FanControl.
        @NotNull JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for FanControl.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/FanControl.png"), this);

        // Adds a button to launch FanControl.
        @NotNull JButton appButton = SwingUtil.createActionButton("Visit FanControl",
                "Control your computer's fan speeds.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://getfancontrol.com", true)
        );
        add(appButton);
    }
}
