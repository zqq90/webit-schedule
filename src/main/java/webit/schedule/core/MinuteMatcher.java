// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

import webit.schedule.Time;

/**
 *
 * @author zqq90
 */
class MinuteMatcher implements Matcher {

    private final Atom atom;

    MinuteMatcher(Atom atom) {
        this.atom = atom;
    }

    @Override
    public boolean match(Time time) {
        return atom.match(time.minute);
    }
}
