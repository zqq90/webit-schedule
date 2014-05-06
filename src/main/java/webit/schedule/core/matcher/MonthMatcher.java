// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core.matcher;

import webit.schedule.core.Atom;
import webit.schedule.core.Matcher;
import webit.schedule.Time;

/**
 *
 * @author zqq90
 */
public final class MonthMatcher implements Matcher {

    final Atom atom;

    public MonthMatcher(Atom atom) {
        this.atom = atom;
    }

    public boolean match(Time time) {
        return atom.match(time.month);
    }
}