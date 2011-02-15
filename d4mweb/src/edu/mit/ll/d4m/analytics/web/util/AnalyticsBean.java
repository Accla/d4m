/**
 * 
 */
package edu.mit.ll.d4m.analytics.web.util;

/**
 * @author cyee
 *
 */
public class AnalyticsBean {
	private String ColumnClutter="";
	private String ColumnSeed="";
	private String ColumnType="";
	private String FilterThreshold="";
	private String FilterWidth="";
	private String GraphDepth="";
	private String LatPoints="";
	private String LonPoints="";
	private String TimeRange="";
	public String getColumnClutter() {
		return ColumnClutter;
	}
	public void setColumnClutter(String columnClutter) {
		ColumnClutter = columnClutter;
	}
	public String getColumnSeed() {
		return ColumnSeed;
	}
	public void setColumnSeed(String columnSeed) {
		ColumnSeed = columnSeed;
	}
	public String getColumnType() {
		return ColumnType;
	}
	public void setColumnType(String columnType) {
		ColumnType = columnType;
	}
	public String getFilterThreshold() {
		return FilterThreshold;
	}
	public void setFilterThreshold(String filterThreshold) {
		FilterThreshold = filterThreshold;
	}
	public String getFilterWidth() {
		return FilterWidth;
	}
	public void setFilterWidth(String filterWidth) {
		FilterWidth = filterWidth;
	}
	public String getGraphDepth() {
		return GraphDepth;
	}
	public void setGraphDepth(String graphDepth) {
		GraphDepth = graphDepth;
	}
	public String getLatPoints() {
		return LatPoints;
	}
	public void setLatPoints(String latPoints) {
		LatPoints = latPoints;
	}
	public String getLonPoints() {
		return LonPoints;
	}
	public void setLonPoints(String lonPoints) {
		LonPoints = lonPoints;
	}
	public String getTimeRange() {
		return TimeRange;
	}
	public void setTimeRange(String timeRange) {
		TimeRange = timeRange;
	}
	/**
	 * 
	 */
	public AnalyticsBean() {
		// TODO Auto-generated constructor stub
	}

	public String toCSVstring() {
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		sb2.append(",");
		sb.append("CSVstring:\",");
		if(this.ColumnClutter.length() > 0) {
			sb.append("ColumnClutter");
			sb2.append(this.ColumnClutter);
		}
		if(this.ColumnSeed.length() > 0) {
			sb.append(",");
			sb.append("ColumnSeed");
			sb2.append(",").append(this.ColumnSeed);
		}
		
		if(this.ColumnType.length() > 0) {
			sb.append(",").append("ColumnType");
			sb2.append(",").append(this.ColumnType);
		}
		
		if(this.FilterThreshold.length() > 0) {
			sb.append(",").append("FilterThreshold");
			sb2.append(",").append(this.FilterThreshold);
		}
		if(this.FilterWidth.length() >0) {
			sb.append(",").append("FilterWidth");
			sb2.append(",").append(this.FilterWidth);
		}
		if(this.GraphDepth.length() > 0) {
			sb.append(",").append("GraphDepth");
			sb2.append(",").append(this.GraphDepth);
		}
		if(this.LatPoints.length() > 0 ) {
			sb.append(",").append("LatPoints");
			sb2.append(",").append(this.LatPoints);
		}
		if(this.LonPoints.length() > 0) {
			sb.append(",").append("LonPoints");
			sb2.append(",").append(this.LonPoints);
		}
		if(this.TimeRange.length() > 0) {
			sb.append(",").append("TimeRange");
			sb2.append(",").append(this.TimeRange);
		}
		
		sb.append("\n");
		sb2.append("\n\"");
		return sb.append(sb2.toString()).toString();
	}
}
