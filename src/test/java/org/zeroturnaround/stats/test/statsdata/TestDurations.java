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
    RunStats stats = new RunStats(1000, 4000, System.currentTimeMillis() - (7 * StatsData.DAY_IN_MS + 10), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1000, 6000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1000, 5000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    long result = statsData.getAvgDuration();
    assertEquals(1000L, result);
  }

  public void testGetAvgDurationPastWeek() {
    RunStats stats = new RunStats(1000, 4000, System.currentTimeMillis() - (7 * StatsData.DAY_IN_MS + 10), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(2000, 6000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(3000, 5000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    long result = statsData.getAvgDurationPastWeek();
    assertEquals(2500L, result);
  }

  public void testGetAvgDurationPerNode() {
    RunStats stats = new RunStats(1500, 4000, System.currentTimeMillis() - (7 * StatsData.DAY_IN_MS + 10), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1500, 6000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1500, 6000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(2000, 3000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME, "node1");
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(3000, 5000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME, "node1");
    statsData.addToTenuredSpace(stats);

    Map<String, Long> result = statsData.getAvgDurationPerNode();
    assertEquals(1500L, result.get("master").longValue());
    assertEquals(2500L, result.get("node1").longValue());
  }

  public void testGetAvgDurationPerNodePastWeek() {
    RunStats stats = new RunStats(1000, 4000, System.currentTimeMillis() - (7 * StatsData.DAY_IN_MS + 10), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1000, 6000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(1500, 6000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(2000, 3000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME, "node1");
    statsData.addToTenuredSpace(stats);

    stats = new RunStats(3000, 5000, System.currentTimeMillis() - 6000, System.currentTimeMillis() - 12000, PROJECT_NAME, "node1");
    statsData.addToTenuredSpace(stats);

    Map<String, Long> result = statsData.getAvgDurationPerNodePastWeek();
    assertEquals(2, result.entrySet().size());
    assertEquals(1250L, result.get("master").longValue());
    assertEquals(2500L, result.get("node1").longValue());
  }

  public void testGetAvgThroughputHoursSimple() {
    assertEquals(0, statsData.getAvgThroughputHour());
  }

  public void testGetAvgThroughputHours() {
    // more than 2 hours ago
    RunStats stats = new RunStats(1000, 4000, System.currentTimeMillis() - (2 * StatsData.HOUR_IN_MS) - 10, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // more than 2 hours ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - (2 * StatsData.HOUR_IN_MS) - 10, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // more than 1 hour ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - StatsData.HOUR_IN_MS - 10, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // during the past hour
    stats = new RunStats(1000, 4000, System.currentTimeMillis(), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    assertEquals(2, statsData.getAvgThroughputHour());
  }

}
