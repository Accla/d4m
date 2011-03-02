/**
 * 
 */
package edu.mit.ll.d4m.analytics.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.mit.ll.oct4j.MiscUtil;
import edu.mit.ll.oct4j.OctDataObject;
import edu.mit.ll.oct4j.OctaveProcFactory;
import edu.mit.ll.oct4j.OctaveProcessor;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.Resource;
import org.apache.log4j.lf5.util.ResourceUtils;
import org.json.JSONException;
/**
 * @author cyee
 *
 */
public class D4mOctaveServlet extends HttpServlet {
	private static Logger log = Logger.getLogger(D4mOctaveServlet.class);
	private String PARAM_NAME="s";

	private D4mAnalyticsListModel  d4mAnalyticsModel= null;
	private String CONF_FILE="./d4m_conf.properties";
	private String script= null;
	private boolean isTest = false;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		if(isTest) {
			System.setProperty("testing", "true");
			D4mAnalyticsListModel.testMode(req,resp);
		} else {
			execD4mRequest(req,resp);
		}
	}

	public void execD4mRequest(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		//StringBuffer sbresp = new StringBuffer();

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
			if(response != null) {
				this.d4mAnalyticsModel.parseList(response);
				response = this.d4mAnalyticsModel.toAnalyticsSelectHtml();
			}
			sendHttpResponse(resp, response);

		} catch (JSONException e) {
			log.warn(e);
		} 

	}

	private void getAnalyticsQueryForm(HttpServletRequest req,HttpServletResponse resp) {
		String analytic= req.getParameter("analytics");
		String r = "Analytic is not available";

		if(analytic != null) {
			r=this.d4mAnalyticsModel.toAnalyticsQueryTextHtml(analytic);
		}

		sendHttpResponse(resp, r);
	}
	private void getAnalyticResponse(HttpServletRequest req,HttpServletResponse resp) {
		String key = req.getParameter("analytics");
		String jsonQuery = this.d4mAnalyticsModel.formAnalyticsQuery(req);
		String response = send(req,resp,jsonQuery);
		//make a table of the

		if(response != null) {
			if(response.length() > 0) {
				response = this.d4mAnalyticsModel.getAnalyticResponseTable(response);
			}
		}
		sendHttpResponse(resp,response);

	}

	// Send is used to "send" the request to the OctaveProcess
	public String send(HttpServletRequest req,HttpServletResponse resp, String json) {
		String retval=null;
		//Create OctaveProcess
		OctaveProcessor octave=null;
		try {
			octave = OctaveProcFactory.getInstance();

			//Exec addpath
			URL url = ResourceUtils.getResourceAsURL(this, new Resource("d4m_api"));
			//String user_dir = System.getProperty("user.dir")+"/webapps/d4mweb/WEB-INF/classes";
			String d4mhome= url.getPath();//user_dir+"/d4m_api";
			String addpath_d4mhome="addpath('"+d4mhome+"');";
			String addpath_matlab_src="addpath('"+d4mhome+"matlab_src');";
			String addpath_examples="addpath('"+d4mhome+"examples');";
			octave.exec(addpath_d4mhome);
			octave.exec(addpath_matlab_src);
			octave.exec(addpath_examples);
			octave.exec("DBinit");
			octave.exec("cd "+d4mhome+"matlab_src");

			//Exec script
			String [] param = {json};
			String scr = MiscUtil.makeCommand(this.script, param);
			octave.exec(scr);
			OctDataObject data = octave.get(MiscUtil.RESPONSE_VAR);
			if(data != null && data.toString() != null)
				retval = data.toString();
		}
		catch(RuntimeException e) {
			log.warn("run time problem ...",e);
			throw new RuntimeException(e);
		} finally {
			octave.shutdown();
		}
		return retval;
	}

	@Override
	public void init() throws ServletException {
		super.init();
		initConfig();

		this.d4mAnalyticsModel = D4mAnalyticsListModel.getInstance();

	}
	private void initConfig() {
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
			this.script = prop.getProperty("octave.script");


			String octaveProgram = prop.getProperty("octave.program");
			System.setProperty("octave.program", octaveProgram);
			//D4M_HOME
			URL url = ResourceUtils.getResourceAsURL(this, new Resource("d4m_api"));
			String d4mhome= url.getPath()+"matlab_src";
			System.setProperty("d4m.home", d4mhome);
			//Set testing flag
			String testflag = prop.getProperty("d4m.testing");
			if(testflag != null) {
				this.isTest = Boolean.parseBoolean(testflag);
			}


		}
		catch(Exception e) {

		}

	}
	private void sendHttpResponse(HttpServletResponse resp,String response) {
		try {
			resp.getWriter().print(response);
			resp.getWriter().flush();

		} catch (IOException e) {
			log.warn(e);
		}

	}

}
