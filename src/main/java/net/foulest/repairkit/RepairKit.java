package net.foulest.repairkit;

import com.sun.jna.platform.win32.WinReg;
import lombok.extern.java.Log;
import net.foulest.repairkit.util.type.UninstallData;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.foulest.repairkit.util.CommandUtil.getCommandOutput;
import static net.foulest.repairkit.util.CommandUtil.runCommand;
import static net.foulest.repairkit.util.FileUtil.*;
import static net.foulest.repairkit.util.ProcessUtil.isProcessRunning;
import static net.foulest.repairkit.util.RegistryUtil.*;
import static net.foulest.repairkit.util.SoundUtil.playSound;
import static net.foulest.repairkit.util.SwingUtil.*;

@Log
public class RepairKit {

    private static final JFrame frame = new JFrame("RepairKit");
    private static final JPanel panelMain = new JPanel(null);
    private static boolean safeMode = false;
    private static boolean outdatedOperatingSystem = false;
    private static boolean windowsUpdateInProgress = false;

    /**
     * The main method of the program.
     *
     * @param args The program's arguments.
     */
    public static void main(String @NotNull [] args) {
        // Checks for incompatibility issues.
        checkOperatingSystemCompatibility();

        // Checks for Windows Update and Medal.
        if (!safeMode) {
            checkForWindowsUpdate();
            checkForMedal();
        }

        // Sets up the shutdown hook.
        setupShutdownHook();

        // Sets up necessary app registry keys.
        setAppRegistryKeys();

        // Creates the main frame.
        SwingUtilities.invokeLater(() -> {
            JFrame frame = createMainFrame();
            frame.setVisible(true);
        });
    }

    /**
     * Checks if the user's operating system is supported.
     */
    private static void checkOperatingSystemCompatibility() {
        String osName = System.getProperty("os.name");

        // Checks if the operating system is 32-bit.
        if (!System.getProperty("os.arch").contains("64")) {
            playSound("win.sound.hand");
            JOptionPane.showMessageDialog(null,
                    "Your operating system is 32-bit."
                            + "\nThis program is designed for 64-bit operating systems."
                            + "\nPlease upgrade to a 64-bit operating system to use this program."
                    , "Incompatible Operating System", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // Checks if the operating system is outdated (older than Windows 10).
        if (!osName.equalsIgnoreCase("Windows 10")
                && !osName.equalsIgnoreCase("Windows 11")) {
            if (osName.equalsIgnoreCase("Windows 8.1")
                    || osName.equalsIgnoreCase("Windows 8")
                    || osName.equalsIgnoreCase("Windows 7")
                    || osName.equalsIgnoreCase("Windows Vista")
                    || osName.equalsIgnoreCase("Windows XP")) {
                playSound("win.sound.hand");
                JOptionPane.showMessageDialog(null,
                        "Your operating system, " + osName + ", "
                                + "is outdated and no longer supported."
                                + "\nFeatures of this program may not work correctly or at all."
                        , "Outdated Operating System", JOptionPane.ERROR_MESSAGE);
                outdatedOperatingSystem = true;
            } else {
                playSound("win.sound.hand");
                JOptionPane.showMessageDialog(null,
                        "Your operating system, " + osName + ", "
                                + "is outdated, unknown, or not Windows based."
                                + "\n"
                        , "Incompatible Operating System", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }

        // Checks if the system is booting in Safe Mode.
        if (getCommandOutput("wmic COMPUTERSYSTEM GET BootupState", false, false).toString().contains("safe")) {
            playSound("win.sound.hand");
            JOptionPane.showMessageDialog(null,
                    "Your system is booting in Safe Mode."
                            + "\nFeatures of this program may not work correctly or at all."
                    , "Safe Mode Detected", JOptionPane.ERROR_MESSAGE);
            safeMode = true;
        }
    }

    /**
     * Checks if Windows Update is running.
     */
    private static void checkForWindowsUpdate() {
        // Checks if Windows Update is running.
        // Windows Update causes problems with DISM.
        if (isProcessRunning("WmiPrvSE.exe")
                && isProcessRunning("TiWorker.exe")
                && isProcessRunning("TrustedInstaller.exe")
                && isProcessRunning("wuauclt.exe")) {
            windowsUpdateInProgress = true;
            playSound("win.sound.asterisk");
            JOptionPane.showMessageDialog(null, "Windows Update is running on your system."
                            + "\nCertain tweaks will not be applied until the Windows Update is finished."
                    , "Software Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Checks if Medal is installed.
     * Medal causes issues with Desktop Window Manager.
     */
    private static void checkForMedal() {
        if (isProcessRunning("medal.exe")) {
            playSound("win.sound.asterisk");
            JOptionPane.showMessageDialog(null,
                    "Warning: Medal is installed and running on your system."
                            + "\nMedal causes issues with Desktop Windows Manager, which affects system performance."
                            + "\nFinding an alternative to Medal, such as ShadowPlay or AMD ReLive is recommended.",
                    "Software Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Sets the program's shutdown hook.
     */
    private static void setupShutdownHook() {
        // Clears the files used by RepairKit on shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                runCommand("rd /s /q " + tempDirectory.getPath(), false))
        );
    }

    /**
     * Sets necessary app registry keys.
     */
    private static void setAppRegistryKeys() {
        // Autoruns
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "CheckVirusTotal", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "EulaAccepted", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "HideEmptyEntries", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "HideMicrosoftEntries", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "HideVirusTotalCleanEntries", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "HideWindowsEntries", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "ScanOnlyPerUserLocations", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "SubmitUnknownImages", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns", "VerifyCodeSignatures", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Autoruns\\VirusTotal", "VirusTotalTermsAccepted", 1);

        // Process Explorer
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "ConfirmKill", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "EulaAccepted", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "VerifySignatures", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "VirusTotalCheck", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer", "VirusTotalSubmitUnknown", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Sysinternals\\Process Explorer\\VirusTotal", "VirusTotalTermsAccepted", 1);
    }

    /**
     * Creates the main frame of the program.
     *
     * @return The main frame of the program.
     */
    private static JFrame createMainFrame() {
        // Sets the program's GUI elements.
        setGUIElements();

        // Deletes pre-existing RepairKit files.
        runCommand("rd /s /q " + tempDirectory.getPath(), false);

        // Creates the main frame.
        frame.setContentPane(panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        return frame;
    }

    /**
     * Sets the program's GUI elements.
     */
    private static void setGUIElements() {
        setMainPanel();
        setLabels();
        setRepairButtons();
        setAppButtons();
        setLinkButtons();
    }

    /**
     * Sets the main panel of the program.
     */
    private static void setMainPanel() {
        panelMain.setPreferredSize(new Dimension(320, 355));
        panelMain.setBackground(new Color(43, 43, 43));
    }

    /**
     * Sets the program's labels.
     */
    private static void setLabels() {
        // Title Label
        JLabel labelTitle = createLabel("RepairKit by Foulest",
                new Color(225, 225, 225), 5, 5, 150, 20);
        addComponents(panelMain, labelTitle);

        // Useful Programs Label
        JLabel labelUsefulPrograms = createLabel("Useful Programs",
                new Color(225, 225, 225), 5, 75, 150, 20);
        addComponents(panelMain, labelUsefulPrograms);

        // System Shortcuts Label
        JLabel labelSystemShortcuts = createLabel("System Shortcuts",
                new Color(225, 225, 225), 5, 255, 150, 20);
        addComponents(panelMain, labelSystemShortcuts);
    }

    /**
     * Sets the program's repair buttons.
     */
    private static void setRepairButtons() {
        // Run Automatic Repairs Button
        JButton buttonRepairs = createActionButton("Run Automatic Repairs",
                "Performs various fixes and maintenance tasks.", () -> {
                    try {
                        // Deletes any system policies.
                        if (!outdatedOperatingSystem) {
                            deleteSystemPolicies();
                        }

                        // Installs 7-Zip and uninstalls other programs.
                        if (!safeMode) {
                            install7Zip();
                        }

                        // Create a new executor
                        ExecutorService executor = Executors.newWorkStealingPool();
                        CountDownLatch latch = new CountDownLatch(7);

                        // Clean junk files
                        executor.submit(() -> {
                            try {
                                cleanJunkFiles();
                                latch.countDown();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });

                        // Repair WMI repository
                        executor.submit(() -> {
                            try {
                                repairWMIRepository();
                                latch.countDown();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });

                        // Service tweaks
                        executor.submit(() -> {
                            try {
                                runServiceTweaks();
                                latch.countDown();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });

                        // Remove pre-installed bloatware
                        executor.submit(() -> {
                            try {
                                if (!outdatedOperatingSystem) {
                                    removeBloatware();
                                }

                                latch.countDown();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });

                        // Registry tweaks
                        executor.submit(() -> {
                            try {
                                if (!outdatedOperatingSystem) {
                                    runRegistryTweaks();
                                }

                                latch.countDown();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });

                        // Settings tweaks
                        executor.submit(() -> {
                            try {
                                if (!outdatedOperatingSystem) {
                                    runSettingsTweaks();
                                }

                                latch.countDown();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });

                        // Windows Defender tweaks
                        executor.submit(() -> {
                            try {
                                if (!outdatedOperatingSystem) {
                                    runWindowsDefenderTweaks();
                                }

                                latch.countDown();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });

                        // Wait for all tasks to complete
                        try {
                            latch.await();
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                            ex.printStackTrace();
                        }

                        // Shut down the executor
                        executor.shutdown();

                        // Displays a message dialog
                        playSound("win.sound.exclamation");
                        JOptionPane.showMessageDialog(null, "System issues repaired successfully.", "Finished", JOptionPane.QUESTION_MESSAGE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

        buttonRepairs.setBackground(new Color(200, 200, 200));
        buttonRepairs.setBounds(5, 30, 310, 35);
        addComponents(panelMain, buttonRepairs);
    }

    /**
     * Sets the program's app buttons.
     */
    private static void setAppButtons() {
        // FanControl Button
        JButton buttonFanControl = createActionButton("FanControl",
                "Allows control over system fans.", () -> {
                    if (outdatedOperatingSystem) {
                        playSound("win.sound.hand");
                        JOptionPane.showMessageDialog(null,
                                "FanControl cannot be run on outdated operating systems."
                                        + "\nPlease upgrade to Windows 10 or 11 to use this feature."
                                , "Outdated Operating System", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (safeMode) {
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
        buttonFanControl.setBackground(new Color(200, 200, 200));
        buttonFanControl.setBounds(5, 100, 152, 25);
        addComponents(panelMain, buttonFanControl);

        // CPU-Z Button
        JButton buttonCPUZ = createAppButton("CPU-Z", "Displays system hardware information.",
                "CPU-Z.zip", "CPU-Z.exe", true, tempDirectory.getPath());
        buttonCPUZ.setBounds(162, 100, 152, 25);
        addComponents(panelMain, buttonCPUZ);

        // TreeSize Button
        JButton buttonTreeSize;
        if (outdatedOperatingSystem) {
            buttonTreeSize = createActionButton("TreeSize",
                    "Displays system files organized by size.", () -> {
                        playSound("win.sound.hand");
                        JOptionPane.showMessageDialog(null,
                                "TreeSize cannot be run on outdated operating systems."
                                        + "\nPlease upgrade to Windows 10 or 11 to use this feature."
                                , "Outdated Operating System", JOptionPane.ERROR_MESSAGE);
                    });
        } else {
            buttonTreeSize = createAppButton("TreeSize", "Displays system files organized by size.",
                    "TreeSize.zip", "TreeSize.exe", true, tempDirectory.getPath());
        }
        buttonTreeSize.setBounds(5, 130, 152, 25);
        addComponents(panelMain, buttonTreeSize);

        // Everything Button
        JButton buttonEverything = createAppButton("Everything", "Displays all files on your system.",
                "Everything.zip", "Everything.exe", true, tempDirectory.getPath());
        buttonEverything.setBounds(162, 130, 152, 25);
        addComponents(panelMain, buttonEverything);

        // HWMonitor Button
        JButton buttonHWMonitor = createAppButton("HWMonitor", "Displays system hardware information.",
                "HWMonitor.zip", "HWMonitor.exe", true, tempDirectory.getPath());
        buttonHWMonitor.setBounds(5, 160, 152, 25);
        addComponents(panelMain, buttonHWMonitor);

        // Emsisoft Scan Button
        JButton buttonEmsisoft;
        if (outdatedOperatingSystem) {
            buttonEmsisoft = createActionButton("Emsisoft Scan",
                    "Scans your system for malware.", () -> {
                        playSound("win.sound.hand");
                        JOptionPane.showMessageDialog(null,
                                "Emsisoft Scan cannot be run on outdated operating systems."
                                        + "\nPlease upgrade to Windows 10 or 11 to use this feature."
                                , "Outdated Operating System", JOptionPane.ERROR_MESSAGE);
                    });
        } else {
            buttonEmsisoft = createAppButton("Emsisoft Scan", "Scans your system for malware.",
                    "Emsisoft.zip", "Emsisoft.exe", true, tempDirectory.getPath());
        }
        buttonEmsisoft.setBounds(162, 160, 152, 25);
        addComponents(panelMain, buttonEmsisoft);

        // Autoruns Button
        JButton buttonAutoruns = createAppButton("Autoruns", "Displays startup items.",
                "Autoruns.zip", "Autoruns.exe",
                true, tempDirectory.getPath());
        buttonAutoruns.setBounds(5, 220, 152, 25);
        addComponents(panelMain, buttonAutoruns);

        // Process Explorer Button
        JButton buttonProcessExplorer = createAppButton("Process Explorer", "Displays system processes.",
                "ProcessExplorer.zip", "ProcessExplorer.exe",
                true, tempDirectory.getPath());
        buttonProcessExplorer.setBounds(162, 220, 152, 25);
        addComponents(panelMain, buttonProcessExplorer);
    }

    /**
     * Sets the program's link buttons.
     */
    private static void setLinkButtons() {
        // uBlock Origin Button
        JButton buttonUBlockOrigin = createLinkButton("uBlock Origin",
                "Blocks ads and trackers across all websites.",
                "start https://ublockorigin.com");
        buttonUBlockOrigin.setBounds(5, 190, 152, 25);
        addComponents(panelMain, buttonUBlockOrigin);

        // TrafficLight Extension Button
        JButton buttonTrafficLight = createLinkButton("TrafficLight",
                "Blocks malicious websites and phishing attacks.",
                "start https://bitdefender.com/solutions/trafficlight.html");
        buttonTrafficLight.setBounds(162, 190, 152, 25);
        addComponents(panelMain, buttonTrafficLight);

        // Apps & Features Button
        JButton buttonAppsFeatures;
        if (!outdatedOperatingSystem) {
            buttonAppsFeatures = createLinkButton("Apps & Features",
                    "start ms-settings:appsfeatures");
        } else {
            buttonAppsFeatures = createLinkButton("Apps & Features",
                    "appwiz.cpl");
        }
        buttonAppsFeatures.setBounds(5, 280, 152, 25);
        addComponents(panelMain, buttonAppsFeatures);

        // Windows Update Button
        JButton buttonCheckForUpdates;
        if (outdatedOperatingSystem) {
            buttonCheckForUpdates = createLinkButton("Windows Update",
                    "control /name Microsoft.WindowsUpdate");
        } else {
            buttonCheckForUpdates = createLinkButton("Windows Update",
                    "start ms-settings:windowsupdate");
        }
        buttonCheckForUpdates.setBounds(162, 280, 152, 25);
        addComponents(panelMain, buttonCheckForUpdates);

        // Task Manager Button
        JButton buttonTaskManager = createLinkButton("Task Manager",
                "taskmgr");
        buttonTaskManager.setBounds(5, 310, 152, 25);
        addComponents(panelMain, buttonTaskManager);

        // Windows Defender Button
        JButton buttonSecurity;
        if (outdatedOperatingSystem) {
            buttonSecurity = createLinkButton("Windows Defender",
                    "control /name Microsoft.WindowsDefender");
        } else {
            buttonSecurity = createLinkButton("Windows Defender",
                    "start windowsdefender:");
        }
        buttonSecurity.setBounds(162, 310, 152, 25);
        addComponents(panelMain, buttonSecurity);
    }

    /**
     * Cleans junk files using CCleaner.
     */
    private static void cleanJunkFiles() {
        log.info("Cleaning junk files...");
        long startTime = System.currentTimeMillis();

        // Kills CCleaner
        runCommand("taskkill /F /IM CCleaner.exe", false);
        runCommand("rd /s /q \"" + tempDirectory + "\\CCleaner\"", false);

        // Extracts CCleaner
        try (InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/CCleaner.zip")) {
            saveFile(Objects.requireNonNull(input), "CCleaner.zip", true);
            unzipFile(tempDirectory + "\\CCleaner.zip", tempDirectory.getPath() + "\\CCleaner");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Runs CCleaner
        runCommand(tempDirectory + "\\CCleaner\\CCleaner /AUTO", false);

        // Deletes the CCleaner scheduled task
        runCommand("schtasks /delete /tn \"CCleanerSkipUAC - Windows\" /F", false);

        log.info("Cleaned junk files in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Deletes any existing system policies.
     */
    private static void deleteSystemPolicies() {
        log.info("Deleting system policies...");
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(4);

        executor.submit(() -> {
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Policies\\Microsoft\\MMC");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Policies\\Microsoft\\Windows\\System");
            deleteRegistryKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Google\\Chrome");
            latch.countDown();
        });

        executor.submit(() -> {
            deleteRegistryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\NonEnum", "{645FF040-5081-101B-9F08-00AA002F954E}");
            deleteRegistryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "DisableRegistryTools");
            deleteRegistryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "DisableTaskMgr");
            deleteRegistryValue(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\System", "DisableCMD");
            deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\Explorer", "DisallowCpl");
            deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\Explorer", "NoFolderOptions");
            deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Windows NT\\System Restore", "DisableConfig");
            deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Windows NT\\System Restore", "DisableSR");
            deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Class\\{4D36E965-E325-11CE-BFC1-08002BE10318}", "LowerFilters");
            deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Class\\{4D36E965-E325-11CE-BFC1-08002BE10318}", "UpperFilters");
            latch.countDown();
        });

        executor.submit(() -> {
            setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon", "Shell", "explorer.exe");
            setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon", "Shell", "explorer.exe");
            setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon", "Userinit", "C:\\Windows\\system32\\userinit.exe,");
            latch.countDown();
        });

        executor.submit(() -> {
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "Icons Only", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced\\Folder\\Hidden\\SHOWALL", "CheckedValue", 1);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\NonEnum", "{645FF040-5081-101B-9F08-00AA002F954E}", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "ConsentPromptBehaviorAdmin", 5);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "ConsentPromptBehaviorUser", 1);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "EnableLUA", 1);
            latch.countDown();
        });

        // Wait for all tasks to complete
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            ex.printStackTrace();
        }

        // Shut down the executor
        executor.shutdown();
        log.info("Deleted system policies in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Installs 7-Zip and uninstalls other archivers.
     */
    private static void install7Zip() {
        log.info("Installing 7-Zip and uninstalling other archivers...");
        Path sevenZipPath = Paths.get("C:\\Program Files\\7-Zip\\7zFM.exe");
        Path tempPath = Paths.get(tempDirectory + "\\7-Zip\\7-Zip.exe");

        List<UninstallData> uninstallDataList = Arrays.asList(
                new UninstallData("C:\\ProgramData\\WinZip", null),
                new UninstallData("C:\\Program Files (x86)\\CAM Development", "\"C:\\Program Files (x86)\\CAM Development\\CAM UnZip 5\\Setup\\unins000.exe\""),
                new UninstallData("C:\\Program Files\\PowerArchiver", "\"C:\\Program Files\\PowerArchiver\\unins000.exe\""),
                new UninstallData("C:\\Program Files (x86)\\IZArc", "\"C:\\Program Files (x86)\\IZArc\\unins000.exe\""),
                new UninstallData("C:\\Program Files (x86)\\ZipGenius 6", "\"C:\\Program Files (x86)\\ZipGenius 6\\unins000.exe\""),
                new UninstallData("C:\\Program Files\\WinRAR", "\"C:\\Program Files\\WinRAR\\uninstall.exe\" /S"),
                new UninstallData("C:\\Program Files (x86)\\WinRAR", "\"C:\\Program Files (x86)\\WinRAR\\uninstall.exe\" /S"),
                new UninstallData("C:\\Program Files\\Bandizip", "\"C:\\Program Files\\Bandizip\\uninstall\" /S"),
                new UninstallData("C:\\Program Files\\PeaZip", "\"C:\\Program Files\\PeaZip\\unins000.exe\""),
                new UninstallData("C:\\Program Files (x86)\\NCH Software\\ExpressZip", null),
                new UninstallData("C:\\Program Files (x86)\\B1 Free Archiver", null),
                new UninstallData(System.getenv("LOCALAPPDATA") + "\\Trend Micro\\UnzipOne", System.getenv("LOCALAPPDATA") + "\\Trend Micro\\UnzipOne\\unins000.exe\"")
        );

        boolean shouldInstall7Zip = !Files.exists(sevenZipPath);
        boolean shouldUninstallOthers = uninstallDataList.stream()
                .anyMatch(data -> Files.exists(Paths.get(data.directoryPath)));

        if (shouldInstall7Zip || shouldUninstallOthers) {
            int choice = JOptionPane.showConfirmDialog(null,
                    "Install 7-Zip and remove other .zip programs? (Recommended)", "Install 7-Zip",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                uninstallDataList.forEach(data -> {
                    if (Files.exists(Paths.get(data.directoryPath))) {
                        if (data.uninstallCommand != null) {
                            runCommand(data.uninstallCommand, false);
                            runCommand("rd /s /q \"" + data.directoryPath + "\"", true);
                        } else {
                            playSound("win.sound.hand");
                            JOptionPane.showMessageDialog(null,
                                    "Please manually uninstall the program in " + data.directoryPath + " via Installed Apps.",
                                    "Error Uninstalling", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

                // Deletes Bandizip files.
                runCommand("del /s /q \"" + System.getenv("APPDATA")
                        + "\\Microsoft\\Internet Explorer\\Quick Launch\\User Pinned\\TaskBar\\Tombstones\\Bandizip.lnk\"", true);

                // Deletes PeaZip files.
                runCommand("rd /s /q \"%AppData%\\PeaZip", true);

                // Installs 7-Zip.
                if (shouldInstall7Zip) {
                    if (!Files.exists(tempPath)) {
                        try (InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/7-Zip.zip")) {
                            saveFile(Objects.requireNonNull(input), "7-Zip.zip", true);
                            unzipFile(tempDirectory + "\\7-Zip.zip", tempDirectory.getPath() + "\\7-Zip");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    runCommand(tempPath + " /D=\"C:\\Program Files\\7-Zip\" /S", false);
                }
            }
        }
    }

    /**
     * Repairs the WMI Repository.
     */
    private static void repairWMIRepository() {
        log.info("Repairing WMI repository...");
        if (getCommandOutput("winmgmt /verifyrepository", false, false).toString().contains("not consistent")
                && getCommandOutput("winmgmt /salvagerepository", false, false).toString().contains("not consistent")) {
            runCommand("winmgmt /resetrepository", false);
        }
    }

    /**
     * Runs tweaks to the Windows registry.
     */
    private static void runRegistryTweaks() {
        log.info("Running registry tweaks...");
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(19);

        // Disables telemetry and annoyances.
        executor.submit(() -> {
            deleteRegistryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Siuf\\Rules", "PeriodInNanoSeconds");
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\International\\User Profile", "HttpAcceptLanguageOptOut", 1);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Personalization\\Settings", "AcceptedPrivacyPolicy", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\InputPersonalization", "RestrictImplicitInkCollection", 1);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\InputPersonalization", "RestrictImplicitTextCollection", 1);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\InputPersonalization\\TrainedDataStore", "HarvestContacts", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Input\\Settings", "InsightsEnabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Input\\TIPC", "Enabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Narrator\\NoRoam", "DetailedFeedback", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Siuf\\Rules", "NumberOfSIUFInPeriod", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Siuf\\Rules", "PeriodInNanoSeconds", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Speech_OneCore\\Settings\\OnlineSpeechPrivacy", "HasAccepted", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\AdvertisingInfo", "Enabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\BackgroundAccessApplications", "GlobalUserDisabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\ContentDeliveryManager", "SilentInstalledAppsEnabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\ContentDeliveryManager", "SoftLandingEnabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\ContentDeliveryManager", "SubscribedContent-310093Enabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\ContentDeliveryManager", "SubscribedContent-314563Enabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\ContentDeliveryManager", "SubscribedContent-338388Enabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\ContentDeliveryManager", "SubscribedContent-338389Enabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\ContentDeliveryManager", "SubscribedContent-338393Enabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\ContentDeliveryManager", "SubscribedContent-353694Enabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\ContentDeliveryManager", "SubscribedContent-353696Enabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\ContentDeliveryManager", "SubscribedContent-353698Enabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\ContentDeliveryManager", "SubscribedContent-88000105Enabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\ContentDeliveryManager", "SystemPaneSuggestionsEnabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Diagnostics\\DiagTrack", "ShowedToastAtLevel", 1);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "TaskbarDa", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "TaskbarMn", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\PenWorkspace", "PenWorkspaceAppSuggestionsEnabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Privacy", "TailoredExperiencesWithDiagnosticDataEnabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\UserProfileEngagement", "ScoobeSystemSettingEnabled", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\PolicyManager\\current\\device\\Bluetooth", "AllowAdvertising", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\PolicyManager\\current\\device\\System", "AllowExperimentation", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\WcmSvc\\wifinetworkmanager\\features\\S-1-5-21-1376222853-718990322-3209866679-1001\\SocialNetworks\\ABCH", "OptInStatus", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\WcmSvc\\wifinetworkmanager\\features\\S-1-5-21-1376222853-718990322-3209866679-1001\\SocialNetworks\\ABCH-SKYPE", "OptInStatus", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\WcmSvc\\wifinetworkmanager\\features\\S-1-5-21-1376222853-718990322-3209866679-1001\\SocialNetworks\\FACEBOOK", "OptInStatus", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Diagnostics\\DiagTrack", "DiagTrackAuthorization", 7);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\ControlSet001\\Control\\WMI\\Autologger\\AutoLogger-Diagtrack-Listener", "Start", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\ControlSet001\\Services\\DiagTrack", "Start", 4);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\ControlSet001\\Services\\diagnosticshub.standardcollector.service", "Start", 4);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\MediaPlayer\\Preferences", "UsageTracking", 0);
            setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Image File Execution Options\\'DeviceCensus.exe'", "Debugger", "%windir%\\System32\\taskkill.exe");
            setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Image File Execution Options\\'CompatTelRunner.exe'", "Debugger", "%windir%\\System32\\taskkill.exe");
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\EdgeUpdate", "DoNotUpdateToEdgeWithChromium", 1);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Remote Assistance", "fAllowToGetHelp", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Remote Assistance", "fAllowFullControl", 0);
            latch.countDown();
        });

        // Patches security vulnerabilities.
        executor.submit(() -> {
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Lsa", "NoLMHash", 1);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Windows\\Installer", "AlwaysInstallElevated", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Windows\\Explorer", "NoDataExecutionPrevention", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Windows\\SYSTEM", "DisableHHDEP", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Windows\\WinRM\\Client", "AllowBasic", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\LSA", "RestrictAnonymous", 1);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Lsa", "LmCompatibilityLevel", 5);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\kernel", "DisableExceptionChainValidation", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\kernel", "RestrictAnonymousSAM", 1);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\LanManServer\\Parameters", "RestrictNullSessAccess", 1);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\Explorer", "NoDriveTypeAutoRun", 255);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Terminal Server", "fDenyTSConnections", 1);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon\\SpecialAccounts\\UserList", "Guest", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon\\SpecialAccounts\\UserList", "Administrator", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\FVE", "UseAdvancedStartup", 1);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows Script Host\\Settings", "Enabled", 0);
            latch.countDown();
        });

        // Deletes telemetry & recent files logs.
        executor.submit(() -> {
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Adobe\\MediaBrowser\\MRU");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Direct3D\\MostRecentApplication");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\MediaPlayer\\Player\\RecentFileList");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\MediaPlayer\\Player\\RecentURLList");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Search Assistant\\ACMru");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Applets\\Paint\\Recent File List");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Applets\\Regedit");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Applets\\Regedit\\Favorites");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Applets\\Wordpad\\Recent File List");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\ComDlg32\\LastVisitedPidlMRU");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\ComDlg32\\LastVisitedPidlMRULegacy");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\ComDlg32\\OpenSaveMRU");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Map Network Drive MRU");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\RecentDocs");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\RunMRU");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\TypedPaths");
            deleteRegistryKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Direct3D\\MostRecentApplication");
            deleteRegistryKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\MediaPlayer\\Player\\RecentFileList");
            deleteRegistryKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\MediaPlayer\\Player\\RecentURLList");
            deleteRegistryKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Applets\\Paint\\Recent File List");
            deleteRegistryKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Applets\\Regedit");
            deleteRegistryKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Applets\\Regedit\\Favorites");
            deleteRegistryKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Map Network Drive MRU");
            deleteRegistryKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\RecentDocs");
            latch.countDown();
        });

        // Disables certain search and Cortana functions.
        executor.submit(() -> {
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Speech_OneCore\\Preferences", "ModelDownloadAllowed", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Speech_OneCore\\Preferences", "VoiceActivationDefaultOn", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Speech_OneCore\\Preferences", "VoiceActivationEnableAboveLockscreen", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Speech_OneCore\\Preferences", "VoiceActivationOn", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "ShowCortanaButton", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "CanCortanaBeEnabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "CortanaConsent", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "CortanaEnabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "CortanaInAmbientMode", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "DeviceHistoryEnabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "HistoryViewEnabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "VoiceShortcut", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\SearchSettings", "IsDeviceSearchHistoryEnabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\SearchSettings", "SafeSearchMode", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\PolicyManager\\default\\Experience\\AllowCortana", "value", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\OOBE", "DisableVoice", 1);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "BingSearchEnabled", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "CortanaEnabled", 0);
            latch.countDown();
        });

        // Disables Windows error reporting.
        executor.submit(() -> {
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\Windows Error Reporting", "Disabled", 1);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\Windows Error Reporting\\Consent", "DefaultConsent", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\Windows Error Reporting\\Consent", "DefaultOverrideBehavior", 1);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\Windows Error Reporting\\Consent", "DefaultOverrideBehavior", 1);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\Windows Error Reporting", "DontSendAdditionalData", 1);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\Windows Error Reporting", "LoggingDisabled", 1);
            latch.countDown();
        });

        // Resets the Recycle Bin's icons.
        executor.submit(() -> {
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CLSID\\{645FF040-5081-101B-9F08-00AA002F954E}\\DefaultIcon", "(Default)", "C:\\Windows\\System32\\imageres.dll,-54");
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CLSID\\{645FF040-5081-101B-9F08-00AA002F954E}\\DefaultIcon", "empty", "C:\\Windows\\System32\\imageres.dll,-55");
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CLSID\\{645FF040-5081-101B-9F08-00AA002F954E}\\DefaultIcon", "full", "C:\\Windows\\System32\\imageres.dll,-54");
            latch.countDown();
        });

        // Enables updates for other Microsoft products.
        executor.submit(() -> {
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\WindowsUpdate\\UX\\Settings", "AllowMUUpdateService", 1);
            latch.countDown();
        });

        // Disables certain File Explorer features.
        executor.submit(() -> {
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "HideFileExt", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer", "ShowFrequent", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "DontUsePowerShellOnWinX", 1);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "ShowSyncProviderNotifications", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "Start_TrackProgs", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "Start_TrackDocs", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\AutoplayHandlers", "DisableAutoplay", 1);
            setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FolderDescriptions\\{31C0DD25-9439-4F12-BF41-7FF4EDA38722}\\PropertyBag", "ThisPCPolicy", "Hide");
            setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FolderDescriptions\\{31C0DD25-9439-4F12-BF41-7FF4EDA38722}\\PropertyBag", "ThisPCPolicy", "Hide");
            latch.countDown();
        });

        // Patches Spectre & Meltdown security vulnerabilities.
        executor.submit(() -> {
            String cpuName = getCommandOutput("wmic cpu get name", false, false).toString();
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Memory Management", "FeatureSettingsOverrideMask", 3);
            setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Virtualization", "MinVmVersionForCpuBasedMitigations", "1.0");

            if (cpuName.contains("Intel")) {
                setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Memory Management", "FeatureSettingsOverride", 0);
            } else if (cpuName.contains("AMD")) {
                setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Memory Management", "FeatureSettingsOverride", 64);
            }
            latch.countDown();
        });

        // Disables the weather and news widget.
        executor.submit(() -> {
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Feeds", "EnableFeeds", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Feeds", "ShellFeedsTaskbarViewMode", 2);
            latch.countDown();
        });

        // Disables Game DVR.
        executor.submit(() -> {
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\GameDVR", "AppCaptureEnabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SYSTEM\\GameConfigStore", "GameDVR_Enabled", 0);
            latch.countDown();
        });

        // Disables lock screen toasts.
        executor.submit(() -> {
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Notifications\\Settings", "NOC_GLOBAL_SETTING_ALLOW_TOASTS_ABOVE_LOCK", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\PushNotifications", "LockScreenToastEnabled", 0);
            latch.countDown();
        });

        // Enables Storage Sense.
        executor.submit(() -> {
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "01", 1);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "04", 1);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "08", 1);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "2048", 1);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "256", 30);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "32", 1);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "512", 30);
            latch.countDown();
        });

        // Modifies Windows graphics settings.
        executor.submit(() -> {
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\DirectX\\UserGpuPreferences", "DirectXUserGlobalSettings", "VRROptimizeEnable=1");
            latch.countDown();
        });

        // Modifies Windows networking settings.
        executor.submit(() -> {
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\LanmanServer\\Parameters", "IRPStackSize", 30);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters", "DefaultTTL", 64);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters", "MaxUserPort", 65534);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters", "Tcp1323Opts", 1);
            latch.countDown();
        });

        // Disables sticky keys.
        executor.submit(() -> {
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Accessibility\\ToggleKeys", "Flags", "58");
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Accessibility\\StickyKeys", "Flags", "506");
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Accessibility\\Keyboard Response", "Flags", "122");
            latch.countDown();
        });

        // Disables mouse acceleration.
        executor.submit(() -> {
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Mouse", "MouseSpeed", "0");
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Mouse", "MouseThreshold1", "0");
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Mouse", "MouseThreshold2", "0");
            latch.countDown();
        });

        // Restores the keyboard layout.
        executor.submit(() -> {
            deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Keyboard Layout", "Scancode Map");
            latch.countDown();
        });

        // Fixes a battery visibility issue.
        executor.submit(() -> {
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Power", "EnergyEstimationEnabled", 1);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Power", "EnergyEstimationDisabled", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Power", "UserBatteryDischargeEstimator", 0);
            latch.countDown();
        });

        // Sets certain services to start automatically.
        executor.submit(() -> {
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows Search", "SetupCompletedSuccessfully", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\WSearch", "Start", 2);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\VSS", "Start", 2);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\PlugPlay", "Start", 2);
            latch.countDown();
        });

        // Wait for all tasks to complete
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            ex.printStackTrace();
        }

        // Shut down the executor
        executor.shutdown();
        log.info("Registry tweaks completed in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Runs various tweaks to the Windows services.
     */
    private static void runServiceTweaks() {
        log.info("Tweaking services...");
        List<String[]> serviceList = Arrays.asList(
                new String[]{"DiagTrack", "Connected User Experiences and Telemetry"},
                new String[]{"MapsBroker", "Downloaded Maps Manager"},
                new String[]{"PcaSvc", "Program Compatibility Assistant Service"},
                new String[]{"RemoteAccess", "Remote Access"},
                new String[]{"RemoteRegistry", "Remote Registry"},
                new String[]{"RetailDemo", "Retail Demo"},
                new String[]{"VSStandardCollectorService150", "Visual Studio Standard Collector Service"},
                new String[]{"WMPNetworkSvc", "Windows Media Player Network Sharing Service"},
                new String[]{"WpcMonSvc", "Parental Controls"},
                new String[]{"diagnosticshub.standardcollector.service", "Diagnostics Hub Standard Collector Service"},
                new String[]{"diagsvc", "Diagnostic Execution Service"},
                new String[]{"dmwappushservice", "WAP Push Message Routing Service"},
                new String[]{"fhsvc", "File History Service"},
                new String[]{"lmhosts", "TCP/IP NetBIOS Helper"},
                new String[]{"wercplsupport", "wercplsupport"},
                new String[]{"wersvc", "wersvc"}
        );

        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(serviceList.size());

        // Iterate through the list of services
        for (String[] serviceInfo : serviceList) {
            executor.submit(() -> {
                try {
                    String serviceName = serviceInfo[0];
                    runCommand("sc stop \"" + serviceName + "\"", true);
                    runCommand("sc config \"" + serviceName + "\" start=disabled", true);
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all tasks to complete
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            ex.printStackTrace();
        }

        // Shut down the executor
        executor.shutdown();
        log.info("Tweaked " + serviceList.size() + " services in "
                + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Removes various bloatware applications from the system.
     */
    private static void removeBloatware() {
        log.info("Removing bloatware...");
        long startTime = System.currentTimeMillis();
        String command = "PowerShell -ExecutionPolicy Unrestricted -Command \"(Get-AppxPackage).ForEach({ $_.Name })\"";
        List<String> output = getCommandOutput(command, false, false);
        Set<String> installedPackages = new HashSet<>(output);

        String[] appPackages = {
                // Pre-installed Windows apps
                "46928bounde.EclipseManager",
                "9E2F88E3.Twitter",
                "ActiproSoftwareLLC.562882FEEB491",
                "ClearChannelRadioDigital.iHeartRadio",
                "D5EA27B7.Duolingo-LearnLanguagesforFree",
                "Flipboard.Flipboard",
                "Microsoft.3DBuilder",
                "Microsoft.549981C3F5F10",
                "Microsoft.Advertising.Xaml",
                "Microsoft.BingFinance",
                "Microsoft.BingNews",
                "Microsoft.BingSports",
                "Microsoft.BingWeather",
                "Microsoft.CommsPhone",
                "Microsoft.GetHelp",
                "Microsoft.Getstarted",
                "Microsoft.GroupMe10",
                "Microsoft.MSPaint",
                "Microsoft.Messaging",
                "Microsoft.Microsoft3DViewer",
                "Microsoft.MicrosoftOfficeHub",
                "Microsoft.MicrosoftSolitaireCollection",
                "Microsoft.MicrosoftStickyNotes",
                "Microsoft.MixedReality.Portal",
                "Microsoft.NetworkSpeedTest",
                "Microsoft.Office.OneNote",
                "Microsoft.Office.Sway",
                "Microsoft.OneConnect",
                "Microsoft.People",
                "Microsoft.Print3D",
                "Microsoft.RemoteDesktop",
                "Microsoft.SkypeApp",
                "Microsoft.Todos",
                "Microsoft.Wallet",
                "Microsoft.Windows.Phone",
                "Microsoft.WindowsFeedbackHub",
                "Microsoft.WindowsMaps",
                "Microsoft.WindowsPhone",
                "Microsoft.WindowsSoundRecorder",
                "Microsoft.YourPhone",
                "PandoraMediaInc.29680B314EFC2",
                "ShazamEntertainmentLtd.Shazam",
                "king.com.CandyCrushSaga",
                "king.com.CandyCrushSodaSaga",

                // Blocked publishers
                "05980FDA.*",
                "0D9A1B2D.*",
                "10301PerfectThumb.*",
                "10801DigitalTz.*",
                "11990MediaHub.*",
                "1200P33kbooVPNServices.*",
                "12166732A9970.*",
                "12450WhiteMoonlight.*",
                "12496JioUWP.*",
                "13158BethanySophia.*",
                "13395RBCORP.*",
                "14184MeetmeXMTechnologyCo.*",
                "14586regulars.*",
                "14589Nov.*",
                "14911ToshikiTomihira.*",
                "14C78905.*",
                "15068GalaxyApps.*",
                "15191PeakPlayer.*",
                "15647NeonBand.*",
                "16579RBSoftInc.*",
                "16939CMDevelopers.*",
                "17580Baronan.*",
                "17648Osceus.*",
                "18182E8D6764.*",
                "18663FirePDF.*",
                "1901TwentyOneTeam.*",
                "19701APPXOTICA.*",
                "20654MicroYiAppStudio.*",
                "20815shootingapp.*",
                "21336V3TApps.*",
                "21676OptimiliaStudios.*",
                "2242VelocityAppsTeam.*",
                "22450.*",
                "22546Cidade.*",
                "2277844670.*",
                "22785wolfSYS.*",
                "22858LISAppStudio.*",
                "22921LinhNguyen.*",
                "23436LAT.*",
                "23469Whatever2048.*",
                "23836FeefiGaming.*",
                "24091FileFormatApps.*",
                "2436VCApps.*",
                "25930UnblockMate.*",
                "26031PicsCanvas.*",
                "2628LiveNewsNowInc.*",
                "26571KonstantinSoftware.*",
                "2664ShoolinShiv.*",
                "2713WilsonByrne.*",
                "2725Swisspix.*",
                "27324InternetOfThingsDev.*",
                "28131MobiDreamNet.*",
                "2841abhijith94.*",
                "28908CodeHive.*",
                "29009AugiApps.*",
                "29645FreeConnectedLimited.*",
                "29982SibistLtd.*",
                "30203DEE513B8.*",
                "3042cilixft.*",
                "3138AweZip.*",
                "32174XingLiHui.*",
                "325289AEDD75.*",
                "32533HUXSoft.*",
                "32703RoxyApps.*",
                "33842Tronlabs.*",
                "33865VideoStudio.*",
                "3396Flysoft.*",
                "34020IRBOETECH.*",
                "34599PandaViolet.*",
                "35450PhotoCoolApps.*",
                "3559TVMedia.*",
                "36059XiaoyaStudio.*",
                "3718.*",
                "37309CoolLeGetInc.*",
                "38123SoftwareGoodiebag.*",
                "38184CDCTech.*",
                "38526MediaLife.*",
                "38623ExtremeSleeper.*",
                "38806TusharKoshti.*",
                "39171BastianAunkofer.*",
                "39252LionGroup.*",
                "39492FruitCandy.*",
                "39611MusiciTubeMedia.*",
                "39691Videopix.*",
                "40090TheMockingBird.*",
                "40119PurpleMartin.*",
                "40174MouriNaruto.*",
                "40242YTDApp.*",
                "40507LinfengLi.*",
                "40720RMDEV.*",
                "41219Prispiii.*",
                "41749.*",
                "41824Dozrekt.*",
                "41879VbfnetApps.*",
                "42331JPLiu.*",
                "42458PDFIUMAPP.*",
                "42606NeededSpecialTools.*",
                "42742filesuite.*",
                "43692CyanFood.*",
                "43911Invotech.*",
                "43975GKMServicesLtd.*",
                "44500SecurityDevelopment.*",
                "4515BlueCapo.*",
                "45552VictoryTechnology.*",
                "45907smallapp.*",
                "47236EllyFieldStudios.*",
                "47772AVGTechnologies.*",
                "48092WHNC.*",
                "4829OILYMOB.*",
                "48433PhantancyBubble.*",
                "48494ChristianRegli.*",
                "48713HLXB.*",
                "49612CrowdedRoad.*",
                "49659SandpiperStudio.*",
                "49715BoskoApps.*",
                "49775MorningInSeattle.*",
                "4978BestGameStudio.*",
                "4K-SOFTLTD.*",
                "50138MConverter.*",
                "50236FileViewerProInc.*",
                "50976yce.*",
                "51371LastMedia.*",
                "51966IsabellaVictoria.*",
                "51CA791E.*",
                "52446FusionChat.*",
                "5259FreeSoftwareApps.*",
                "52808CardDevelop.*",
                "53058betterapp.*",
                "53288ThiagoFortes.*",
                "53354DuckheadSoftware.*",
                "54034Myrcello.*",
                "547363FEF1877.*",
                "5514tejasbst.*",
                "55164OliverLi.*",
                "55218SkysparkSoftware.*",
                "55562LudeStudio.*",
                "55858HATAYANX.*",
                "55993czmade.*",
                "56360MoonlightTidalTechno.*",
                "56438Zazzu.*",
                "57443TechFireX.*",
                "57808ToolFun.*",
                "57868Codaapp.*",
                "57935AX-Systems.com.*",
                "58121SomeMediaApps.*",
                "5874nestebe.*",
                "5913DefineStudio.*",
                "59169Willpowersystems.*",
                "5970SecurityInternetDevel.*",
                "59992Roob.*",
                "5A894077.*",
                "5E8FC25E.*",
                "60191FreshJuice.*",
                "60907HaThiDieuTrang.*",
                "61083ApeApps.*",
                "61338learntechnologyapp.*",
                "61545TimGrabinat.*",
                "61878MobilityinLifeapplic.*",
                "62132PavloVS.*",
                "6229MusicallyWorld.*",
                "62307pauljohn.*",
                "62327DamTechDesigns.*",
                "63341FinalA..*",
                "63780CryptiqWEB3.*",
                "6382CoalaApps.*",
                "64343GTDocStudio.*",
                "64404Softuna.*",
                "64932DatLeThanh.*",
                "6655KAEROS.*",
                "6655kaeros.*",
                "6727MontyInc.*",
                "6760NGPDFLab.*",
                "6764XLGeekCoder.*",
                "6846IndigoPDFLLC.*",
                "6F71D7A7.*",
                "723BlossXHawkDev.*",
                "7549finetuneapps.*",
                "76Chococode.*",
                "8075Queenloft.*",
                "8266FireFlyBrowser.*",
                "89E2DF08.*",
                "9432UNISAPPS.*",
                "9601SemivioTechnologies.*",
                "A8B8B8A8.*",
                "AFF540DC.*",
                "AmplifyVentures.*",
                "AnywaySoftInc.*",
                "ArtGroup.*",
                "Avira.*",
                "BNESIM.*",
                "BOOSTUDIOLLC.*",
                "BallardAppCraftery.*",
                "Bandisoft.com.*",
                "BitberrySoftware.*",
                "BooStudioLLC.*",
                "CyberheartPte.Ltd.*",
                "D17A4821.*",
                "DayglowsInc.*",
                "Defenx.*",
                "DeskShare.*",
                "DeviceDoctor.*",
                "DriveHeadquartersInc.*",
                "EverydayToolsLLC.*",
                "FASTPOTATOPTE.LTD.*",
                "FIREWORKSTECHNOLOGYINC.*",
                "FIYINGORONOCOLTD.*",
                "Farlex.*",
                "First-Query.*",
                "FlyingbeeSoftwareCo.*",
                "FreeVPNPlanet.*",
                "Gamma.app.*",
                "GenmokuCo.Ltd.*",
                "GoodnotesLimited.*",
                "IFreeNetInc.*",
                "IOForth.*",
                "IOStreamCo.*",
                "InternetTVServices.*",
                "JoydustryTOO.*",
                "LLCSKYSPARKCORP.*",
                "LifeAppTechnologyLimited.*",
                "MAGIX.*",
                "MobiSystems.*",
                "NANOSecurity.*",
                "NCHSoftware.*",
                "NeroAG.*",
                "NodeVPN.*",
                "PDFTechnologiesInc.*",
                "Poikosoft.*",
                "PrimeFintechSolutionCYLtd.*",
                "PrivadaTechLimited.*",
                "ProtelionGmbH.*",
                "QIHU360SOFTWARECO.LIMITED.*",
                "ROCKETTECHNOLOGYINC.*",
                "RapartyLTD.*",
                "Roxy.*",
                "SOFTPOSTLLC.*",
                "SUNNETTECHNOLOGYINC.*",
                "SecureDownloadLtd.*",
                "SecurityGuarder.*",
                "SharpenedProductions.*",
                "Showell.*",
                "Simpledio.*",
                "SunrisePrivacyinc.*",
                "SymantecCorporation.*",
                "TIGERVPNSLTD.*",
                "ToolStyle.*",
                "ToolsAssistantLLC.*",
                "TweakingTechnologiesPvt.*",
                "UABMNTechnologijos.*",
                "VPNZone.*",
                "VirtualPulse.*",
                "WILDFIRETECHNOLOGYINC.*",
                "WellMadeVenturesGmbH.*",
                "WinZipComputing.*",
                "WuhanBamiTechnologyCo.*",
                "WuhanNetPowerTechnologyCo.*",
                "YellowElephantProductions.*",
                "ZhuhaiKingsoftOfficeSoftw.*",
                "excense.*",
                "softXpansion.*",
        };

        // Convert wildcards to regex patterns and compile them
        List<Pattern> patternsToRemove = Arrays.stream(appPackages)
                .map(pkg -> pkg.replace(".", "\\.").replace("*", ".*"))
                .map(Pattern::compile)
                .collect(Collectors.toList());

        // Match installed packages against the patterns
        List<String> packagesToRemove = installedPackages.stream()
                .filter(installedPackage -> patternsToRemove.stream().anyMatch(pattern -> pattern.matcher(installedPackage).matches()))
                .collect(Collectors.toList());

        // If no packages to remove, simply exit
        if (packagesToRemove.isEmpty()) {
            return;
        }

        // Create a thread pool and latch
        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(packagesToRemove.size());

        // Iterate through the list of packages to remove and remove them
        for (String appPackage : packagesToRemove) {
            executor.submit(() -> {
                try {
                    runCommand("PowerShell -ExecutionPolicy Unrestricted -Command \"Get-AppxPackage '"
                            + appPackage + "' | Remove-AppxPackage\"", false);
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all tasks to complete
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            ex.printStackTrace();
        }

        // Shut down the executor
        executor.shutdown();
        log.info("Removed " + packagesToRemove.size() + " installed bloatware apps in "
                + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Runs tweaks to Windows settings.
     */
    private static void runSettingsTweaks() {
        log.info("Tweaking Windows settings...");
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(4);

        executor.submit(() -> {
            // Fixes micro-stuttering in games.
            runCommand("bcdedit /set useplatformtick yes", true);
            runCommand("bcdedit /deletevalue useplatformclock", true);

            // Enables scheduled defrag.
            runCommand("schtasks /Change /ENABLE /TN \"\\Microsoft\\Windows\\Defrag\\ScheduledDefrag\"", true);

            // Disables various telemetry tasks.
            runCommand("schtasks /change /TN \"Microsoft\\Windows\\Application Experience\\ProgramDataUpdater\" /disable", true);
            runCommand("schtasks /change /TN \"Microsoft\\Windows\\Customer Experience Improvement Program\\Consolidator\" /disable", true);
            runCommand("schtasks /change /TN \"Microsoft\\Windows\\Customer Experience Improvement Program\\UsbCeip\" /disable", true);
            runCommand("schtasks /change /TN \"Microsoft\\Windows\\Application Experience\\StartupAppTask\" /disable", true);
            runCommand("schtasks /change /TN \"Microsoft\\Windows\\Application Experience\\Microsoft Compatibility Appraiser\" /disable", true);
            runCommand("schtasks /change /TN \"Microsoft\\Windows\\Windows Error Reporting\\QueueReporting\" /disable", true);
            runCommand("schtasks /change /TN \"Microsoft\\Windows\\Device Information\\Device\" /disable", true);
            runCommand("setx DOTNET_CLI_TELEMETRY_OPTOUT 1", true);
            runCommand("setx POWERSHELL_TELEMETRY_OPTOUT 1", true);

            // Deletes the controversial 'default0' user.
            runCommand("net user defaultuser0 /delete", true);

            // Clears the Windows product key from registry.
            runCommand("cscript.exe //nologo \"%SystemRoot%\\system32\\slmgr.vbs\" /cpky", true);

            // Resets network settings.
            runCommand("netsh winsock reset", true);
            runCommand("netsh int ip reset", true);
            runCommand("ipconfig /flushdns", true);

            // Re-registers ExplorerFrame.dll.
            runCommand("regsvr32 /s ExplorerFrame.dll", true);

            // Repairs broken Wi-Fi settings.
            deleteRegistryKey(WinReg.HKEY_CLASSES_ROOT, "CLSID\\{988248f3-a1ad-49bf-9170-676cbbc36ba3}");
            runCommand("netcfg -v -u dni_dne", true);
            latch.countDown();
        });

        executor.submit(() -> {
            // Disables NetBios for all interfaces.
            String baseKeyPath = "SYSTEM\\CurrentControlSet\\services\\NetBT\\Parameters\\Interfaces";
            List<String> subKeys = listSubKeys(WinReg.HKEY_LOCAL_MACHINE, baseKeyPath);

            for (String subKey : subKeys) {
                String fullPath = baseKeyPath + "\\" + subKey;
                setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, fullPath, "NetbiosOptions", 2);
            }
            latch.countDown();
        });

        executor.submit(() -> {
            // Resets Windows Media Player.
            runCommand("regsvr32 /s jscript.dll", false);
            runCommand("regsvr32 /s vbscript.dll", true);
            latch.countDown();
        });

        executor.submit(() -> {
            // Patches security vulnerabilities.
            if (!windowsUpdateInProgress) {
                String[] features = {
                        "SMB1Protocol",
                        "SMB1Protocol-Client",
                        "SMB1Protocol-Server",
                        "SMB1Protocol-Deprecation",
                        "TelnetClient",
                        "Internet-Explorer-Optional-amd64",
                        "MicrosoftWindowsPowerShellV2",
                        "MicrosoftWindowsPowerShellV2Root",
                };

                for (String feature : features) {
                    if (getCommandOutput("PowerShell -ExecutionPolicy Unrestricted -Command"
                            + " \"Get-WindowsOptionalFeature -FeatureName '" + feature + "' -Online | Select-Object -Property"
                            + " State\"", false, false).toString().contains("Enabled")) {
                        runCommand("DISM /Online /Disable-Feature /FeatureName:\"" + feature + "\" /NoRestart", false);
                    }
                }

                String[] capabilities = {
                        "Print.Fax.Scan~~~~*",
                        "Microsoft.Windows.WordPad~~~~*",
                        "MathRecognizer~~~~*",
                        "Browser.InternetExplorer~~~~*",
                        "App.StepsRecorder~~~~*"
                };

                // Check if the capability (any version) is enabled
                for (String capability : capabilities) {
                    if (getCommandOutput("PowerShell -ExecutionPolicy Unrestricted -Command"
                            + " \"Get-WindowsCapability -Name '" + capability + "' -Online | Where-Object State"
                            + " -eq 'Installed'\"", false, false).toString().contains("Installed")) {
                        runCommand("DISM /Online /Remove-Capability /CapabilityName:\"" + capability + "\" /NoRestart", false);
                    }
                }
            }

            latch.countDown();
        });

        // Wait for all tasks to complete
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            ex.printStackTrace();
        }

        // Shut down the executor
        executor.shutdown();
        log.info("Settings tweaks completed in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Runs various tweaks to Windows Defender and initiates a Quick Scan.
     */
    private static void runWindowsDefenderTweaks() {
        log.info("Tweaking Windows Defender...");
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(5);

        executor.submit(() -> {
            // Sets Windows Firewall to recommended settings
            runCommand("PowerShell -ExecutionPolicy Unrestricted -Command \"Set-NetFirewallProfile"
                    + " -Profile Domain,Private,Public -Enabled True\"", false);
            latch.countDown();
        });

        executor.submit(() -> {
            // Sets Windows Defender to recommended settings
            runCommand("PowerShell -ExecutionPolicy Unrestricted -Command \"Set-MpPreference"
                    + " -DisableRealtimeMonitoring 0"
                    + " -MAPSReporting 2"
                    + " -SubmitSamplesConsent 3"
                    + " -CloudBlockLevel 4"
                    + " -CloudExtendedTimeout 10"
                    + " -EnableNetworkProtection 1"
                    + " -DisableBehaviorMonitoring 0"
                    + " -PUAProtection 1"
                    + " -DisableBlockAtFirstSeen 0"
                    + " -DisableEmailScanning 0"
                    + " -DisableIOAVProtection 0"
                    + " -DisableScriptScanning 0"
                    + " -DisableArchiveScanning 0"
                    + " -DisableRemovableDriveScanning 0"
                    + " -DisableScanningNetworkFiles 0"
                    + " -DisableScanningMappedNetworkDrivesForFullScan 0"
                    + " -EnableLowCpuPriority 0"
                    + " -ScanAvgCPULoadFactor 50"
                    + " -SignatureBlobUpdateInterval 120"
                    + " -EnableFileHashComputation 0"
                    + " -LowThreatDefaultAction Block"
                    + " -ModerateThreatDefaultAction Clean"
                    + " -HighThreatDefaultAction Quarantine"
                    + " -SevereThreatDefaultAction Remove"
                    + "\"", false);
            latch.countDown();
        });

        executor.submit(() -> {
            // ASR: Block Adobe Reader from creating child processes
            // ASR: Block all Office applications from creating child processes
            // ASR: Block executable content from email client and webmail
            // ASR: Block execution of potentially obfuscated scripts
            // ASR: Block JavaScript or VBScript from launching downloaded executable content
            // ASR: Block Office applications from creating executable content
            // ASR: Block Office applications from injecting code into other processes
            // ASR: Block Office communication application from creating child processes
            // ASR: Block persistence through WMI event subscription
            // ASR: Block untrusted and unsigned processes that run from USB
            // ASR: Block Win32 API calls from Office macros
            // ASR: Use advanced protection against ransomware
            runCommand("PowerShell -ExecutionPolicy Unrestricted -Command \"Add-MpPreference"
                    + " -AttackSurfaceReductionRules_Ids "
                    + "7674ba52-37eb-4a4f-a9a1-f0f9a1619a2c,"
                    + "d4f940ab-401b-4efc-aadc-ad5f3c50688a,"
                    + "be9ba2d9-53ea-4cdc-84e5-9b1eeee46550,"
                    + "5beb7efe-fd9a-4556-801d-275e5ffc04cc,"
                    + "d3e037e1-3eb8-44c8-a917-57927947596d,"
                    + "3b576869-a4ec-4529-8536-b80a7769e899,"
                    + "75668c1f-73b5-4cf0-bb93-3ecf5cb7cc84,"
                    + "26190899-1602-49e8-8b27-eb1d0a1ce869,"
                    + "e6db77e5-3df2-4cf1-b95a-636979351e5b,"
                    + "b2b3f03d-6a65-4f7b-a9c7-1c7ef74a9ba4,"
                    + "92e97fa1-2edf-4476-bdd6-9dd0b4dddc7b,"
                    + "c1db55ab-c21a-4637-bb3f-a12568109d35"
                    + " -AttackSurfaceReductionRules_Actions Enabled\"", false);
            latch.countDown();
        });

        executor.submit(() -> {
            // ASR: Don't block credential stealing from the Windows local security authority subsystem
            // ASR: Don't block executable files from running unless they meet a prevalence, age, or trusted list criterion
            // ASR: Don't block process creations originating from PSExec and WMI commands
            runCommand("PowerShell -ExecutionPolicy Unrestricted -Command \"Add-MpPreference"
                    + " -AttackSurfaceReductionRules_Ids "
                    + "9e6c4e1f-7d60-472f-ba1a-a39ef669e4b2,"
                    + "01443614-cd74-433a-b99e-2ecdc07bfc25,"
                    + "d1e49aac-8f56-4280-b9ba-993a6d77406c"
                    + " -AttackSurfaceReductionRules_Actions Disabled\"", false);
            latch.countDown();
        });

        executor.submit(() -> {
            // ASR: Warn against abuse of exploited vulnerable signed drivers
            // ASR: Warn against Webshell creation for Servers
            runCommand("PowerShell -ExecutionPolicy Unrestricted -Command \"Add-MpPreference"
                    + " -AttackSurfaceReductionRules_Ids "
                    + "56a863a9-875e-4185-98a7-b882c64b5ce5,"
                    + "a8f5898e-1dc8-49a9-9878-85004b8a61e6"
                    + " -AttackSurfaceReductionRules_Actions Warn\"", false);
            latch.countDown();
        });

        // Wait for all tasks to complete
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            ex.printStackTrace();
        }

        // Shut down the executor
        executor.shutdown();
        log.info("Windows Defender tweaks completed in " + (System.currentTimeMillis() - startTime) + "ms.");

        // Updates Windows Defender signatures
        log.info("Updating Windows Defender signatures...");
        startTime = System.currentTimeMillis();
        runCommand("\"C:\\Program Files\\Windows Defender\\MpCmdRun.exe\" -SignatureUpdate", false);
        log.info("Windows Defender signatures updated in " + (System.currentTimeMillis() - startTime) + "ms.");

        // Runs a quick scan with Windows Defender
        log.info("Running a quick scan with Windows Defender...");
        startTime = System.currentTimeMillis();
        runCommand("\"C:\\Program Files\\Windows Defender\\MpCmdRun.exe\" -Scan -ScanType 1", false);
        log.info("Quick scan with Windows Defender completed in " + (System.currentTimeMillis() - startTime) + "ms.");
    }
}
