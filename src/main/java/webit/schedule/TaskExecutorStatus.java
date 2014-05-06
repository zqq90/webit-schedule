// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.schedule;

/**
 *
 * @author zqq90
 */
public enum TaskExecutorStatus {

    STOPPED(1),
    PAUSED(2),
    STOPPING(3),
    PAUSING(4),
    RUNNING(5),
    HOLDDING(6);
    
    public final int value;

    private TaskExecutorStatus(int value) {
        this.value = value;
    }
    
}
