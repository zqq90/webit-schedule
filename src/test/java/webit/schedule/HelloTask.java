// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.schedule;

/**
 *
 * @author Zqq
 */
public class HelloTask implements Task{

    @Override
    public String getTaskName() {
        return "Hi, I'm HelloTask";
    }

    @Override
    public void execute(TaskContext context) {
        System.out.println("Hello Task!");
    }
}
