package org.zeroturnaround.stats;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Plugin;
import hudson.model.Action;
import hudson.model.ManagementLink;
import hudson.model.TaskListener;
import hudson.model.Hudson;
import hudson.model.Project;
import hudson.model.Queue.QueueDecisionHandler;
import hudson.model.Queue.Task;
import hudson.model.Run;
import hudson.model.listeners.RunListener;
import hudson.util.LogTaskListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import jenkins.model.Jenkins;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.zeroturnaround.stats.model.RunStats;
import org.zeroturnaround.stats.model.StatsData;
import org.zeroturnaround.stats.util.ConvertXML;

public class ClusterStatisticsPlugin extends Plugin {
  private StatsData statsData = new StatsData();
  private transient long lastSaved = 0L;

  private static final Logger log = Logger.getLogger(ClusterStatisticsPlugin.class.getName());
  private static final int DATA_SIZE_LIMIT = 150000;
  private static final int SOME_EXTRA = 10000;

  public StatsData getStatsData() {
    return statsData;
  }

  public void setStatsData(StatsData statsData) {
    this.statsData = statsData;
  }

  public static ClusterStatisticsPlugin getInstance() {
    return Jenkins.getInstance().getPlugin(ClusterStatisticsPlugin.class);
  }

  @Override
  public void start() throws Exception {
    super.start();
    load();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
    save();
  }

  public void maybeSave() throws IOException {
    long time = System.currentTimeMillis();
    if (lastSaved == 0L || (time - lastSaved) > (60L * 1000)) {
      super.save();
      lastSaved = System.currentTimeMillis();
    }
  }

  public String getDataStoreSize() {
    long bytes = getConfigXml().getFile().length();
    long kBytes = bytes / 1024;
    if (kBytes == 0L)
      return bytes + " bytes";
    return kBytes + " kb";
  }

  public void doProcessForm(final StaplerRequest request, final StaplerResponse response) throws IOException, ServletException {
    if (request.getParameter("downloadStats") != null) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      PrintStream pos = new PrintStream(bos);
      ConvertXML.processXMLStats(getInstance().getConfigXml().getFile(), pos);
      InputStream is = new ByteArrayInputStream(bos.toByteArray());
      response.serveFile(request, is, lastSaved, 5000, bos.size(), "cluster-stats.csv");
    }
    else if (request.getParameter("deleteStats") != null) {
      getStatsData().deleteAllStatistics();
      save();
      response.forwardToPreviousPage(request);
    }
  }

  public void cleanUp() {
    if (statsData.getAllWaitTimes().size() > (DATA_SIZE_LIMIT + SOME_EXTRA)) {
      statsData.cleanUp(DATA_SIZE_LIMIT);
    }
  }

  @Extension
  public static class MyDecisionHandler extends QueueDecisionHandler {

    @Override
    public boolean shouldSchedule(Task p, List<Action> actions) {
      RunStats stats = new RunStats();
      stats.setProjectName(p.getDisplayName());
      
      ClusterStatisticsPlugin plugin = ClusterStatisticsPlugin.getInstance();
      plugin.getStatsData().addToEdenSpace(stats);

      return true;
    }
  }

  @Extension
  public static class MyListener extends RunListener<Run> {

    @Override
    public void onCompleted(Run r, TaskListener listener) {
      super.onCompleted(r, listener);
      
      ClusterStatisticsPlugin plugin = ClusterStatisticsPlugin.getInstance();
      RunStats stats = plugin.getStatsData().popUnInitializedItem(r.getParent().getDisplayName());
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
      ClusterStatisticsPlugin plugin = ClusterStatisticsPlugin.getInstance();
      RunStats stats = plugin.getStatsData().getUnInitializedItem(r.getParent().getDisplayName());
      if (stats != null) {
        stats.setStarted(System.currentTimeMillis());
      }
      else {
        log.fine("Unable to find the task from Eden space for startup. Ignoring.");
      }
    }

  }

  @Extension
  public static class ClusterStatsManagementLink extends ManagementLink {

    public String getIconFileName() {
      return "/plugin/cluster-stats/icons/cluster-48.png";
    }

    public String getDisplayName() {
      return "Cluster Statistics";
    }

    public String getUrlName() {
      return Hudson.getInstance().getRootUrl() + "plugin/cluster-stats/";
    }

    @Override
    public String getDescription() {
      return "Get statistics about your Jenkins cluster. Average wait time, queue time for the master and the nodes.";
    }
  }
}
