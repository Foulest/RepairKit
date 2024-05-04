package net.foulest.repairkit.panels;

import lombok.extern.java.Log;
import net.foulest.repairkit.RepairKit;
import net.foulest.repairkit.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static net.foulest.repairkit.util.CommandUtil.getCommandOutput;
import static net.foulest.repairkit.util.CommandUtil.runCommand;
import static net.foulest.repairkit.util.FileUtil.*;
import static net.foulest.repairkit.util.ProcessUtil.isProcessRunning;
import static net.foulest.repairkit.util.SoundUtil.playSound;
import static net.foulest.repairkit.util.SwingUtil.*;

@Log
public class UsefulPrograms extends JPanel {

    public UsefulPrograms() {
        setLayout(null);

        // Creates the title label.
        JLabel titleLabel = new JLabel("Useful Programs");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(20, 15, 200, 30);
        add(titleLabel);

        // Adds the components to the panel.
        setupCPUZ();
        setupHWMonitor();
        setupAutoruns();
        setupProcessExplorer();
        setupTreeSize();
        setupEverything();
        setupFanControl();
        setupNVCleanstall();
        setupEmsisoftScan();
        setupSophosScan();
        setupUBlockOrigin();
        setupTrafficLight();

        // Sets the panel's border.
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    public void setupCPUZ() {
        int baseHeight = 55;
        int baseWidth = 20;

        // Adds a title label for CPU-Z.
        JLabel title = new JLabel("CPU-Z");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for CPU-Z.
        JLabel description = new JLabel("Version: 2.09.0");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for CPU-Z.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/CPU-Z.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch CPU-Z.
        JButton appButton = createAppButton("Launch CPU-Z", "Displays system hardware information.",
                "CPU-Z.zip", "CPU-Z.exe", true, tempDirectory.getPath());
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    public void setupHWMonitor() {
        int baseHeight = 150;
        int baseWidth = 20;

        // Adds a title label for the HWMonitor.
        JLabel title = new JLabel("HWMonitor");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for HWMonitor.
        JLabel description = new JLabel("Version: 1.53.0");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for HWMonitor.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/HWMonitor.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch HWMonitor.
        JButton appButton = createAppButton("Launch HWMonitor", "Displays hardware voltages & temperatures.",
                "HWMonitor.zip", "HWMonitor.exe", true, tempDirectory.getPath());
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    public void setupAutoruns() {
        int baseHeight = 245;
        int baseWidth = 20;

        // Adds a title label for Autoruns.
        JLabel title = new JLabel("Autoruns");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for Autoruns.
        JLabel description = new JLabel("Version: 14.11");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for Autoruns.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/Autoruns.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch HWMonitor.
        JButton appButton = createAppButton("Launch Autoruns", "Displays startup items.",
                "Autoruns.zip", "Autoruns.exe",
                true, tempDirectory.getPath());
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    public void setupProcessExplorer() {
        int baseHeight = 340;
        int baseWidth = 20;

        // Adds a title label for Process Explorer.
        JLabel title = new JLabel("Process Explorer");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for Process Explorer.
        JLabel description = new JLabel("Version: 17.05");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for Process Explorer.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/ProcessExplorer.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch Process Explorer.
        JButton appButton = createAppButton("Launch Process Explorer", "Displays system processes.",
                "ProcessExplorer.zip", "ProcessExplorer.exe",
                true, tempDirectory.getPath());
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    public void setupTreeSize() {
        int baseHeight = 55;
        int baseWidth = 250;

        // Adds a title label for TreeSize.
        JLabel title = new JLabel("TreeSize");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for TreeSize.
        JLabel description = new JLabel("Version: 4.6.3.520");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for TreeSize.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/TreeSize.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch TreeSize.
        JButton appButton;
        if (RepairKit.isOutdatedOperatingSystem()) {
            appButton = createActionButton("Launch TreeSize",
                    "Displays system files organized by size.", () -> {
                        playSound("win.sound.hand");
                        JOptionPane.showMessageDialog(null,
                                "TreeSize cannot be run on outdated operating systems."
                                        + "\nPlease upgrade to Windows 10 or 11 to use this feature."
                                , "Outdated Operating System", JOptionPane.ERROR_MESSAGE);
                    });
        } else {
            appButton = createAppButton("Launch TreeSize", "Displays system files organized by size.",
                    "TreeSize.zip", "TreeSize.exe", true, tempDirectory.getPath());
        }
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    public void setupEverything() {
        int baseHeight = 150;
        int baseWidth = 250;

        // Adds a title label for Everything.
        JLabel title = new JLabel("Everything");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for Everything.
        JLabel description = new JLabel("Version: 1.4.1.1024");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for Everything.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/Everything.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch Everything.
        JButton appButton = createAppButton("Launch Everything", "Displays all files on your system.",
                "Everything.zip", "Everything.exe", true, tempDirectory.getPath());
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    public void setupFanControl() {
        int baseHeight = 245;
        int baseWidth = 250;

        // Adds a title label for FanControl.
        JLabel title = new JLabel("FanControl");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for FanControl.
        JLabel description = new JLabel("Version: Auto-Updated");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for FanControl.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/FanControl.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch FanControl.
        JButton appButton = createActionButton("Launch FanControl",
                "Allows control over system fans.", () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        playSound("win.sound.hand");
                        JOptionPane.showMessageDialog(null,
                                "FanControl cannot be run on outdated operating systems."
                                        + "\nPlease upgrade to Windows 10 or 11 to use this feature."
                                , "Outdated Operating System", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (RepairKit.isSafeMode()) {
                        playSound("win.sound.hand");
                        JOptionPane.showMessageDialog(null,
                                "FanControl cannot be run in Safe Mode."
                                        + "\nPlease restart your system in normal mode to use this feature."
                                , "Safe Mode Detected", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        String fanControlPath = getCommandOutput("wmic process where name=\"FanControl.exe\""
                                + " get ExecutablePath /value", false, false).toString();
                        fanControlPath = fanControlPath.replace("[, , , , ExecutablePath=", "");
                        fanControlPath = fanControlPath.replace(", , , , , , , ]", "");

                        // If FanControl is not running, extract and launch it.
                        // Otherwise, launch the existing instance.
                        if (!isProcessRunning("FanControl.exe")) {
                            launchApplication("FanControl.zip", "\\FanControl.exe",
                                    true, System.getenv("APPDATA") + "\\FanControl");
                        } else {
                            runCommand("start \"\" \"" + fanControlPath + "\"", false);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    public void setupNVCleanstall() {
        int baseHeight = 340;
        int baseWidth = 250;

        // Adds a title label for NVCleanstall.
        JLabel title = new JLabel("NVCleanstall");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for NVCleanstall.
        JLabel description = new JLabel("Version: 1.16.0");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for NVCleanstall.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/NVCleanstall.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch NVCleanstall.
        JButton appButton = createActionButton("Launch NVCleanstall",
                "A lightweight NVIDIA graphics card driver updater.", () -> {
                    if (RepairKit.isOutdatedOperatingSystem()) {
                        playSound("win.sound.hand");
                        JOptionPane.showMessageDialog(null,
                                "NVCleanstall cannot be run on outdated operating systems."
                                        + "\nPlease upgrade to Windows 10 or 11 to use this feature."
                                , "Outdated Operating System", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try (InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("bin/NVCleanstall.zip")) {
                        saveFile(Objects.requireNonNull(input), "NVCleanstall.zip", true);
                        unzipFile(tempDirectory + "\\NVCleanstall.zip", tempDirectory.getPath());
                        runCommand(tempDirectory + "\\NVCleanstall.exe", true);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    public void setupEmsisoftScan() {
        int baseHeight = 55;
        int baseWidth = 480;

        // Adds a title label for Emsisoft Scan.
        JLabel title = new JLabel("Emsisoft Scan");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for Emsisoft Scan.
        JLabel description = new JLabel("Version: Auto-Updated");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for Emsisoft Scan.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/Emsisoft.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch Emsisoft Scan.
        JButton appButton;
        if (RepairKit.isOutdatedOperatingSystem()) {
            appButton = createActionButton("Launch Emsisoft Scan",
                    "Scans for malware with Emsisoft.", () -> {
                        playSound("win.sound.hand");
                        JOptionPane.showMessageDialog(null,
                                "Emsisoft Scan cannot be run on outdated operating systems."
                                        + "\nPlease upgrade to Windows 10 or 11 to use this feature."
                                , "Outdated Operating System", JOptionPane.ERROR_MESSAGE);
                    });
        } else {
            appButton = createAppButton("Launch Emsisoft Scan", "Scans for malware with Emsisoft.",
                    "Emsisoft.zip", "Emsisoft.exe", true, tempDirectory.getPath());
        }
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    public void setupSophosScan() {
        int baseHeight = 150;
        int baseWidth = 480;

        // Adds a title label for Sophos Scan.
        JLabel title = new JLabel("Sophos Scan");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for Sophos Scan.
        JLabel description = new JLabel("Version: Auto-Updated");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for Sophos Scan.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/Sophos.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch Sophos Scan.
        JButton appButton = createActionButton("Launch Sophos Scan",
                "Scans for malware with Sophos.", () -> {
                    try (InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("bin/Sophos.zip")) {
                        saveFile(Objects.requireNonNull(input), "Sophos.zip", true);
                        unzipFile(tempDirectory + "\\Sophos.zip", tempDirectory.getPath());
                        runCommand("start \"\" \"" + tempDirectory + "\\Sophos.exe\"", true);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    public void setupUBlockOrigin() {
        int baseHeight = 245;
        int baseWidth = 480;

        // Adds a title label for uBlock Origin.
        JLabel title = new JLabel("uBlock Origin");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for uBlock Origin.
        JLabel description = new JLabel("Version: Auto-Updated");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for uBlock Origin.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/uBlockOrigin.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch uBlock Origin.
        JButton appButton = createActionButton("Launch uBlock Origin",
                "Link to the ad-blocker browser extension.",
                () -> runCommand("start https://ublockorigin.com", true));
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }

    public void setupTrafficLight() {
        int baseHeight = 340;
        int baseWidth = 480;

        // Adds a title label for TrafficLight.
        JLabel title = new JLabel("TrafficLight");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(baseWidth + 43, baseHeight, 200, 30);
        add(title);

        // Adds a description label for TrafficLight.
        JLabel description = new JLabel("Version: Auto-Updated");
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBounds(baseWidth + 43, baseHeight + 20, 200, 30);
        add(description);

        // Adds an icon for TrafficLight.
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(RepairKit.class.getClassLoader().getResource("icons/TrafficLight.png")));
        SwingUtil.setupAppIcon(baseHeight, baseWidth, imageIcon, this);

        // Adds a button to launch TrafficLight.
        JButton appButton = createActionButton("Launch TrafficLight",
                "Link to BitDefender's TrafficLight extension.",
                () -> runCommand("start https://bitdefender.com/solutions/trafficlight.html", true));
        appButton.setBackground(new Color(200, 200, 200));
        appButton.setBounds(baseWidth, baseHeight + 50, 200, 30);
        add(appButton);
    }
}
