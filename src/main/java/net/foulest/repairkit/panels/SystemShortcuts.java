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

import lombok.extern.java.Log;
import net.foulest.repairkit.RepairKit;

import javax.swing.*;
import java.awt.*;

import static net.foulest.repairkit.util.CommandUtil.runCommand;
import static net.foulest.repairkit.util.FileUtil.getImageIcon;
import static net.foulest.repairkit.util.SoundUtil.playSound;
import static net.foulest.repairkit.util.SwingUtil.*;

@Log
public class SystemShortcuts extends JPanel {

    /**
     * Creates the System Shortcuts panel.
     */
    public SystemShortcuts() {
        // Sets the panel's layout to null.
        setLayout(null);

        // Creates the title label.
        JLabel titleLabel = createLabel("System Shortcuts",
                new Rectangle(20, 15, 200, 30),
                new Font("Arial", Font.BOLD, 18)
        );
        add(titleLabel);

        // Adds the components to the panel.
        setupAppsFeatures();
        setupStartupApps();
        setupWindowsUpdate();
        setupWindowsSecurity();
        setupDisplaySettings();
        setupStorageSettings();
        setupSoundSettings();
        setupOptionalFeatures();
        setupTaskManager();
        setupDeviceManager();
        setupDiskCleanup();
        setupMSConfig();

        // Sets the panel's border.
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    /**
     * Sets up the Apps & Features section.
     */
    public void setupAppsFeatures() {
        int baseHeight = 55;
        int baseWidth = 20;

        // Adds a title label for Apps & Features.
        JLabel title = createLabel("Apps & Features",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Apps & Features.
        JLabel description = createLabel("Manage installed programs.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Apps & Features.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/AppsFeatures.png"), this);

        // Adds a button to launch Apps & Features.
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
        JLabel title = createLabel("Startup Apps",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Startup Apps.
        JLabel description = createLabel("Manage startup programs.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Startup Apps.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/StartupApps.png"), this);

        // Adds a button to launch Startup Apps.
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
        JLabel title = createLabel("Windows Update",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Windows Update.
        JLabel description = createLabel("Check for updates.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Windows Update.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/WindowsUpdate.png"), this);

        // Adds a button to launch Windows Update.
        JButton appButton = createActionButton("Open Windows Update",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        if (RepairKit.isSafeMode()) {
                            playSound("win.sound.hand");
                            JOptionPane.showMessageDialog(null,
                                    "Windows Update cannot be run in Safe Mode."
                                            + "\nPlease restart your system in normal mode to use this feature."
                                    , "Safe Mode Detected", JOptionPane.ERROR_MESSAGE);
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
        JLabel title = createLabel("Windows Security",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Windows Security.
        JLabel description = createLabel("Manage security settings.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Windows Security.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/WindowsSecurity.png"), this);

        // Adds a button to launch Windows Security.
        JButton appButton = createActionButton("Open Windows Security",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        if (RepairKit.isSafeMode()) {
                            playSound("win.sound.hand");
                            JOptionPane.showMessageDialog(null,
                                    "Windows Defender cannot be run in Safe Mode."
                                            + "\nPlease restart your system in normal mode to use this feature."
                                    , "Safe Mode Detected", JOptionPane.ERROR_MESSAGE);
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
        JLabel title = createLabel("Display Settings",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Display Settings.
        JLabel description = createLabel("Manage display settings.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Display Settings.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/DisplaySettings.png"), this);

        // Adds a button to launch Display Settings.
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
        JLabel title = createLabel("Storage Settings",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Storage Settings.
        JLabel description = createLabel("Manage storage settings.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Storage Settings.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/StorageSettings.png"), this);

        // Adds a button to launch Storage Settings.
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
        JLabel title = createLabel("Sound Settings",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Sound Settings.
        JLabel description = createLabel("Manage sound settings.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Sound Settings.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/SoundSettings.png"), this);

        // Adds a button to launch Sound Settings.
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
        JLabel title = createLabel("Optional Features",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Optional Features.
        JLabel description = createLabel("Manage optional features.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Sound Settings.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/AppsFeatures.png"), this);

        // Adds a button to launch Sound Settings.
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
        JLabel title = createLabel("Task Manager",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Task Manager.
        JLabel description = createLabel("Manage running processes.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Task Manager.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/TaskManager.png"), this);

        // Adds a button to launch Task Manager.
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
        JLabel title = createLabel("Device Manager",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Device Manager.
        JLabel description = createLabel("Manage hardware devices.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Device Manager.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/DeviceManager.png"), this);

        // Adds a button to launch Device Manager.
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
        JLabel title = createLabel("Disk Cleanup",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Disk Cleanup.
        JLabel description = createLabel("Clean up disk space.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Disk Cleanup.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/DiskCleanup.png"), this);

        // Adds a button to launch Disk Cleanup.
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
        JLabel title = createLabel("MSConfig",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for MSConfig.
        JLabel description = createLabel("System configuration utility.",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for MSConfig.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/MSConfig.png"), this);

        // Adds a button to launch MSConfig.
        JButton appButton = createActionButton("Open MSConfig",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> runCommand("msconfig", true)
        );
        add(appButton);
    }
}
