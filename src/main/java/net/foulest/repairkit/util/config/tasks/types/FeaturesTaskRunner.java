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
 * The task runner for modifying Windows features.
 *
 * @author Foulest
 */
public final class FeaturesTaskRunner extends AbstractTaskRunner {

    /**
     * Constructs a new task runner instance.
     *
     * @param config The JSON config instance
     */
    public FeaturesTaskRunner(Map<String, Map<String, Object>> config) {
        super(config);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    protected List<Runnable> createTasks(@NotNull Map<String, Object> entries) {
        List<Runnable> tasks = new ArrayList<>();
        List<String> values = (List<String>) entries.get("values");

        values.forEach(value -> {
            Runnable task = () -> {
                DebugUtil.debug("Removing feature: " + value);

                if (CommandUtil.getPowerShellCommandOutput("Get-WindowsOptionalFeature -FeatureName '" + value
                        + "' -Online | Select-Object -Property State", false, false).toString().contains("Enabled")) {
                    CommandUtil.runCommand("DISM /Online /Disable-Feature /FeatureName:\"" + value + "\" /NoRestart", false);
                }
            };

            tasks.add(task);
        });
        return tasks;
    }
}
