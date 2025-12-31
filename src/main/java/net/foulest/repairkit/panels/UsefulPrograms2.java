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
        @NotNull JLabel titleLabel = SwingUtil.createLabel("Useful Programs",
                new Rectangle(20, 15, 230, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 18)
        );
        add(titleLabel);

        // Creates the page label.
        DebugUtil.debug("Creating the Useful Programs (Page 2) page label...");
        @NotNull JLabel pageLabel = SwingUtil.createLabel("(Page 2/2)",
                new Rectangle(172, 15, 69, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 14)
        );
        add(pageLabel);

        // Adds the components to the panel.
        DebugUtil.debug("Adding components to the Useful Programs (Page 2) panel...");

        // Creates tasks for the executor.
        @NotNull List<Runnable> tasks = Arrays.asList(
                this::setupNVCleanstall,
                this::setupDDU,
                this::setupRecuva,

                this::setup7Zip,
                this::setupNotepadPlusPlus,

                this::setupBitwarden,
                this::setupSophosHome,
                this::setupUBlockOrigin,
                this::setupOsprey,

                this::setupTwinkleTray,
                this::setupFanControl
        );

        // Executes tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);

        // Sets the panel's border.
        DebugUtil.debug("Setting the Useful Programs (Page 2) panel border...");
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Adds a label for the previous page button.
        @NotNull JLabel previousPage = SwingUtil.createLabel("<",
                new Rectangle(250, 21, 22, 22),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 25)
        );
        previousPage.setForeground(Color.DARK_GRAY);
        add(previousPage);

        // Adds a button under the previous page label.
        @NotNull JButton previousPageButton = SwingUtil.createPanelButton("",
                "Useful Programs (Page 1)",
                new Rectangle(246, 20, 22, 23)
        );
        previousPageButton.setOpaque(false);
        previousPageButton.setContentAreaFilled(false);
        add(previousPageButton);

        // Adds a label for the next page button.
        @NotNull JLabel nextPage = SwingUtil.createLabel(">",
                new Rectangle(270, 21, 22, 22),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 25)
        );
        nextPage.setForeground(Color.LIGHT_GRAY);
        add(nextPage);
    }

    /**
     * Sets up the NVCleanstall section.
     */
    private void setupNVCleanstall() {
        int baseHeight = 55;
        int baseWidth = 20;

        // Adds a title label for NVCleanstall.
        DebugUtil.debug("Creating the NVCleanstall title label...");
        @NotNull JLabel title = SwingUtil.createLabel("NVCleanstall",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for NVCleanstall.
        DebugUtil.debug("Creating the NVCleanstall description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Version: 1.19.0",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for NVCleanstall.
        DebugUtil.debug("Setting up the NVCleanstall icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/NVCleanstall.png"), this);

        // Adds a button to launch NVCleanstall.
        DebugUtil.debug("Creating the NVCleanstall launch button...");
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
    private void setupDDU() {
        int baseHeight = 150;
        int baseWidth = 20;

        // Adds a title label for DDU.
        DebugUtil.debug("Creating the DDU title label...");
        @NotNull JLabel title = SwingUtil.createLabel("DDU",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for DDU.
        DebugUtil.debug("Creating the DDU description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Version: 18.1.3.9",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for DDU.
        DebugUtil.debug("Setting up the DDU icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/DDU.png"), this);

        // Adds a button to launch DDU.
        DebugUtil.debug("Creating the DDU launch button...");
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
    private void setupRecuva() {
        int baseHeight = 245;
        int baseWidth = 20;

        // Adds a title label for Recuva.
        DebugUtil.debug("Creating the Recuva title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Recuva",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Recuva.
        DebugUtil.debug("Creating the Recuva description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Version: 1.54.120",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Recuva.
        DebugUtil.debug("Setting up the Recuva icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Recuva.png"), this);

        // Adds a button to launch Recuva.
        DebugUtil.debug("Creating the Recuva launch button...");
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
     * Sets up the 7-Zip section.
     */
    private void setup7Zip() {
        int baseHeight = 340;
        int baseWidth = 20;

        // Adds a title label for 7-Zip.
        DebugUtil.debug("Creating the 7-Zip title label...");
        @NotNull JLabel title = SwingUtil.createLabel("7-Zip",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for 7-Zip.
        DebugUtil.debug("Creating the 7-Zip description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for 7-Zip.
        DebugUtil.debug("Setting up the 7-Zip icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/7-Zip.png"), this);

        // Adds a button to launch 7-Zip.
        DebugUtil.debug("Creating the 7-Zip launch button...");
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
    private void setupNotepadPlusPlus() {
        int baseHeight = 55;
        int baseWidth = 250;

        // Adds a title label for Notepad++.
        DebugUtil.debug("Creating the Notepad++ title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Notepad++",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Notepad++.
        DebugUtil.debug("Creating the Notepad++ description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Notepad++.
        DebugUtil.debug("Setting up the Notepad++ icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Notepad++.png"), this);

        // Adds a button to launch Notepad++.
        DebugUtil.debug("Creating the Notepad++ launch button...");
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
    private void setupBitwarden() {
        int baseHeight = 150;
        int baseWidth = 250;

        // Adds a title label for Bitwarden.
        DebugUtil.debug("Creating the Bitwarden title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Bitwarden",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Bitwarden.
        DebugUtil.debug("Creating the Bitwarden description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Bitwarden.
        DebugUtil.debug("Setting up the Bitwarden icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Bitwarden.png"), this);

        // Adds a button to launch Bitwarden.
        DebugUtil.debug("Creating the Bitwarden launch button...");
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
    private void setupSophosHome() {
        int baseHeight = 245;
        int baseWidth = 250;

        // Adds a title label for Sophos Home.
        DebugUtil.debug("Creating the Sophos Home title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Sophos Home",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Sophos Home.
        DebugUtil.debug("Creating the Sophos Home description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Price: $5/month",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Process Explorer.
        DebugUtil.debug("Setting up the Sophos Home icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Sophos.png"), this);

        // Adds a button to launch Sophos Home.
        DebugUtil.debug("Creating the Sophos Home launch button...");
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
    private void setupUBlockOrigin() {
        int baseHeight = 340;
        int baseWidth = 250;

        // Adds a title label for uBlock Origin.
        DebugUtil.debug("Creating the uBlock Origin title label...");
        @NotNull JLabel title = SwingUtil.createLabel("uBlock Origin",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for uBlock Origin.
        DebugUtil.debug("Creating the uBlock Origin description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for uBlock Origin.
        DebugUtil.debug("Setting up the uBlock Origin icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/uBlockOrigin.png"), this);

        // Adds a button to launch uBlock Origin.
        DebugUtil.debug("Creating the uBlock Origin launch button...");
        @NotNull JButton appButton = SwingUtil.createActionButton("Visit uBlock Origin",
                "Browser extension for content-filtering.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://ublockorigin.com", true)
        );
        add(appButton);
    }

    /**
     * Sets up the Osprey: Browser Protection section.
     */
    private void setupOsprey() {
        int baseHeight = 55;
        int baseWidth = 480;

        // Adds a title label for Osprey.
        DebugUtil.debug("Creating the Osprey title label...");
        @NotNull JLabel title = SwingUtil.createLabel("Osprey",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Osprey.
        DebugUtil.debug("Creating the Osprey description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Osprey.
        DebugUtil.debug("Setting up the Osprey icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/Osprey.png"), this);

        // Adds a button to launch Osprey.
        DebugUtil.debug("Creating the Osprey launch button...");
        @NotNull JButton appButton = SwingUtil.createActionButton("Visit Osprey",
                "Browser extension for safe browsing.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://github.com/Foulest/Osprey", true)
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
        @NotNull JLabel title = SwingUtil.createLabel("Twinkle Tray",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for Twinkle Tray.
        DebugUtil.debug("Creating the Twinkle Tray description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for Twinkle Tray.
        DebugUtil.debug("Setting up the Twinkle Tray icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/TwinkleTray.png"), this);

        // Adds a button to launch Twinkle Tray.
        DebugUtil.debug("Creating the Twinkle Tray launch button...");
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
    private void setupFanControl() {
        int baseHeight = 245;
        int baseWidth = 480;

        // Adds a title label for FanControl.
        DebugUtil.debug("Creating the FanControl title label...");
        @NotNull JLabel title = SwingUtil.createLabel("FanControl",
                new Rectangle(baseWidth + 43, baseHeight, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 16)
        );
        add(title);

        // Adds a description label for FanControl.
        DebugUtil.debug("Creating the FanControl description label...");
        @NotNull JLabel description = SwingUtil.createLabel("Price: Free",
                new Rectangle(baseWidth + 43, baseHeight + 20, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 12)
        );
        add(description);

        // Adds an icon for FanControl.
        DebugUtil.debug("Setting up the FanControl icon...");
        SwingUtil.setupAppIcon(baseHeight, baseWidth, FileUtil.getImageIcon("icons/FanControl.png"), this);

        // Adds a button to launch FanControl.
        DebugUtil.debug("Creating the FanControl launch button...");
        @NotNull JButton appButton = SwingUtil.createActionButton("Visit FanControl",
                "Control your computer's fan speeds.",
                new Rectangle(baseWidth, baseHeight + 50, 200, 30),
                new Color(200, 200, 200),
                () -> CommandUtil.runCommand("start https://getfancontrol.com", true)
        );
        add(appButton);
    }
}
