// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule;

/**
 *
 * @author zqq90
 */
public interface TaskExecutorFactory {

    TaskExecutor createTaskExecutor(Task task);
}
