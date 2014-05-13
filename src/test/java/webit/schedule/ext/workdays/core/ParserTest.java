// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.ext.workdays.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Zqq
 */
public class ParserTest {

    @Test
    public void test() throws IOException {
        String path = "src/main/resources/ParserTest.txt";

        InputStreamReader reader = new InputStreamReader(new FileInputStream(path), "utf-8");

        YearEntry yearEntry = new Parser().parse(2014, reader);

        assertEquals("工作日", yearEntry.getMessage(1, 1));
        assertEquals("工作日", yearEntry.getMessage(1, 2));
        assertEquals("工作日", yearEntry.getMessage(1, 3));
        assertEquals("周末", yearEntry.getMessage(1, 4));
        assertEquals("周末", yearEntry.getMessage(1, 5));
        assertEquals("工作日", yearEntry.getMessage(1, 6));
        assertEquals("工作日", yearEntry.getMessage(1, 7));
        assertEquals("工作日", yearEntry.getMessage(1, 8));
        assertEquals("工作日", yearEntry.getMessage(1, 9));
        assertEquals("工作日", yearEntry.getMessage(1, 10));
        assertEquals(null, yearEntry.getMessage(1, 11));

        assertEquals("周末", yearEntry.getMessage(1, 25));
        assertEquals("周末", yearEntry.getMessage(1, 26));

        assertTrue(yearEntry.isWorkday(1, 1));
        assertTrue(yearEntry.isWorkday(1, 2));
        assertTrue(yearEntry.isWorkday(1, 3));

        assertFalse(yearEntry.isWorkday(1, 4));
        assertFalse(yearEntry.isWorkday(1, 5));

        assertTrue(yearEntry.isWorkday(1, 6));
        assertTrue(yearEntry.isWorkday(1, 7));
        assertTrue(yearEntry.isWorkday(1, 8));
        assertTrue(yearEntry.isWorkday(1, 9));
        assertTrue(yearEntry.isWorkday(1, 10));

        assertFalse(yearEntry.isWorkday(1, 11));

        assertFalse(yearEntry.isWorkday(1, 25));
        assertFalse(yearEntry.isWorkday(1, 26));

        //10-1
        assertEquals("国庆节", yearEntry.getMessage(10, 1));
        assertFalse(yearEntry.isWorkday(10, 1));

        reader.close();
    }
}
