// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

import webit.schedule.util.IntList;

/**
 *
 * @author zqq90
 */
class ValueAtom implements Atom, AtomProto {

    final int value;

    ValueAtom(int value) {
        this.value = value;
    }

    @Override
    public boolean match(int value) {
        return this.value == value;
    }

    @Override
    public int maxNumber(int min, int max) {
        return 1;
    }

    @Override
    public void render(IntList list, int min, int max) {
        if (this.value >= min && this.value <= max) {
            list.addIfAbsent(this.value);
        }
    }
}
