// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.ext.workdays;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import webit.schedule.Time;

/**
 *
 * @author Zqq
 */
public class Workday {

    private final WorkdayLoader loader;
    private final Map<Integer, YearEntry> yearEntryCache;

    public Workday(WorkdayLoader loader) {
        this.loader = loader;
        this.yearEntryCache = new HashMap<Integer, YearEntry>();
    }

    /**
     * If is workday.
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public boolean isWorkday(int year, int month, int day) {
        return getYearEntry(year).isWorkday(month, day);
    }

    /**
     * If is workday.
     *
     * @param time
     * @return
     */
    public boolean isWorkday(Time time) {
        return isWorkday(time.year, time.month, time.day);
    }

    /**
     * Get message for this day.
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public String getMessage(int year, int month, int day) {
        return getYearEntry(year).getMessage(month, day);
    }

    /**
     * Get message for this day.
     *
     * @param time
     * @return
     */
    public String getMessage(Time time) {
        return getMessage(time.year, time.month, time.day);
    }

    /**
     * Get <code>YearEntry</code> by year.
     *
     * @param year
     * @return
     * @throws ConfigIOException
     */
    private YearEntry getYearEntry(int year) throws ConfigIOException {
        YearEntry yearEntry = this.yearEntryCache.get(year);
        if (yearEntry != null) {
            return yearEntry;
        }
        return loadYearEntryIfAbsent(year);
    }

    /**
     * Load <code>YearEntry</code> by year if absent.
     *
     * @param year
     * @return
     * @throws ConfigIOException
     */
    private YearEntry loadYearEntryIfAbsent(int year) throws ConfigIOException {
        synchronized (this.yearEntryCache) {
            YearEntry yearEntry = this.yearEntryCache.get(year);
            if (yearEntry == null) {
                Reader reader = loader.openReader(year);
                try {
                    yearEntry = new Parser().parse(year, reader);
                } catch (IOException e) {
                    throw new ConfigIOException(e);
                } finally {
                    try {
                        reader.close();
                    } catch (IOException ignore) {
                    }
                }
                this.yearEntryCache.put(year, yearEntry);
            }
            return yearEntry;
        }
    }

    /**
     * Clear cache.
     */
    public void clear() {
        synchronized (this.yearEntryCache) {
            this.yearEntryCache.clear();
        }
    }

    /**
     * Create <code>Workday</code> by loader.
     *
     * @param loader
     * @return
     */
    public static Workday create(WorkdayLoader loader) {
        return new Workday(loader);
    }
}
