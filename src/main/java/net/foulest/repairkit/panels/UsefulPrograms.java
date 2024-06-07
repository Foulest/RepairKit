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

    /**
     * Creates the Useful Programs panel.
     */
    public UsefulPrograms() {
        // Sets the panel's layout to null.
        setLayout(null);

        // Creates the title label.
        JLabel titleLabel = createLabel("Useful Programs",
                new Rectangle(20, 15, 200, 30),
                new Font("Arial", Font.BOLD, 18)
        );
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

    /**
     * Sets up the CPU-Z section.
     */
    public void setupCPUZ() {
        int baseHeight = 55;
        int baseWidth = 20;

        // Adds a title label for CPU-Z.
        JLabel title = createLabel("CPU-Z",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for CPU-Z.
        JLabel description = createLabel("Version: 2.09.0",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for CPU-Z.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/CPU-Z.png"), this);

        // Adds a button to launch CPU-Z.
        JButton appButton = createAppButton("Launch CPU-Z",
                "Displays system hardware information.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                "CPU-Z.zip",
                "CPU-Z.exe",
                true, tempDirectory.getPath()
        );
        add(appButton);
    }

    /**
     * Sets up the HWMonitor section.
     */
    public void setupHWMonitor() {
        int baseHeight = 150;
        int baseWidth = 20;

        // Adds a title label for the HWMonitor.
        JLabel title = createLabel("HWMonitor",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for HWMonitor.
        JLabel description = createLabel("Version: 1.53.0",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for HWMonitor.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/HWMonitor.png"), this);

        // Adds a button to launch HWMonitor.
        JButton appButton = createAppButton("Launch HWMonitor",
                "Displays hardware voltages & temperatures.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                "HWMonitor.zip",
                "HWMonitor.exe",
                true, tempDirectory.getPath()
        );
        add(appButton);
    }

    /**
     * Sets up the Autoruns section.
     */
    public void setupAutoruns() {
        int baseHeight = 245;
        int baseWidth = 20;

        // Adds a title label for Autoruns.
        JLabel title = createLabel("Autoruns",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Autoruns.
        JLabel description = createLabel("Version: 14.11",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Autoruns.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/Autoruns.png"), this);

        // Adds a button to launch HWMonitor.
        JButton appButton = createAppButton("Launch Autoruns",
                "Displays startup items.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                "Autoruns.zip",
                "Autoruns.exe",
                true, tempDirectory.getPath()
        );
        add(appButton);
    }

    /**
     * Sets up the Process Explorer section.
     */
    public void setupProcessExplorer() {
        int baseHeight = 340;
        int baseWidth = 20;

        // Adds a title label for Process Explorer.
        JLabel title = createLabel("Process Explorer",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Process Explorer.
        JLabel description = createLabel("Version: 17.06",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Process Explorer.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/ProcessExplorer.png"), this);

        // Adds a button to launch Process Explorer.
        JButton appButton = createAppButton("Launch Process Explorer",
                "Displays system processes.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                "ProcessExplorer.zip",
                "ProcessExplorer.exe",
                true, tempDirectory.getPath()
        );
        add(appButton);
    }

    /**
     * Sets up the TreeSize section.
     */
    public void setupTreeSize() {
        int baseHeight = 55;
        int baseWidth = 250;

        // Adds a title label for TreeSize.
        JLabel title = createLabel("TreeSize",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for TreeSize.
        JLabel description = createLabel("Version: 4.6.3.520",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for TreeSize.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/TreeSize.png"), this);

        // Adds a button to launch TreeSize.
        JButton appButton;
        if (RepairKit.isOutdatedOperatingSystem()) {
            appButton = createActionButton("Launch TreeSize",
                    "Displays system files organized by size.",
                    new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                    new Color(200, 200, 200), () -> {
                        playSound("win.sound.hand");
                        JOptionPane.showMessageDialog(null,
                                "TreeSize cannot be run on outdated operating systems."
                                        + "\nPlease upgrade to Windows 10 or 11 to use this feature."
                                , "Outdated Operating System", JOptionPane.ERROR_MESSAGE);
                    }
            );
        } else {
            appButton = createAppButton("Launch TreeSize",
                    "Displays system files organized by size.",
                    new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                    new Color(200, 200, 200),
                    "TreeSize.zip",
                    "TreeSize.exe",
                    true, tempDirectory.getPath()
            );
        }
        add(appButton);
    }

    /**
     * Sets up the Everything section.
     */
    public void setupEverything() {
        int baseHeight = 150;
        int baseWidth = 250;

        // Adds a title label for Everything.
        JLabel title = createLabel("Everything",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Everything.
        JLabel description = createLabel("Version: 1.4.1.1024",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Everything.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/Everything.png"), this);

        // Adds a button to launch Everything.
        JButton appButton = createAppButton("Launch Everything",
                "Displays all files on your system.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                "Everything.zip",
                "Everything.exe",
                true, tempDirectory.getPath()
        );
        add(appButton);
    }

    /**
     * Sets up the FanControl section.
     */
    public void setupFanControl() {
        int baseHeight = 245;
        int baseWidth = 250;

        // Adds a title label for FanControl.
        JLabel title = createLabel("FanControl",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for FanControl.
        JLabel description = createLabel("Version: Auto-Updated",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for FanControl.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/FanControl.png"), this);

        // Adds a button to launch FanControl.
        JButton appButton = createActionButton("Launch FanControl",
                "Allows control over system fans.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
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
                }
        );
        add(appButton);
    }

    /**
     * Sets up the NVCleanstall section.
     */
    public void setupNVCleanstall() {
        int baseHeight = 340;
        int baseWidth = 250;

        // Adds a title label for NVCleanstall.
        JLabel title = createLabel("NVCleanstall",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for NVCleanstall.
        JLabel description = createLabel("Version: 1.16.0",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for NVCleanstall.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/NVCleanstall.png"), this);

        // Adds a button to launch NVCleanstall.
        JButton appButton = createActionButton("Launch NVCleanstall",
                "A lightweight NVIDIA graphics card driver updater.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
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
                }
        );
        add(appButton);
    }

    /**
     * Sets up the Emsisoft Scan section.
     */
    public void setupEmsisoftScan() {
        int baseHeight = 55;
        int baseWidth = 480;

        // Adds a title label for Emsisoft Scan.
        JLabel title = createLabel("Emsisoft Scan",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Emsisoft Scan.
        JLabel description = createLabel("Version: Auto-Updated",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Emsisoft Scan.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/Emsisoft.png"), this);

        // Adds a button to launch Emsisoft Scan.
        JButton appButton;
        if (RepairKit.isOutdatedOperatingSystem()) {
            appButton = createActionButton("Launch Emsisoft Scan",
                    "Scans for malware with Emsisoft.",
                    new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                    new Color(200, 200, 200), () -> {
                        playSound("win.sound.hand");
                        JOptionPane.showMessageDialog(null,
                                "Emsisoft Scan cannot be run on outdated operating systems."
                                        + "\nPlease upgrade to Windows 10 or 11 to use this feature."
                                , "Outdated Operating System", JOptionPane.ERROR_MESSAGE);
                    }
            );
        } else {
            appButton = createAppButton("Launch Emsisoft Scan",
                    "Scans for malware with Emsisoft.",
                    new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                    new Color(200, 200, 200),
                    "Emsisoft.zip",
                    "Emsisoft.exe",
                    true, tempDirectory.getPath()
            );
        }
        add(appButton);
    }

    /**
     * Sets up the Sophos Scan section.
     */
    public void setupSophosScan() {
        int baseHeight = 150;
        int baseWidth = 480;

        // Adds a title label for Sophos Scan.
        JLabel title = createLabel("Sophos Scan",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Sophos Scan.
        JLabel description = createLabel("Version: Auto-Updated",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Sophos Scan.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/Sophos.png"), this);

        // Adds a button to launch Sophos Scan.
        JButton appButton = createActionButton("Launch Sophos Scan",
                "Scans for malware with Sophos.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    try (InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("bin/Sophos.zip")) {
                        saveFile(Objects.requireNonNull(input), "Sophos.zip", true);
                        unzipFile(tempDirectory + "\\Sophos.zip", tempDirectory.getPath());
                        runCommand("start \"\" \"" + tempDirectory + "\\Sophos.exe\"", true);
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
    public void setupUBlockOrigin() {
        int baseHeight = 245;
        int baseWidth = 480;

        // Adds a title label for uBlock Origin.
        JLabel title = createLabel("uBlock Origin",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for uBlock Origin.
        JLabel description = createLabel("Version: Auto-Updated",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for uBlock Origin.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/uBlockOrigin.png"), this);

        // Adds a button to launch uBlock Origin.
        JButton appButton = createActionButton("Launch uBlock Origin",
                "Link to the ad-blocker browser extension.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> runCommand("start https://ublockorigin.com", true)
        );
        add(appButton);
    }

    /**
     * Sets up the TrafficLight section.
     */
    public void setupTrafficLight() {
        int baseHeight = 340;
        int baseWidth = 480;

        // Adds a title label for TrafficLight.
        JLabel title = createLabel("TrafficLight",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font("Arial", Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for TrafficLight.
        JLabel description = createLabel("Version: Auto-Updated",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font("Arial", Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for TrafficLight.
        setupAppIcon(baseHeight, baseWidth, getImageIcon("icons/TrafficLight.png"), this);

        // Adds a button to launch TrafficLight.
        JButton appButton = createActionButton("Launch TrafficLight",
                "Link to BitDefender's TrafficLight extension.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> runCommand("start https://bitdefender.com/solutions/trafficlight.html", true)
        );
        add(appButton);
    }
}
