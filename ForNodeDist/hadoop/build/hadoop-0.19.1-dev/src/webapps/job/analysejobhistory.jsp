<%@ page
  contentType="text/html; charset=UTF-8"
  import="javax.servlet.http.*"
  import="java.io.*"
  import="java.util.*"
  import="org.apache.hadoop.mapred.*"
  import="org.apache.hadoop.util.*"
  import="java.text.SimpleDateFormat"
  import="org.apache.hadoop.mapred.JobHistory.*"
%>
<jsp:include page="loadhistory.jsp">
  <jsp:param name="jobid" value="<%=request.getParameter("jobid") %>"/>
  <jsp:param name="logFile" value="<%=request.getParameter("logFile") %>"/>
</jsp:include>
<%!	private static SimpleDateFormat dateFormat 
                              = new SimpleDateFormat("d/MM HH:mm:ss") ; 
%>
<html><body>
<%
  String jobid = request.getParameter("jobid");
  String logFile = request.getParameter("logFile");
  String encodedLogFileName = JobHistory.JobInfo.encodeJobHistoryFilePath(logFile);
  String numTasks = request.getParameter("numTasks");
  int showTasks = 10 ; 
  if (numTasks != null) {
    showTasks = Integer.parseInt(numTasks);  
  }
  JobInfo job = (JobInfo)request.getSession().getAttribute("job");
%>
<h2>Hadoop Job <a href="jobdetailshistory.jsp?jobid=<%=jobid%>&&logFile=<%=encodedLogFileName%>"><%=jobid %> </a></h2>
<b>User : </b> <%=job.get(Keys.USER) %><br/> 
<b>JobName : </b> <%=job.get(Keys.JOBNAME) %><br/> 
<b>JobConf : </b> <%=job.get(Keys.JOBCONF) %><br/> 
<b>Submitted At : </b> <%=StringUtils.getFormattedTimeWithDiff(dateFormat, job.getLong(Keys.SUBMIT_TIME), 0 ) %><br/> 
<b>Launched At : </b> <%=StringUtils.getFormattedTimeWithDiff(dateFormat, job.getLong(Keys.LAUNCH_TIME), job.getLong(Keys.SUBMIT_TIME)) %><br/>
<b>Finished At : </b>  <%=StringUtils.getFormattedTimeWithDiff(dateFormat, job.getLong(Keys.FINISH_TIME), job.getLong(Keys.LAUNCH_TIME)) %><br/>
<b>Status : </b> <%= ((job.get(Keys.JOB_STATUS) == null)?"Incomplete" :job.get(Keys.JOB_STATUS)) %><br/> 
<hr/>
<center>
<%
  if (!Values.SUCCESS.name().equals(job.get(Keys.JOB_STATUS))) {
    out.print("<h3>No Analysis available as job did not finish</h3>");
    return;
  }
  Map<String, JobHistory.Task> tasks = job.getAllTasks();
  int finishedMaps = job.getInt(Keys.FINISHED_MAPS)  ;
  int finishedReduces = job.getInt(Keys.FINISHED_REDUCES) ;
  JobHistory.Task [] mapTasks = new JobHistory.Task[finishedMaps]; 
  JobHistory.Task [] reduceTasks = new JobHistory.Task[finishedReduces]; 
  int mapIndex = 0 , reduceIndex=0; 
  long avgMapTime = 0;
  long avgReduceTime = 0;
  long avgShuffleTime = 0;

  for (JobHistory.Task task : tasks.values()) {
    Map<String, TaskAttempt> attempts = task.getTaskAttempts();
    for (JobHistory.TaskAttempt attempt : attempts.values()) {
      if (attempt.get(Keys.TASK_STATUS).equals(Values.SUCCESS.name())) {
        long avgFinishTime = (attempt.getLong(Keys.FINISH_TIME) -
      		                attempt.getLong(Keys.START_TIME));
        if (Values.MAP.name().equals(task.get(Keys.TASK_TYPE))) {
          mapTasks[mapIndex++] = attempt ; 
          avgMapTime += avgFinishTime;
        } else if (Values.REDUCE.name().equals(task.get(Keys.TASK_TYPE))) { 
          reduceTasks[reduceIndex++] = attempt;
          avgShuffleTime += (attempt.getLong(Keys.SHUFFLE_FINISHED) - 
                             attempt.getLong(Keys.START_TIME));
          avgReduceTime += (attempt.getLong(Keys.FINISH_TIME) -
                            attempt.getLong(Keys.SHUFFLE_FINISHED));
        }
        break;
      }
    }
  }
	 
  if (finishedMaps > 0) {
    avgMapTime /= finishedMaps;
  }
  if (finishedReduces > 0) {
    avgReduceTime /= finishedReduces;
    avgShuffleTime /= finishedReduces;
  }
  Comparator<JobHistory.Task> cMap = new Comparator<JobHistory.Task>(){
    public int compare(JobHistory.Task t1, JobHistory.Task t2){
      long l1 = t1.getLong(Keys.FINISH_TIME) - t1.getLong(Keys.START_TIME); 
      long l2 = t2.getLong(Keys.FINISH_TIME) - t2.getLong(Keys.START_TIME);
      return (l2<l1 ? -1 : (l2==l1 ? 0 : 1));
    }
  }; 
  Comparator<JobHistory.Task> cShuffle = new Comparator<JobHistory.Task>(){
    public int compare(JobHistory.Task t1, JobHistory.Task t2){
      long l1 = t1.getLong(Keys.SHUFFLE_FINISHED) - 
                t1.getLong(Keys.START_TIME); 
      long l2 = t2.getLong(Keys.SHUFFLE_FINISHED) - 
                t2.getLong(Keys.START_TIME); 
      return (l2<l1 ? -1 : (l2==l1 ? 0 : 1));
    }
  }; 
  Arrays.sort(mapTasks, cMap);
  JobHistory.Task minMap = mapTasks[mapTasks.length-1] ;
%>

<h3>Time taken by best performing Map task 
<a href="taskdetailshistory.jsp?jobid=<%=jobid%>&logFile=<%=encodedLogFileName%>&taskid=<%=minMap.get(Keys.TASKID)%>">
<%=minMap.get(Keys.TASKID) %></a> : <%=StringUtils.formatTimeDiff(minMap.getLong(Keys.FINISH_TIME), minMap.getLong(Keys.START_TIME) ) %></h3>
<h3>Average time taken by Map tasks: 
<%=StringUtils.formatTimeDiff(avgMapTime, 0) %></h3>
<h3>Worse performing map tasks</h3>
<table border="2" cellpadding="5" cellspacing="2">
<tr><td>Task Id</td><td>Time taken</td></tr>
<%
  for (int i=0;i<showTasks && i<mapTasks.length; i++) {
%>
    <tr>
    <td><a href="taskdetailshistory.jsp?jobid=<%=jobid%>&logFile=<%=encodedLogFileName%>&taskid=<%=mapTasks[i].get(Keys.TASKID)%>">
        <%=mapTasks[i].get(Keys.TASKID) %></a></td>
    <td><%=StringUtils.formatTimeDiff(mapTasks[i].getLong(Keys.FINISH_TIME), mapTasks[i].getLong(Keys.START_TIME)) %></td>
    </tr>
<%
  }
%>
</table>
<%  
  Comparator<JobHistory.Task> cFinishMapRed = 
    new Comparator<JobHistory.Task>() {
    public int compare(JobHistory.Task t1, JobHistory.Task t2){
      long l1 = t1.getLong(Keys.FINISH_TIME); 
      long l2 = t2.getLong(Keys.FINISH_TIME);
      return (l2<l1 ? -1 : (l2==l1 ? 0 : 1));
    }
  };
  Arrays.sort(mapTasks, cFinishMapRed);
  JobHistory.Task lastMap = mapTasks[0] ;
%>

<h3>The last Map task 
<a href="taskdetailshistory.jsp?jobid=<%=jobid%>&logFile=<%=encodedLogFileName%>
&taskid=<%=lastMap.get(Keys.TASKID)%>"><%=lastMap.get(Keys.TASKID) %></a> 
finished at (relative to the Job launch time): 
<%=StringUtils.getFormattedTimeWithDiff(dateFormat, 
                              lastMap.getLong(Keys.FINISH_TIME), 
                              job.getLong(Keys.LAUNCH_TIME) ) %></h3>
<hr/>

<%
  if (reduceTasks.length <= 0) return;
  Arrays.sort(reduceTasks, cShuffle); 
  JobHistory.Task minShuffle = reduceTasks[reduceTasks.length-1] ;
%>
<h3>Time taken by best performing shuffle
<a href="taskdetailshistory.jsp?jobid=<%=jobid%>&logFile=<%=encodedLogFileName%>
&taskid=<%=minShuffle.get(Keys.TASKID)%>"><%=minShuffle.get(Keys.TASKID)%></a> : 
<%=StringUtils.formatTimeDiff(minShuffle.getLong(Keys.SHUFFLE_FINISHED), 
                              minShuffle.getLong(Keys.START_TIME) ) %></h3>
<h3>Average time taken by Shuffle: 
<%=StringUtils.formatTimeDiff(avgShuffleTime, 0) %></h3>
<h3>Worse performing Shuffle(s)</h3>
<table border="2" cellpadding="5" cellspacing="2">
<tr><td>Task Id</td><td>Time taken</td></tr>
<%
  for (int i=0;i<showTasks && i<reduceTasks.length; i++) {
%>
    <tr>
    <td><a href="taskdetailshistory.jsp?jobid=<%=jobid%>&logFile=
<%=encodedLogFileName%>&taskid=<%=reduceTasks[i].get(Keys.TASKID)%>">
<%=reduceTasks[i].get(Keys.TASKID) %></a></td>
    <td><%=
           StringUtils.formatTimeDiff(
                       reduceTasks[i].getLong(Keys.SHUFFLE_FINISHED),
                       reduceTasks[i].getLong(Keys.START_TIME)) %>
    </td>
    </tr>
<%
  }
%>
</table>
<%  
  Comparator<JobHistory.Task> cFinishShuffle = 
    new Comparator<JobHistory.Task>() {
    public int compare(JobHistory.Task t1, JobHistory.Task t2){
      long l1 = t1.getLong(Keys.SHUFFLE_FINISHED); 
      long l2 = t2.getLong(Keys.SHUFFLE_FINISHED);
      return (l2<l1 ? -1 : (l2==l1 ? 0 : 1));
    }
  };
  Arrays.sort(reduceTasks, cFinishShuffle);
  JobHistory.Task lastShuffle = reduceTasks[0] ;
%>

<h3>The last Shuffle  
<a href="taskdetailshistory.jsp?jobid=<%=jobid%>&logFile=<%=encodedLogFileName%>
&taskid=<%=lastShuffle.get(Keys.TASKID)%>"><%=lastShuffle.get(Keys.TASKID)%>
</a> finished at (relative to the Job launch time): 
<%=StringUtils.getFormattedTimeWithDiff(dateFormat,
                              lastShuffle.getLong(Keys.SHUFFLE_FINISHED), 
                              job.getLong(Keys.LAUNCH_TIME) ) %></h3>

<%
  Comparator<JobHistory.Task> cReduce = new Comparator<JobHistory.Task>(){
    public int compare(JobHistory.Task t1, JobHistory.Task t2){
      long l1 = t1.getLong(Keys.FINISH_TIME) - 
                t1.getLong(Keys.SHUFFLE_FINISHED); 
      long l2 = t2.getLong(Keys.FINISH_TIME) - 
                t2.getLong(Keys.SHUFFLE_FINISHED);
      return (l2<l1 ? -1 : (l2==l1 ? 0 : 1));
    }
  }; 
  Arrays.sort(reduceTasks, cReduce); 
  JobHistory.Task minReduce = reduceTasks[reduceTasks.length-1] ;
%>
<hr/>
<h3>Time taken by best performing Reduce task : 
<a href="taskdetailshistory.jsp?jobid=<%=jobid%>&logFile=<%=encodedLogFileName%>&taskid=<%=minReduce.get(Keys.TASKID)%>">
<%=minReduce.get(Keys.TASKID) %></a> : 
<%=StringUtils.formatTimeDiff(minReduce.getLong(Keys.FINISH_TIME),
    minReduce.getLong(Keys.SHUFFLE_FINISHED) ) %></h3>

<h3>Average time taken by Reduce tasks: 
<%=StringUtils.formatTimeDiff(avgReduceTime, 0) %></h3>
<h3>Worse performing reduce tasks</h3>
<table border="2" cellpadding="5" cellspacing="2">
<tr><td>Task Id</td><td>Time taken</td></tr>
<%
  for (int i=0;i<showTasks && i<reduceTasks.length; i++) {
%>
    <tr>
    <td><a href="taskdetailshistory.jsp?jobid=<%=jobid%>&logFile=<%=encodedLogFileName%>&taskid=<%=reduceTasks[i].get(Keys.TASKID)%>">
        <%=reduceTasks[i].get(Keys.TASKID) %></a></td>
    <td><%=StringUtils.formatTimeDiff(
             reduceTasks[i].getLong(Keys.FINISH_TIME), 
             reduceTasks[i].getLong(Keys.SHUFFLE_FINISHED)) %></td>
    </tr>
<%
  }
%>
</table>
<%  
  Arrays.sort(reduceTasks, cFinishMapRed);
  JobHistory.Task lastReduce = reduceTasks[0] ;
%>

<h3>The last Reduce task 
<a href="taskdetailshistory.jsp?jobid=<%=jobid%>&logFile=<%=encodedLogFileName%>
&taskid=<%=lastReduce.get(Keys.TASKID)%>"><%=lastReduce.get(Keys.TASKID)%>
</a> finished at (relative to the Job launch time): 
<%=StringUtils.getFormattedTimeWithDiff(dateFormat,
                              lastReduce.getLong(Keys.FINISH_TIME), 
                              job.getLong(Keys.LAUNCH_TIME) ) %></h3>
</center>
</body></html>
