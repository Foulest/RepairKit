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
import net.foulest.repairkit.util.TaskUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static net.foulest.repairkit.util.CommandUtil.*;
import static net.foulest.repairkit.util.ConstantUtil.*;
import static net.foulest.repairkit.util.DebugUtil.debug;
import static net.foulest.repairkit.util.RegistryUtil.*;
import static net.foulest.repairkit.util.SoundUtil.playSound;
import static net.foulest.repairkit.util.SwingUtil.createLabel;

public class AutomaticRepairs extends JPanel {

    private final JCheckBox[] progressCheckboxes;
    private final JButton runButton;

    /**
     * Creates the Automatic Repairs panel.
     */
    public AutomaticRepairs() {
        // Sets the panel's layout to null.
        debug("Setting the Automatic Repairs panel layout to null...");
        setLayout(null);

        // Creates the title label.
        debug("Creating the Automatic Repairs title label...");
        JLabel titleLabel = createLabel("Automatic Repairs",
                new Rectangle(20, 15, 200, 30),
                new Font(ARIAL, Font.BOLD, 18)
        );
        add(titleLabel);

        // Creates the description label.
        debug("Creating the Automatic Repairs description label...");
        JLabel descriptionLabel = createLabel("<html>RepairKit will automatically apply registry settings,"
                        + " disable telemetry settings, optimize Windows services, remove bloatware, repair disk"
                        + " issues, and more.<br><br>Automatic repairs are recommended to be run once per month.</html>",
                new Rectangle(20, 40, 500, 100),
                new Font(ARIAL, Font.PLAIN, 14)
        );
        descriptionLabel.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
        add(descriptionLabel);

        // Creates the run button.
        debug("Creating the Automatic Repairs run button...");
        runButton = new JButton("Run Automatic Repairs");
        runButton.setBounds(20, 145, 200, 40);
        runButton.setFont(new Font(ARIAL, Font.BOLD, 14));
        runButton.setBackground(new Color(0, 120, 215));
        runButton.setForeground(Color.WHITE);
        runButton.addActionListener(e -> runAutomaticRepairs());
        add(runButton);

        // Creates the progress label.
        debug("Creating the Automatic Repairs progress label...");
        JLabel progressLabel = createLabel("Progress:",
                new Rectangle(20, 205, 200, 30),
                new Font(ARIAL, Font.BOLD, 14)
        );
        add(progressLabel);

        String[] progressItems = {
                "Delete System Policies",
                "Remove Bloatware",
                "Repair Disk Issues",
                "Run Registry Tweaks",
                "Run Service Tweaks",
                "Run Settings Tweaks",
                "Scan with Windows Defender"
        };

        // Creates the progress checkboxes.
        debug("Creating the Automatic Repairs progress checkboxes...");
        progressCheckboxes = new JCheckBox[progressItems.length];
        for (int i = 0; i < progressItems.length; i++) {
            progressCheckboxes[i] = new JCheckBox(progressItems[i]);
            progressCheckboxes[i].setFont(new Font(ARIAL, Font.PLAIN, 14));
            progressCheckboxes[i].setBounds(16, 235 + (i * 28), 500, 30);
            progressCheckboxes[i].setEnabled(false);
        }

        // Adds the progress checkboxes to the panel.
        debug("Adding the Automatic Repairs progress checkboxes to the panel...");
        for (JCheckBox checkbox : progressCheckboxes) {
            add(checkbox);
        }

        // Sets the panel's border.
        debug("Setting the Automatic Repairs panel border...");
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    /**
     * Runs the automatic repairs.
     */
    private void runAutomaticRepairs() {
        debug("Running Automatic Repairs...");

        // Disables the run button.
        debug("Disabling the run button...");
        runButton.setEnabled(false);
        runButton.setBackground(Color.LIGHT_GRAY);

        // Creates a new thread to run the automatic repairs.
        Thread repairThread = new Thread(() -> {
            try {
                // Checks if the operating system is outdated.
                debug("Checking if the operating system is outdated...");
                if (RepairKit.isOutdatedOperatingSystem()) {
                    playSound(ERROR_SOUND);
                    JOptionPane.showMessageDialog(null, OUTDATED_OS_MESSAGE, OUTDATED_OS_TITLE, JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Creates a restore point.
                debug("Creating a restore point...");
                createRestorePoint();

                // Deletes system policies.
                debug("Deleting system policies...");
                deleteSystemPolicies();
                SwingUtilities.invokeLater(() -> progressCheckboxes[0].setSelected(true));

                // Creates tasks for the executor.
                List<Runnable> tasks = Arrays.asList(
                        () -> {
                            // Removes bloatware if the system is not in safe mode.
                            if (!RepairKit.isSafeMode()) {
                                removeBloatware();
                            }
                            SwingUtilities.invokeLater(() -> progressCheckboxes[1].setSelected(true));
                        },

                        () -> {
                            // Runs registry tweaks.
                            runRegistryTweaks();
                            SwingUtilities.invokeLater(() -> progressCheckboxes[3].setSelected(true));
                        },

                        () -> {
                            // Runs service tweaks.
                            runServiceTweaks();
                            SwingUtilities.invokeLater(() -> progressCheckboxes[4].setSelected(true));
                        },

                        () -> {
                            // Runs settings tweaks.
                            runSettingsTweaks();
                            SwingUtilities.invokeLater(() -> progressCheckboxes[5].setSelected(true));

                            // Repairs disk issues.
                            // This has to be done after the DISM tweaks in the settings tweaks.
                            repairDiskIssues();
                            SwingUtilities.invokeLater(() -> progressCheckboxes[2].setSelected(true));
                        },

                        () -> {
                            // Runs Windows Defender tweaks and performs a Quick Scan.
                            runWindowsDefenderTweaks();
                            SwingUtilities.invokeLater(() -> progressCheckboxes[6].setSelected(true));
                        }
                );

                // Executes tasks using TaskUtil.
                TaskUtil.executeTasks(tasks);
                debug("Completed Automatic Repairs.");

                // Displays a message dialog.
                debug("Displaying the Automatic Repairs completion dialog...");
                playSound("win.sound.exclamation");
                JOptionPane.showMessageDialog(null,
                        "Automatic repairs have been completed.",
                        "Finished", JOptionPane.QUESTION_MESSAGE);

                // Resets the run button.
                debug("Resetting the run button...");
                runButton.setEnabled(true);
                runButton.setBackground(new Color(0, 120, 215));

                // Resets the checkboxes.
                debug("Resetting the Automatic Repairs progress checkboxes...");
                for (JCheckBox checkbox : progressCheckboxes) {
                    checkbox.setSelected(false);
                }
            } catch (Exception ex) {
                debug("[WARN] An error occurred while running Automatic Repairs.");
                ex.printStackTrace();
            }
        });

        // Starts the repair thread.
        repairThread.start();
    }

    /**
     * Creates a restore point.
     */
    private static void createRestorePoint() {
        runCommand("wmic.exe /Namespace:\\\\root\\default Path SystemRestore Call CreateRestorePoint"
                + " \"RepairKit Automatic Repairs\", 100, 7", false);
    }

    /**
     * Deletes any existing system policies.
     */
    public static void deleteSystemPolicies() {
        debug("Deleting system policies...");

        // Creates tasks for the executor.
        List<Runnable> tasks = Arrays.asList(
                () -> {
                    // Deletes specific system policies.
                    deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Policies\\Microsoft\\MMC");
                    deleteRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Policies\\Microsoft\\Windows\\System");
                    deleteRegistryKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Google\\Chrome");
                },

                () -> {
                    // Remove restrictions on certain system tools and settings.
                    deleteRegistryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\NonEnum", "{645FF040-5081-101B-9F08-00AA002F954E}");
                    deleteRegistryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "DisableRegistryTools");
                    deleteRegistryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "DisableTaskMgr");
                    deleteRegistryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\System", "DisableCMD");

                    // Remove restrictions on Control Panel and Folder Options.
                    deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\Explorer", "DisallowCpl");
                    deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\Explorer", "NoFolderOptions");

                    // Restore default icon settings and visibility of hidden files and folders.
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "Icons Only", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced\\Folder\\Hidden\\SHOWALL", "CheckedValue", 1);

                    // Enable System Restore functionality.
                    deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Windows NT\\System Restore", "DisableConfig");
                    deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Windows NT\\System Restore", "DisableSR");

                    // Remove restrictions on certain drivers.
                    deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Class\\{4D36E965-E325-11CE-BFC1-08002BE10318}", "LowerFilters");
                    deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Class\\{4D36E965-E325-11CE-BFC1-08002BE10318}", "UpperFilters");
                },

                () -> {
                    // Restores Winlogon system policies to default settings.
                    setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon", "Shell", "explorer.exe");
                    setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon", "Userinit", "C:\\Windows\\system32\\userinit.exe,");
                },

                () -> {
                    // Restores User Account Control (UAC) settings to default.
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\NonEnum", "{645FF040-5081-101B-9F08-00AA002F954E}", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "ConsentPromptBehaviorAdmin", 5);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "ConsentPromptBehaviorUser", 1);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "EnableLUA", 1);
                }
        );

        // Executes tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);
        debug("Completed deleting system policies.");
    }

    /**
     * Removes various bloatware applications from the system.
     */
    private static void removeBloatware() {
        debug("Removing installed bloatware apps...");
        List<String> output = getPowerShellCommandOutput("(Get-AppxPackage).ForEach({ $_.Name })", false, false);
        Set<String> installedPackages = new HashSet<>(output);

        String[] appPackages = {
                // Pre-installed Windows apps
                "46928bounde.EclipseManager", // Eclipse Manager
                "9E2F88E3.Twitter", // Twitter
                "ActiproSoftwareLLC.562882FEEB491", // Actipro Software
                "ClearChannelRadioDigital.iHeartRadio", // iHeartRadio
                "D5EA27B7.Duolingo-LearnLanguagesforFree", // Duolingo
                "Flipboard.Flipboard", // Flipboard
                "Microsoft.3DBuilder", // 3D Builder
                "Microsoft.549981C3F5F10", // Cortana
                "Microsoft.Advertising.Xaml", // Microsoft Advertising
                "Microsoft.BingFinance", // Bing Finance
                "Microsoft.BingNews", // Bing News
                "Microsoft.BingSports", // Bing Sports
                "Microsoft.BingWeather", // Bing Weather
                "Microsoft.CommsPhone", // Communications Phone
                "Microsoft.GetHelp", // Get Help
                "Microsoft.Getstarted", // Get Started
                "Microsoft.GroupMe10", // GroupMe
                "Microsoft.MSPaint", // Paint 3D
                "Microsoft.Messaging", // Messaging
                "Microsoft.Microsoft3DViewer", // Microsoft 3D Viewer
                "Microsoft.MicrosoftSolitaireCollection", // Microsoft Solitaire Collection
                "Microsoft.MixedReality.Portal", // Mixed Reality Portal
                "Microsoft.NetworkSpeedTest", // Network Speed Test
                "Microsoft.Office.OneNote", // OneNote
                "Microsoft.Office.Sway", // Office Sway
                "Microsoft.OneConnect", // OneConnect
                "Microsoft.People", // People
                "Microsoft.Print3D", // Print 3D
                "Microsoft.RemoteDesktop", // Remote Desktop
                "Microsoft.SkypeApp", // Skype
                "Microsoft.Todos", // Microsoft To-Do
                "Microsoft.Wallet", // Wallet
                "Microsoft.Windows.Ai.Copilot.Provider", // AI Copilot Provider
                "Microsoft.Windows.Phone", // Windows Phone
                "Microsoft.WindowsFeedbackHub", // Feedback Hub
                "Microsoft.WindowsMaps", // Maps
                "Microsoft.WindowsPhone", // Windows Phone
                "PandoraMediaInc.29680B314EFC2", // Pandora
                "ShazamEntertainmentLtd.Shazam", // Shazam
                "king.com.CandyCrushSaga", // Candy Crush Saga
                "king.com.CandyCrushSodaSaga", // Candy Crush Soda Saga

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
                .toList();

        // Match installed packages against the patterns
        List<String> packagesToRemove = installedPackages.stream()
                .filter(installedPackage -> patternsToRemove.stream().anyMatch(pattern -> pattern.matcher(installedPackage).matches()))
                .toList();

        // If no packages to remove, simply exit
        if (packagesToRemove.isEmpty()) {
            return;
        }

        // Create tasks for the executor.
        List<Runnable> tasks = packagesToRemove.stream().map(appPackage -> (Runnable) () -> {
            debug("Removing bloatware app: " + appPackage);
            runPowerShellCommand("Get-AppxPackage '" + appPackage + "' | Remove-AppxPackage", false);
        }).toList();

        // Executes tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);
        debug("Completed removing installed bloatware apps.");
    }

    /**
     * Repairs various disk issues.
     */
    private static void repairDiskIssues() {
        debug("Repairing WMI repository...");

        if (getCommandOutput("winmgmt /verifyrepository", false, false).toString().contains("not consistent")
                && getCommandOutput("winmgmt /salvagerepository", false, false).toString().contains("not consistent")) {
            runCommand("winmgmt /resetrepository", false);
            debug("Repaired WMI repository.");
        } else {
            debug("WMI repository is already consistent.");
        }

        debug("Repairing disk issues with SFC...");

        if (getCommandOutput("sfc /scannow", false, false).toString().contains("Windows Resource Protection found")) {
            debug("Found disk issues with SFC. Repairing with DISM...");
            runCommand("DISM /Online /Cleanup-Image /RestoreHealth", false);
            debug("Repaired disk issues with DISM.");
        } else {
            debug("No disk issues found with SFC.");
        }
    }

    /**
     * Runs tweaks to the Windows registry.
     */
    private static void runRegistryTweaks() {
        debug("Running registry tweaks...");

        List<Runnable> tasks = Arrays.asList(
                // Disables telemetry and annoyances.
                () -> {
                    deleteRegistryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Siuf\\Rules", "PeriodInNanoSeconds");
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\International\\User Profile", "HttpAcceptLanguageOptOut", 1);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\InputPersonalization", "RestrictImplicitInkCollection", 1);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\InputPersonalization", "RestrictImplicitTextCollection", 1);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\InputPersonalization\\TrainedDataStore", "HarvestContacts", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Input\\Settings", "InsightsEnabled", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Input\\TIPC", "Enabled", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\MediaPlayer\\Preferences", "UsageTracking", 0);
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
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Diagnostics\\DiagTrack", "ShowedToastAtLevel", 1);
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
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Remote Assistance", "fAllowFullControl", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Remote Assistance", "fAllowToGetHelp", 0);
                    setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Image File Execution Options\\'CompatTelRunner.exe'", "Debugger", "%windir%\\System32\\taskkill.exe");
                    setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Image File Execution Options\\'DeviceCensus.exe'", "Debugger", "%windir%\\System32\\taskkill.exe");
                },

                // Patches security vulnerabilities.
                () -> {
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon\\SpecialAccounts\\UserList", "Administrator", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon\\SpecialAccounts\\UserList", "Guest", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows Script Host\\Settings", "Enabled", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\WindowsUpdate\\UX\\Settings", "AllowMUUpdateService", 1);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\Explorer", "NoDriveTypeAutoRun", 255);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\FVE", "UseAdvancedStartup", 1);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Windows\\Explorer", "NoDataExecutionPrevention", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Windows\\Installer", "AlwaysInstallElevated", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Windows\\SYSTEM", "DisableHHDEP", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Policies\\Microsoft\\Windows\\WinRM\\Client", "AllowBasic", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\LSA", "RestrictAnonymous", 1);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Lsa", "LmCompatibilityLevel", 5);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Lsa", "NoLMHash", 1);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\kernel", "DisableExceptionChainValidation", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\kernel", "RestrictAnonymousSAM", 1);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Terminal Server", "fDenyTSConnections", 1);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\LanManServer\\Parameters", "RestrictNullSessAccess", 1);
                },

                // Deletes telemetry & recent files logs.
                () -> {
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
                },

                // Disables certain search and Cortana functions.
                () -> {
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Speech_OneCore\\Preferences", "ModelDownloadAllowed", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Speech_OneCore\\Preferences", "VoiceActivationDefaultOn", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Speech_OneCore\\Preferences", "VoiceActivationEnableAboveLockscreen", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Speech_OneCore\\Preferences", "VoiceActivationOn", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search ", "BingSearchEnabled", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "CanCortanaBeEnabled", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "CortanaConsent", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "CortanaEnabled", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "CortanaInAmbientMode", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "DeviceHistoryEnabled", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "HistoryViewEnabled", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "VoiceShortcut", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\SearchSettings", "IsDeviceSearchHistoryEnabled", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\SearchSettings", "SafeSearchMode", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\SearchSettings", "IsDynamicSearchBoxEnabled", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\PolicyManager\\default\\Experience\\AllowCortana", "value", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\OOBE", "DisableVoice", 1);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "BingSearchEnabled", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Search", "CortanaEnabled", 0);
                },

                // Disables Windows error reporting.
                () -> {
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\Windows Error Reporting", "Disabled", 1);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\Windows Error Reporting", "DontSendAdditionalData", 1);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\Windows Error Reporting", "LoggingDisabled", 1);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\Windows Error Reporting\\Consent", "DefaultConsent", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\Windows Error Reporting\\Consent", "DefaultOverrideBehavior", 1);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\Windows Error Reporting\\Consent", "DefaultOverrideBehavior", 1);
                },

                // Resets the Recycle Bin's icons.
                () -> {
                    setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CLSID\\{645FF040-5081-101B-9F08-00AA002F954E}\\DefaultIcon", "(Default)", "C:\\Windows\\System32\\imageres.dll,-54");
                    setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CLSID\\{645FF040-5081-101B-9F08-00AA002F954E}\\DefaultIcon", "empty", "C:\\Windows\\System32\\imageres.dll,-55");
                    setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CLSID\\{645FF040-5081-101B-9F08-00AA002F954E}\\DefaultIcon", "full", "C:\\Windows\\System32\\imageres.dll,-54");
                },

                // Disables certain File Explorer features.
                () -> {
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer", "ShowFrequent", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "DontUsePowerShellOnWinX", 1);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "HideFileExt", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "ShowSyncProviderNotifications", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "Start_TrackDocs", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "Start_TrackProgs", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "TaskbarDa", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "TaskbarMn", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "ShowCopilotButton", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "ShowCortanaButton", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "Start_AccountNotifications", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "Start_IrisRecommendations", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\AutoplayHandlers", "DisableAutoplay", 1);
                    setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FolderDescriptions\\{31C0DD25-9439-4F12-BF41-7FF4EDA38722}\\PropertyBag", "ThisPCPolicy", "Hide");
                    setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FolderDescriptions\\{31C0DD25-9439-4F12-BF41-7FF4EDA38722}\\PropertyBag", "ThisPCPolicy", "Hide");
                },

                // Patches Spectre & Meltdown security vulnerabilities.
                () -> {
                    String cpuName = getCommandOutput("wmic cpu get name", false, false).toString();
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Memory Management", "FeatureSettingsOverrideMask", 3);
                    setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Virtualization", "MinVmVersionForCpuBasedMitigations", "1.0");

                    if (cpuName.contains("Intel")) {
                        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Memory Management", "FeatureSettingsOverride", 8);
                    } else if (cpuName.contains("AMD")) {
                        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Memory Management", "FeatureSettingsOverride", 72);
                    } else if (cpuName.contains("ARM")) {
                        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Memory Management", "FeatureSettingsOverride", 64);
                    }
                },

                // Disables the weather and news widget.
                () -> {
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Feeds", "EnableFeeds", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Feeds", "ShellFeedsTaskbarViewMode", 2);
                },

                // Disables Game DVR.
                () -> {
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\GameDVR", "AppCaptureEnabled", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SYSTEM\\GameConfigStore", "GameDVR_Enabled", 0);
                },

                // Disables lock screen toasts.
                () -> {
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Notifications\\Settings", "NOC_GLOBAL_SETTING_ALLOW_TOASTS_ABOVE_LOCK", 0);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\PushNotifications", "LockScreenToastEnabled", 0);
                },

                // Enables Storage Sense.
                () -> {
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "01", 1);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "04", 1);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "08", 1);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "2048", 1);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "256", 30);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "32", 1);
                    setRegistryIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\StorageSense\\Parameters\\StoragePolicy", "512", 30);
                },

                // Modifies Windows networking settings.
                () -> {
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\LanmanServer\\Parameters", "IRPStackSize", 30);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters", "DefaultTTL", 64);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters", "MaxUserPort", 65534);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters", "Tcp1323Opts", 1);
                },

                // Disables sticky keys.
                () -> {
                    setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Accessibility\\ToggleKeys", "Flags", "58");
                    setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Accessibility\\StickyKeys", "Flags", "506");
                    setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Accessibility\\Keyboard Response", "Flags", "122");
                },

                // Disables mouse acceleration.
                () -> {
                    setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Mouse", "MouseSpeed", "0");
                    setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Mouse", "MouseThreshold1", "0");
                    setRegistryStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Mouse", "MouseThreshold2", "0");
                },

                // Restores the keyboard layout.
                () -> deleteRegistryValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Keyboard Layout", "Scancode Map"),

                // Fixes a battery visibility issue.
                () -> {
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Power", "EnergyEstimationEnabled", 1);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Power", "EnergyEstimationDisabled", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Power", "UserBatteryDischargeEstimator", 0);
                },

                // Sets certain services to start automatically.
                () -> {
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows Search", "SetupCompletedSuccessfully", 0);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\WSearch", "Start", 2);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\VSS", "Start", 2);
                    setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\PlugPlay", "Start", 2);
                }
        );

        // Execute tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);
        debug("Completed registry tweaks.");
    }

    /**
     * Runs various tweaks to the Windows services.
     */
    private static void runServiceTweaks() {
        debug("Running service tweaks...");

        List<String[]> serviceList = Arrays.asList(
                new String[]{"DiagTrack", "Connected User Experiences and Telemetry"},
                new String[]{"MapsBroker", "Downloaded Maps Manager"},
                new String[]{"PcaSvc", "Program Compatibility Assistant Service"},
                new String[]{"RemoteAccess", "Remote Access"},
                new String[]{"RemoteRegistry", "Remote Registry"},
                new String[]{"RetailDemo", "Retail Demo"},
                new String[]{"VSStandardCollectorService150", "Visual Studio Standard Collector Service"},
                new String[]{"WMPNetworkSvc", "Windows Media Player Network Sharing Service"},
                new String[]{"diagnosticshub.standardcollector.service", "Diagnostics Hub Standard Collector Service"},
                new String[]{"diagsvc", "Diagnostic Execution Service"},
                new String[]{"dmwappushservice", "WAP Push Message Routing Service"},
                new String[]{"fhsvc", "File History Service"},
                new String[]{"lmhosts", "TCP/IP NetBIOS Helper"},
                new String[]{"wercplsupport", "Problem Reports Control Panel Support"},
                new String[]{"wersvc", "wersvc"}
        );

        // Create tasks for the executor.
        List<Runnable> tasks = serviceList.stream().map(service -> (Runnable) () -> {
            String serviceName = service[0];
            debug("Disabling service: " + serviceName);
            runCommand("sc stop \"" + serviceName + "\"", true);
            runCommand("sc config \"" + serviceName + "\" start=disabled", true);
        }).toList();

        // Executes tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);
        debug("Completed service tweaks.");
    }

    /**
     * Runs tweaks to Windows settings.
     */
    private static void runSettingsTweaks() {
        debug("Running settings tweaks...");

        List<Runnable> tasks = Arrays.asList(
                () -> {
                    // Fixes micro-stuttering in games.
                    runCommand("bcdedit /set useplatformtick yes", true);
                    runCommand("bcdedit /deletevalue useplatformclock", true);

                    // Enables scheduled defrag.
                    runCommand("schtasks /Change /ENABLE /TN \"\\Microsoft\\Windows\\Defrag\\ScheduledDefrag\"", true);

                    // Disables various telemetry tasks.
                    runCommand("schtasks /change /TN \"Microsoft\\Windows\\Application Experience\\Microsoft Compatibility Appraiser\" /disable", true);
                    runCommand("schtasks /change /TN \"Microsoft\\Windows\\Application Experience\\ProgramDataUpdater\" /disable", true);
                    runCommand("schtasks /change /TN \"Microsoft\\Windows\\Application Experience\\StartupAppTask\" /disable", true);
                    runCommand("schtasks /change /TN \"Microsoft\\Windows\\Customer Experience Improvement Program\\Consolidator\" /disable", true);
                    runCommand("schtasks /change /TN \"Microsoft\\Windows\\Customer Experience Improvement Program\\UsbCeip\" /disable", true);
                    runCommand("schtasks /change /TN \"Microsoft\\Windows\\Device Information\\Device\" /disable", true);
                    runCommand("schtasks /change /TN \"Microsoft\\Windows\\Windows Error Reporting\\QueueReporting\" /disable", true);
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
                },

                () -> {
                    // Disables NetBios for all interfaces.
                    String baseKeyPath = "SYSTEM\\CurrentControlSet\\services\\NetBT\\Parameters\\Interfaces";
                    java.util.List<String> subKeys = listSubKeys(WinReg.HKEY_LOCAL_MACHINE, baseKeyPath);

                    for (String subKey : subKeys) {
                        String fullPath = baseKeyPath + "\\" + subKey;
                        setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, fullPath, "NetbiosOptions", 2);
                    }
                },

                () -> {
                    // Resets Windows Media Player.
                    runCommand("regsvr32 /s jscript.dll", false);
                    runCommand("regsvr32 /s vbscript.dll", true);
                },

                () -> {
                    // Patches security vulnerabilities.
                    if (!RepairKit.isWindowsUpdateInProgress()) {
                        String[] features = {
                                "Internet-Explorer-Optional-amd64",
                                "MicrosoftWindowsPowerShellV2",
                                "MicrosoftWindowsPowerShellV2Root",
                                "SMB1Protocol",
                                "SMB1Protocol-Client",
                                "SMB1Protocol-Deprecation",
                                "SMB1Protocol-Server",
                                "TelnetClient",
                        };

                        for (String feature : features) {
                            if (getPowerShellCommandOutput("Get-WindowsOptionalFeature -FeatureName '" + feature
                                    + "' -Online | Select-Object -Property State", false, false).toString().contains("Enabled")) {
                                runCommand("DISM /Online /Disable-Feature /FeatureName:\"" + feature + "\" /NoRestart", false);
                            }
                        }

                        String[] capabilities = {
                                "App.StepsRecorder~~~~*",
                                "Browser.InternetExplorer~~~~*",
                                "MathRecognizer~~~~*",
                                "Microsoft.Windows.WordPad~~~~*",
                                "Print.Fax.Scan~~~~*",
                        };

                        // Check if the capability (any version) is enabled
                        for (String capability : capabilities) {
                            if (getPowerShellCommandOutput("Get-WindowsCapability -Name '" + capability
                                    + "' -Online | Where-Object State -eq 'Installed'", false, false).toString().contains("Installed")) {
                                runCommand("DISM /Online /Remove-Capability /CapabilityName:\"" + capability + "\" /NoRestart", false);
                            }
                        }
                    }
                });

        // Execute tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);
        debug("Completed settings tweaks.");
    }

    /**
     * Runs various tweaks to Windows Defender and initiates a Quick Scan.
     */
    private static void runWindowsDefenderTweaks() {
        debug("Running Windows Defender tweaks...");

        List<Runnable> tasks = Arrays.asList(
                // Sets Windows Firewall to recommended settings
                () -> runPowerShellCommand("Set-NetFirewallProfile"
                        + " -Profile Domain,Private,Public"
                        + " -Enabled True", false),

                // Sets Windows Defender to recommended settings
                () -> runPowerShellCommand("Set-MpPreference"
                        + " -CloudBlockLevel 4"
                        + " -CloudExtendedTimeout 10"
                        + " -DisableArchiveScanning 0"
                        + " -DisableBehaviorMonitoring 0"
                        + " -DisableBlockAtFirstSeen 0"
                        + " -DisableEmailScanning 0"
                        + " -DisableIOAVProtection 0"
                        + " -DisableRealtimeMonitoring 0"
                        + " -DisableRemovableDriveScanning 0"
                        + " -DisableScanningMappedNetworkDrivesForFullScan 0"
                        + " -DisableScanningNetworkFiles 0"
                        + " -DisableScriptScanning 0"
                        + " -EnableFileHashComputation 0"
                        + " -EnableLowCpuPriority 0"
                        + " -EnableNetworkProtection 1"
                        + " -HighThreatDefaultAction Quarantine"
                        + " -LowThreatDefaultAction Block"
                        + " -MAPSReporting 2"
                        + " -ModerateThreatDefaultAction Clean"
                        + " -PUAProtection 1"
                        + " -ScanAvgCPULoadFactor 50"
                        + " -SevereThreatDefaultAction Remove"
                        + " -SignatureBlobUpdateInterval 120"
                        + " -SubmitSamplesConsent 3", false),

                // Sets Windows Defender ASR rules to recommended settings
                () -> runPowerShellCommand("Add-MpPreference"
                        + " -AttackSurfaceReductionRules_Ids "
                        + "26190899-1602-49e8-8b27-eb1d0a1ce869," // ASR: Block Adobe Reader from creating child processes
                        + "3b576869-a4ec-4529-8536-b80a7769e899," // ASR: Block all Office applications from creating child processes
                        + "5beb7efe-fd9a-4556-801d-275e5ffc04cc," // ASR: Block executable content from email client and webmail
                        + "75668c1f-73b5-4cf0-bb93-3ecf5cb7cc84," // ASR: Block execution of potentially obfuscated scripts
                        + "7674ba52-37eb-4a4f-a9a1-f0f9a1619a2c," // ASR: Block JavaScript or VBScript from launching downloaded executable content
                        + "92e97fa1-2edf-4476-bdd6-9dd0b4dddc7b," // ASR: Block Office applications from creating executable content
                        + "b2b3f03d-6a65-4f7b-a9c7-1c7ef74a9ba4," // ASR: Block Office applications from injecting code into other processes
                        + "be9ba2d9-53ea-4cdc-84e5-9b1eeee46550," // ASR: Block Office communication application from creating child processes
                        + "c1db55ab-c21a-4637-bb3f-a12568109d35," // ASR: Block persistence through WMI event subscription
                        + "d3e037e1-3eb8-44c8-a917-57927947596d," // ASR: Block untrusted and unsigned processes that run from USB
                        + "d4f940ab-401b-4efc-aadc-ad5f3c50688a," // ASR: Block Win32 API calls from Office macros
                        + "e6db77e5-3df2-4cf1-b95a-636979351e5b" // ASR: Use advanced protection against ransomware
                        + " -AttackSurfaceReductionRules_Actions Enabled", false),

                // Disables certain ASR rules from blocking
                () -> runPowerShellCommand("Add-MpPreference"
                        + " -AttackSurfaceReductionRules_Ids "
                        + "01443614-cd74-433a-b99e-2ecdc07bfc25," // ASR: Don't block credential stealing from the Windows local security authority subsystem
                        + "9e6c4e1f-7d60-472f-ba1a-a39ef669e4b2," // ASR: Don't block executable files from running unless they meet a prevalence, age, or trusted list criterion
                        + "d1e49aac-8f56-4280-b9ba-993a6d77406c" // ASR: Don't block process creations originating from PSExec and WMI commands
                        + " -AttackSurfaceReductionRules_Actions Disabled", false),

                // Disables certain ASR rules from warning
                () -> runPowerShellCommand("Add-MpPreference"
                        + " -AttackSurfaceReductionRules_Ids "
                        + "56a863a9-875e-4185-98a7-b882c64b5ce5," // ASR: Warn against abuse of exploited vulnerable signed drivers
                        + "a8f5898e-1dc8-49a9-9878-85004b8a61e6" // ASR: Warn against Webshell creation for servers
                        + " -AttackSurfaceReductionRules_Actions Warn", false)
        );

        // Execute tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);
        debug("Completed Windows Defender tweaks.");

        // Updates Windows Defender signatures
        if (!RepairKit.isSafeMode()) {
            debug("Updating Windows Defender signatures...");
            runCommand("\"C:\\Program Files\\Windows Defender\\MpCmdRun.exe\" -SignatureUpdate", false);
            debug("Completed Windows Defender signature update.");
        }

        // Runs a quick scan with Windows Defender
        debug("Running a quick scan with Windows Defender...");
        runCommand("\"C:\\Program Files\\Windows Defender\\MpCmdRun.exe\" -Scan -ScanType 1", false);
        debug("Completed Windows Defender quick scan.");
    }
}
