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

import net.foulest.repairkit.util.CommandUtil;
import net.foulest.repairkit.util.DebugUtil;
import net.foulest.repairkit.util.config.tasks.AbstractTaskRunner;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The task runner for modifying Windows services.
 *
 * @author Foulest
 */
public class ServicesTaskRunner extends AbstractTaskRunner {

    /**
     * Constructs a new task runner instance.
     *
     * @param config The JSON config instance
     */
    public ServicesTaskRunner(Map<String, Map<String, Object>> config) {
        super(config);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    protected List<Runnable> createTasks(@NotNull Map<String, Object> entries) {
        @NotNull List<Runnable> tasks = new ArrayList<>();
        Map<String, Object> values = (Map<String, Object>) entries.get("values");

        values.forEach((key, value) -> {
            @NotNull Runnable task = () -> {
                ServiceAction action;

                if (value instanceof String) {
                    switch ((String) value) {
                        case "boot":
                            action = ServiceAction.BOOT;
                            break;
                        case "system":
                            action = ServiceAction.SYSTEM;
                            break;
                        case "auto":
                            action = ServiceAction.AUTO;
                            break;
                        case "demand":
                            action = ServiceAction.DEMAND;
                            break;
                        case "disabled":
                            action = ServiceAction.DISABLED;
                            break;
                        case "delayed-auto":
                            action = ServiceAction.DELAYED_AUTO;
                            break;
                        default:
                            DebugUtil.debug("Invalid value: " + value);
                            return;
                    }
                } else {
                    DebugUtil.debug("Invalid value: " + value);
                    return;
                }

                switch (action) {
                    case BOOT:
                        DebugUtil.debug("Setting service to boot: " + key);
                        CommandUtil.runCommand("sc config \"" + key + "\" start=boot", true);
                        break;

                    case SYSTEM:
                        DebugUtil.debug("Setting service to system: " + key);
                        CommandUtil.runCommand("sc config \"" + key + "\" start=system", true);
                        break;

                    case AUTO:
                        DebugUtil.debug("Setting service to auto: " + key);
                        CommandUtil.runCommand("sc config \"" + key + "\" start=auto", true);
                        break;

                    case DEMAND:
                        DebugUtil.debug("Setting service to demand: " + key);
                        CommandUtil.runCommand("sc config \"" + key + "\" start=demand", true);
                        break;

                    case DISABLED:
                        DebugUtil.debug("Setting service to disabled: " + key);
                        CommandUtil.runCommand("sc stop \"" + key + "\"", true);
                        CommandUtil.runCommand("sc config \"" + key + "\" start=disabled", true);
                        break;

                    case DELAYED_AUTO:
                        DebugUtil.debug("Setting service to delayed-auto: " + key);
                        CommandUtil.runCommand("sc config \"" + key + "\" start=delayed-auto", true);
                        break;

                    default:
                        break;
                }
            };

            tasks.add(task);
        });
        return tasks;
    }

    /**
     * Represents the service action.
     */
    private enum ServiceAction {
        BOOT,
        SYSTEM,
        AUTO,
        DEMAND,
        DISABLED,
        DELAYED_AUTO
    }
}
