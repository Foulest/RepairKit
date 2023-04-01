package net.foulest.repairkit;

import com.sun.jna.platform.win32.WinReg;
import net.foulest.repairkit.util.type.HardwareBrand;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.foulest.repairkit.util.CommandUtil.*;
import static net.foulest.repairkit.util.FileUtil.*;
import static net.foulest.repairkit.util.RegistryUtil.*;
import static net.foulest.repairkit.util.SoundUtil.playSound;
import static net.foulest.repairkit.util.SwingUtil.*;

public class RepairKit {

    private static final Set<String> SUPPORTED_OS_NAMES = new HashSet<>(Arrays.asList("Windows 10", "Windows 11"));
    private static final String OUTDATED_OS_MESSAGE = "Your operating system, %s, is outdated, unknown, or not Windows based."
            + "\nThis software only works on up-to-date Windows operating systems.";

    public static final String programName = "RepairKit";
    public static final JPanel panelMain = new JPanel(null);
    public static final JFrame frame = new JFrame(programName);
    public static final JLabel labelProgress = new JLabel();
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
        cleanTempFiles(false, false, false);

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
        panelMain.setPreferredSize(new Dimension(320, 375));
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

        // Progress Label
        labelProgress.setForeground(new Color(225, 225, 225));
        labelProgress.setBounds(5, 325, 500, 50);
        addComponents(panelMain, labelProgress);
        updateProgressLabel("");

        // Useful Programs Label
        JLabel labelUsefulPrograms = createLabel("Useful Programs",
                new Color(225, 225, 225), 5, 95, 150, 20);
        addComponents(panelMain, labelUsefulPrograms);

        // System Shortcuts Label
        JLabel labelSystemShortcuts = createLabel("System Shortcuts",
                new Color(225, 225, 225), 5, 245, 150, 20);
        addComponents(panelMain, labelSystemShortcuts);
    }

    /**
     * Sets the program's app buttons.
     */
    public static void setAppButtons() {
        // FanControl Button
        JButton buttonFanControl = createAppButton("FanControl", "Allows control over system fans.",
                "FanControl", "FanControl.zip", "FanControl\\FanControl.exe", true, System.getenv("APPDATA"));
        buttonFanControl.setBounds(5, 120, 152, 25);
        addComponents(panelMain, buttonFanControl);

        // CPU-Z Button
        JButton buttonCPUZ = createAppButton("CPU-Z", "Displays system hardware information.",
                "CPU-Z", "CPU-Z.exe", "CPU-Z.exe", false, tempDirectory.getPath());
        buttonCPUZ.setBounds(162, 120, 152, 25);
        addComponents(panelMain, buttonCPUZ);

        // WinDirStat Button
        JButton buttonWinDirStat = createAppButton("WinDirStat", "Displays system files organized by size.",
                "WinDirStat", "WinDirStat.zip", "WinDirStat.exe", true, tempDirectory.getPath());
        buttonWinDirStat.setBounds(5, 150, 152, 25);
        addComponents(panelMain, buttonWinDirStat);

        // Everything Button
        JButton buttonEverything = createAppButton("Everything", "Displays all files on your system.",
                "Everything", "Everything.exe", "Everything.exe", false, tempDirectory.getPath());
        buttonEverything.setBounds(162, 150, 152, 25);
        addComponents(panelMain, buttonEverything);

        // HWMonitor Button
        JButton buttonHWMonitor = createAppButton("HWMonitor", "Displays system hardware information.",
                "HWMonitor", "HWMonitor.exe", "HWMonitor.exe", false, tempDirectory.getPath());
        buttonHWMonitor.setBounds(5, 180, 152, 25);
        addComponents(panelMain, buttonHWMonitor);

        // Emsisoft Scan Button
        JButton buttonEmsisoft = createAppButton("Emsisoft Scan", "Scans your system for malware.",
                "Emsisoft", "Emsisoft.zip", "Emsisoft.exe", true, tempDirectory.getPath());
        buttonEmsisoft.setBounds(162, 180, 152, 25);
        addComponents(panelMain, buttonEmsisoft);
    }

    /**
     * Sets the program's link buttons.
     */
    public static void setLinkButtons() {
        // AdGuard Button
        JButton buttonAdGuard = createLinkButton("AdGuard", "Blocks ads, trackers, and malicious websites.", "start https://adguard.com/en/adguard-browser-extension/overview.html", "Opening link(s)...");
        buttonAdGuard.setBounds(5, 210, 152, 25);
        addComponents(panelMain, buttonAdGuard);

        // NordPass Button
        JButton buttonNordPass = createLinkButton("NordPass", "The best password manager available.", "start https://nordpass.com/download", "Opening link(s)...");
        buttonNordPass.setBounds(162, 210, 152, 25);
        addComponents(panelMain, buttonNordPass);

        // Installed Apps Button
        JButton buttonInstalledApps = createLinkButton("Installed Apps", "", "start ms-settings:appsfeatures", "Launching...");
        buttonInstalledApps.setBounds(5, 270, 152, 25);
        addComponents(panelMain, buttonInstalledApps);

        // Windows Update Button
        JButton buttonCheckForUpdates = createLinkButton("Windows Update", "", "start ms-settings:windowsupdate", "Launching...");
        buttonCheckForUpdates.setBounds(162, 270, 152, 25);
        addComponents(panelMain, buttonCheckForUpdates);

        // Task Manager Button
        JButton buttonTaskManager = createLinkButton("Task Manager", "", "taskmgr", "Launching...");
        buttonTaskManager.setBounds(5, 300, 152, 25);
        addComponents(panelMain, buttonTaskManager);

        // Windows Defender Button
        JButton buttonSecurity = createLinkButton("Windows Defender", "", "start windowsdefender:", "Launching...");
        buttonSecurity.setBounds(162, 300, 152, 25);
        addComponents(panelMain, buttonSecurity);
    }

    /**
     * Sets the program's repair buttons.
     */
    public static void setRepairButtons() {
        // Repair System Issues Button
        JButton buttonSystemIssues = new JButton("Repair System Issues");
        buttonSystemIssues.setToolTipText("Performs various fixes and maintenance tasks.");
        buttonSystemIssues.setBackground(new Color(200, 200, 200));
        buttonSystemIssues.setBounds(5, 30, 310, 25);
        addComponents(panelMain, buttonSystemIssues);
        buttonSystemIssues.addActionListener(actionEvent -> {
            try {
                updateProgressLabel("Repairing system issues...");

                // Deletes any system policies.
                deleteSystemPolicies();

                // Checks for AMD or NVIDIA hardware for driver updates.
                checkForDriverUpdates();

                // Installs 7-Zip and uninstalls other programs.
                install7Zip();

                // Create a fixed thread pool with a predefined number of threads.
                int numberOfThreads = 6;
                ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

                // Submit tasks to the executor.
                executor.submit(RepairKit::runServiceTweaks);
                executor.submit(RepairKit::runRegistryTweaks);
                executor.submit(RepairKit::runSettingsTweaks);
                executor.submit(RepairKit::cleanTempFilesAll);
                executor.submit(RepairKit::cleanSystemMemory);
                executor.submit(RepairKit::cleanFileExplorerThumbnails);

                // Shut down the executor after all tasks are submitted.
                executor.shutdown();

                // Wait for all tasks to finish.
                boolean allTasksCompleted = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

                if (allTasksCompleted) {
                    updateProgressLabel("Done.", 5000);
                    playSound("win.sound.exclamation");
                    JOptionPane.showMessageDialog(null, "System issues repaired successfully.", "Finished", JOptionPane.QUESTION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Some tasks did not complete.", "Warning", JOptionPane.WARNING_MESSAGE);
                }

            } catch (Exception ignored) {
            }
        });

        // Repair Disk Issues Button
        JButton buttonRepairDisk = createActionButton("Repair Disk Issues", "Repairs disk issues and restores disk health.", RepairKit::repairDiskIssues);
        buttonRepairDisk.setBounds(5, 60, 310, 25);
        addComponents(panelMain, buttonRepairDisk);
    }

    /**
     * Launches an application.
     *
     * @param appName        The name of the application.
     * @param appResource    The name of the application's resource.
     * @param appExecutable  The name of the application's executable.
     * @param isZipped       Whether the application is zipped or not.
     * @param extractionPath The path to extract the application to.
     */
    public static void launchApplication(String appName, String appResource, String appExecutable, boolean isZipped,
                                         String extractionPath) {
        Path path = Paths.get(extractionPath, appExecutable);

        if (!Files.exists(path)) {
            updateProgressLabel("Extracting files...");
            InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/" + appResource);
            saveFile(input, appResource, false);

            if (isZipped) {
                unzipFile(tempDirectory + "\\" + appResource, extractionPath);
            }
        }

        updateProgressLabel("Launching " + appName + "...", 3000);
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
     * Cleans the file explorer thumbnails.
     */
    private static void cleanFileExplorerThumbnails() {
        runCommand("taskkill /F /IM explorer.exe", false);
        deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\IconCache.db"));
        deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\Microsoft\\Windows\\Explorer\\"), "thumbcache*.db");
        runCommand("explorer", false);
    }

    /**
     * Checks for driver updates for AMD and NVIDIA hardware.
     */
    public static void checkForDriverUpdates() {
        List<HardwareBrand> hardwareBrands = Arrays.asList(
                new HardwareBrand("AMD", "C:\\Program Files\\AMD\\CNext\\CNext\\RadeonSoftware.exe", "https://www.amd.com/en/support"),
                new HardwareBrand("NVIDIA", "C:\\Program Files\\NVIDIA Corporation\\NVIDIA GeForce Experience\\NVIDIA GeForce Experience.exe", "https://www.nvidia.com/en-us/geforce/geforce-experience/download/")
        );

        String cpuName = getCommandOutput("wmic cpu get name", false, false).toString();
        String gpuName = getCommandOutput("wmic path win32_VideoController get name", false, false).toString();

        for (HardwareBrand brand : hardwareBrands) {
            if (cpuName.contains(brand.name) || gpuName.contains(brand.name)) {
                int reply = JOptionPane.showConfirmDialog(null, brand.name + " hardware found. Check for driver updates?",
                        "Driver Updates", JOptionPane.YES_NO_OPTION);

                if (reply == JOptionPane.YES_OPTION) {
                    if (Files.exists(Paths.get(brand.hardwareFile))) {
                        runCommand("\"" + brand.hardwareFile + "\"", true);
                    } else {
                        runCommand("start " + brand.downloadUrl, true);
                    }
                }
            }
        }
    }

    /**
     * Cleans the system memory.
     */
    private static void cleanSystemMemory() {
        Path tempPath = Paths.get(tempDirectory + "\\EmptyStandbyList.exe");

        if (!Files.exists(tempPath)) {
            InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/EmptyStandbyList.exe");
            saveFile(input, "EmptyStandbyList.exe", false);
        }

        runCommand(tempPath + " workingsets", true);
        runCommand(tempPath + " modifiedpagelist", true);
        runCommand(tempPath + " standbylist", true);
    }

    /**
     * Cleans all temporary files.
     */
    private static void cleanTempFilesAll() {
        cleanTempFiles(true, true, true);
    }

    /**
     * Cleans temporary files.
     *
     * @param recycleBin   Whether to empty the recycle bin.
     * @param windowsOld   Whether to notify about the Windows.Old folder.
     * @param programFiles Whether to clear log files and memory dumps.
     */
    private static void cleanTempFiles(boolean recycleBin, boolean windowsOld, boolean programFiles) {
        // Deletes the temp directory.
        deleteDirectory(new File(System.getProperty("java.io.tmpdir")));

        // Empties the recycle bin.
        if (recycleBin) {
            deleteDirectory(new File("C:\\$Recycle.Bin"));
        }

        // Notifies about the Windows.old folder.
        if (windowsOld) {
            if (Files.exists(Paths.get("C:\\Windows.old"))) {
                JOptionPane.showMessageDialog(null, "An old Windows installation was found on your system." +
                                "\n \nYou can delete C:\\Windows.old to save significant disk space.",
                        "Windows.old Detected", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Clears old log files and memory dumps.
        if (programFiles) {
            deleteDirectory(new File("C:\\"), "*.log");
            deleteDirectory(new File("C:\\"), "*.old");
            deleteDirectory(new File("C:\\"), "*.dmp");

            deleteDirectory(new File("C:\\AMD\\Chipset_Software\\Logs"), "*.txt");
            deleteDirectory(new File("C:\\Program Files (x86)\\Steam\\Logs\\"), "*.txt");
            deleteDirectory(new File("C:\\ProgramData\\NVIDIA"), "*.log.0");
            deleteDirectory(new File("C:\\ProgramData\\NVIDIA"), "*.log_backup1");
            deleteDirectory(new File("C:\\WINDOWS\\TEMP"), "*.tmp");
            deleteDirectory(new File("C:\\Windows\\System32\\"), "*.tmp");
            deleteDirectory(new File("C:\\Windows\\System32\\DriverStore\\Temp\\"), "*.tmp");
            deleteDirectory(new File(System.getenv("APPDATA") + "\\yuzu\\log"), "*.txt");
        }
    }

    /**
     * Deletes any existing system policies.
     */
    private static void deleteSystemPolicies() {
        deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Policies\\Microsoft\\MMC");
        deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Policies\\Microsoft\\Windows\\System");

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

        setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon", "Shell", "explorer.exe");
        setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon", "Shell", "explorer.exe");
        setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon", "Userinit", "c:\\windows\\system32\\userinit.exe,");

        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "Icons Only", 0);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced\\Folder\\Hidden\\SHOWALL", "CheckedValue", 1);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\NonEnum", "{645FF040-5081-101B-9F08-00AA002F954E}", 0);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "ConsentPromptBehaviorAdmin", 5);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "ConsentPromptBehaviorUser", 1);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "EnableLUA", 1);
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

                deleteDirectory(new File(System.getenv("APPDATA")
                        + "\\Microsoft\\Internet Explorer\\Quick Launch\\User Pinned\\TaskBar\\Tombstones\\Bandizip.lnk"));
                runCommand("rd /s /q \"%AppData%\\PeaZip", true);

                // Installs 7-Zip.
                if (shouldInstall7Zip) {
                    if (!Files.exists(tempPath)) {
                        updateProgressLabel("Extracting...");
                        InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/7-Zip.exe");
                        saveFile(input, "7-Zip.exe", false);
                    }

                    updateProgressLabel("Installing 7-Zip...");
                    runCommand(tempPath + " /D=\"C:\\Program Files\\7-Zip\" /S", false);
                }
            }
        }
    }

    /**
     * Repairs disk issues.
     */
    private static void repairDiskIssues() {
        // Repairs disk issues using chkdsk.
        updateProgressLabel("Repairing disk issues (1/3)...");
        runCommand("echo y | chkdsk /r", false);

        // Restores image health using DISM.
        updateProgressLabel("Repairing disk issues (2/3)...");
        displayCommandOutput("DISM /Online /Cleanup-Image /RestoreHealth", false);

        // Repairs any corrupted system files.
        updateProgressLabel("Repairing disk issues (3/3)...");
        displayCommandOutput("sfc /scannow", false);

        // Repairs the WMI Repository if broken.
        repairWMIRepository();

        updateProgressLabel("Done.", 5000);
        playSound("win.sound.exclamation");
        JOptionPane.showMessageDialog(null, "Disk issues repaired successfully.", "Finished", JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Repairs the WMI Repository.
     */
    private static void repairWMIRepository() {
        if (getCommandOutput("winmgmt /verifyrepository", false, false).toString().contains("not consistent")) {
            updateProgressLabel("Repairing WMI repository...");

            if (getCommandOutput("winmgmt /salvagerepository", false, false).toString().contains("not consistent")) {
                runCommand("winmgmt /resetrepository", false);
            }
        }
    }

    /**
     * Runs tweaks to the Windows registry.
     */
    private static void runRegistryTweaks() {
        // Disables telemetry and annoyances.
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Diagnostics\\DiagTrack", "ShowedToastAtLevel", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\InputPersonalization", "RestrictImplicitInkCollection", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\InputPersonalization", "RestrictImplicitTextCollection", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\InputPersonalization\\TrainedDataStore", "HarvestContacts", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Input\\Settings", "InsightsEnabled", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Input\\TIPC", "Enabled", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Narrator\\NoRoam", "DetailedFeedback", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Narrator\\NoRoam", "DetailedFeedback", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Personalization\\Settings", "AcceptedPrivacyPolicy", 0);
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
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Windows Search", "CortanaConsent", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Privacy", "TailoredExperiencesWithDiagnosticDataEnabled", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\PenWorkspace", "PenWorkspaceAppSuggestionsEnabled", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\SearchSettings", "IsDeviceSearchHistoryEnabled", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\SearchSettings", "SafeSearchMode", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\UserProfileEngagement", "ScoobeSystemSettingEnabled", 0);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Windows\\DataCollection", "AllowTelemetry", 0);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\PolicyManager\\current\\device\\Bluetooth", "AllowAdvertising", 0);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\PolicyManager\\current\\device\\System", "AllowExperimentation", 0);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\WcmSvc\\wifinetworkmanager\\features\\S-1-5-21-1376222853-718990322-3209866679-1001\\SocialNetworks\\ABCH", "OptInStatus", 0);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\WcmSvc\\wifinetworkmanager\\features\\S-1-5-21-1376222853-718990322-3209866679-1001\\SocialNetworks\\ABCH-SKYPE", "OptInStatus", 0);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\WcmSvc\\wifinetworkmanager\\features\\S-1-5-21-1376222853-718990322-3209866679-1001\\SocialNetworks\\FACEBOOK", "OptInStatus", 0);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\ControlSet001\\Services\\diagnosticshub.standardcollector.service", "Start", 4);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Diagnostics\\DiagTrack", "DiagTrackAuthorization", 7);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\ControlSet001\\Control\\WMI\\Autologger\\AutoLogger-Diagtrack-Listener", "Start", 0);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\ControlSet001\\Services\\DiagTrack", "Start", 4);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\International\\User Profile", "HttpAcceptLanguageOptOut", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "TaskbarMn", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "TaskbarDa", 0);

        // Enables Game Mode.
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\GameBar", "AllowAutoGameMode", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\GameBar", "AutoGameModeEnabled", 1);

        // Resets the Recycle Bin's icons.
        setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CLSID\\{645FF040-5081-101B-9F08-00AA002F954E}\\DefaultIcon", "(Default)", "C:\\Windows\\System32\\imageres.dll,-54");
        setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CLSID\\{645FF040-5081-101B-9F08-00AA002F954E}\\DefaultIcon", "empty", "C:\\Windows\\System32\\imageres.dll,-55");
        setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CLSID\\{645FF040-5081-101B-9F08-00AA002F954E}\\DefaultIcon", "full", "C:\\Windows\\System32\\imageres.dll,-54");

        // Enables updates for other Microsoft products.
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "Software\\Policies\\Microsoft\\Windows\\WindowsUpdate\\AU", "AllowMUUpdateService", 1);

        // Disables certain File Explorer features.
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "HideFileExt", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer", "ShowFrequent", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "DontUsePowerShellOnWinX", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "ShowSyncProviderNotifications", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "Start_TrackProgs", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "Start_TrackDocs", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\AutoplayHandlers", "DisableAutoplay", 1);

        // Disables the weather and news widget.
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Feeds", "EnableFeeds", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Feeds", "ShellFeedsTaskbarViewMode", 0);

        // Disables Game DVR.
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\GameDVR", "AppCaptureEnabled", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SYSTEM\\GameConfigStore", "GameDVR_Enabled", 0);

        // Disables lock screen toasts.
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Notifications\\Settings", "NOC_GLOBAL_SETTING_ALLOW_TOASTS_ABOVE_LOCK", 0);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\PushNotifications", "LockScreenToastEnabled", 0);

        // Enables Storage Sense.
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "01", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "04", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "08", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "2048", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "256", 30);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "32", 1);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "512", 30);

        // Enables Windows error reporting.
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\Windows Error Reporting", "Disabled", 0);

        // Modifies Windows graphics settings.
        setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\DirectX\\UserGpuPreferences", "DirectXUserGlobalSettings", "VRROptimizeEnable=1");

        // Disables Delivery Optimization.
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\DoSvc", "Start", 4);

        // Modifies Windows networking settings.
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\LanmanServer\\Parameters", "IRPStackSize", 30);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters", "DefaultTTL", 64);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters", "MaxUserPort", 65534);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters", "Tcp1323Opts", 1);

        // Disables sticky keys.
        setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Accessibility\\ToggleKeys", "Flags", "58");
        setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Accessibility\\StickyKeys", "Flags", "506");
        setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Accessibility\\Keyboard Response", "Flags", "122");

        // Disables mouse acceleration.
        setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Mouse", "MouseSpeed", "0");
        setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Mouse", "MouseThreshold1", "0");
        setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Mouse", "MouseThreshold2", "0");

        // Restores the keyboard layout.
        deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Keyboard Layout", "Scancode Map");

        // Fixes a battery visibility issue.
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Power", "EnergyEstimationEnabled", 1);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Power", "EnergyEstimationDisabled", 0);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Power", "UserBatteryDischargeEstimator", 0);

        // Various Internet Explorer tweaks.
        setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Internet Explorer\\Main", "Disable Script Debugger", "yes");
        setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Internet Explorer\\Main", "DisableScriptDebuggerIE", "yes");
        setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Internet Explorer\\Main", "Error Dlg Displayed On Every Error", "no");
        deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Internet Explorer\\Restrictions", "NoBrowserContextMenu");
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "MaxConnectionsPer1_0Server", 10);
        setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "MaxConnectionsPerServer", 10);
    }

    /**
     * Runs various tweaks to the Windows services.
     */
    private static void runServiceTweaks() {
        List<String[]> serviceList = Arrays.asList(
                new String[]{"PcaSvc", "Program Compatibility Assistant"},
                new String[]{"DiagTrack", "Connected User Experiences and Telemetry"},
                new String[]{"WMPNetworkSvc", "Windows Media Player Network Sharing"},
                new String[]{"RemoteAccess", "Remote Access"},
                new String[]{"diagnosticshub.standardcollector.service", "Diagnostics Hub Standard Collector"},
                new String[]{"MapsBroker", "Downloaded Maps Manager"},
                new String[]{"Fax", "Fax"},
                new String[]{"fhsvc", "File History Service"},
                new String[]{"WpcMonSvc", "Parental Controls"},
                new String[]{"RemoteRegistry", "Remote Registry"},
                new String[]{"RetailDemo", "Retail Demo"},
                new String[]{"lmhosts", "TCP/IP NetBIOS Helper"}
        );

        for (String[] serviceInfo : serviceList) {
            String serviceName = serviceInfo[0];
            runCommand("sc stop \"" + serviceName + "\"", false);
            runCommand("sc config \"" + serviceName + "\" start=disabled", true);
        }
    }

    /**
     * Runs tweaks to Windows settings.
     */
    private static void runSettingsTweaks() {
        // Enables Telnet Client.
        runCommand("DISM /Online /Enable-Feature /FeatureName:TelnetClient", true);

        // Enables scheduled defrag.
        runCommand("schtasks /Change /ENABLE /TN \"\\Microsoft\\Windows\\Defrag\\ScheduledDefrag\"", true);

        // Fixes micro-stuttering in games.
        runCommand("bcdedit /set useplatformtick yes", true);
        runCommand("bcdedit /deletevalue useplatformclock", true);

        // Enables the High Performance power plan.
        runCommand("powercfg /setactive 8c5e7fda-e8bf-4a96-9a85-a6e23a8c635c", true);

        // Re-registers ExplorerFrame.dll.
        runCommand("regsvr32 /s ExplorerFrame.dll", true);

        // Repairs broken Wi-Fi settings.
        deleteRegistryKey(WinReg.HKEY_CLASSES_ROOT, "CLSID\\{988248f3-a1ad-49bf-9170-676cbbc36ba3}");
        runCommand("netcfg -v -u dni_dne", true);

        // Resets various internet settings.
        runCommand("netsh winsock reset", false);
        runCommand("netsh int ip reset", false);
        runCommand("ipconfig /flushdns", false);
        runCommand("net stop dnscache", false);
        runCommand("net start dnscache", false);

        // Resets Device Manager.
        runCommand("net start PlugPlay", false);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\PlugPlay", "Start", 2);

        // Resets Windows search.
        runCommand("net stop WSearch /y", false);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\WSearch", "Start", 2);
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows Search", "SetupCompletedSuccessfully", 0);
        runCommand("net stop WSearch", true);

        // Resets Windows Script Host.
        deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows Script Host\\Settings", "Enabled");

        // Resets Volume Shadow Copy service.
        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\VSS", "Start", 2);
        runCommand("net start vss", true);

        // Resets Windows Media Player.
        runCommand("regsvr32 /s jscript.dll", false);
        runCommand("regsvr32 /s vbscript.dll", true);
    }
}
