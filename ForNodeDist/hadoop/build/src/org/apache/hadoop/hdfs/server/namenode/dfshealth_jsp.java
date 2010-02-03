package org.apache.hadoop.hdfs.server.namenode;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.*;
import org.apache.hadoop.hdfs.server.namenode.*;
import org.apache.hadoop.hdfs.server.datanode.*;
import org.apache.hadoop.hdfs.protocol.*;
import org.apache.hadoop.util.*;
import java.text.DateFormat;
import java.lang.Math;
import java.net.URLEncoder;

public final class dfshealth_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

FSNamesystem fsn = FSNamesystem.getFSNamesystem();
  String namenodeLabel = fsn.getDFSNameNodeMachine() + ":" + fsn.getDFSNameNodePort();
  JspHelper jspHelper = new JspHelper();

  int rowNum = 0;
  int colNum = 0;

  String rowTxt() { colNum = 0;
      return "<tr class=\"" + (((rowNum++)%2 == 0)? "rowNormal" : "rowAlt")
          + "\"> "; }
  String colTxt() { return "<td id=\"col" + ++colNum + "\"> "; }
  void counterReset () { colNum = 0; rowNum = 0 ; }

  long diskBytes = 1024 * 1024 * 1024;
  String diskByteStr = "GB";

  String sorterField = null;
  String sorterOrder = null;

  String NodeHeaderStr(String name) {
      String ret = "class=header";
      String order = "ASC";
      if ( name.equals( sorterField ) ) {
          ret += sorterOrder;
          if ( sorterOrder.equals("ASC") )
              order = "DSC";
      }
      ret += " onClick=\"window.document.location=" +
          "'/dfshealth.jsp?sorter/field=" + name + "&sorter/order=" +
          order + "'\" title=\"sort on this column\"";
      
      return ret;
  }
      
  public void generateNodeData( JspWriter out, DatanodeDescriptor d,
                                    String suffix, boolean alive )
    throws IOException {
      
    /* Say the datanode is dn1.hadoop.apache.org with ip 192.168.0.5
       we use:
       1) d.getHostName():d.getPort() to display.
           Domain and port are stripped if they are common across the nodes.
           i.e. "dn1"
       2) d.getHost():d.Port() for "title".
          i.e. "192.168.0.5:50010"
       3) d.getHostName():d.getInfoPort() for url.
          i.e. "http://dn1.hadoop.apache.org:50075/..."
          Note that "d.getHost():d.getPort()" is what DFS clients use
          to interact with datanodes.
    */
    // from nn_browsedfscontent.jsp:
    String url = "http://" + d.getHostName() + ":" + d.getInfoPort() +
                 "/browseDirectory.jsp?namenodeInfoPort=" +
                 fsn.getNameNodeInfoPort() + "&dir=" +
                 URLEncoder.encode("/", "UTF-8");
     
    String name = d.getHostName() + ":" + d.getPort();
    if ( !name.matches( "\\d+\\.\\d+.\\d+\\.\\d+.*" ) ) 
        name = name.replaceAll( "\\.[^.:]*", "" );    
    int idx = (suffix != null && name.endsWith( suffix )) ?
        name.indexOf( suffix ) : -1;
    
    out.print( rowTxt() + "<td class=\"name\"><a title=\""
               + d.getHost() + ":" + d.getPort() +
               "\" href=\"" + url + "\">" +
               (( idx > 0 ) ? name.substring(0, idx) : name) + "</a>" +
               (( alive ) ? "" : "\n") );
    if ( !alive )
        return;
    
    long c = d.getCapacity();
    long u = d.getDfsUsed();
    long nu = d.getNonDfsUsed();
    long r = d.getRemaining();
    String percentUsed = FsShell.limitDecimalTo2(d.getDfsUsedPercent());    
    String percentRemaining = FsShell.limitDecimalTo2(d.getRemainingPercent());    
    
    String adminState = (d.isDecommissioned() ? "Decommissioned" :
                         (d.isDecommissionInProgress() ? "Decommission In Progress":
                          "In Service"));
    
    long timestamp = d.getLastUpdate();
    long currentTime = System.currentTimeMillis();
    out.print("<td class=\"lastcontact\"> " +
              ((currentTime - timestamp)/1000) +
              "<td class=\"adminstate\">" +
              adminState +
              "<td align=\"right\" class=\"capacity\">" +
              FsShell.limitDecimalTo2(c*1.0/diskBytes) +
              "<td align=\"right\" class=\"used\">" +
              FsShell.limitDecimalTo2(u*1.0/diskBytes) +      
              "<td align=\"right\" class=\"nondfsused\">" +
              FsShell.limitDecimalTo2(nu*1.0/diskBytes) +      
              "<td align=\"right\" class=\"remaining\">" +
              FsShell.limitDecimalTo2(r*1.0/diskBytes) +      
              "<td align=\"right\" class=\"pcused\">" + percentUsed +
              "<td class=\"pcused\">" +
              ServletUtil.percentageGraph( (int)Double.parseDouble(percentUsed) , 100) +
              "<td align=\"right\" class=\"pcremaining`\">" + percentRemaining +
              "<td title=" + "\"blocks scheduled : " + d.getBlocksScheduled() + 
              "\" class=\"blocks\">" + d.numBlocks() + "\n");
  }

  public void generateDFSHealthReport(JspWriter out,
                                      HttpServletRequest request)
                                      throws IOException {
    ArrayList<DatanodeDescriptor> live = new ArrayList<DatanodeDescriptor>();
    ArrayList<DatanodeDescriptor> dead = new ArrayList<DatanodeDescriptor>();
    jspHelper.DFSNodesStatus(live, dead);

    sorterField = request.getParameter("sorter/field");
    sorterOrder = request.getParameter("sorter/order");
    if ( sorterField == null )
        sorterField = "name";
    if ( sorterOrder == null )
        sorterOrder = "ASC";

    // Find out common suffix. Should this be before or after the sort?
    String port_suffix = null;
    if ( live.size() > 0 ) {
        String name = live.get(0).getName();
        int idx = name.indexOf(':');
        if ( idx > 0 ) {
            port_suffix = name.substring( idx );
        }
        
        for ( int i=1; port_suffix != null && i < live.size(); i++ ) {
            if ( live.get(i).getName().endsWith( port_suffix ) == false ) {
                port_suffix = null;
                break;
            }
        }
    }
        
    counterReset();
    
    long total = fsn.getCapacityTotal();
    long remaining = fsn.getCapacityRemaining();
    long used = fsn.getCapacityUsed();
    long nonDFS = fsn.getCapacityUsedNonDFS();
    float percentUsed = fsn.getCapacityUsedPercent();
    float percentRemaining = fsn.getCapacityRemainingPercent();

    out.print( "<div id=\"dfstable\"> <table>\n" +
	       rowTxt() + colTxt() + "Configured Capacity" + colTxt() + ":" + colTxt() +
	       FsShell.byteDesc( total ) +
	       rowTxt() + colTxt() + "DFS Used" + colTxt() + ":" + colTxt() +
	       FsShell.byteDesc( used ) +
	       rowTxt() + colTxt() + "Non DFS Used" + colTxt() + ":" + colTxt() +
	       FsShell.byteDesc( nonDFS ) +
	       rowTxt() + colTxt() + "DFS Remaining" + colTxt() + ":" + colTxt() +
	       FsShell.byteDesc( remaining ) +
	       rowTxt() + colTxt() + "DFS Used%" + colTxt() + ":" + colTxt() +
	       FsShell.limitDecimalTo2(percentUsed) + " %" +
	       rowTxt() + colTxt() + "DFS Remaining%" + colTxt() + ":" + colTxt() +
	       FsShell.limitDecimalTo2(percentRemaining) + " %" +
	       rowTxt() + colTxt() +
               "<a href=\"#LiveNodes\">Live Nodes</a> " +
               colTxt() + ":" + colTxt() + live.size() +
	       rowTxt() + colTxt() +
               "<a href=\"#DeadNodes\">Dead Nodes</a> " +
               colTxt() + ":" + colTxt() + dead.size() +
               "</table></div><br><hr>\n" );
    
    if (live.isEmpty() && dead.isEmpty()) {
        out.print("There are no datanodes in the cluster");
    }
    else {
        
	out.print( "<div id=\"dfsnodetable\"> "+
                   "<a name=\"LiveNodes\" id=\"title\">" +
                   "Live Datanodes : " + live.size() + "</a>" +
                   "<br><br>\n<table border=1 cellspacing=0>\n" );

        counterReset();
        
	if ( live.size() > 0 ) {
            
            if ( live.get(0).getCapacity() > 1024 * diskBytes ) {
                diskBytes *= 1024;
                diskByteStr = "TB";
            }

      out.print( "<tr class=\"headerRow\"> <th " +
                 NodeHeaderStr("name") + "> Node <th " +
                 NodeHeaderStr("lastcontact") + "> Last <br>Contact <th " +
                 NodeHeaderStr("adminstate") + "> Admin State <th " +
                 NodeHeaderStr("capacity") + "> Configured <br>Capacity (" + 
                 diskByteStr + ") <th " + 
                 NodeHeaderStr("used") + "> Used <br>(" + 
                 diskByteStr + ") <th " + 
                 NodeHeaderStr("nondfsused") + "> Non DFS <br>Used (" + 
                 diskByteStr + ") <th " + 
                 NodeHeaderStr("remaining") + "> Remaining <br>(" + 
                 diskByteStr + ") <th " + 
                 NodeHeaderStr("pcused") + "> Used <br>(%) <th " + 
                 NodeHeaderStr("pcused") + "> Used <br>(%) <th " +
                 NodeHeaderStr("pcremaining") + "> Remaining <br>(%) <th " +
                 NodeHeaderStr("blocks") + "> Blocks\n" );
            
      jspHelper.sortNodeList(live, sorterField, sorterOrder);
      for ( int i=0; i < live.size(); i++ ) {
        generateNodeData( out, live.get(i), port_suffix, true );
      }
    }
    out.print("</table>\n");
    
    counterReset();
	
	out.print("<br> <a name=\"DeadNodes\" id=\"title\"> " +
                  " Dead Datanodes : " +dead.size() + "</a><br><br>\n");

	if ( dead.size() > 0 ) {
	    out.print( "<table border=1 cellspacing=0> <tr id=\"row1\"> " +
		       "<td> Node \n" );
	    
      jspHelper.sortNodeList(dead, "name", "ASC");
	    for ( int i=0; i < dead.size() ; i++ ) {
                generateNodeData( out, dead.get(i), port_suffix, false );
	    }
	    
	    out.print("</table>\n");
	}
	out.print("</div>");
    }
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
      out.write("\n\n<html>\n\n<link rel=\"stylesheet\" type=\"text/css\" href=\"/static/hadoop.css\">\n<title>Hadoop NameNode ");
      out.print(namenodeLabel);
      out.write("</title>\n    \n<body>\n<h1>NameNode '");
      out.print(namenodeLabel);
      out.write("'</h1>\n\n\n<div id=\"dfstable\"> <table>\t  \n<tr> <td id=\"col1\"> Started: <td> ");
      out.print( fsn.getStartTime());
      out.write("\n<tr> <td id=\"col1\"> Version: <td> ");
      out.print( VersionInfo.getVersion());
      out.write(',');
      out.write(' ');
      out.write('r');
      out.print( VersionInfo.getRevision());
      out.write("\n<tr> <td id=\"col1\"> Compiled: <td> ");
      out.print( VersionInfo.getDate());
      out.write(" by ");
      out.print( VersionInfo.getUser());
      out.write("\n<tr> <td id=\"col1\"> Upgrades: <td> ");
      out.print( jspHelper.getUpgradeStatusText());
      out.write("\n</table></div><br>\t\t\t\t      \n\n<b><a href=\"/nn_browsedfscontent.jsp\">Browse the filesystem</a></b><br>\n<b><a href=\"/logs/\">Namenode Logs</a></b>\n\n<hr>\n<h3>Cluster Summary</h3>\n<b> ");
      out.print( jspHelper.getSafeModeText());
      out.write(" </b>\n<b> ");
      out.print( jspHelper.getInodeLimitText());
      out.write(" </b>\n");
 
    generateDFSHealthReport(out, request); 

      out.write('\n');
      out.write('\n');

out.println(ServletUtil.htmlFooter());

      out.write('\n');
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
