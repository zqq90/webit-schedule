// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

import webit.schedule.util.IntList;

/**
 *
 * @author zqq90
 */
class RangeAtom implements Atom, AtomProto {

    final int min;
    final int max;

    RangeAtom(int min, int max) {
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
    public void render(IntList list, int min, int max) {
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
