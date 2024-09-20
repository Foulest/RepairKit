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
package net.foulest.repairkit.util.config.tasks.types;

import com.sun.jna.platform.win32.WinReg;
import net.foulest.repairkit.util.DebugUtil;
import net.foulest.repairkit.util.RegistryUtil;
import net.foulest.repairkit.util.config.tasks.AbstractTaskRunner;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The task runner for modifying the Windows registry.
 *
 * @author Foulest
 */
public final class RegistryTaskRunner extends AbstractTaskRunner {

    /**
     * Constructs a new task runner instance.
     *
     * @param config The JSON config instance
     */
    public RegistryTaskRunner(Map<String, Map<String, Object>> config) {
        super(config);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    protected List<Runnable> createTasks(@NotNull Map<String, Object> entries) {
        List<Runnable> tasks = new ArrayList<>();
        Map<String, Object> values = (Map<String, Object>) entries.get("values");

        values.forEach((key, value) -> {
            String keyPath;
            String keyName = "";
            WinReg.HKEY hkey;
            RegistryAction action;
            Object keyValue = value;

            // Get the HKEY of the path.
            if (key.startsWith("HKEY_LOCAL_MACHINE") || key.startsWith("HKLM")) {
                hkey = WinReg.HKEY_LOCAL_MACHINE;
                keyPath = key.replace("HKEY_LOCAL_MACHINE\\", "").replace("HKLM\\", "");
            } else if (key.startsWith("HKEY_CURRENT_USER") || key.startsWith("HKCU")) {
                hkey = WinReg.HKEY_CURRENT_USER;
                keyPath = key.replace("HKEY_CURRENT_USER\\", "").replace("HKCU\\", "");
            } else {
                DebugUtil.debug("Invalid key: " + key);
                return;
            }

            // Get the registry action from the path.
            if (value instanceof String) {
                if (value.equals("DELETE_VALUE")) {
                    action = RegistryAction.DELETE_VALUE;
                } else if (value.equals("DELETE_KEY")) {
                    action = RegistryAction.DELETE_KEY;
                } else {
                    action = RegistryAction.SET_STRING_VALUE;
                }
            } else if (value instanceof Integer || value instanceof Double) {
                keyValue = value instanceof Double ? ((Double) value).intValue() : value;
                action = RegistryAction.SET_INT_VALUE;
            } else {
                DebugUtil.debug("Invalid value: " + value);
                return;
            }

            // Separate the key name from the path.
            if (action != RegistryAction.DELETE_KEY) {
                int index = keyPath.lastIndexOf('\\');
                keyName = keyPath.substring(index + 1);
                keyPath = keyPath.substring(0, index);
            }

            // Execute the associated action.
            switch (action) {
                case SET_INT_VALUE:
                    RegistryUtil.setRegistryIntValue(hkey, keyPath, keyName, (Integer) keyValue);
                    break;

                case SET_STRING_VALUE:
                    RegistryUtil.setRegistryStringValue(hkey, keyPath, keyName, (String) keyValue);
                    break;

                case DELETE_VALUE:
                    RegistryUtil.deleteRegistryValue(hkey, keyPath, keyName);
                    break;

                case DELETE_KEY:
                    RegistryUtil.deleteRegistryKey(hkey, keyPath);
                    break;

                default:
                    break;
            }
        });
        return tasks;
    }

    /**
     * Represents the registry action.
     */
    private enum RegistryAction {
        SET_INT_VALUE,
        SET_STRING_VALUE,
        DELETE_VALUE,
        DELETE_KEY
    }
}
