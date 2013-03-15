package org.zeroturnaround.stats;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Queue.QueueDecisionHandler;
import hudson.model.Queue.Task;

import java.util.List;

import org.zeroturnaround.stats.model.RunStats;

@Extension
public class QueingScheduler extends QueueDecisionHandler {

  /**
   * We will store all scheduled items into an eden space. Each
   * item will get a timestamp on creation so we know when they
   * were scheduled.
   */
  @Override
  public boolean shouldSchedule(Task p, List<Action> actions) {
    RunStats stats = new RunStats();
    stats.setProjectName(p.getDisplayName());

    ClusterStatisticsPlugin plugin = ClusterStatisticsPlugin.getInstance();
    plugin.getStatsData().addToEdenSpace(stats);

    return true;
  }
}