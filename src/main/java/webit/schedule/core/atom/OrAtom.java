// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core.atom;

import webit.schedule.core.Atom;

/**
 *
 * @author zqq90
 */
public class OrAtom implements Atom {

    public final Atom left;
    public final Atom right;

    public OrAtom(Atom left, Atom right) {
        this.left = left;
        this.right = right;
    }

    public boolean match(int value) {
        return left.match(value) || right.match(value);
    }
}
