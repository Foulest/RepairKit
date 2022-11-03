package net.foulest.repairkit.util;

import lombok.Getter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;

public class WindowsShortcut {

    @Getter
    private boolean directory;
    @Getter
    private boolean local;
    @Getter
    private String realFile;
    @Getter
    private String description;
    @Getter
    private String relativePath;
    @Getter
    private String workingDirectory;
    @Getter
    private String commandLineArguments;

    public WindowsShortcut(File file) throws IOException, ParseException {
        try (InputStream in = Files.newInputStream(file.toPath())) {
            parseLink(getBytes(in));
        }
    }

    /**
     * Provides a quick test to see if this could be a valid link !
     * If you try to instantiate a new WindowShortcut and the link is not valid,
     * Exceptions may be thrown and Exceptions are extremely slow to generate,
     * therefore any code needing to loop through several files should first check this.
     *
     * @param file the potential link
     * @return true if the file may be a link, false otherwise
     * @throws IOException if an IOException is thrown while reading from the file
     */
    public static boolean isPotentialValidLink(File file) throws IOException {
        final int minimum_length = 0x64;
        InputStream fis = Files.newInputStream(file.toPath());
        boolean isPotentiallyValid;

        try {
            isPotentiallyValid = file.isFile()
                    && file.getName().toLowerCase().endsWith(".lnk")
                    && fis.available() >= minimum_length
                    && isMagicPresent(getBytes(fis, 32));
        } finally {
            fis.close();
        }

        return isPotentiallyValid;
    }

    /**
     * Gets all the bytes from an InputStream
     *
     * @param in the InputStream from which to read bytes
     * @return array of all the bytes contained in 'in'
     * @throws IOException if an IOException is encountered while reading the data from the InputStream
     */
    private static byte[] getBytes(InputStream in) throws IOException {
        return getBytes(in, null);
    }

    /**
     * Gets up to max bytes from an InputStream
     *
     * @param in  the InputStream from which to read bytes
     * @param max maximum number of bytes to read
     * @return array of all the bytes contained in 'in'
     * @throws IOException if an IOException is encountered while reading the data from the InputStream
     */
    private static byte[] getBytes(InputStream in, Integer max) throws IOException {
        // read the entire file into a byte buffer
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buff = new byte[256];

        while (max == null || max > 0) {
            int n = in.read(buff);

            if (n == -1) {
                break;
            }

            bout.write(buff, 0, n);

            if (max != null) {
                max -= n;
            }
        }

        in.close();
        return bout.toByteArray();
    }

    private static boolean isMagicPresent(byte[] link) {
        final int magic = 0x0000004C;
        final int magic_offset = 0x00;
        return link.length >= 32 && bytesToDword(link, magic_offset) == magic;
    }

    private static String getNullDelimitedString(byte[] bytes, int off) {
        int len = 0;

        // count bytes until the null character (0)
        while (bytes[off + len] != 0) {
            len++;
        }

        return new String(bytes, off, len);
    }

    private static String getUTF16String(byte[] bytes, int off, int len) {
        return new String(bytes, off, len, StandardCharsets.UTF_16LE);
    }

    /*
     * convert two bytes into a short note, this is little endian because it's
     * for an Intel only OS.
     */
    private static int bytesToWord(byte[] bytes, int off) {
        return ((bytes[off + 1] & 0xff) << 8) | (bytes[off] & 0xff);
    }

    private static int bytesToDword(byte[] bytes, int off) {
        return (bytesToWord(bytes, off + 2) << 16) | bytesToWord(bytes, off);
    }

    /**
     * Gobbles up link data by parsing it and storing info in member fields
     *
     * @param link all the bytes from the .lnk file
     */
    private void parseLink(byte[] link) throws ParseException {
        try {
            if (!isMagicPresent(link)) {
                throw new ParseException("Invalid shortcut; magic is missing", 0);
            }

            // get the flags byte
            byte flags = link[0x14];

            // get the file attributes byte
            final int file_atts_offset = 0x18;
            byte file_atts = link[file_atts_offset];
            final byte is_dir_mask = 0x10;
            directory = (file_atts & is_dir_mask) > 0;

            // if the shell settings are present, skip them
            final int shell_offset = 0x4c;
            final byte has_shell_mask = 0x01;
            int shell_len = 0;

            if ((flags & has_shell_mask) > 0) {
                // the plus 2 accounts for the length marker itself
                shell_len = bytesToWord(link, shell_offset) + 2;
            }

            // get to the file settings
            int file_start = 0x4c + shell_len;

            final int file_location_info_flag_offset_offset = 0x08;
            int file_location_info_flag = link[file_start + file_location_info_flag_offset_offset];
            local = (file_location_info_flag & 1) == 1;
            // get the local volume and local system values
            //final int localVolumeTable_offset_offset = 0x0C;
            final int basename_offset_offset = 0x10;
            final int networkVolumeTable_offset_offset = 0x14;
            final int finalname_offset_offset = 0x18;
            int finalname_offset = bytesToDword(link, file_start + finalname_offset_offset) + file_start;
            String finalname = getNullDelimitedString(link, finalname_offset);

            if (local) {
                int basename_offset = bytesToDword(link, file_start + basename_offset_offset) + file_start;
                String basename = getNullDelimitedString(link, basename_offset);
                realFile = basename + finalname;

            } else {
                int networkVolumeTable_offset = link[file_start + networkVolumeTable_offset_offset] + file_start;
                final int shareName_offset_offset = 0x08;
                int shareName_offset = link[networkVolumeTable_offset + shareName_offset_offset] + networkVolumeTable_offset;
                String shareName = getNullDelimitedString(link, shareName_offset);
                realFile = shareName + "\\" + finalname;
            }

            // parse additional strings coming after file location
            int file_location_size = bytesToDword(link, file_start);
            int next_string_start = file_start + file_location_size;

            final byte has_description = 0b00000100;
            final byte has_relative_path = 0b00001000;
            final byte has_working_directory = 0b00010000;
            final byte has_command_line_arguments = 0b00100000;

            // if description is present, parse it
            if ((flags & has_description) > 0) {
                int string_len = bytesToWord(link, next_string_start) * 2; // times 2 because UTF-16
                description = getUTF16String(link, next_string_start + 2, string_len);
                next_string_start = next_string_start + string_len + 2;
            }

            // if relative path is present, parse it
            if ((flags & has_relative_path) > 0) {
                int string_len = bytesToWord(link, next_string_start) * 2; // times 2 because UTF-16
                relativePath = getUTF16String(link, next_string_start + 2, string_len);
                next_string_start = next_string_start + string_len + 2;
            }

            // if working directory is present, parse it
            if ((flags & has_working_directory) > 0) {
                int string_len = bytesToWord(link, next_string_start) * 2; // times 2 because UTF-16
                workingDirectory = getUTF16String(link, next_string_start + 2, string_len);
                next_string_start = next_string_start + string_len + 2;
            }

            // if command line arguments are present, parse them
            if ((flags & has_command_line_arguments) > 0) {
                int string_len = bytesToWord(link, next_string_start) * 2; // times 2 because UTF-16
                commandLineArguments = getUTF16String(link, next_string_start + 2, string_len);
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ParseException("Could not be parsed, probably not a valid WindowsShortcut", 0);
        }
    }
}
