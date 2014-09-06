// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

/**
 *
 * @author zqq90
 */
class DivAtom implements Atom, AtomProto {

    private final int div;

    DivAtom(int div) {
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
    public void render(IntSet list, int min, int max) {
        int step = min / div * div;
        if (step < min) {
            step += div;
        }
        while (step <= max) {
            list.add(step);
            step += div;
        }
    }
}
