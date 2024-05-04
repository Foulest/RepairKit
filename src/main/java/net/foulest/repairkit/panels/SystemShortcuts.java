package net.foulest.repairkit.panels;

import lombok.extern.java.Log;
import net.foulest.repairkit.RepairKit;
import net.foulest.repairkit.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static net.foulest.repairkit.util.CommandUtil.runCommand;
import static net.foulest.repairkit.util.SoundUtil.playSound;
import static net.foulest.repairkit.util.SwingUtil.createActionButton;

@Log
public class SystemShortcuts extends JPanel {

    /**
     * Creates the System Shortcuts panel.
     */
    public SystemShortcuts() {
        setLayout(null);

        // Creates the title label.
        JLabel titleLabel = new JLabel("System Shortcuts");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(20, 15, 200, 30);
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
        JLabel title = new JLabel("Apps & Features");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for Apps & Features.
        JLabel description = new JLabel("Manage installed programs.");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for Apps & Features.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/AppsFeatures.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch Apps & Features.
        JButton appButton = createActionButton("Open Apps & Features", () -> {
            if (!RepairKit.isOutdatedOperatingSystem()) {
                runCommand("start ms-settings:appsfeatures", true);
            } else {
                runCommand("appwiz.cpl", true);
            }
        });
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    /**
     * Sets up the Startup Apps section.
     */
    public void setupStartupApps() {
        int baseHeight = 150;
        int baseWidth = 20;

        // Adds a title label for Startup Apps.
        JLabel title = new JLabel("Startup Apps");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for Startup Apps.
        JLabel description = new JLabel("Manage startup programs.");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for Startup Apps.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/StartupApps.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch Startup Apps.
        JButton appButton = createActionButton("Open Startup Apps", () -> {
            if (!RepairKit.isOutdatedOperatingSystem()) {
                runCommand("start ms-settings:startupapps", true);
            } else {
                runCommand("msconfig", true);
            }
        });
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    /**
     * Sets up the Windows Update section.
     */
    public void setupWindowsUpdate() {
        int baseHeight = 245;
        int baseWidth = 20;

        // Adds a title label for Windows Update.
        JLabel title = new JLabel("Windows Update");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for Windows Update.
        JLabel description = new JLabel("Check for updates.");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for Windows Update.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/WindowsUpdate.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch Windows Update.
        JButton appButton = createActionButton("Open Windows Update", () -> {
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
        });
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    /**
     * Sets up the Windows Security section.
     */
    public void setupWindowsSecurity() {
        int baseHeight = 340;
        int baseWidth = 20;

        // Adds a title label for Windows Security.
        JLabel title = new JLabel("Windows Security");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for Windows Security.
        JLabel description = new JLabel("Manage security settings.");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for Windows Security.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/WindowsSecurity.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch Windows Security.
        JButton appButton = createActionButton("Open Windows Security", () -> {
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
        });
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    /**
     * Sets up the Display Settings section.
     */
    public void setupDisplaySettings() {
        int baseHeight = 55;
        int baseWidth = 250;

        // Adds a title label for Display Settings.
        JLabel title = new JLabel("Display Settings");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for Display Settings.
        JLabel description = new JLabel("Manage display settings.");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for Display Settings.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/DisplaySettings.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch Display Settings.
        JButton appButton = createActionButton("Open Display Settings",
                () -> runCommand("start desk.cpl", true));
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    /**
     * Sets up the Storage Settings section.
     */
    public void setupStorageSettings() {
        int baseHeight = 150;
        int baseWidth = 250;

        // Adds a title label for Storage Settings.
        JLabel title = new JLabel("Storage Settings");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for Storage Settings.
        JLabel description = new JLabel("Manage storage settings.");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for Storage Settings.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/StorageSettings.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch Storage Settings.
        JButton appButton = createActionButton("Open Storage Settings", () -> {
            if (RepairKit.isOutdatedOperatingSystem()) {
                runCommand("start explorer /select,\"This PC\"", true);
            } else {
                runCommand("start ms-settings:storagesense", true);
            }
        });
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    /**
     * Sets up the Sound Settings section.
     */
    public void setupSoundSettings() {
        int baseHeight = 245;
        int baseWidth = 250;

        // Adds a title label for Sound Settings.
        JLabel title = new JLabel("Sound Settings");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for Sound Settings.
        JLabel description = new JLabel("Manage sound settings.");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for Sound Settings.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/SoundSettings.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch Sound Settings.
        JButton appButton = createActionButton("Open Sound Settings", () -> {
            if (RepairKit.isOutdatedOperatingSystem()) {
                runCommand("start control mmsys.cpl sounds", true);
            } else {
                runCommand("start ms-settings:sound", true);
            }
        });
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    /**
     * Sets up the Optional Features section.
     */
    public void setupOptionalFeatures() {
        int baseHeight = 340;
        int baseWidth = 250;

        // Adds a title label for Optional Features.
        JLabel title = new JLabel("Optional Features");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for Optional Features.
        JLabel description = new JLabel("Manage optional features.");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for Sound Settings.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/AppsFeatures.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch Sound Settings.
        JButton appButton = createActionButton("Open Optional Features", () -> {
            if (RepairKit.isOutdatedOperatingSystem()) {
                runCommand("start OptionalFeatures", true);
            } else {
                runCommand("start ms-settings:optionalfeatures", true);
            }
        });
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    /**
     * Sets up the Task Manager section.
     */
    public void setupTaskManager() {
        int baseHeight = 55;
        int baseWidth = 480;

        // Adds a title label for Task Manager.
        JLabel title = new JLabel("Task Manager");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for Task Manager.
        JLabel description = new JLabel("Manage running processes.");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for Task Manager.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/TaskManager.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch Task Manager.
        JButton appButton = createActionButton("Open Task Manager",
                () -> runCommand("taskmgr", true));
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    /**
     * Sets up the Device Manager section.
     */
    public void setupDeviceManager() {
        int baseHeight = 150;
        int baseWidth = 480;

        // Adds a title label for Device Manager.
        JLabel title = new JLabel("Device Manager");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for Device Manager.
        JLabel description = new JLabel("Manage hardware devices.");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for Device Manager.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/DeviceManager.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch Device Manager.
        JButton appButton = createActionButton("Open Device Manager",
                () -> runCommand("devmgmt.msc", true));
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    /**
     * Sets up the Disk Cleanup section.
     */
    public void setupDiskCleanup() {
        int baseHeight = 245;
        int baseWidth = 480;

        // Adds a title label for Disk Cleanup.
        JLabel title = new JLabel("Disk Cleanup");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for Disk Cleanup.
        JLabel description = new JLabel("Clean up disk space.");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for Disk Cleanup.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/DiskCleanup.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch Disk Cleanup.
        JButton appButton = createActionButton("Open Disk Cleanup",
                () -> runCommand("cleanmgr", true));
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    /**
     * Sets up the MSConfig section.
     */
    public void setupMSConfig() {
        int baseHeight = 340;
        int baseWidth = 480;

        // Adds a title label for MSConfig.
        JLabel title = new JLabel("MSConfig");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for MSConfig.
        JLabel description = new JLabel("System configuration utility.");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for MSConfig.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/MSConfig.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch MSConfig.
        JButton appButton = createActionButton("Open MSConfig",
                () -> runCommand("msconfig", true));
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }
}
