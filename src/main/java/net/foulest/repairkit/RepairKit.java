package net.foulest.repairkit;

import com.sun.jna.platform.win32.WinReg;
import lombok.Getter;
import lombok.Synchronized;
import net.foulest.repairkit.util.MessageUtil;
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
import java.util.stream.Collectors;

import static net.foulest.repairkit.util.CommandUtil.getCommandOutput;
import static net.foulest.repairkit.util.CommandUtil.runCommand;
import static net.foulest.repairkit.util.FileUtil.*;
import static net.foulest.repairkit.util.ProcessUtil.isProcessRunning;
import static net.foulest.repairkit.util.RegistryUtil.*;
import static net.foulest.repairkit.util.SoundUtil.playSound;
import static net.foulest.repairkit.util.SwingUtil.*;

public class RepairKit {

    private static final Set<String> SUPPORTED_OS_NAMES = new HashSet<>(Arrays.asList("Windows 10", "Windows 11"));

    private static final JFrame frame = new JFrame("RepairKit");
    private static final JPanel panelMain = new JPanel(null);

    @Getter
    private static boolean debugMode = false;
    private static boolean windowsUpdateInProgress = false;

    /**
     * The main method of the program.
     *
     * @param args The program's arguments.
     */
    public static void main(String @NotNull [] args) {
        checkForDebugMode(args);
        checkOperatingSystemCompatibility();
        setupShutdownHook();
        checkForWindowsUpdate();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = createMainFrame();
            frame.setVisible(true);
        });
    }

    /**
     * Checks if debug mode is enabled.
     *
     * @param args The program's arguments.
     */
    @Synchronized
    private static void checkForDebugMode(String @NotNull [] args) {
        // Check if the "/debug" flag is present in the launch arguments
        for (String arg : args) {
            if (arg.equals("/debug")) {
                debugMode = true;
                break;
            }
        }
    }

    /**
     * Checks if the user's operating system is supported.
     */
    private static void checkOperatingSystemCompatibility() {
        MessageUtil.debug("Checking operating system compatibility...");
        String osName = System.getProperty("os.name");

        if (!SUPPORTED_OS_NAMES.contains(osName)) {
            JOptionPane.showMessageDialog(null,
                    "Your operating system" + (osName != null ? ", " + osName + ", " : " ")
                            + "is outdated, unknown, or not Windows based."
                            + "\nThis software only works on up-to-date Windows operating systems."
                    , "Incompatible Operating System", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        MessageUtil.debug("Operating system (" + System.getProperty("os.name") + ") is compatible.");
    }

    /**
     * Checks if Windows Update is running.
     */
    private static void checkForWindowsUpdate() {
        MessageUtil.debug("Checking for Windows Update...");

        // Checks if Windows Update is running.
        // Windows Update causes problems with DISM.
        if (isProcessRunning("WmiPrvSE.exe")
                && isProcessRunning("TiWorker.exe")
                && isProcessRunning("TrustedInstaller.exe")
                && isProcessRunning("wuauclt.exe")) {
            windowsUpdateInProgress = true;
            MessageUtil.debug("Windows Update is running; warning user...");
            JOptionPane.showMessageDialog(null, "Windows Update is running on your system."
                            + "\nCertain tweaks will not be applied until the Windows Update is finished."
                    , "Software Warning", JOptionPane.WARNING_MESSAGE);
        }

        MessageUtil.debug("Windows Update check complete.");
    }

    /**
     * Sets the program's shutdown hook.
     */
    private static void setupShutdownHook() {
        MessageUtil.debug("Setting up shutdown hook...");

        // Clears the files used by RepairKit on shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                runCommand("rd /s /q " + tempDirectory.getPath(), false))
        );

        MessageUtil.debug("Shutdown hook set up.");
    }

    /**
     * Creates the main frame of the program.
     *
     * @return The main frame of the program.
     */
    private static JFrame createMainFrame() {
        MessageUtil.debug("Creating main frame...");

        // Sets the program's GUI elements.
        setGUIElements();

        // Deletes pre-existing RepairKit files.
        runCommand("rd /s /q " + tempDirectory.getPath(), false);

        // Checks if Medal is installed.
        checkForMedal();

        // Creates the main frame.
        frame.setContentPane(panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        MessageUtil.debug("Main frame created.");
        return frame;
    }

    /**
     * Sets the program's GUI elements.
     */
    private static void setGUIElements() {
        MessageUtil.debug("Setting up GUI elements...");
        setMainPanel();
        setLabels();
        setRepairButtons();
        setAppButtons();
        setLinkButtons();
        MessageUtil.debug("GUI elements set up.");
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
                        deleteSystemPolicies();

                        // Installs 7-Zip and uninstalls other programs.
                        install7Zip();

                        // Create a new executor
                        ExecutorService executor = Executors.newWorkStealingPool();
                        CountDownLatch latch = new CountDownLatch(6);

                        // Clean junk files
                        executor.submit(() -> {
                            try {
                                cleanJunkFiles();
                                latch.countDown();
                            } catch (Exception ex) {
                                MessageUtil.printException(ex);
                            }
                        });

                        // Repair WMI repository
                        executor.submit(() -> {
                            try {
                                repairWMIRepository();
                                latch.countDown();
                            } catch (Exception ex) {
                                MessageUtil.printException(ex);
                            }
                        });

                        // Service tweaks
                        executor.submit(() -> {
                            try {
                                runServiceTweaks();
                                latch.countDown();
                            } catch (Exception ex) {
                                MessageUtil.printException(ex);
                            }
                        });

                        // Remove stock apps
                        executor.submit(() -> {
                            try {
                                removeStockApps();
                                latch.countDown();
                            } catch (Exception ex) {
                                MessageUtil.printException(ex);
                            }
                        });

                        // Registry tweaks
                        executor.submit(() -> {
                            try {
                                runRegistryTweaks();
                                latch.countDown();
                            } catch (Exception ex) {
                                MessageUtil.printException(ex);
                            }
                        });

                        // Settings tweaks
                        executor.submit(() -> {
                            try {
                                runSettingsTweaks();
                                latch.countDown();
                            } catch (Exception ex) {
                                MessageUtil.printException(ex);
                            }
                        });

                        // Wait for all tasks to complete
                        try {
                            latch.await();
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                            MessageUtil.printException(ex);
                        }

                        // Shut down the executor
                        executor.shutdown();

                        // Displays a message dialog
                        playSound("win.sound.exclamation");
                        JOptionPane.showMessageDialog(null, "System issues repaired successfully.", "Finished", JOptionPane.QUESTION_MESSAGE);
                    } catch (Exception ex) {
                        MessageUtil.printException(ex);
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
                        MessageUtil.printException(ex);
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
        JButton buttonWinDirStat = createAppButton("TreeSize", "Displays system files organized by size.",
                "TreeSize.zip", "TreeSize.exe", true, tempDirectory.getPath());
        buttonWinDirStat.setBounds(5, 130, 152, 25);
        addComponents(panelMain, buttonWinDirStat);

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
        JButton buttonEmsisoft = createAppButton("Emsisoft Scan", "Scans your system for malware.",
                "Emsisoft.zip", "Emsisoft.exe", true, tempDirectory.getPath());
        buttonEmsisoft.setBounds(162, 160, 152, 25);
        addComponents(panelMain, buttonEmsisoft);

        // Process Explorer Button
        JButton buttonProcessExplorer = createAppButton("Process Explorer", "Displays system processes.",
                "ProcessExplorer.zip", "ProcessExplorer.exe", "/accepteula",
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

        // NVCleanstall Button
        JButton buttonNVCleanstallExtension = createLinkButton("NVCleanstall",
                "A lightweight NVIDIA graphics card driver updater.",
                "start https://techpowerup.com/download/techpowerup-nvcleanstall");
        buttonNVCleanstallExtension.setBounds(5, 220, 152, 25);
        addComponents(panelMain, buttonNVCleanstallExtension);

        // Apps & Features Button
        JButton buttonAppsFeatures = createLinkButton("Apps & Features",
                "start ms-settings:appsfeatures");
        buttonAppsFeatures.setBounds(5, 280, 152, 25);
        addComponents(panelMain, buttonAppsFeatures);

        // Windows Update Button
        JButton buttonCheckForUpdates = createLinkButton("Windows Update",
                "start ms-settings:windowsupdate");
        buttonCheckForUpdates.setBounds(162, 280, 152, 25);
        addComponents(panelMain, buttonCheckForUpdates);

        // Task Manager Button
        JButton buttonTaskManager = createLinkButton("Task Manager",
                "taskmgr");
        buttonTaskManager.setBounds(5, 310, 152, 25);
        addComponents(panelMain, buttonTaskManager);

        // Windows Defender Button
        JButton buttonSecurity = createLinkButton("Windows Defender",
                "start windowsdefender:");
        buttonSecurity.setBounds(162, 310, 152, 25);
        addComponents(panelMain, buttonSecurity);
    }

    /**
     * Checks if Medal is installed.
     * Medal causes issues with Desktop Window Manager.
     */
    private static void checkForMedal() {
        MessageUtil.debug("Checking for Medal...");

        if (isProcessRunning("medal.exe")) {
            MessageUtil.debug("Medal is running; warning user...");
            JOptionPane.showMessageDialog(null,
                    "Warning: Medal is installed and running on your system."
                            + "\nMedal causes issues with Desktop Windows Manager, which affects system performance."
                            + "\nFinding an alternative to Medal, such as Shadowplay or AMD ReLive is recommended.",
                    "Software Warning", JOptionPane.ERROR_MESSAGE);
        }

        MessageUtil.debug("Medal check complete.");
    }

    /**
     * Cleans junk files using CCleaner.
     */
    private static void cleanJunkFiles() {
        long startTime = System.currentTimeMillis();

        // Kills CCleaner
        MessageUtil.debug("Cleaning junk files... (1/4)");
        runCommand("taskkill /F /IM CCleaner.exe", false);
        runCommand("rd /s /q \"" + tempDirectory + "\\CCleaner\"", false);

        // Extracts CCleaner
        MessageUtil.debug("Cleaning junk files... (2/4)");
        try (InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/CCleaner.zip")) {
            saveFile(Objects.requireNonNull(input), "CCleaner.zip", true);
            unzipFile(tempDirectory + "\\CCleaner.zip", tempDirectory.getPath() + "\\CCleaner");
        } catch (IOException ex) {
            MessageUtil.printException(ex);
        }

        // Runs CCleaner
        MessageUtil.debug("Cleaning junk files... (3/4)");
        runCommand(tempDirectory + "\\CCleaner\\CCleaner /AUTO", false);

        // Restarts Explorer
        MessageUtil.debug("Cleaning junk files... (4/4)");
        runCommand("taskkill /F /IM explorer.exe", false);
        runCommand("start explorer.exe", false);

        MessageUtil.debug("Cleaned junk files in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Deletes any existing system policies.
     */
    private static void deleteSystemPolicies() {
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(4);

        executor.submit(() -> {
            MessageUtil.debug("Deleting system policies... (1/4)");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Policies\\Microsoft\\MMC");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Policies\\Microsoft\\Windows\\System");
            deleteRegistryKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Internet Explorer");
            deleteRegistryKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Google\\Chrome");
            latch.countDown();
        });

        executor.submit(() -> {
            MessageUtil.debug("Deleting system policies... (2/4)");
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
            MessageUtil.debug("Deleting system policies... (3/4)");
            setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon", "Shell", "explorer.exe");
            setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon", "Shell", "explorer.exe");
            setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon", "Userinit", "C:\\Windows\\system32\\userinit.exe,");
            latch.countDown();
        });

        executor.submit(() -> {
            MessageUtil.debug("Deleting system policies... (4/4)");
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
            MessageUtil.printException(ex);
        }

        // Shut down the executor
        executor.shutdown();
        MessageUtil.debug("Deleted system policies in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Installs 7-Zip and uninstalls other archivers.
     */
    private static void install7Zip() {
        MessageUtil.debug("Checking for 7-Zip and other .zip programs...");
        Path sevenZipPath = Paths.get("C:\\Program Files\\7-Zip\\7zFM.exe");
        Path tempPath = Paths.get(tempDirectory + "\\7-Zip.exe");

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
                new UninstallData("C:\\Program Files (x86)\\B1 Free Archiver", null)
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
                            JOptionPane.showMessageDialog(null,
                                    "Please manually uninstall the program in " + data.directoryPath + " via Installed Apps.",
                                    "Error Uninstalling", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

                runCommand("del /s /q \"" + System.getenv("APPDATA")
                        + "\\Microsoft\\Internet Explorer\\Quick Launch\\User Pinned\\TaskBar\\Tombstones\\Bandizip.lnk\"", true);
                runCommand("rd /s /q \"%AppData%\\PeaZip", true);

                // Installs 7-Zip.
                if (shouldInstall7Zip) {
                    if (!Files.exists(tempPath)) {
                        try (InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/7-Zip.exe")) {
                            saveFile(Objects.requireNonNull(input), "7-Zip.exe", false);
                        } catch (IOException ex) {
                            MessageUtil.printException(ex);
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
        MessageUtil.debug("Repairing WMI repository...");
        long startTime = System.currentTimeMillis();

        if (getCommandOutput("winmgmt /verifyrepository", false, false).toString().contains("not consistent")
                && getCommandOutput("winmgmt /salvagerepository", false, false).toString().contains("not consistent")) {
            runCommand("winmgmt /resetrepository", false);
        }

        MessageUtil.debug("Repaired WMI repository in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Runs tweaks to the Windows registry.
     */
    private static void runRegistryTweaks() {
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(19);

        // Disables telemetry and annoyances.
        executor.submit(() -> {
            MessageUtil.debug("Running registry tweaks... (1/20)");
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
            MessageUtil.debug("Running registry tweaks... (2/20)");
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
            MessageUtil.debug("Running registry tweaks... (3/20)");
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
            MessageUtil.debug("Running registry tweaks... (4/20)");
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
            MessageUtil.debug("Running registry tweaks... (5/20)");
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
            MessageUtil.debug("Running registry tweaks... (6/20)");
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CLSID\\{645FF040-5081-101B-9F08-00AA002F954E}\\DefaultIcon", "(Default)", "C:\\Windows\\System32\\imageres.dll,-54");
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CLSID\\{645FF040-5081-101B-9F08-00AA002F954E}\\DefaultIcon", "empty", "C:\\Windows\\System32\\imageres.dll,-55");
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CLSID\\{645FF040-5081-101B-9F08-00AA002F954E}\\DefaultIcon", "full", "C:\\Windows\\System32\\imageres.dll,-54");
            latch.countDown();
        });

        // Enables updates for other Microsoft products.
        executor.submit(() -> {
            MessageUtil.debug("Running registry tweaks... (7/20)");
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\WindowsUpdate\\UX\\Settings", "AllowMUUpdateService", 1);
            latch.countDown();
        });

        // Disables certain File Explorer features.
        executor.submit(() -> {
            MessageUtil.debug("Running registry tweaks... (8/20)");
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
            MessageUtil.debug("Running registry tweaks... (9/20)");
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
            MessageUtil.debug("Running registry tweaks... (10/20)");
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Feeds", "EnableFeeds", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Feeds", "ShellFeedsTaskbarViewMode", 2);
            latch.countDown();
        });

        // Disables Game DVR.
        executor.submit(() -> {
            MessageUtil.debug("Running registry tweaks... (11/20)");
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\GameDVR", "AppCaptureEnabled", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SYSTEM\\GameConfigStore", "GameDVR_Enabled", 0);
            latch.countDown();
        });

        // Disables lock screen toasts.
        executor.submit(() -> {
            MessageUtil.debug("Running registry tweaks... (12/20)");
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Notifications\\Settings", "NOC_GLOBAL_SETTING_ALLOW_TOASTS_ABOVE_LOCK", 0);
            setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\PushNotifications", "LockScreenToastEnabled", 0);
            latch.countDown();
        });

        // Enables Storage Sense.
        executor.submit(() -> {
            MessageUtil.debug("Running registry tweaks... (13/20)");
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
            MessageUtil.debug("Running registry tweaks... (14/20)");
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\DirectX\\UserGpuPreferences", "DirectXUserGlobalSettings", "VRROptimizeEnable=1");
            latch.countDown();
        });

        // Modifies Windows networking settings.
        executor.submit(() -> {
            MessageUtil.debug("Running registry tweaks... (15/20)");
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\LanmanServer\\Parameters", "IRPStackSize", 30);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters", "DefaultTTL", 64);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters", "MaxUserPort", 65534);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters", "Tcp1323Opts", 1);
            latch.countDown();
        });

        // Disables sticky keys.
        executor.submit(() -> {
            MessageUtil.debug("Running registry tweaks... (16/20)");
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Accessibility\\ToggleKeys", "Flags", "58");
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Accessibility\\StickyKeys", "Flags", "506");
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Accessibility\\Keyboard Response", "Flags", "122");
            latch.countDown();
        });

        // Disables mouse acceleration.
        executor.submit(() -> {
            MessageUtil.debug("Running registry tweaks... (17/20)");
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Mouse", "MouseSpeed", "0");
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Mouse", "MouseThreshold1", "0");
            setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Mouse", "MouseThreshold2", "0");
            latch.countDown();
        });

        // Restores the keyboard layout.
        executor.submit(() -> {
            MessageUtil.debug("Running registry tweaks... (18/20)");
            deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Keyboard Layout", "Scancode Map");
            latch.countDown();
        });

        // Fixes a battery visibility issue.
        executor.submit(() -> {
            MessageUtil.debug("Running registry tweaks... (19/20)");
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Power", "EnergyEstimationEnabled", 1);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Power", "EnergyEstimationDisabled", 0);
            setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Power", "UserBatteryDischargeEstimator", 0);
            latch.countDown();
        });

        // Sets certain services to start automatically.
        executor.submit(() -> {
            MessageUtil.debug("Running registry tweaks... (20/20)");
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
            MessageUtil.printException(ex);
        }

        // Shut down the executor
        executor.shutdown();
        MessageUtil.debug("Registry tweaks completed in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Runs various tweaks to the Windows services.
     */
    private static void runServiceTweaks() {
        long startTime = System.currentTimeMillis();

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

        // Create a thread pool and latch
        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(serviceList.size());

        // Iterate through the list of services
        for (String[] serviceInfo : serviceList) {
            executor.submit(() -> {
                try {
                    MessageUtil.debug("Tweaking service: " + serviceInfo[1] + "...");
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
            MessageUtil.printException(ex);
        }

        // Shut down the executor
        executor.shutdown();
        MessageUtil.debug("Tweaked " + serviceList.size() + " services in "
                + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Removes various useless stock Windows apps.
     */
    private static void removeStockApps() {
        long startTime = System.currentTimeMillis();
        String command = "PowerShell -ExecutionPolicy Unrestricted -Command \"(Get-AppxPackage).ForEach({ $_.Name })\"";
        List<String> output = getCommandOutput(command, false, false);
        Set<String> installedPackages = new HashSet<>(output);

        String[] appPackages = {
                "Microsoft.3DBuilder",
                "Microsoft.Microsoft3DViewer",
                "Microsoft.BingWeather",
                "Microsoft.BingSports",
                "Microsoft.BingNews",
                "Microsoft.BingFinance",
                "Microsoft.MicrosoftOfficeHub",
                "Microsoft.Office.OneNote",
                "Microsoft.Office.Sway",
                "Microsoft.WindowsPhone",
                "Microsoft.Windows.Phone",
                "Microsoft.CommsPhone",
                "Microsoft.YourPhone",
                "Microsoft.549981C3F5F10",
                "Microsoft.GetHelp",
                "Microsoft.Getstarted",
                "Microsoft.Messaging",
                "Microsoft.MixedReality.Portal",
                "Microsoft.MSPaint",
                "Microsoft.WindowsMaps",
                "Microsoft.People",
                "Microsoft.Wallet",
                "Microsoft.Print3D",
                "Microsoft.OneConnect",
                "Microsoft.MicrosoftSolitaireCollection",
                "Microsoft.SkypeApp",
                "Microsoft.GroupMe10",
                "Microsoft.Advertising.Xaml",
                "Microsoft.RemoteDesktop",
                "Microsoft.NetworkSpeedTest",
                "Microsoft.Todos",
                "ShazamEntertainmentLtd.Shazam",
                "king.com.CandyCrushSaga",
                "king.com.CandyCrushSodaSaga",
                "Flipboard.Flipboard",
                "9E2F88E3.Twitter",
                "ClearChannelRadioDigital.iHeartRadio",
                "D5EA27B7.Duolingo-LearnLanguagesforFree",
                "PandoraMediaInc.29680B314EFC2",
                "46928bounde.EclipseManager",
                "ActiproSoftwareLLC.562882FEEB491",
                "Microsoft.MicrosoftStickyNotes",
                "Microsoft.WindowsSoundRecorder",
                "Microsoft.WindowsFeedbackHub"
        };

        List<String> packagesToRemove = Arrays.stream(appPackages)
                .filter(installedPackages::contains)
                .collect(Collectors.toList());

        // If no packages to remove, simply exit
        if (packagesToRemove.isEmpty()) {
            MessageUtil.debug("No stock apps found to remove.");
            return;
        }

        // Create a thread pool and latch
        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(packagesToRemove.size());

        // Iterate through the list of packages to remove and remove them
        for (String appPackage : packagesToRemove) {
            executor.submit(() -> {
                try {
                    MessageUtil.debug("Removing stock app: " + appPackage + "...");
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
            MessageUtil.printException(ex);
        }

        // Shut down the executor
        executor.shutdown();
        MessageUtil.debug("Removed " + packagesToRemove.size() + " stock apps in "
                + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Runs tweaks to Windows settings.
     */
    private static void runSettingsTweaks() {
        long startTime = System.currentTimeMillis();

        // Create a thread pool and latch
        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(4);

        executor.submit(() -> {
            // Fixes micro-stuttering in games.
            MessageUtil.debug("Running settings tweaks... (1/11)");
            runCommand("bcdedit /set useplatformtick yes", true);
            runCommand("bcdedit /deletevalue useplatformclock", true);

            // Enables scheduled defrag.
            MessageUtil.debug("Running settings tweaks... (2/11)");
            runCommand("schtasks /Change /ENABLE /TN \"\\Microsoft\\Windows\\Defrag\\ScheduledDefrag\"", true);

            // Disables various telemetry tasks.
            MessageUtil.debug("Running settings tweaks... (3/11)");
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
            MessageUtil.debug("Running settings tweaks... (4/11)");
            runCommand("net user defaultuser0 /delete", true);

            // Clears the Windows product key from registry.
            MessageUtil.debug("Running settings tweaks... (5/11)");
            runCommand("cscript.exe //nologo \"%SystemRoot%\\system32\\slmgr.vbs\" /cpky", true);

            // Resets network settings.
            MessageUtil.debug("Running settings tweaks... (6/11)");
            runCommand("netsh winsock reset", true);
            runCommand("netsh int ip reset", true);
            runCommand("ipconfig /flushdns", true);

            // Re-registers ExplorerFrame.dll.
            MessageUtil.debug("Running settings tweaks... (7/11)");
            runCommand("regsvr32 /s ExplorerFrame.dll", true);

            // Repairs broken Wi-Fi settings.
            MessageUtil.debug("Running settings tweaks... (8/11)");
            deleteRegistryKey(WinReg.HKEY_CLASSES_ROOT, "CLSID\\{988248f3-a1ad-49bf-9170-676cbbc36ba3}");
            runCommand("netcfg -v -u dni_dne", true);
            latch.countDown();
        });

        executor.submit(() -> {
            // Disables NetBios for all interfaces.
            MessageUtil.debug("Running settings tweaks... (9/11)");
            final String baseKeyPath = "SYSTEM\\CurrentControlSet\\services\\NetBT\\Parameters\\Interfaces";
            List<String> subKeys = listSubKeys(WinReg.HKEY_LOCAL_MACHINE, baseKeyPath);

            for (String subKey : subKeys) {
                String fullPath = baseKeyPath + "\\" + subKey;
                setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, fullPath, "NetbiosOptions", 2);
            }
            latch.countDown();
        });

        executor.submit(() -> {
            // Resets Windows Media Player.
            MessageUtil.debug("Running settings tweaks... (10/11)");
            runCommand("regsvr32 /s jscript.dll", false);
            runCommand("regsvr32 /s vbscript.dll", true);
            latch.countDown();
        });

        executor.submit(() -> {
            // Patches security vulnerabilities.
            if (!windowsUpdateInProgress) {
                MessageUtil.debug("Running settings tweaks... (11/11)");

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
                            + " State\"", false, false).contains("Enabled")) {
                        MessageUtil.debug("Disabling feature: " + feature + "...");
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

                for (String capability : capabilities) {
                    // Check if the capability (any version) is enabled
                    if (getCommandOutput("PowerShell -ExecutionPolicy Unrestricted -Command"
                            + " \"Get-WindowsCapability -Name '" + capability + "' -Online | Where-Object State"
                            + " -eq 'Installed'\"", false, false).contains("Installed")) {
                        MessageUtil.debug("Removing capability: " + capability + "...");
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
            MessageUtil.printException(ex);
        }

        // Shut down the executor
        executor.shutdown();
        MessageUtil.debug("Settings tweaks completed in " + (System.currentTimeMillis() - startTime) + "ms.");
    }
}
