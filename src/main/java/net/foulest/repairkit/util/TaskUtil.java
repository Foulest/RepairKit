package net.foulest.repairkit.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskUtil {

    /**
     * Executes a list of tasks concurrently using an ExecutorService and CountDownLatch.
     *
     * @param tasks the list of tasks to execute
     */
    public static void executeTasks(@NotNull List<Runnable> tasks) {
        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(tasks.size());

        for (Runnable task : tasks) {
            executor.submit(() -> {
                try {
                    task.run();
                } catch (Exception ex) {
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
