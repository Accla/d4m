/**
 * 
 */
package edu.mit.ll.d4m.analytics.web;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.Resource;
import org.apache.log4j.lf5.util.ResourceUtils;

import edu.mit.ll.d4m.analytics.web.util.ColumnValuePair;
/**
 * @author cyee
 *
 *  1. Parse json
 *  2. Determine type of json
 *       list of analytics
 *       response to a query
 *       
 *  3. Create HTML for a drop-down list of analytics
 *  4. Create HTML for query textfields based on a particular analytics
 */
public class D4mAnalyticsListModel {
	private static Logger log = Logger.getLogger(D4mAnalyticsListModel.class);
	//map to analytics and their fields/parameters
	private ConcurrentHashMap<String, ArrayList<ColumnValuePair>>  analyticsMap=null;
	private static D4mAnalyticsListModel instance=null;
	private String fieldNames=null;
	private boolean testing=false;
	public static String GET_ANALYTICS_LIST="GetAnalyticsList";
	public static String GET_ANALYTICS_FORM="GetAnalyticsQueryForm";
	public static String GET_ANALYTICS_RESPONSE="GetAnalyticsResponse";
	public static String COL_ColumnClutter= "ColumnClutter";
	public static String COL_ColumnSeed= "ColumnSeed";
	public static String COL_ColumnType= "ColumnType";
	public static String COL_FilterThreshold= "FilterThreshold";
	public static String COL_FilterWidth= "FilterWidth";
	public static String COL_GraphDepth= "GraphDepth";
	public static String COL_LatPoints= "LatPoints";
	public static String COL_LonPoints= "LonPoints";
	public static String COL_TimeRange= "TimeRange";

	public static String [] COL_FIELDS={COL_ColumnClutter,COL_ColumnSeed,
		COL_ColumnType, COL_FilterThreshold, COL_FilterWidth, COL_GraphDepth,
		COL_LatPoints, COL_LonPoints, COL_TimeRange};


	private ConcurrentHashMap<String,String> testDataMap=null;
	/**
	 * 
	 */
	private D4mAnalyticsListModel() {
		analyticsMap = new ConcurrentHashMap<String, ArrayList<ColumnValuePair>>();
		String testFlag=System.getProperty("testing");
		if(testFlag != null) {
			testing = Boolean.parseBoolean(testFlag);
		}
		if(testing) {
			loadAnalyticsList();
			loadTestAnalyticsResponse();
		}
	}

	public static D4mAnalyticsListModel getInstance() {
		if(instance == null ) {
			instance = new D4mAnalyticsListModel();
		}
		return instance;
	}

	public String formQueryForAnalyticsList() {

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", "QueryRequest");
		map.put("rowSeparator", "\n");
		map.put("columnSeparator", ",");
		map.put("Nrows", 2);
		map.put("Ncolumns", 2);
		map.put("Nentries", 3);
		map.put("CSVstring", ",Col1\nGetAnalysisDefaults,1");

		JSONObject json= new JSONObject(map);

		return json.toString();
	}
	private void loadAnalyticsList() {
		String filename = "D4mAnalyticsList.txt";
		Resource resource = new Resource(filename);
		URL url = resource.getURL();
		try {
			BufferedReader br = new BufferedReader(new FileReader(url.getFile()));
			StringBuffer sb = new StringBuffer();
			String json = null;
			while ((json = br.readLine()) != null) {
				sb.append(json);
			}

			//Put it in the map
			parseList(sb.toString());
		} catch (FileNotFoundException e) {
			log.warn(e);
		}
		catch (IOException e) {
			log.warn(e);
		} catch (JSONException e) {
			log.warn(e);
		}

	}

	private void loadTestAnalyticsResponse() {
		String filename = "D4mStatsTypeCountResponse.txt";
		Resource resource = new Resource(filename);
		URL url = resource.getURL();
		try {
			BufferedReader br = new BufferedReader(new FileReader(url.getFile()));
			StringBuffer sb = new StringBuffer();
			String json = null;
			while ((json = br.readLine()) != null) {
				sb.append(json);
			}

			//Put it in the map
			this.testDataMap = new  ConcurrentHashMap<String, String>();
			Set<String> keys = this.analyticsMap.keySet();
			Iterator<String> itKeys = keys.iterator();
			while(itKeys.hasNext()) {
				String key = itKeys.next();
				this.testDataMap.put(key, sb.toString());
			}

		} catch (FileNotFoundException e) {
			log.warn(e);
		}
		catch (IOException e) {
			log.warn(e);
		} 

	}

	public String getTestResponse(String key) 
	{
		String retval = this.testDataMap.get(key);
		return retval;
	}

	/*
	 * ParseList method is used to organize the AnalyticsList
	 * into a HashMap.
	 * The key is the analytics name
	 * The value is an ArrayList of the relevant fields.
	 * 
	 */
	public void parseList(String json) throws JSONException {

		JSONObject jobj = new JSONObject(json);

		String cvs = jobj.getString("CSVstring");
		log.info(cvs);
		String [] csvRowSplit = cvs.split("\n");
		fieldNames = csvRowSplit[0];
		String [] fieldNameSplit = fieldNames.split(",");
		for (int i = 1; i < csvRowSplit.length; i++) {
			log.info(i+".  "+csvRowSplit[i]);
			String [] rowSplit = csvRowSplit[i].split(",");
			String analyticsKey = rowSplit[0];
			ArrayList<ColumnValuePair> map  = new ArrayList<ColumnValuePair>();
			for(int j=1; j < rowSplit.length;j++) {
				int size = rowSplit[j].length();
				if (size > 0) {
					log.info("KEY ="+fieldNameSplit[j]+", VAL= "+ rowSplit[j]);
					map.add(new ColumnValuePair(fieldNameSplit[j], rowSplit[j]));
				}
			}
			log.info("ANALYTICS KEY="+analyticsKey);
			this.analyticsMap.put(analyticsKey, map);

		}
		log.info("MAP SIZE ="+this.analyticsMap.size());

	}
	public ArrayList<ColumnValuePair> getColumnValuePairs(String key) {
		return this.analyticsMap.get(key);
	}

	/*
	 * This method will make the drop-down menu of Analytics
	 */
	public String toAnalyticsSelectHtml() {
		String retval=null;
		StringBuffer sb = new StringBuffer();
		sb.append("<p/>");
		sb.append("Choose an Analytics:");
		sb.append("<p/>");
		sb.append("<select id=\"analyticsListSelect\"  name=\"analytics\" onchange=\"getAnalyticsQueryForm(this.value)\">").append("\n");
		Set<String> keys = this.analyticsMap.keySet();
		Iterator<String> itKeys = keys.iterator();
		while(itKeys.hasNext()) {
			String key = itKeys.next();
			sb.append("<option value=\"");
			sb.append(key);
			sb.append("\">");
			sb.append(key);
			sb.append("</option>");
			sb.append("\n");
		}
		sb.append("</select>").append("\n");
		//input button
		sb.append("<HR WIDTH=\"75%\" COLOR=\"#6699FF\" SIZE=\"3\">").append("\n");

		retval = sb.toString();
		return retval;
	}

	/*
	 * This method will generate the query form as input text fields.
	 * The key is the analytic name.
	 */
	public String toAnalyticsQueryTextHtml(String key) {

		String retval=null;
		ArrayList<ColumnValuePair> list = getColumnValuePairs(key);
		if(list != null) {
			StringBuffer sb = new StringBuffer();
			sb.append("<form name=\"analyticsQueryForm\" action=\"\"  >");
			sb.append("<table id=\"QueryFormTable\">");//FIXME
			for(ColumnValuePair pair : list) {
				String column = pair.getColumnName();
				String value = pair.getValue();
				sb.append("<tr>");
				sb.append("<td>");
				sb.append(column);
				sb.append("</td>");

				sb.append("<td>");
				sb.append(": <input type=\"text\" ").append(" id=\"").append(column).append("\" ");
				sb.append("name=\"");
				sb.append(column);
				sb.append("  \" value=\"");
				sb.append(value);
				sb.append("\"  size=\"50\" />");
				sb.append("</td>");
				sb.append("\n");
				sb.append("</tr>");
			}
			sb.append("</table>");
			sb.append("</form>").append("<br/>");

			//FIXME need a submit button
			// submit values
			sb.append("<input type=\"button\" onclick=\"submitQuery()\" value=\"Submit Query\"/>");
			sb.append("<HR WIDTH=\"75%\" COLOR=\"#6699FF\" SIZE=\"3\">").append("\n");
			sb.append("<br/>");
			retval = sb.toString();
		}
		return retval;
	}

	//Make html table showing analytic results
	public String getAnalyticResponseTable (String json) {

		StringBuffer sb = new StringBuffer();
		StringBuffer rowBuffer = new StringBuffer();
		sb.append("<table>");
		//Create JSONObject
		//Get CSVstring
		//Split CSVstring to appropriate components

		JSONObject jobj=null;
		try {
			jobj = new JSONObject(json);
			String cvs = jobj.getString("CSVstring");
			log.info(cvs);
			String [] csvRowSplit = cvs.split("\n");
			String columnHeadings = csvRowSplit[0];

			//Make the column headings
			String [] colHeadSplit = columnHeadings.split(",");
			rowBuffer.append("<tr>");
			for(int i=0; i < colHeadSplit.length; i++) {
				rowBuffer.append("<th>").append(colHeadSplit[i]).append("</th>");
			}
			rowBuffer.append("</tr>");

			//Fill subsequent rows with the data
			for (int i = 1; i < csvRowSplit.length; i++) {
				log.info(i+".  "+csvRowSplit[i]);
				String [] rowSplit = csvRowSplit[i].split(",");
				rowBuffer.append("<tr>");
				for(int j=0; j < rowSplit.length;j++) {
					int size = rowSplit[j].length();
					if (size > 0) {
						log.info("ROW_VAL= "+ rowSplit[j]);
						rowBuffer.append("<td>").append(rowSplit[j]).append("</td>").append("\n");
					} else if ( size == 0){
						rowBuffer.append("<td>").append(" ").append("</td>").append("\n");
						
					}
				}
				rowBuffer.append("</tr>").append("\n");

			}
		} catch (JSONException e) {
			log.warn(e);
		}
		sb.append(rowBuffer.toString());

		sb.append("</table>");
		return sb.toString();
	}

	//FIXME  need method to form json query from HttpServletRequest
	public String formAnalyticsQuery(HttpServletRequest req) {

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", "QueryRequest");
		map.put("rowSeparator", "\n");
		map.put("columnSeparator", ",");
		map.put("Nrows", 1);
		int cntCol=0;

		StringBuffer sb=new StringBuffer();
		StringBuffer sb2 =new StringBuffer();
		String analytics = req.getParameter("analytics");
		sb2.append(analytics);
		for (int i = 0; i < COL_FIELDS.length; i++) {
			String s = req.getParameter(COL_FIELDS[i]);
			if(s != null) {
				sb.append(",").append(COL_FIELDS[i]);
				sb2.append(",").append(s);
				cntCol++;
			}
		}
		map.put("Ncolumns", cntCol);
		map.put("Nentries", cntCol);

		sb.append("\n").append(sb2.toString());
		map.put("CSVstring", sb.toString());

		JSONObject json= new JSONObject(map);

		return json.toString();
	}

	public static void main (String [] args) {

		String filename = "logs/D4mAnalyticsList.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			StringBuffer sb = new StringBuffer();
			String json = null;
			while ((json = br.readLine()) != null) {
				sb.append(json);
			}
			D4mAnalyticsListModel model = D4mAnalyticsListModel.getInstance();
			model.parseList(sb.toString());
			String key="Stats/Type/Count/";
			log.info("****** "+model.toAnalyticsQueryTextHtml(key));

			log.info("SELECT  ==> "+model.toAnalyticsSelectHtml());
			
			
//			HashMap<String,String> map = new HashMap<String,String>();
//			map.put("analytics", "Stats/Type/Count/");
//			map.put("ColumnClutter", "NE_ORGANIZATION/IEEE;");
//			map.put("ColumnSeed","100");
//			map.put("ColumnType", "NE_LOCATION/;");
//			map.put("TimeRange",":");
//			
//			String jsonQuery =model.formAnalyticsQuery(map);
//			log.info("Stat/Type/Count/ query format = "+jsonQuery);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
