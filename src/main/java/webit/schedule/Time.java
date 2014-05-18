// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule;

import webit.schedule.util.TimeUtil;

/**
 *
 * @author zqq90
 */
public final class Time {

    private static final long MILLIS_IN_DAY = 1000L * 60 * 60 * 24;
    private static final int JD_1970_integer = 2440587;
    private static final double JD_1970_fraction = 0.5;
    public final long millis;
    /**
     * Year.
     */
    public final int year;
    /**
     * Month, range: [1 - 12]
     */
    public final int month;
    /**
     * Day, range: [1 - 31]
     */
    public final int day;
    /**
     * Hour, range: [0 - 23]
     */
    public final int hour;
    /**
     * Minute, range [0 - 59]
     */
    public final int minute;
    /**
     * Day of week, range: [1-7], 1 (Monday), ... 6(SATURDAY), 7 (Sunday)
     */
    public final int dayofweek;
    /**
     * Leap year flag.
     */
    public final boolean leap;

    public Time(long millis) {
        this.millis = millis;

        final int integer = (int) (millis / MILLIS_IN_DAY) + JD_1970_integer;
        final double fraction = (double) (millis % MILLIS_IN_DAY) / MILLIS_IN_DAY + JD_1970_fraction + 0.5;

        //dayofweek
        this.dayofweek = ((int) ((double) integer + fraction) % 7) +1; //  1 (Monday),... 7 (Sunday),

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
     * total days of this Month
     *
     * @return int
     */
    public int getTotalDaysOfThisMonth() {
        if (this.month != 2 || !this.leap) {
            return TimeUtil.getMonthLengthOfCommonYear(this.month);
        } else {
            return 29;
        }
    }

    /**
     * if is weekend. 7 (Sunday) or 6(SATURDAY)
     *
     * @return boolean
     */
    public boolean isWeekend() {
        return this.dayofweek == 7
                || this.dayofweek == 6;
    }

    /**
     * if is weekday. not 7 (Sunday) or 6(SATURDAY)
     *
     * @return boolean
     */
    public boolean isWeekday() {
        return this.dayofweek != 7
                && this.dayofweek != 6;
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
        switch (this.dayofweek) {
            case 1:
            case 2:
            case 3:
            case 4:
                return lastdays == 0;
            case 5:
                return lastdays <= 2;
            default: // 7 or 6
                return false;
        }
    }

    /**
     * For test
     *
     * @param millis
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param dayofweek
     * @param leap
     */
    public Time(long millis, int year, int month, int day, int hour, int minute, int dayofweek, boolean leap) {
        this.millis = millis;
        this.year = year;
        this.month = month > 0 && month <= 12 ? month : month % 12 + 1;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.dayofweek = dayofweek;
        this.leap = leap;
    }
}
