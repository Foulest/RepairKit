package net.foulest.repairkit;

import com.sun.jna.platform.win32.WinReg;
import lombok.NonNull;
import net.foulest.repairkit.util.type.UninstallData;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static net.foulest.repairkit.util.CommandUtil.getCommandOutput;
import static net.foulest.repairkit.util.CommandUtil.runCommand;
import static net.foulest.repairkit.util.FileUtil.saveFile;
import static net.foulest.repairkit.util.FileUtil.unzipFile;
import static net.foulest.repairkit.util.RegistryUtil.*;
import static net.foulest.repairkit.util.SoundUtil.playSound;
import static net.foulest.repairkit.util.SwingUtil.*;

@SuppressWarnings("CallToPrintStackTrace")
public class RepairKit {

    private static final Set<String> SUPPORTED_OS_NAMES = new HashSet<>(Arrays.asList("Windows 10", "Windows 11"));
    private static final String OUTDATED_OS_MESSAGE = "Your operating system, %s, is outdated, unknown, or not Windows based."
            + "\nThis software only works on up-to-date Windows operating systems.";

    public static final String programName = "RepairKit";
    public static final JPanel panelMain = new JPanel(null);
    public static final JFrame frame = new JFrame(programName);
    public static final File tempDirectory = new File(System.getenv("TEMP") + "\\" + programName);

    /**
     * The main method of the program.
     *
     * @param args The program's arguments.
     */
    public static void main(String[] args) {
        checkOperatingSystemCompatibility();
        setupShutdownHook();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = createMainFrame();
            frame.setVisible(true);
        });
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
    public static void setGUIElements() {
        setMainPanel();
        setLabels();
        setRepairButtons();
        setAppButtons();
        setLinkButtons();
    }

    /**
     * Sets the program's shutdown hook.
     */
    private static void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            runCommand("taskkill /F /IM Everything.exe", false); // Everything process
            runCommand("rd /s /q " + tempDirectory.getPath(), false); // Temporary files
        }));
    }

    /**
     * Sets the main panel of the program.
     */
    public static void setMainPanel() {
        panelMain.setPreferredSize(new Dimension(320, 355));
        panelMain.setBackground(new Color(43, 43, 43));
    }

    /**
     * Sets the program's labels.
     */
    public static void setLabels() {
        // Title Label
        JLabel labelTitle = createLabel(programName + " by Foulest",
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
     * Sets the program's app buttons.
     */
    public static void setAppButtons() {
        // FanControl Button
        JButton buttonFanControl = new JButton("FanControl");
        buttonFanControl.setToolTipText("Allows control over system fans.");
        buttonFanControl.setBackground(new Color(200, 200, 200));
        buttonFanControl.setBounds(5, 100, 152, 25);
        addComponents(panelMain, buttonFanControl);
        buttonFanControl.addActionListener(actionEvent -> {
            try {
                String fanControlPath = getCommandOutput("PowerShell -ExecutionPolicy Unrestricted -Command \"Get-Process -Name FanControl | Select-Object Path | ft -hidetableheaders\"", false, false).toString();
                fanControlPath = fanControlPath.replace("[, ", "");
                fanControlPath = fanControlPath.replace(", , ]", "");

                if (fanControlPath.contains("Cannot find a process with the name")) {
                    launchApplication("FanControl.zip", "\\FanControl.exe", true, System.getenv("APPDATA") + "\\FanControl");
                } else {
                    runCommand("start \"\" \"" + fanControlPath + "\"", false);
                }
            } catch (Exception ignored) {
            }
        });

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
                "Everything.exe", "Everything.exe", false, tempDirectory.getPath());
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
    }

    /**
     * Sets the program's link buttons.
     */
    public static void setLinkButtons() {
        // uBlock Origin Button
        JButton buttonUBlockOrigin = createLinkButton("uBlock Origin",
                "Blocks ads and trackers across all websites.",
                "start https://ublockorigin.com");
        buttonUBlockOrigin.setBounds(5, 190, 152, 25);
        addComponents(panelMain, buttonUBlockOrigin);

        // MS Defender Extension Button
        JButton buttonDefenderExtension = createLinkButton("Defender Extension",
                "Blocks malicious websites and phishing attacks.",
                "start https://chrome.google.com/webstore/detail/microsoft-defender-browse/bkbeeeffjjeopflfhgeknacdieedcoml");
        buttonDefenderExtension.setBounds(162, 190, 152, 25);
        addComponents(panelMain, buttonDefenderExtension);

        // NVCleanstall Button
        JButton buttonNVCleanstallExtension = createLinkButton("NVCleanstall",
                "A lightweight NVIDIA graphics card driver updater.",
                "start https://www.techpowerup.com/download/techpowerup-nvcleanstall");
        buttonNVCleanstallExtension.setBounds(5, 220, 152, 25);
        addComponents(panelMain, buttonNVCleanstallExtension);

        // Intel DSA Button
        JButton buttonIntelDSA = createLinkButton("Intel DSA Software",
                "Downloads Intel drivers & keeps them up-to-date.",
                "start https://www.intel.com/content/www/us/en/support/detect.html");
        buttonIntelDSA.setBounds(162, 220, 152, 25);
        addComponents(panelMain, buttonIntelDSA);

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
     * Sets the program's repair buttons.
     */
    public static void setRepairButtons() {
        // Run Automatic Repairs Button
        JButton buttonRepairs = new JButton("Run Automatic Repairs");
        buttonRepairs.setToolTipText("Performs various fixes and maintenance tasks.");
        buttonRepairs.setBackground(new Color(200, 200, 200));
        buttonRepairs.setBounds(5, 30, 310, 35);
        addComponents(panelMain, buttonRepairs);
        buttonRepairs.addActionListener(actionEvent -> {
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

                // Remove stock apps
                executor.submit(() -> {
                    try {
                        removeStockApps();
                        latch.countDown();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                // Registry tweaks
                executor.submit(() -> {
                    try {
                        runRegistryTweaks();
                        latch.countDown();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                // Settings tweaks
                executor.submit(() -> {
                    try {
                        runSettingsTweaks();
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
            } catch (Exception ignored) {
            }
        });
    }

    /**
     * Launches an application.
     *
     * @param appResource    The name of the application's resource.
     * @param appExecutable  The name of the application's executable.
     * @param isZipped       Whether the application is zipped or not.
     * @param extractionPath The path to extract the application to.
     */
    public static void launchApplication(@NonNull String appResource, @NonNull String appExecutable,
                                         boolean isZipped, @NonNull String extractionPath) {
        Path path = Paths.get(extractionPath, appExecutable);

        if (!Files.exists(path)) {
            InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/" + appResource);
            saveFile(input, appResource, false);

            if (isZipped) {
                unzipFile(tempDirectory + "\\" + appResource, extractionPath);
            }
        }

        runCommand(path.toString(), true);
    }

    /**
     * Checks if the user's operating system is supported.
     */
    private static void checkOperatingSystemCompatibility() {
        String osName = System.getProperty("os.name");

        if (!SUPPORTED_OS_NAMES.contains(osName)) {
            String errorMessage = String.format(OUTDATED_OS_MESSAGE, (osName != null ? osName : "unknown"));
            JOptionPane.showMessageDialog(null, errorMessage, "Incompatible Operating System", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    /**
     * Cleans junk files using CCleaner.
     */
    private static void cleanJunkFiles() {
        long startTime = System.currentTimeMillis();

        // Kills CCleaner
        runCommand("taskkill /F /IM CCleaner.exe", false);
        runCommand("rd /s /q \"" + tempDirectory + "\\CCleaner\"", false);

        // Extracts CCleaner
        InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/" + "CCleaner.zip");
        saveFile(input, "CCleaner.zip", true);
        unzipFile(tempDirectory + "\\CCleaner.zip", tempDirectory.getPath() + "\\CCleaner");

        // Runs CCleaner
        runCommand(tempDirectory + "\\CCleaner\\CCleaner /AUTO", false);

        // Restarts Explorer
        runCommand("taskkill /F /IM explorer.exe", false);
        runCommand("start explorer.exe", false);

        System.out.println("Cleaned junk files in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Deletes any existing system policies.
     */
    private static void deleteSystemPolicies() {
        long startTime = System.currentTimeMillis();

        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(4);

        executor.submit(() -> {
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Policies\\Microsoft\\MMC");
            deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Policies\\Microsoft\\Windows\\System");
            deleteRegistryKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Internet Explorer");
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

        System.out.println("Deleted system policies in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Installs 7-Zip and uninstalls other archivers.
     */
    private static void install7Zip() {
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

                runCommand("del /s /q \"" + System.getenv("APPDATA") + "\\Microsoft\\Internet Explorer\\Quick Launch\\User Pinned\\TaskBar\\Tombstones\\Bandizip.lnk\"", true);
                runCommand("rd /s /q \"%AppData%\\PeaZip", true);

                // Installs 7-Zip.
                if (shouldInstall7Zip) {
                    if (!Files.exists(tempPath)) {
                        InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/7-Zip.exe");
                        saveFile(input, "7-Zip.exe", false);
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
        long startTime = System.currentTimeMillis();

        if (getCommandOutput("winmgmt /verifyrepository", false, false).toString().contains("not consistent")) {
            if (getCommandOutput("winmgmt /salvagerepository", false, false).toString().contains("not consistent")) {
                runCommand("winmgmt /resetrepository", false);
            }
        }

        System.out.println("Repaired WMI repository in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Runs tweaks to the Windows registry.
     */
    private static void runRegistryTweaks() {
        long startTime = System.currentTimeMillis();

        // Create a thread pool and latch
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

        System.out.println("Tweaked Windows in " + (System.currentTimeMillis() - startTime) + "ms.");
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

        System.out.println("Tweaked " + serviceList.size() + " services in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Removes various useless stock Windows apps.
     */
    private static void removeStockApps() {
        long startTime = System.currentTimeMillis();

        // This function will get a list of all installed app packages
        String command = "PowerShell -ExecutionPolicy Unrestricted -Command \"Get-AppxPackage | Select-Object -ExpandProperty Name\"";
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
            System.out.println("No stock apps to remove");
            return;
        }

        // Create a thread pool and latch
        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(packagesToRemove.size());

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

        System.out.println("Removed " + packagesToRemove.size() + " stock apps in " + (System.currentTimeMillis() - startTime) + "ms.");
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
            // Patches security vulnerabilities.
            runCommand("DISM /Online /Disable-Feature /FeatureName:\"SMB1Protocol\" /NoRestart", false);
            runCommand("DISM /Online /Disable-Feature /FeatureName:\"SMB1Protocol-Client\" /NoRestart", false);
            runCommand("DISM /Online /Disable-Feature /FeatureName:\"SMB1Protocol-Server\" /NoRestart", false);
            runCommand("DISM /Online /Disable-Feature /FeatureName:\"SMB1Protocol-Deprecation\" /NoRestart", false);
            runCommand("DISM /Online /Disable-Feature /FeatureName:\"TelnetClient\" /NoRestart", false);
            runCommand("DISM /Online /Disable-Feature /FeatureName:\"Internet-Explorer-Optional-amd64\" /NoRestart", false);
            runCommand("DISM /Online /Disable-Feature /FeatureName:\"MicrosoftWindowsPowerShellV2\" /NoRestart", false);
            runCommand("DISM /Online /Disable-Feature /FeatureName:\"MicrosoftWindowsPowerShellV2Root\" /NoRestart", false);
            latch.countDown();
        });

        executor.submit(() -> {
            // Disables NetBios for all interfaces.
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
            runCommand("regsvr32 /s jscript.dll", false);
            runCommand("regsvr32 /s vbscript.dll", true);
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

        System.out.println("Settings tweaks completed in " + (System.currentTimeMillis() - startTime) + "ms.");
    }
}
