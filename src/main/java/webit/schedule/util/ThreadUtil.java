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
            tillDies(thread);
        }
    }

    public static void tillDies(Thread thread) {
        if (thread != null) {
            for (;;) {
                try {
                    thread.join();
                    break;
                } catch (InterruptedException ignore) {
                }
            }
        }
    }
}
