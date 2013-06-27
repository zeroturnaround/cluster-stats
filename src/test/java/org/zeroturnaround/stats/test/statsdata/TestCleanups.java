package org.zeroturnaround.stats.test.statsdata;

import java.util.List;

import junit.framework.TestCase;

import org.zeroturnaround.stats.ClusterStatisticsPlugin;
import org.zeroturnaround.stats.model.RunStats;
import org.zeroturnaround.stats.model.StatsData;

public class TestCleanups extends TestCase {
  private static final String PROJECT_NAME = "TestProject";
  private static final StatsData statsData = new StatsData();

  public void setUp() {
    statsData.deleteAllStatistics();
  }

  public void testDeletingOlderOnes() {
    int limit = ClusterStatisticsPlugin.DATA_SIZE_LIMIT+ClusterStatisticsPlugin.SOME_EXTRA;
    for (int i = 0; i < limit; i++) {
      RunStats stats = new RunStats(1000, 4000, i, System.currentTimeMillis() - 12000,
          PROJECT_NAME);
      statsData.addToTenuredSpace(stats);
    }
    
    List<RunStats> all = statsData.getAllWaitTimes();
    RunStats latest = all.get(all.size()-1);
    
    statsData.cleanUp(ClusterStatisticsPlugin.DATA_SIZE_LIMIT);
    
    RunStats nowLatest = all.get(all.size()-1);
    assertEquals(latest, nowLatest);
  }

  public void testCleanup() {
    for (int i = 0; i < ClusterStatisticsPlugin.DATA_SIZE_LIMIT; i++) {
      RunStats stats = new RunStats(1000, 4000, System.currentTimeMillis() - (ClusterStatisticsPlugin.DATA_SIZE_LIMIT - i), System.currentTimeMillis() - 12000,
          PROJECT_NAME);
      statsData.addToTenuredSpace(stats);
    }
    statsData.cleanUp(ClusterStatisticsPlugin.DATA_SIZE_LIMIT);
    assertEquals(ClusterStatisticsPlugin.DATA_SIZE_LIMIT, statsData.getSize());
  }
}
