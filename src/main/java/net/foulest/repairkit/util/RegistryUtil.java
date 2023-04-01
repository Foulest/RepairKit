package net.foulest.repairkit.util;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

public class RegistryUtil {

    private static void createRegistryKeyIfNeeded(WinReg.HKEY hkey, String keyPath) {
        if (!Advapi32Util.registryKeyExists(hkey, keyPath)) {
            Advapi32Util.registryCreateKey(hkey, keyPath);
        }
    }

    public static void setRegistryIntValue(WinReg.HKEY hkey, String keyPath, String keyName, int value) {
        createRegistryKeyIfNeeded(hkey, keyPath);
        Advapi32Util.registrySetIntValue(hkey, keyPath, keyName, value);
    }

    public static void setRegistryStringValue(WinReg.HKEY hkey, String keyPath, String keyName, String value) {
        createRegistryKeyIfNeeded(hkey, keyPath);
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
