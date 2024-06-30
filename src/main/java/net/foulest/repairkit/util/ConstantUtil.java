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
