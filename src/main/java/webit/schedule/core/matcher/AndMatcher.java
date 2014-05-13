// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core.matcher;

import webit.schedule.Time;
import webit.schedule.core.Matcher;

/**
 *
 * @author zqq90
 */
public class AndMatcher implements Matcher {

    final Matcher left;
    final Matcher right;

    public AndMatcher(Matcher left, Matcher right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean match(final Time time) {
        return left.match(time) && right.match(time);
    }
}
