package edu.mit.ll.d4m.analytics.web;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.Resource;
import org.apache.log4j.lf5.util.ResourceUtils;
import org.json.JSONException;

public class D4mServlet extends HttpServlet {
	private static Logger log = Logger.getLogger(D4mServlet.class);
	String serverAddress="localhost";
	int serverPort=6222;
	String CONF_FILE="./d4m_conf.properties";
	private String SERVER_PROP="d4m.server";
	private String PORT_PROP="d4m.port";
	private boolean isTest=true;

	private String PARAM_NAME="s";

	private D4mAnalyticsListModel  d4mAnalyticsModel= null;
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		if(isTest) {
			testDoGet(req,resp);	
		}
		else {
			execD4mRequest(req,resp);
		}
	}
	public void testDoGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		String r= req.getParameter(PARAM_NAME);

		if(r == null) {
			r = req.getParameter("q");
			log.info("Q ==> "+r);
			if(r.equals(D4mAnalyticsListModel.GET_ANALYTICS_LIST)) {
				r = this.d4mAnalyticsModel.toAnalyticsSelectHtml();
			}
			else if(r.equals(D4mAnalyticsListModel.GET_ANALYTICS_FORM) ) {
				String analytics= req.getParameter("analytics");
				r = "Analytics is not available";

				if(analytics != null) {
					r=this.d4mAnalyticsModel.toAnalyticsQueryTextHtml(analytics);
				}
			}
			else if(r.equals(D4mAnalyticsListModel.GET_ANALYTICS_RESPONSE)) {
				String key = req.getParameter("analytics");
				r="No result";
				if(key != null) {
					r = this.d4mAnalyticsModel.getTestResponse(key);
					r = this.d4mAnalyticsModel.getAnalyticResponseTable(r);
					log.info(key+"::QueryResponse = "+r);
				}
				
				if(log.isInfoEnabled()) {
					log.info("ColumnClutter ="+ req.getParameter("ColumnClutter"));
					log.info("ColumnSeed ="+ req.getParameter("ColumnSeed"));
					log.info("ColumnType ="+ req.getParameter("ColumnType"));
					log.info("FilterThreshold ="+ req.getParameter("FilterThreshold"));
					log.info("FilterWidth ="+ req.getParameter("FilterWidth"));
					log.info("GraphDepth ="+ req.getParameter("GraphDepth"));
					log.info("LatPOints ="+ req.getParameter("LatPoints"));
					log.info("LonPoints ="+ req.getParameter("LonPoints"));
					log.info("TimeRange ="+ req.getParameter("TimeRange"));
				}

			}
		} else {
			log.info("%%%%  The request = "+r);
			PrintWriter out = resp.getWriter();
			out.println("I got it. Thanks for request = "+r);
			out.close();
		}

		if(r != null) {
			resp.setContentType("text/xml");

			PrintWriter out = resp.getWriter();
			out.println(r);
			out.close();
		}
	}

	public void execD4mRequest(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		StringBuffer sbresp = new StringBuffer();

		String r = req.getParameter(PARAM_NAME);
		if(r == null) {
			r = req.getParameter("q");
			if(r.equals(D4mAnalyticsListModel.GET_ANALYTICS_LIST)) {
				getAnalyticsList(req,resp);
			}
			else if(r.equals(D4mAnalyticsListModel.GET_ANALYTICS_FORM) ) {
				getAnalyticsQueryForm(req, resp);
			}
			else if(r.equals(D4mAnalyticsListModel.GET_ANALYTICS_RESPONSE)) {
				getAnalyticResponse(req,resp);
			}
		}
		else {
			sendHttpResponse(resp,"Your request is not recognized.");
		}
		
	}
	
	private void getAnalyticsList(HttpServletRequest req,HttpServletResponse resp) {
		String json = this.d4mAnalyticsModel.formQueryForAnalyticsList();
		String response = send(req,resp,json);
		
		//Create select list and write to HttpServletResponse
		try {
			this.d4mAnalyticsModel.parseList(response);
			response = this.d4mAnalyticsModel.toAnalyticsSelectHtml();
			sendHttpResponse(resp, response);
//			resp.getWriter().print(response);
//			resp.getWriter().flush();
			
		} catch (JSONException e) {
			log.warn(e);
		} 
		
	}

	//This method will return a query form based on the chosen analytic
	private void getAnalyticsQueryForm(HttpServletRequest req,HttpServletResponse resp) {
		String analytic= req.getParameter("analytics");
		String r = "Analytics is not available";

		if(analytic != null) {
			r=this.d4mAnalyticsModel.toAnalyticsQueryTextHtml(analytic);
		}
//		try {
//			resp.getWriter().print(r);
//			resp.getWriter().flush();
//
//		} catch (IOException e) {
//			log.warn(e);
//		}
		sendHttpResponse(resp, r);
	}
	
	private void getAnalyticResponse(HttpServletRequest req,HttpServletResponse resp) {
		String key = req.getParameter("analytics");
		String jsonQuery = this.d4mAnalyticsModel.formAnalyticsQuery(req);
		String response = send(req,resp,jsonQuery);
		//make a table of the
		if(response.length() > 0) {
			response = this.d4mAnalyticsModel.getAnalyticResponseTable(response);
		}
		sendHttpResponse(resp,response);
		
	}
	
	private void sendHttpResponse(HttpServletResponse resp,String response) {
		try {
			resp.getWriter().print(response);
			resp.getWriter().flush();

		} catch (IOException e) {
			log.warn(e);
		}

	}
	public String send (HttpServletRequest req,HttpServletResponse resp, String json) {
		StringBuffer sbresp = new StringBuffer();
		try {
			Socket sock = new Socket(serverAddress, serverPort);
			log.info("Connecting to: " + serverAddress + ":" + serverPort);

			while (!sock.isConnected()) {
				Thread.sleep(100);
			}
			PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
			log.info("Sending to Matlab: " + json);

			out.print(json);
			if (!json.endsWith("\n"))
				out.println();
			out.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			log.debug("Waiting for response from Matlab/Octave...");
			String line = in.readLine();

			sbresp.append(line + "\n");
			while (line != null && !line.contains("}")) {
				line = in.readLine();
				if (line == null)
					break;
				sbresp.append(line + "\n");
			}

			log.info(" THE RESPONSE = "+sbresp.toString());
//			resp.getWriter().print(sbresp.toString());
//			resp.getWriter().flush();
			out.close();
			in.close();
			sock.close();
		}
		catch (Exception e) {
			log.warn(e);
		}

		log.info("Returning: " + resp.toString());
		return sbresp.toString();
	}
	
	public void initServerInfo() {
		//Read in properties file

		ClassLoader loader = null;


		/* Get the System ClassLoader */

		try {
			String filename = CONF_FILE;
			//Use Log4j ResourceUtils class to get resource.
			//The resource is the config file
			Resource resource = new Resource(filename);
			InputStream is = ResourceUtils.getResourceAsStream(this, resource);
			if (is == null) {
				loader = ClassLoader.getSystemClassLoader();
				log.info("Using the System ClassLoader");
				is = loader.getResourceAsStream(filename);
			}
			Properties prop = new Properties();
			prop.load(is);

			String serverName=prop.getProperty(SERVER_PROP);
			if(serverName != null) {
				serverAddress = serverName;
			}

			String port = prop.getProperty(PORT_PROP);
			if(port != null) {
				serverPort = Integer.parseInt(port);
			}
			log.info("Server address = "+serverName+ ", server port = "+ port);
			
			String testflag = prop.getProperty("d4m.testing");
			if(testflag != null) {
				this.isTest = Boolean.parseBoolean(testflag);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void init() throws ServletException {
		super.init();
		initServerInfo();

		if(isTest) {
			System.setProperty("testing", "true");
		}

		this.d4mAnalyticsModel = D4mAnalyticsListModel.getInstance();

	}

}
