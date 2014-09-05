// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

import webit.schedule.util.IntList;

/**
 *
 * @author zqq90
 */
interface AtomProto extends Atom {

    int maxNumber(int min, int max);

    void render(IntList list, int min, int max);
}
