package org.zeroturnaround.stats;

import hudson.Extension;
import hudson.model.ManagementLink;
import hudson.model.Hudson;

@Extension
public class ClusterStatsManagementLink extends ManagementLink {

  public String getIconFileName() {
    return "/plugin/cluster-stats/icons/cluster-48.png";
  }

  public String getDisplayName() {
    return "Cluster Statistics";
  }

  public String getUrlName() {
    return Hudson.getInstance().getRootUrlFromRequest() + "plugin/cluster-stats/";
  }

  @Override
  public String getDescription() {
    return "Get statistics about your Jenkins cluster. Average wait time, queue time for the master and the nodes.";
  }
}
