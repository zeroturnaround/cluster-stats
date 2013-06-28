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
    
    h1(_("Jenkins Cluster Statistics"))
    a(href:myRootURL+"/plugin/cluster-stats/") {
      text("Back")
    }
    def throughput = statsData.getWeeklyThroughput();
    raw("""<script>""")
    raw("""var myData = [""")
    raw("""['Week', 'Throughput'],""")
    throughput.each{ k, v ->
      raw("""['W${k}', ${v}],""")
    }
    raw("""];""")
    if (throughput.size() == 0)
      raw("""myData = [['Week','Throughput'],[0,0]]""")
    raw("""</script>""")
    
    raw("""<script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable(myData);

        var options = {
          title: 'Weekly Throughput',
          vAxis: {
            title: "# of builds"
          },
          hAxis: {
            title: "Week #"
          }
        };

        var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
        chart.draw(data, options);
      }</script>""")

    raw("""
        <div id="chart_div" style="width: 900px; height: 500px;"></div>
    """)
  }
}
