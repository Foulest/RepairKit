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

import net.foulest.repairkit.RepairKit;
import net.foulest.repairkit.util.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for System Shortcuts.
 *
 * @author Foulest
 */
public class SystemShortcuts extends JPanel {

    // Grid layout constants
    private static final int[] COLUMN_X = {20, 250, 480};
    private static final int ROW_START_Y = 55;
    private static final int ROW_SPACING = 95;
    private static final int ROWS = 4;

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
     * Creates the System Shortcuts panel.
     */
    public SystemShortcuts() {
        // Sets the panel's layout to null.
        setLayout(null);

        // Creates the title label.
        @NotNull JLabel titleLabel = SwingUtil.createLabel("System Shortcuts",
                new Rectangle(20, 15, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 18)
        );
        add(titleLabel);

        // Creates tasks for the executor.
        @NotNull List<Runnable> tasks = Arrays.asList(
                this::setupAppsFeatures,
                this::setupStartupApps,
                this::setupWindowsUpdate,
                this::setupWindowsSecurity,

                this::setupDisplaySettings,
                this::setupStorageSettings,
                this::setupSoundSettings,
                this::setupSystemInformation,
                this::setupTaskManager,
                this::setupDeviceManager,
                this::setupDiskCleanup,
                this::setupMSConfig
        );

        // Executes tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);

        // Sets the panel's border.
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    /**
     * Sets up the Apps & Features section.
     */
    private void setupAppsFeatures() {
        int baseWidth = slotX(0);
        int baseHeight = slotY(0);

        // Adds a title label for Apps & Features.
        @NotNull JLabel title = SwingUtil.createLabel("Apps & Features",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Apps & Features.
        @NotNull JLabel description = SwingUtil.createLabel("Manage installed programs.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Apps & Features.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/AppsFeatures.png"), this);

        // Adds a button to launch Apps & Features.
        @NotNull JButton appButton = SwingUtil.createActionButton("Open Apps & Features",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        CommandUtil.runCommand("appwiz.cpl", true);
                    } else {
                        CommandUtil.runCommand("start ms-settings:appsfeatures", true);
                    }
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Startup Apps section.
     */
    private void setupStartupApps() {
        int baseWidth = slotX(1);
        int baseHeight = slotY(1);

        // Adds a title label for Startup Apps.
        @NotNull JLabel title = SwingUtil.createLabel("Startup Apps",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Startup Apps.
        @NotNull JLabel description = SwingUtil.createLabel("Manage startup programs.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Startup Apps.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/StartupApps.png"), this);

        // Adds a button to launch Startup Apps.
        @NotNull JButton appButton = SwingUtil.createActionButton("Open Startup Apps",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        CommandUtil.runCommand("msconfig", true);
                    } else {
                        CommandUtil.runCommand("start ms-settings:startupapps", true);
                    }
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Windows Update section.
     */
    private void setupWindowsUpdate() {
        int baseWidth = slotX(2);
        int baseHeight = slotY(2);

        // Adds a title label for Windows Update.
        @NotNull JLabel title = SwingUtil.createLabel("Windows Update",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Windows Update.
        @NotNull JLabel description = SwingUtil.createLabel("Check for updates.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Windows Update.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/WindowsUpdate.png"), this);

        // Adds a button to launch Windows Update.
        @NotNull JButton appButton = SwingUtil.createActionButton("Open Windows Update",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        if (RepairKit.isSafeMode()) {
                            SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                            JOptionPane.showMessageDialog(null, ConstantUtil.SAFE_MODE_MESSAGE, ConstantUtil.SAFE_MODE_TITLE, JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        CommandUtil.runCommand("control /name Microsoft.WindowsUpdate", true);
                    } else {
                        CommandUtil.runCommand("start ms-settings:windowsupdate", true);
                    }
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Windows Security section.
     */
    private void setupWindowsSecurity() {
        int baseWidth = slotX(3);
        int baseHeight = slotY(3);

        // Adds a title label for Windows Security.
        @NotNull JLabel title = SwingUtil.createLabel("Windows Security",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Windows Security.
        @NotNull JLabel description = SwingUtil.createLabel("Manage security settings.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Windows Security.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/WindowsSecurity.png"), this);

        // Adds a button to launch Windows Security.
        @NotNull JButton appButton = SwingUtil.createActionButton("Open Windows Security",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        if (RepairKit.isSafeMode()) {
                            SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                            JOptionPane.showMessageDialog(null, ConstantUtil.SAFE_MODE_MESSAGE, ConstantUtil.SAFE_MODE_TITLE, JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        CommandUtil.runCommand("control /name Microsoft.WindowsDefender", true);
                    } else {
                        CommandUtil.runCommand("start windowsdefender:", true);
                    }
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Display Settings section.
     */
    private void setupDisplaySettings() {
        int baseWidth = slotX(4);
        int baseHeight = slotY(4);

        // Adds a title label for Display Settings.
        @NotNull JLabel title = SwingUtil.createLabel("Display Settings",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Display Settings.
        @NotNull JLabel description = SwingUtil.createLabel("Manage display settings.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Display Settings.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/DisplaySettings.png"), this);

        // Adds a button to launch Display Settings.
        @NotNull JButton appButton = SwingUtil.createActionButton("Open Display Settings",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start desk.cpl", true)
        );
        add(appButton);
    }

    /**
     * Sets up the Storage Settings section.
     */
    private void setupStorageSettings() {
        int baseWidth = slotX(5);
        int baseHeight = slotY(5);

        // Adds a title label for Storage Settings.
        @NotNull JLabel title = SwingUtil.createLabel("Storage Settings",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Storage Settings.
        @NotNull JLabel description = SwingUtil.createLabel("Manage storage settings.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Storage Settings.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/StorageSettings.png"), this);

        // Adds a button to launch Storage Settings.
        @NotNull JButton appButton = SwingUtil.createActionButton("Open Storage Settings",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        CommandUtil.runCommand("start explorer /select,\"This PC\"", true);
                    } else {
                        CommandUtil.runCommand("start ms-settings:storagesense", true);
                    }
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Sound Settings section.
     */
    private void setupSoundSettings() {
        int baseWidth = slotX(6);
        int baseHeight = slotY(6);

        // Adds a title label for Sound Settings.
        @NotNull JLabel title = SwingUtil.createLabel("Sound Settings",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Sound Settings.
        @NotNull JLabel description = SwingUtil.createLabel("Manage sound settings.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Sound Settings.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/SoundSettings.png"), this);

        // Adds a button to launch Sound Settings.
        @NotNull JButton appButton = SwingUtil.createActionButton("Open Sound Settings",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        CommandUtil.runCommand("start control mmsys.cpl sounds", true);
                    } else {
                        CommandUtil.runCommand("start ms-settings:sound", true);
                    }
                }
        );
        add(appButton);
    }

    /**
     * Sets up the System Information section.
     */
    private void setupSystemInformation() {
        int baseWidth = slotX(7);
        int baseHeight = slotY(7);

        // Adds a title label for System Information.
        @NotNull JLabel title = SwingUtil.createLabel("System Information",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for System Information.
        @NotNull JLabel description = SwingUtil.createLabel("View system information.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for System Information.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/SystemInformation.png"), this);

        // Adds a button to launch System Information.
        @NotNull JButton appButton = SwingUtil.createActionButton("Open System Information",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> CommandUtil.runCommand("start msinfo32", true)
        );
        add(appButton);
    }

    /**
     * Sets up the Task Manager section.
     */
    private void setupTaskManager() {
        int baseWidth = slotX(8);
        int baseHeight = slotY(8);

        // Adds a title label for Task Manager.
        @NotNull JLabel title = SwingUtil.createLabel("Task Manager",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Task Manager.
        @NotNull JLabel description = SwingUtil.createLabel("Manage running processes.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Task Manager.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/TaskManager.png"), this);

        // Adds a button to launch Task Manager.
        @NotNull JButton appButton = SwingUtil.createActionButton("Open Task Manager",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("taskmgr", true)
        );
        add(appButton);
    }

    /**
     * Sets up the Device Manager section.
     */
    private void setupDeviceManager() {
        int baseWidth = slotX(9);
        int baseHeight = slotY(9);

        // Adds a title label for Device Manager.
        @NotNull JLabel title = SwingUtil.createLabel("Device Manager",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Device Manager.
        @NotNull JLabel description = SwingUtil.createLabel("Manage hardware devices.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Device Manager.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/DeviceManager.png"), this);

        // Adds a button to launch Device Manager.
        @NotNull JButton appButton = SwingUtil.createActionButton("Open Device Manager",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("devmgmt.msc", true)
        );
        add(appButton);
    }

    /**
     * Sets up the Disk Cleanup section.
     */
    private void setupDiskCleanup() {
        int baseWidth = slotX(10);
        int baseHeight = slotY(10);

        // Adds a title label for Disk Cleanup.
        @NotNull JLabel title = SwingUtil.createLabel("Disk Cleanup",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Disk Cleanup.
        @NotNull JLabel description = SwingUtil.createLabel("Clean up disk space.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Disk Cleanup.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/DiskCleanup.png"), this);

        // Adds a button to launch Disk Cleanup.
        @NotNull JButton appButton = SwingUtil.createActionButton("Open Disk Cleanup",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("cleanmgr", true)
        );
        add(appButton);
    }

    /**
     * Sets up the MSConfig section.
     */
    private void setupMSConfig() {
        int baseWidth = slotX(11);
        int baseHeight = slotY(11);

        // Adds a title label for MSConfig.
        @NotNull JLabel title = SwingUtil.createLabel("MSConfig",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for MSConfig.
        @NotNull JLabel description = SwingUtil.createLabel("System configuration utility.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for MSConfig.
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/MSConfig.png"), this);

        // Adds a button to launch MSConfig.
        @NotNull JButton appButton = SwingUtil.createActionButton("Open MSConfig",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("msconfig", true)
        );
        add(appButton);
    }
}
