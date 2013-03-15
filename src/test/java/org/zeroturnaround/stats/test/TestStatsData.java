package org.zeroturnaround.stats.test;

import java.util.Map;

import junit.framework.TestCase;

import org.zeroturnaround.stats.model.RunStats;
import org.zeroturnaround.stats.model.StatsData;

public class TestStatsData extends TestCase {
  private static final String PROJECT_NAME = "TestProject";
  private static final StatsData statsData = new StatsData();

  public void setUp() {
    statsData.deleteAllStatistics();
  }

  public void testGetAvgWaitEmpty() {
    long result = statsData.getAvgWait();
    assertEquals(0L, result);
  }

  public void testFindItemByProjectNoGoodCandidate() {
    RunStats stats = new RunStats(1000, 4000, System.currentTimeMillis() - (7 * StatsData.DAY_IN_MS + 10), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    RunStats item = statsData.popUnInitializedItem(PROJECT_NAME);
    assertNull(item);
  }

  public void testFindItemByProjectNoCandidateAtAll() {
    RunStats item = statsData.popUnInitializedItem(PROJECT_NAME);
    assertNull(item);
  }

  public void testFindUnInitedItemByProjectFirst() {
    RunStats stats = new RunStats();
    stats.setQueued(System.currentTimeMillis());
    stats.setProjectName(PROJECT_NAME);
    statsData.addToEdenSpace(stats);

    RunStats stats2 = new RunStats();
    stats2.setQueued(System.currentTimeMillis());
    stats.setProjectName(PROJECT_NAME);
    statsData.addToEdenSpace(stats2);

    RunStats item = statsData.popUnInitializedItem(PROJECT_NAME);
    assertEquals(stats, item);
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

  public void testGetAvgThroughputDays() {
    // more than 2 days ago
    RunStats stats = new RunStats(1000, 4000, System.currentTimeMillis() - (2 * StatsData.DAY_IN_MS) - 10, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // more than 2 days ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - (2 * StatsData.DAY_IN_MS) - 10, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // more than 1 days ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - StatsData.DAY_IN_MS - 10, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // during the past day
    stats = new RunStats(1000, 4000, System.currentTimeMillis(), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    assertEquals(2, statsData.getAvgThroughputDay());
    assertEquals(0, statsData.getAvgThroughputHour());
  }

  public void testGetAvgThroughputWeeks() {
    // more than 2 weeks ago
    RunStats stats = new RunStats(1000, 4000, System.currentTimeMillis() - (2 * StatsData.WEEK_IN_MS) - 10, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // more than 2 weeks ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - (2 * StatsData.WEEK_IN_MS) - 10, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // more than 1 week ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - StatsData.WEEK_IN_MS - 10, System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // during the past week
    stats = new RunStats(1000, 4000, System.currentTimeMillis(), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    assertEquals(2, statsData.getAvgThroughputWeek());
    assertEquals(0, statsData.getAvgThroughputHour());
  }

  public void testGetAvgThroughputHoursPastWeek() {
    // more than 2 hours ago and 2 weeks ago
    RunStats stats = new RunStats(1000, 4000, System.currentTimeMillis() - (2 * StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS) - 10, System.currentTimeMillis() - 12000,
        PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // more than 2 hours ago and 2 weeks ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - (2 * StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS) - 10, System.currentTimeMillis() - 12000,
        PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // more than 2 hours ago and 1 week ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - (StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS) - 10, System.currentTimeMillis() - 12000,
        PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // in the last week
    stats = new RunStats(1000, 4000, System.currentTimeMillis(), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // in the last week
    stats = new RunStats(1000, 4000, System.currentTimeMillis(), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    assertEquals(2, statsData.getAvgThroughputHourPastWeek());
  }

  public void testGetAvgThroughputDayPastWeek() {
    // more than 2 hours ago and 2 weeks ago
    RunStats stats = new RunStats(1000, 4000, System.currentTimeMillis() - (2 * StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS) - 10, System.currentTimeMillis() - 12000,
        PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // more than 2 hours ago and 2 weeks ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - (2 * StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS) - 10, System.currentTimeMillis() - 12000,
        PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // more than 2 hours ago and 1 week ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - (StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS) - 10, System.currentTimeMillis() - 12000,
        PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // almost a week ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - (7 * StatsData.DAY_IN_MS - 10), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // more than 2 days ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - (2 * StatsData.DAY_IN_MS + 10), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // more than a day ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - (StatsData.DAY_IN_MS + 10), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // during today
    stats = new RunStats(1000, 4000, System.currentTimeMillis(), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // during today
    stats = new RunStats(1000, 4000, System.currentTimeMillis(), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // during today
    stats = new RunStats(1000, 4000, System.currentTimeMillis(), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // during today
    stats = new RunStats(1000, 4000, System.currentTimeMillis(), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    assertEquals(1, statsData.getAvgThroughputDayPastWeek());
  }

  public void testGetAvgThroughputWeekPastWeek() {
    // more than 2 hours ago and 2 weeks ago
    RunStats stats = new RunStats(1000, 4000, System.currentTimeMillis() - (2 * StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS) - 10, System.currentTimeMillis() - 12000,
        PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // more than 2 hours ago and 2 weeks ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - (2 * StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS) - 10, System.currentTimeMillis() - 12000,
        PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // more than 2 hours ago and 1 week ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - (StatsData.WEEK_IN_MS + 2 * StatsData.HOUR_IN_MS) - 10, System.currentTimeMillis() - 12000,
        PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // almost a week ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - (7 * StatsData.DAY_IN_MS - 10), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // more than 2 days ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - (2 * StatsData.DAY_IN_MS + 10), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // more than a day ago
    stats = new RunStats(1000, 4000, System.currentTimeMillis() - (StatsData.DAY_IN_MS + 10), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // during today
    stats = new RunStats(1000, 4000, System.currentTimeMillis(), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // during today
    stats = new RunStats(1000, 4000, System.currentTimeMillis(), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // during today
    stats = new RunStats(1000, 4000, System.currentTimeMillis(), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    // during today
    stats = new RunStats(1000, 4000, System.currentTimeMillis(), System.currentTimeMillis() - 12000, PROJECT_NAME);
    statsData.addToTenuredSpace(stats);

    assertEquals(10, statsData.getAvgThroughputWeekPastWeek());
  }
}
