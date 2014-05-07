// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.ext.workdays;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import webit.schedule.util.TimeUtil;

/**
 * Generate a default calendar to a file or <code>Writer</code>.
 *
 * @author Zqq
 */
public class WorkdayConfigGenerator {

    private final static char BLANK = ' ';
    private final static char NEW_LINE = '\n';
    private final static char[] WEEK_TITLE = ("Mon  Tue  Wed  Thu  Fri  Sat  Sun  " + NEW_LINE).toCharArray();
    private final static int[] INSERT_POINTS = {0, 5, 10, 15, 20, 25, 30}; //0-6 : i = weekday - 1
    private final static char[] WEEK_DAYS_TMPL;

    private final static String[] MONTH_NAME = {
        null,
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    };

    static {
        int len = WEEK_TITLE.length;
        Arrays.fill(WEEK_DAYS_TMPL = new char[len], BLANK);
        WEEK_DAYS_TMPL[len - 1] = NEW_LINE;
    }

    /**
     * Render to <code>Writer</code>.
     *
     * @param year
     * @param writer
     * @throws IOException
     */
    public static void renderTo(int year, Writer writer) throws IOException {
        new WorkdayConfigGenerator(year).renderTo(writer);
    }

    /**
     * Render to file with file path.
     *
     * @param year
     * @param filepath
     * @throws IOException
     */
    public static void renderToFile(int year, String filepath) throws IOException {
        renderToFile(year, new File(filepath));
    }

    /**
     * Render to <code>File</code>.
     *
     * @param year
     * @param file
     * @throws IOException
     */
    public static void renderToFile(int year, File file) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, false));
            renderTo(year, writer);
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    private Writer writer;
    private final int year;
    private final char[] weekDaysBuffer;

    /**
     * Create with year.
     *
     * @param year
     */
    public WorkdayConfigGenerator(int year) {
        this.year = year;
        weekDaysBuffer = new char[WEEK_DAYS_TMPL.length];
    }

    /**
     * Render to <code>Writer</code>.
     *
     * @param writer
     * @throws IOException
     */
    public void renderTo(Writer writer) throws IOException {
        this.writer = writer;
        writeFileHeader();
        writeMonths();
        writeFileFooter();
    }

    /**
     * Reset <code>this.weekDaysBuffer</code>.
     */
    protected void resetWeekDaysBuffer() {
        System.arraycopy(WEEK_DAYS_TMPL, 0, weekDaysBuffer, 0, weekDaysBuffer.length);
    }

    /**
     * Write file header.
     *
     * @throws IOException
     */
    protected void writeFileHeader() throws IOException {
        writer.append(NEW_LINE)
                .append("###################################").append(NEW_LINE)
                .append("# Workdays - for webit schedule").append(NEW_LINE)
                .append("# Year ").append(Integer.toString(year)).append(NEW_LINE)
                .append("# Last Modify: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append(NEW_LINE)
                .append("###################################").append(NEW_LINE);
    }

    /**
     * Write file footer.
     *
     * @throws IOException
     */
    protected void writeFileFooter() throws IOException {
        this.writer.append(NEW_LINE);
    }

    /**
     * Write months。
     *
     * @throws IOException
     */
    protected void writeMonths() throws IOException {
        for (int i = 1; i < 13; i++) {
            writeMonth(i);
        }
    }

    /**
     * Write month.
     *
     * @param month
     * @throws IOException
     */
    protected void writeMonth(int month) throws IOException {
        writeMonthHeader(month);
        writeMonthDeclear(month);
        writeWeekTitle();
        writeMonthDays(month);
        writeMonthFooter(month);
    }

    /**
     * Write month declaration.
     *
     * @param month
     * @throws IOException
     */
    protected void writeMonthDeclear(int month) throws IOException {
        final Writer myWriter = writer;
        myWriter.append('{');
        if (month < 10) {
            myWriter.append((char) ('0' + month));
        } else {
            int tenNumber = month / 10;
            myWriter.append((char) ('0' + tenNumber))
                    .append((char) ('0' + (month - tenNumber * 10)));
        }
        myWriter.append('}').append(BLANK)
                .append('#').append(MONTH_NAME[month])
                .append(NEW_LINE);
    }

    /**
     * Write month footer.
     *
     * @param month
     * @throws IOException
     */
    protected void writeMonthHeader(int month) throws IOException {
        this.writer.append(NEW_LINE);
    }

    /**
     * Write month footer.
     *
     * @param month
     * @throws IOException
     */
    protected void writeMonthFooter(int month) throws IOException {

    }

    /**
     * Write month title.
     *
     * @throws IOException
     */
    protected void writeWeekTitle() throws IOException {
        this.writer.write(WEEK_TITLE);
    }

    /**
     * Write dates of this month
     *
     * @param month
     * @throws IOException
     */
    protected void writeMonthDays(int month) throws IOException {
        final Writer myWriter = this.writer;
        final char[] buffer = weekDaysBuffer;
        final int[] insertPoint = INSERT_POINTS;
        final int startWeekday = TimeUtil.dayOfWeek(year, month, 1);
        final int dayMax = TimeUtil.getMonthLength(year, month);
        resetWeekDaysBuffer();
        for (int day = 1; day <= dayMax; day++) {
            int index = (day + startWeekday - 2) % 7; // == weekday - 1
            appendMonthDay(buffer, day,
                    insertPoint[index],
                    isFreeday(month, day, index + 1));
            if (index == 6
                    || day == dayMax) { // sunday or last day of month
                myWriter.write(buffer);
                if (day != dayMax) {
                    resetWeekDaysBuffer();
                }
            }
        }
    }

    /**
     * if is a free day, opposite to workday.
     *
     * @param month
     * @param day
     * @param weekday
     * @return
     */
    protected boolean isFreeday(int month, int day, int weekday) {
        return weekday == 6
                || weekday == 7;
    }

    /**
     * Append one day to buffer.
     *
     * @param buffer
     * @param day
     * @param start
     * @param freeday
     */
    protected void appendMonthDay(final char[] buffer, int day, int start, boolean freeday) {
        if (freeday) {
            buffer[start++] = '[';
        } else {
            start++; //buffer[start++] = BLANK;
        }
        if (day < 10) {
            start++; //buffer[start++] = BLANK;
            buffer[start++] = (char) (day + '0');
        } else {
            int tenNumber = day / 10;
            buffer[start++] = (char) (tenNumber + '0');
            buffer[start++] = (char) ((day - tenNumber * 10) + '0');
        }
        if (freeday) {
            buffer[start++] = ']';
        } else {
            //start++; //buffer[start++] = BLANK;
        }
    }

}
