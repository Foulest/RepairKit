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
 * Useful Programs panel (Page 2).
 *
 * @author Foulest
 */
public class UsefulPrograms2 extends JPanel {

    /**
     * Creates the Useful Programs (Page 2) panel.
     */
    public UsefulPrograms2() {
        // Sets the panel's layout to null.
        DebugUtil.debug("Setting the Useful Programs (Page 2) panel layout to null...");
        setLayout(null);

        // Creates the title label.
        DebugUtil.debug("Creating the Useful Programs (Page 2) title label...");
        JLabel titleLabel = SwingUtil.createLabel("Useful Programs",
                new Rectangle(20, 15, 230, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 18)
        );
        add(titleLabel);

        // Creates the page label.
        DebugUtil.debug("Creating the Useful Programs (Page 2) page label...");
        JLabel pageLabel = SwingUtil.createLabel("(Page 2/2)",
                new Rectangle(172, 15, 69, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 14)
        );
        add(pageLabel);

        // Adds the components to the panel.
        DebugUtil.debug("Adding components to the Useful Programs (Page 2) panel...");

        // Creates tasks for the executor.
        List<Runnable> tasks = Arrays.asList(
                this::setupWingetAutoUpdate,
                this::setupNVCleanstall,
                this::setupDDU,
                this::setup7Zip,

                this::setupBitwarden,
                this::setupSophosHome,
                this::setupUBlockOrigin,
                this::setupTrafficLight,

                this::setupNotepadPlusPlus,
                this::setupTwinkleTray,
                this::setupFanControl
        );

        // Executes tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);

        // Sets the panel's border.
        DebugUtil.debug("Setting the Useful Programs (Page 2) panel border...");
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Adds a label for the previous page button.
        JLabel previousPage = SwingUtil.createLabel("<",
                new Rectangle(250, 21, 22, 22),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 25)
        );
        previousPage.setForeground(Color.DARK_GRAY);
        add(previousPage);

        // Adds a button under the previous page label.
        JButton previousPageButton = SwingUtil.createPanelButton("",
                "Useful Programs (Page 1)",
                new Rectangle(246, 20, 22, 23)
        );
        previousPageButton.setOpaque(false);
        previousPageButton.setContentAreaFilled(false);
        add(previousPageButton);

        // Adds a label for the next page button.
        JLabel nextPage = SwingUtil.createLabel(">",
                new Rectangle(270, 21, 22, 22),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 25)
        );
        nextPage.setForeground(Color.LIGHT_GRAY);
        add(nextPage);
    }

    /**
     * Sets up the Winget-AutoUpdate section.
     */
    private void setupWingetAutoUpdate() {
        int baseHeight = 55;
        int baseWidth = 20;

        // Adds a title label for Winget-AutoUpdate.
        DebugUtil.debug("Creating the Winget-AutoUpdate title label...");
        JLabel title = SwingUtil.createLabel("Winget-AutoUpdate",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Winget-AutoUpdate.
        DebugUtil.debug("Creating the Winget-AutoUpdate description label...");
        JLabel description = SwingUtil.createLabel(ConstantUtil.VERSION_AUTO_UPDATED,
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for TrafficLight.
        DebugUtil.debug("Setting up the Winget-AutoUpdate icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Winget-AutoUpdate.png"), this);

        // Adds a button to launch Winget-AutoUpdate.
        DebugUtil.debug("Creating the Winget-AutoUpdate launch button...");
        JButton appButton = SwingUtil.createActionButton("Launch Winget-AutoUpdate",
                "Automatically updates programs using Winget.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Stops if Winget-AutoUpdate is currently running.
                    if (ProcessUtil.isProcessRunning("wscript.exe")
                            || ProcessUtil.isProcessRunning("winget.exe")
                            || ProcessUtil.isProcessRunning("powershell.exe")) {
                        SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                        JOptionPane.showMessageDialog(null, """
                                        Winget-AutoUpdate cannot be launched. It might be already running.
                                        
                                        Please wait for the following processes to finish:
                                        - wscript.exe
                                        - winget.exe
                                        - powershell.exe""",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Enables Windows Script Host for Winget-AutoUpdate.
                    DebugUtil.debug("Enabling Windows Script Host for Winget-AutoUpdate...");
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows Script Host\\Settings", "Enabled", 1);

                    try (InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("bin/WAU.7z")) {
                        // Saves and unzips the Winget-AutoUpdate files.
                        DebugUtil.debug("Extracting Winget-AutoUpdate files...");
                        FileUtil.saveFile(Objects.requireNonNull(input), FileUtil.tempDirectory + "\\WAU.7z", true);
                        FileUtil.unzipFile(FileUtil.tempDirectory + "\\WAU.7z", FileUtil.tempDirectory.getPath());

                        // Prompt the user if they want to check for updates on logon, or just once.
                        int option = JOptionPane.showOptionDialog(null,
                                "Would you like Winget-AutoUpdate to automatically update your programs?",
                                "Winget-AutoUpdate", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                new String[]{"Yes", "No"}, "Yes");

                        // Silently installs Winget-AutoUpdate.
                        DebugUtil.debug("Silently installing Winget-AutoUpdate...");
                        CommandUtil.runCommand("PowerShell -ExecutionPolicy Bypass \""
                                + FileUtil.tempDirectory + "\\Winget-AutoUpdate-Install.ps1\" -Silent"
                                + (option == JOptionPane.YES_OPTION
                                ? " -UpdatesAtLogon -UpdatesInterval Daily"
                                : " -UpdatesInterval Never")
                                + " -NotificationLevel Full"
                                + " -StartMenuShortcut -DoNotUpdate", false);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    // Launches Winget-AutoUpdate.
                    DebugUtil.debug("Running update check with Winget-AutoUpdate...");
                    CommandUtil.runCommand("C:\\Windows\\system32\\wscript.exe"
                            + " \"C:\\ProgramData\\Winget-AutoUpdate\\Invisible.vbs\" \"powershell.exe"
                            + " -NoProfile -ExecutionPolicy Bypass -File"
                            + " \"\"\"C:\\ProgramData\\Winget-AutoUpdate\\user-run.ps1\"\"", false);
                }
        );
        add(appButton);
    }

    /**
     * Sets up the NVCleanstall section.
     */
    private void setupNVCleanstall() {
        int baseHeight = 150;
        int baseWidth = 20;

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

                    // Unzips and launches NVCleanstall.
                    SwingUtil.launchApplication("NVCleanstall.7z", "\\NVCleanstall.exe",
                            true, FileUtil.tempDirectory.getPath());
                }
        );
        add(appButton);
    }

    /**
     * Sets up the DDU section.
     */
    private void setupDDU() {
        int baseHeight = 245;
        int baseWidth = 20;

        // Adds a title label for DDU.
        DebugUtil.debug("Creating the DDU title label...");
        JLabel title = SwingUtil.createLabel("DDU",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for DDU.
        DebugUtil.debug("Creating the DDU description label...");
        JLabel description = SwingUtil.createLabel("Version: 18.0.8.1",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for DDU.
        DebugUtil.debug("Setting up the DDU icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/DDU.png"), this);

        // Adds a button to launch DDU.
        DebugUtil.debug("Creating the DDU launch button...");
        JButton appButton = SwingUtil.createActionButton("Launch DDU",
                "Display Driver Uninstaller for NVIDIA, AMD, and Intel.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200), () -> {
                    // Unzips and launches DDU.
                    SwingUtil.launchApplication("DDU.7z", "\\DDU.exe",
                            true, FileUtil.tempDirectory.getPath());
                }
        );
        add(appButton);
    }

    /**
     * Sets up the 7-Zip section.
     */
    private void setup7Zip() {
        int baseHeight = 340;
        int baseWidth = 20;

        // Adds a title label for 7-Zip.
        DebugUtil.debug("Creating the 7-Zip title label...");
        JLabel title = SwingUtil.createLabel("7-Zip",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for 7-Zip.
        DebugUtil.debug("Creating the 7-Zip description label...");
        JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for 7-Zip.
        DebugUtil.debug("Setting up the 7-Zip icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/7-Zip.png"), this);

        // Adds a button to launch 7-Zip.
        DebugUtil.debug("Creating the 7-Zip launch button...");
        JButton appButton = SwingUtil.createActionButton("Visit 7-Zip",
                "Free and open-source file archiver.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://7-zip.org/download.html", true)
        );
        add(appButton);
    }

    /**
     * Sets up the Bitwarden section.
     */
    private void setupBitwarden() {
        int baseHeight = 55;
        int baseWidth = 250;

        // Adds a title label for Bitwarden.
        DebugUtil.debug("Creating the Bitwarden title label...");
        JLabel title = SwingUtil.createLabel("Bitwarden",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Bitwarden.
        DebugUtil.debug("Creating the Bitwarden description label...");
        JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Bitwarden.
        DebugUtil.debug("Setting up the Bitwarden icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Bitwarden.png"), this);

        // Adds a button to launch Bitwarden.
        DebugUtil.debug("Creating the Bitwarden launch button...");
        JButton appButton = SwingUtil.createActionButton("Visit Bitwarden",
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
    private void setupSophosHome() {
        int baseHeight = 150;
        int baseWidth = 250;

        // Adds a title label for Sophos Home.
        DebugUtil.debug("Creating the Sophos Home title label...");
        JLabel title = SwingUtil.createLabel("Sophos Home",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Sophos Home.
        DebugUtil.debug("Creating the Sophos Home description label...");
        JLabel description = SwingUtil.createLabel("Price: $5/month",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Process Explorer.
        DebugUtil.debug("Setting up the Sophos Home icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Sophos.png"), this);

        // Adds a button to launch Sophos Home.
        DebugUtil.debug("Creating the Sophos Home launch button...");
        JButton appButton = SwingUtil.createActionButton("Visit Sophos Home",
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
    private void setupUBlockOrigin() {
        int baseHeight = 245;
        int baseWidth = 250;

        // Adds a title label for uBlock Origin.
        DebugUtil.debug("Creating the uBlock Origin title label...");
        JLabel title = SwingUtil.createLabel("uBlock Origin",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for uBlock Origin.
        DebugUtil.debug("Creating the uBlock Origin description label...");
        JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for uBlock Origin.
        DebugUtil.debug("Setting up the uBlock Origin icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/uBlockOrigin.png"), this);

        // Adds a button to launch uBlock Origin.
        DebugUtil.debug("Creating the uBlock Origin launch button...");
        JButton appButton = SwingUtil.createActionButton("Visit uBlock Origin",
                "Browser extension for content-filtering.",
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
        int baseWidth = 250;

        // Adds a title label for TrafficLight.
        DebugUtil.debug("Creating the TrafficLight title label...");
        JLabel title = SwingUtil.createLabel("TrafficLight",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for TrafficLight.
        DebugUtil.debug("Creating the TrafficLight description label...");
        JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for TrafficLight.
        DebugUtil.debug("Setting up the TrafficLight icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/TrafficLight.png"), this);

        // Adds a button to launch TrafficLight.
        DebugUtil.debug("Creating the TrafficLight launch button...");
        JButton appButton = SwingUtil.createActionButton("Visit TrafficLight",
                "Browser extension for safe browsing.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://bitdefender.com/solutions/trafficlight.html", true)
        );
        add(appButton);
    }

    /**
     * Sets up the Notepad++ section.
     */
    private void setupNotepadPlusPlus() {
        int baseHeight = 55;
        int baseWidth = 480;

        // Adds a title label for Notepad++.
        DebugUtil.debug("Creating the Notepad++ title label...");
        JLabel title = SwingUtil.createLabel("Notepad++",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Notepad++.
        DebugUtil.debug("Creating the Notepad++ description label...");
        JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Notepad++.
        DebugUtil.debug("Setting up the Notepad++ icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Notepad++.png"), this);

        // Adds a button to launch Notepad++.
        DebugUtil.debug("Creating the Notepad++ launch button...");
        JButton appButton = SwingUtil.createActionButton("Visit Notepad++",
                "Free and open-source text editor.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://notepad-plus-plus.org/downloads", true)
        );
        add(appButton);
    }

    /**
     * Sets up the Twinkle Tray section.
     */
    private void setupTwinkleTray() {
        int baseHeight = 150;
        int baseWidth = 480;

        // Adds a title label for Twinkle Tray.
        DebugUtil.debug("Creating the Twinkle Tray title label...");
        JLabel title = SwingUtil.createLabel("Twinkle Tray",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Twinkle Tray.
        DebugUtil.debug("Creating the Twinkle Tray description label...");
        JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Twinkle Tray.
        DebugUtil.debug("Setting up the Twinkle Tray icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/TwinkleTray.png"), this);

        // Adds a button to launch Twinkle Tray.
        DebugUtil.debug("Creating the Twinkle Tray launch button...");
        JButton appButton = SwingUtil.createActionButton("Visit Twinkle Tray",
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
    private void setupFanControl() {
        int baseHeight = 245;
        int baseWidth = 480;

        // Adds a title label for FanControl.
        DebugUtil.debug("Creating the FanControl title label...");
        JLabel title = SwingUtil.createLabel("FanControl",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for FanControl.
        DebugUtil.debug("Creating the FanControl description label...");
        JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for FanControl.
        DebugUtil.debug("Setting up the FanControl icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/FanControl.png"), this);

        // Adds a button to launch FanControl.
        DebugUtil.debug("Creating the FanControl launch button...");
        JButton appButton = SwingUtil.createActionButton("Visit FanControl",
                "Control your computer's fan speeds.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://getfancontrol.com", true)
        );
        add(appButton);
    }
}
