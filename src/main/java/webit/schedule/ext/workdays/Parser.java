// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.ext.workdays;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

/**
 * Calendar parser.
 *
 * @author Zqq
 */
class Parser {

    private final static int STATE_INIT = 1; //init
    private final static int STATE_MONTH_START = 2; //after month declare
    private final static int STATE_WEEK_HEAD = 3; //after week head declare
    private final static int STATE_DATES = 4; //stated date block
    private final static int STATE_MESSAGE = 5; //stated message

    private final int[] currentWeekdayIndexer = new int[7];
    private final boolean[] monthFlag = new boolean[13];
    private int currentMonth;
    private int lineNumber;
    private int currentDay;
    private int state;

    private YearEntry currentYearEntry;

    /**
     * Create a Parser.
     */
    Parser() {
        reset();
    }

    private void reset() {
        state = STATE_INIT;
        lineNumber = 0;
        currentMonth = 0;
        currentDay = 0;
        currentYearEntry = null;
        Arrays.fill(this.monthFlag, false);
    }

    /**
     * Parse from reader.
     *
     * @param year
     * @param reader
     * @return
     * @throws IOException
     */
    YearEntry parse(int year, Reader reader) throws IOException {
        final YearEntry entry = currentYearEntry = new YearEntry(year);
        try {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            char[] buffer = new char[64];
            while ((line = bufferedReader.readLine()) != null) {
                lineNumber++;
                if (line.isEmpty()) {
                    continue;
                }
                int end = line.indexOf('#');
                if (end == 0) {
                    continue;
                }
                if (end < 0) {
                    end = line.length();
                }
                if (buffer.length < end) {
                    buffer = new char[end];
                }
                line.getChars(0, end, buffer, 0);
                parseLine(new Segment(buffer, lineNumber, 0, end));
            }
            reader.close();
            checkFinishedCurrentMonth();
            checkFinishedAllMonths();
        } finally {
            reset();
        }
        return entry;
    }

    /**
     * parse one line.
     *
     * @param segment
     */
    private void parseLine(final Segment segment) {
        final int start = segment.getNextPos();
        final int currentState = state;
        segment.skipBlanks();
        if (segment.hasNext() == false) {
            //skip blank lines
            return;
        }
        switch (segment.next()) {
            case '{': //month declare
                if (currentState != STATE_INIT
                        && currentState != STATE_DATES
                        && currentState != STATE_MESSAGE) {
                    throw createException("Unexpect state to declare month");
                }
                parseMonth(segment);
                break;
            case '/': //month declare
                if (currentState != STATE_DATES
                        && currentState != STATE_MESSAGE) {
                    throw createException("Unexpect position to write message", segment);
                }
                if (segment.next() != '/') {
                    throw createException("Unexpect message start, please start message with '//'", segment);
                }
                parseMessage(segment);
                break;
            default:
                if (currentState == STATE_MONTH_START) {
                    segment.resetPos(start);
                    parseWeekTitle(segment);
                } else if (currentState == STATE_WEEK_HEAD
                        || currentState == STATE_DATES) {
                    segment.resetPos(start);
                    parseDates(segment);
                } else {
                    throw createException("Unexpect line start", segment);
                }
        }
    }

    /**
     * Check if finished all month.
     */
    private void checkFinishedAllMonths() {
        if (state != STATE_MESSAGE
                && state != STATE_DATES) {
            throw createException("Wrong file end");
        }
        final boolean[] flag = this.monthFlag;
        for (int i = 1; i < 13; i++) {
            if (flag[i] == false) {
                throw createException("Not found configs for month: " + i);
            }
        }
    }

    /**
     * Check if finished current month.
     */
    private void checkFinishedCurrentMonth() {
        if (state != STATE_DATES) {
            return;
        }
        int month = currentMonth;
        if (month > 0) {
            if (currentYearEntry.getMonthLength(month) > currentDay) {
                throw createException("Not finish month: " + month);
            }
        }
    }

    /**
     * Parse month. like: 5}
     */
    private void parseMonth(final Segment segment) {
        checkFinishedCurrentMonth();
        segment.skipBlanks();
        final int mouth;
        final char c = segment.next();
        if (c == '1') {
            char next = segment.next();
            switch (next) {
                case ' ':
                    mouth = 1;
                    segment.checkCharWithBlanks('}');
                    break;
                case '}':
                    mouth = 1;
                    segment.checkBlanks();
                    break;
                case '0':
                case '1':
                case '2':
                    mouth = 10 + next - '0';
                    segment.checkCharWithBlanks('}');
                    break;
                default:
                    if (next >= '3' && next <= '9') {
                        throw createException("Unexpect mouth " + (10 + next - '0'), segment);
                    }
                    throw createUnexpectCharException(next, segment);
            }
        } else if (c >= '2' && c <= '9') {
            mouth = c - '0';
            segment.checkCharWithBlanks('}');
        } else {
            throw createException("Wrong month, need a number", segment);
        }
        if (this.monthFlag[mouth]) {
            throw createException("Month has bean decleared " + mouth);
        }
        this.monthFlag[mouth] = true;
        this.currentMonth = mouth;
        this.currentDay = 0;
        this.state = STATE_MONTH_START;
    }

    /**
     * Parse indexer from week title. like: Mon Tue Wed Thu Fri Sat Sun
     */
    private void parseWeekTitle(final Segment segment) {
        final int[] weekdayIndexer = this.currentWeekdayIndexer;
        for (int i = 0; i < 7; i++) {
            segment.skipBlanks();
            weekdayIndexer[i] = segment.getNextPos();
            segment.skipUnblanks();
        }
        segment.checkBlanks();
        this.state = STATE_WEEK_HEAD;
    }

    /**
     * Parse days of one week. like: 1 [2] [3]
     */
    private void parseDates(final Segment segment) {
        final YearEntry yearEntry = this.currentYearEntry;
        final int[] weekdayIndexer = this.currentWeekdayIndexer;
        final int month = this.currentMonth;
        final int lastDay = this.currentDay;
        final int dayMax = yearEntry.getMonthLength(month);
        final int startWeekday;
        final int endWeekday;
        if (lastDay == 0) {
            //first week
            startWeekday = yearEntry.dayOfWeek(month, 1);
            endWeekday = 7;
            segment.checkBlanksTo(weekdayIndexer[startWeekday - 1]);
        } else {
            int remainDays = dayMax - lastDay;
            startWeekday = 1;
            if (remainDays <= 0) {
                throw createException("Month {" + month + "} only has " + dayMax + " days");
            }
            endWeekday = remainDays > 7 ? 7 : remainDays;
        }
        int daybase = lastDay - startWeekday + 1;
        for (int weekday = startWeekday; weekday <= endWeekday; weekday++) {
            final int currDay = daybase + weekday;
            final int start = weekdayIndexer[weekday - 1];
            final int end;
            if (weekday == 7) {
                end = segment.getEnd();
            } else {
                end = weekdayIndexer[weekday];
            }
            segment.resetPos(start);

            segment.skipBlanks();
            if (segment.getNextPos() >= end) {
                throw createMissDayOfMonthDayException(month, currDay, segment);
            }
            boolean isFreeDay = segment.advanceNext('[');
            if (isFreeDay) {
                segment.next(); //skip [
                segment.skipBlanks();
                if (segment.getNextPos() >= end) {
                    throw createMissDayOfMonthDayException(month, currDay, segment);
                }
            }
            //resure day
            int remainChar = segment.remainLength(end);
            if (currDay >= 10) {
                if (remainChar < 2) {
                    throw createMissDayOfMonthDayException(month, currDay, segment);
                }
                int tenNumber = currDay / 10;
                if (segment.next() != (tenNumber + '0')) {
                    throw createUnexpectDayException(currDay, segment);
                }
                if (segment.next() != (currDay - tenNumber * 10 + '0')) {
                    throw createUnexpectDayException(currDay, segment);
                }
            } else {
                if (remainChar < 1) {
                    throw createMissDayOfMonthDayException(month, currDay, segment);
                }
                if (segment.next() != (currDay + '0')) {
                    throw createUnexpectDayException(currDay, segment);
                }
            }
            // if with ] ?
            segment.skipBlanks();
            final boolean isFreeDayClosed;
            remainChar = segment.remainLength(end);
            if (remainChar > 0) {
                char next = segment.next();
                if (next == ']') {
                    isFreeDayClosed = true;
                } else {
                    throw createUnexpectCharException(next, segment);
                }
                //resure no other chars
                segment.checkBlanksTo(end);
            } else {
                isFreeDayClosed = false;
            }
            if (isFreeDay) {
                if (isFreeDayClosed == false) {
                    throw createException("Not support set half free day now", segment);
                }
            } else {
                yearEntry.setWorkday(month, currDay);
            }
        }

        if (startWeekday == 1 && endWeekday < 7) {
            segment.checkBlanksToEnd(weekdayIndexer[endWeekday]);
        }

        //plus days
        this.currentDay = daybase + endWeekday;
        this.state = STATE_DATES;
    }

    /**
     * Parse Messages. like: 1-2,3-5,5,6,7 weekend
     */
    private void parseMessage(Segment segment) {
        checkFinishedCurrentMonth();
        segment.skipBlanks();

        int pos = segment.pos;
        int end = segment.end;
        int blankIndex = segment.indexOf(' ');
        if (blankIndex < 0 || blankIndex + 1 >= end) {
            return; //without message
        }
        String message = new String(segment.buffer, blankIndex + 1, end - blankIndex - 1).trim();
        //number list
        while (pos < blankIndex) {
            int nextComma = segment.indexOf(pos, blankIndex, ',');
            if (nextComma < 0) {
                nextComma = blankIndex;
            }
            parseNumberPeerForMessage(segment, pos, nextComma, message);
            pos = nextComma + 1;
        }
        this.state = STATE_MESSAGE;
    }

    /**
     * Parse number peer for message. like: 1-5 or 3
     *
     * @param buffer
     * @param offset
     * @param to
     * @param message
     */
    private void parseNumberPeerForMessage(final Segment segment, int form, final int to, String message) {
        int splitIndex = segment.indexOf(form, to, '-');
        if (splitIndex >= 0) {
            int dayFrom = parseNumber(segment.buffer, form, splitIndex);
            int dayTo = parseNumber(segment.buffer, splitIndex + 1, to);
            for (int day = dayFrom; day <= dayTo; day++) {
                setDayMessage(day, message);
            }
        } else {
            int day = parseNumber(segment.buffer, form, to);
            setDayMessage(day, message);
        }
    }

    private void setDayMessage(int day, String message) {
        if (day <= 0 || day > this.currentYearEntry.getMonthLength(this.currentMonth)) {
            throw createException("Month " + this.currentMonth + " don't have such day: " + day);
        }
        this.currentYearEntry.setMessage(this.currentMonth, day, message);
    }

    /**
     * Parse number form buffer.
     */
    private int parseNumber(final char[] buffer, int offset, final int to) {
        if (offset >= to) {
            throw createException("Need a number", offset);
        }
        int value = 0;
        char c;
        while (offset < to) {
            c = buffer[offset++];
            if (c >= '0' && c <= '9') {
                value = value * 10 + ((int) c - (int) '0');
            } else {
                throw createException("Invalid numberic char '" + c + '\'', offset);
            }
        }
        return value;
    }

    private RuntimeException createException(String message) {
        return new RuntimeException(message + ", at line " + lineNumber);
    }

    private RuntimeException createUnexpectCharException(char c, Segment segment) {
        return createException("Unexpect char '" + c + '\'', segment);
    }

    private RuntimeException createMissDayOfMonthDayException(int month, int day, Segment segment) {
        throw createException("Month " + month + " miss date " + day, segment);
    }

    private RuntimeException createUnexpectDayException(int day, Segment segment) {
        return createException("Unexpect day, should be " + day, segment);
    }

    private RuntimeException createException(String message, Segment segment) {
        return createException(message, segment.line, segment.getPrePos());
    }

    private RuntimeException createException(String message, int line, int column) {
        return new RuntimeException(message + ", at line " + line + ", column " + column + 1);
    }

    private RuntimeException createException(String message, int column) {
        return createException(message, lineNumber, column);
    }
}
