package com.kodedu.tryjshell.helper;

import java.util.concurrent.*;

/**
 * Created by usta on 29.03.2015.
 */
public class ThreadHelper {

    private static final Semaphore uiSemaphore = new Semaphore(1);
    private static final ExecutorService singleExecutorService = Executors.newSingleThreadExecutor();


    private static void releaseUiSemaphor() {
        singleExecutorService.submit(() -> {
            uiSemaphore.release();
        });
    }

    public static void start(Runnable runnable) {
        Thread thread = new Thread(runnable, "JShell I/O");
        thread.start();
    }

    public static ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
        return Executors.newSingleThreadScheduledExecutor()
                .schedule(runnable, delay, timeUnit);
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit timeUnit) {
        return Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(runnable, initialDelay, period, timeUnit);
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
    }
}
