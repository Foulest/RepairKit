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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TaskUtil {

    /**
     * Executes a list of tasks concurrently using an ExecutorService and CountDownLatch.
     *
     * @param tasks the list of tasks to execute
     */
    public static void executeTasks(@NotNull Collection<Runnable> tasks) {
        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(tasks.size());

        for (Runnable task : tasks) {
            executor.submit(() -> {
                try {
                    task.run();
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all tasks to complete
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            ex.printStackTrace();
        }

        // Shut down the executor
        executor.shutdown();
    }
}
