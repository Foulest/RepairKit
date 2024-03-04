package net.foulest.repairkit.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {

    private static final ExecutorService DOWNLOAD_EXECUTOR = Executors.newFixedThreadPool(4);
    public static final File tempDirectory = new File(System.getenv("TEMP") + "\\RepairKit");
    public static final File logFile = new File(System.getenv("APPDATA") + "\\RepairKit.log");

    /**
     * Unzips a file.
     *
     * @param fileZip  The file to unzip.
     * @param fileDest The destination to unzip the file to.
     */
    public static void unzipFile(String fileZip, String fileDest) {
        fileZip = fileZip.replace("%temp%", System.getenv("TEMP"));
        fileDest = fileDest.replace("%temp%", System.getenv("TEMP"));

        try {
            Path sourcePath = Paths.get(fileZip);
            Path targetPath = Paths.get(fileDest);

            try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(sourcePath))) {
                ZipEntry zipEntry = zis.getNextEntry();

                while (zipEntry != null) {
                    Path newPath = targetPath.resolve(zipEntry.getName()).normalize();

                    // Check for path traversal vulnerabilities
                    if (!newPath.startsWith(targetPath)) {
                        throw new IOException("Bad zip entry (potential path traversal): " + zipEntry.getName());
                    }

                    if (zipEntry.isDirectory()) {
                        Files.createDirectories(newPath);
                    } else {
                        Files.createDirectories(newPath.getParent());
                        Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                    }

                    zipEntry = zis.getNextEntry();
                }
            }
        } catch (IOException ex) {
            MessageUtil.printException(ex);
        }
    }

    /**
     * Downloads a file.
     *
     * @param link           The link to download the file from.
     * @param fileName       The name of the file to download.
     * @param replaceOldFile Whether or not to replace the old file.
     */
    @SuppressWarnings("unused")
    public static void downloadFile(String link, String fileName, boolean replaceOldFile) {
        DOWNLOAD_EXECUTOR.submit(() -> {
            try {
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36");
                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);

                //noinspection EmptyTryBlock
                try (InputStream ignored = con.getInputStream()) {
                    // Returns if IP address is blocked.
                } catch (IOException ex) {
                    MessageUtil.printException(ex);
                    return;
                }

                try (InputStream inputStream = new BufferedInputStream(con.getInputStream())) {
                    saveFile(inputStream, fileName, replaceOldFile);
                }
            } catch (Exception ex) {
                MessageUtil.printException(ex);
            }
        });
    }

    /**
     * Saves a file.
     *
     * @param input          The input stream to save.
     * @param fileName       The name of the file to save.
     * @param replaceOldFile Whether or not to replace the old file.
     */
    public static void saveFile(InputStream input, String fileName, boolean replaceOldFile) {
        Path savedFilePath = Paths.get(String.valueOf(tempDirectory), fileName);

        try {
            if (Files.exists(savedFilePath)) {
                if (replaceOldFile) {
                    Files.delete(savedFilePath);
                } else {
                    return;
                }
            }

            if (!Files.exists(savedFilePath.getParent())) {
                Files.createDirectories(savedFilePath.getParent());
            }

            Files.copy(input, savedFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            MessageUtil.printException(ex);
        }
    }

    /**
     * Writes the given text to the log file.
     * Appends the text to the file if it already exists.
     * Attaches the current hour, minute, and second to the log entry.
     *
     * @param text The text to write to the log file.
     */
    public static void writeToLogFile(String text) {
        // Creates the log file if it doesn't exist.
        if (!logFile.exists()) {
            try {
                Files.createDirectories(logFile.getParentFile().toPath());
                Files.createFile(logFile.toPath());
            } catch (IOException ex) {
                MessageUtil.printException(ex);
            }
        }

        // Get the current time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = now.format(formatter);

        // Writes the text to the log file.
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write("[" + formattedTime + "] " + text + System.lineSeparator());
        } catch (IOException ex) {
            MessageUtil.printException(ex);
        }
    }
}
