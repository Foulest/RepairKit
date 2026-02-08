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

import com.sun.jna.platform.win32.WinReg;
import net.foulest.repairkit.RepairKit;
import net.foulest.repairkit.util.*;
import net.foulest.repairkit.util.config.ConfigLoader;
import net.foulest.repairkit.util.config.tasks.types.BloatwareTaskRunner;
import net.foulest.repairkit.util.config.tasks.types.FeaturesTaskRunner;
import net.foulest.repairkit.util.config.tasks.types.RegistryTaskRunner;
import net.foulest.repairkit.util.config.tasks.types.ServicesTaskRunner;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Automatic Repairs panel.
 *
 * @author Foulest
 */
public class AutomaticRepairs extends JPanel {

    /**
     * The progress checkboxes that display the status of the automatic repairs.
     */
    private final JCheckBox @NotNull [] progressCheckboxes;

    /**
     * The run button for the automatic repairs.
     */
    private final @NotNull JButton runButton;

    /**
     * Creates the Automatic Repairs panel.
     */
    @SuppressWarnings("NestedMethodCall")
    public AutomaticRepairs() {
        // Sets the panel's layout to null.
        DebugUtil.debug("Setting the Automatic Repairs panel layout to null...");
        setLayout(null);

        // Creates the title label.
        DebugUtil.debug("Creating the Automatic Repairs title label...");
        @NotNull JLabel titleLabel = SwingUtil.createLabel("Automatic Repairs",
                new Rectangle(20, 15, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 18)
        );
        add(titleLabel);

        // Creates the description label.
        DebugUtil.debug("Creating the Automatic Repairs description label...");
        @NotNull JLabel descriptionLabel = SwingUtil.createLabel("<html>RepairKit will automatically apply registry settings,"
                        + " disable telemetry settings, optimize Windows services, remove bloatware, repair disk"
                        + " issues, and more.<br><br>Automatic repairs are recommended to be run once per month.</html>",
                new Rectangle(20, 40, 500, 100),
                new Font(ConstantUtil.ARIAL, Font.PLAIN, 14)
        );
        descriptionLabel.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
        add(descriptionLabel);

        // Creates the run button.
        DebugUtil.debug("Creating the Automatic Repairs run button...");
        runButton = new JButton("Run Automatic Repairs");
        runButton.setBounds(20, 145, 200, 40);
        runButton.setFont(new Font(ConstantUtil.ARIAL, Font.BOLD, 14));
        runButton.setBackground(new Color(0, 120, 215));
        runButton.setForeground(Color.WHITE);
        runButton.addActionListener(e -> runAutomaticRepairs());
        add(runButton);

        // Creates the progress label.
        DebugUtil.debug("Creating the Automatic Repairs progress label...");
        @NotNull JLabel progressLabel = SwingUtil.createLabel("Progress:",
                new Rectangle(20, 205, 200, 30),
                new Font(ConstantUtil.ARIAL, Font.BOLD, 14)
        );
        add(progressLabel);

        String @NotNull [] progressItems = {
                "Delete System Policies",
                "Run Registry Tweaks",
                "Run System Tweaks",
                "Run Features Tweaks",
                "Run Services Tweaks",
                "Remove Junk Files",
                "Remove Bloatware",
                "Repair Disk Issues",
                "Update Outdated Programs"
        };

        // Creates the progress checkboxes.
        DebugUtil.debug("Creating the Automatic Repairs progress checkboxes...");
        progressCheckboxes = new JCheckBox[progressItems.length];
        int numberOfItems = progressItems.length;

        int x = 16;
        int y = 235;
        int maxWidth = 450;
        int checkboxHeight = 28;
        int checkboxWidth = 220; // Adjust width as needed

        for (int i = 0; i < numberOfItems; i++) {
            progressCheckboxes[i] = new JCheckBox(progressItems[i]);
            progressCheckboxes[i].setFont(new Font(ConstantUtil.ARIAL, Font.PLAIN, 14));
            progressCheckboxes[i].setBounds(x, y, checkboxWidth, checkboxHeight);
            progressCheckboxes[i].setSelected(false);

            y += checkboxHeight;
            if (y + checkboxHeight > maxWidth) {
                y = 235;
                x += checkboxWidth;
            }
        }

        // Adds the progress checkboxes to the panel.
        DebugUtil.debug("Adding the Automatic Repairs progress checkboxes to the panel...");
        for (JCheckBox checkbox : progressCheckboxes) {
            add(checkbox);
        }

        // Sets the panel's border.
        DebugUtil.debug("Setting the Automatic Repairs panel border...");
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    /**
     * Runs the automatic repairs.
     */
    @SuppressWarnings("NestedMethodCall")
    private void runAutomaticRepairs() {
        DebugUtil.debug("Running Automatic Repairs...");

        // Disables the run button.
        DebugUtil.debug("Disabling the run button...");
        runButton.setEnabled(false);
        runButton.setBackground(Color.LIGHT_GRAY);

        // Creates a new thread to run the automatic repairs.
        @NotNull Thread repairThread = new Thread(() -> {
            try {
                // Sets the state of all checkboxes to variables.
                boolean deleteSystemPolicies = progressCheckboxes[0].isSelected();
                boolean runRegistryTweaks = progressCheckboxes[1].isSelected();
                boolean runSystemTweaks = progressCheckboxes[2].isSelected();
                boolean runFeaturesTweaks = progressCheckboxes[3].isSelected();
                boolean runServicesTweaks = progressCheckboxes[4].isSelected();
                boolean removeJunkFiles = progressCheckboxes[5].isSelected();
                boolean removeBloatware = progressCheckboxes[6].isSelected();
                boolean repairDiskIssues = progressCheckboxes[7].isSelected();
                boolean updateOutdatedPrograms = progressCheckboxes[8].isSelected();

                // Disables all checkboxes.
                for (@NotNull JCheckBox checkbox : progressCheckboxes) {
                    checkbox.setEnabled(false);
                    checkbox.setSelected(false);
                }

                // Checks if no repair options are selected.
                if (!deleteSystemPolicies
                        && !runRegistryTweaks
                        && !runSystemTweaks
                        && !runFeaturesTweaks
                        && !runServicesTweaks
                        && !removeJunkFiles
                        && !removeBloatware
                        && !repairDiskIssues
                        && !updateOutdatedPrograms) {
                    SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                    JOptionPane.showMessageDialog(null, "Please select at least one repair option.", "Error", JOptionPane.ERROR_MESSAGE);

                    // Resets the run button.
                    DebugUtil.debug("Resetting the run button...");
                    runButton.setEnabled(true);
                    runButton.setBackground(new Color(0, 120, 215));

                    // Resets the checkboxes.
                    DebugUtil.debug("Resetting the Automatic Repairs progress checkboxes...");
                    for (@NotNull JCheckBox checkbox : progressCheckboxes) {
                        checkbox.setEnabled(true);
                        checkbox.setSelected(false);
                    }
                    return;
                }

                // Checks if the operating system is outdated.
                DebugUtil.debug("Checking if the operating system is outdated...");
                if (RepairKit.isOutdatedOperatingSystem()) {
                    SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                    JOptionPane.showMessageDialog(null, ConstantUtil.OUTDATED_OS_MESSAGE, ConstantUtil.OUTDATED_OS_TITLE, JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Creates a restore point.
                runButton.setText("Creating Restore Point");
                createRestorePoint();
                runButton.setText("Running Repairs...");

                // Deletes system policies.
                if (deleteSystemPolicies) {
                    deleteSystemPolicies();
                    SwingUtilities.invokeLater(() -> progressCheckboxes[0].setSelected(true));
                }

                // Creates tasks for the executor.
                @NotNull List<Runnable> tasks = List.of(
                        () -> {
                            if (runRegistryTweaks) {
                                // Runs registry tweaks.
                                runRegistryTweaks();
                                SwingUtilities.invokeLater(() -> progressCheckboxes[1].setSelected(true));
                            }
                        },

                        () -> {
                            if (runSystemTweaks) {
                                // Runs system tweaks.
                                runSystemTweaks();
                                SwingUtilities.invokeLater(() -> progressCheckboxes[2].setSelected(true));
                            }
                        },

                        () -> {
                            if (runFeaturesTweaks) {
                                // Runs features tweaks.
                                runFeaturesTweaks();
                                SwingUtilities.invokeLater(() -> progressCheckboxes[3].setSelected(true));
                            }

                            if (repairDiskIssues) {
                                // Repairs disk issues.
                                // This has to be done after the DISM commands in the above tweaks.
                                repairDiskIssues();
                                SwingUtilities.invokeLater(() -> progressCheckboxes[7].setSelected(true));
                            }
                        },

                        () -> {
                            if (runServicesTweaks) {
                                runServicesTweaks();
                                SwingUtilities.invokeLater(() -> progressCheckboxes[4].setSelected(true));
                            }
                        },

                        () -> {
                            if (removeJunkFiles) {
                                // Removes junk files.
                                JunkFileUtil.removeJunkFiles();
                                SwingUtilities.invokeLater(() -> progressCheckboxes[5].setSelected(true));
                            }
                        },

                        () -> {
                            if (removeBloatware) {
                                // Removes bloatware if the system is not in safe mode.
                                if (!RepairKit.isSafeMode()) {
                                    removeBloatware();
                                }
                                SwingUtilities.invokeLater(() -> progressCheckboxes[6].setSelected(true));
                            }
                        },

                        () -> {
                            if (updateOutdatedPrograms) {
                                // Updates outdated programs.
                                updateOutdatedPrograms();
                                SwingUtilities.invokeLater(() -> progressCheckboxes[8].setSelected(true));
                            }
                        }
                );

                // Executes tasks using TaskUtil.
                TaskUtil.executeTasks(tasks);
                DebugUtil.debug("Completed Automatic Repairs.");

                // Displays a message dialog.
                DebugUtil.debug("Displaying the Automatic Repairs completion dialog...");
                SoundUtil.playSound(ConstantUtil.EXCLAMATION_SOUND);
                JOptionPane.showMessageDialog(null,
                        "Automatic repairs have been completed.",
                        "Finished", JOptionPane.QUESTION_MESSAGE);

                // Resets the run button.
                DebugUtil.debug("Resetting the run button...");
                runButton.setText("Run Automatic Repairs");
                runButton.setEnabled(true);
                runButton.setBackground(new Color(0, 120, 215));

                // Resets the checkboxes.
                DebugUtil.debug("Resetting the Automatic Repairs progress checkboxes...");
                for (@NotNull JCheckBox checkbox : progressCheckboxes) {
                    checkbox.setEnabled(true);
                    checkbox.setSelected(false);
                }
            } catch (HeadlessException ex) {
                DebugUtil.warn("Failed to run Automatic Repairs", ex);
            }
        });

        // Starts the repair thread.
        repairThread.start();
    }

    /**
     * Creates a restore point.
     */
    private static void createRestorePoint() {
        DebugUtil.debug("Creating a restore point...");
        CommandUtil.runPowerShellCommand("Checkpoint-Computer -Description 'RepairKit Automatic Repairs'", false);
    }

    /**
     * Deletes any existing system policies.
     */
    private static void deleteSystemPolicies() {
        DebugUtil.debug("Deleting system policies...");
        @NotNull ConfigLoader configLoader = new ConfigLoader(FileUtil.getConfigFile("system_policies.json"));
        Map<String, Map<String, Object>> config = configLoader.getConfig();
        @NotNull RegistryTaskRunner taskRunner = new RegistryTaskRunner(config);
        @NotNull List<Runnable> tasks = taskRunner.getTasks();

        // Execute tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);
        DebugUtil.debug("Completed deleting system policies.");
    }

    /**
     * Removes various bloatware applications from the system.
     */
    private static void removeBloatware() {
        DebugUtil.debug("Removing installed bloatware apps...");
        @NotNull ConfigLoader configLoader = new ConfigLoader(FileUtil.getConfigFile("bloatware.json"));
        Map<String, Map<String, Object>> config = configLoader.getConfig();
        @NotNull BloatwareTaskRunner taskRunner = new BloatwareTaskRunner(config);
        @NotNull List<Runnable> tasks = taskRunner.getTasks();

        // Execute tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);
        DebugUtil.debug("Completed removing installed bloatware apps.");
    }

    /**
     * Repairs various disk issues.
     */
    private static void repairDiskIssues() {
        @NotNull ConfigLoader configLoader = new ConfigLoader(FileUtil.getConfigFile("disk_issues.json"));
        Map<String, Object> config = configLoader.getConfig().get("diskIssues");

        // Checks if the config is null.
        if (config == null) {
            return;
        }

        // Repairs the WMI repository.
        if (config.get("repairWMI") != null
                && config.get("repairWMI").equals(Boolean.TRUE)) {
            DebugUtil.debug("Repairing WMI repository...");

            if (CommandUtil.getCommandOutput("winmgmt /verifyrepository", false, false).toString().contains("not consistent")
                    && CommandUtil.getCommandOutput("winmgmt /salvagerepository", false, false).toString().contains("not consistent")) {
                CommandUtil.runCommand("winmgmt /resetrepository", false);
                DebugUtil.debug("Repaired WMI repository.");
            } else {
                DebugUtil.debug("WMI repository is already consistent.");
            }
        }

        // Repairs disk issues with SFC.
        if (config.get("repairWithSFC") != null
                && config.get("repairWithSFC").equals(Boolean.TRUE)) {
            DebugUtil.debug("Repairing disk issues with SFC...");

            if (CommandUtil.getCommandOutput("sfc /scannow", false, false).toString().contains("Windows Resource Protection found")) {
                DebugUtil.debug("Found disk issues with SFC.");

                // Repairs disk issues with DISM.
                if (config.get("repairWithDISM") != null
                        && config.get("repairWithDISM").equals(Boolean.TRUE)) {
                    DebugUtil.debug("Repairing disk issues with DISM...");
                    CommandUtil.runCommand("DISM /Online /Cleanup-Image /RestoreHealth", false);
                    DebugUtil.debug("Repaired disk issues with DISM.");
                }
            } else {
                DebugUtil.debug("No disk issues found with SFC.");
            }
        }
    }

    /**
     * Runs tweaks to the Windows registry.
     */
    private static void runRegistryTweaks() {
        DebugUtil.debug("Running registry tweaks...");
        @NotNull ConfigLoader configLoader = new ConfigLoader(FileUtil.getConfigFile("registry_tweaks.json"));
        Map<String, Map<String, Object>> config = configLoader.getConfig();
        @NotNull RegistryTaskRunner taskRunner = new RegistryTaskRunner(config);
        @NotNull List<Runnable> tasks = taskRunner.getTasks();
        Map<String, Object> spectreMeltdown = configLoader.getConfig().get("spectreMeltdown");

        // Checks if the config is null.
        if (spectreMeltdown == null) {
            return;
        }

        // Patches Spectre & Meltdown security vulnerabilities.
        if (spectreMeltdown.get("enabled") != null
                && spectreMeltdown.get("enabled").equals(Boolean.TRUE)) {
            tasks.add(() -> {
                String cpuName = CommandUtil.getPowerShellCommandOutput("Get-CimInstance -ClassName Win32_Processor | Select-Object -ExpandProperty Name", false, false).toString();
                RegistryUtil.setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Memory Management", "FeatureSettingsOverrideMask", 3);
                RegistryUtil.setRegistryStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Virtualization", "MinVmVersionForCpuBasedMitigations", "1.0");

                if (cpuName.contains("Intel")) {
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Memory Management", "FeatureSettingsOverride", 8);
                } else if (cpuName.contains("AMD")) {
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Memory Management", "FeatureSettingsOverride", 72);
                } else if (cpuName.contains("ARM")) {
                    RegistryUtil.setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Memory Management", "FeatureSettingsOverride", 64);
                }
            });
        }

        // Execute tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);
        DebugUtil.debug("Completed registry tweaks.");
    }

    /**
     * Runs tweaks to the system.
     */
    private static void runSystemTweaks() {
        DebugUtil.debug("Running system tweaks...");
        @NotNull List<Runnable> tasks = new ArrayList<>();
        @NotNull ConfigLoader configLoader = new ConfigLoader(FileUtil.getConfigFile("system_tweaks.json"));
        Map<String, Object> config = configLoader.getConfig().get("tweaks");

        // Checks if the config is null.
        if (config == null) {
            return;
        }

        // Fixes micro-stuttering in games.
        if (config.get("platformTickClock") != null
                && config.get("platformTickClock").equals(Boolean.TRUE)) {
            tasks.add(() -> {
                CommandUtil.runCommand("bcdedit /set useplatformtick yes", true);
                CommandUtil.runCommand("bcdedit /deletevalue useplatformclock", true);
            });
        }

        // Enables scheduled defrag.
        if (config.get("scheduledDefrag") != null
                && config.get("scheduledDefrag").equals(Boolean.TRUE)) {
            tasks.add(() -> CommandUtil.runCommand("schtasks /Change /ENABLE /TN \"\\Microsoft\\Windows\\Defrag\\ScheduledDefrag\"", true));
        }

        // Disables various telemetry tasks.
        if (config.get("telemetry") != null
                && config.get("telemetry").equals(Boolean.TRUE)) {
            tasks.add(() -> {
                CommandUtil.runCommand("schtasks /change /TN \"Microsoft\\Windows\\Application Experience\\Microsoft Compatibility Appraiser\" /disable", true);
                CommandUtil.runCommand("schtasks /change /TN \"Microsoft\\Windows\\Application Experience\\ProgramDataUpdater\" /disable", true);
                CommandUtil.runCommand("schtasks /change /TN \"Microsoft\\Windows\\Application Experience\\StartupAppTask\" /disable", true);
                CommandUtil.runCommand("schtasks /change /TN \"Microsoft\\Windows\\Customer Experience Improvement Program\\Consolidator\" /disable", true);
                CommandUtil.runCommand("schtasks /change /TN \"Microsoft\\Windows\\Customer Experience Improvement Program\\UsbCeip\" /disable", true);
                CommandUtil.runCommand("schtasks /change /TN \"Microsoft\\Windows\\Device Information\\Device\" /disable", true);
                CommandUtil.runCommand("schtasks /change /TN \"Microsoft\\Windows\\Windows Error Reporting\\QueueReporting\" /disable", true);
                CommandUtil.runCommand("setx DOTNET_CLI_TELEMETRY_OPTOUT 1", true);
                CommandUtil.runCommand("setx POWERSHELL_TELEMETRY_OPTOUT 1", true);
            });
        }

        // Deletes the controversial 'defaultuser0' user.
        if (config.get("defaultuser0") != null
                && config.get("defaultuser0").equals(Boolean.TRUE)) {
            tasks.add(() -> CommandUtil.runCommand("net user defaultuser0 /delete", true));
        }

        // Clears the Windows product key from registry.
        if (config.get("productKey") != null
                && config.get("productKey").equals(Boolean.TRUE)) {
            tasks.add(() -> CommandUtil.runCommand("cscript.exe //nologo \"%SystemRoot%\\system32\\slmgr.vbs\" /cpky", true));
        }

        // Resets network settings.
        if (config.get("networkReset") != null
                && config.get("networkReset").equals(Boolean.TRUE)) {
            tasks.add(() -> {
                CommandUtil.runCommand("netsh winsock reset", true);
                CommandUtil.runCommand("netsh int ip reset", true);
                CommandUtil.runCommand("ipconfig /flushdns", true);

                // Repairs broken Wi-Fi settings.
                RegistryUtil.deleteRegistryKey(WinReg.HKEY_CLASSES_ROOT, "CLSID\\{988248f3-a1ad-49bf-9170-676cbbc36ba3}");
                CommandUtil.runCommand("netcfg -v -u dni_dne", true);
            });
        }

        // Re-registers ExplorerFrame.dll.
        if (config.get("explorerFrame") != null
                && config.get("explorerFrame").equals(Boolean.TRUE)) {
            tasks.add(() -> CommandUtil.runCommand("regsvr32 /s ExplorerFrame.dll", true));
        }

        // Disables NetBios for all interfaces.
        if (config.get("netBios") != null
                && config.get("netBios").equals(Boolean.TRUE)) {
            @NotNull String baseKeyPath = "SYSTEM\\CurrentControlSet\\services\\NetBT\\Parameters\\Interfaces";
            java.util.@NotNull List<String> subKeys = RegistryUtil.listSubKeys(WinReg.HKEY_LOCAL_MACHINE, baseKeyPath);

            for (String subKey : subKeys) {
                @NotNull String fullPath = baseKeyPath + "\\" + subKey;
                RegistryUtil.setRegistryIntValue(WinReg.HKEY_LOCAL_MACHINE, fullPath, "NetbiosOptions", 2);
            }
        }

        // Resets Windows Media Player.
        if (config.get("mediaPlayer") != null
                && config.get("mediaPlayer").equals(Boolean.TRUE)) {
            tasks.add(() -> {
                CommandUtil.runCommand("regsvr32 /s jscript.dll", false);
                CommandUtil.runCommand("regsvr32 /s vbscript.dll", true);
            });
        }

        Map<String, Object> defender = (Map<String, Object>) config.get("defender");

        if (defender != null) {
            // Enables Windows Firewall for all profiles.
            // Path in JSON: defender\fixFirewall
            if (defender.get("fixFirewall") != null
                    && defender.get("fixFirewall").equals(Boolean.TRUE)) {
                tasks.add(() -> CommandUtil.runPowerShellCommand("Set-NetFirewallProfile"
                        + " -Profile Domain,Private,Public"
                        + " -Enabled True", false));
            }

            // Removes all Windows Defender exclusions.
            if (defender.get("removeExclusions") != null
                    && defender.get("removeExclusions").equals(Boolean.TRUE)) {
                tasks.add(() -> {
                    CommandUtil.runPowerShellCommand("Get-MpPreference | Select-Object -ExpandProperty"
                            + " ExclusionPath | ForEach-Object { Remove-MpPreference -ExclusionPath $_ }", false);
                    CommandUtil.runPowerShellCommand("Get-MpPreference | Select-Object -ExpandProperty"
                            + " ExclusionExtension | ForEach-Object { Remove-MpPreference -ExclusionExtension $_ }", false);
                    CommandUtil.runPowerShellCommand("Get-MpPreference | Select-Object -ExpandProperty"
                            + " ExclusionProcess | ForEach-Object { Remove-MpPreference -ExclusionProcess $_ }", false);
                    CommandUtil.runPowerShellCommand("Get-MpPreference | Select-Object -ExpandProperty"
                            + " ExclusionIpAddress | ForEach-Object { Remove-MpPreference -ExclusionIpAddress $_ }", false);
                    CommandUtil.runPowerShellCommand("Get-MpPreference | Select-Object -ExpandProperty"
                            + " ThreatIDDefaultAction_Ids | ForEach-Object { Remove-MpPreference -ThreatIDDefaultAction_Ids $_ }", false);
                    CommandUtil.runPowerShellCommand("Get-MpPreference | Select-Object -ExpandProperty"
                            + " ThreatIDDefaultAction_Actions | ForEach-Object { Remove-MpPreference -ThreatIDDefaultAction_Actions $_ }", false);
                    CommandUtil.runPowerShellCommand("Get-MpPreference | Select-Object -ExpandProperty"
                            + " AttackSurfaceReductionOnlyExclusions | ForEach-Object { Remove-MpPreference -AttackSurfaceReductionOnlyExclusions $_ }", false);
                    CommandUtil.runPowerShellCommand("Get-MpPreference | Select-Object -ExpandProperty"
                            + " ControlledFolderAccessAllowedApplications | ForEach-Object { Remove-MpPreference -ControlledFolderAccessAllowedApplications $_ }", false);
                    CommandUtil.runPowerShellCommand("Get-MpPreference | Select-Object -ExpandProperty"
                            + " ControlledFolderAccessProtectedFolders | ForEach-Object { Remove-MpPreference -ControlledFolderAccessProtectedFolders $_ }", false);
                    CommandUtil.runPowerShellCommand("Get-MpPreference | Select-Object -ExpandProperty"
                            + " AttackSurfaceReductionRules_Ids | ForEach-Object { Remove-MpPreference -AttackSurfaceReductionRules_Ids $_ }", false);
                    CommandUtil.runPowerShellCommand("Get-MpPreference | Select-Object -ExpandProperty"
                            + " AttackSurfaceReductionRules_Actions | ForEach-Object { Remove-MpPreference -AttackSurfaceReductionRules_Actions $_ }", false);
                });
            }

            // Removes all previous Windows Defender settings.
            if (defender.get("removePreviousSettings") != null
                    && defender.get("removePreviousSettings").equals(Boolean.TRUE)) {
                tasks.add(() -> CommandUtil.runPowerShellCommand("Remove-MpPreference"
                        + " -RealTimeScanDirection"
                        + " -QuarantinePurgeItemsAfterDelay"
                        + " -RemediationScheduleDay"
                        + " -RemediationScheduleTime"
                        + " -ReportingAdditionalActionTimeOut"
                        + " -ReportingCriticalFailureTimeOut"
                        + " -ReportingNonCriticalTimeOut"
                        + " -ScanAvgCPULoadFactor"
                        + " -CheckForSignaturesBeforeRunningScan"
                        + " -ScanPurgeItemsAfterDelay"
                        + " -ScanOnlyIfIdleEnabled"
                        + " -ScanParameters"
                        + " -ScanScheduleDay"
                        + " -ScanScheduleQuickScanTime"
                        + " -ScanScheduleTime"
                        + " -SignatureFirstAuGracePeriod"
                        + " -SignatureAuGracePeriod"
                        + " -SignatureDefinitionUpdateFileSharesSources"
                        + " -SignatureDisableUpdateOnStartupWithoutEngine"
                        + " -SignatureFallbackOrder"
                        + " -SharedSignaturesPath"
                        + " -SignatureScheduleDay"
                        + " -SignatureScheduleTime"
                        + " -SignatureUpdateCatchupInterval"
                        + " -SignatureUpdateInterval"
                        + " -SignatureBlobUpdateInterval"
                        + " -SignatureBlobFileSharesSources"
                        + " -MeteredConnectionUpdates"
                        + " -AllowNetworkProtectionOnWinServer"
                        + " -DisableDatagramProcessing"
                        + " -DisableCpuThrottleOnIdleScans"
                        + " -MAPSReporting"
                        + " -SubmitSamplesConsent"
                        + " -DisableAutoExclusions"
                        + " -DisablePrivacyMode"
                        + " -RandomizeScheduleTaskTimes"
                        + " -SchedulerRandomizationTime"
                        + " -DisableBehaviorMonitoring"
                        + " -DisableIntrusionPreventionSystem"
                        + " -DisableIOAVProtection"
                        + " -DisableRealtimeMonitoring"
                        + " -DisableScriptScanning"
                        + " -DisableArchiveScanning"
                        + " -DisableCatchupFullScan"
                        + " -DisableCatchupQuickScan"
                        + " -DisableEmailScanning"
                        + " -DisableRemovableDriveScanning"
                        + " -DisableRestorePoint"
                        + " -DisableScanningMappedNetworkDrivesForFullScan"
                        + " -DisableScanningNetworkFiles"
                        + " -UILockdown"
                        + " -UnknownThreatDefaultAction"
                        + " -LowThreatDefaultAction"
                        + " -ModerateThreatDefaultAction"
                        + " -HighThreatDefaultAction"
                        + " -SevereThreatDefaultAction"
                        + " -DisableBlockAtFirstSeen"
                        + " -PUAProtection"
                        + " -CloudBlockLevel"
                        + " -CloudExtendedTimeout"
                        + " -EnableNetworkProtection"
                        + " -EnableControlledFolderAccess"
                        + " -EnableLowCpuPriority"
                        + " -EnableFileHashComputation"
                        + " -EnableFullScanOnBatteryPower"
                        + " -ProxyPacUrl"
                        + " -ProxyServer"
                        + " -ProxyBypass"
                        + " -ForceUseProxyOnly"
                        + " -DisableTlsParsing"
                        + " -DisableHttpParsing"
                        + " -DisableDnsParsing"
                        + " -DisableDnsOverTcpParsing"
                        + " -DisableSshParsing"
                        + " -PlatformUpdatesChannel"
                        + " -EngineUpdatesChannel"
                        + " -SignaturesUpdatesChannel"
                        + " -DisableGradualRelease"
                        + " -AllowNetworkProtectionDownLevel"
                        + " -AllowDatagramProcessingOnWinServer"
                        + " -EnableDnsSinkhole"
                        + " -DisableInboundConnectionFiltering"
                        + " -DisableRdpParsing"
                        + " -Force", false));
            }

            // Sets Windows Defender to recommended settings.
            if (defender.get("setRecommendedSettings") != null
                    && defender.get("setRecommendedSettings").equals(Boolean.TRUE)) {
                tasks.add(() -> CommandUtil.runPowerShellCommand("Set-MpPreference"
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
                        + " -SubmitSamplesConsent 3", false));
            }

            // Sets Windows Defender ASR rules to recommended settings.
            if (defender.get("setASRRules") != null
                    && defender.get("setASRRules").equals(Boolean.TRUE)) {
                tasks.add(() -> {
                    CommandUtil.runPowerShellCommand("Add-MpPreference"
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
                            + " -AttackSurfaceReductionRules_Actions Enabled", false);

                    // Disables certain ASR rules from blocking.
                    CommandUtil.runPowerShellCommand("Add-MpPreference"
                            + " -AttackSurfaceReductionRules_Ids "
                            + "01443614-cd74-433a-b99e-2ecdc07bfc25," // ASR: Don't block credential stealing from the Windows local security authority subsystem
                            + "9e6c4e1f-7d60-472f-ba1a-a39ef669e4b2," // ASR: Don't block executable files from running unless they meet a prevalence, age, or trusted list criterion
                            + "d1e49aac-8f56-4280-b9ba-993a6d77406c" // ASR: Don't block process creations originating from PSExec and WMI commands
                            + " -AttackSurfaceReductionRules_Actions Disabled", false);

                    // Disables certain ASR rules from warning.
                    CommandUtil.runPowerShellCommand("Add-MpPreference"
                            + " -AttackSurfaceReductionRules_Ids "
                            + "56a863a9-875e-4185-98a7-b882c64b5ce5," // ASR: Warn against abuse of exploited vulnerable signed drivers
                            + "a8f5898e-1dc8-49a9-9878-85004b8a61e6" // ASR: Warn against Webshell creation for servers
                            + " -AttackSurfaceReductionRules_Actions Warn", false);
                });
            }
        }

        // Execute system tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);
        DebugUtil.debug("Completed system tweaks.");
    }

    /**
     * Runs tweaks to Windows features.
     */
    private static void runFeaturesTweaks() {
        DebugUtil.debug("Running Windows features tweaks...");
        @NotNull ConfigLoader configLoader = new ConfigLoader(FileUtil.getConfigFile("features_tweaks.json"));
        Map<String, Map<String, Object>> config = configLoader.getConfig();
        @NotNull FeaturesTaskRunner taskRunner = new FeaturesTaskRunner(config);
        @NotNull List<Runnable> tasks = taskRunner.getTasks();

        // Execute tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);
        DebugUtil.debug("Completed Windows features tweaks.");
    }

    /**
     * Runs tweaks to Windows services.
     */
    private static void runServicesTweaks() {
        DebugUtil.debug("Running services tweaks...");
        @NotNull ConfigLoader configLoader = new ConfigLoader(FileUtil.getConfigFile("services_tweaks.json"));
        Map<String, Map<String, Object>> config = configLoader.getConfig();
        @NotNull ServicesTaskRunner taskRunner = new ServicesTaskRunner(config);
        @NotNull List<Runnable> tasks = taskRunner.getTasks();

        // Execute tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);
        DebugUtil.debug("Completed services tweaks.");
    }

    /**
     * Updates outdated programs using WinGet.
     */
    private static void updateOutdatedPrograms() {
        DebugUtil.debug("Updating outdated programs...");

        // Updates outdated programs using Winget.
        WinGetUtil.updateAllPrograms();

        DebugUtil.debug("Completed updating outdated programs.");
    }
}
