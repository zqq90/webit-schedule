// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

/**
 *
 * @author zqq90
 */
class OrAtomGroup implements Atom {

    private final Atom[] atoms;

    OrAtomGroup(Atom[] atoms) {
        this.atoms = atoms;
    }

    @Override
    public boolean match(int value) {
        for (int i = 0; i < atoms.length; i++) {
            if (atoms[i].match(value)) {
                return true;
            }
        }
        return false;
    }
}
