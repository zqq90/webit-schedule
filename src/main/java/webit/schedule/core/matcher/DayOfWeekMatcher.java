// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core.matcher;

import webit.schedule.Time;
import webit.schedule.core.Atom;
import webit.schedule.core.Matcher;

/**
 *
 * @author zqq90
 */
public final class DayOfWeekMatcher implements Matcher {

    final Atom atom;

    public DayOfWeekMatcher(Atom atom) {
        this.atom = atom;
    }

    @Override
    public boolean match(Time time) {
        return atom.match(time.dayOfWeek);
    }
}
