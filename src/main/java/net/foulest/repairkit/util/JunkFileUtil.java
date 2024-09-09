package net.foulest.repairkit.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JunkFileUtil {

    // File extensions to scan for
    private static final List<String> JUNK_FILE_EXTENSIONS = List.of(
            ".tmp", ".temp", ".old", ".dmp", ".DS_Store", ".hprof"
    );

    // Paths to exclude from scanning
    private static final List<Path> EXCLUDED_PATHS = List.of(
            Paths.get("C:\\$Recycle.Bin"),
            Paths.get("C:\\Users\\Default"),
            Paths.get("C:\\Users\\Public")
    );

    // Time constants
    private static final long LAST_24_HOURS = Instant.now().minus(24, ChronoUnit.HOURS).toEpochMilli();

    // Analytics
    private static long totalCount;
    private static long totalSize;

    /**
     * Checks for junk files on the system.
     */
    public static void removeJunkFiles() {
        // Collects data for analytics.
        long now = System.currentTimeMillis();
        totalCount = 0;
        totalSize = 0;

        List<Runnable> tasks = new ArrayList<>(List.of());
        Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();

        // Adds each drive to the task list.
        for (Path root : rootDirectories) {
            tasks.add(() -> scanDrive(root));
        }

        // Executes tasks using TaskUtil.
        TaskUtil.executeTasks(tasks);
        DebugUtil.debug("Junk files found: " + totalCount);
        DebugUtil.debug("Total size: " + totalSize);
        DebugUtil.debug("Time taken: " + (System.currentTimeMillis() - now) + "ms");
    }

    /**
     * Scans the drive for junk files.
     *
     * @param root The root directory to scan.
     */
    private static void scanDrive(Path root) {
        try {
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                /**
                 * Pre-visits directories to skip excluded paths.
                 *
                 * @param dir The directory to visit.
                 * @param attrs The directory's attributes.
                 * @return {@code FileVisitResult.SKIP_SUBTREE} if the directory is excluded,
                 *         otherwise {@code FileVisitResult.CONTINUE}.
                 */
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (EXCLUDED_PATHS.contains(dir)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                /**
                 * Visits files to check if they are junk files.
                 *
                 * @param file The file to visit.
                 * @param attrs The file's attributes.
                 * @return {@code FileVisitResult.CONTINUE} to continue visiting files.
                 */
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (isJunkFile(file, attrs)) {
                        // Gets the file's size.
                        long size = 0;

                        try {
                            size = Files.size(file);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        // Aggregates the data for analytics.
                        totalCount++;
                        totalSize += size;

                        // Prints the file path before deleting it.
                        DebugUtil.debug("Cleaning junk file: " + file + " (" + size + " bytes)");

                        // Deletes the file.
                        try {
                            Files.delete(file);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                /**
                 * Skips files that cannot be accessed.
                 *
                 * @param file The file to visit.
                 * @param ex The exception that occurred.
                 * @return {@code FileVisitResult.CONTINUE} if the file cannot be accessed,
                 *         otherwise {@code super.visitFileFailed(file, ex)}.
                 */
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException ex) throws IOException {
                    if (ex instanceof AccessDeniedException) {
                        return FileVisitResult.CONTINUE;
                    }
                    return super.visitFileFailed(file, ex);
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Checks if the file is a junk file.
     * <p>
     * This is classified as a file that ends with one of the junk file
     * extensions, and was not accessed in the last 24 hours.
     *
     * @param file The file to check.
     * @param attrs The file's attributes.
     * @return {@code true} if the file is a junk file, otherwise {@code false}.
     */
    private static boolean isJunkFile(@NotNull Path file, BasicFileAttributes attrs) {
        String fileName = file.getFileName().toString().toLowerCase(Locale.ROOT);

        for (String extension : JUNK_FILE_EXTENSIONS) {
            if (fileName.endsWith(extension) && attrs.lastAccessTime().toMillis() < LAST_24_HOURS) {
                return true;
            }
        }
        return false;
    }
}
