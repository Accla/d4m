package cloudbase.core.tabletserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.util.EmbeddedWebServer;

public class MonitorThread implements Runnable {

	private static Logger log = Logger.getLogger(MonitorThread.class.getName());

	private static String styleSheet = null;
	
	public static synchronized String getStyleSheet()	{
	    if (styleSheet != null) return styleSheet;
	    styleSheet = "";
	    byte[] buffer = new byte[1024];
	    InputStream is = MonitorThread.class.getClassLoader().getResourceAsStream("monitor.css");
	    if (is != null) {
	        while (true ) {
	            int count = 0;
	            try {
	                count = is.read(buffer);
	            } catch (IOException e) {
	                log.error("Unable to read monitorThread stylesheet");
	            }
	            if (count < 0) break;
	            styleSheet += new String(buffer, 0, count);
	        }
	    } else {
	        log.error("Unable to load monitor.css");
	    }
	    return styleSheet;
	}

	@SuppressWarnings("serial")
	public static class MonitorServlet extends HttpServlet {
		public int refreshTime = 5;
		public long lastRecalc = 0L;				
		public String htmlData;
		String styleDisplay ="";

		public StringBuilder htmlDataBuilder = new StringBuilder();

		private synchronized void rebuildHTMLData() {
			try {
				StringBuilder forLater = new StringBuilder();
				boolean highlight = false;
				MathContext myContext = new MathContext(3);

				double minorElapsed = 0;
				int minorNum = 0;							
				double minorSumDev = 0;
				double minorQueueTime = 0;
				double totalElapsedForAll = 0;
				int statusMinor = 0;
				int statusMajor = 0;
				int totalEntries = 0;
				double minorStdDev = 0;

				double splitStdDev = 0;
				double migrateStdDev = 0;
				double minorQueueSumDev = 0;
				double minorQueueStdDev = 0;

				double majorElapsed = 0;
				int majorNum = 0;
				double majorQueueTime = 0;
				double majorQueueSumDev = 0;
				double majorSumDev = 0;
				double majorStdDev = 0;

				double majorQueueStdDev = 0;

				double currentMajorAvg = 0;
				double currentMinorAvg = 0;
				double currentMajorStdDev = 0;
				double currentMinorStdDev = 0;

				int minorFailure = 0;
				int majorFailure = 0;


				TreeMap<KeyExtent, Tablet> statsSnapshot = new TreeMap<KeyExtent, Tablet>();
				instance.getStats(statsSnapshot);

				for (Tablet stats : statsSnapshot.values()) {															
					if(!(stats.getExtent()==null)) {		
						totalEntries += stats.getNumEntries();

						minorElapsed += stats.timer.minorElapsed;
						minorNum += stats.timer.minorNum;
						minorQueueTime += stats.timer.minorQueueTime;
						minorQueueSumDev += stats.timer.minorQueueSumDev;
						minorSumDev += stats.timer.minorSumDev;
						statusMinor += stats.timer.statusMinor;

						majorElapsed += stats.timer.majorElapsed;
						majorNum += stats.timer.majorNum;
						majorQueueTime += stats.timer.majorQueueTime;
						majorQueueSumDev += stats.timer.majorQueueSumDev;
						majorSumDev += stats.timer.majorSumDev;
						statusMajor += stats.timer.statusMajor;

						minorFailure += stats.timer.minorFail;
						majorFailure += stats.timer.majorFail;

						if(highlight)
							forLater.append("<tr class=highlight>");
						else
							forLater.append("<tr>");
						String url = URLEncoder.encode(stats.getExtent().toString(), "utf8");
						// TODO: insufficient quoting
						String html = stats.getExtent().toString().replaceAll("<", "&lt;");
						forLater.append("<td><p><font color=black><a href='/ti?tabletExtent=" + url + "'>" + html + "</a></font></p></td><td>" + String.format("%,d", stats.getNumEntries()) + "</td>");
						if(stats.timer.minorNum != 0) {
							forLater.append("<td>" + new BigDecimal(stats.timer.minorElapsed/stats.timer.minorNum, myContext) + " s</td><td>" + new BigDecimal(Math.sqrt((1/(stats.timer.minorElapsed/stats.timer.minorNum))*(stats.timer.minorSumDev - (stats.timer.minorNum * Math.pow((stats.timer.minorElapsed/stats.timer.minorNum), 2)))), myContext) + " s</td>");
							forLater.append("<td>"+String.format("%,d",(int)(stats.timer.minorCount / stats.timer.minorElapsed))+"</td>");
						}
						else
							forLater.append("<td></td><td></td><td></td>");
						if(stats.timer.majorNum != 0){
							forLater.append("<td>" + new BigDecimal(stats.timer.majorElapsed/stats.timer.majorNum, myContext) + " s</td><td>" + new BigDecimal(Math.sqrt((1/(stats.timer.majorElapsed/stats.timer.majorNum))*(stats.timer.majorSumDev - (stats.timer.majorNum * Math.pow((stats.timer.majorElapsed/stats.timer.majorNum), 2)))), myContext) + " s</td>");
							forLater.append("<td>"+String.format("%,d",(int)(stats.timer.majorCount / stats.timer.majorElapsed))+"</td>");
						}else
							forLater.append("<td></td><td></td><td></td>");
					}

					if(highlight==false)
						highlight=true;
					else
						highlight=false;					
				}									

				//Calculate current averages before adding in historical data
				if(minorNum != 0)
					currentMinorAvg = minorElapsed/minorNum;
				if(minorElapsed != 0 && minorNum != 0)
					currentMinorStdDev = Math.sqrt((1/(minorElapsed/minorNum))*(minorSumDev - (minorNum * Math.pow((minorElapsed/minorNum), 2))));

				if(majorNum != 0) 
					currentMajorAvg = majorElapsed/majorNum;
				if(majorElapsed != 0 && majorNum != 0)
					currentMajorStdDev = Math.sqrt((1/(majorElapsed/majorNum))*(majorSumDev - (majorNum * Math.pow((majorElapsed/majorNum), 2))));


				//After these += operations, these variables are now total for current tablets and historical tablets
				TabletTimer timer = instance.getTimer();
				minorElapsed += timer.minorElapsed;
				minorNum += timer.minorNum;
				minorQueueTime += timer.minorQueueTime;	
				minorQueueSumDev += timer.minorQueueSumDev;
				minorSumDev += timer.minorSumDev;

				majorElapsed += timer.majorElapsed;
				majorNum += timer.majorNum;
				majorQueueTime += timer.majorQueueTime;
				majorQueueSumDev += timer.majorQueueSumDev;
				majorSumDev += timer.majorSumDev;

				minorFailure += timer.minorFail;
				majorFailure += timer.majorFail;

				totalElapsedForAll += majorElapsed + timer.splitElapsed + timer.migrateElapsed + minorElapsed;

				if(minorNum != 0)
					minorStdDev = Math.sqrt((1/(minorElapsed/minorNum))*(minorSumDev - (minorNum * Math.pow((minorElapsed/minorNum), 2))));
				if(minorQueueTime != 0 && minorNum != 0)
					minorQueueStdDev = Math.sqrt((1/(minorQueueTime/minorNum))*(minorQueueSumDev - (minorNum * Math.pow((minorQueueTime/minorNum), 2))));

				if(majorNum != 0)
					majorStdDev =  Math.sqrt((1/(majorElapsed/majorNum))*(majorSumDev - (majorNum * Math.pow((majorElapsed/majorNum), 2))));
				if(majorQueueTime != 0 && majorNum != 0)
					majorQueueStdDev =  Math.sqrt((1/(majorQueueTime/majorNum))*(majorQueueSumDev - (majorNum * Math.pow((majorQueueTime/majorNum), 2))));

				if(timer.splitNum != 0 && timer.splitElapsed != 0)
					splitStdDev =  Math.sqrt((1/(timer.splitElapsed/timer.splitNum))*(timer.splitSumDev - (timer.splitNum * Math.pow((timer.splitElapsed/timer.splitNum), 2))));
				if(timer.migrateNum != 0 && timer.migrateElapsed != 0)
					migrateStdDev = Math.sqrt((1/(timer.migrateElapsed/timer.migrateNum))*(timer.migrateSumDev - (timer.migrateNum * Math.pow((timer.migrateElapsed/timer.migrateNum), 2))));


				htmlDataBuilder.setLength(0);
				htmlDataBuilder.append("<html><head><META http-equiv=\"refresh\" content=\"" + refreshTime + "\">");
				htmlDataBuilder.append("<title>Cloudbase " + CBConstants.VERSION + "</title>");
				htmlDataBuilder.append("<script language=javascript>");
				htmlDataBuilder.append("function toggle(id) {");
				htmlDataBuilder.append("target = document.all(id);");
				htmlDataBuilder.append("if(target.style.display=='none')");
				htmlDataBuilder.append(" target.style.display='';");
				htmlDataBuilder.append("else");
				htmlDataBuilder.append(" target.style.display='none';} </script></head>");
				htmlDataBuilder.append("<body>");
				htmlDataBuilder.append("<style>");
				htmlDataBuilder.append(getStyleSheet());
				htmlDataBuilder.append("</style>");
                htmlDataBuilder.append("<table width=1000><tr><td><h1 class=header1>Cloudbase " + CBConstants.VERSION + "</h1></td></tr>");
				htmlDataBuilder.append("<td><h2 class=header2>Tablet Server Reporting Tool</h2></td></tr></table><br>");

				htmlDataBuilder.append("<table width=1000 align=center>");
				htmlDataBuilder.append("<tr><td class=banner>Tablet Server Status</td></tr></table>");
				htmlDataBuilder.append("<table align=center><thead>");
				htmlDataBuilder.append("<tr><td></td>");
				htmlDataBuilder.append("<td class=thead>Hosted Tablets</td>");
				htmlDataBuilder.append("<td class=thead>Entries</td>");
				htmlDataBuilder.append("</tr></thead>");
				htmlDataBuilder.append("<tr><td></td>");
				htmlDataBuilder.append("<td>" + statsSnapshot.size()+ "</td>");
				htmlDataBuilder.append("<td>" + totalEntries + "</td>");
				htmlDataBuilder.append("</tr></table><br><br>");

				htmlDataBuilder.append("<table align=center><thead>");					
				htmlDataBuilder.append("<tr><td class=thead>Minor Compacting</td>");
				htmlDataBuilder.append("<td class=thead>Major Compacting</td>");
				htmlDataBuilder.append("<td class=thead>Splitting</td>");		
				htmlDataBuilder.append("<td class=thead>Migrating</td>");	
				htmlDataBuilder.append("</thead></tr>");
				htmlDataBuilder.append("<tr><td>" + statusMinor + "...</td><td>" + statusMajor + "...</td><td>" + timer.statusSplit + "...</td><td>" + timer.statusMigrate + "...</td></tr></table><br><br>");

				htmlDataBuilder.append("<table width=1000 align=center><tr><td class=banner>All-Time Tablet Operation Results</td></tr></table>");	

				htmlDataBuilder.append("<table align=center><thead>");
				htmlDataBuilder.append("<tr><td class=thead>Operation</td>");
				htmlDataBuilder.append("<td class=thead>Queue Time Avg</td>");
				htmlDataBuilder.append("<td class=thead>Queue Time Std Dev</td>");				

				htmlDataBuilder.append("</thead></tr>");

				if(majorNum != 0) {
					htmlDataBuilder.append("<tr><td>Major Compaction</td><td>" + new BigDecimal(majorQueueTime/majorNum, myContext) + " s</td>");						
					htmlDataBuilder.append("<td>" + new BigDecimal(majorQueueStdDev, myContext)+ " s</td>");
				}
				else					
					htmlDataBuilder.append("<tr><td>Major Compaction</td><td></td><td></td>");	



				if(minorNum != 0) {
					htmlDataBuilder.append("<tr class=highlight><td>Minor Compaction</td><td>" + new BigDecimal(minorQueueTime/minorNum, myContext) + " s</td>");
					htmlDataBuilder.append("<td>" +  new BigDecimal(minorQueueStdDev, myContext) + " s</td></table>");							
				}
				else									
					htmlDataBuilder.append("<tr class=highlight><td>Minor Compaction</td><td></td><td></td>");			

				htmlDataBuilder.append("<table align=center><thead><tr><td class=thead>Operation</td>");
				htmlDataBuilder.append("<td class=thead>Success</td>");	
				htmlDataBuilder.append("<td class=thead>Failure</td>");	
				htmlDataBuilder.append("<td class=thead>Average Time</td>");		
				htmlDataBuilder.append("<td class=thead>Std Deviation</td>");
				htmlDataBuilder.append("<td class=thead>Percentage Time Spent</td>");
				htmlDataBuilder.append("</tr></thead>");

				htmlDataBuilder.append("<tr><td>Split</td>");
				htmlDataBuilder.append("<td>" + timer.splitNum + "</td>");	
				htmlDataBuilder.append("<td>" + timer.splitFail + "</td>");	
				if(timer.splitNum != 0) {
					htmlDataBuilder.append("<td>" + new BigDecimal(timer.splitElapsed/timer.splitNum, myContext) + " s</td>");
					htmlDataBuilder.append("<td>" + new BigDecimal(splitStdDev, myContext)+ " s</td>");				    	
				}
				else
					htmlDataBuilder.append("<td></td><td></td>");						    

				htmlDataBuilder.append("<td><div align=left>" + getChartOperations(timer.splitElapsed, totalElapsedForAll) + "</div></td></tr>");

				htmlDataBuilder.append("<tr class=highlight><td>Migration</td>");
				htmlDataBuilder.append("<td>" + timer.migrateNum + "</td>");
				htmlDataBuilder.append("<td>" + timer.migrateFail + "</td>");
				if(timer.migrateNum != 0) {				    	
					htmlDataBuilder.append("<td>" + new BigDecimal(timer.migrateElapsed/timer.migrateNum, myContext) + " s</td>");	
					htmlDataBuilder.append("<td>" + new BigDecimal(migrateStdDev, myContext) + " s</td>");
				}
				else				    	
					htmlDataBuilder.append("<td></td><td></td>");					

				htmlDataBuilder.append("<td><div align=left>" + getChartOperations(timer.migrateElapsed, totalElapsedForAll) + "</div></td></tr>");

				htmlDataBuilder.append("<tr><td>Major Compaction</td>");
				htmlDataBuilder.append("<td>" + majorNum + "</td>");	
				htmlDataBuilder.append("<td>" + majorFailure + "</td>");	
				if(majorNum != 0) {
					htmlDataBuilder.append("<td>" + new BigDecimal(majorElapsed/majorNum, myContext) + " s</td>");	
					htmlDataBuilder.append("<td>" + new BigDecimal(majorStdDev, myContext) + " s</td>");
				}
				else				    					    		
					htmlDataBuilder.append("<td></td><td></td>");	

				htmlDataBuilder.append("<td><div align=left>" + getChartOperations(majorElapsed, totalElapsedForAll) + "</div></td></tr>");

				htmlDataBuilder.append("<tr class=highlight><td>Minor Compaction</td>");
				htmlDataBuilder.append("<td>" + minorNum + "</td>");		
				htmlDataBuilder.append("<td>" + minorFailure + "</td>");	
				if(minorNum != 0) {
					htmlDataBuilder.append("<td>" + new BigDecimal(minorElapsed/minorNum, myContext) + " s</td>");
					htmlDataBuilder.append("<td>" + new BigDecimal(minorStdDev, myContext) + " s</td>");
				}
				else			    	
					htmlDataBuilder.append("<td></td><td></td>");	

				htmlDataBuilder.append("<td><div align=left>" + getChartOperations(minorElapsed, totalElapsedForAll) + "</div></td></tr>");

				htmlDataBuilder.append("</table><br>");

				htmlDataBuilder.append("<br><table width=1000 align=center><tr><td class=banner>Current Tablet Operation Results</td></table>");

				htmlDataBuilder.append("<table align=center><thead>");					
				htmlDataBuilder.append("<tr>");
				htmlDataBuilder.append("<td class=thead>Minor Avg</td>");;
				htmlDataBuilder.append("<td class=thead>Minor Std Dev</td>");
				htmlDataBuilder.append("<td class=thead>Major Avg</td>");		
				htmlDataBuilder.append("<td class=thead>Major Std Dev</td>");	
				htmlDataBuilder.append("</tr></thead>");

				htmlDataBuilder.append("<tr><td>" + new BigDecimal(currentMinorAvg, myContext) + " s</td><td>" + new BigDecimal(currentMinorStdDev, myContext) + " s</td><td>" + new BigDecimal(currentMajorAvg, myContext) + " s</td><td>" + new BigDecimal(currentMajorStdDev, myContext) + " s<td></td></table><br><br>");

				htmlDataBuilder.append("<table align=center><thead>");
				htmlDataBuilder.append("<tr><td class=thead>Tablet</td>");
				htmlDataBuilder.append("<td class=thead>Entries</td><td class=thead>Minor Avg</td>");
				htmlDataBuilder.append("<td class=thead>Minor Std Dev</td>");
				htmlDataBuilder.append("<td class=thead>Minor Avg e/s</td>");
				htmlDataBuilder.append("<td class=thead>Major Avg</td>");		
				htmlDataBuilder.append("<td class=thead>Major Std Dev</td>");
				htmlDataBuilder.append("<td class=thead>Major Avg e/s</td>");	
				htmlDataBuilder.append("</tr></thead>");

				htmlDataBuilder.append(forLater.toString());		

				htmlDataBuilder.append("</table><br><br><br><br></body>");
				htmlDataBuilder.append("</html>");
			}
			catch(NumberFormatException E ) {						
				//log.error(E.getMessage(), E);
			}
			catch(Exception E) {						
				//log.error(E.getMessage(), E);
			}
			htmlData = htmlDataBuilder.toString();
		}

		private String getChartOperations(double num, double total) {
			String toReturn = "";
			double percent = 0;
			if(total != 0) 		
				percent = (num/total)*100;

			toReturn += "<TABLE BORDER=0 CELLSPACING=0 CELLPADDING=2>";				
			toReturn += "<TD valign=middle>";

			toReturn += "<TABLE><TR><TD bgcolor=blue";

			toReturn += "><IMG width=";
			if(percent < 1 && percent > 0)
				toReturn += "1";
			else
				toReturn += String.valueOf(percent);

			toReturn += " height=1></TD>";
			toReturn += "<TD><FONT SIZE=2>";
			if(percent < 1 && percent > 0)
				toReturn += "<1";
			else
				toReturn += String.valueOf((int)percent);

			toReturn += " %</FONT></TD></TR></TABLE>";
			toReturn += "</TD></TR>";
			toReturn += "</TABLE>";	


			return toReturn;
		}	

		public void doGet(HttpServletRequest request,
				HttpServletResponse response) throws ServletException,
				IOException {


			// just print out a page of stats for now
			response.setContentType("text/html");
			PrintWriter pwr = response.getWriter();
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastRecalc > refreshTime * 1000) {
				// recalculate xml data
				rebuildHTMLData();
				lastRecalc = currentTime;
			}

			pwr.print(htmlData);
		}
	}


	@SuppressWarnings("serial")
	public static class TabletInspectorServlet extends HttpServlet {
		String htmlData;
		StringBuilder htmlDataBuilder = new StringBuilder();
		private int refreshTime = 2;
		long lastRecalc = 0L;
		String tabletString;

		private synchronized void rebuildHtmlData() {
			htmlDataBuilder.setLength(0);
			htmlDataBuilder.append("<html><head><META http-equiv=\"refresh\" content=\"" + refreshTime + "\">");
			htmlDataBuilder.append("<title>Cloudbase " + CBConstants.VERSION + "</title>");
			htmlDataBuilder.append("<script language=javascript>");
			htmlDataBuilder.append("function toggle(id) {");
			htmlDataBuilder.append("target = document.all(id);");
			htmlDataBuilder.append("if(target.style.display=='none')");
			htmlDataBuilder.append(" target.style.display='';");
			htmlDataBuilder.append("else");
			htmlDataBuilder.append(" target.style.display='none';} </script></head>");
			htmlDataBuilder.append("<body>");
			htmlDataBuilder.append(getStyleSheet());
			htmlDataBuilder.append("<table width=1000><tr><td><h1 class=header1>Cloudbase " + CBConstants.VERSION + "</h1></td></tr>");
			htmlDataBuilder.append("<td><h2 class=header2><b>Tablet Reporting Tool</b></h2></td></tr>");
			htmlDataBuilder.append("<table width=1000>");
			htmlDataBuilder.append("<tr><td class=banner>Tablet Status</td></tr></table>");
			htmlDataBuilder.append("<table width=1000><tr><td><center>");
			htmlDataBuilder.append("<p>Reporting for tablet: " + tabletString +"</p>");
			htmlDataBuilder.append("</td></tr>");

			htmlDataBuilder.append("<center><tr><td class=banner>Tablet Event History</td></tr>");
			htmlDataBuilder.append("</body></html>");

			htmlData = htmlDataBuilder.toString();
		}

		public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			response.setContentType("text/html");
			PrintWriter pwr = response.getWriter();

			tabletString = request.getParameter("tabletExtent");

			long currentTime = System.currentTimeMillis();
			if(currentTime - lastRecalc > refreshTime * 1000) {

				rebuildHtmlData();
				lastRecalc = currentTime;
			}
			pwr.print(htmlData);
		}
	}

	static TabletServer instance;

	public MonitorThread(TabletServer ins) {
		instance = ins;
	}

	EmbeddedWebServer mt;
	InetSocketAddress getAddress() {
		return new InetSocketAddress(TabletServer.getMasterAddress().getAddress(), mt.getPort());
	}
	
	public void run() {
	    try {
	        mt = EmbeddedWebServer.create();
	    } catch (Throwable t) {
	        log.error("Unable to starte embedded web server", t);
	        return;
	    }
	    mt.addServlet(MonitorServlet.class, "/monitor");
	    mt.addServlet(TabletInspectorServlet.class, "/ti");
	    mt.start();		
	}
	
	public void stopServer() {
		try {
			mt.stop();
		} catch (Exception e) {
			log.error("Error stopping monitor thread", e);
		}
	}
}
