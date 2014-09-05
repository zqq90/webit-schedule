// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

import webit.schedule.Time;

/**
 *
 * @author zqq90
 */
class OrMatcherGroup implements Matcher {

    private final Matcher[] matchers;

    OrMatcherGroup(Matcher[] matchers) {
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
