// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core.matcher;

import webit.schedule.core.Matcher;
import webit.schedule.Time;

/**
 *
 * @author zqq90
 */
public final class AndMatcherGroup implements Matcher {

    final Matcher[] matchers;

    public AndMatcherGroup(Matcher[] matchers) {
        this.matchers = matchers;
    }

    public boolean match(final Time time) {
        for (Matcher matcher : matchers) {
            if (!matcher.match(time)) {
                return false;
            }
        }
        return true;
    }
}
