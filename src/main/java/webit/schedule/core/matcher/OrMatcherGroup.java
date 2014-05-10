// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core.matcher;

import webit.schedule.core.Matcher;
import webit.schedule.Time;

/**
 *
 * @author zqq90
 */
public final class OrMatcherGroup implements Matcher{
    final Matcher[] matchers;

    public OrMatcherGroup(Matcher[] matchers) {
        this.matchers = matchers;
    }

    @Override
    public boolean match(final Time time) {
        for (Matcher matcher : matchers) {
            if (matcher.match(time)) {
                return true;
            }
        }
        return false;
    }
}
