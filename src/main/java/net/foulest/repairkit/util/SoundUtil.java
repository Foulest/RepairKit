package net.foulest.repairkit.util;

import lombok.NonNull;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoundUtil {

    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    /**
     * Plays a sound.
     *
     * @param soundName Name of the sound to play.
     */
    public static void playSound(@NonNull String soundName) {
        Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty(soundName);

        if (runnable != null) {
            threadPool.execute(runnable);
        }
    }
}
