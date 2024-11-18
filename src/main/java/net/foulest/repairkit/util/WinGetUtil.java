package net.foulest.repairkit.util;

import com.sshtools.twoslices.Slice;
import com.sshtools.twoslices.Toast;
import com.sshtools.twoslices.ToastType;
import lombok.Cleanup;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class WinGetUtil {

    @SuppressWarnings({"unused", "OverlyBroadCatchBlock"})
    public static void updateAllPrograms() {
        try {
            List<String> outdatedPackages = getOutdatedPrograms();

            if (outdatedPackages.isEmpty()) {
                @Cleanup Slice noneFound = Toast.toast(ToastType.INFO, "RepairKit", "No outdated programs found.");
                DebugUtil.debug("No outdated programs found.");
                return;
            }

            for (String id : outdatedPackages) {
                @Cleanup Slice updating = Toast.toast(ToastType.INFO, "RepairKit", "Updating package: " + id);
                DebugUtil.debug("Updating program: " + id);

                if (updatePackage(id)) {
                    @Cleanup Slice updated = Toast.toast(ToastType.INFO, "RepairKit", "Updated program: " + id);
                    DebugUtil.debug("Updated program: " + id);
                } else {
                    @Cleanup Slice failed = Toast.toast(ToastType.INFO, "RepairKit", "Failed to update program: " + id);
                    DebugUtil.debug("Failed to update program: " + id);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void installDependencies() {
        // Installs NuGet and the WinGet client.
        CommandUtil.runPowerShellCommand("Install-PackageProvider -Name NuGet -Force | Out-Null", false);
        CommandUtil.runPowerShellCommand("Install-Module -Name Microsoft.WinGet.Client -Force -Repository PSGallery | Out-Null", false);
    }

    @SuppressWarnings({"OverlyBroadCatchBlock", "unused"})
    private static @NotNull List<String> getOutdatedPrograms() {
        try {
            @Cleanup Slice checking = Toast.toast(ToastType.INFO, "RepairKit", "Checking for outdated programs...");
            DebugUtil.debug("Checking for outdated programs...");

            // Installs the required dependencies for WinGet.
            installDependencies();

            // Gets the list of outdated packages.
            List<String> output = CommandUtil.getPowerShellCommandOutput("Get-WinGetPackage -Source winget"
                    + " | Where-Object IsUpdateAvailable | Select-Object -ExpandProperty Id", false, false);

            List<String> packages = new ArrayList<>();

            // Ignores empty lines.
            for (String line : output) {
                String trim = line.trim();

                if (trim.isEmpty()) {
                    continue;
                }

                packages.add(trim);
            }
            return packages;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static boolean updatePackage(String id) {
        List<String> output = CommandUtil.getPowerShellCommandOutput("winget upgrade --id " + id
                        + " --disable-interactivity --silent --accept-package-agreements --accept-source-agreements",
                false, false);

        return !output.contains("The package cannot be upgraded");
    }
}
