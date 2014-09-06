// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule;

/**
 *
 * @author zqq90
 */
public interface TaskExecutor {

    int STATE_STOPPED = 1;
    int STATE_PAUSED = 2;
    int STATE_STOPPING = 3;
    int STATE_PAUSING = 4;
    int STATE_RUNNING = 5;
    int STATE_HOLDING = 6;

    void runIfNot(Time time);

    void stopAndWait();

    void goonIfPaused();

    void askforPause();

    void askforStop();

    Task getTask();

    Scheduler getScheduler();

    int getState();
}
