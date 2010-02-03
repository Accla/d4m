package cloudbase.core.util;

import java.util.Comparator;

import cloudbase.core.master.thrift.TabletRates;

public class TabletFunctions {
	
	// Tablet load is defined by 5*ingest + query rate + 1
	// Ingest is more taxing then query
	// 1 ensures some default distribution if no query/ingest on the system
	public static double getLoad(TabletRates tablet) {
		return getLoad(tablet.tableIngestRate, tablet.tableQueryRate);
	}
	
	public static double getLoad(double ingest, double query) {
		return 3*ingest + query + 1;
	}
	
	public static class TabletRatesComparator implements Comparator<TabletRates> {

		public int compare(TabletRates o1, TabletRates o2) {
			return o1.key.compareTo(o2.key);
		}
	}
	
}
