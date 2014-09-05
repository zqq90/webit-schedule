// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

/**
 *
 * @author zqq90
 */
class OrValueAtom implements Atom {

    private final int left;
    private final int right;

    OrValueAtom(int left, int right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean match(int value) {
        return value == left || value == right;
    }
}
