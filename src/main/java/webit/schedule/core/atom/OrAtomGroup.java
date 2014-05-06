// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core.atom;

import webit.schedule.core.Atom;

/**
 *
 * @author zqq90
 */
public class OrAtomGroup implements Atom {

    public final Atom[] atoms;

    public OrAtomGroup(Atom[] atoms) {
        this.atoms = atoms;
    }

    public boolean match(int value) {
        for (int i = 0; i < atoms.length; i++) {
            if (atoms[i].match(value)) {
                return true;
            }
        }
        return false;
    }
}
