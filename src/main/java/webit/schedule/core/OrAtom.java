// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

/**
 *
 * @author zqq90
 */
class OrAtom implements Atom {

    private final Atom left;
    private final Atom right;

    OrAtom(Atom left, Atom right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean match(int value) {
        return left.match(value) || right.match(value);
    }
}
