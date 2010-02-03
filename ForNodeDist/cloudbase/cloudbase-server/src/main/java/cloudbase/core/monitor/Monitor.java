package cloudbase.core.monitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.Cloudbase;
import cloudbase.core.client.impl.MasterClient;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.master.thrift.Compacting;
import cloudbase.core.master.thrift.TabletRates;
import cloudbase.core.master.thrift.TabletServerStatus;
import cloudbase.core.monitor.thrift.MasterMonitorInfo;
import cloudbase.core.monitor.thrift.MasterMonitorService;
import cloudbase.core.monitor.thrift.TableInfo;
import cloudbase.core.util.EmbeddedWebServer;
import cloudbase.core.util.UtilWaitThread;
import cloudbase.core.master.mgmt.TabletServerState;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.transport.TSocket;

/**
 * Serve master statistics with an embedded web server.
 */
public class Monitor {
	static Logger log = Logger.getLogger(Monitor.class.getName());

	static private class CompactingSummary {
		public int running = 0;
		public int queued = 0;
	}
	static private class MajorMinorStats {
		public CompactingSummary major = new CompactingSummary();
		public CompactingSummary minor = new CompactingSummary();
	}


	static int refreshTime = 5;
	static long lastRecalc = 0L;
	static double totalIngest = 0.0;
	static double totalQuery = 0.0;
	static double totalDisk = 0.0;
	static long totalEntries = 0L; 
	static int onlineTabletCount = 0;

	static volatile boolean fetching = false;
	static MasterMonitorInfo mmi;
	static MasterMonitorHandler mmh = new MasterMonitorHandler();

	static private void addStats(CompactingSummary summary, Compacting stats) {
		if (stats != null) {
			if (stats.compacting)
				summary.running++;
			if (stats.queued)
				summary.queued++;
		}
	}

	static private Map<String, MajorMinorStats> summarizeTableStats(MasterMonitorInfo mmi) {
		Map<String, MajorMinorStats> compactingByTable = new HashMap<String, MajorMinorStats>();
		for (TabletServerStatus status : mmi.tServerInfo) {
			if(status != null && status.tabletRates != null){
				for (TabletRates rates : status.tabletRates) {
					String table = rates.key.getTableName().toString();
					MajorMinorStats current = compactingByTable.get(table);
					if (current == null) {
						current = new MajorMinorStats();
					}
					addStats(current.major, rates.major);
					addStats(current.minor, rates.minor);
					compactingByTable.put(table, current);
				}
			}
		}
		return compactingByTable;
	}


	static private int summarizeTabletStats(TabletServerStatus status, 
			MajorMinorStats stats) {
		int numTablets = 0;
		if(status != null && status.tabletRates != null){
			numTablets = status.tabletRates.size();
			for (TabletRates rates : status.tabletRates) {
				if (rates.minor != null) {
					if (rates.minor.compacting) stats.minor.running++;
					if (rates.minor.queued) stats.minor.queued++;
				}
				if (rates.major != null) {
					if (rates.major.compacting) stats.major.running++;
					if (rates.major.queued) stats.major.queued++;
				}
			}
		}
		return numTablets;
	}


	/**
	 * Compare fields of a TabletServerConnection by name, assuming the fields yield objects with "compareTo"
	 */
	static public class CompareField implements Comparator<TabletServerStatus> {
		String fieldName;

		public CompareField(String fieldName) { this.fieldName = fieldName; }
		public int compare(TabletServerStatus o1, TabletServerStatus o2) {
			try {
				Field getter = o1.getClass().getField(fieldName);
				Object left = getter.get(o1);
				Object right = getter.get(o2);
				Method compareTo = left.getClass().getMethod("compareTo", Object.class);
				int result = (Integer)compareTo.invoke(left, right);
				return result;
			} catch (Exception e) {
				log.error("Unable to compare", e);
				return o1.name.compareTo(o2.name);
			}
		}
		public boolean equal(TabletServerStatus o1, TabletServerStatus o2) {
			try {
				Field getter = o1.getClass().getField(fieldName);
				Object left = getter.get(o1);
				Object right = getter.get(o2);
				return left.equals(right);
			} catch (Exception e) {
				return o1.equals(o2);
			}
		}
	}

	static public class CompareTabletCount implements Comparator<TabletServerStatus> {
		public int compare(TabletServerStatus left, TabletServerStatus right) {
			if (left == null) {
				if (left == right) {
					return 0;
				}
				return -1;
			}
			return left.tabletRates.size() - right.tabletRates.size();
		}
		public boolean equal(TabletServerStatus o1, TabletServerStatus o2) {
			return compare(o1, o2) == 0;
		}
	}

	static public class CompareCompaction implements Comparator<TabletServerStatus> {
		String fieldName;

		public CompareCompaction(String which) {
			this.fieldName = which;
		}
		public int compare(TabletServerStatus left, TabletServerStatus right) {
			MajorMinorStats leftStats = new MajorMinorStats();
			MajorMinorStats rightStats = new MajorMinorStats();
			summarizeTabletStats(left, leftStats);
			summarizeTabletStats(right, rightStats);
			if (fieldName.equals("minor"))
				return leftStats.minor.running + leftStats.minor.queued - rightStats.minor.running - rightStats.minor.queued;
			//else
			return leftStats.major.running + leftStats.major.queued - rightStats.major.running - rightStats.major.queued;
		}
	}


	static private synchronized void fetchData() {
		double totalIngest = 0.;
		double totalQuery = 0.;
		long totalEntries = 0;
		int onlineTabletCount = 0;
		boolean retry = true;

		if (fetching) return;

        // only recalc every so often
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRecalc < refreshTime * 1000) return;

		try {
		    fetching = true;
		    while (retry) {
		        try {
		            mmi = mmh.getMasterStats();
		            retry = false;
		        } catch (TException e) {
		            log.error("Error fetching stats: ", e);
		            UtilWaitThread.sleep(1000);
		        }
		    }

		    for (TabletServerStatus server : mmi.tServerInfo) {   
		        totalIngest += server.ingestRate;
		        totalQuery += server.queryRate;
		        totalEntries += server.totalRecords;
		        if (server.tabletRates != null) {
		            onlineTabletCount += server.tabletRates.size();
		        }
		    }
		    Monitor.totalIngest = totalIngest;
		    Monitor.totalQuery = totalQuery;
		    Monitor.totalEntries = totalEntries;
		    Monitor.onlineTabletCount = onlineTabletCount;
		} finally {
		    fetching = false;
            lastRecalc = currentTime;
		}
	}


	@SuppressWarnings("serial")
	public static class MonitorServlet extends HttpServlet {

		static final TabletServerStatus NO_STATUS = new TabletServerStatus();
		static final Integer ZERO = new Integer(0);

		private String htmlData(HttpServletRequest request) {
			StringBuilder sb = new StringBuilder();
			try {
				boolean highlight = false;

				sb.append("<html><head><META http-equiv=\"refresh\" content=\"" + refreshTime + "\">");
				sb.append("<title>Cloudbase " + CBConstants.VERSION + "</title></head>");
				sb.append("<body>");
				sb.append("<style>");
				sb.append(cloudbase.core.tabletserver.MonitorThread.getStyleSheet());
				sb.append("</style>");
				sb.append("<table width=1000><tr><td><h1 class=header1>Cloudbase " + CBConstants.VERSION + "</h1></td></tr>");
				sb.append("<tr><td><h2 class=header2>Master Reporting Tool</h2></td></tr></table><br><br>");
				sb.append("<table width=1000 align='center'>");
				sb.append("<tr><td class=banner>Master Server Status</td></tr></table>");				

				sb.append("<table align='center'><thead>");
				sb.append("<tr><td></td>");
				sb.append("<td class=thead><div align='center'>Tablet Servers</div></td>");
				sb.append("<td class=thead><div align='center'>Hosted Tablets</div></td>");
				sb.append("<td class=thead><div align='center'>Entries</div></td>");
				sb.append("<td class=thead><div align='center'>Ingest</div></td>");
				sb.append("<td class=thead><div align='center'>Query</div></td>");
				sb.append("</tr></thead>");
				sb.append("<tr><td></td>");
				sb.append("<td><div align='center'>" + commas(mmi.tServerInfo.size()) + "</div></td>");
				sb.append("<td><div align='center'>" + commas(onlineTabletCount) + "</div></td>");
				sb.append("<td><div align='center'>" + commas(totalEntries) + "</div></td>");	    
				sb.append("<td><div align='center'>" + commas(Math.round(totalIngest)) + "</div></td>");
				sb.append("<td><div align='center'>" + commas(Math.round(totalQuery)) + "</div></td>");
				sb.append("</tr></table><br><br><br>");


				sb.append("<table width=1000 align='center'><tr><td class=banner>Tables</td></tr></table><br>");
				sb.append("<table align='center'><tr><td>");
				sb.append("<p>Individual table statistics are listed below.<br></td></tr></table>");
				sb.append("<table align='center'><thead>");
				sb.append("<tr>");
				sb.append("<td class=thead><div align='center'>Table</div></td>");
				sb.append("<td class=thead><div align='center'># Tablets</div></td>");
				sb.append("<td class=thead><div align='center'># Online Tablets</div></td>");
				sb.append("<td class=thead><div align='center'>Entries</div></td>");
				sb.append("<td class=thead><div align='center'>Entries In Memory</div></td>");
				sb.append("<td class=thead><div align='center'>Ingest</div></td>");
				sb.append("<td class=thead><div align='center'>Query</div></td>");
				sb.append("<td class=thead><div align='center'>Major Compactions</div></td>");
				sb.append("</tr></thead>\n");

				SortedMap<String, TableInfo> tableStats = new TreeMap<String, TableInfo>(mmi.tableMap);

				Map<String, MajorMinorStats> compactingByTable = summarizeTableStats(mmi);


				for (Entry<String, TableInfo> entry3 : tableStats.entrySet()) {

					TableInfo tableInfo = entry3.getValue();

					if(highlight)
						sb.append("<tr class=highlight>");
					else
						sb.append("<tr>");


					sb.append("<td><div align='center'>" + entry3.getKey() + "</div></td>");
					sb.append("<td><div align='center'>" + commas(tableInfo.tablets) + "</div></td>");
					sb.append("<td><div align='center'>" + commas(tableInfo.onlineTablets) + "</div></td>");
					sb.append("<td><div align='center'>" + commas(tableInfo.recs) + "</div></td>");
					sb.append("<td><div align='center'>" + commas(tableInfo.recsInMemory) + "</div></td>");
					sb.append("<td><div align='center'>" + commas(Math.round(tableInfo.ingestRate)) + "</div></td>");
					sb.append("<td><div align='center'>" + commas(Math.round(tableInfo.queryRate)) + "</div></td>");

					MajorMinorStats compacting = compactingByTable.get(entry3.getKey());
					int running = 0;
					if (compacting != null)
						running = compacting.major.running;
					sb.append("<td><div align='center'>" + commas(running) + "</div></td>");
					sb.append("</tr>\n");

					highlight = !highlight;


				}


				sb.append("</table>\n");
				sb.append("<p>");

				StringBuffer here = request.getRequestURL();
				String sortDirection = request.getParameter("sortDirection");
				if (sortDirection == null)
					sortDirection = "asc";
				String otherDirection = "desc";
				if (sortDirection.equals("desc"))
					otherDirection = "asc";

				if ( !mmi.badTServers.isEmpty() ) {

					sb.append("<table width=1000 align='center'><tr><td class=banner2>Non-Functioning Tablet Servers</td></tr></table><br>");
                    sb.append("<table align='center'><thead><tr><td colspan='2'>");
					sb.append("The following tablet servers reported a status other than Online.</td></tr>\n");
					sb.append("<tr>");

					sb.append(String.format("<td class='thead'><a href='%s?sortDirection=%s'>Tablet Server</a></td>", here, otherDirection));

					sb.append(String.format("<td class='thead'><a href='%s?sortDirection=%s&tabletSort=tabletStatus'>Tablet Server Status</a></td>", 
							here, otherDirection));
					sb.append("</tr></thead>");

					for (Entry<String,Short> badserver : mmi.badTServers.entrySet()) {
						String badServerName = badserver.getKey();
						Short badServerValue = badserver.getValue(); 

						TabletServerState states[] = TabletServerState.values();

						sb.append("<tr>");
						sb.append("<td><div align='center'>" + badServerName  + "</div></td>");
						sb.append("<td><div align='center'>" + states[badServerValue] + "</div></td>");
						sb.append("</tr>\n");
					}
                    sb.append("</table>\n");
				}

				sb.append("<table width=1000 align='center'><tr><td class=banner>Tablet Server Status</td></tr></table><br>");
				sb.append("<table align='center'><tr><td>");
				sb.append("<p>Individual tablet server statistics are listed below.<br>");
				sb.append("Click on the <font color=blue family=verdana>server address</font> to jump to that server's web service for detailed performance statistics.</p><br>");
				sb.append("</td></tr></table>\n");
				sb.append("<table align='center'><thead>");
				sb.append("<tr>");
				sb.append(String.format("<td class='thead'><a href='%s?sortDirection=%s'>Tablet Server</a></td>", here, otherDirection));
				sb.append(String.format("<td class='thead'><a href='%s?sortDirection=%s&tabletSort=tabletCount'>Hosted Tablets</a></td>", 
						here, otherDirection));
				sb.append(String.format("<td class='thead'><a href='%s?sortDirection=%s&tabletSort=lastContact'>Last Contact</a></td>", 
						here, otherDirection));
				sb.append(String.format("<td class='thead'><a href='%s?sortDirection=%s&tabletSort=totalRecords'>Entries</td>",
						here, otherDirection));
				sb.append(String.format("<td class='thead'><a href='%s?sortDirection=%s&tabletSort=load'>Load</a></td>", 
						here, otherDirection));
				sb.append(String.format("<td class='thead'><a href='%s?sortDirection=%s&tabletSort=ingestRate'>Ingest</a></td>", 
						here, otherDirection));
				sb.append(String.format("<td class='thead'><a href='%s?sortDirection=%s&tabletSort=queryRate'>Query</a></td>", 
						here, otherDirection));
				sb.append(String.format("<td class='thead'><a href='%s?sortDirection=%s&tabletSort=minor'>Minor</a></td>", 
						here, otherDirection));
				sb.append(String.format("<td class='thead'><a href='%s?sortDirection=%s&tabletSort=major'>Major</a></td>", 
						here, otherDirection));
				sb.append(String.format("<td class='thead'><a href='%s?sortDirection=%s&tabletSort=osLoad'>OS Load</a></td>", 
						here, otherDirection));
				sb.append("</tr></thead>");

				highlight = false;

				ArrayList<TabletServerStatus> sorted = new ArrayList<TabletServerStatus>(mmi.tServerInfo);
				String sortType = request.getParameter("tabletSort");
				if (sortType != null) {
					if (sortType.equals("tabletCount")) {
						Collections.sort(sorted, new CompareTabletCount());
					}
					else if (sortType.equals("major") || sortType.equals("minor")) {
						Collections.sort(sorted, new CompareCompaction(sortType));
					} else {
						Collections.sort(sorted, new CompareField(sortType));
					}
				}
				if (sortDirection.equals("desc")) {
					Collections.reverse(sorted);
				}
				log.debug("sorted size is " + sorted.size());
				for (TabletServerStatus status : sorted) {

					if(status == null){
						status = NO_STATUS;
					}

					if(highlight)
						sb.append("<tr class=highlight>");
					else
						sb.append("<tr>");


					sb.append("<td><a href=" + status.URL  +">" + status.name + "</a></td>");
					MajorMinorStats stats = new MajorMinorStats();

					int numTablets = summarizeTabletStats(status, stats);

					sb.append("<td><div align='center'>" + numTablets + "</div></td>");
					sb.append("<td><div align='center'>" + ((System.currentTimeMillis()-status.lastContact)/ 1000.0) + "</div></td>");
					sb.append("<td><div align='center'>" + commas(status.totalRecords) + "</div></td>");

					sb.append("<td><div align='center'>" + String.format("%.2f", status.load) + "</div></td>");
					sb.append("<td><div align='center'>" + commas(Math.round(status.ingestRate)) + "</div></td>");
					sb.append("<td><div align='center'>" + commas(Math.round(status.queryRate)) + "</div></td>");
					sb.append("<td><div align='center'>" + String.format("%d (%d)", stats.minor.running, stats.minor.queued) + "</div></td>");
					sb.append("<td><div align='center'>" + String.format("%d (%d)", stats.major.running, stats.major.queued) + "</div></td>");
					sb.append("<td><div align='center'>" + String.format("%.2f", status.osLoad) + "</div></td>");
					sb.append("</tr>\n");

					highlight = !highlight;
				}

				sb.append("</table>\n");
				sb.append("</body></html>\n"); 
			}
			catch(NumberFormatException E ) {
				log.warn("Problem rendering master web page : "+E.getMessage(), E);			
			}
			catch(Exception E) {
				log.warn("Problem rendering master web page : "+E.getMessage(), E);
			}

			return sb.toString();
		}


		private static String commas(long value) {
			return String.format("%,d", value);
		}


		public void doGet(HttpServletRequest request,
				HttpServletResponse response) throws ServletException,
				IOException {
			try {
				// just print out a page of stats for now
				response.setContentType("text/html");
				PrintWriter pwr = response.getWriter();

				fetchData();
				pwr.print(htmlData(request));
			} catch (Throwable t) {
				log.error("Error processing html request", t);
			}
		}
	}

	public static class MasterMonitorHandler {

		private MasterMonitorService.Client getMasterConnection() {
			try {
				String hostname = MasterClient.lookupMaster();
                int port = CBConfiguration.getInstance().getInt("cloudbase.master.monitor.port", CBConstants.MASTER_MONITOR_PORT_DEFAULT);
				TSocket transport = new TSocket(hostname, port);
				TBinaryProtocol protocol = new TBinaryProtocol(transport);
				MasterMonitorService.Client client = 
					new MasterMonitorService.Client(protocol);
				transport.open();
				return client;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}

		public MasterMonitorInfo getMasterStats() throws TException {
			MasterMonitorService.Client client = getMasterConnection();
			try {
				return client.getMasterStats();
			} finally {
				client.getOutputProtocol().getTransport().close();
			}
		}			
	}



	@SuppressWarnings("serial")
	public static class XMLServlet extends HttpServlet {
		private String xmlData;

		private StringBuilder xml = new StringBuilder();

		private int refreshTime = 2;

		long lastRecalc = 0L;

		private synchronized void rebuildXMLData() {
			double ingest = 0.0;
			double query = 0.0;
			double disk = 0.0;
			long totalEntries = 0L;

			xml.setLength(0);
			xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			xml.append("<stats><servers>");
			SortedMap<String, TableInfo> tableStats = new TreeMap<String, TableInfo>(mmi.tableMap);
			Map<String, MajorMinorStats> compactingByTable = summarizeTableStats(mmi);

			for (TabletServerStatus status : mmi.tServerInfo) {

				xml.append("<server id='" + status.name + "'>");

				xml.append("<ingestrate>" + status.ingestRate	+ "</ingestrate>");
				xml.append("<queryrate>" + status.queryRate + "</queryrate>");
				xml.append("<numentries>" + status.totalRecords	+ "</numentries>");
				xml.append("<lastContact>" + (System.currentTimeMillis()-status.lastContact) + "</lastContact>");

				MajorMinorStats stats = new MajorMinorStats();
				int count = summarizeTabletStats(status, stats);
				xml.append("<compactions><major><running>" + stats.major.running + "</running>" +
						"<queued>" + stats.major.queued + "</queued></major>" +
						"<minor><running>" + stats.minor.running + "</running>" +
						"<queued>" + stats.minor.queued + "</queued></minor></compactions>");
				xml.append("<tablets>" + count + "</tablets>");

				ingest += status.ingestRate;
				query += status.queryRate;
				totalEntries += status.totalRecords;

				xml.append("</server>");
			}
			xml.append("</servers>\n<tables>");
			for (Entry<String, TableInfo> entry : tableStats.entrySet()) {
				TableInfo tableInfo = entry.getValue();

				xml.append("<table>");
				xml.append("<tablename>"+entry.getKey()+"</tablename>");
				xml.append("<tablets>"+tableInfo.tablets+"</tablets>");
				xml.append("<onlineTablets>" + tableInfo.onlineTablets+ "</onlineTablets>");
				xml.append("<recs>" + tableInfo.recs + "</recs>");
				xml.append("<recsInMemory>" + tableInfo.recsInMemory + "</recsInMemory>");
				xml.append("<ingestRate>" + tableInfo.ingestRate + "</ingestRate>");
				xml.append("<queryRate>" + tableInfo.queryRate + "</queryRate>");
				int running = 0;
				int queued = 0;
				MajorMinorStats compacting = compactingByTable.get(entry.getKey());
				if (compacting != null) {
					running = compacting.major.running;
					queued = compacting.major.queued;
				}
				xml.append("<majorCompactions>" +
						"<running>" + running + "</running>" +
						"<queued>" + queued + "</queued>" +
				"</majorCompactions>");
				xml.append("</table>");
			}
			xml.append("</tables>\n<totals>");
			xml.append("<ingestrate>" + ingest + "</ingestrate>");
			xml.append("<queryrate>" + query + "</queryrate>");
			xml.append("<diskrate>" + disk + "</diskrate>");
			xml.append("<numentries>" + totalEntries	+ "</numentries>");
			xml.append("</totals></stats>");

			xmlData = xml.toString();
		}

		public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			try {
				response.setContentType("text/xml");
				PrintWriter pwr = response.getWriter();
                fetchData();
                rebuildXMLData();
				pwr.print(xmlData);
			} catch (Throwable t) {
				log.error("Error fetching xml", t);
			}
		}
	}


	public static void main(String[] args) throws UnknownHostException {
		Cloudbase.init("//conf//monitor_logger.ini");

		int port = CBConfiguration.getInstance().getInt("cloudbase.monitor.port", 50095);
		EmbeddedWebServer server;
		try {
			server = EmbeddedWebServer.create(port);
		} catch (Throwable ex) {
			log.error("Unable to start embedded web server", ex);
			return;
		}
		server.addServlet(MonitorServlet.class, "/monitor");
		server.addServlet(XMLServlet.class, "/xml");

		server.start();
	}
}

