<%@ page
  contentType="text/html; charset=UTF-8"
  import="javax.servlet.http.*"
  import="java.io.*"
  import="java.util.*"
  import="org.apache.hadoop.fs.*"
  import="org.apache.hadoop.mapred.*"
  import="org.apache.hadoop.util.*"
  import="java.text.SimpleDateFormat"
  import="org.apache.hadoop.mapred.JobHistory.*"
%>
<jsp:include page="loadhistory.jsp">
  <jsp:param name="jobid" value="<%=request.getParameter("jobid") %>"/>
  <jsp:param name="logFile" value="<%=request.getParameter("logFile") %>"/>
</jsp:include>
<%! static SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMM-yyyy HH:mm:ss") ; %>
<%
    String jobid = request.getParameter("jobid");
    String logFile = request.getParameter("logFile");
	String encodedLogFileName = JobHistory.JobInfo.encodeJobHistoryFilePath(logFile);
	
    Path jobFile = new Path(logFile);
    String[] jobDetails = jobFile.getName().split("_");
    String jobUniqueString = jobDetails[0] + "_" +jobDetails[1] + "_" + jobid ;
	
    JobInfo job = (JobInfo)request.getSession().getAttribute("job");
    FileSystem fs = (FileSystem)request.getSession().getAttribute("fs");
%>
<html><body>
<h2>Hadoop Job <%=jobid %> on <a href="jobhistory.jsp">History Viewer</a></h2>

<b>User: </b> <%=job.get(Keys.USER) %><br/> 
<b>JobName: </b> <%=job.get(Keys.JOBNAME) %><br/> 
<b>JobConf: </b> <a href="jobconf_history.jsp?jobid=<%=jobid%>&jobLogDir=<%=new Path(logFile).getParent().toString()%>&jobUniqueString=<%=jobUniqueString%>"> 
                 <%=job.get(Keys.JOBCONF) %></a><br/> 
<b>Submitted At: </b> <%=StringUtils.getFormattedTimeWithDiff(dateFormat, job.getLong(Keys.SUBMIT_TIME), 0 )  %><br/> 
<b>Launched At: </b> <%=StringUtils.getFormattedTimeWithDiff(dateFormat, job.getLong(Keys.LAUNCH_TIME), job.getLong(Keys.SUBMIT_TIME)) %><br/>
<b>Finished At: </b>  <%=StringUtils.getFormattedTimeWithDiff(dateFormat, job.getLong(Keys.FINISH_TIME), job.getLong(Keys.LAUNCH_TIME)) %><br/>
<b>Status: </b> <%= ((job.get(Keys.JOB_STATUS) == "")?"Incomplete" :job.get(Keys.JOB_STATUS)) %><br/> 
<%
    Map<String, JobHistory.Task> tasks = job.getAllTasks();
    int totalMaps = 0 ; 
    int totalReduces = 0;
    int totalCleanups = 0; 
    int totalSetups = 0; 
    int numFailedMaps = 0; 
    int numKilledMaps = 0;
    int numFailedReduces = 0 ; 
    int numKilledReduces = 0;
    int numFinishedCleanups = 0;
    int numFailedCleanups = 0;
    int numKilledCleanups = 0;
    int numFinishedSetups = 0;
    int numFailedSetups = 0;
    int numKilledSetups = 0;
	
    long mapStarted = 0 ; 
    long mapFinished = 0 ; 
    long reduceStarted = 0 ; 
    long reduceFinished = 0;
    long cleanupStarted = 0;
    long cleanupFinished = 0; 
    long setupStarted = 0;
    long setupFinished = 0; 
        
    Map <String,String> allHosts = new TreeMap<String,String>();
    for (JobHistory.Task task : tasks.values()) {
      Map<String, TaskAttempt> attempts = task.getTaskAttempts();
      allHosts.put(task.get(Keys.HOSTNAME), "");
      for (TaskAttempt attempt : attempts.values()) {
        long startTime = attempt.getLong(Keys.START_TIME) ; 
        long finishTime = attempt.getLong(Keys.FINISH_TIME) ; 
        if (Values.MAP.name().equals(task.get(Keys.TASK_TYPE))){
          if (mapStarted==0 || mapStarted > startTime ) {
            mapStarted = startTime; 
          }
          if (mapFinished < finishTime ) {
            mapFinished = finishTime ; 
          }
          totalMaps++; 
          if (Values.FAILED.name().equals(attempt.get(Keys.TASK_STATUS))) {
            numFailedMaps++; 
          } else if (Values.KILLED.name().equals(attempt.get(Keys.TASK_STATUS))) {
            numKilledMaps++;
          }
        } else if (Values.REDUCE.name().equals(task.get(Keys.TASK_TYPE))) {
          if (reduceStarted==0||reduceStarted > startTime) {
            reduceStarted = startTime ; 
          }
          if (reduceFinished < finishTime) {
            reduceFinished = finishTime; 
          }
          totalReduces++; 
          if (Values.FAILED.name().equals(attempt.get(Keys.TASK_STATUS))) {
            numFailedReduces++;
          } else if (Values.KILLED.name().equals(attempt.get(Keys.TASK_STATUS))) {
            numKilledReduces++;
          }
        } else if (Values.CLEANUP.name().equals(task.get(Keys.TASK_TYPE))) {
          if (cleanupStarted==0||cleanupStarted > startTime) {
            cleanupStarted = startTime ; 
          }
          if (cleanupFinished < finishTime) {
            cleanupFinished = finishTime; 
          }
          totalCleanups++; 
          if (Values.SUCCESS.name().equals(attempt.get(Keys.TASK_STATUS))) {
            numFinishedCleanups++;
          } else if (Values.FAILED.name().equals(attempt.get(Keys.TASK_STATUS))) {
            numFailedCleanups++;
          } else if (Values.KILLED.name().equals(attempt.get(Keys.TASK_STATUS))) {
            numKilledCleanups++;
          } 
        } else if (Values.SETUP.name().equals(task.get(Keys.TASK_TYPE))) {
          if (setupStarted==0||setupStarted > startTime) {
            setupStarted = startTime ; 
          }
          if (setupFinished < finishTime) {
            setupFinished = finishTime; 
          }
          totalSetups++; 
          if (Values.SUCCESS.name().equals(attempt.get(Keys.TASK_STATUS))) {
            numFinishedSetups++;
          } else if (Values.FAILED.name().equals(attempt.get(Keys.TASK_STATUS))) {
            numFailedSetups++;
          } else if (Values.KILLED.name().equals(attempt.get(Keys.TASK_STATUS))) {
            numKilledSetups++;
          }
        }
      }
    }
%>
<b><a href="analysejobhistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>">Analyse This Job</a></b> 
<hr/>
<center>
<table border="2" cellpadding="5" cellspacing="2">
<tr>
<td>Kind</td><td>Total Tasks(successful+failed+killed)</td><td>Successful tasks</td><td>Failed tasks</td><td>Killed tasks</td><td>Start Time</td><td>Finish Time</td>
</tr>
<tr>
<td>Setup</td>
    <td><a href="jobtaskshistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>&taskType=<%=Values.SETUP.name() %>&status=all">
        <%=totalSetups%></a></td>
    <td><a href="jobtaskshistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>&taskType=<%=Values.SETUP.name() %>&status=<%=Values.SUCCESS %>">
        <%=numFinishedSetups%></a></td>
    <td><a href="jobtaskshistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>&taskType=<%=Values.SETUP.name() %>&status=<%=Values.FAILED %>">
        <%=numFailedSetups%></a></td>
    <td><a href="jobtaskshistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>&taskType=<%=Values.SETUP.name() %>&status=<%=Values.KILLED %>">
        <%=numKilledSetups%></a></td>  
    <td><%=StringUtils.getFormattedTimeWithDiff(dateFormat, setupStarted, 0) %></td>
    <td><%=StringUtils.getFormattedTimeWithDiff(dateFormat, setupFinished, setupStarted) %></td>
</tr>
<tr>
<td>Map</td>
    <td><a href="jobtaskshistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>&taskType=<%=Values.MAP.name() %>&status=all">
        <%=totalMaps %></a></td>
    <td><a href="jobtaskshistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>&taskType=<%=Values.MAP.name() %>&status=<%=Values.SUCCESS %>">
        <%=job.getInt(Keys.FINISHED_MAPS) %></a></td>
    <td><a href="jobtaskshistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>&taskType=<%=Values.MAP.name() %>&status=<%=Values.FAILED %>">
        <%=numFailedMaps %></a></td>
    <td><a href="jobtaskshistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>&taskType=<%=Values.MAP.name() %>&status=<%=Values.KILLED %>">
        <%=numKilledMaps %></a></td>
    <td><%=StringUtils.getFormattedTimeWithDiff(dateFormat, mapStarted, 0) %></td>
    <td><%=StringUtils.getFormattedTimeWithDiff(dateFormat, mapFinished, mapStarted) %></td>
</tr>
<tr>
<td>Reduce</td>
    <td><a href="jobtaskshistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>&taskType=<%=Values.REDUCE.name() %>&status=all">
        <%=totalReduces%></a></td>
    <td><a href="jobtaskshistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>&taskType=<%=Values.REDUCE.name() %>&status=<%=Values.SUCCESS %>">
        <%=job.getInt(Keys.FINISHED_REDUCES)%></a></td>
    <td><a href="jobtaskshistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>&taskType=<%=Values.REDUCE.name() %>&status=<%=Values.FAILED %>">
        <%=numFailedReduces%></a></td>
    <td><a href="jobtaskshistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>&taskType=<%=Values.REDUCE.name() %>&status=<%=Values.KILLED %>">
        <%=numKilledReduces%></a></td>  
    <td><%=StringUtils.getFormattedTimeWithDiff(dateFormat, reduceStarted, 0) %></td>
    <td><%=StringUtils.getFormattedTimeWithDiff(dateFormat, reduceFinished, reduceStarted) %></td>
</tr>
<tr>
<td>Cleanup</td>
    <td><a href="jobtaskshistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>&taskType=<%=Values.CLEANUP.name() %>&status=all">
        <%=totalCleanups%></a></td>
    <td><a href="jobtaskshistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>&taskType=<%=Values.CLEANUP.name() %>&status=<%=Values.SUCCESS %>">
        <%=numFinishedCleanups%></a></td>
    <td><a href="jobtaskshistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>&taskType=<%=Values.CLEANUP.name() %>&status=<%=Values.FAILED %>">
        <%=numFailedCleanups%></a></td>
    <td><a href="jobtaskshistory.jsp?jobid=<%=jobid %>&logFile=<%=encodedLogFileName%>&taskType=<%=Values.CLEANUP.name() %>&status=<%=Values.KILLED %>">
        <%=numKilledCleanups%></a></td>  
    <td><%=StringUtils.getFormattedTimeWithDiff(dateFormat, cleanupStarted, 0) %></td>
    <td><%=StringUtils.getFormattedTimeWithDiff(dateFormat, cleanupFinished, cleanupStarted) %></td>
</tr>
</table>

<br/>
 <%
    DefaultJobHistoryParser.FailedOnNodesFilter filter = 
                 new DefaultJobHistoryParser.FailedOnNodesFilter();
    JobHistory.parseHistoryFromFS(logFile, filter, fs); 
    Map<String, Set<String>> badNodes = filter.getValues(); 
    if (badNodes.size() > 0) {
 %>
<h3>Failed tasks attempts by nodes </h3>
<table border="1">
<tr><td>Hostname</td><td>Failed Tasks</td></tr>
 <%	  
      for (Map.Entry<String, Set<String>> entry : badNodes.entrySet()) {
        String node = entry.getKey();
        Set<String> failedTasks = entry.getValue();
%>
        <tr>
        <td><%=node %></td>
        <td>
<%
        for (String t : failedTasks) {
%>
          <a href="taskdetailshistory.jsp?jobid=<%=jobid%>&logFile=<%=encodedLogFileName%>&taskid=<%=t %>"><%=t %></a>,&nbsp;
<%		  
        }
%>	
        </td>
        </tr>
<%	  
      }
	}
 %>
</table>
<br/>
 <%
    DefaultJobHistoryParser.KilledOnNodesFilter killedFilter =
                 new DefaultJobHistoryParser.KilledOnNodesFilter();
    JobHistory.parseHistoryFromFS(logFile, filter, fs); 
    badNodes = killedFilter.getValues(); 
    if (badNodes.size() > 0) {
 %>
<h3>Killed tasks attempts by nodes </h3>
<table border="1">
<tr><td>Hostname</td><td>Killed Tasks</td></tr>
 <%	  
      for (Map.Entry<String, Set<String>> entry : badNodes.entrySet()) {
        String node = entry.getKey();
        Set<String> killedTasks = entry.getValue();
%>
        <tr>
        <td><%=node %></td>
        <td>
<%
        for (String t : killedTasks) {
%>
          <a href="taskdetailshistory.jsp?jobid=<%=jobid%>&logFile=<%=encodedLogFileName%>&taskid=<%=t %>"><%=t %></a>,&nbsp;
<%		  
        }
%>	
        </td>
        </tr>
<%	  
      }
    }
%>
</table>
</center>
</body></html>
