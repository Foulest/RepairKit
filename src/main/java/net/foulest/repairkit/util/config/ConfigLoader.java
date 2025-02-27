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
package net.foulest.repairkit.util.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Utility class for loading configuration files.
 *
 * @author Foulest
 */
@Data
public class ConfigLoader {

    private final Map<String, Map<String, Object>> config;

    /**
     * Constructs a new config loader instance.
     *
     * @param file The file to load
     */
    public ConfigLoader(@NotNull File file) {
        try (@NotNull FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            Type type = new TypeToken<Map<String, Map<String, Object>>>() {
            }.getType();

            config = new Gson().fromJson(reader, type);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load config file: " + file.getName(), ex);
        }
    }
}
