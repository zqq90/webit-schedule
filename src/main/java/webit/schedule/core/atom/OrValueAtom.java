// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core.atom;

import webit.schedule.core.Atom;

/**
 *
 * @author zqq90
 */
public class OrValueAtom implements Atom {

    public final int left;
    public final int right;

    public OrValueAtom(int left, int right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean match(int value) {
        return value == left || value == right;
    }
}
