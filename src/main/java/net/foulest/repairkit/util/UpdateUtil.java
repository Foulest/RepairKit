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
package net.foulest.repairkit.util;

import lombok.Cleanup;
import lombok.Data;
import net.foulest.repairkit.RepairKit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for updating the program.
 *
 * @author Foulest
 */
@Data
public class UpdateUtil {

    private static final String REPO_API_URL = "https://api.github.com/repos/Foulest/RepairKit/releases/latest";
    private static final String DOWNLOAD_URL = "https://github.com/Foulest/RepairKit/releases/latest";
    static final boolean CONNECTED_TO_INTERNET;

    static {
        CONNECTED_TO_INTERNET = connectedToInternet();
    }

    /**
     * Checks for updates and logs the result.
     */
    public static void checkForUpdates() {
        @Nullable String latestVersion = getLatestReleaseVersion();
        String currentVersion = getVersionFromProperties();

        if (latestVersion == null) {
            // If the user has internet access but the update check failed, we'll notify the user.
            if (CONNECTED_TO_INTERNET) {
                SoundUtil.playSound(ConstantUtil.ERROR_SOUND);
                JOptionPane.showMessageDialog(null,
                        "Failed to check for updates. Please try again later.",
                        "Update Check Failed", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        if (!latestVersion.equals(currentVersion)) {
            SoundUtil.playSound(ConstantUtil.EXCLAMATION_SOUND);

            // When the user clicks "Yes", the program will open the GitHub page in the default browser.
            int result = JOptionPane.showConfirmDialog(null,
                    "A new version of RepairKit is available. Would you like to download it?",
                    "Update Available", JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                CommandUtil.runCommand("start \"\" \"" + DOWNLOAD_URL + "\"", true);
            }
        }
    }

    /**
     * Checks if the system has internet access.
     *
     * @return True if the system has internet access, otherwise false.
     */
    @SuppressWarnings("OverlyBroadCatchBlock")
    private static boolean connectedToInternet() {
        // Prefer IPv4 over IPv6
        System.setProperty("java.net.preferIPv4Stack", "true");

        try {
            @NotNull URL url = new URL("https://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            connection.disconnect();
            return true;
        } catch (IOException ex) {
            DebugUtil.warn("Failed to get internet connection", ex);
            return false;
        }
    }

    /**
     * Fetches the latest release version from the GitHub API.
     *
     * @return The latest release version or null if an error occurred.
     */
    @SuppressWarnings("OverlyBroadCatchBlock")
    private static @Nullable String getLatestReleaseVersion() {
        try {
            @NotNull URL url = new URL(REPO_API_URL);
            @Cleanup("disconnect") HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            @Cleanup InputStream inputStream = connection.getInputStream();
            @Cleanup @NotNull BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String inputLine;
            @NotNull StringBuilder content = new StringBuilder();

            while (true) {
                inputLine = in.readLine();

                if (inputLine == null) {
                    break;
                }

                content.append(inputLine);
            }

            @NotNull String output = content.toString();
            return extractVersion(output);
        } catch (IOException ex) {
            DebugUtil.warn("Failed to get latest release version", ex);
            return null;
        }
    }

    /**
     * Extracts the version from the JSON response.
     *
     * @param jsonResponse The JSON response.
     * @return The version or null if not found.
     */
    private static @Nullable String extractVersion(@NotNull CharSequence jsonResponse) {
        @NotNull String versionRegex = "\"tag_name\":\"(.*?)\"";
        @NotNull Pattern pattern = Pattern.compile(versionRegex);
        @NotNull Matcher matcher = pattern.matcher(jsonResponse);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Gets the version of the program.
     *
     * @return The version of the program.
     */
    public static String getVersionFromProperties() {
        DebugUtil.debug("Getting version from properties...");
        @NotNull Properties properties = new Properties();

        try (@Nullable InputStream input = RepairKit.class.getResourceAsStream("/version.properties")) {
            if (input == null) {
                JOptionPane.showMessageDialog(null,
                        "Failed to load version properties.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return "Unknown";
            }

            DebugUtil.debug("Loading properties...");
            properties.load(input);
            return properties.getProperty("version");
        } catch (IOException ex) {
            DebugUtil.warn("Failed to get version from properties", ex);
        }
        return "Unknown";
    }
}
