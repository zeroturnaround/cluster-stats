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

    assertEquals(7, statsData.getAvgThroughputWeekPastWeek());
  }
}
