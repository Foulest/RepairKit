package net.foulest.repairkit.util;

import lombok.NonNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {

    private static final ExecutorService DOWNLOAD_EXECUTOR = Executors.newFixedThreadPool(4);
    public static final File tempDirectory = new File(System.getenv("TEMP") + "\\RepairKit");

    /**
     * Unzips a file.
     *
     * @param fileZip  The file to unzip.
     * @param fileDest The destination to unzip the file to.
     */
    public static void unzipFile(@NonNull String fileZip, @NonNull String fileDest) {
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
            MessageUtil.log(Level.WARNING, "Failed to unzip file: " + fileZip);
            ex.printStackTrace();
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
    public static void downloadFile(@NonNull String link, @NonNull String fileName, boolean replaceOldFile) {
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
                    MessageUtil.log(Level.WARNING, "Failed to download file: " + fileName);
                    ex.printStackTrace();
                    return;
                }

                try (InputStream inputStream = new BufferedInputStream(con.getInputStream())) {
                    saveFile(inputStream, fileName, replaceOldFile);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
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
    public static void saveFile(@NonNull InputStream input, @NonNull String fileName, boolean replaceOldFile) {
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
            MessageUtil.log(Level.WARNING, "Failed to save file: " + fileName);
            ex.printStackTrace();
        }
    }
}
