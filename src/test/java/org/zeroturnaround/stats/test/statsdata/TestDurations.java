package org.zeroturnaround.stats.test.statsdata;

import java.util.Map;

import junit.framework.TestCase;

import org.zeroturnaround.stats.model.RunStats;
import org.zeroturnaround.stats.model.StatsData;

public class TestDurations extends TestCase {
  private static final String PROJECT_NAME = "TestProject";
  private static final StatsData statsData = new StatsData();

  public void setUp() {
    statsData.deleteAllStatistics();
  }

  public void testGetAvgDurationAll() {
    genStats(1000, 4000, 7 * StatsData.DAY_IN_MS + 10);
    genStats(1000, 6000, 6000);
    genStats(1000, 5000, 6000);

    long result = statsData.getAvgDuration();

    assertEquals(1000L, result);
  }

  private void genStats(int duration, int timeInQueue, long startedMillisAgo) {
    RunStats stats = new RunStats(duration, timeInQueue, System.currentTimeMillis() - startedMillisAgo, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);
  }

  private void genStats(int duration, int timeInQueue, long startedMillisAgo, String node) {
    RunStats stats = new RunStats(duration, timeInQueue, System.currentTimeMillis() - startedMillisAgo, System.currentTimeMillis() - 12000, PROJECT_NAME, node);
    statsData.addToTenuredSpace(stats);
  }

  public void testGetAvgDurationPastWeek() {
    genStats(1000, 4000, 7 * StatsData.DAY_IN_MS + 10);
    genStats(2000, 6000, 6000);
    genStats(3000, 5000, 6000);

    long result = statsData.getAvgDurationTrailingWeek();
    assertEquals(2500L, result);
  }

  public void testGetAvgDurationPerNode() {
    genStats(1500, 4000, 7 * StatsData.DAY_IN_MS + 10);

    genStats(1500, 6000, 6000);
    genStats(1500, 6000, 6000);

    genStats(2000, 3000, 6000, "node1");
    genStats(3000, 5000, 6000, "node1");

    Map<String, Long> result = statsData.getAvgDurationPerNode();
    assertEquals(1500L, result.get("master").longValue());
    assertEquals(2500L, result.get("node1").longValue());
  }

  public void testGetAvgDurationPerNodePastWeek() {
    genStats(1000, 4000, 7 * StatsData.DAY_IN_MS + 10);

    genStats(1000, 6000, 6000);
    genStats(1500, 6000, 6000);

    genStats(2000, 3000, 6000, "node1");
    genStats(3000, 5000, 6000, "node1");

    Map<String, Long> result = statsData.getAvgDurationPerNodeTrailingWeek();
    assertEquals(2, result.entrySet().size());
    assertEquals(1250L, result.get("master").longValue());
    assertEquals(2500L, result.get("node1").longValue());
  }

  public void testGetAvgThroughputHoursSimple() {
    assertEquals(0, statsData.getAvgThroughputHour());
  }

  public void testGetAvgThroughputHours() {
    genStats(1000, 4000, 2 * StatsData.HOUR_IN_MS + 10);
    genStats(1000, 4000, 2 * StatsData.HOUR_IN_MS + 10);

    genStats(1000, 4000, StatsData.HOUR_IN_MS + 10);
    genStats(1000, 4000, 0);

    assertEquals(2, statsData.getAvgThroughputHour());
  }

}
