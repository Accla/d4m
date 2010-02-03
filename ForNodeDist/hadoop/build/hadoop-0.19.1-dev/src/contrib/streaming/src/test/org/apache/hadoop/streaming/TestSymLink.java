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

package org.apache.hadoop.streaming;

import junit.framework.TestCase;
import java.io.*;
import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.hdfs.MiniDFSCluster;
/**
 * This test case tests the symlink creation
 * utility provided by distributed caching 
 */
public class TestSymLink extends TestCase
{
  String INPUT_FILE = "/testing-streaming/input.txt";
  String OUTPUT_DIR = "/testing-streaming/out";
  String CACHE_FILE = "/testing-streaming/cache.txt";
  String input = "check to see if we can read this none reduce";
  String map = "xargs cat ";
  String reduce = "cat";
  String mapString = "testlink\n";
  String cacheString = "This is just the cache string";
  StreamJob job;

  public TestSymLink() throws IOException
  {
  }

  public void testSymLink()
  {
    try {
      boolean mayExit = false;
      MiniMRCluster mr = null;
      MiniDFSCluster dfs = null; 
      try{
        Configuration conf = new Configuration();
        dfs = new MiniDFSCluster(conf, 1, true, null);
        FileSystem fileSys = dfs.getFileSystem();
        String namenode = fileSys.getName();
        mr  = new MiniMRCluster(1, namenode, 3);
        // During tests, the default Configuration will use a local mapred
        // So don't specify -config or -cluster
        String strJobtracker = "mapred.job.tracker=" + "localhost:" + mr.getJobTrackerPort();
        String strNamenode = "fs.default.name=" + namenode;
        String argv[] = new String[] {
          "-input", INPUT_FILE,
          "-output", OUTPUT_DIR,
          "-mapper", map,
          "-reducer", reduce,
          //"-verbose",
          //"-jobconf", "stream.debug=set"
          "-jobconf", strNamenode,
          "-jobconf", strJobtracker,
          "-jobconf", "stream.tmpdir="+System.getProperty("test.build.data","/tmp"),
          "-jobconf", "mapred.child.java.opts=-Dcontrib.name=" + System.getProperty("contrib.name") + " " +
                      "-Dbuild.test=" + System.getProperty("build.test") + " " +
                      conf.get("mapred.child.java.opts",""),
          "-cacheFile", "hdfs://"+fileSys.getName()+CACHE_FILE + "#testlink"
        };

        fileSys.delete(new Path(OUTPUT_DIR));
        
        DataOutputStream file = fileSys.create(new Path(INPUT_FILE));
        file.writeBytes(mapString);
        file.close();
        file = fileSys.create(new Path(CACHE_FILE));
        file.writeBytes(cacheString);
        file.close();
          
        job = new StreamJob(argv, mayExit);      
        job.go();

        fileSys = dfs.getFileSystem();
        String line = null;
        Path[] fileList = FileUtil.stat2Paths(fileSys.listStatus(
                                                new Path(OUTPUT_DIR),
                                                new OutputLogFilter()));
        for (int i = 0; i < fileList.length; i++){
          System.out.println(fileList[i].toString());
          BufferedReader bread =
            new BufferedReader(new InputStreamReader(fileSys.open(fileList[i])));
          line = bread.readLine();
          System.out.println(line);
        }
        assertEquals(cacheString + "\t", line);
      } finally{
        if (dfs != null) { dfs.shutdown(); }
        if (mr != null) { mr.shutdown();}
      }
      
    } catch(Exception e) {
      failTrace(e);
    }
  }

  void failTrace(Exception e)
  {
    StringWriter sw = new StringWriter();
    e.printStackTrace(new PrintWriter(sw));
    fail(sw.toString());
  }

  public static void main(String[]args) throws Exception
  {
    new TestStreaming().testCommandLine();
  }

}
