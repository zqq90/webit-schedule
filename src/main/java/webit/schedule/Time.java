// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule;

import java.util.TimeZone;
import webit.schedule.util.TimeUtil;

/**
 *
 * @author zqq90
 */
public final class Time {

    /**
     * millisecond.
     */
    public final long millisecond;
    /**
     * Year.
     */
    public final int year;
    /**
     * Month, range: [1 - 12].
     */
    public final int month;
    /**
     * Day, range: [1 - 31].
     */
    public final int day;
    /**
     * Hour, range: [0 - 23].
     */
    public final int hour;
    /**
     * Minute, range [0 - 59].
     */
    public final int minute;
    /**
     * Day of week, range: [1-7], 1 (Monday),, 6(SATURDAY), 7 (Sunday).
     */
    public final int dayOfWeek;
    /**
     * Leap year flag.
     */
    public final boolean leap;
    /**
     * TimeZone offset.
     */
    public final int offset;

    /**
     * Create Time with default TimeZone offset.
     *
     * @param millis
     */
    public Time(long millis) {
        this(millis, TimeZone.getDefault().getRawOffset());
    }

    /**
     * For test.
     *
     * @param millisecond
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param dayofweek
     * @param leap
     * @Deprecated
     */
    @Deprecated
    public Time(long millisecond, int year, int month, int day, int hour, int minute, int dayofweek, boolean leap) {
        this.millisecond = millisecond;
        this.year = year;
        this.month = month > 0 && month <= 12 ? month : month % 12 + 1;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.dayOfWeek = dayofweek;
        this.leap = leap;
        this.offset = 0;
    }

    /**
     * Create Time.
     *
     * @param millisecond
     * @param offset TimeZone offset
     */
    public Time(long millisecond, int offset) {
        this.millisecond = millisecond;
        this.offset = offset;

        millisecond += offset; // plus offset

        final int integer = (int) (millisecond / (1000L * 60 * 60 * 24)) + 2440587;
        final double fraction = (double) (millisecond % (1000L * 60 * 60 * 24)) / (1000L * 60 * 60 * 24) + 0.5 + 0.5;

        //dayofweek
        this.dayOfWeek = ((int) ((double) integer + fraction) % 7) + 1; //  1 (Monday),... 7 (Sunday),

        //
        int year, month, day;
        double frac;
        int jd, ka, kb, kc, kd, ke, ialp;

        //double JD = jds.doubleValue();//jdate;
        //jd = (int)(JD + 0.5);							// integer julian date
        //frac = JD + 0.5 - (double)jd + 1.0e-10;		// day fraction
        ka = (int) (fraction);
        jd = integer + ka;
        frac = fraction - ka + 1.0e-10;

        ka = jd;
        if (jd >= 2299161) {
            ialp = (int) (((double) jd - 1867216.25) / (36524.25));
            ka = jd + 1 + ialp - (ialp >> 2);
        }
        kb = ka + 1524;
        kc = (int) (((double) kb - 122.1) / 365.25);
        kd = (int) ((double) kc * 365.25);
        ke = (int) ((double) (kb - kd) / 30.6001);
        day = kb - kd - ((int) ((double) ke * 30.6001));
        if (ke > 13) {
            month = ke - 13;
        } else {
            month = ke - 1;
        }
        if ((month == 2) && (day > 28)) {
            day = 29;
        }
        if ((month == 2) && (day == 29) && (ke == 3)) {
            year = kc - 4716;
        } else if (month > 2) {
            year = kc - 4716;
        } else {
            year = kc - 4715;
        }
        this.year = year;
        this.month = month;
        this.day = day;

        // hour with minute and second included as fraction
        double d_hour = frac * 24.0;
        this.hour = (int) d_hour;				// integer hour

        // minute with second included as a fraction
        double d_minute = (d_hour - (double) this.hour) * 60.0;
        this.minute = (int) d_minute;			// integer minute

        //leap
        this.leap = TimeUtil.isLeapYear(year);
    }

    /**
     * total days of this Month.
     *
     * @return int
     */
    public int getTotalDaysOfThisMonth() {
        if (this.month == 2 && this.leap) {
            return 29;
        }
        return TimeUtil.getMonthLengthOfCommonYear(this.month);
    }

    /**
     * if is weekend. 7 (Sunday) or 6(SATURDAY)
     *
     * @return boolean
     */
    public boolean isWeekend() {
        return this.dayOfWeek > 5;
    }

    /**
     * if is weekday. not 7 (Sunday) or 6(SATURDAY)
     *
     * @return boolean
     */
    public boolean isWeekday() {
        return this.dayOfWeek < 6;
    }

    /**
     * if is last day of this month.
     *
     * @return boolean
     */
    public boolean isLastDayOfThisMonth() {
        return this.day == getTotalDaysOfThisMonth();
    }

    /**
     * if is last weekday of this month.
     *
     * @return boolean
     */
    public boolean isLastWeekdayOfThisMonth() {
        final int lastdays = getTotalDaysOfThisMonth() - this.day;
        final int off = this.dayOfWeek - 5;
        if (off < 0) {
            // 1-4
            return lastdays == 0;
        }
        if (off == 0) {
            // 5
            return lastdays <= 2;
        }
        // 6-7
        return false;
    }

}
