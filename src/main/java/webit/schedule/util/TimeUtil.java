// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.util;

/**
 *
 * @author Zqq
 */
public class TimeUtil {

    private final static int[] DAYS_OF_MONTH = new int[]{
        0, //
        31, // 1
        28, // 2
        31, // 3
        30, // 4
        31, // 5
        30, // 6
        31, // 7
        31, // 8
        30, // 9
        31, // 10
        30, // 11
        31 // 12
    };

    public static int getMonthLength(final int year, final int month) {
        if (month != 2 || !isLeapYear(year)) {
            return DAYS_OF_MONTH[month];
        } else {
            return 29;
        }
    }

    public static int getMonthLengthOfCommonYear(final int month) {
        return DAYS_OF_MONTH[month];
    }

    public static boolean isLeapYear(final int year) {
        return ((year % 4) == 0) // must be divisible by 4...
                && ((year < 1582) // and either before reform year...
                || ((year % 100) != 0) // or not a century...
                || ((year % 400) == 0)); // or a multiple of 400...
    }

    /**
     * Day of week. Mon Tue ... Sun : 1 2 ... 7
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static int dayOfWeek(int year, int month, int day) {
        if (month == 1 || month == 2) {
            month += 12;
            year--;
        }
        return (day + 2 * month + 3 * (month + 1) / 5 + year + year / 4 - year / 100 + year / 400) % 7 + 1;
    }
}
