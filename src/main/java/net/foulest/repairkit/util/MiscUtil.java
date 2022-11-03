package net.foulest.repairkit.util;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.Timer;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static net.foulest.repairkit.RepairKit.*;

public class MiscUtil {

    private static final Runtime cmd = Runtime.getRuntime();

    public static void playSound(String soundName) {
        Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty(soundName);

        if (runnable != null) {
            runnable.run();
        }
    }

    public static List<String> getCommandOutput(String command, boolean display) {
        try {
            Process proc = cmd.exec("C:\\WINDOWS\\system32\\cmd.exe /c " + command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            List<String> output = new ArrayList<>();

            // Reads the output from the command.
            String line;
            while ((line = stdInput.readLine()) != null) {
                output.add(line);

                if (display && !line.trim().isEmpty()) {
                    updateProgressLabel(line);
                }
            }

            // Reads any errors from the command.
            while ((line = stdError.readLine()) != null) {
                if (display && !line.trim().isEmpty()) {
                    updateProgressLabel(line);
                }
            }

            proc.waitFor();
            return output;

        } catch (NullPointerException | InterruptedException | IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error Debug", JOptionPane.ERROR_MESSAGE);
        }

        return Collections.singletonList("");
    }

    public static void updateProgressLabel(String text) {
        labelProgress.setText(String.format(htmlFormat, 250, "Progress: " + text));
        frame.update(frame.getGraphics());
    }

    public static void updateProgressLabel(String text, long clearDelay) {
        labelProgress.setText(String.format(htmlFormat, 250, "Progress: " + text));
        frame.update(frame.getGraphics());

        if (clearDelay > 0) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    updateProgressLabel("");
                }
            }, clearDelay);
        }
    }

    public static void displayCommandOutput(String command) {
        try {
            Process proc = cmd.exec("C:\\WINDOWS\\system32\\cmd.exe /c " + command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            // Reads the output from the command.
            String line;
            while ((line = stdInput.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    updateProgressLabel(line);
                }
            }

            // Reads any errors from the command.
            while ((line = stdError.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    updateProgressLabel(line);
                }
            }

            proc.waitFor();

        } catch (NullPointerException | InterruptedException | IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error Debug", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void runCommand(String command, boolean async) {
        try {
            Process proc = cmd.exec("C:\\WINDOWS\\system32\\cmd.exe /c " + command);

            if (!async) {
                proc.waitFor();
            }

        } catch (NullPointerException | InterruptedException | IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error Debug", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unused")
    public static long getDirectorySize(Path path) {
        long size = 0;

        try (Stream<Path> walk = Files.walk(path)) {
            size = walk.filter(Files::isRegularFile).mapToLong(p -> {
                try {
                    return Files.size(p);
                } catch (IOException e) {
                    System.out.printf("Failed to get size of %s%n%s", p, e);
                    return 0L;
                }
            }).sum();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return size;
    }

    @SuppressWarnings("unused")
    public static String getReadableByteCount(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);

        if (absB < 1024) {
            return bytes + " B";
        }

        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");

        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }

        value *= Long.signum(bytes);
        return String.format("%.1f %cB", value / 1024.0, ci.current());
    }

    public static void downloadFile(String link, String fileName, boolean replaceOldFile) {
        try {
            URL url = new URL(link);

            // Adds user agent.
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36");
            con.setReadTimeout(5000);
            con.setConnectTimeout(5000);

            // Returns if IP address is blocked.
            try {
                con.getInputStream();
            } catch (IOException ex) {
                return;
            }

            saveFile(con.getInputStream(), fileName, replaceOldFile);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void saveFile(InputStream input, String fileName, boolean replaceOldFile) {
        try {
            File savedFile = new File(System.getProperty("java.io.tmpdir") + "/" + programName + "/" + fileName);

            // Deletes the old file if it already exists.
            if (savedFile.exists()) {
                if (replaceOldFile) {
                    savedFile.delete();
                } else {
                    return;
                }
            }

            // Makes download directory if not found.
            if (!savedFile.getParentFile().exists()) {
                savedFile.getParentFile().mkdirs();
            }

            // Saves the file to the temp directory.
            try {
                Files.copy(input, savedFile.toPath());
                System.out.println("File saved: " + fileName);
            } catch (IOException ignored) {
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public static String getSID() {
        List<String> output = getCommandOutput("wmic useraccount where name='%username%' get sid", false);

        for (String line : output) {
            if (line.startsWith("S-")) {
                return line.trim();
            }
        }

        return "";
    }

    public static void addComponents(JPanel panel, Component... components) {
        for (Component component : components) {
            panel.add(component);
        }
    }

    public static void unzipFile(String fileZip, String fileDest) {
        fileZip = fileZip.replace("%temp%", System.getenv("TEMP"));
        fileDest = fileDest.replace("%temp%", System.getenv("TEMP"));

        try {
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(Files.newInputStream(Paths.get(fileZip)));
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                File newFile = newFile(new File(fileDest), zipEntry);

                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        JOptionPane.showMessageDialog(null,
                                "Failed to create new directory " + newFile,
                                "Error Debug", JOptionPane.ERROR_MESSAGE);
                        throw new IOException("Failed to create directory " + newFile);
                    }

                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        JOptionPane.showMessageDialog(null,
                                "Failed to create parent directory " + parent,
                                "Error Debug", JOptionPane.ERROR_MESSAGE);
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;

                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                }

                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error Debug", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static File newFile(File destDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destDir, zipEntry.getName());
        String destDirPath = destDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static void deleteDirectory(File directory) {
        File[] allContents = directory.listFiles();

        if (allContents != null) {
            for (File file : allContents) {
                file.delete();
            }
        }

        directory.delete();
    }

    public static void deleteDirectory(File directory, String filter) {
        FileFilter fileFilter = new WildcardFileFilter(filter.toLowerCase());

        if (directory.listFiles() != null) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (fileFilter.accept(file)) {
                    deleteDirectory(file);
                }
            }
        }
    }

    public static void deleteDirectory(File directory, String filter, boolean debug) {
        FileFilter fileFilter = new WildcardFileFilter(filter.toLowerCase());

        if (directory.listFiles() != null) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (debug) {
                    updateProgressLabel("Found: " + file.getAbsolutePath());
                }

                if (fileFilter.accept(file)) {
                    if (debug) {
                        updateProgressLabel("Deleting: " + file.getAbsolutePath());
                    }

                    deleteDirectory(file);
                }
            }
        }
    }

    public static void setRegistryIntValue(WinReg.HKEY hkey, String keyPath, String keyName, int value) {
        if (!Advapi32Util.registryKeyExists(hkey, keyPath)) {
            Advapi32Util.registryCreateKey(hkey, keyPath);
        }

        Advapi32Util.registrySetIntValue(hkey, keyPath, keyName, value);
    }

    public static void setRegistryStringValue(WinReg.HKEY hkey, String keyPath, String keyName, String value) {
        if (!Advapi32Util.registryKeyExists(hkey, keyPath)) {
            Advapi32Util.registryCreateKey(hkey, keyPath);
        }

        Advapi32Util.registrySetStringValue(hkey, keyPath, keyName, value);
    }

    public static void deleteRegistryValue(WinReg.HKEY hkey, String keyPath, String value) {
        if (Advapi32Util.registryValueExists(hkey, keyPath, value)) {
            Advapi32Util.registryDeleteValue(hkey, keyPath, value);
        }
    }

    public static void deleteRegistryKey(WinReg.HKEY hkey, String keyPath) {
        if (Advapi32Util.registryKeyExists(hkey, keyPath)) {
            Advapi32Util.registryDeleteKey(hkey, keyPath);
        }
    }
}
