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
package net.foulest.repairkit.util;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Utility class for task operations.
 *
 * @author Foulest
 */
@Data
public class TaskUtil {

    /**
     * Executes a list of tasks concurrently using an ExecutorService and CountDownLatch.
     *
     * @param tasks the list of tasks to execute
     */
    public static void executeTasks(@NotNull Collection<Runnable> tasks) {
        ExecutorService executor = Executors.newWorkStealingPool();
        int size = tasks.size();
        CountDownLatch latch = new CountDownLatch(size);

        // Submit each task to the executor
        for (Runnable task : tasks) {
            executor.submit(() -> {
                try {
                    task.run();
                } catch (RuntimeException ex) {
                    DebugUtil.warn("Failed to execute task", ex);
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all tasks to complete
        try {
            latch.await();
        } catch (InterruptedException ex) {
            DebugUtil.warn("Failed to wait for tasks to complete", ex);
            Thread.currentThread().interrupt();
        }

        // Shut down the executor
        executor.shutdown();
    }
}
