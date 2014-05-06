// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.impl;

import webit.schedule.MatchableTask;
import webit.schedule.Scheduler;
import webit.schedule.Task;
import webit.schedule.TaskExecutor;
import webit.schedule.TaskExecutorFactory;

/**
 *
 * @author Zqq
 */
public class DefaultTaskExecutorFactory implements TaskExecutorFactory {

    private Scheduler scheduler;

    public TaskExecutor createTaskExecutor(Task task) {
        return task instanceof MatchableTask
                ? new DefaultMatchableTaskExecutor((MatchableTask) task, scheduler)
                : new DefaultTaskExecutor(task, scheduler);
    }

    public void init(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
