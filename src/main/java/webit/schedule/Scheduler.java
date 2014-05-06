// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule;

import webit.schedule.core.Matcher;
import webit.schedule.core.CronParser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimeZone;
import webit.schedule.core.InvalidCronException;
import webit.schedule.impl.DefaultTaskExecutorFactory;
import webit.schedule.util.ThreadUtil;

/**
 *
 * @author zqq90
 */
public final class Scheduler {

    private final static int ONE_MINUTE = 60 * 1000;
    private final static int TTL = ONE_MINUTE;
    //settings
    private boolean daemon = false;
    private boolean enableNotifyThread = false;
    private int timeOffset = 0;

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public void setTimeOffset(int timeOffset) {
        this.timeOffset = timeOffset;
    }

    public void setExecutorFactoryClass(Class executorFactoryClass) {
        try {
            executorFactory = (TaskExecutorFactory) executorFactoryClass.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal class name: " + executorFactoryClass, e);
        }
    }

    public void setTimeZone(int timeZone) {
        timeOffset = 60 * ONE_MINUTE * timeZone; // timeZone * 1h
    }

    public void setEnableNotifyThread(boolean enableNotifyThread) {
        this.enableNotifyThread = enableNotifyThread;
    }

    //
    private boolean started = false;
    private volatile boolean initialized;
    //
    private NotifyThread notifyThread;
    private TimerThread timerThread;
    private final ArrayList<TaskExecutorEntry> executorEntrys;
    private TaskExecutorFactory executorFactory = null;
    private final Object lockThis = new Object();

    public Scheduler() {
        this.executorEntrys = new ArrayList<TaskExecutorEntry>();
        this.timeOffset = TimeZone.getDefault().getRawOffset();
    }

    private void initialize() {
        if (initialized == false) {
            synchronized (lockThis) {
                if (initialized == false) {
                    if (this.executorFactory == null) {
                        executorFactory = new DefaultTaskExecutorFactory();
                    }
                    initialized = true;
                }
            }
        }
    }

    public void addTask(String cron, Task task) throws InvalidCronException {
        initialize();
        this.addTask(CronParser.parse(cron), this.executorFactory.createTaskExecutor(task));
    }

    private void addTask(Matcher matcher, TaskExecutor executor) {
        synchronized (this.executorEntrys) {
            this.executorEntrys.add(new TaskExecutorEntry(matcher, executor));
        }
    }

    private TaskExecutorEntry[] getTaskExecutors() {
        synchronized (executorEntrys) {
            return executorEntrys.toArray(new TaskExecutorEntry[executorEntrys.size()]);
        }
    }

    public boolean remove(final Task task) {
        TaskExecutor taskExecutor = null;
        synchronized (this.executorEntrys) {
            for (Iterator<TaskExecutorEntry> it = executorEntrys.iterator(); it.hasNext();) {
                taskExecutor = it.next().executor;
                if (taskExecutor.getTask() == task) {
                    it.remove();
                    break;
                }
            }
        }
        if (taskExecutor != null) {
            taskExecutor.stopAndWait();
            return true;
        }
        return false;
    }

    public void start() throws IllegalStateException, IllegalArgumentException {
        initialize();
        synchronized (lockThis) {
            if (started) {
                throw new IllegalStateException("Scheduler already started");
            }
            (timerThread = new TimerThread(this)).start();
            // Change the state of the scheduler.
            started = true;
        }
    }

    public void stop() throws IllegalStateException {
        synchronized (lockThis) {
            if (started) {
                // Interrupts the timer and waits for its death.
                ThreadUtil.interruptAndTillDies(this.timerThread);
                ThreadUtil.interruptAndTillDies(this.notifyThread);
                final TaskExecutorEntry[] entrys;
                int i = (entrys = this.getTaskExecutors()).length;
                while (i != 0) {
                    entrys[--i].executor.askforStop();
                }
                i = entrys.length;
                while (i != 0) {
                    entrys[--i].executor.stopAndWait();
                }
                // Change the state of the object.
                started = false;
            } else {
                throw new IllegalStateException("Scheduler not started");
            }
        }
    }

    private boolean paused = false;

    public void pauseAllIfSupport() throws IllegalStateException {
        synchronized (lockThis) {
            if (started) {
                if (paused == false) {
                    // Interrupts the timer and waits for its death.
                    ThreadUtil.interruptAndTillDies(this.timerThread);
                    ThreadUtil.interruptAndTillDies(this.notifyThread);
                    final TaskExecutorEntry[] entrys;
                    int i = (entrys = this.getTaskExecutors()).length;
                    while (i != 0) {
                        entrys[--i].executor.askforStop();
                    }
                    i = entrys.length;
                    while (i != 0) {
                        entrys[--i].executor.stopAndWait();
                    }
                    // Change the state of the object.
                    paused = true;
                }
            } else {
                throw new IllegalStateException("Scheduler not started");
            }
        }
    }

    public void goonAllIfPaused() throws IllegalStateException {
        synchronized (lockThis) {
            if (started) {
                if (paused) {
                    // Interrupts the timer and waits for its death.
                    final TaskExecutorEntry[] entrys;
                    int i = (entrys = this.getTaskExecutors()).length;
                    while (i != 0) {
                        entrys[--i].executor.goonIfPaused();
                    }
                    (timerThread = new TimerThread(this)).start();
                    paused = false;
                }
            } else {
                throw new IllegalStateException("Scheduler not started");
            }
        }
    }

    private void click(final long millis) {
        if (this.enableNotifyThread) {

            final NotifyThread myNotifyThread;
            //XXX: if ((myNotifyThread = this.notifyThread) != null) ??
            this.notifyThread = myNotifyThread
                    = new NotifyThread(this, "webit-scheduler-notify-".concat(nextThreadNumString()));
            myNotifyThread.startNotify(millis);
        } else {
            this.notifyAllExecutor(millis);
        }
    }

    private void notifyAllExecutor(final long millis) {
        final Time time = new Time(millis + this.timeOffset);
        final TaskExecutorEntry[] entrys;
        int i = (entrys = this.getTaskExecutors()).length;
        while (i != 0) {
            try {
                entrys[--i].notify(time);
            } catch (Exception e) {
                //Exceptions when notify
                //TODO: we should log this
            }
        }
    }

    private final static class NotifyThread extends Thread {

        private final Scheduler scheduler;
        private long millis;

        NotifyThread(Scheduler scheduler, String name) {
            super(name);
            this.setDaemon((this.scheduler = scheduler).daemon);
        }

        void startNotify(long millis) {
            this.millis = millis;
//            if (this.isAlive()) {
//                //TODO: if the pre notify not complate we should log this
//                try {
//                    this.interrupt();
//                } catch (Exception e) {
//                    //ignore
//                }
//            }
            this.start();
        }

        @Override
        public void run() {
            this.scheduler.notifyAllExecutor(this.millis);
        }
    }
    //

    private final static class TimerThread extends Thread {

        private static final long MISSTAKE_ALLOW = 200;
        private final Scheduler scheduler;

        public TimerThread(Scheduler scheduler) {
            super("webit-scheduler-timer-".concat(nextThreadNumString()));
            this.scheduler = scheduler;
        }

        private static void safeSleepToMillis(long nextMinute) throws InterruptedException {
            long sleepTime;
            while ((sleepTime = nextMinute - System.currentTimeMillis()) > MISSTAKE_ALLOW) {
                Thread.sleep(sleepTime);
            }
        }

        @Override
        public void run() {
            long nextMinute = ((System.currentTimeMillis() / TTL) + 1) * TTL;
            // Work until the scheduler is started.
            for (;;) {
                try {
                    safeSleepToMillis(nextMinute);

                    this.scheduler.click(nextMinute);
                } catch (Exception e) {
                    // Must exit!
                    break;
                }

                // Calculating next minute.
                do {
                    nextMinute += TTL;
                } while (nextMinute < System.currentTimeMillis());
            }
        }
    }
    //

    private final static class TaskExecutorEntry {

        private final Matcher matcher;
        private final TaskExecutor executor;

        TaskExecutorEntry(Matcher matcher, TaskExecutor executor) {
            this.matcher = matcher;
            this.executor = executor;
        }

        void notify(Time time) {
            if (matcher.match(time)) {
                executor.runIfNot(time);
            }
        }
    }

    private static int threadInitNumber = 0;

    private static synchronized String nextThreadNumString() {
        return Integer.toString(threadInitNumber++);
    }

    public int getTimeOffset() {
        return timeOffset;
    }
}
