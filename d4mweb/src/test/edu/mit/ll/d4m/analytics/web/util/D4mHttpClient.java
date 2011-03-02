package edu.mit.ll.d4m.analytics.web.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.ll.d4m.analytics.web.D4mAnalyticsListModel;

public class D4mHttpClient {

	public static String filename="d4mResponseJson.txt";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test2(args);
		test3(args);
		test4(args);
		test5(args);
	}


	// test2 get the  analytics list
	public static void test2(String[] args) {
		System.out.println("***************************************");

		System.out.println("***************** 2 *******************");

		String server=args[0];
		int port = Integer.parseInt(args[1]);
		String address = server+":"+port;

		//analytics list request string
		/*
		String jsonRequest="['{"name\":"QueryRequest",' ...
  '"rowSeparator":"\n","columnSeparator":",",' ...
  '"Nrows":2,"Ncolumns":2,"Nentries": 3,' ...
  '"CSVstring":",Col1\nGetAnalysisDefaults,1"}'];";
		 */
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", "QueryRequest");
		map.put("rowSeparator", "\n");
		map.put("columnSeparator", ",");
		map.put("Nrows", 2);
		map.put("Ncolumns", 2);
		map.put("Nentries", 3);
		map.put("CSVstring", ",Col1\nGetAnalysisDefaults,1");


		JSONObject json= new JSONObject(map);
	
		//json.put(map);		
		System.out.println("JSON = "+json.toString());
		filename = "D4mAnalyticsList.txt";
		send(server,port,json.toString());
	}

	public static void test3(String [] args) {
		String server=args[0];
		int port = Integer.parseInt(args[1]);
		System.out.println("***************************************");

		System.out.println("***************** 3 *******************");

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", "QueryResponse");
		map.put("rowSeparator", "\n");
		map.put("columnSeparator", ",");
		map.put("Nrows", 1);
		map.put("Ncolumns", 4);
		map.put("Nentries", 4);
		
		
		/*
		 * 
		 * "CSVstring":
		 * ",ColumnClutter,
		 * ColumnSeed,
		 * ColumnType,
		 * TimeRange\nStats/Type/Count/,
		 * NE_LOCATION/Minnesota;
		 * NE_ORGANIZATION/IEEE;
		 * NE_PERSON/Billy Bob;,
		 * 100,TIME/;TIMELOCAL/;
		 * NE_ORGANIZATION/;
		 * NE_PERSON/;
		 * NE_PERSON_GENERIC/;NE_PERSON_MILITARY/;GEO/;NE_LOCATION/;,:\n"
		 */
		//Form value of CSVstring
		StringBuffer sb = new StringBuffer();
		sb.append(",ColumnClutter,");
		sb.append("ColumnSeed,");
		sb.append("ColumnType,");
		sb.append("TimeRange\nStats/Type/Count/,");
		sb.append("NE_LOCATION/Minnesota;");
		sb.append("NE_ORGANIZATION/IEEE;");
		sb.append("NE_PERSON/Billy Bob;,");
		sb.append("100,");
		sb.append("TIME/;TIMELOCAL/;");
		sb.append("NE_ORGANIZATION/;");
		sb.append("NE_PERSON/;");
		sb.append("NE_PERSON_GENERIC/;");
		sb.append("NE_PERSON_MILITARY/;");
		sb.append("GEO/;");
		sb.append("NE_LOCATION/;,:\n");
		map.put("CSVstring",sb.toString());
		
		JSONObject json = new JSONObject(map);
		System.out.println("JSON = "+json.toString());
		filename="D4mStatsTypeCountResponse.txt";
		send(server,port,json.toString());
		
	}
	
	public static void test4(String [] args) {
		String server=args[0];
		int port = Integer.parseInt(args[1]);
		System.out.println("***************************************");

		System.out.println("***************** 4 *******************");
		//Make Analytic qeury for 
		String analytic = "Stats/Type/Count/";
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("q", D4mAnalyticsListModel.GET_ANALYTICS_RESPONSE));
		qparams.add(new BasicNameValuePair("analytics", analytic));
		//ColumnClutter
		StringBuffer sb = new StringBuffer();
		sb.append("NE_LOCATION/Minnesota;");
		sb.append("NE_ORGANIZATION/IEEE;");
		sb.append("NE_PERSON/Billy Bob;");
		qparams.add(new BasicNameValuePair("ColumnClutter", sb.toString()));
		//ColumnSeed
		qparams.add(new BasicNameValuePair("ColumnSeed", "100"));
		//ColumnType
		sb = new StringBuffer();
		sb.append("TIME/;TIMELOCAL/;");
		sb.append("NE_ORGANIZATION/;");
		sb.append("NE_PERSON/;");
		sb.append("NE_PERSON_GENERIC/;");
		sb.append("NE_PERSON_MILITARY/;");
		sb.append("GEO/;");
		sb.append("NE_LOCATION/;");
		qparams.add(new BasicNameValuePair("ColumnType", sb.toString()));
		//TimeRange
		qparams.add(new BasicNameValuePair("TimeRange",":" ));
		send(server,port, qparams);
	}
	
	public static void test5(String [] args) {
		String server=args[0];
		int port = Integer.parseInt(args[1]);
		System.out.println("***************************************");

		System.out.println("***************** 5 *******************");
		System.out.println("  +++ Get a list of analytics +++");
		//Get the list of Analytics
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("q", D4mAnalyticsListModel.GET_ANALYTICS_LIST ));
		
		send(server,port,qparams);
	}
	public static void send(String server, int port, List<NameValuePair> qparams) {
		long start = System.currentTimeMillis();
		URI uri =null;
		HttpClient httpclient = new DefaultHttpClient();

		try {
			uri = URIUtils.createURI("http", server, port,
					"/d4mweb/query", 
					URLEncodedUtils.format(qparams, "UTF-8"), null);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpGet httpget = new HttpGet(uri);
		System.out.println(httpget.getURI());

		HttpResponse response =null;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			StringBuffer sb = new StringBuffer();
			sb.append("THE RESPONSE::  ");
			if (entity != null) {



				long len = entity.getContentLength();
				if (len != -1 && len < 2048) {
					sb.append(EntityUtils.toString(entity));
				} else {
					// Stream content out
					InputStream instream = entity.getContent();
					int l=-1;
					byte[] tmp = new byte[2048];
					while ((l = instream.read(tmp)) != -1) {
						String s = new String(tmp);
						sb.append(s);
					}
				}
			}
			System.out.println(sb.toString());

			FileWriter fw = new FileWriter(new File(System.getProperty("user.dir")+"/logs/"+filename));	
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(sb.toString());
			bw.close();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("++++++++ Req/Resp Elapsed time (ms) = "+(end-start) +" ++++++++\n");

	}
	public static void send(String server, int port, String json) {
		long start = System.currentTimeMillis();
		HttpClient httpclient = new DefaultHttpClient();

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("s", json));
		//qparams.add(new BasicNameValuePair("btnG", "Google Search"));

		URI uri =null;
		try {
			uri = URIUtils.createURI("http", server, port,
					"/d4mweb/query", 
					URLEncodedUtils.format(qparams, "UTF-8"), null);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpGet httpget = new HttpGet(uri);
		System.out.println(httpget.getURI());

		HttpResponse response =null;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			StringBuffer sb = new StringBuffer();
			sb.append("THE RESPONSE::  ");
			if (entity != null) {



				long len = entity.getContentLength();
				if (len != -1 && len < 2048) {
					sb.append(EntityUtils.toString(entity));
				} else {
					// Stream content out
					InputStream instream = entity.getContent();
					int l=-1;
					byte[] tmp = new byte[2048];
					while ((l = instream.read(tmp)) != -1) {
						String s = new String(tmp);
						sb.append(s);
					}
				}
			}
			System.out.println(sb.toString());

			FileWriter fw = new FileWriter(new File(System.getProperty("user.dir")+"/logs/"+filename));	
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(sb.toString());
			bw.close();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("++++++++ Req/Resp Elapsed time (ms) = "+(end-start) +" ++++++++\n");
	}

	public static void test1(String[] args) {

		String server=args[0];
		String port = args[1];
		String address = server+":"+port;
		HttpClient httpclient = new DefaultHttpClient();

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("s", "Hi D4m httpclient"));
		//qparams.add(new BasicNameValuePair("btnG", "Google Search"));

		URI uri =null;
		try {
			uri = URIUtils.createURI("http", server, Integer.parseInt(port),
					"/d4mweb/query", 
					URLEncodedUtils.format(qparams, "UTF-8"), null);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpGet httpget = new HttpGet(uri);
		System.out.println(httpget.getURI());

		HttpResponse response =null;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			StringBuffer sb = new StringBuffer();
			sb.append("THE RESPONSE::  ");
			if (entity != null) {



				long len = entity.getContentLength();
				if (len != -1 && len < 2048) {
					sb.append(EntityUtils.toString(entity));
				} else {
					// Stream content out
					InputStream instream = entity.getContent();
					int l=-1;
					byte[] tmp = new byte[2048];
					while ((l = instream.read(tmp)) != -1) {
						String s = new String(tmp);
						sb.append(s);
					}
				}
			}
			System.out.println(sb.toString());


		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
