// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core.atom;

import webit.schedule.core.Atom;
import webit.schedule.core.AtomProto;
import webit.schedule.util.IntArrayList;

/**
 *
 * @author zqq90
 */
public class RangeAtom implements Atom, AtomProto {

    public final int min;
    public final int max;

    public RangeAtom(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean match(int value) {
        return value >= min && value <= max;
    }

    @Override
    public int maxNumber(int min, int max) {
        if (min < this.min) {
            min = this.min;
        }
        if (max > this.max) {
            max = this.max;
        }
        return max - min + 1;
    }

    @Override
    public void render(IntArrayList list, int min, int max) {
        if (min < this.min) {
            min = this.min;
        }
        if (max > this.max) {
            max = this.max;
        }
        for (int step = min; step <= max; step++) {
            list.addIfAbsent(step);
        }
    }
}
