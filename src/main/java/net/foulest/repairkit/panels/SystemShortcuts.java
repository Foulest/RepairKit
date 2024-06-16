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

import net.foulest.repairkit.RepairKit;
import net.foulest.repairkit.util.TaskUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static net.foulest.repairkit.util.CommandUtil.runCommand;
import static net.foulest.repairkit.util.ConstantUtil.*;
import static net.foulest.repairkit.util.DebugUtil.debug;
import static net.foulest.repairkit.util.FileUtil.getImageIcon;
import static net.foulest.repairkit.util.SoundUtil.playSound;
import static net.foulest.repairkit.util.SwingUtil.*;

public class SystemShortcuts extends JPanel {

    /**
     * Creates the System Shortcuts panel.
     */
    public SystemShortcuts() {
        // Sets the panel's layout to null.
        debug("Setting the System Shortcuts panel layout to null...");
        setLayout(null);

        // Creates the title label.
        debug("Creating the System Shortcuts title label...");
        JLabel titleLabel = createLabel("System Shortcuts",
                new Rectangle(20, 15, 200, 30),
                new Font(ARIAL, Font.BOLD, 18)
        );
        add(titleLabel);

        // Adds the components to the panel.
        debug("Adding the components to the System Shortcuts panel...");

        // Creates tasks for the executor.
        List<Runnable> tasks = Arrays.asList(
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
        debug("Setting the System Shortcuts panel border...");
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    /**
     * Sets up the Apps & Features section.
     */
    public void setupAppsFeatures() {
        int baseHeight = 55;
        int baseWidth = 20;

        // Adds a title label for Apps & Features.
        debug("Creating the Apps & Features title label...");
        JLabel title = createLabel("Apps & Features",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Apps & Features.
        debug("Creating the Apps & Features description label...");
        JLabel description = createLabel("Manage installed programs.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Apps & Features.
        debug("Setting up the Apps & Features icon...");
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/AppsFeatures.png"), this);

        // Adds a button to launch Apps & Features.
        debug("Creating the Apps & Features button...");
        JButton appButton = createActionButton("Open Apps & Features",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (!RepairKit.isOutdatedOperatingSystem()) {
                        runCommand("start ms-settings:appsfeatures", true);
                    } else {
                        runCommand("appwiz.cpl", true);
                    }
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Startup Apps section.
     */
    public void setupStartupApps() {
        int baseHeight = 150;
        int baseWidth = 20;

        // Adds a title label for Startup Apps.
        debug("Creating the Startup Apps title label...");
        JLabel title = createLabel("Startup Apps",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Startup Apps.
        debug("Creating the Startup Apps description label...");
        JLabel description = createLabel("Manage startup programs.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Startup Apps.
        debug("Setting up the Startup Apps icon...");
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/StartupApps.png"), this);

        // Adds a button to launch Startup Apps.
        debug("Creating the Startup Apps button...");
        JButton appButton = createActionButton("Open Startup Apps",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (!RepairKit.isOutdatedOperatingSystem()) {
                        runCommand("start ms-settings:startupapps", true);
                    } else {
                        runCommand("msconfig", true);
                    }
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Windows Update section.
     */
    public void setupWindowsUpdate() {
        int baseHeight = 245;
        int baseWidth = 20;

        // Adds a title label for Windows Update.
        debug("Creating the Windows Update title label...");
        JLabel title = createLabel("Windows Update",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Windows Update.
        debug("Creating the Windows Update description label...");
        JLabel description = createLabel("Check for updates.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Windows Update.
        debug("Setting up the Windows Update icon...");
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/WindowsUpdate.png"), this);

        // Adds a button to launch Windows Update.
        debug("Creating the Windows Update button...");
        JButton appButton = createActionButton("Open Windows Update",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        if (RepairKit.isSafeMode()) {
                            playSound(ERROR_SOUND);
                            JOptionPane.showMessageDialog(null, SAFE_MODE_MESSAGE, SAFE_MODE_TITLE, JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        runCommand("control /name Microsoft.WindowsUpdate", true);
                    } else {
                        runCommand("start ms-settings:windowsupdate", true);
                    }
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Windows Security section.
     */
    public void setupWindowsSecurity() {
        int baseHeight = 340;
        int baseWidth = 20;

        // Adds a title label for Windows Security.
        debug("Creating the Windows Security title label...");
        JLabel title = createLabel("Windows Security",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Windows Security.
        debug("Creating the Windows Security description label...");
        JLabel description = createLabel("Manage security settings.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Windows Security.
        debug("Setting up the Windows Security icon...");
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/WindowsSecurity.png"), this);

        // Adds a button to launch Windows Security.
        debug("Creating the Windows Security button...");
        JButton appButton = createActionButton("Open Windows Security",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        if (RepairKit.isSafeMode()) {
                            playSound(ERROR_SOUND);
                            JOptionPane.showMessageDialog(null, SAFE_MODE_MESSAGE, SAFE_MODE_TITLE, JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        runCommand("control /name Microsoft.WindowsDefender", true);
                    } else {
                        runCommand("start windowsdefender:", true);
                    }
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Display Settings section.
     */
    public void setupDisplaySettings() {
        int baseHeight = 55;
        int baseWidth = 250;

        // Adds a title label for Display Settings.
        debug("Creating the Display Settings title label...");
        JLabel title = createLabel("Display Settings",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Display Settings.
        debug("Creating the Display Settings description label...");
        JLabel description = createLabel("Manage display settings.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Display Settings.
        debug("Setting up the Display Settings icon...");
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/DisplaySettings.png"), this);

        // Adds a button to launch Display Settings.
        debug("Creating the Display Settings button...");
        JButton appButton = createActionButton("Open Display Settings",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> runCommand("start desk.cpl", true)
        );
        add(appButton);
    }

    /**
     * Sets up the Storage Settings section.
     */
    public void setupStorageSettings() {
        int baseHeight = 150;
        int baseWidth = 250;

        // Adds a title label for Storage Settings.
        debug("Creating the Storage Settings title label...");
        JLabel title = createLabel("Storage Settings",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Storage Settings.
        debug("Creating the Storage Settings description label...");
        JLabel description = createLabel("Manage storage settings.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Storage Settings.
        debug("Setting up the Storage Settings icon...");
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/StorageSettings.png"), this);

        // Adds a button to launch Storage Settings.
        debug("Creating the Storage Settings button...");
        JButton appButton = createActionButton("Open Storage Settings",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        runCommand("start explorer /select,\"This PC\"", true);
                    } else {
                        runCommand("start ms-settings:storagesense", true);
                    }
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Sound Settings section.
     */
    public void setupSoundSettings() {
        int baseHeight = 245;
        int baseWidth = 250;

        // Adds a title label for Sound Settings.
        debug("Creating the Sound Settings title label...");
        JLabel title = createLabel("Sound Settings",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Sound Settings.
        debug("Creating the Sound Settings description label...");
        JLabel description = createLabel("Manage sound settings.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Sound Settings.
        debug("Setting up the Sound Settings icon...");
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/SoundSettings.png"), this);

        // Adds a button to launch Sound Settings.
        debug("Creating the Sound Settings button...");
        JButton appButton = createActionButton("Open Sound Settings",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        runCommand("start control mmsys.cpl sounds", true);
                    } else {
                        runCommand("start ms-settings:sound", true);
                    }
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Optional Features section.
     */
    public void setupOptionalFeatures() {
        int baseHeight = 340;
        int baseWidth = 250;

        // Adds a title label for Optional Features.
        debug("Creating the Optional Features title label...");
        JLabel title = createLabel("Optional Features",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Optional Features.
        debug("Creating the Optional Features description label...");
        JLabel description = createLabel("Manage optional features.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Sound Settings.
        debug("Setting up the Optional Features icon...");
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/AppsFeatures.png"), this);

        // Adds a button to launch Sound Settings.
        debug("Creating the Optional Features button...");
        JButton appButton = createActionButton("Open Optional Features",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        runCommand("start OptionalFeatures", true);
                    } else {
                        runCommand("start ms-settings:optionalfeatures", true);
                    }
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Task Manager section.
     */
    public void setupTaskManager() {
        int baseHeight = 55;
        int baseWidth = 480;

        // Adds a title label for Task Manager.
        debug("Creating the Task Manager title label...");
        JLabel title = createLabel("Task Manager",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Task Manager.
        debug("Creating the Task Manager description label...");
        JLabel description = createLabel("Manage running processes.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Task Manager.
        debug("Setting up the Task Manager icon...");
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/TaskManager.png"), this);

        // Adds a button to launch Task Manager.
        debug("Creating the Task Manager button...");
        JButton appButton = createActionButton("Open Task Manager",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> runCommand("taskmgr", true)
        );
        add(appButton);
    }

    /**
     * Sets up the Device Manager section.
     */
    public void setupDeviceManager() {
        int baseHeight = 150;
        int baseWidth = 480;

        // Adds a title label for Device Manager.
        debug("Creating the Device Manager title label...");
        JLabel title = createLabel("Device Manager",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Device Manager.
        debug("Creating the Device Manager description label...");
        JLabel description = createLabel("Manage hardware devices.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Device Manager.
        debug("Setting up the Device Manager icon...");
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/DeviceManager.png"), this);

        // Adds a button to launch Device Manager.
        debug("Creating the Device Manager button...");
        JButton appButton = createActionButton("Open Device Manager",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> runCommand("devmgmt.msc", true)
        );
        add(appButton);
    }

    /**
     * Sets up the Disk Cleanup section.
     */
    public void setupDiskCleanup() {
        int baseHeight = 245;
        int baseWidth = 480;

        // Adds a title label for Disk Cleanup.
        debug("Creating the Disk Cleanup title label...");
        JLabel title = createLabel("Disk Cleanup",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Disk Cleanup.
        debug("Creating the Disk Cleanup description label...");
        JLabel description = createLabel("Clean up disk space.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Disk Cleanup.
        debug("Setting up the Disk Cleanup icon...");
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/DiskCleanup.png"), this);

        // Adds a button to launch Disk Cleanup.
        debug("Creating the Disk Cleanup button...");
        JButton appButton = createActionButton("Open Disk Cleanup",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> runCommand("cleanmgr", true)
        );
        add(appButton);
    }

    /**
     * Sets up the MSConfig section.
     */
    public void setupMSConfig() {
        int baseHeight = 340;
        int baseWidth = 480;

        // Adds a title label for MSConfig.
        debug("Creating the MSConfig title label...");
        JLabel title = createLabel("MSConfig",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for MSConfig.
        debug("Creating the MSConfig description label...");
        JLabel description = createLabel("System configuration utility.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for MSConfig.
        debug("Setting up the MSConfig icon...");
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/MSConfig.png"), this);

        // Adds a button to launch MSConfig.
        debug("Creating the MSConfig button...");
        JButton appButton = createActionButton("Open MSConfig",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> runCommand("msconfig", true)
        );
        add(appButton);
    }
}
