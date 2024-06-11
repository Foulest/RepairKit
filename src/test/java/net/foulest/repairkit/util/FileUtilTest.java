package net.foulest.repairkit.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FileUtilTest {

    private final Path tempDir = Paths.get(System.getenv("TEMP"), "RepairKitTest");
    private final Path zipPath = tempDir.resolve("test.zip");
    private final Path unzipPath = tempDir.resolve("unzipped");
    private final Path filePath = tempDir.resolve("test.txt");

    @BeforeEach
    void setUp() throws IOException {
        // Delete the temp directory if it exists
        if (Files.exists(tempDir)) {
            try (Stream<Path> paths = Files.walk(tempDir)) {
                paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        }

        Files.createDirectories(tempDir);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(zipPath);

        if (Files.exists(unzipPath)) {
            try (Stream<Path> paths = Files.walk(unzipPath)) {
                paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        }

        Files.deleteIfExists(filePath);
    }

    @Test
    void unzipFile() throws IOException {
        // Create a zip file
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            zos.putNextEntry(new ZipEntry("test.txt"));
            zos.write("Hello, world!".getBytes());
            zos.closeEntry();
        }

        // Unzip the file
        FileUtil.unzipFile(zipPath.toString(), unzipPath.toString());

        // Check if the file was correctly unzipped
        assertTrue(Files.exists(unzipPath.resolve("test.txt")));
    }

    @Test
    void saveFile() {
        // Create an InputStream
        ByteArrayInputStream input = new ByteArrayInputStream("Hello, world!".getBytes());

        // Save the file
        FileUtil.saveFile(input, filePath.toString(), true);

        // Check if the file was correctly saved
        assertTrue(Files.exists(filePath));
    }
}
