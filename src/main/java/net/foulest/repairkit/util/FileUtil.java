package net.foulest.repairkit.util;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static net.foulest.repairkit.RepairKit.programName;
import static net.foulest.repairkit.util.SwingUtil.showErrorDialog;

public class FileUtil {

    private static final ExecutorService DOWNLOAD_EXECUTOR = Executors.newFixedThreadPool(4);

    public static void unzipFile(String fileZip, String fileDest) {
        fileZip = fileZip.replace("%temp%", System.getenv("TEMP"));
        fileDest = fileDest.replace("%temp%", System.getenv("TEMP"));

        try {
            Path sourcePath = Paths.get(fileZip);
            Path targetPath = Paths.get(fileDest);

            try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(sourcePath))) {
                ZipEntry zipEntry = zis.getNextEntry();

                while (zipEntry != null) {
                    Path newPath = targetPath.resolve(zipEntry.getName());

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
            showErrorDialog(ex, "Unzip File");
        }
    }

    public static void deleteDirectory(File directory) {
        try {
            Path dirPath = directory.toPath();
            Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ignored) {
        }
    }

    public static void deleteDirectory(File directory, String filter) {
        FileFilter fileFilter = new WildcardFileFilter(filter.toLowerCase());
        Path dirPath = directory.toPath();

        try {
            Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (fileFilter.accept(file.toFile())) {
                        Files.delete(file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (fileFilter.accept(dir.toFile())) {
                        Files.delete(dir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ignored) {
        }
    }

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
                    return;
                }

                try (InputStream inputStream = new BufferedInputStream(con.getInputStream())) {
                    saveFile(inputStream, fileName, replaceOldFile);
                }

            } catch (Exception ex) {
                showErrorDialog(ex, "Download File");
            }
        });
    }

    public static void saveFile(InputStream input, String fileName, boolean replaceOldFile) {
        Path savedFilePath = Paths.get(System.getenv("TEMP"), programName, fileName);

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
            showErrorDialog(ex, "Save File");
        }
    }
}
