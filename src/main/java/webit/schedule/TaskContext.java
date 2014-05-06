// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule;

/**
 *
 * @author zqq90
 */
public interface TaskContext {

    public void pauseIfRequested();

    public boolean isRequestedStop();

    public boolean isRequestedPause();
    
    public boolean isRequestedStopOrPause();

    public Scheduler getScheduler();

    public Time getTime();
}
