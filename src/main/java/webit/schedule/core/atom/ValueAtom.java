// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core.atom;

import webit.schedule.core.Atom;
import webit.schedule.core.AtomProto;
import webit.schedule.util.IntArrayList;

/**
 *
 * @author zqq90
 */
public class ValueAtom implements Atom, AtomProto {

    public final int value;

    public ValueAtom(int value) {
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
    public void render(IntArrayList list, int min, int max) {
        if (this.value >= min && this.value <= max) {
            list.addIfAbsent(this.value);
        }
    }
}
