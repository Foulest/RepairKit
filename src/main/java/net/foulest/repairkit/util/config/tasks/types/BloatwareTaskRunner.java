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

import java.util.*;
import java.util.regex.Pattern;

/**
 * The task runner for removing bloatware.
 *
 * @author Foulest
 */
public class BloatwareTaskRunner extends AbstractTaskRunner {

    /**
     * Constructs a new task runner instance.
     *
     * @param config The JSON config instance
     */
    public BloatwareTaskRunner(@NotNull Map<String, Map<String, Object>> config) {
        super(config);
    }

    @NotNull
    @Override
    @SuppressWarnings({"unchecked", "NestedMethodCall"})
    protected List<Runnable> createTasks(@NotNull Map<String, Object> entries) {
        @NotNull List<Runnable> tasks = new ArrayList<>();
        List<String> values = (List<String>) entries.get("values");
        @NotNull List<String> output = CommandUtil.getPowerShellCommandOutput("(Get-AppxPackage).ForEach({ $_.Name })", false, false);
        @NotNull Collection<String> installedPackages = new HashSet<>(output);

        @NotNull List<Pattern> patternsToRemove = values.stream()
                .map(pkg -> pkg.replace(".", "\\.").replace("*", ".*"))
                .map(Pattern::compile)
                .toList();

        @NotNull List<String> packagesToRemove = installedPackages.stream()
                .filter(installedPackage -> patternsToRemove.stream().anyMatch(pattern -> pattern.matcher(installedPackage).matches()))
                .toList();

        if (packagesToRemove.isEmpty()) {
            return tasks;
        }

        packagesToRemove.forEach(appPackage -> {
            @NotNull Runnable task = () -> {
                DebugUtil.debug("Removing bloatware app: " + appPackage);
                CommandUtil.runPowerShellCommand("Get-AppxPackage '" + appPackage + "' | Remove-AppxPackage", false);
            };

            tasks.add(task);
        });
        return tasks;
    }
}
