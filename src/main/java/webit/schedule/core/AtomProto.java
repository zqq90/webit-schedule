// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

/**
 *
 * @author zqq90
 */
interface AtomProto extends Atom {

    int maxNumber(int min, int max);

    void render(IntSet list, int min, int max);
}
