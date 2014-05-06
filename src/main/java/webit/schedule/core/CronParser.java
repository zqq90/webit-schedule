// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import webit.schedule.core.atom.ArrayAtom;
import webit.schedule.core.atom.DivAtom;
import webit.schedule.core.atom.OrAtom;
import webit.schedule.core.atom.OrAtomGroup;
import webit.schedule.core.atom.OrThreeValueAtom;
import webit.schedule.core.atom.OrValueAtom;
import webit.schedule.core.atom.RangeAtom;
import webit.schedule.core.atom.RangeDivAtom;
import webit.schedule.core.atom.ValueAtom;
import webit.schedule.core.matcher.AndMatcher;
import webit.schedule.core.matcher.AndMatcherGroup;
import webit.schedule.core.matcher.DayOfMonthMatcher;
import webit.schedule.core.matcher.DayOfWeekMatcher;
import webit.schedule.core.matcher.HourMatcher;
import webit.schedule.core.matcher.MinuteMatcher;
import webit.schedule.core.matcher.MonthMatcher;
import webit.schedule.core.matcher.OrMatcher;
import webit.schedule.core.matcher.OrMatcherGroup;
import webit.schedule.core.matcher.YearMatcher;
import webit.schedule.util.IntArrayList;

/**
 *
 * @author zqq90
 */
public class CronParser {

    public static Matcher parse(String cron) throws InvalidCronException {
        return new CronParser(cron).parse();
    }
    private final static char[] EMPTY_CHAR_ARRAY = new char[0];

    private final char[] buffer;

    public CronParser(final char[] cron) {
        this.buffer = cron != null ? cron : EMPTY_CHAR_ARRAY;
    }

    public CronParser(final String cron) {
        this.buffer = cron != null ? cron.toCharArray() : EMPTY_CHAR_ARRAY;
    }

    public Matcher parse() throws InvalidCronException {
        final int buff_len = buffer.length;
        final List<Matcher> matchers = new ArrayList<Matcher>();
        int start = 0;
        int next;
        Matcher matcher;
        while (start < buff_len) {
            next = nextChar('|', start, buff_len);
            matcher = parseSingleMatcher(start, next);
            if (matcher != null
                    && matcher != Matcher.MATCH_ALL) {
                matchers.add(matcher);
            } else {
                //NOTE: if one matcher is null, it means match all.
                return Matcher.MATCH_ALL;
            }
            start = next + 1;
        }
        //export
        switch (matchers.size()) {
            case 0:
                return Matcher.MATCH_ALL;
            case 1:
                return matchers.get(0);
            case 2:
                return new OrMatcher(matchers.get(0), matchers.get(1));
            default:
                return new OrMatcherGroup(matchers.toArray(new Matcher[matchers.size()]));
        }
    }

    private int skipRepeatChar(char c, int offset, final int to) {
        while (offset < to) {
            if (buffer[offset] == c) {
                offset++;
            } else {
                return offset;
            }
        }
        return to;
    }

    private Matcher parseSingleMatcher(final int offset, final int to) {
        if (offset >= to) {
            throw createInvalidCronException("Invalid cron-expression", offset);
        }
        final List<Matcher> matchers = new ArrayList<Matcher>();

        int start = skipRepeatChar(' ', offset, to);
        if (start >= to) {
            throw createInvalidCronException("Invalid cron-expression", offset);
        }
        int next;
        Atom atom;

        int step = 0;

        while (true) {
            next = nextChar(' ', start, to);
            final List<AtomProto> atomProtos = parseAtoms(start, next);
            switch (step) {
                case 0: //minute
                    atom = warpToOrAtom(atomProtos, 0, 59);
                    if (atom != null) {
                        matchers.add(new MinuteMatcher(atom));
                    }
                    break;
                case 1: //hour
                    atom = warpToOrAtom(atomProtos, 0, 23);
                    if (atom != null) {
                        matchers.add(new HourMatcher(atom));
                    }
                    break;
                case 2: //Day
                    atom = warpToOrAtom(atomProtos, 1, 31);
                    if (atom != null) {
                        matchers.add(new DayOfMonthMatcher(atom));
                    }
                    break;
                case 3: //month
                    atom = warpToOrAtom(atomProtos, 1, 12);
                    if (atom != null) {
                        matchers.add(new MonthMatcher(atom));
                    }
                    break;
                case 4: //year
                    atom = warpToOrAtom(atomProtos, 1, 99999);
                    if (atom != null) {
                        matchers.add(new YearMatcher(atom));
                    }
                    break;
                case 5: //dayofweek
                    atom = warpToOrAtom(atomProtos, 0, 6);
                    if (atom != null) {
                        matchers.add(new DayOfWeekMatcher(atom));
                    }
                    break;
                default:
                    return wrapToSingleAndMatcher(matchers);
            }
            step++;

            start = skipRepeatChar(' ', next, to);
            if (start == to) {
                return wrapToSingleAndMatcher(matchers);
            }
        }
    }

    private Matcher wrapToSingleAndMatcher(final List<Matcher> matchers) {
        //XXX: 根据复杂度优化排序
        switch (matchers.size()) {
            case 0:
                return null;
            case 1:
                return matchers.get(0);
            case 2:
                return new AndMatcher(matchers.get(0), matchers.get(1));
            default:
                return new AndMatcherGroup(matchers.toArray(new Matcher[matchers.size()]));
        }
    }

    private List<AtomProto> parseAtoms(final int offset, final int to) {
        if (buffer[to - 1] == ',') {
            throw createInvalidCronException("Invalid chat ','", to);
        }
        final List<AtomProto> atoms = new ArrayList<AtomProto>();
        int start = offset;
        int next;
        AtomProto atom;
        while (start < to) {
            next = nextChar(',', start, to);
            atom = parseSingleAtom(start, next);
            if (atom != null) {
                atoms.add(atom);
            }
            start = next + 1;
        }
        return atoms;
    }

    private Atom warpToOrAtom(List<AtomProto> atomProtos, final int min, final int max) {

        final int protoSize = atomProtos.size();
        if (protoSize == 1) {
            return atomProtos.get(0);
        } else if (protoSize == 0) {
            return null;
        } else {
            final List<Atom> atoms = new ArrayList<Atom>(protoSize);
            final IntArrayList list = new IntArrayList();
            AtomProto atomProto;

            for (Iterator<AtomProto> it = atomProtos.iterator(); it.hasNext();) {
                atomProto = it.next();
                if (atomProto.maxNumber(min, max) <= 6) {
                    atomProto.render(list, min, max);
                } else {
                    atoms.add(atomProto);
                }
            }
            switch (list.size()) {
                case 0:
                    break;
                case 1:
                    atoms.add(0, new ValueAtom(list.get(0)));
                    break;
                case 2:
                    atoms.add(0, new OrValueAtom(list.get(0), list.get(1)));
                    break;
                case 3:
                    atoms.add(0, new OrThreeValueAtom(list.get(0), list.get(1), list.get(2)));
                    break;
                default:
                    atoms.add(0, new ArrayAtom(list.toSortedArray()));
            }

            //export
            switch (atoms.size()) {
                case 0:
                    return null;
                case 1:
                    return atoms.get(0);
                case 2:
                    return new OrAtom(atoms.get(0), atoms.get(1));
                default:
                    return new OrAtomGroup(atoms.toArray(new Atom[atoms.size()]));
            }
        }
    }

    private AtomProto parseSingleAtom(final int offset, final int to) {
        if (offset >= to) {
            throw createInvalidCronException("Invalid cron-expression", offset);
        }

        int rangeChar = nextChar('-', offset, to);
        if (rangeChar != to) {
            int divChar = nextChar('/', rangeChar + 1, to);
            if (divChar != to) {
                return new RangeDivAtom(paserNumber(offset, rangeChar),
                        paserNumber(rangeChar + 1, divChar),
                        paserNumber(divChar + 1, to));
            } else {
                return new RangeAtom(paserNumber(offset, rangeChar),
                        paserNumber(rangeChar + 1, to));
            }
        } else if (buffer[offset] == '*') {
            final int offset_1;
            if ((offset_1 = offset + 1) == to) {
                return null; //TRUE_ATOM;
            } else if (buffer[offset_1] == '/') {
                return new DivAtom(paserNumber(offset_1 + 1, to));
            } else {
                throw createInvalidCronException("Invalid char '" + buffer[offset_1] + '\'', offset_1);
            }
        } else {
            int divChar = nextChar('/', offset + 1, to);
            if (divChar != to) {
                return new RangeDivAtom(paserNumber(offset, divChar),
                        Integer.MAX_VALUE,
                        paserNumber(divChar + 1, to));
            } else {
                return new ValueAtom(paserNumber(offset, to));
            }
        }
    }

    private int paserNumber(int offset, final int to) throws InvalidCronException {
        if (offset >= to) {
            throw createInvalidCronException("Need a number", offset);
        }
        int value = 0;
        char c;
        while (offset < to) {
            c = buffer[offset++];
            switch (c) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    value = value * 10 + ((int) c - (int) '0');
                    break;
                default:
                    throw createInvalidCronException("Invalid numberic char '" + c + '\'', offset);
            }
        }
        return value;
    }

    private InvalidCronException createInvalidCronException(String message, int offset) {
        return new InvalidCronException(new StringBuilder(message)
                .append(", at ").append(offset)
                .append(" of cron '").append(buffer).append("'.")
                .toString());
    }

    private int nextChar(char c, int offset, int to) {
        final char[] buf = buffer;
        for (; offset < to; offset++) {
            if (buf[offset] == c) {
                return offset;
            }
        }
        return to;
    }
}