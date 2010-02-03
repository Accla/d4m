/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hdfs.server.namenode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.protocol.DatanodeID;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.FSConstants.UpgradeAction;
import org.apache.hadoop.hdfs.server.common.HdfsConstants;
import org.apache.hadoop.hdfs.server.common.UpgradeStatusReport;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.*;

public class JspHelper {
  final static public String WEB_UGI_PROPERTY_NAME = "dfs.web.ugi";

  static FSNamesystem fsn = null;
  public static InetSocketAddress nameNodeAddr;
  public static Configuration conf = new Configuration();
  public static final UnixUserGroupInformation webUGI
  = UnixUserGroupInformation.createImmutable(
      conf.getStrings(WEB_UGI_PROPERTY_NAME));

  public static int defaultChunkSizeToView = 
    conf.getInt("dfs.default.chunk.view.size", 32 * 1024);
  static Random rand = new Random();

  public JspHelper() {
    if (DataNode.getDataNode() != null) {
      nameNodeAddr = DataNode.getDataNode().getNameNodeAddr();
    }
    else {
      fsn = FSNamesystem.getFSNamesystem();
      nameNodeAddr = new InetSocketAddress(fsn.getDFSNameNodeMachine(),
                                           fsn.getDFSNameNodePort()); 
    }      

    UnixUserGroupInformation.saveToConf(conf,
        UnixUserGroupInformation.UGI_PROPERTY_NAME, webUGI);
  }

  public DatanodeID randomNode() throws IOException {
    return fsn.getRandomDatanode();
  }

  public DatanodeInfo bestNode(LocatedBlock blk) throws IOException {
    TreeSet<DatanodeInfo> deadNodes = new TreeSet<DatanodeInfo>();
    DatanodeInfo chosenNode = null;
    int failures = 0;
    Socket s = null;
    DatanodeInfo [] nodes = blk.getLocations();
    if (nodes == null || nodes.length == 0) {
      throw new IOException("No nodes contain this block");
    }
    while (s == null) {
      if (chosenNode == null) {
        do {
          chosenNode = nodes[rand.nextInt(nodes.length)];
        } while (deadNodes.contains(chosenNode));
      }
      int index = rand.nextInt(nodes.length);
      chosenNode = nodes[index];

      //just ping to check whether the node is alive
      InetSocketAddress targetAddr = NetUtils.createSocketAddr(
          chosenNode.getHost() + ":" + chosenNode.getInfoPort());
        
      try {
        s = new Socket();
        s.connect(targetAddr, HdfsConstants.READ_TIMEOUT);
        s.setSoTimeout(HdfsConstants.READ_TIMEOUT);
      } catch (IOException e) {
        deadNodes.add(chosenNode);
        s.close();
        s = null;
        failures++;
      }
      if (failures == nodes.length)
        throw new IOException("Could not reach the block containing the data. Please try again");
        
    }
    s.close();
    return chosenNode;
  }
  public void streamBlockInAscii(InetSocketAddress addr, long blockId, 
                                 long genStamp, long blockSize, 
                                 long offsetIntoBlock, long chunkSizeToView, JspWriter out) 
    throws IOException {
    if (chunkSizeToView == 0) return;
    Socket s = new Socket();
    s.connect(addr, HdfsConstants.READ_TIMEOUT);
    s.setSoTimeout(HdfsConstants.READ_TIMEOUT);
      
      long amtToRead = Math.min(chunkSizeToView, blockSize - offsetIntoBlock);     
      
      // Use the block name for file name. 
      DFSClient.BlockReader blockReader = 
        DFSClient.BlockReader.newBlockReader(s, addr.toString() + ":" + blockId,
                                             blockId, genStamp ,offsetIntoBlock, 
                                             amtToRead, 
                                             conf.getInt("io.file.buffer.size",
                                                         4096));
        
    byte[] buf = new byte[(int)amtToRead];
    int readOffset = 0;
    int retries = 2;
    while ( amtToRead > 0 ) {
      int numRead;
      try {
        numRead = blockReader.readAll(buf, readOffset, (int)amtToRead);
      }
      catch (IOException e) {
        retries--;
        if (retries == 0)
          throw new IOException("Could not read data from datanode");
        continue;
      }
      amtToRead -= numRead;
      readOffset += numRead;
    }
    blockReader = null;
    s.close();
    out.print(new String(buf));
  }
  public void DFSNodesStatus(ArrayList<DatanodeDescriptor> live,
                             ArrayList<DatanodeDescriptor> dead) {
    if (fsn != null)
      fsn.DFSNodesStatus(live, dead);
  }
  public void addTableHeader(JspWriter out) throws IOException {
    out.print("<table border=\"1\""+
              " cellpadding=\"2\" cellspacing=\"2\">");
    out.print("<tbody>");
  }
  public void addTableRow(JspWriter out, String[] columns) throws IOException {
    out.print("<tr>");
    for (int i = 0; i < columns.length; i++) {
      out.print("<td style=\"vertical-align: top;\"><B>"+columns[i]+"</B><br></td>");
    }
    out.print("</tr>");
  }
  public void addTableRow(JspWriter out, String[] columns, int row) throws IOException {
    out.print("<tr>");
      
    for (int i = 0; i < columns.length; i++) {
      if (row/2*2 == row) {//even
        out.print("<td style=\"vertical-align: top;background-color:LightGrey;\"><B>"+columns[i]+"</B><br></td>");
      } else {
        out.print("<td style=\"vertical-align: top;background-color:LightBlue;\"><B>"+columns[i]+"</B><br></td>");
          
      }
    }
    out.print("</tr>");
  }
  public void addTableFooter(JspWriter out) throws IOException {
    out.print("</tbody></table>");
  }

  public String getSafeModeText() {
    if (!fsn.isInSafeMode())
      return "";
    return "Safe mode is ON. <em>" + fsn.getSafeModeTip() + "</em><br>";
  }

  public String getInodeLimitText() {
    long inodes = fsn.dir.totalInodes();
    long blocks = fsn.getBlocksTotal();
    long maxobjects = fsn.getMaxObjects();
    long totalMemory = Runtime.getRuntime().totalMemory();   
    long maxMemory = Runtime.getRuntime().maxMemory();   

    long used = (totalMemory * 100)/maxMemory;
 
    String str = inodes + " files and directories, " +
                 blocks + " blocks = " +
                 (inodes + blocks) + " total";
    if (maxobjects != 0) {
      long pct = ((inodes + blocks) * 100)/maxobjects;
      str += " / " + maxobjects + " (" + pct + "%)";
    }
    str += ".  Heap Size is " + FsShell.byteDesc(totalMemory) + " / " + 
           FsShell.byteDesc(maxMemory) + 
           " (" + used + "%) <br>";
    return str;
  }

  public String getUpgradeStatusText() {
    String statusText = "";
    try {
      UpgradeStatusReport status = 
        fsn.distributedUpgradeProgress(UpgradeAction.GET_STATUS);
      statusText = (status == null ? 
          "There are no upgrades in progress." :
            status.getStatusText(false));
    } catch(IOException e) {
      statusText = "Upgrade status unknown.";
    }
    return statusText;
  }

  public void sortNodeList(ArrayList<DatanodeDescriptor> nodes,
                           String field, String order) {
        
    class NodeComapare implements Comparator<DatanodeDescriptor> {
      static final int 
        FIELD_NAME              = 1,
        FIELD_LAST_CONTACT      = 2,
        FIELD_BLOCKS            = 3,
        FIELD_CAPACITY          = 4,
        FIELD_USED              = 5,
        FIELD_PERCENT_USED      = 6,
        FIELD_NONDFS_USED       = 7,
        FIELD_REMAINING         = 8,
        FIELD_PERCENT_REMAINING = 9,
        SORT_ORDER_ASC          = 1,
        SORT_ORDER_DSC          = 2;

      int sortField = FIELD_NAME;
      int sortOrder = SORT_ORDER_ASC;
            
      public NodeComapare(String field, String order) {
        if (field.equals("lastcontact")) {
          sortField = FIELD_LAST_CONTACT;
        } else if (field.equals("capacity")) {
          sortField = FIELD_CAPACITY;
        } else if (field.equals("used")) {
          sortField = FIELD_USED;
        } else if (field.equals("nondfsused")) {
          sortField = FIELD_NONDFS_USED;
        } else if (field.equals("remaining")) {
          sortField = FIELD_REMAINING;
        } else if (field.equals("pcused")) {
          sortField = FIELD_PERCENT_USED;
        } else if (field.equals("pcremaining")) {
          sortField = FIELD_PERCENT_REMAINING;
        } else if (field.equals("blocks")) {
          sortField = FIELD_BLOCKS;
        } else {
          sortField = FIELD_NAME;
        }
                
        if (order.equals("DSC")) {
          sortOrder = SORT_ORDER_DSC;
        } else {
          sortOrder = SORT_ORDER_ASC;
        }
      }

      public int compare(DatanodeDescriptor d1,
                         DatanodeDescriptor d2) {
        int ret = 0;
        switch (sortField) {
        case FIELD_LAST_CONTACT:
          ret = (int) (d2.getLastUpdate() - d1.getLastUpdate());
          break;
        case FIELD_CAPACITY:
          long  dlong = d1.getCapacity() - d2.getCapacity();
          ret = (dlong < 0) ? -1 : ((dlong > 0) ? 1 : 0);
          break;
        case FIELD_USED:
          dlong = d1.getDfsUsed() - d2.getDfsUsed();
          ret = (dlong < 0) ? -1 : ((dlong > 0) ? 1 : 0);
          break;
        case FIELD_NONDFS_USED:
          dlong = d1.getNonDfsUsed() - d2.getNonDfsUsed();
          ret = (dlong < 0) ? -1 : ((dlong > 0) ? 1 : 0);
          break;
        case FIELD_REMAINING:
          dlong = d1.getRemaining() - d2.getRemaining();
          ret = (dlong < 0) ? -1 : ((dlong > 0) ? 1 : 0);
          break;
        case FIELD_PERCENT_USED:
          double ddbl =((d1.getDfsUsedPercent())-
                        (d2.getDfsUsedPercent()));
          ret = (ddbl < 0) ? -1 : ((ddbl > 0) ? 1 : 0);
          break;
        case FIELD_PERCENT_REMAINING:
          ddbl =((d1.getRemainingPercent())-
                 (d2.getRemainingPercent()));
          ret = (ddbl < 0) ? -1 : ((ddbl > 0) ? 1 : 0);
          break;
        case FIELD_BLOCKS:
          ret = d1.numBlocks() - d2.numBlocks();
          break;
        case FIELD_NAME: 
          ret = d1.getHostName().compareTo(d2.getHostName());
          break;
        }
        return (sortOrder == SORT_ORDER_DSC) ? -ret : ret;
      }
    }
        
    Collections.sort(nodes, new NodeComapare(field, order));
  }

  public static void printPathWithLinks(String dir, JspWriter out, int namenodeInfoPort ) throws IOException {
    try {
      String[] parts = dir.split(Path.SEPARATOR);
      StringBuilder tempPath = new StringBuilder(dir.length());
      out.print("<a href=\"browseDirectory.jsp" + "?dir="+ Path.SEPARATOR
          + "&namenodeInfoPort=" + namenodeInfoPort
          + "\">" + Path.SEPARATOR + "</a>");
      tempPath.append(Path.SEPARATOR);
      for (int i = 0; i < parts.length-1; i++) {
        if (!parts[i].equals("")) {
          tempPath.append(parts[i]);
          out.print("<a href=\"browseDirectory.jsp" + "?dir="
              + tempPath.toString() + "&namenodeInfoPort=" + namenodeInfoPort);
          out.print("\">" + parts[i] + "</a>" + Path.SEPARATOR);
          tempPath.append(Path.SEPARATOR);
        }
      }
      if(parts.length > 0) {
        out.print(parts[parts.length-1]);
      }
    }
    catch (UnsupportedEncodingException ex) {
      ex.printStackTrace();
    }
  }

  public static void printGotoForm(JspWriter out, int namenodeInfoPort, String file) throws IOException {
    out.print("<form action=\"browseDirectory.jsp\" method=\"get\" name=\"goto\">");
    out.print("Goto : ");
    out.print("<input name=\"dir\" type=\"text\" width=\"50\" id\"dir\" value=\""+ file+"\">");
    out.print("<input name=\"go\" type=\"submit\" value=\"go\">");
    out.print("<input name=\"namenodeInfoPort\" type=\"hidden\" "
        + "value=\"" + namenodeInfoPort  + "\">");
    out.print("</form>");
  }
  
  public static void createTitle(JspWriter out, 
      HttpServletRequest req, String  file) throws IOException{
    if(file == null) file = "";
    int start = Math.max(0,file.length() - 100);
    if(start != 0)
      file = "..." + file.substring(start, file.length());
    out.print("<title>HDFS:" + file + "</title>");
  }
}
