// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule;

/**
 *
 * @author zqq90
 */
public interface Task {

    String getTaskName();

    void execute(TaskContext context);
}
