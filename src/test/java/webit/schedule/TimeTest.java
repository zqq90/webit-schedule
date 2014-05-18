// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule;

import java.util.Calendar;
import java.util.TimeZone;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author zqq90
 */
public class TimeTest {

    @Test
    public void test() {
        long millis = System.currentTimeMillis();
        int offset = TimeZone.getDefault().getRawOffset();

        Time time = new Time(millis + offset);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        assertEquals(time.year, calendar.get(Calendar.YEAR));
        assertEquals(time.month, calendar.get(Calendar.MONTH) + 1); //[1-12] JANUARY - DECEMBER
        assertEquals(time.day, calendar.get(Calendar.DAY_OF_MONTH)); //[1-31]
        assertEquals(time.hour, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(time.minute, calendar.get(Calendar.MINUTE));
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        assertEquals(time.dayofweek, dayOfWeek); //  1 (Monday), ... 6(SATURDAY), 7(Sunday),
    }
}
