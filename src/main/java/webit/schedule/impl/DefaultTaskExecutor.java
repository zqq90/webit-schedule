// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.impl;

import webit.schedule.InitableTask;
import webit.schedule.Scheduler;
import webit.schedule.Task;
import webit.schedule.TaskContext;
import webit.schedule.TaskExecutor;
import webit.schedule.TaskExecutorStatus;
import webit.schedule.Time;
import webit.schedule.util.ThreadUtil;

/**
 *
 * @author Zqq
 */
public class DefaultTaskExecutor implements TaskExecutor {

    private final Object lockThis = new Object();
    protected final Task task;
    protected final Scheduler scheduler;
    protected final DefaultTaskContext taskContext;
    protected final String threadNamePrefix;
    protected boolean requestedStop = false;
    protected boolean requestedPause = false;
    protected boolean running = false;
    protected Thread executeThread;
    protected Time time;

    protected int threadCount = 1;

    public DefaultTaskExecutor(Task task, Scheduler scheduler) {
        this.task = task;
        this.threadNamePrefix = "schedule-" + task.getTaskName() + '-';
        this.scheduler = scheduler;
        this.taskContext = new DefaultTaskContext(this);
        initTask();
    }

    protected final void initTask() {
        if (task instanceof InitableTask) {
            ((InitableTask) task).init(this);
        }
    }

    @Override
    public void runIfNot(Time time) {
        this.time = time;
        this.requestedStop = false;
        if (!running) {
            synchronized (lockThis) {
                if (!running) {
                    running = true;
                    Thread thread;
                    executeThread = thread = new Thread(this.threadNamePrefix.concat(Integer.toString(threadCount++))) {
                        @Override
                        public void run() {
                            try {
                                task.execute(taskContext);
                            } finally {
                                running = false;
                            }
                        }
                    };
                    thread.start();
                }
            }
        }
    }

    @Override
    public void stopAndWait() {
        synchronized (lockThis) {
            if (this.requestedStop == false) {
                askforStop();
            }
            if (running) {
                ThreadUtil.tillDies(this.executeThread);
                running = false;
            }
        }
    }

    @Override
    public void askforStop() {
        this.requestedStop = true;
        goonIfPaused();
    }

    @Override
    public void goonIfPaused() {
        if (this.requestedPause) {
            synchronized (lockThis) {
                if (this.requestedPause) {
                    this.requestedPause = false;
                    this.lockThis.notifyAll();
                }
            }
        }
    }

    @Override
    public void askforPause() {
        this.requestedPause = true;
    }

    protected void pauseIfRequested() {
        if (this.requestedPause) {
            synchronized (this.lockThis) {
                if (this.requestedPause) {
                    try {
                        this.lockThis.wait();
                    } catch (InterruptedException ignore) {
                    }
                }
            }
        }
    }

    private boolean isRequestedStopOrPause() {
        return this.requestedStop || this.requestedPause;
    }

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public TaskExecutorStatus getStatus() {
        if (requestedStop) {
            return running ? TaskExecutorStatus.STOPPING : TaskExecutorStatus.STOPPED;
        } else if (requestedPause) {
            return running ? TaskExecutorStatus.PAUSING : TaskExecutorStatus.PAUSED;
        } else {
            return running ? TaskExecutorStatus.RUNNING : TaskExecutorStatus.HOLDDING;
        }
    }

    protected static class DefaultTaskContext implements TaskContext {

        protected final DefaultTaskExecutor taskExecutor;

        public DefaultTaskContext(DefaultTaskExecutor taskExecutor) {
            this.taskExecutor = taskExecutor;
        }

        @Override
        public void pauseIfRequested() {
            this.taskExecutor.pauseIfRequested();
        }

        @Override
        public boolean isRequestedStop() {
            return this.taskExecutor.requestedStop;
        }

        @Override
        public Scheduler getScheduler() {
            return this.taskExecutor.scheduler;
        }

        @Override
        public Time getTime() {
            return this.taskExecutor.time;
        }

        @Override
        public boolean isRequestedPause() {
            return this.taskExecutor.requestedPause;
        }

        @Override
        public boolean isRequestedStopOrPause() {
            return this.taskExecutor.isRequestedStopOrPause();
        }
    }
}
