// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core.atom;

import webit.schedule.core.Atom;

/**
 *
 * @author zqq90
 */
public class OrThreeValueAtom implements Atom {

    public final int one;
    public final int two;
    public final int three;

    public OrThreeValueAtom(int one, int two, int three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }

    public boolean match(int value) {
        return value == one || value == two || value == three;
    }
}
