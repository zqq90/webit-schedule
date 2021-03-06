// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

import webit.schedule.Time;

/**
 *
 * @author zqq90
 */
public interface Matcher {

    final static Matcher MATCH_ALL = new Matcher() {

        @Override
        public boolean match(Time time) {
            return true;
        }
    };

    boolean match(Time time);
}
