package org.zeroturnaround.stats;

import hudson.Extension;
import hudson.model.Queue.LeftItem;
import hudson.model.Queue.WaitingItem;
import hudson.model.queue.QueueListener;

import java.util.logging.Logger;

import org.zeroturnaround.stats.model.RunStats;

@Extension
public class MyQueueListener extends QueueListener {
  private static final Logger log = Logger.getLogger(MyQueueListener.class.getName());

  @Override
  public void onEnterWaiting(WaitingItem wi) {
    RunStats stats = new RunStats();
    stats.setProjectName(wi.task.getName());
    stats.setQueueId(wi.id);

    ClusterStatisticsPlugin plugin = ClusterStatisticsPlugin.getInstance();
    plugin.getStatsData().addToEdenSpace(stats);
  }

  @Override
  public void onLeft(LeftItem li) {
    ClusterStatisticsPlugin plugin = ClusterStatisticsPlugin.getInstance();
    RunStats stats = plugin.getStatsData().getUnInitializedItem(li.id);
    if (stats != null) {
      stats.setStarted(System.currentTimeMillis());
    }
    else {
      log.fine("Unable to find the task from Eden space for startup. Ignoring.");
    }
  }

}