// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.ext.workdays.core;

import webit.schedule.Time;
import webit.schedule.util.TimeUtil;

/**
 *
 * @author Zqq
 */
public class YearEntry {

    //treat as leap year
    private static final int[] MONTH_DAYS_OF_YEAR = new int[]{
        -1,
        -1, //+31,
        30, //+29,
        59, //+31,
        90, //+30,
        120, //+31,
        151, //+30,
        181, //+31,
        212, //+31,
        243, //+30,
        273, //+31,
        304, //+30,
        334//, //+31
    //365
    };

    public static int getIndexOfDate(int month, int day) {
        //assert 1 <= month <= 12
        return MONTH_DAYS_OF_YEAR[month] + day;
    }

    public static int getIndexOfDate(Time time) {
        return getIndexOfDate(time.month, time.day);
    }

    public final int year;
    public final boolean isLeapYear;
    protected final boolean[] datas;
    protected final String[] messages;

    public YearEntry(int year) {
        this(year, createDatasArray(), new String[366]);
    }

    private static boolean[] createDatasArray() {
        boolean[] arr = new boolean[366];
        //for (int i = 0; i < arr.length; i++) {
        //    arr[i] = false;
        //}
        return arr;
    }

    YearEntry(int year, boolean[] datas, String[] messages) {
        this.year = year;
        this.isLeapYear = TimeUtil.isLeapYear(year);
        this.datas = datas;
        this.messages = messages;
    }

    //--------------->
    public boolean isWorkday(int month, int day) {
        return this.datas[getIndexOfDate(month, day)];
    }

    public boolean isWorkday(Time time) {
        return isWorkday(time.month, time.day);
    }

    public String getMessage(int month, int day) {
        return this.messages[getIndexOfDate(month, day)];
    }

    public String getMessage(Time time) {
        return getMessage(time.month, time.day);
    }

    //--------------->
    public void setWorkday(int month, int day, boolean workday) {
        this.datas[getIndexOfDate(month, day)] = workday;
    }

    public void setWorkday(int month, int day) {
        setWorkday(month, day, true);
    }

    public void setNotWorkday(int month, int day) {
        setWorkday(month, day, false);
    }

    public void setMessage(int month, int day, String message) {
        this.messages[getIndexOfDate(month, day)] = message;
    }

    //--------------->
    public int getMonthLength(final int month) {
        if (month != 2 || !this.isLeapYear) {
            return TimeUtil.getMonthLengthOfCommonYear(month);
        } else {
            return 29;
        }
    }

    /**
     * Day of week. Mon Tue ... Sun : 1 2 ... 7
     *
     * @param month
     * @param day
     * @return
     */
    public int dayOfWeek(int month, int day) {
        return TimeUtil.dayOfWeek(year, month, day);
    }
}
