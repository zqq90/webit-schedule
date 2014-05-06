// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core.atom;

import webit.schedule.core.Atom;

/**
 *
 * @author zqq90
 */
public class ArrayAtom implements Atom {

    private final int[] array;

    public ArrayAtom(int[] array) {
        this.array = array;
    }

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
