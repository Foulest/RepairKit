/*
  RepairKit - an all-in-one Java-based Windows repair and maintenance toolkit.
  Copyright (C) 2024 Foulest (https://github.com/Foulest)

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package net.foulest.repairkit.util;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RegistryUtil {

    /**
     * Creates a registry key if it doesn't exist.
     *
     * @param hkey    Registry key to check.
     * @param keyPath Path to the registry key.
     */
    private static void createRegistryKeyIfNeeded(WinReg.HKEY hkey, String keyPath) {
        if (!Advapi32Util.registryKeyExists(hkey, keyPath)) {
            Advapi32Util.registryCreateKey(hkey, keyPath);
        }
    }

    /**
     * Sets a registry int value.
     *
     * @param hkey    Registry key to check.
     * @param keyPath Path to the registry key.
     * @param keyName Name of the registry key.
     * @param value   Value to set.
     */
    public static void setRegistryIntValue(WinReg.HKEY hkey, String keyPath, String keyName, int value) {
        createRegistryKeyIfNeeded(hkey, keyPath);
        Advapi32Util.registrySetIntValue(hkey, keyPath, keyName, value);
    }

    /**
     * Sets a registry string value.
     *
     * @param hkey    Registry key to check.
     * @param keyPath Path to the registry key.
     * @param keyName Name of the registry key.
     * @param value   Value to set.
     */
    public static void setRegistryStringValue(WinReg.HKEY hkey, String keyPath, String keyName, String value) {
        createRegistryKeyIfNeeded(hkey, keyPath);
        Advapi32Util.registrySetStringValue(hkey, keyPath, keyName, value);
    }

    /**
     * Deletes a registry value.
     *
     * @param hkey    Registry key to check.
     * @param keyPath Path to the registry key.
     * @param value   Value to delete.
     */
    public static void deleteRegistryValue(WinReg.HKEY hkey, String keyPath, String value) {
        if (Advapi32Util.registryValueExists(hkey, keyPath, value)) {
            Advapi32Util.registryDeleteValue(hkey, keyPath, value);
        }
    }

    /**
     * Deletes a registry key.
     *
     * @param hkey    Registry key to check.
     * @param keyPath Path to the registry key.
     */
    public static void deleteRegistryKey(WinReg.HKEY hkey, String keyPath) {
        if (Advapi32Util.registryKeyExists(hkey, keyPath)) {
            Advapi32Util.registryDeleteKey(hkey, keyPath);
        }
    }

    /**
     * Lists all sub keys of a registry key.
     *
     * @param root    Registry key to check.
     * @param keyPath Path to the registry key.
     * @return List of sub keys.
     */
    public static @NotNull List<String> listSubKeys(WinReg.HKEY root, String keyPath) {
        List<String> subKeysList = new ArrayList<>();
        WinReg.HKEYByReference hkeyRef = Advapi32Util.registryGetKey(root, keyPath, WinNT.KEY_READ);

        try {
            IntByReference lpcSubKeys = new IntByReference();
            IntByReference lpcMaxSubKeyLen = new IntByReference();

            if (Advapi32.INSTANCE.RegQueryInfoKey(hkeyRef.getValue(), null, null, null,
                    lpcSubKeys, lpcMaxSubKeyLen, null, null, null,
                    null, null, null) == WinError.ERROR_SUCCESS) {
                int maxSubKeyLen = lpcMaxSubKeyLen.getValue() + 1; // account for null-terminator
                char[] nameBuffer = new char[maxSubKeyLen];

                for (int index = 0; index < lpcSubKeys.getValue(); index++) {
                    IntByReference lpcchValueName = new IntByReference(maxSubKeyLen);

                    if (Advapi32.INSTANCE.RegEnumKeyEx(hkeyRef.getValue(), index, nameBuffer, lpcchValueName,
                            null, null, null, null) == WinError.ERROR_SUCCESS) {
                        subKeysList.add(Native.toString(nameBuffer));
                    }
                }
            }
        } finally {
            Advapi32.INSTANCE.RegCloseKey(hkeyRef.getValue());
        }
        return subKeysList;
    }
}
