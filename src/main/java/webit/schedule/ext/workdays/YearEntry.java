// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.ext.workdays;

import webit.schedule.Time;
import webit.schedule.util.TimeUtil;

/**
 *
 * @author Zqq
 */
class YearEntry {

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

    final int year;
    final boolean isLeapYear;
    private final boolean[] datas;
    private final String[] messages;

    YearEntry(int year) {
        this(year, new boolean[366], new String[366]);
    }

    YearEntry(int year, boolean[] datas, String[] messages) {
        this.year = year;
        this.isLeapYear = TimeUtil.isLeapYear(year);
        this.datas = datas;
        this.messages = messages;
    }

    boolean isWorkday(int month, int day) {
        return this.datas[getIndexOfDate(month, day)];
    }

    boolean isWorkday(Time time) {
        return isWorkday(time.month, time.day);
    }

    String getMessage(int month, int day) {
        return this.messages[getIndexOfDate(month, day)];
    }

    String getMessage(Time time) {
        return getMessage(time.month, time.day);
    }

    void setWorkday(int month, int day, boolean workday) {
        this.datas[getIndexOfDate(month, day)] = workday;
    }

    void setWorkday(int month, int day) {
        setWorkday(month, day, true);
    }

    void setNotWorkday(int month, int day) {
        setWorkday(month, day, false);
    }

    void setMessage(int month, int day, String message) {
        this.messages[getIndexOfDate(month, day)] = message;
    }

    int getMonthLength(final int month) {
        if (month == 2 && this.isLeapYear) {
            return 29;
        }
        return TimeUtil.getMonthLengthOfCommonYear(month);
    }

    /**
     * Day of week. Mon Tue ... Sun : 1 2 ... 7
     *
     * @param month
     * @param day
     * @return
     */
    int dayOfWeek(int month, int day) {
        return TimeUtil.dayOfWeek(year, month, day);
    }

    private static int getIndexOfDate(int month, int day) {
        //assert 1 <= month <= 12
        return MONTH_DAYS_OF_YEAR[month] + day;
    }
}
