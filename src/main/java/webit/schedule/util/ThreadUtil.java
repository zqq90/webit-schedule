// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.util;

/**
 *
 * @author zqq90
 */
public class ThreadUtil {

    private ThreadUtil() {
    }

    public static void interruptAndTillDies(final Thread thread) {
        if (thread != null) {
            thread.interrupt();
            ThreadUtil.tillDies(thread);
        }
    }

    public static void tillDies(Thread thread) {
        if (thread != null) {
            boolean dead = false;
            do {
                try {
                    thread.join();
                    dead = true;
                } catch (InterruptedException ignore) {
                }
            } while (!dead);
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {
        }
    }
}
