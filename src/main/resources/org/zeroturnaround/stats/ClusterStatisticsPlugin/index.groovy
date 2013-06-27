// Namespaces
st = namespace("jelly:stapler")
j = namespace("jelly:core")
i = namespace("jelly:fmt")
t = namespace("/lib/hudson")
f = namespace("/lib/form")
d = namespace("jelly:define")

import java.text.Normalizer.Form;
import java.text.DecimalFormat;

import hudson.Util
import hudson.model.Hudson;
import lib.LayoutTagLib
import org.zeroturnaround.stats.model.StatsData
import org.zeroturnaround.stats.ClusterStatisticsPlugin
import org.zeroturnaround.stats.ClusterStatsManagementLink

def l=namespace(LayoutTagLib.class)
def StatsData statsData = my.statsData;
def myRootURL = Hudson.getInstance().getRootUrlFromRequest()
def myResURL =  rootURL+hudson.Functions.getResourcePath() + "/plugin/cluster-stats";
clusterMetaInfo = statsData.getClusterMetaInfo();

l.layout(title: _("Disk Usage"), secured: "true") {
  l.side_panel() {
    l.tasks() {
      l.task(icon: "images/24x24/up.gif", title: _("Back to Dashboard"), href: "${rootURL}/")
    }
  }
  l.main_panel() {
    style("""
      .stats {
        padding:10px;
        margin:10px;
      }

      .stats th {
        font-size:10pt;
        text-align:left;
      }

      .stats td {
        padding-right:40px;
        margin:2px;
        text-align:left;
      }
    """)
    script(src:"${rootURL}/plugin/cluster-stats/js/cluster-stats.js")
    h1(_("Jenkins Cluster Statistics"))
    h2("Build duration")
    div() {
      text("Average build time: ")
      b(Util.getTimeSpanString(statsData.getAvgDuration()))
      text(" (all time) ")
      b(Util.getTimeSpanString(statsData.getAvgDurationTrailingWeek()))
      text(" (trailing 7 days). ")
      raw(""" <a href="#" onClick="toggleNodeView('buildNodes');return false;">Show by Node</a>""")
      br()
      text("95th percentile: " + Util.getTimeSpanString(statsData.getDurationPercentile(95)))
      
      Map perNode = statsData.getAvgDurationPerNode();
      perNode = perNode.sort{a, b -> b.value <=> a.value}
      
      raw("""<div id="buildNodes" style="visibility:hidden;display:none"><table class="stats">""")
      tr() {
        th() {
          text("Node")
        }
        th() {
          text("Average duration")
        }
      }
      perNode.each{ k, v ->
        tr() {
          td() {
            text(k)
          }
          td() {
            text(Util.getTimeSpanString(v))
          }
        }
      }
      raw("</table></div>")
    }
    
    h2("Wait time in queue")
    div() {
      text("Average wait time: ")
      b(Util.getTimeSpanString(statsData.getAvgWait()))
      text(" (all time) ")
      b(Util.getTimeSpanString(statsData.getAvgWaitTrailingWeek()))
      text(" (trailing 7 days). ")
      
      raw(""" <a href="#" onClick="toggleNodeView('waitNodes');return false;">Show by Node</a>""")
      br()
      text("95th percentile: " + Util.getTimeSpanString(statsData.getWaitPercentile(95)))
      
      Map perNode = statsData.getAvgWaitPerNode();
      perNode = perNode.sort{a, b -> b.value <=> a.value}
      
      raw("""<div id="waitNodes" style="visibility:hidden;display:none"><table class="stats">""")
      tr() {
        th() {
          text("Node")
        }
        th() {
          text("Average wait")
        }
      }
      perNode.each{ k, v ->
        tr() {
          td() {
            text(k)
          }
          td() {
            text(Util.getTimeSpanString(v))
          }
        }
      }
      raw("</table></div>")
    }
    raw("""<h2>Throughput <a href="${rootURL}/plugin/cluster-stats/throughput"><img src="${myResURL}/icons/chart-48.png" height="16px" width="16px"></a></h2>""")
    jobMetaInfo = statsData.getJobsMetaInfo();
    div() {
        text("""All time: ${statsData.getAvgThroughputHour()} jobs/hour, 
              ${statsData.getAvgThroughputDay()} jobs/day, 
              ${statsData.getAvgThroughputWeek()} jobs/week. """)
        raw(""" <a href="#" onClick="toggleNodeView('throughput');return false;">Show by Node</a>""")
        br()
        text("""Trailing 7 days: ${statsData.getAvgThroughputHourTrailingWeek()} jobs/hour,
                ${statsData.getAvgThroughputDayTrailingWeek()} jobs/day,
                ${statsData.getAvgThroughputWeekTrailingWeek()} jobs/week. """)
        raw(""" <a href="#" onClick="toggleNodeView('throughputTrailingWeek');return false;">Show by Node</a>""")
        br()
        text("We have ")
        b(jobMetaInfo.size())
        text(" unique job configurations. ")
        raw(""" <a href="#" onClick="toggleNodeView('jobBreakdown');return false;">Show by Node</a>""")
        
      raw("""<div id="throughput" style="visibility:hidden;display:none"><table class="stats">""")
      tr() {
        th() {
          text("Node")
        }
        th() {
          text("# jobs")
        }
        th() {
          text("# jobs/exec")
        }
      }
      
      Map perNode = statsData.getJobCountBreakdown();
      perNode = perNode.sort{a, b -> b.value <=> a.value}
      perNode.each{ k, v ->
        tr() {
          td() {
            text(k)
          }
          td() {
            text(v)
          }
          td() {
            noExecs = clusterMetaInfo.get(k)
            if (noExecs == null)
              noExecs = 1;
            else
              noExecs = noExecs.split(",")[0].toInteger();
            
            DecimalFormat df = new DecimalFormat("#####0.00");
            text(df.format(v.toInteger()/(double)noExecs))
            text(" (")
            text(noExecs)
            text(")")
          }
        }
      }
      raw("</table></div>")
      
      raw("""<div id="throughputTrailingWeek" style="visibility:hidden;display:none"><table class="stats">""")
      tr() {
        th() {
          text("Node")
        }
        th() {
          text("# jobs")
        }
        th() {
          text("# jobs/exec")
        }
      }
      
      perNode = statsData.getJobCountBreakdownTrailingWeek();
      perNode = perNode.sort{a, b -> b.value <=> a.value}
      perNode.each{ k, v ->
        tr() {
          td() {
            text(k)
          }
          td() {
            text(v)
          }
          td(){
            noExecs = clusterMetaInfo.get(k)
            if (noExecs == null)
              noExecs = 1;
            else
              noExecs = noExecs.split(",")[0].toInteger();
            
            DecimalFormat df = new DecimalFormat("#####0.00");
            text(df.format(v.toInteger()/(double)noExecs))
            text(" (")
            text(noExecs)
            text(")")
          }
        }
      }
      raw("</table></div>")
    }
    
    raw("""<div id="jobBreakdown" style="visibility:hidden;display:none"><table class="stats">""")
      tr() {
        th() {
          text("Node")
        }
        th() {
          text("No of jobs")
        }
      }
      
      perNode = jobMetaInfo.sort{a, b -> b.value <=> a.value}
      perNode.each{ k, v ->
        tr() {
          td() {
            text(k)
          }
          td() {
            text(v)
          }
        }
      }
      raw("</table></div>")
    
    noExecs = 0;
    clusterMetaInfo.each {
      key, val ->
        noExecs += val.split(",")[0].toInteger();
    }
    h2("Cluster meta info")
    p() {
      text("We have ")
      b(clusterMetaInfo.size())
      text(" nodes in the cluster with ")
      b(noExecs)
      text(" executors.")
      raw(""" <a href="#" onClick="toggleNodeView('metaInfo');return false;">Show by Node</a>""")
      
      raw("""<div id="metaInfo" style="visibility:hidden;display:none"><table class="stats">""")
      tr() {
        th() {
          text("OS")
        }
        th() {
          text("Node")
        }
        th() {
          text("Execs")
        }
      }

      clusterMetaInfo.each{ key, val ->
        tr() {
          td() {
            text(val.split(",")[1])
          }
          td() {
            text(key)
          }
          td() {
            text(val.split(",")[0])
          }
        }
      }
      raw("</table></div>")
    }
    
    h2("Stats")
    p() {
      text("We have been gathering stats for ")
      b("${statsData.getAge()}")
      text(" and there are ")
      b("${statsData.getSize()} data points.")
      text(" The data storage size is ")
      b(my.getDataStoreSize())
      text(".")
    }
    p() {
      f.form(name: "processForm", action: "processForm", method: "post") {
        f.submit(name: "downloadStats", value: "Download recorded information as CSV")
        f.submit(name: "deleteStats", value: "Delete recorded information")
      }
    }
  }
}
