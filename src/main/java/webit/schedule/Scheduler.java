// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimeZone;
import webit.schedule.core.CronParser;
import webit.schedule.core.InvalidCronException;
import webit.schedule.core.Matcher;
import webit.schedule.impl.DefaultTaskExecutorFactory;
import webit.schedule.util.ThreadUtil;

/**
 *
 * @author zqq90
 */
public final class Scheduler {

    private final static int TTL = 60 * 1000;
    private static int threadInitNumber = 0;

    private final Object lockThis = new Object();
    private volatile boolean initialized;
    
    private boolean daemon;
    private boolean enableNotifyThread;
    private int timeOffset;

    private boolean started;
    private boolean paused;
    private NotifyThread notifyThread;
    private TimerThread timerThread;
    private final ArrayList<TaskExecutorEntry> executorEntrys;
    private TaskExecutorFactory executorFactory;

    public Scheduler() {
        this.executorEntrys = new ArrayList<TaskExecutorEntry>();
        this.timeOffset = TimeZone.getDefault().getRawOffset();
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public void setTimeZone(int timeZone) {
        timeOffset = timeZone * (60 * 60 * 1000); // timeZone * 1h
    }

    public void setTimeOffset(int timeOffset) {
        this.timeOffset = timeOffset;
    }

    public int getTimeOffset() {
        return timeOffset;
    }

    public void setEnableNotifyThread(boolean enableNotifyThread) {
        this.enableNotifyThread = enableNotifyThread;
    }

    public void setExecutorFactoryClass(Class executorFactoryClass) {
        try {
            executorFactory = (TaskExecutorFactory) executorFactoryClass.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal class name: " + executorFactoryClass, e);
        }
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
        final Time time = new Time(millis, this.timeOffset);
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

    private static synchronized String nextThreadNumString() {
        return Integer.toString(threadInitNumber++);
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
            this.start();
        }

        @Override
        public void run() {
            this.scheduler.notifyAllExecutor(this.millis);
        }
    }

    private final static class TimerThread extends Thread {

        private final Scheduler scheduler;

        TimerThread(Scheduler scheduler) {
            super("webit-scheduler-timer-".concat(nextThreadNumString()));
            this.scheduler = scheduler;
        }

        private static void safeSleepToMillis(long nextMinute) throws InterruptedException {
            long sleepTime;
            //MISSTAKE_ALLOW = 200
            while ((sleepTime = nextMinute - System.currentTimeMillis()) > 200) {
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
                } catch (InterruptedException e) {
                    // exit if interrupted!
                    break;
                }

                // Calculating next minute.
                do {
                    nextMinute += TTL;
                } while (nextMinute < System.currentTimeMillis());
            }
        }
    }

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

}
