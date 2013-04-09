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

  public void testGetAvgWaitAll() {
    RunStats stats = new RunStats(1000, 4000, System.currentTimeMillis() - (7 * StatsData.DAY_IN_MS + 10), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1000, 6000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1000, 5000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    long result = statsData.getAvgWait();
    assertEquals(5000L, result);
  }

  public void testGetAvgWaitPastWeekEmpty() {
    long result = statsData.getAvgWaitPastWeek();
    assertEquals(0L, result);
  }

  public void testGetAvgWaitPastWeek() {
    RunStats stats = new RunStats(1000, 6000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1000, 4000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    long result = statsData.getAvgWaitPastWeek();
    assertEquals(5000L, result);
  }

  public void testGetAvgWaitPastWeekIgnoreOlderData() {
    // Lets add some data that should not be used
    RunStats stats = new RunStats(1000, 4000, System.currentTimeMillis() - (7 * StatsData.DAY_IN_MS + 10), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // Lets add some data that should be used in calculations
    stats = new RunStats(1000, 6000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1000, 4000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    long result = statsData.getAvgWaitPastWeek();
    assertEquals(5000L, result);
  }

  public void testGetAvgWaitAllPerNode() {
    RunStats stats = new RunStats(1000, 4000, System.currentTimeMillis() - (7 * StatsData.DAY_IN_MS + 10), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1000, 6000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1000, 3000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME, "node1");
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1000, 5000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME, "node1");
    statsData.addToTenuredSpace(stats);

    Map<String, Long> result = statsData.getAvgWaitPerNode();
    assertEquals(5000L, result.get("master").longValue());
    assertEquals(4000L, result.get("node1").longValue());
  }

  public void testGetAvgWaitPastWeekPerNode() {
    RunStats stats = new RunStats(1000, 4000, System.currentTimeMillis() - (7 * StatsData.DAY_IN_MS + 10), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1000, 6000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1000, 3000, System.currentTimeMillis() - (7 * StatsData.DAY_IN_MS + 10), System.currentTimeMillis() - 12000, PROJECT_NAME, "node1");
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1000, 4000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME, "node1");
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1000, 5000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME, "node1");
    statsData.addToTenuredSpace(stats);

    Map<String, Long> result = statsData.getAvgWaitPerNodePastWeek();
    assertEquals(6000L, result.get("master").longValue());
    assertEquals(4500L, result.get("node1").longValue());
  }
}
