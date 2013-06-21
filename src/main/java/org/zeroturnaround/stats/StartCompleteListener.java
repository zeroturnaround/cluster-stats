package org.zeroturnaround.stats;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.util.LogTaskListener;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.zeroturnaround.stats.model.RunStats;

@Extension
public class StartCompleteListener extends RunListener<Run> {
  private static final Logger log = Logger.getLogger(StartCompleteListener.class.getName());

  /**
   * Whenever a job is completed we try to figure out which item from the scheduled
   * items was completed. We then attach the node name to the item and propagate
   * from eden to tenured as a sign of completed statistics item.
   */
  @Override
  public void onCompleted(Run r, TaskListener listener) {
    super.onCompleted(r, listener);
    ClusterStatisticsPlugin plugin = ClusterStatisticsPlugin.getInstance();
    RunStats stats = plugin.getStatsData().popUnInitializedItem(r.getParent().getName());
    if (stats != null) {
      stats.setDuration(r.getDuration());
      stats.setStarted(r.getTimeInMillis());

      String nodeName = "master";

      try {
        EnvVars envVars = r.getEnvironment(new LogTaskListener(log, Level.INFO));
        nodeName = envVars.get("NODE_NAME");
      }
      catch (IOException e) {
      }
      catch (InterruptedException e) {
      }

      stats.setNodeName(nodeName);
      plugin.getStatsData().addToTenuredSpace(stats);

      try {
        plugin.cleanUp();
        plugin.maybeSave();
      }
      catch (IOException e1) {
        e1.printStackTrace();
      }
    }
    else {
      log.fine("Unable to find the task from the tasks for completed. Ignoring");
    }
  }

  @Override
  public void onStarted(Run r, TaskListener listener) {
    super.onStarted(r, listener);
    // we actually start the process in the queue listener
    // we should do it here but right now we dont' worry about
    // an item leaving the queue and staying too long before started
    // probably should measure the time in the limbo at one point
  }

}
