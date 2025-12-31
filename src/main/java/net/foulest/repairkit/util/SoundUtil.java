/*
 * RepairKit - an all-in-one Java-based Windows repair and maintenance toolkit.
 * Copyright (C) 2026 Foulest (https://github.com/Foulest)
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

import lombok.Data;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Utility class for sound operations.
 *
 * @author Foulest
 */
@Data
public class SoundUtil {

    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    /**
     * Plays a sound.
     *
     * @param soundName Name of the sound to play.
     */
    public static void playSound(String soundName) {
        DebugUtil.debug("Playing sound: " + soundName);
        Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty(soundName);

        if (runnable != null) {
            threadPool.execute(runnable);
        }
    }
}
