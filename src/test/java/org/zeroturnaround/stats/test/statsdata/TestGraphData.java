package org.zeroturnaround.stats.test.statsdata;

import java.util.Map;

import junit.framework.TestCase;

import org.zeroturnaround.stats.model.RunStats;
import org.zeroturnaround.stats.model.StatsData;

public class TestGraphData extends TestCase {
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

  public void testThroughPutPerWeek() {
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

    Map<Integer, Integer> result = statsData.getWeeklyThroughput();
    assertEquals(Integer.valueOf(2), result.get(24));
    assertEquals(Integer.valueOf(2), result.get(25));
    assertEquals(Integer.valueOf(6), result.get(26));
  }
}
