<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" href="resources/css/screen.css" type="text/css"
	media="screen" charset="utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Dynamic Distributed Dimensional Data Model</title>
<%@ page import="edu.mit.ll.d4m.analytics.web.D4mAnalyticsListModel"%>

<script type="text/javascript">
 var req;
 function getAnalyticsList() {
	 req = new XMLHttpRequest();
	 var get_analytics_list="<%=D4mAnalyticsListModel.GET_ANALYTICS_LIST%>";
     var url="query?q="+get_analytics_list;
     req.onreadystatechange=drawAnalyticsList;
     req.open("GET",url,true);
     req.send();

 }
 
 function drawAnalyticsList() {
	 if(req.readyState == 4 && req.status == 200) {
        document.getElementById("analyticsList").innerHTML=req.responseText;
	 }
 }
 function drawAnalyticsQueryForm() {
	 if(req.readyState == 4 && req.status == 200) {
        document.getElementById("analyticsQueryForm").innerHTML=req.responseText;
        clearAnalyticsResponse();
	 }
 }
 
 function getAnalyticsQueryForm(analyticsType) {
	 req = new XMLHttpRequest();
	 var get_analytics_form="<%=D4mAnalyticsListModel.GET_ANALYTICS_FORM%>";
	 var url="query?q="+get_analytics_form+"&analytics="+analyticsType;
	    req.onreadystatechange=drawAnalyticsQueryForm;
	     req.open("GET",url,true);
	     req.send();

 }
 function submitQuery() {
	 req = new XMLHttpRequest();
	 var get_analytics="<%=D4mAnalyticsListModel.GET_ANALYTICS_RESPONSE%>";
	 var analyticsType = document.getElementById("analyticsListSelect").value;
	 var url="query?q="+get_analytics+"&analytics="+analyticsType;
	 var element =  document.getElementById("ColumnClutter");
	 if(element != null) {
	 var columnClutter = "&ColumnClutter="+element.value;
	 url += columnClutter;
	 }
	 element = document.getElementById("ColumnSeed");
	 if(element != null) {
	 var columnSeed = "&ColumnSeed="+ element.value;
	 url += columnSeed;
	 }
	 
	 element = document.getElementById("ColumnType"); 
	 if(element != null){
	 var columnType = "&ColumnType="+ element.value;
	 url += columnType;
	 }
	 element = document.getElementById("FilterThreshold");
	 if(element != null) {
	 var filterThreshold = "&FilterThreshold="+ element.value;
	 url += filterThreshold;
	 }
	 element = document.getElementById("FilterWidth");
	 if(element != null) {
	 var filterWidth = "&FilterWidth="+ element.value;
	 url += filterWidth;
	 }
	 element = document.getElementById("GraphDepth");
	 if(element != null) {
	 var graphDepth = "&GraphDepth="+ element.value;
	 url += graphDepth;
	 }
	 element =  document.getElementById("LatPoints");
	 if(element != null) {
	 var latPoints = "&LatPoints="+ element.value;
	 url += latPoints;
	 }
	 element = document.getElementById("LonPoints");
	 if(element != null) {
	 var lonPoints = "&LonPoints="+ element.value;
	 url += lonPoints;
	 }
	 element = document.getElementById("TimeRange");
	 if(element != null) {
	 var timeRange = "&TimeRange="+ element.value;
	 url += timeRange;
	 }
     
     req.onreadystatechange=drawAnalyticsResponse;
     req.open("GET",url,true);
     req.send();
 }
 
 function drawAnalyticsResponse () {
	 if(req.readyState == 4 && req.status == 200) {
	        document.getElementById("analyticsResponse").innerHTML=req.responseText;
		 }
	 
 }
 function clearAnalyticsResponse () {
     document.getElementById("analyticsResponse").innerHTML="";
 }
 </script>

</head>
<body>


<%@ include file="/WEB-INF/include/header.jsp"%>
<% String get_analytics_list = D4mAnalyticsListModel.GET_ANALYTICS_LIST; %>
<input type="button" value="GetAnalyticsList"
	onclick="getAnalyticsList()">
<br>

<div id="analyticsList"></div>
<p/>
<div id="analyticsQueryForm"></div>
<div id="analyticsResponse"></div>
</body>
</html>