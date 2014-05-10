// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core.atom;

import webit.schedule.core.AtomProto;
import webit.schedule.core.Atom;
import webit.schedule.util.IntArrayList;

/**
 *
 * @author zqq90
 */
public class DivAtom implements Atom, AtomProto {

    final int div;

    public DivAtom(int div) {
        this.div = div;
    }

    @Override
    public boolean match(int value) {
        return value % div == 0;
    }

    @Override
    public int maxNumber(int min, int max) {
        return (max - min) / div + 1;
    }

    @Override
    public void render(IntArrayList list, int min, int max) {
        int step = min / div * div;
        if (step < min) {
            step += div;
        }
        while (step <= max) {
            list.addIfAbsent(step);
            step += div;
        }
    }
}
