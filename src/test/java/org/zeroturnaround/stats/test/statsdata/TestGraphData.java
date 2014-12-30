package org.zeroturnaround.stats.test.statsdata;

import java.util.Arrays;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Assert;
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

    // more than 2 hours ago and 1 week ago
    genStats(StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS + 10);

    // today
    genStats(0);
    genStats(0);
    genStats(0);
    genStats(0);

    // returns a map of "week no dash 2 digits of the year" as a key and then
    // number of data points in that week. They are ordered but for this test
    // in December the ordering can be different. So weird assert follows :)
    //
    Map<String, Integer> result = statsData.getWeeklyThroughput();
    Integer[] actual = result.values().toArray(new Integer[] {});
    Integer[] expected = new Integer[] { 2, 2, 4 };
    Arrays.sort(actual);
    Arrays.sort(expected);
    Assert.assertArrayEquals(expected, actual);
  }
}
