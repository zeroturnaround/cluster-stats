package org.zeroturnaround.stats.model;

import hudson.model.Computer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jenkins.model.Jenkins;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

public class StatsData {
  public static final long WEEK_IN_MS = 1000L * 60 * 60 * 24 * 7;
  public static final long DAY_IN_MS = 1000L * 60 * 60 * 24;
  public static final long HOUR_IN_MS = 1000L * 60 * 60;

  private List<RunStats> runStats = new ArrayList<RunStats>();
  private transient List<RunStats> edenStats = new ArrayList<RunStats>();

  public List<RunStats> getAllWaitTimes() {
    return runStats;
  }

  public void addWaitTime(long waitTime) {
  }

  private List<RunStats> getEdenStats() {
    if (edenStats == null)
      edenStats = new ArrayList<RunStats>();
    return edenStats;
  }

  public void addToEdenSpace(RunStats runStat) {
    getEdenStats().add(runStat);
  }

  public void addToTenuredSpace(RunStats runStat) {
    runStats.add(runStat);
  }

  public long getSize() {
    return runStats.size();
  }

  public String getAge() {
    if (runStats.size() == 0)
      return "0 days";
    else
      return hudson.Util.getPastTimeString(System.currentTimeMillis() - runStats.get(0).getStarted());
  }

  /*
   * Returns the item that was created in queue phase but has not been fully
   * initialized yet. We use this to find out the items that we should continue working
   * when Jenkins finishes jobs and we need to figure out how long they were in queue phase.
   */
  public RunStats popUnInitializedItem(String name) {
    for (Iterator<RunStats> ite = getEdenStats().iterator(); ite.hasNext();) {
      RunStats stats = ite.next();

      if (name.equals(stats.getProjectName()) &&
          0L == stats.getDuration() && -1L != stats.getTimeInQueue()) {
        ite.remove();
        return stats;
      }
    }
    return null;
  }

  public RunStats getUnInitializedItem(int queueId) {
    for (int i = getEdenStats().size() - 1; i >= 0; i--) {
      RunStats stats = getEdenStats().get(i);
      if (queueId == stats.getQueueId()) {
        return stats;
      }
    }
    return null;
  }

  public long getAvgWait() {
    long total = 0;
    long num = 0;
    for (Iterator<RunStats> ite = runStats.iterator(); ite.hasNext();) {
      RunStats stats = (RunStats) ite.next();
      total += stats.getTimeInQueue();
      num++;
    }

    if (num > 0)
      return total / num;
    else
      return 0L;
  }

  public long getAvgWaitPastWeek() {
    final long sevenDaysAgo = System.currentTimeMillis() - (7 * DAY_IN_MS);

    List<RunStats> copyRunStats = new ArrayList<RunStats>(runStats);
    long total = 0;
    long num = 0;
    for (int i = copyRunStats.size() - 1; i > -1; i--) {
      RunStats stats = copyRunStats.get(i);

      if (stats.getStarted() <= sevenDaysAgo) {
        continue;
      }
      else {
        total += stats.getTimeInQueue();
        num++;
      }
    }

    if (num > 0)
      return total / num;
    else
      return 0L;
  }

  public Map<String, Long> getAvgWaitPerNode() {
    Map<String, Long> rtrn = new HashMap<String, Long>();
    Map<String, Long> counts = new HashMap<String, Long>();

    for (Iterator<RunStats> ite = runStats.iterator(); ite.hasNext();) {
      RunStats stats = (RunStats) ite.next();

      Long value = rtrn.get(stats.getNodeName());
      if (value == null) {
        value = stats.getTimeInQueue();
      }
      else {
        value = value + stats.getTimeInQueue();
      }
      rtrn.put(stats.getNodeName(), value);

      Long count = counts.get(stats.getNodeName());
      if (count == null) {
        count = 1L;
      }
      else {
        count++;
      }
      counts.put(stats.getNodeName(), count);
    }

    for (Iterator<Map.Entry<String, Long>> ite = rtrn.entrySet().iterator(); ite.hasNext();) {
      Map.Entry<String, Long> entry = ite.next();
      Long count = counts.get(entry.getKey());
      rtrn.put(entry.getKey(), entry.getValue() / count);
    }
    return rtrn;
  }

  public Map<String, Long> getAvgWaitPerNodePastWeek() {
    final long sevenDaysAgo = System.currentTimeMillis() - (7 * DAY_IN_MS);

    Map<String, Long> rtrn = new HashMap<String, Long>();
    Map<String, Long> counts = new HashMap<String, Long>();

    for (Iterator<RunStats> ite = runStats.iterator(); ite.hasNext();) {
      RunStats stats = (RunStats) ite.next();

      if (stats.getStarted() <= sevenDaysAgo) {
        continue;
      }
      else {
        Long value = rtrn.get(stats.getNodeName());
        if (value == null) {
          value = stats.getTimeInQueue();
        }
        else {
          value = value + stats.getTimeInQueue();
        }
        rtrn.put(stats.getNodeName(), value);

        Long count = counts.get(stats.getNodeName());
        if (count == null) {
          count = 1L;
        }
        else {
          count++;
        }
        counts.put(stats.getNodeName(), count);
      }
    }

    for (Iterator<Map.Entry<String, Long>> ite = rtrn.entrySet().iterator(); ite.hasNext();) {
      Map.Entry<String, Long> entry = ite.next();
      Long count = counts.get(entry.getKey());
      rtrn.put(entry.getKey(), entry.getValue() / count);
    }
    return rtrn;
  }

  public long getAvgDuration() {
    long total = 0;
    long num = 0;

    for (Iterator<RunStats> ite = runStats.iterator(); ite.hasNext();) {
      RunStats stats = (RunStats) ite.next();
      total += stats.getDuration();
      num++;
    }

    if (num > 0)
      return total / num;
    else
      return 0L;
  }

  public long getAvgDurationPastWeek() {
    final long sevenDaysAgo = System.currentTimeMillis() - (7 * DAY_IN_MS);

    List<RunStats> copyRunStats = new ArrayList<RunStats>(runStats);
    long total = 0;
    long num = 0;
    for (int i = copyRunStats.size() - 1; i > -1; i--) {
      RunStats stats = copyRunStats.get(i);

      if (stats.getStarted() <= sevenDaysAgo) {
        continue;
      }
      else {
        total += stats.getDuration();
        num++;
      }
    }

    if (num > 0)
      return total / num;
    else
      return 0L;
  }

  public Map<String, Long> getAvgDurationPerNode() {
    Map<String, Long> rtrn = new HashMap<String, Long>();
    Map<String, Long> counts = new HashMap<String, Long>();

    for (Iterator<RunStats> ite = runStats.iterator(); ite.hasNext();) {
      RunStats stats = (RunStats) ite.next();

      Long value = rtrn.get(stats.getNodeName());
      if (value == null) {
        value = stats.getDuration();
      }
      else {
        value = value + stats.getDuration();
      }
      rtrn.put(stats.getNodeName(), value);

      Long count = counts.get(stats.getNodeName());
      if (count == null) {
        count = 1L;
      }
      else {
        count++;
      }
      counts.put(stats.getNodeName(), count);
    }

    for (Iterator<Map.Entry<String, Long>> ite = rtrn.entrySet().iterator(); ite.hasNext();) {
      Map.Entry<String, Long> entry = ite.next();
      Long count = counts.get(entry.getKey());
      rtrn.put(entry.getKey(), entry.getValue() / count);
    }
    return rtrn;
  }

  public Map<String, Long> getAvgDurationPerNodePastWeek() {
    final long sevenDaysAgo = System.currentTimeMillis() - (7 * DAY_IN_MS);

    Map<String, Long> rtrn = new HashMap<String, Long>();
    Map<String, Long> counts = new HashMap<String, Long>();

    for (Iterator<RunStats> ite = runStats.iterator(); ite.hasNext();) {
      RunStats stats = (RunStats) ite.next();

      if (stats.getStarted() > sevenDaysAgo) {
        Long value = rtrn.get(stats.getNodeName());
        if (value == null) {
          value = stats.getDuration();
        }
        else {
          value = value + stats.getDuration();
        }
        rtrn.put(stats.getNodeName(), value);

        Long count = counts.get(stats.getNodeName());
        if (count == null) {
          count = 1L;
        }
        else {
          count++;
        }
        counts.put(stats.getNodeName(), count);
      }
      else {
        continue;
      }
    }

    for (Iterator<Map.Entry<String, Long>> ite = rtrn.entrySet().iterator(); ite.hasNext();) {
      Map.Entry<String, Long> entry = ite.next();
      Long count = counts.get(entry.getKey());
      rtrn.put(entry.getKey(), entry.getValue() / count);
    }
    return rtrn;
  }

  public long getAvgThroughputHour() {
    if (runStats.size() == 0)
      return 0L;

    final long first = runStats.get(0).getStarted();
    final long last = runStats.get(runStats.size() - 1).getStarted();

    final long diff = last - first;
    final long hours = diff / HOUR_IN_MS;

    if (HOUR_IN_MS > diff)
      return runStats.size();

    return runStats.size() / hours;
  }

  public long getAvgThroughputDay() {
    if (runStats.size() == 0)
      return 0L;

    final long first = runStats.get(0).getStarted();
    final long last = runStats.get(runStats.size() - 1).getStarted();

    final long diff = last - first;
    final long days = diff / DAY_IN_MS;

    if (DAY_IN_MS > diff)
      return runStats.size();

    return runStats.size() / days;
  }

  public long getAvgThroughputWeek() {
    if (runStats.size() == 0)
      return 0L;

    final long first = runStats.get(0).getStarted();
    final long last = runStats.get(runStats.size() - 1).getStarted();

    final long diff = last - first;
    final long weeks = diff / WEEK_IN_MS;

    if (WEEK_IN_MS > diff)
      return runStats.size();

    return runStats.size() / weeks;
  }

  private RunStats findFirstOfPastWeek() {
    final long sevenDaysAgo = System.currentTimeMillis() - (7 * DAY_IN_MS);

    for (Iterator<RunStats> ite = runStats.iterator(); ite.hasNext();) {
      RunStats stat = ite.next();
      if (stat.getStarted() >= sevenDaysAgo)
        return stat;
    }
    return runStats.get(0);
  }

  private long getCountPastWeek() {
    final long sevenDaysAgo = System.currentTimeMillis() - (7 * DAY_IN_MS);
    long rtrn = 0L;
    for (Iterator<RunStats> ite = runStats.iterator(); ite.hasNext();) {
      RunStats stat = ite.next();
      if (stat.getStarted() >= sevenDaysAgo)
        rtrn++;
    }
    return rtrn;
  }

  public long getAvgThroughputHourPastWeek() {
    if (runStats.size() < 2)
      return 0L;

    final long first = findFirstOfPastWeek().getStarted();
    final long last = runStats.get(runStats.size() - 1).getStarted();

    final long diff = last - first;
    final long hours = diff / HOUR_IN_MS;

    final long pastWeekCount = getCountPastWeek();

    if (HOUR_IN_MS > diff)
      return pastWeekCount;

    return pastWeekCount / hours;
  }

  public long getAvgThroughputDayPastWeek() {
    if (runStats.size() < 2)
      return 0L;

    final long first = findFirstOfPastWeek().getStarted();
    final long last = runStats.get(runStats.size() - 1).getStarted();

    final long diff = last - first;
    final long days = diff / DAY_IN_MS;
    final long pastWeekCount = getCountPastWeek();

    if (DAY_IN_MS > diff)
      return pastWeekCount;

    return pastWeekCount / days;
  }

  public long getAvgThroughputWeekPastWeek() {
    if (runStats.size() < 2)
      return 0L;

    final long pastWeekCount = getCountPastWeek();

    return pastWeekCount;
  }

  public long getDurationPercentile(long percentile) {
    double[] statsL = new double[runStats.size()];
    int i = 0;
    for (Iterator<RunStats> ite = runStats.iterator(); ite.hasNext();) {
      RunStats stats = ite.next();
      statsL[i++] = stats.getDuration();
    }

    Percentile perc = new Percentile();
    Arrays.sort(statsL);
    perc.setData(statsL);
    long rtrn = (long) perc.evaluate(percentile);
    return rtrn;
  }

  public long getWaitPercentile(long percentile) {
    double[] statsL = new double[runStats.size()];
    int i = 0;
    for (Iterator<RunStats> ite = runStats.iterator(); ite.hasNext();) {
      RunStats stats = ite.next();
      statsL[i++] = stats.getTimeInQueue();
    }

    Percentile perc = new Percentile();
    Arrays.sort(statsL);
    perc.setData(statsL);
    long rtrn = (long) perc.evaluate(percentile);
    return rtrn;
  }

  public Map<String, Long> getJobCountBreakdown() {
    Map<String, Long> nodeInfo = new HashMap<String, Long>();
    for (Iterator<RunStats> ite = runStats.iterator(); ite.hasNext();) {
      RunStats stats = ite.next();

      Long count = nodeInfo.get(stats.getNodeName());
      if (count == null)
        count = Long.valueOf(0);
      nodeInfo.put(stats.getNodeName(), ++count);
    }
    return nodeInfo;
  }

  public Map<String, Long> getJobCountBreakdownPastweek() {
    final long sevenDaysAgo = System.currentTimeMillis() - (7 * DAY_IN_MS);
    Map<String, Long> nodeInfo = new HashMap<String, Long>();
    for (Iterator<RunStats> ite = runStats.iterator(); ite.hasNext();) {
      RunStats stats = ite.next();
      if (stats.getStarted() >= sevenDaysAgo) {
        Long count = nodeInfo.get(stats.getNodeName());
        if (count == null)
          count = Long.valueOf(0);
        nodeInfo.put(stats.getNodeName(), ++count);
      }
    }
    return nodeInfo;
  }

  public Map<String, String> getClusterMetaInfo() {
    Map<String, String> clusterInfo = new HashMap<String, String>();
    Computer[] computers = Jenkins.getInstance().getComputers();
    for (int i = 0; i < computers.length; i++) {
      Computer c = computers[i];
      Map<Object, Object> env = null;
      try {
        env = c.getSystemProperties();
      }
      catch (IOException e) {
      }
      catch (InterruptedException e) {
      }
      String osName = "Unknown";
      if (env.get("os.name") != null)
        osName = (String) env.get("os.name");
      clusterInfo.put(computers[i].getNode().getDisplayName(), c.countExecutors()+","+osName);
    }
    return clusterInfo;
  }
  
  public Map<String, Long> getJobsMetaInfo() {
    Map<String, Long> jobInfo = new HashMap<String, Long>();
    for (Iterator<RunStats> ite = runStats.iterator(); ite.hasNext();) {
      RunStats stats = ite.next();

      Long count = jobInfo.get(stats.getProjectName());
      if (count == null)
        count = Long.valueOf(0);
      jobInfo.put(stats.getProjectName(), ++count);
    }
    return jobInfo;
  }

  public void deleteAllStatistics() {
    runStats = new ArrayList<RunStats>();
    edenStats = new ArrayList<RunStats>();
  }

  public void cleanUp(final int dataSizeLimit) {
    if (runStats.size() > dataSizeLimit) {
      runStats = runStats.subList(runStats.size() - dataSizeLimit, runStats.size() - 1);
    }
  }
}
