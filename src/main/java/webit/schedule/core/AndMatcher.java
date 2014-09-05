// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

import webit.schedule.Time;

/**
 *
 * @author zqq90
 */
class AndMatcher implements Matcher {

    private final Matcher left;
    private final Matcher right;

    AndMatcher(Matcher left, Matcher right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean match(final Time time) {
        return left.match(time) && right.match(time);
    }
}
