package org.apache.hadoop.mapred;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.io.*;
import java.util.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.fs.*;
import javax.servlet.jsp.*;
import java.text.SimpleDateFormat;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.JobHistory.*;

public final class jobhistory_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

	
  private static SimpleDateFormat dateFormat = 
                                    new SimpleDateFormat("d/MM HH:mm:ss");


    private void printJob(String trackerHostName, String trackerid,
                          String jobId, String jobName,
                          String user, Path logFile, JspWriter out)
    throws IOException {
      out.print("<tr>"); 
      out.print("<td>" + trackerHostName + "</td>"); 
      out.print("<td>" + new Date(Long.parseLong(trackerid)) + "</td>"); 
      out.print("<td>" + "<a href=\"jobdetailshistory.jsp?jobid=" + jobId + 
                "&logFile=" + logFile.toString() + "\">" + jobId + "</a></td>"); 
      out.print("<td>" + jobName + "</td>"); 
      out.print("<td>" + user + "</td>"); 
      out.print("</tr>");
    }

  private static java.util.Vector _jspx_dependants;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    JspFactory _jspxFactory = null;
    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      _jspxFactory = JspFactory.getDefaultFactory();
      response.setContentType("text/html; charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write('\n');
      out.write("\n<html>\n<head>\n<title>Hadoop Map/Reduce Administration</title>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"/static/hadoop.css\">\n</head>\n<body>\n<h1>Hadoop Map/Reduce History Viewer</h1>\n<hr>\n<h2>Available History </h2>\n");

    PathFilter jobLogFileFilter = new PathFilter() {
      public boolean accept(Path path) {
        return !(path.getName().endsWith(".xml"));
      }
    };
    
    FileSystem fs = (FileSystem) application.getAttribute("fileSys");
    String historyLogDir = (String) application.getAttribute("historyLogDir");
    if (fs == null) {
      out.println("Null file system. May be namenode is in safemode!");
      return;
    }
    Path[] jobFiles = FileUtil.stat2Paths(fs.listStatus(new Path(historyLogDir),
                                          jobLogFileFilter));
    if (null == jobFiles )  {
      out.println("NULL files !!!"); 
      return ; 
    }

    // sort the files on creation time.
    Arrays.sort(jobFiles, new Comparator<Path>() {
      public int compare(Path p1, Path p2) {
        String dp1 = null;
        String dp2 = null;
        
        try {
          dp1 = JobHistory.JobInfo.decodeJobHistoryFileName(p1.getName());
          dp2 = JobHistory.JobInfo.decodeJobHistoryFileName(p2.getName());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
                
        String[] split1 = dp1.split("_");
        String[] split2 = dp2.split("_");
        
        // compare job tracker start time
        int res = new Date(Long.parseLong(split1[1])).compareTo(
                             new Date(Long.parseLong(split2[1])));
        if (res == 0) {
          res = new Date(Long.parseLong(split1[3])).compareTo(
                           new Date(Long.parseLong(split2[3])));
        }
        if (res == 0) {
          Long l1 = Long.parseLong(split1[4]);
          res = l1.compareTo(Long.parseLong(split2[4]));
        }
        return res;
      }
    });

    out.print("<table align=center border=2 cellpadding=\"5\" cellspacing=\"2\">");
    out.print("<tr><td align=\"center\" colspan=\"9\"><b>Available Jobs </b></td></tr>\n");
    out.print("<tr>");
    out.print("<td>Job tracker Host Name</td>" +
              "<td>Job tracker Start time</td>" +
              "<td>Job Id</td><td>Name</td><td>User</td>") ; 
    out.print("</tr>"); 
    
    Set<String> displayedJobs = new HashSet<String>();
    for (Path jobFile: jobFiles) {
      String decodedJobFileName = 
          JobHistory.JobInfo.decodeJobHistoryFileName(jobFile.getName());

      String[] jobDetails = decodedJobFileName.split("_");
      String trackerHostName = jobDetails[0];
      String trackerStartTime = jobDetails[1];
      String jobId = jobDetails[2] + "_" +jobDetails[3] + "_" + jobDetails[4] ;
      String user = jobDetails[5];
      String jobName = jobDetails[6];
      
      // Check if the job is already displayed. There can be multiple job 
      // history files for jobs that have restarted
      if (displayedJobs.contains(jobId)) {
        continue;
      } else {
        displayedJobs.add(jobId);
      }
      
      // Encode the logfile name again to cancel the decoding done by the browser
      String encodedJobFileName = 
          JobHistory.JobInfo.encodeJobHistoryFileName(jobFile.getName());

      out.write("\n<center>\n");
	
      printJob(trackerHostName, trackerStartTime, jobId,
               jobName, user, new Path(jobFile.getParent(), encodedJobFileName), 
               out) ; 

      out.write("\n</center> \n");

    } // end while trackers 

      out.write('\n');
      out.write(" \n</body></html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      if (_jspxFactory != null) _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
