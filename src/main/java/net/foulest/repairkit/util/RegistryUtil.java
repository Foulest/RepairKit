package net.foulest.repairkit.util;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class RegistryUtil {

    private static void createRegistryKeyIfNeeded(@NonNull WinReg.HKEY hkey, @NonNull String keyPath) {
        if (!Advapi32Util.registryKeyExists(hkey, keyPath)) {
            Advapi32Util.registryCreateKey(hkey, keyPath);
        }
    }

    public static void setRegistryIntValue(@NonNull WinReg.HKEY hkey, @NonNull String keyPath,
                                           @NonNull String keyName, int value) {
        createRegistryKeyIfNeeded(hkey, keyPath);
        Advapi32Util.registrySetIntValue(hkey, keyPath, keyName, value);
    }

    public static void setRegistryStringValue(@NonNull WinReg.HKEY hkey, @NonNull String keyPath,
                                              @NonNull String keyName, @NonNull String value) {
        createRegistryKeyIfNeeded(hkey, keyPath);
        Advapi32Util.registrySetStringValue(hkey, keyPath, keyName, value);
    }

    public static void deleteRegistryValue(@NonNull WinReg.HKEY hkey, @NonNull String keyPath, @NonNull String value) {
        if (Advapi32Util.registryValueExists(hkey, keyPath, value)) {
            Advapi32Util.registryDeleteValue(hkey, keyPath, value);
        }
    }

    public static void deleteRegistryKey(@NonNull WinReg.HKEY hkey, @NonNull String keyPath) {
        if (Advapi32Util.registryKeyExists(hkey, keyPath)) {
            Advapi32Util.registryDeleteKey(hkey, keyPath);
        }
    }

    public static List<String> listSubKeys(@NonNull WinReg.HKEY root, @NonNull String keyPath) {
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
