// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule;

/**
 *
 * @author zqq90
 */
public interface TaskExecutor {

    void runIfNot(Time time);

    void stopAndWait();

    void goonIfPaused();

    void askforPause();

    void askforStop();

    Task getTask();

    Scheduler getScheduler();

    TaskExecutorStatus getState();
}
