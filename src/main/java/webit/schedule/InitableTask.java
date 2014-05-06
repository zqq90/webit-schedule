// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.schedule;

/**
 *
 * @author zqq90
 */
public interface InitableTask extends Task {

    void init(TaskExecutor executor);
}
