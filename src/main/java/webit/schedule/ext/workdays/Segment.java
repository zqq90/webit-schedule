// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.ext.workdays;

/**
 *
 * @author Zqq
 */
class Segment {

    final char[] buffer;
    final int line;
    int pos;
    int end;

    Segment(char[] buffer, int line, int pos, int end) {
        this.buffer = buffer;
        this.line = line;
        this.pos = pos;
        this.end = end;
    }

    char next() {
        if (pos < end) {
            return buffer[pos++];
        }
        throw createException("Expect more char.");
    }

    void checkBlanks(int from, int to) {
        if (to > end) {
            to = end;
        }
        while (from < to) {
            char c = buffer[from];
            if (c != ' ' && c != '\t') {
                throw createException("Expect blanks, but meet '" + c + '\'');
            }
            from++;
        }
    }

    void checkBlanksToEnd(int from) {
        checkBlanks(from, end);
    }

    void checkBlanksTo(int to) {
        checkBlanks(pos, to);
    }

    void checkBlanks() {
        checkBlanks(pos, end);
    }

    void setPos(int pos) {
        this.pos = pos;
    }

    void setEnd(int end) {
        this.end = end;
    }

    int getEnd() {
        return end;
    }

    void checkCharWithBlanks(char c) {
        skipBlanks();
        if (pos < end) {
            char real = buffer[pos++];
            if (real != c) {
                throw createException("Expect char '" + c + "\', but meet '" + real + '\'');
            }
            checkBlanks();
        } else {
            throw createException("Expect char '" + c + '\'');
        }
    }

    boolean hasNext() {
        return pos < end;
    }

    void resetPos(int pos) {
        this.pos = pos;
    }

    boolean advanceNext(char c) {
        return buffer[pos] == c;
    }

    int getPrePos() {
        return pos - 1;
    }

    int getNextPos() {
        return pos;
    }

    void skipBlanks() {
        while (pos < end) {
            char c = buffer[pos];
            if (c != ' ' && c != '\t') {
                return;
            }
            pos++;
        }
    }

    void skipUnblanks() {
        while (pos < end) {
            char c = buffer[pos];
            if (c == ' ' || c == '\t') {
                return;
            }
            pos++;
        }
    }

    int remainLength() {
        return end - pos;
    }

    int remainLength(int to) {
        if (to <= end) {
            return to - pos;
        } else {
            return end - pos;
        }
    }

    int indexOf(char c) {
        return indexOf(pos, end, c);
    }

    int indexOfFrom(int from, char c) {
        return indexOf(from, end, c);
    }

    int indexOfTo(int to, char c) {
        return indexOf(pos, to, c);
    }

    int indexOf(int from, int to, char c) {
        while (from < to) {
            if (buffer[from] == c) {
                return from;
            }
            from++;
        }
        return -1;
    }

    private RuntimeException createException(String message) {
        return new RuntimeException(message + ", at line " + line + ", column " + pos);
    }
}
