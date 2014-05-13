// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core.atom;

import webit.schedule.core.Atom;
import webit.schedule.core.AtomProto;
import webit.schedule.util.IntArrayList;

/**
 *
 * @author zqq90
 */
public class RangeDivAtom implements Atom, AtomProto {

    public final int min;
    public final int max;
    public final int div;

    public RangeDivAtom(int min, int max, int div) {
        this.min = min;
        this.max = max;
        this.div = div;
    }

    @Override
    public boolean match(final int value) {
        final int temp;
        return value <= max
                && (temp = value - min) >= 0
                && (temp % div) == 0;
    }

    @Override
    public int maxNumber(int min, int max) {
        if (min < this.min) {
            min = this.min;
        }
        if (max > this.max) {
            max = this.max;
        }
        return (max - min) / div + 1;
    }

    @Override
    public void render(final IntArrayList list, int min, int max) {
        if (min < this.min) {
            min = this.min;
        }
        if (max > this.max) {
            max = this.max;
        }
        int step = this.min;
        while (step < min) {
            step += div;
        }
        while (step <= max) {
            list.addIfAbsent(step);
            step += div;
        }
    }
}
