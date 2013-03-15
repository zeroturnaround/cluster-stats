// Namespaces
st = namespace("jelly:stapler")
j = namespace("jelly:core")
i = namespace("jelly:fmt")
t = namespace("/lib/hudson")
f = namespace("/lib/form")
d = namespace("jelly:define")

import java.text.Normalizer.Form;

import hudson.Util
import lib.LayoutTagLib
import org.zeroturnaround.stats.model.StatsData
import org.zeroturnaround.stats.ClusterStatisticsPlugin

def l=namespace(LayoutTagLib.class)
def StatsData statsData = my.statsData;

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
      b(Util.getTimeSpanString(statsData.getAvgDurationPastWeek()))
      text(" (last 7 days) ")
      raw(""" <a href="#" onClick="toggleNodeView('buildNodes');return false;">show by Node</a>""")
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
    
    h2("Wait time in queue")
    div() {
      text("Average wait time: ")
      b(Util.getTimeSpanString(statsData.getAvgWait()))
      text(" (all time) ")
      b(Util.getTimeSpanString(statsData.getAvgWaitPastWeek()))
      text(" (last 7 days) ")
      
      raw(""" <a href="#" onClick="toggleNodeView('waitNodes');return false;">show by Node</a>""")
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
    h2("Throughput")
    div() {
        text("""All time: ${statsData.getAvgThroughputHour()} jobs/hour, 
              ${statsData.getAvgThroughputDay()} jobs/day, 
              ${statsData.getAvgThroughputWeek()} jobs/week""")
        br()
        text("""Last 7 days: ${statsData.getAvgThroughputHourPastWeek()} jobs/hour,
                ${statsData.getAvgThroughputDayPastWeek()} jobs/day,
                ${statsData.getAvgThroughputWeekPastWeek()} jobs/week""")    
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
