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
package net.foulest.repairkit.util.config.tasks;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.foulest.repairkit.util.DebugUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Represents an abstract task runner.
 *
 * @author Foulest
 */
@Data
@AllArgsConstructor
public abstract class AbstractTaskRunner implements TaskRunner {

    /**
     * The JSON config instance.
     */
    protected final Map<String, Map<String, Object>> config;

    /**
     * Gets the tasks to run from the config.
     *
     * @return The tasks to run
     */
    @NotNull
    @Override
    public List<Runnable> getTasks() {
        List<Runnable> tasks = new ArrayList<>();

        config.forEach((category, entries) -> {
            if (!isEnabled(entries)) {
                DebugUtil.debug("Category is disabled: " + category);
                return;
            }

            if (!hasValues(entries)) {
                DebugUtil.debug("No values found for category: " + category);
                return;
            }

            List<Runnable> runnables = createTasks(entries);
            tasks.addAll(runnables);
        });
        return tasks;
    }

    /**
     * Checks if the category is enabled.
     *
     * @param entries The entries to check
     * @return {@code true} if the category is enabled, otherwise {@code false}
     */
    private static boolean isEnabled(@NotNull Map<String, Object> entries) {
        return !entries.containsKey("enabled")
                || (entries.containsKey("enabled")
                && entries.get("enabled") instanceof Boolean
                && (Boolean) entries.get("enabled"));
    }

    /**
     * Checks if the category has values.
     *
     * @param entries The entries to check
     * @return {@code true} if the category has values, otherwise {@code false}
     */
    private static boolean hasValues(@NotNull Map<String, Object> entries) {
        if (!entries.containsKey("values")) {
            return false;
        }

        Object values = entries.get("values");

        if (values instanceof Map) {
            return !((Map<?, ?>) values).isEmpty();
        } else if (values instanceof Collection) {
            return !((Collection<?>) values).isEmpty();
        }
        return false;
    }

    /**
     * Creates the tasks from the entries.
     *
     * @param entries The entries to create tasks from
     * @return The tasks
     */
    protected abstract List<Runnable> createTasks(Map<String, Object> entries);
}
