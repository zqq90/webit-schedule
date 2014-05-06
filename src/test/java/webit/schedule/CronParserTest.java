// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule;

import webit.schedule.core.Matcher;
import webit.schedule.core.CronParser;
import org.junit.Assert;
import org.junit.Test;
import webit.schedule.core.matcher.MinuteMatcher;

/**
 *
 * @author zqq90
 */
public class CronParserTest {

    @Test
    public void test() {
        Matcher matcher;

        //matcher = new CronParser("* * * * *").parse();
        //matcher = new CronParser("* * *").parse();
        //matcher = new CronParser("").parse();
        matcher = new CronParser("1,3,4  5,6-8  8/3,0-10/2|12,34 * 2,111,33").parse();

        matcher = new CronParser("8-10/3,5 1").parse();

        int i = 0;
    }

    @Test
    public void testMinute() {
        Matcher matcher;

        //ALL
        Assert.assertSame(Matcher.MATCH_ALL, new CronParser((String) null).parse());
        Assert.assertSame(Matcher.MATCH_ALL, new CronParser("* *").parse());
        Assert.assertSame(Matcher.MATCH_ALL, new CronParser("* * *").parse());
        Assert.assertSame(Matcher.MATCH_ALL, new CronParser("* * * * * * * * *").parse());

        //range
        matcher = new CronParser("0-100 * * * *").parse();
        Assert.assertTrue(matcher instanceof MinuteMatcher);
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 0, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 1, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 2, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 3, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 4, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 59, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 60, 1, true)));

        // div
        matcher = new CronParser("*/2 * * * *").parse();

        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 0, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 1, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 2, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 100, 1, true)));

        //range div & list
        matcher = new CronParser("54,1-4/2,55,100 * * * *").parse();

        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 0, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 1, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 2, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 3, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 4, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 55, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 59, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 60, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 100, 1, true)));
        
        //range div & list 2
        matcher = new CronParser("0-59/3,5,8 * * *").parse();

        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 0, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 1, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 2, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 3, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 4, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 5, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 6, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 7, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 8, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 59, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 60, 1, true)));

        
        //range div 2
        matcher = new CronParser("3/2 * * * *").parse();
        
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 0, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 1, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 2, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 3, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 4, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 59, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 60, 1, true)));
        
    }

    @Test
    public void testHour() {
        Matcher matcher;

        matcher = new CronParser("3,5,8 1,2,3 * *").parse();

        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 0, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 1, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 2, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 3, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 4, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 5, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 1, 8, 1, true)));

        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 2, 3, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 1, 3, 3, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 4, 3, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 24, 3, 1, true)));

    }

    @Test
    public void testDay() {
        Matcher matcher;

        matcher = new CronParser("* * 2,4,*/3 *").parse();

        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 0, 1, 1, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 1, 1, 1, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 2, 1, 1, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 3, 1, 1, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 4, 1, 1, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 1, 1, 5, 1, 1, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 1, 1, 6, 1, 1, 1, true)));
    }

    @Test
    public void testMouth() {

        Matcher matcher;

        matcher = new CronParser("* * * */2 * *").parse();

        Assert.assertFalse(matcher.match(new Time(1, 2014, 1, 1, 1, 1, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 2014, 2, 1, 1, 1, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 2014, 3, 1, 1, 1, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 2014, 4, 1, 1, 1, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 2014, 5, 1, 1, 1, 1, true)));
    }

    @Test
    public void testYear() {

        Matcher matcher;

        matcher = new CronParser("* * * * */2 *").parse();

        Assert.assertTrue(matcher.match(new Time(1, 2010, 1, 1, 1, 1, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 2011, 1, 1, 1, 1, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 2012, 1, 1, 1, 1, 1, true)));
        Assert.assertFalse(matcher.match(new Time(1, 2013, 1, 1, 1, 1, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 2014, 1, 1, 1, 1, 1, true)));
    }

    @Test
    public void testWeek() {

        Matcher matcher;

        matcher = new CronParser("* * * * * */2").parse();
        
        Assert.assertFalse(matcher.match(new Time(1, 2014, 1, 1, 1, 1, 1, true)));
        Assert.assertTrue(matcher.match(new Time(1, 2014, 1, 1, 1, 1, 2, true)));
        Assert.assertFalse(matcher.match(new Time(1, 2014, 1, 1, 1, 1, 3, true)));
        Assert.assertTrue(matcher.match(new Time(1, 2014, 1, 1, 1, 1, 4, true)));
        Assert.assertFalse(matcher.match(new Time(1, 2014, 1, 1, 1, 1, 5, true)));
    }

}
