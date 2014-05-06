// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.schedule.impl;

import webit.schedule.MatchableTask;
import webit.schedule.Scheduler;
import webit.schedule.Time;

/**
 *
 * @author zqq90
 */
public class DefaultMatchableTaskExecutor extends DefaultTaskExecutor {

    protected final MatchableTask matchableTask;

    public DefaultMatchableTaskExecutor(MatchableTask task, Scheduler scheduler) {
        super(task, scheduler);
        this.matchableTask = task;
    }

    @Override
    public void runIfNot(Time time) {
        if (matchableTask.match(time)) {
            super.runIfNot(time);
        }
    }
}
