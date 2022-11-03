package net.foulest.repairkit;

import com.sun.jna.platform.win32.WinReg;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static net.foulest.repairkit.util.MiscUtil.*;

public class RepairKit {

    // TODO: Make Java being installed to run program not needed
    // TODO: Fix AutoRuns not opening initially

    public static final String programName = "RepairKit";
    public static final JPanel panelMain = new JPanel(null);
    public static final JFrame frame = new JFrame(programName);
    public static final JLabel labelProgress = new JLabel("Progress: ");
    public static final String htmlFormat = "<html><body style='width: %1spx'>%1s";
    public static final File tempDirectory = new File(System.getenv("TEMP") + "\\" + programName);
    public static List<String> kasperskyResults = new ArrayList<>();
    public static List<String> adwCleanerResults = new ArrayList<>();
    public static int scansCompleted = 0;

    public static void main(String[] args) {
        // Provides warnings about using outdated operating systems.
        if (SystemUtils.OS_NAME != null) {
            switch (SystemUtils.OS_NAME) {
                case "Windows 10":
                case "Windows 11":
                    break;

                case "Windows 7":
                case "Windows 8":
                case "Windows 8.1":
                case "Windows 95":
                case "Windows 98":
                case "Windows NT":
                case "Windows ME":
                case "Windows 2003":
                case "Windows 2000":
                case "Windows XP":
                case "Windows Vista":
                case "Windows NT (Unknown)":
                    JOptionPane.showMessageDialog(null, "Your operating system, "
                                    + SystemUtils.OS_NAME + ", is severely outdated."
                                    + "\nThis program is only compatible with Windows 10 or newer versions.",
                            "Outdated Operating System", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Your operating system, "
                                    + SystemUtils.OS_NAME + ", is unknown or not Windows based." +
                                    "\nThis software only works on Windows operating systems.",
                            "Unknown Operating System", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                    break;
            }

        } else {
            JOptionPane.showMessageDialog(null, "Your operating system is unknown or not Windows based. "
                            + "\nThis software only works on Windows operating systems.",
                    "Unknown Operating System", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        setGUIElements();

        // Deletes pre-existing RepairKit files.
        deleteDirectory(tempDirectory);

        // Deletes temporary files on shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            runCommand("taskkill /F /IM Everything.exe", false);
            deleteDirectory(new File(System.getProperty("java.io.tmpdir"))); // Temporary files
            runCommand("rd /s /q \"C:\\AdwCleaner\"", false); // AdwCleaner log files
            runCommand("rd /s /q \"C:\\KVRT2020_Data\"", false); // Kaspersky log files
        }));

        // Makes the program visible.
        frame.setContentPane(panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void setGUIElements() {
        // Main Panel
        panelMain.setPreferredSize(new Dimension(320, 405));
        panelMain.setBackground(new Color(43, 43, 43));

        // Title Label
        JLabel labelTitle = new JLabel();
        labelTitle.setText(programName + " by Foulest");
        labelTitle.setForeground(new Color(225, 225, 225));
        labelTitle.setBounds(5, 5, 150, 20);
        addComponents(panelMain, labelTitle);

        // Progress Label
        labelProgress.setForeground(new Color(225, 225, 225));
        labelProgress.setBounds(5, 355, 500, 50);
        addComponents(panelMain, labelProgress);

        // Repair System Issues Button
        JButton buttonSystemIssues = new JButton();
        buttonSystemIssues.setText("Repair System Issues");
        buttonSystemIssues.setToolTipText("Performs various fixes and maintenance tasks.");
        buttonSystemIssues.setBackground(new Color(200, 200, 200));
        buttonSystemIssues.setBounds(5, 30, 310, 25);
        addComponents(panelMain, buttonSystemIssues);
        buttonSystemIssues.addActionListener(actionEvent -> {
            try {
                updateProgressLabel("Repairing system issues...");

                // Deletes any system policies.
                deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Policies\\Microsoft\\Windows\\System");
                deleteRegistryValue(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\System", "DisableCMD");
                setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon", "Shell", "explorer.exe");
                setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon", "Userinit", "c:\\windows\\system32\\userinit.exe,");
                deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Windows NT\\System Restore", "DisableConfig");
                deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Windows NT\\System Restore", "DisableSR");
                deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Policies\\Microsoft\\MMC");
                deleteRegistryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "DisableTaskMgr");
                deleteRegistryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "DisableRegistryTools");
                setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "EnableLUA", 1);
                setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "ConsentPromptBehaviorUser", 1);
                setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "ConsentPromptBehaviorAdmin", 5);
                deleteRegistryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\NonEnum", "{645FF040-5081-101B-9F08-00AA002F954E}");
                deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\Explorer", "DisallowCpl");
                deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\Explorer", "NoFolderOptions");
                setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon", "Shell", "explorer.exe");
                setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "Icons Only", 0);
                deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Class\\{4D36E965-E325-11CE-BFC1-08002BE10318}", "UpperFilters");
                deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Class\\{4D36E965-E325-11CE-BFC1-08002BE10318}", "LowerFilters");
                setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced\\Folder\\Hidden\\SHOWALL", "CheckedValue", 1);
                setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\NonEnum", "{645FF040-5081-101B-9F08-00AA002F954E}", 0);

                // Checks for AMD hardware for driver updates.
                if (getCommandOutput("wmic cpu get name", false).toString().contains("AMD")
                        || getCommandOutput("wmic path win32_VideoController get name", false).toString().contains("AMD")) {
                    int reply = JOptionPane.showConfirmDialog(null, "AMD hardware found. Check for driver updates?",
                            "Driver Updates", JOptionPane.YES_NO_OPTION);

                    if (reply == JOptionPane.YES_OPTION) {
                        if (Files.exists(Paths.get("C:\\Program Files\\AMD\\CNext\\CNext\\RadeonSoftware.exe"))) {
                            runCommand("\"C:\\Program Files\\AMD\\CNext\\CNext\\RadeonSoftware.exe\"", true);
                        } else {
                            runCommand("start https://www.amd.com/en/support", true);
                        }
                    }
                }

                // Checks for NVIDIA hardware for driver updates.
                if (getCommandOutput("wmic path win32_VideoController get name", false).toString().contains("NVIDIA")) {
                    int reply = JOptionPane.showConfirmDialog(null, "NVIDIA hardware found. Check for driver updates?",
                            "Driver Updates", JOptionPane.YES_NO_OPTION);

                    if (reply == JOptionPane.YES_OPTION) {
                        if (Files.exists(Paths.get("C:\\Program Files\\NVIDIA Corporation\\NVIDIA GeForce Experience\\NVIDIA GeForce Experience.exe"))) {
                            runCommand("\"C:\\Program Files\\NVIDIA Corporation\\NVIDIA GeForce Experience\\NVIDIA GeForce Experience.exe\"", true);
                        } else {
                            runCommand("start https://www.nvidia.com/en-us/geforce/geforce-experience/download/", true);
                        }
                    }
                }

                // Installs 7-Zip and uninstalls other programs.
                if (!Files.exists(Paths.get("C:\\Program Files\\7-Zip\\7zFM.exe"))
                        || Files.exists(Paths.get("C:\\ProgramData\\WinZip"))
                        || Files.exists(Paths.get("C:\\Program Files (x86)\\CAM Development"))
                        || Files.exists(Paths.get("C:\\Program Files\\Bandizip"))
                        || Files.exists(Paths.get("C:\\Program Files\\WinRAR"))
                        || Files.exists(Paths.get("C:\\Program Files\\PowerArchiver"))
                        || Files.exists(Paths.get("C:\\Program Files\\PeaZip"))
                        || Files.exists(Paths.get("C:\\Program Files (x86)\\ZipGenius 6"))
                        || Files.exists(Paths.get("C:\\Program Files (x86)\\NCH Software\\ExpressZip"))
                        || Files.exists(Paths.get("C:\\Program Files (x86)\\B1 Free Archiver"))
                        || Files.exists(Paths.get("C:\\Program Files (x86)\\IZArc"))) {
                    if (JOptionPane.showConfirmDialog(null,
                            "Install 7-Zip and remove other .zip programs? (Recommended)", "Install 7-Zip",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                        // Uninstalls CAM UnZip 5.
                        runCommand("\"C:\\Program Files (x86)\\CAM Development\\CAM UnZip 5\\Setup\\unins000.exe\"", false);
                        runCommand("rd /s /q \"C:\\Program Files (x86)\\CAM Development\"", true);

                        // Uninstalls PowerArchiver.
                        runCommand("\"C:\\Program Files\\PowerArchiver\\unins000.exe\"", false);
                        runCommand("rd /s /q \"C:\\Program Files\\PowerArchiver\"", true);

                        // Uninstalls IZArc.
                        runCommand("\"C:\\Program Files (x86)\\IZArc\\unins000.exe\"", false);
                        runCommand("rd /s /q \"C:\\Program Files (x86)\\IZArc\"", true);

                        // Uninstalls ZipGenius 6.
                        runCommand("\"C:\\Program Files (x86)\\ZipGenius 6\\unins000.exe\"", false);
                        runCommand("rd /s /q \"C:\\Program Files (x86)\\ZipGenius 6\"", true);

                        // Uninstalls WinRAR.
                        runCommand("\"C:\\Program Files\\WinRAR\\uninstall.exe\" /S", false);
                        runCommand("\"C:\\Program Files (x86)\\WinRAR\\uninstall.exe\" /S", false);

                        // Uninstalls Bandizip.
                        runCommand("\"C:\\Program Files\\Bandizip\\uninstall\" /S", false);
                        deleteDirectory(new File(System.getenv("APPDATA")
                                + "\\Microsoft\\Internet Explorer\\Quick Launch\\User Pinned\\TaskBar\\Tombstones\\Bandizip.lnk"));

                        // Uninstalls PeaZip.
                        runCommand("\"C:\\Program Files\\PeaZip\\unins000.exe\"", false);
                        runCommand("rd /s /q \"%AppData%\\PeaZip", true);

                        // Handles WinZip.
                        if (Files.exists(Paths.get("C:\\ProgramData\\WinZip"))) {
                            JOptionPane.showMessageDialog(null,
                                    "WinZip could not be automatically uninstalled."
                                            + "\nPlease manually uninstall it via Installed Apps.",
                                    "Error Uninstalling", JOptionPane.ERROR_MESSAGE);
                        }
                        runCommand("rd /s /q \"C:\\ProgramData\\WinZip\"", true);

                        // Handles ExpressZip.
                        if (Files.exists(Paths.get("C:\\Program Files (x86)\\NCH Software\\ExpressZip"))) {
                            JOptionPane.showMessageDialog(null,
                                    "ExpressZip could not be automatically uninstalled."
                                            + "\nPlease manually uninstall it via Installed Apps.",
                                    "Error Uninstalling", JOptionPane.ERROR_MESSAGE);
                        }

                        // Handles B1 Free Archiver.
                        if (Files.exists(Paths.get("C:\\Program Files (x86)\\B1 Free Archiver"))) {
                            JOptionPane.showMessageDialog(null,
                                    "B1 Free Archiver could not be automatically uninstalled."
                                            + "\nPlease manually uninstall it via Installed Apps.",
                                    "Error Uninstalling", JOptionPane.ERROR_MESSAGE);
                        }

                        // Installs 7-Zip.
                        if (!Files.exists(Paths.get("C:\\Program Files\\7-Zip\\7zFM.exe"))) {
                            downloadFile("https://www.7-zip.org/a/7z2107-x64.exe", "7-Zip.exe", true);
                            runCommand(tempDirectory + "\\7-Zip.exe" + " /D=\"C:\\Program Files\\7-Zip\" /S", false);
                        }
                    }
                }

                Thread services = new Thread(() -> {
                    // Disables the Program Compatibility Assistant service.
                    runCommand("sc stop \"PcaSvc\"", false);
                    runCommand("sc config \"PcaSvc\" start=disabled", true);

                    // Disables the Connected User Experiences and Telemetry service.
                    runCommand("sc stop \"DiagTrack\"", false);
                    runCommand("sc config \"DiagTrack\" start=disabled", true);

                    // Disables the Windows Media Player Network Sharing service.
                    runCommand("sc stop \"WMPNetworkSvc\"", false);
                    runCommand("sc config \"WMPNetworkSvc\" start=disabled", true);

                    // Disables the Remote Access service.
                    runCommand("sc stop \"RemoteAccess\"", false);
                    runCommand("sc config \"RemoteAccess\" start=disabled", true);

                    // Disables the Diagnostics Hub Standard Collector service.
                    runCommand("sc stop \"diagnosticshub.standardcollector.service\"", false);
                    runCommand("sc config \"diagnosticshub.standardcollector.service\" start=disabled", true);

                    // Disables the Downloaded Maps Manager service.
                    runCommand("sc stop \"MapsBroker\"", false);
                    runCommand("sc config \"MapsBroker\" start=disabled", true);

                    // Disables the Fax service.
                    runCommand("sc stop \"Fax\"", false);
                    runCommand("sc config \"Fax\" start=disabled", true);
                    runCommand("sc stop \"fhsvc\"", false);
                    runCommand("sc config \"fhsvc\" start=disabled", true);

                    // Disables the Parental Controls service.
                    runCommand("sc stop \"WpcMonSvc\"", false);
                    runCommand("sc config \"WpcMonSvc\" start=disabled", true);

                    // Disables the Remote Registry service.
                    runCommand("sc stop \"RemoteRegistry\"", false);
                    runCommand("sc config \"RemoteRegistry\" start=disabled", true);

                    // Disables the Remote Registry service.
                    runCommand("sc stop \"RemoteRegistry\"", false);
                    runCommand("sc config \"RemoteRegistry\" start=disabled", true);

                    // Disables the Retail Demo service.
                    runCommand("sc stop \"RetailDemo\"", false);
                    runCommand("sc config \"RetailDemo\" start=disabled", true);

                    // Disables the TCP/IP NetBIOS Helper service.
                    runCommand("sc stop \"lmhosts\"", false);
                    runCommand("sc config \"lmhosts\" start=disabled", true);
                });

                Thread registry = new Thread(() -> {
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
                });

                Thread commands = new Thread(() -> {
//                    // Repairs any broken Microsoft apps.
//                    PowerShell.executeSingleCommand("Get-AppXPackage | Foreach {Add-AppxPackage -DisableDevelopmentMode -Register \"$($_.InstallLocation)\\AppXManifest.xml\"}");

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

                    // Resets Windows update.
                    runCommand("net stop wuauserv", false);
                    runCommand("net stop AeLookupSvc", false);
                    runCommand("net stop bits", false);
                    deleteDirectory(new File("C:\\Windows\\SoftwareDistribution"));
                    runCommand("net start wuauserv", false);
                    runCommand("net start AeLookupSvc", false);
                    runCommand("net start bits", false);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\WindowsUpdate\\UX", "IsConvergedUpdateStackAvailable", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\WindowsUpdate\\UX\\Settings", "UxOption", 0);
                });

                Thread tempFiles = new Thread(() -> {
                    // Deletes the temp directory.
                    deleteDirectory(new File(System.getProperty("java.io.tmpdir")));

                    // Empties the Recycle Bin.
                    deleteDirectory(new File("C:\\$Recycle.Bin"));

                    // Notifies about the Windows.old folder.
                    if (Files.exists(Paths.get("C:\\Windows.old"))) {
                        JOptionPane.showMessageDialog(null, "An old Windows installation was found on your system." +
                                        "\n \nYou can delete C:\\Windows.old to save significant disk space.",
                                "Windows.old Detected", JOptionPane.ERROR_MESSAGE);
                    }

                    // Clears log files and memory dumps.
                    deleteDirectory(new File("C:\\AMD\\Chipset_Software\\Logs"), "*.log");
                    deleteDirectory(new File("C:\\AMD\\Chipset_Software\\Logs"), "*.txt");
                    deleteDirectory(new File("C:\\Program Files (x86)\\Steam\\"), "*.log");
                    deleteDirectory(new File("C:\\Program Files (x86)\\Steam\\"), "*.old");
                    deleteDirectory(new File("C:\\Program Files (x86)\\Steam\\Dumps\\"), "*.dmp");
                    deleteDirectory(new File("C:\\Program Files (x86)\\Steam\\Logs\\"), "*.txt");
                    deleteDirectory(new File("C:\\Program Files\\Cylance\\Desktop\\log"), "*.log");
                    deleteDirectory(new File("C:\\Program Files\\Rockstar Games\\Launcher\\"), "*.old");
                    deleteDirectory(new File("C:\\ProgramData\\Microsoft\\EdgeUpdate\\Log\\"), "*.bak");
                    deleteDirectory(new File("C:\\ProgramData\\Microsoft\\EdgeUpdate\\Log\\"), "*.log");
                    deleteDirectory(new File("C:\\ProgramData\\NVIDIA Corporation\\GeForce Experience\\Logs"), "*.log");
                    deleteDirectory(new File("C:\\ProgramData\\NVIDIA Corporation\\ShadowPlay\\"), "*.old");
                    deleteDirectory(new File("C:\\ProgramData\\NVIDIA"), "*.log");
                    deleteDirectory(new File("C:\\ProgramData\\NVIDIA"), "*.log.0");
                    deleteDirectory(new File("C:\\ProgramData\\NVIDIA"), "*.log_backup1");
                    deleteDirectory(new File("C:\\ProgramData\\XSplit\\VCam\\CrashDumps\\"), "*.dmp");
                    deleteDirectory(new File("C:\\Riot Games\\Riot Client\\"), "*.old");
                    deleteDirectory(new File("C:\\WINDOWS\\"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\Debug\\"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\Logs\\"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\Logs\\CBS"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\Logs\\DISM"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\Logs\\DPX"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\Logs\\MoSetup"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\Logs\\WinREAgent"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\Microsoft.NET\\Framework64\\v4.0.30319\\"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\Microsoft.NET\\Framework\\v4.0.30319\\"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\Panther\\UnattendGC\\"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\Performance\\WinSAT\\"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\ServiceProfiles\\LocalService\\AppData\\Local\\Temp\\"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\ServiceProfiles\\NetworkService\\AppData\\Local\\Temp\\"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\SoftwareDistribution\\"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\SysWOW64\\config\\systemprofile\\AppData\\Local\\Microsoft\\CLR_v4.0_32\\UsageLogs\\"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\TEMP"), "*.tmp");
                    deleteDirectory(new File("C:\\WINDOWS\\debug\\WIA"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\inf\\"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\security\\logs\\"), "*.log");
                    deleteDirectory(new File("C:\\WINDOWS\\security\\logs\\"), "*.old");
                    deleteDirectory(new File("C:\\WINDOWS\\system32\\config\\systemprofile\\AppData\\Local\\Microsoft\\CLR_v4.0\\UsageLogs\\"), "*.log");
                    deleteDirectory(new File("C:\\Windows\\ServiceProfiles\\LocalService\\AppData\\Local\\CrashDumps\\"), "*.dmp");
                    deleteDirectory(new File("C:\\Windows\\SysWOW64\\config\\systemprofile\\AppData\\Local\\CrashDumps\\"), "*.dmp");
                    deleteDirectory(new File("C:\\Windows\\System32\\"), "*.tmp");
                    deleteDirectory(new File("C:\\Windows\\System32\\DriverStore\\Temp\\"), "*.tmp");
                    deleteDirectory(new File("C:\\Windows\\System32\\LogFiles\\setupcln\\"), "*.log");
                    deleteDirectory(new File("C:\\Windows\\System32\\config\\systemprofile\\AppData\\Local\\CrashDumps\\"), "*.dmp");
                    deleteDirectory(new File(System.getProperty("user.home") + "\\.gradle\\daemon"), "*.log");
                    deleteDirectory(new File(System.getProperty("user.home") + "\\.lunarclient\\logs"), "*.log");
                    deleteDirectory(new File(System.getProperty("user.home") + "\\AppData\\LocalLow\\Evil Tortilla Games\\WhosYourDaddy\\Player.log"));
                    deleteDirectory(new File(System.getenv("APPDATA") + "\\.minecraft\\logs"), "*.log");
                    deleteDirectory(new File(System.getenv("APPDATA") + "\\.technic\\logs"), "*.log");
                    deleteDirectory(new File(System.getenv("APPDATA") + "\\Elgato\\4KCaptureUtility\\Log"), "*.log");
                    deleteDirectory(new File(System.getenv("APPDATA") + "\\TIDAL\\Logs"), "*.log");
                    deleteDirectory(new File(System.getenv("APPDATA") + "\\discord\\Crashpad\\reports\\"), "*.dmp");
                    deleteDirectory(new File(System.getenv("APPDATA") + "\\yuzu\\log"), "*.txt");
                    deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\CrashDumps"));
                    deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\FortniteGame\\Saved\\Logs"), "*.log");
                    deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\Microsoft\\CLR_v4.0\\"), "*.log");
                    deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\Microsoft\\CLR_v4.0\\UsageLogs\\"), "*.log");
                    deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\Microsoft\\CLR_v4.0_32\\"), "*.log");
                    deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\Microsoft\\CLR_v4.0_32\\UsageLogs\\"), "*.log");
                    deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\Microsoft\\Windows\\SettingSync\\metastore\\"), "*.log");
                    deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\Microsoft\\Windows\\SettingSync\\remotemetastore\\v1\\"), "*.log");
                    deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\Microsoft\\Windows\\WebCache\\"), "*.log");
                    deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\NVIDIA Corporation\\NVIDIA GeForce Experience\\"), "*.bak");
                    deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\NVIDIA Corporation\\NvNode\\"), "*.bak");
                    deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\NVIDIA\\NvBackend\\"), "*.bak");
                    deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\VALORANT\\Saved\\Logs\\"), "*.log");
                });

                // Cleans system memory.
                Thread memory = new Thread(() -> {
                    if (!Files.exists(Paths.get(tempDirectory + "\\EmptyStandbyList.exe"))) {
                        InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/EmptyStandbyList.exe");
                        saveFile(input, "EmptyStandbyList.exe", false);
                    }

                    runCommand(tempDirectory + "\\EmptyStandbyList.exe workingsets", true);
                    runCommand(tempDirectory + "\\EmptyStandbyList.exe modifiedpagelist", true);
                    runCommand(tempDirectory + "\\EmptyStandbyList.exe standbylist", true);
                });

                // Cleans File Explorer thumbnails.
                Thread explorer = new Thread(() -> {
                    runCommand("taskkill /F /IM explorer.exe", false);
                    deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\IconCache.db"));
                    deleteDirectory(new File(System.getenv("LOCALAPPDATA") + "\\Microsoft\\Windows\\Explorer\\"), "thumbcache*.db");
                    runCommand("explorer", false);
                });

                // Starts and waits for threads.
                explorer.start();
                memory.start();
                services.start();
                commands.start();
                tempFiles.start();
                registry.start();
                explorer.join();
                memory.join();
                services.join();
                commands.join();
                tempFiles.join();
                registry.join();

            } catch (Exception ex) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                JOptionPane.showMessageDialog(null, sw.toString(), "Error Debug", JOptionPane.ERROR_MESSAGE);
            }

            updateProgressLabel("Done.", 5000);
            playSound("win.sound.exclamation");
            JOptionPane.showMessageDialog(null, "System issues repaired successfully.", "Finished", JOptionPane.QUESTION_MESSAGE);
        });

        // Repair Disk Issues Button
        JButton buttonRepairDisk = new JButton();
        buttonRepairDisk.setText("Repair Disk Issues");
        buttonRepairDisk.setToolTipText("Repairs disk issues and restores disk health.");
        buttonRepairDisk.setBackground(new Color(200, 200, 200));
        buttonRepairDisk.setBounds(5, 60, 310, 25);
        addComponents(panelMain, buttonRepairDisk);
        buttonRepairDisk.addActionListener(actionEvent -> {
            try {
                // Repairs disk issues using chkdsk.
                updateProgressLabel("Repairing disk issues (1/3)...");
                runCommand("echo y | chkdsk /r", false);

                // Restores image health using DISM.
                updateProgressLabel("Repairing disk issues (2/3)...");
                displayCommandOutput("DISM /Online /Cleanup-Image /RestoreHealth");

                // Repairs any corrupted system files.
                updateProgressLabel("Repairing disk issues (3/3)...");
                displayCommandOutput("sfc /scannow");

                // Repairs the WMI Repository if broken.
                if (getCommandOutput("winmgmt /verifyrepository", false).toString().contains("not consistent")) {
                    updateProgressLabel("Repairing WMI repository...");

                    if (getCommandOutput("winmgmt /salvagerepository", false).toString().contains("not consistent")) {
                        runCommand("winmgmt /resetrepository", false);
                    }
                }

                updateProgressLabel("Done.", 5000);
                playSound("win.sound.exclamation");
                JOptionPane.showMessageDialog(null, "Disk issues repaired successfully.", "Finished", JOptionPane.QUESTION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error Debug", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Malware Scanner Button
        JButton buttonMalwareScan = new JButton();
        buttonMalwareScan.setText("Scan for Malware");
        buttonMalwareScan.setToolTipText("Removes viruses and malware from your system.");
        buttonMalwareScan.setBackground(new Color(200, 200, 200));
        buttonMalwareScan.setBounds(5, 90, 310, 25);
        addComponents(panelMain, buttonMalwareScan);
        buttonMalwareScan.addActionListener(actionEvent -> {
            try {
                // Downloads Kaspersky files to the temp directory.
                Thread kasperskyPrep = new Thread(() -> {
                    if (!Files.exists(Paths.get(tempDirectory + "\\KVRT.exe"))) {
                        updateProgressLabel("Preparing files...");
                        downloadFile("https://devbuilds.s.kaspersky-labs.com/devbuilds/KVRT/latest/full/KVRT.exe", "KVRT.exe", false);
                    }
                });

                // Downloads TDSSKiller files to the temp directory.
                Thread tdssKillerPrep = new Thread(() -> {
                    if (!Files.exists(Paths.get(tempDirectory + "\\TDSSKiller.exe"))) {
                        updateProgressLabel("Preparing files...");
                        downloadFile("https://media.kaspersky.com/utilities/VirusUtilities/EN/tdsskiller.exe", "TDSSKiller.exe", false);
                    }
                });

                // Downloads AdwCleaner files to the temp directory.
                Thread adwCleanerPrep = new Thread(() -> {
                    if (!Files.exists(Paths.get(tempDirectory + "\\AdwCleaner.exe"))) {
                        updateProgressLabel("Preparing files...");
                        downloadFile("https://adwcleaner.malwarebytes.com/adwcleaner?channel=release", "AdwCleaner.exe", false);
                    }
                });

                // Starts the prep threads.
                kasperskyPrep.start();
                tdssKillerPrep.start();
                adwCleanerPrep.start();
                kasperskyPrep.join();
                tdssKillerPrep.join();
                adwCleanerPrep.join();

                // Scans for malware using Emsisoft and Kaspersky engines.
                Thread kasperskyScan = new Thread(() -> {
                    kasperskyResults = getCommandOutput(tempDirectory + "\\KVRT.exe -accepteula -processlevel 1 -noads -silent", false);
                    scansCompleted++;
                });

                Thread tdssKillerScan = new Thread(() -> {
                    runCommand(tempDirectory + "\\TDSSKiller.exe -accepteula -accepteulaksn -dcexact -silent", false);
                    scansCompleted++;
                });

                Thread adwCleanerScan = new Thread(() -> {
                    adwCleanerResults = getCommandOutput(tempDirectory + "\\AdwCleaner.exe /eula /clean /noreboot /path %temp%\\AdwCleaner", false);
                    scansCompleted++;
                });

                scansCompleted = 0;

                TimerTask updateLabelTask = new TimerTask() {
                    @Override
                    public void run() {
                        updateProgressLabel("Scanning for malware... (" + scansCompleted + "/3 done)");
                    }
                };

                // Starts the scan threads.
                new Timer().schedule(updateLabelTask, 0L, 500L);
                kasperskyScan.start();
                tdssKillerScan.start();
                adwCleanerScan.start();
                kasperskyScan.join();
                tdssKillerScan.join();
                adwCleanerScan.join();
                updateLabelTask.cancel();

                // Parses through scan results.
                int detections = 0;
                if (!kasperskyScan.isAlive() && !tdssKillerScan.isAlive() && !adwCleanerScan.isAlive()) {
                    for (String scanResult : kasperskyResults) {
                        if (scanResult.contains("        Detected: ")) {
                            detections += Integer.parseInt(scanResult.substring(scanResult.length() - 1));
                        }
                    }
                }

                // Prints parsed scan results.
                updateProgressLabel("Done.", 5000);
                playSound("win.sound.exclamation");

                if (detections > 0) {
                    JOptionPane.showMessageDialog(null, "Scan finished. Detected and removed "
                            + detections + " threats.", "Scan Finished", JOptionPane.QUESTION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Scan finished. No threats detected.",
                            "Scan Finished", JOptionPane.QUESTION_MESSAGE);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Useful Programs Label
        JLabel labelUsefulPrograms = new JLabel();
        labelUsefulPrograms.setText("Useful Programs");
        labelUsefulPrograms.setForeground(new Color(225, 225, 225));
        labelUsefulPrograms.setBounds(5, 125, 150, 20);
        addComponents(panelMain, labelUsefulPrograms);

        // Autoruns Button
        JButton buttonAutoruns = new JButton();
        buttonAutoruns.setText("Autoruns");
        buttonAutoruns.setToolTipText("Displays Windows startup entries.");
        buttonAutoruns.setBackground(new Color(200, 200, 200));
        buttonAutoruns.setBounds(5, 150, 152, 25);
        addComponents(panelMain, buttonAutoruns);
        buttonAutoruns.addActionListener(actionEvent -> {
            if (!Files.exists(Paths.get(tempDirectory + "\\Autoruns.exe"))) {
                updateProgressLabel("Extracting...");
                InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/Autoruns.exe");
                saveFile(input, "Autoruns.exe", false);
            }

            updateProgressLabel("Launching...", 3000);
            runCommand(tempDirectory + "\\Autoruns.exe", true);
        });

        // Process Explorer Button
        JButton buttonProcExp = new JButton();
        buttonProcExp.setText("Process Explorer");
        buttonProcExp.setToolTipText("Displays currently running programs.");
        buttonProcExp.setBackground(new Color(200, 200, 200));
        buttonProcExp.setBounds(162, 150, 152, 25);
        addComponents(panelMain, buttonProcExp);
        buttonProcExp.addActionListener(actionEvent -> {
            if (!Files.exists(Paths.get(tempDirectory + "\\ProcessExplorer.exe"))) {
                updateProgressLabel("Extracting...");
                InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/ProcessExplorer.exe");
                saveFile(input, "ProcessExplorer.exe", false);
            }

            updateProgressLabel("Launching...", 3000);
            runCommand(tempDirectory + "\\ProcessExplorer.exe", true);
        });

        // WinDirStat Button
        JButton buttonWinDirStat = new JButton();
        buttonWinDirStat.setText("WinDirStat");
        buttonWinDirStat.setToolTipText("Displays system files organized by size.");
        buttonWinDirStat.setBackground(new Color(200, 200, 200));
        buttonWinDirStat.setBounds(5, 180, 152, 25);
        addComponents(panelMain, buttonWinDirStat);
        buttonWinDirStat.addActionListener(actionEvent -> {
            if (!Files.exists(Paths.get(tempDirectory + "\\WinDirStat\\WinDirStat.exe"))) {
                updateProgressLabel("Extracting...");
                InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/WinDirStat.zip");
                saveFile(input, "WinDirStat.zip", false);
                unzipFile(tempDirectory + "\\WinDirStat.zip", tempDirectory + "\\WinDirStat");
            }

            updateProgressLabel("Launching...", 3000);
            runCommand(tempDirectory + "\\WinDirStat\\WinDirStat.exe", true);
        });

        // Everything Button
        JButton buttonEverything = new JButton();
        buttonEverything.setText("Everything");
        buttonEverything.setToolTipText("Displays all files on your system.");
        buttonEverything.setBackground(new Color(200, 200, 200));
        buttonEverything.setBounds(162, 180, 152, 25);
        addComponents(panelMain, buttonEverything);
        buttonEverything.addActionListener(actionEvent -> {
            if (!Files.exists(Paths.get(tempDirectory + "\\Everything.exe"))) {
                updateProgressLabel("Extracting...");
                InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/Everything.exe");
                saveFile(input, "Everything.exe", false);
            }

            updateProgressLabel("Launching...", 3000);
            runCommand(tempDirectory + "\\Everything.exe", true);
        });

        // HWMonitor Button
        JButton buttonHWMonitor = new JButton();
        buttonHWMonitor.setText("HWMonitor");
        buttonHWMonitor.setToolTipText("Displays system hardware information.");
        buttonHWMonitor.setBackground(new Color(200, 200, 200));
        buttonHWMonitor.setBounds(5, 210, 152, 25);
        addComponents(panelMain, buttonHWMonitor);
        buttonHWMonitor.addActionListener(actionEvent -> {
            if (!Files.exists(Paths.get(tempDirectory + "\\HWMonitor.exe"))) {
                updateProgressLabel("Extracting...");
                InputStream input = RepairKit.class.getClassLoader().getResourceAsStream("resources/HWMonitor.exe");
                saveFile(input, "HWMonitor.exe", false);
            }

            updateProgressLabel("Launching...", 3000);
            runCommand(tempDirectory + "\\HWMonitor.exe", true);
        });

        // Browser Guard Button
        JButton buttonBrowserGuard = new JButton();
        buttonBrowserGuard.setText("Browser Guard");
        buttonBrowserGuard.setToolTipText("Blocks ads, trackers, and malicious websites.");
        buttonBrowserGuard.setBackground(new Color(200, 200, 200));
        buttonBrowserGuard.setBounds(162, 210, 152, 25);
        addComponents(panelMain, buttonBrowserGuard);
        buttonBrowserGuard.addActionListener(actionEvent -> {
            updateProgressLabel("Opening link(s)...", 3000);
            runCommand("start https://malwarebytes.com/browserguard", true);
        });

        // WinDbg Button
        JButton buttonWinDbg = new JButton();
        buttonWinDbg.setText("WinDbg Preview");
        buttonWinDbg.setToolTipText("Analyze past system memory dumps.");
        buttonWinDbg.setBackground(new Color(200, 200, 200));
        buttonWinDbg.setBounds(5, 240, 152, 25);
        addComponents(panelMain, buttonWinDbg);
        buttonWinDbg.addActionListener(actionEvent -> {
            updateProgressLabel("Opening link(s)...", 3000);
            runCommand("start https://apps.microsoft.com/store/detail/windbg-preview/9PGJGD53TN86", true);
        });

        // Password Manager Button
        JButton buttonPasswordManager = new JButton();
        buttonPasswordManager.setText("Password Manager");
        buttonPasswordManager.setBackground(new Color(200, 200, 200));
        buttonPasswordManager.setBounds(162, 240, 152, 25);
        buttonPasswordManager.setToolTipText("Securely and conveniently stores all your passwords.");
        addComponents(panelMain, buttonPasswordManager);
        buttonPasswordManager.addActionListener(actionEvent -> {
            updateProgressLabel("Opening link(s)...", 3000);
            runCommand("start https://nordpass.com/download", true);
        });

        // System Shortcuts Label
        JLabel labelSystemShortcuts = new JLabel();
        labelSystemShortcuts.setText("System Shortcuts");
        labelSystemShortcuts.setForeground(new Color(225, 225, 225));
        labelSystemShortcuts.setBounds(5, 275, 150, 20);
        addComponents(panelMain, labelSystemShortcuts);

        // Installed Apps Button
        JButton buttonInstalledApps = new JButton();
        buttonInstalledApps.setText("Installed Apps");
        buttonInstalledApps.setBackground(new Color(200, 200, 200));
        buttonInstalledApps.setBounds(5, 300, 152, 25);
        addComponents(panelMain, buttonInstalledApps);
        buttonInstalledApps.addActionListener(actionEvent -> {
            updateProgressLabel("Launching...", 3000);
            runCommand("start ms-settings:appsfeatures", true);
        });

        // Check For Updates Button
        JButton buttonCheckForUpdates = new JButton();
        buttonCheckForUpdates.setText("Check for Updates");
        buttonCheckForUpdates.setBackground(new Color(200, 200, 200));
        buttonCheckForUpdates.setBounds(162, 300, 152, 25);
        addComponents(panelMain, buttonCheckForUpdates);
        buttonCheckForUpdates.addActionListener(actionEvent -> {
            updateProgressLabel("Launching...", 3000);
            runCommand("start ms-settings:windowsupdate", true);
        });

        // Task Manager Button
        JButton buttonTaskManager = new JButton();
        buttonTaskManager.setText("Task Manager");
        buttonTaskManager.setBackground(new Color(200, 200, 200));
        buttonTaskManager.setBounds(5, 330, 152, 25);
        addComponents(panelMain, buttonTaskManager);
        buttonTaskManager.addActionListener(actionEvent -> {
            updateProgressLabel("Launching...", 3000);
            runCommand("taskmgr", true);
        });

        // Security Settings Button
        JButton buttonSecurity = new JButton();
        buttonSecurity.setText("Security Settings");
        buttonSecurity.setBackground(new Color(200, 200, 200));
        buttonSecurity.setBounds(162, 330, 152, 25);
        addComponents(panelMain, buttonSecurity);
        buttonSecurity.addActionListener(actionEvent -> {
            updateProgressLabel("Launching...", 3000);
            runCommand("start windowsdefender:", true);
        });
    }
}
