// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

/**
 *
 * @author zqq90
 */
class ArrayAtom implements Atom {

    private final int[] array;

    ArrayAtom(int[] array) {
        this.array = array;
    }

    @Override
    public boolean match(int value) {
        final int len;
        final int[] myArray;
        len = (myArray = array).length;
        //XXX: binary search ?
        for (int i = 0; i < len; i++) {
            if (myArray[i] == value) {
                return true;
            }
        }
        return false;
    }
}
