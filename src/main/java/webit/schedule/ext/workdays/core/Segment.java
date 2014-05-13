// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.ext.workdays.core;

import webit.schedule.util.CharUtil;

/**
 *
 * @author Zqq
 */
public class Segment {

    final char[] buffer;
    final int line;
    int pos;
    int end;

    public Segment(char[] buffer, int line, int pos, int end) {
        this.buffer = buffer;
        this.line = line;
        this.pos = pos;
        this.end = end;
    }

    public char next() {
        if (pos < end) {
            return buffer[pos++];
        } else {
            throw createException("Expect more char(s)");
        }
    }

    public void checkBlanks(int from, int to) {
        if (to > end) {
            to = end;
        }
        for (; from < to; from++) {
            if (CharUtil.isBlank(buffer[from]) == false) {
                throw createException("Expect blank char, but '" + buffer[from] + '\'');
            }
        }
    }

    public void checkBlanksToEnd(int from) {
        checkBlanks(from, end);
    }

    public void checkBlanksTo(int to) {
        checkBlanks(pos, to);
    }

    public void checkBlanks() {
        checkBlanks(pos, end);
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getEnd() {
        return end;
    }

    public void checkCharWithBlanks(char c) {
        skipBlanks();
        if (pos < end) {
            char real = buffer[pos++];
            if (real != c) {
                throw createException("Expect char '" + c + "\', but '" + real + '\'');
            }
            checkBlanks();
        } else {
            throw createException("Expect char '" + c + '\'');
        }
    }

    public boolean hasNext() {
        return pos < end;
    }

    public void resetPos(int pos) {
        this.pos = pos;
    }

    public boolean advanceNext(char c) {
        return buffer[pos] == c;
    }

    public int getPrePos() {
        return pos - 1;
    }

    public int getNextPos() {
        return pos;
    }

    public void skipBlanks() {
        while (pos < end && CharUtil.isBlank(buffer[pos])) {
            pos++;
        }
    }

    public void skipNotBlanks() {
        while (pos < end && (CharUtil.isBlank(buffer[pos]) == false)) {
            pos++;
        }
    }

    private RuntimeException createException(String message) {
        return new RuntimeException(message + ", at line " + line + ", column " + pos);
    }

    public int remainLength() {
        return end - pos;
    }

    public int remainLength(int to) {
        if (to <= end) {
            return to - pos;
        } else {
            return end - pos;
        }
    }

    public int indexOf(char c) {
        return indexOf(pos, end, c);
    }

    public int indexOfFrom(int from, char c) {
        return indexOf(from, end, c);
    }

    public int indexOfTo(int to, char c) {
        return indexOf(pos, to, c);
    }
    
    public int indexOf(int from, int to, char c) {
        while (from < to) {
            if (buffer[from] == c) {
                return from;
            }
            from++;
        }
        return -1;
    }

    public static boolean isBlank(char c) {
        return c == ' ';
        //|| c == '\t'; Note: Not support
    }
}
