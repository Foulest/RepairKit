package net.foulest.repairkit.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstantUtil {

    public static final String ARIAL = "Arial";

    public static final String ERROR_SOUND = "win.sound.hand";
    public static final String WARNING_SOUND = "win.sound.asterisk";

    public static final String VERSION_AUTO_UPDATED = "Version: Auto-Updated";

    public static final String SAFE_MODE_TITLE = "Safe Mode Detected";
    public static final String SAFE_MODE_MESSAGE = "This feature is incompatible with Safe Mode."
            + "\nPlease restart your system in normal mode to use this feature.";

    public static final String INCOMPATIBLE_OS_TITLE = "Incompatible Operating System";

    public static final String OUTDATED_OS_TITLE = "Outdated Operating System";
    public static final String OUTDATED_OS_MESSAGE = "This feature is incompatible with outdated operating systems."
            + "\nPlease upgrade to Windows 10 or 11 to use this feature.";

    public static final String BAD_FILE_LOCATION = "RepairKit cannot be run from this folder/directory."
            + "\nPlease move the RepairKit folder to a different location and try again.";
}
