<%@ page
  contentType="text/html; charset=UTF-8"
  import="javax.servlet.*"
  import="javax.servlet.http.*"
  import="java.io.*"
  import="java.util.*"
  import="org.apache.hadoop.mapred.*"
  import="org.apache.hadoop.util.*"
%>

<%
  JobTracker tracker = (JobTracker) application.getAttribute("job.tracker");
  String trackerName = 
           StringUtils.simpleHostname(tracker.getJobTrackerMachine());
%>
<%! 
  private void printFailedAttempts(JspWriter out,
                                   JobTracker tracker,
                                   JobID jobId,
                                   TaskInProgress tip,
                                   TaskStatus.State failState) throws IOException {
    TaskStatus[] statuses = tip.getTaskStatuses();
    TaskID tipId = tip.getTIPId();
    for(int i=0; i < statuses.length; ++i) {
      TaskStatus.State taskState = statuses[i].getRunState();
      if ((failState == null && (taskState == TaskStatus.State.FAILED || 
          taskState == TaskStatus.State.KILLED)) || taskState == failState) {
        String taskTrackerName = statuses[i].getTaskTracker();
        TaskTrackerStatus taskTracker = tracker.getTaskTracker(taskTrackerName);
        out.print("<tr><td>" + statuses[i].getTaskID() +
                  "</td><td><a href=\"taskdetails.jsp?jobid="+ jobId + 
                  "&tipid=" + tipId + "\">" + tipId +
                  "</a></td>");
        if (taskTracker == null) {
          out.print("<td>" + taskTrackerName + "</td>");
        } else {
          out.print("<td><a href=\"http://" + taskTracker.getHost() + ":" +
                    taskTracker.getHttpPort() + "\">" +  taskTracker.getHost() + 
                    "</a></td>");
        }
        out.print("<td>" + taskState + "</td>");
        out.print("<td><pre>");
        String[] failures = 
                     tracker.getTaskDiagnostics(statuses[i].getTaskID());
        if (failures == null) {
          out.print("&nbsp;");
        } else {
          for(int j = 0 ; j < failures.length ; j++){
            out.print(failures[j]);
            if (j < (failures.length - 1)) {
              out.print("\n-------\n");
            }
          }
        }
        out.print("</pre></td>");
        
        out.print("<td>");
        String taskLogUrl = null;
        if (taskTracker != null) {
          taskLogUrl = TaskLogServlet.getTaskLogUrl(taskTracker.getHost(),
                                String.valueOf(taskTracker.getHttpPort()),
                                statuses[i].getTaskID().toString());
        }
        if (taskLogUrl != null) {
          String tailFourKBUrl = taskLogUrl + "&start=-4097";
          String tailEightKBUrl = taskLogUrl + "&start=-8193";
          String entireLogUrl = taskLogUrl;
          out.print("<a href=\"" + tailFourKBUrl + "\">Last 4KB</a><br/>");
          out.print("<a href=\"" + tailEightKBUrl + "\">Last 8KB</a><br/>");
          out.print("<a href=\"" + entireLogUrl + "\">All</a><br/>");
        } else { 
          out.print("n/a"); // task tracker was lost
        }
        out.print("</td>");
        
        out.print("</tr>\n");
       }
    }
  }
             
  private void printFailures(JspWriter out, 
                             JobTracker tracker,
                             JobID jobId,
                             String kind, 
                             String cause) throws IOException {
    JobInProgress job = (JobInProgress) tracker.getJob(jobId);
    if (job == null) {
      out.print("<b>Job " + jobId + " not found.</b><br>\n");
      return;
    }
    
    boolean includeMap = false;
    boolean includeReduce = false;
    if (kind == null) {
      includeMap = true;
      includeReduce = true;
    } else if ("map".equals(kind)) {
      includeMap = true;
    } else if ("reduce".equals(kind)) {
      includeReduce = true;
    } else if ("all".equals(kind)) {
      includeMap = true;
      includeReduce = true;
    } else {
      out.print("<b>Kind " + kind + " not supported.</b><br>\n");
      return;
    }
    
    TaskStatus.State state = null;
    try {
      if (cause != null) {
        state = TaskStatus.State.valueOf(cause.toUpperCase());
        if (state != TaskStatus.State.FAILED && state != TaskStatus.State.KILLED) {
          out.print("<b>Cause '" + cause + 
              "' is not an 'unsuccessful' state.</b><br>\n");
          return;
        }
      }
    } catch (IllegalArgumentException e) {
      out.print("<b>Cause '" + cause + "' not supported.</b><br>\n");
      return;
    }
    	
    out.print("<table border=2 cellpadding=\"5\" cellspacing=\"2\">");
    out.print("<tr><th>Attempt</th><th>Task</th><th>Machine</th><th>State</th>" +
              "<th>Error</th><th>Logs</th></tr>\n");
    if (includeMap) {
      TaskInProgress[] tips = job.getMapTasks();
      for(int i=0; i < tips.length; ++i) {
        printFailedAttempts(out, tracker, jobId, tips[i], state);
      }
    }
    if (includeReduce) {
      TaskInProgress[] tips = job.getReduceTasks();
      for(int i=0; i < tips.length; ++i) {
        printFailedAttempts(out, tracker, jobId, tips[i], state);
      }
    }
    out.print("</table>\n");
  }
%>

<%
    String jobId = request.getParameter("jobid");
    if (jobId == null) {
      out.println("<h2>Missing 'jobid'!</h2>");
      return;
    }
    JobID jobIdObj = JobID.forName(jobId);
    String kind = request.getParameter("kind");
    String cause = request.getParameter("cause");
%>

<html>
<title>Hadoop <%=jobId%> failures on <%=trackerName%></title>
<body>
<h1>Hadoop <a href="jobdetails.jsp?jobid=<%=jobId%>"><%=jobId%></a>
failures on <a href="jobtracker.jsp"><%=trackerName%></a></h1>

<% 
    printFailures(out, tracker, jobIdObj, kind, cause); 
%>

<hr>
<a href="jobtracker.jsp">Go back to JobTracker</a><br>
<%
out.println(ServletUtil.htmlFooter());
%>
