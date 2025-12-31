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

    /**
     * Creates the System Shortcuts panel.
     */
    public SystemShortcuts() {
        // Sets the panel's layout to null.
        DebugUtil.debug("Setting the System Shortcuts panel layout to null...");
        setLayout(null);

        // Creates the title label.
        DebugUtil.debug("Creating the System Shortcuts title label...");
        @NotNull JLabel titleLabel = SwingUtil.createLabel("System Shortcuts",
                new Rectangle(20, 15, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 18)
        );
        add(titleLabel);

        // Adds the components to the panel.
        DebugUtil.debug("Adding the components to the System Shortcuts panel...");

        // Creates tasks for the executor.
        @NotNull List<Runnable> tasks = Arrays.asList(
                this::setupAppsFeatures,
                this::setupStartupApps,
                this::setupWindowsUpdate,
                this::setupWindowsSecurity,
                this::setupDisplaySettings,
                this::setupStorageSettings,
                this::setupSoundSettings,
                this::setupOptionalFeatures,
                this::setupTaskManager,
                this::setupDeviceManager,
                this::setupDiskCleanup,
                this::setupMSConfig
        );

        // Executes tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);

        // Sets the panel's border.
        DebugUtil.debug("Setting the System Shortcuts panel border...");
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    /**
     * Sets up the Apps & Features section.
     */
    private void setupAppsFeatures() {
        int baseHeight = 55;
        int baseWidth = 20;

        // Adds a title label for Apps & Features.
        DebugUtil.debug("Creating the Apps & Features title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Apps & Features",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Apps & Features.
        DebugUtil.debug("Creating the Apps & Features description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Manage installed programs.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Apps & Features.
        DebugUtil.debug("Setting up the Apps & Features icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/AppsFeatures.png"), this);

        // Adds a button to launch Apps & Features.
        DebugUtil.debug("Creating the Apps & Features button...");
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
        int baseHeight = 150;
        int baseWidth = 20;

        // Adds a title label for Startup Apps.
        DebugUtil.debug("Creating the Startup Apps title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Startup Apps",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Startup Apps.
        DebugUtil.debug("Creating the Startup Apps description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Manage startup programs.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Startup Apps.
        DebugUtil.debug("Setting up the Startup Apps icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/StartupApps.png"), this);

        // Adds a button to launch Startup Apps.
        DebugUtil.debug("Creating the Startup Apps button...");
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
        int baseHeight = 245;
        int baseWidth = 20;

        // Adds a title label for Windows Update.
        DebugUtil.debug("Creating the Windows Update title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Windows Update",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Windows Update.
        DebugUtil.debug("Creating the Windows Update description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Check for updates.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Windows Update.
        DebugUtil.debug("Setting up the Windows Update icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/WindowsUpdate.png"), this);

        // Adds a button to launch Windows Update.
        DebugUtil.debug("Creating the Windows Update button...");
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
        int baseHeight = 340;
        int baseWidth = 20;

        // Adds a title label for Windows Security.
        DebugUtil.debug("Creating the Windows Security title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Windows Security",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Windows Security.
        DebugUtil.debug("Creating the Windows Security description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Manage security settings.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Windows Security.
        DebugUtil.debug("Setting up the Windows Security icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/WindowsSecurity.png"), this);

        // Adds a button to launch Windows Security.
        DebugUtil.debug("Creating the Windows Security button...");
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
        int baseHeight = 55;
        int baseWidth = 250;

        // Adds a title label for Display Settings.
        DebugUtil.debug("Creating the Display Settings title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Display Settings",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Display Settings.
        DebugUtil.debug("Creating the Display Settings description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Manage display settings.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Display Settings.
        DebugUtil.debug("Setting up the Display Settings icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/DisplaySettings.png"), this);

        // Adds a button to launch Display Settings.
        DebugUtil.debug("Creating the Display Settings button...");
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
        int baseHeight = 150;
        int baseWidth = 250;

        // Adds a title label for Storage Settings.
        DebugUtil.debug("Creating the Storage Settings title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Storage Settings",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Storage Settings.
        DebugUtil.debug("Creating the Storage Settings description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Manage storage settings.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Storage Settings.
        DebugUtil.debug("Setting up the Storage Settings icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/StorageSettings.png"), this);

        // Adds a button to launch Storage Settings.
        DebugUtil.debug("Creating the Storage Settings button...");
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
        int baseHeight = 245;
        int baseWidth = 250;

        // Adds a title label for Sound Settings.
        DebugUtil.debug("Creating the Sound Settings title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Sound Settings",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Sound Settings.
        DebugUtil.debug("Creating the Sound Settings description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Manage sound settings.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Sound Settings.
        DebugUtil.debug("Setting up the Sound Settings icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/SoundSettings.png"), this);

        // Adds a button to launch Sound Settings.
        DebugUtil.debug("Creating the Sound Settings button...");
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
     * Sets up the Optional Features section.
     */
    private void setupOptionalFeatures() {
        int baseHeight = 340;
        int baseWidth = 250;

        // Adds a title label for Optional Features.
        DebugUtil.debug("Creating the Optional Features title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Optional Features",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Optional Features.
        DebugUtil.debug("Creating the Optional Features description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Manage optional features.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Sound Settings.
        DebugUtil.debug("Setting up the Optional Features icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/AppsFeatures.png"), this);

        // Adds a button to launch Sound Settings.
        DebugUtil.debug("Creating the Optional Features button...");
        @NotNull JButton appButton = SwingUtil.createActionButton("Open Optional Features",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        CommandUtil.runCommand("start OptionalFeatures", true);
                    } else {
                        CommandUtil.runCommand("start ms-settings:optionalfeatures", true);
                    }
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Task Manager section.
     */
    private void setupTaskManager() {
        int baseHeight = 55;
        int baseWidth = 480;

        // Adds a title label for Task Manager.
        DebugUtil.debug("Creating the Task Manager title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Task Manager",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Task Manager.
        DebugUtil.debug("Creating the Task Manager description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Manage running processes.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Task Manager.
        DebugUtil.debug("Setting up the Task Manager icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/TaskManager.png"), this);

        // Adds a button to launch Task Manager.
        DebugUtil.debug("Creating the Task Manager button...");
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
        int baseHeight = 150;
        int baseWidth = 480;

        // Adds a title label for Device Manager.
        DebugUtil.debug("Creating the Device Manager title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Device Manager",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Device Manager.
        DebugUtil.debug("Creating the Device Manager description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Manage hardware devices.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Device Manager.
        DebugUtil.debug("Setting up the Device Manager icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/DeviceManager.png"), this);

        // Adds a button to launch Device Manager.
        DebugUtil.debug("Creating the Device Manager button...");
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
        int baseHeight = 245;
        int baseWidth = 480;

        // Adds a title label for Disk Cleanup.
        DebugUtil.debug("Creating the Disk Cleanup title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Disk Cleanup",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Disk Cleanup.
        DebugUtil.debug("Creating the Disk Cleanup description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Clean up disk space.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Disk Cleanup.
        DebugUtil.debug("Setting up the Disk Cleanup icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/DiskCleanup.png"), this);

        // Adds a button to launch Disk Cleanup.
        DebugUtil.debug("Creating the Disk Cleanup button...");
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
        int baseHeight = 340;
        int baseWidth = 480;

        // Adds a title label for MSConfig.
        DebugUtil.debug("Creating the MSConfig title label...");
        @NotNull JLabel title = SwingUtil.createLabel("MSConfig",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for MSConfig.
        DebugUtil.debug("Creating the MSConfig description label...");
        @NotNull JLabel description = SwingUtil.createLabel("System configuration utility.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for MSConfig.
        DebugUtil.debug("Setting up the MSConfig icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/MSConfig.png"), this);

        // Adds a button to launch MSConfig.
        DebugUtil.debug("Creating the MSConfig button...");
        @NotNull JButton appButton = SwingUtil.createActionButton("Open MSConfig",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("msconfig", true)
        );
        add(appButton);
    }
}
