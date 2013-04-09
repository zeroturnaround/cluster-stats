package org.zeroturnaround.stats.test.statsdata;

import junit.framework.TestCase;

import org.zeroturnaround.stats.model.RunStats;
import org.zeroturnaround.stats.model.StatsData;

public class TestThroughput extends TestCase {
  private static final String PROJECT_NAME = "TestProject";
  private static final StatsData statsData = new StatsData();

  public void setUp() {
    statsData.deleteAllStatistics();
  }

  // quite specific helper
  private void genStats(long startedMillisAgo) {
    RunStats stats = new RunStats(1000, 4000, System.currentTimeMillis() - startedMillisAgo, System.currentTimeMillis() - 12000,
        PROJECT_NAME);
    statsData.addToTenuredSpace(stats);
  }

  public void testGetAvgThroughputHoursSimple() {
    assertEquals(0, statsData.getAvgThroughputHour());
  }

  public void testGetAvgThroughputHours() {
    // more than 2 hours ago
    genStats((2 * StatsData.HOUR_IN_MS) + 10);

    // more than 2 hours ago
    genStats((2 * StatsData.HOUR_IN_MS) + 10);

    // more than 1 hour ago
    genStats(StatsData.HOUR_IN_MS + 10);

    // during the past hour
    genStats(0);

    assertEquals(2, statsData.getAvgThroughputHour());
  }

  public void testGetAvgThroughputDays() {
    // more than 2 days ago
    genStats((2 * StatsData.DAY_IN_MS) + 10);

    // more than 2 days ago
    genStats((2 * StatsData.DAY_IN_MS) + 10);

    // more than 1 days ago
    genStats(StatsData.DAY_IN_MS + 10);

    // during the past day
    genStats(0);

    assertEquals(2, statsData.getAvgThroughputDay());
    assertEquals(0, statsData.getAvgThroughputHour());
  }

  public void testGetAvgThroughputWeeks() {
    // more than 2 weeks ago
    genStats((2 * StatsData.WEEK_IN_MS) + 10);
    genStats((2 * StatsData.WEEK_IN_MS) + 10);
    // more than 1 week ago
    genStats(StatsData.WEEK_IN_MS + 10);
    // last week
    genStats(0);

    assertEquals(2, statsData.getAvgThroughputWeek());
    assertEquals(0, statsData.getAvgThroughputHour());
  }

  public void testGetAvgThroughputHoursPastWeek() {
    // more than 2 hours ago and 2 weeks ago
    genStats((2 * StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS) + 10);
    genStats((2 * StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS) + 10);

    // more than 2 hours ago and 1 week ago
    genStats((StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS) + 10);

    // in the last week
    genStats(0);
    genStats(0);

    assertEquals(2, statsData.getAvgThroughputHourPastWeek());
  }

  public void testGetAvgThroughputDayPastWeek() {
    // more than 2 hours ago and 2 weeks ago
    genStats((2 * StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS) + 10);

    // more than 2 hours ago and 2 weeks ago
    genStats((2 * StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS) + 10);

    // more than 2 hours ago and 1 week ago
    genStats((StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS) + 10);

    // almost a week ago
    genStats((7 * StatsData.DAY_IN_MS - 10));

    // more than 2 days ago
    genStats((2 * StatsData.DAY_IN_MS + 10));

    // more than a day ago
    genStats((StatsData.DAY_IN_MS + 10));

    // during today
    genStats(0);
    genStats(0);
    genStats(0);
    genStats(0);

    assertEquals(1, statsData.getAvgThroughputDayPastWeek());
  }

  public void testGetAvgThroughputWeekPastWeek() {
    // more than 2 hours ago and 2 weeks ago
    genStats(2 * StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS + 10);

    // more than 2 hours ago and 2 weeks ago
    genStats(2 * StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS + 10);

    // more than 2 hours ago and 1 week ago
    genStats(StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS + 10);

    // almost a week ago
    genStats(7 * StatsData.DAY_IN_MS - 10);

    // more than 2 days ago
    genStats(2 * StatsData.DAY_IN_MS + 10);

    // more than a day ago
    genStats(StatsData.DAY_IN_MS + 10);

    // today
    genStats(0);
    genStats(0);
    genStats(0);
    genStats(0);

    assertEquals(7, statsData.getAvgThroughputWeekPastWeek());
  }
}
