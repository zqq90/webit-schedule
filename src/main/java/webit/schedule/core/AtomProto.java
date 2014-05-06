// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

import webit.schedule.util.IntArrayList;

/**
 *
 * @author zqq90
 */
public interface AtomProto extends Atom{

    int maxNumber(int min, int max);

    void render(IntArrayList list, int min, int max);
}
