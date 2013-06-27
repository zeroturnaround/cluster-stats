package org.zeroturnaround.stats.test.statsdata;

import java.util.Map;

import junit.framework.TestCase;

import org.zeroturnaround.stats.model.RunStats;
import org.zeroturnaround.stats.model.StatsData;

public class TestWaits extends TestCase {
  private static final String PROJECT_NAME = "TestProject";
  private static final StatsData statsData = new StatsData();

  public void setUp() {
    statsData.deleteAllStatistics();
  }

  public void testGetAvgWaitEmpty() {
    long result = statsData.getAvgWait();
    assertEquals(0L, result);
  }

  private void genStats(int duration, int timeInQueue, long startedMillisAgo) {
    RunStats stats = new RunStats(duration, timeInQueue, System.currentTimeMillis() - startedMillisAgo, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);
  }

  private void genStats(int duration, int timeInQueue, long startedMillisAgo, String node) {
    RunStats stats = new RunStats(duration, timeInQueue, System.currentTimeMillis() - startedMillisAgo, System.currentTimeMillis() - 12000, PROJECT_NAME, node);
    statsData.addToTenuredSpace(stats);
  }

  public void testGetAvgWaitAll() {
    genStats(1000, 4000, 7 * StatsData.DAY_IN_MS + 10);

    genStats(1000, 6000, 6000);
    genStats(1000, 5000, 6000);

    long result = statsData.getAvgWait();
    assertEquals(5000L, result);
  }

  public void testGetAvgWaitPastWeekEmpty() {
    long result = statsData.getAvgWaitTrailingWeek();
    assertEquals(0L, result);
  }

  public void testGetAvgWaitPastWeek() {
    genStats(1000, 6000, 6000);
    genStats(1000, 4000, 6000);

    long result = statsData.getAvgWaitTrailingWeek();
    assertEquals(5000L, result);
  }

  public void testGetAvgWaitPastWeekIgnoreOlderData() {
    genStats(1000, 6000, 7 * StatsData.DAY_IN_MS + 10);

    genStats(1000, 6000, 6000);
    genStats(1000, 4000, 6000);

    long result = statsData.getAvgWaitTrailingWeek();
    assertEquals(5000L, result);
  }

  public void testGetAvgWaitAllPerNode() {
    genStats(1000, 4000, 7 * StatsData.DAY_IN_MS + 10);

    genStats(1000, 6000, 6000);

    genStats(1000, 3000, 6000, "node1");
    genStats(1000, 5000, 6000, "node1");

    Map<String, Long> result = statsData.getAvgWaitPerNode();
    assertEquals(5000L, result.get("master").longValue());
    assertEquals(4000L, result.get("node1").longValue());
  }

  public void testGetAvgWaitPastWeekPerNode() {
    genStats(1000, 4000, 7 * StatsData.DAY_IN_MS + 10);
    genStats(1000, 6000, 6000);

    genStats(1000, 3000, 7 * StatsData.DAY_IN_MS + 10, "node1");

    genStats(1000, 4000, 6000, "node1");
    genStats(1000, 5000, 6000, "node1");

    Map<String, Long> result = statsData.getAvgWaitPerNodeTrailingWeek();
    assertEquals(6000L, result.get("master").longValue());
    assertEquals(4500L, result.get("node1").longValue());
  }
}
