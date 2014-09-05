// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

/**
 *
 * @author zqq90
 */
class OrThreeValueAtom implements Atom {

    private final int one;
    private final int two;
    private final int three;

    OrThreeValueAtom(int one, int two, int three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }

    @Override
    public boolean match(int value) {
        return value == one || value == two || value == three;
    }
}
