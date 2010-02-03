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
package org.apache.hadoop.mapred;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.mapred.TestJobTrackerRestart;

import junit.framework.TestCase;
import java.io.*;

/** 
 * This test checks if the jobtracker can detect and recover a tracker that was
 * lost while the jobtracker was down.
 */
public class TestJobTrackerRestartWithLostTracker extends TestCase {
  final Path testDir = new Path("/jt-restart-lost-tt-testing");
  final Path inDir = new Path(testDir, "input");
  final Path shareDir = new Path(testDir, "share");
  final Path outputDir = new Path(testDir, "output");
  
  private JobConf configureJob(JobConf conf, int[] maps, int[] reduces,
                               String mapSignal, String redSignal) 
  throws IOException {
    JobPriority[] priority = new JobPriority[] {JobPriority.NORMAL};
    return TestJobTrackerRestart.getJobs(conf, priority, 
                                         maps, reduces, outputDir, inDir, 
                                         mapSignal, redSignal)[0];
  }
  
  public void testRecoveryWithLostTracker(MiniDFSCluster dfs,
                                          MiniMRCluster mr) 
  throws IOException {
    FileSystem fileSys = dfs.getFileSystem();
    JobConf jobConf = mr.createJobConf();
    int numMaps = 50;
    int numReds = 1;
    String mapSignalFile = TestJobTrackerRestart.getMapSignalFile(shareDir);
    String redSignalFile = TestJobTrackerRestart.getReduceSignalFile(shareDir);
    
    // Configure the jobs
    JobConf job = configureJob(jobConf, new int[] {numMaps}, 
                               new int[] {numReds}, 
                               mapSignalFile, redSignalFile);
      
    TestJobTrackerRestart.cleanUp(fileSys, shareDir);
    
    // Submit a master job   
    JobClient jobClient = new JobClient(job);
    RunningJob rJob = jobClient.submitJob(job);
    JobID id = rJob.getID();
    
    // wait for the job to be inited
    mr.initializeJob(id);
    
    // Make sure that the master job is 50% completed
    while (TestJobTrackerRestart.getJobStatus(jobClient, id).mapProgress() 
           < 0.5f) {
      TestJobTrackerRestart.waitFor(100);
    }

    // Kill the jobtracker
    mr.stopJobTracker();

    // Signal the maps to complete
    TestJobTrackerRestart.signalTasks(dfs, fileSys, true, 
                                      mapSignalFile, redSignalFile);
    
    // Enable recovery on restart
    mr.getJobTrackerConf().setBoolean("mapred.jobtracker.restart.recover", 
                                      true);
    
    // Kill the 2nd tasktracker
    mr.stopTaskTracker(1);
    
    // Wait for a minute before submitting a job
    TestJobTrackerRestart.waitFor(60 * 1000);
    
    // Restart the jobtracker
    mr.startJobTracker();

    // Check if the jobs are still running
    
    // Wait for the JT to be ready
    TestJobTrackerRestart.waitForJobTracker(jobClient);

    // Signal the reducers to complete
    TestJobTrackerRestart.signalTasks(dfs, fileSys, false, 
                                      mapSignalFile, redSignalFile);
    
    TestJobTrackerRestart.waitTillDone(jobClient);

    // Check if the tasks on the lost tracker got re-executed
    assertTrue("Tracker killed while the jobtracker was down did not get lost "
                + "upon restart", 
                jobClient.getClusterStatus().getTaskTrackers() 
                < mr.getNumTaskTrackers());
  }
  
  public void testRestartWithLostTracker() throws IOException {
    String namenode = null;
    MiniDFSCluster dfs = null;
    MiniMRCluster mr = null;
    FileSystem fileSys = null;

    try {
      Configuration conf = new Configuration();
      conf.setBoolean("dfs.replication.considerLoad", false);
      dfs = new MiniDFSCluster(conf, 1, true, null, null);
      dfs.waitActive();
      fileSys = dfs.getFileSystem();
      
      // clean up
      fileSys.delete(testDir, true);
      
      if (!fileSys.mkdirs(inDir)) {
        throw new IOException("Mkdirs failed to create " + inDir.toString());
      }

      // Write the input file
      TestRackAwareTaskPlacement.writeFile(dfs.getNameNode(), conf, 
                                           new Path(inDir + "/file"), 
                                           (short)1);

      dfs.startDataNodes(conf, 1, true, null, null, null, null);
      dfs.waitActive();

      namenode = (dfs.getFileSystem()).getUri().getHost() + ":" 
                 + (dfs.getFileSystem()).getUri().getPort();

      // Make sure that jobhistory leads to a proper job restart
      // So keep the blocksize and the buffer size small
      JobConf jtConf = new JobConf();
      jtConf.set("mapred.jobtracker.job.history.block.size", "1024");
      jtConf.set("mapred.jobtracker.job.history.buffer.size", "1024");
      jtConf.setInt("mapred.tasktracker.reduce.tasks.maximum", 1);
      jtConf.setLong("mapred.tasktracker.expiry.interval", 25 * 1000);
      
      mr = new MiniMRCluster(2, namenode, 1, null, null, jtConf);
      
      // Test Lost tracker case
      testRecoveryWithLostTracker(dfs, mr);
    } finally {
      if (mr != null) {
        try {
          mr.shutdown();
        } catch (Exception e) {}
      }
      if (dfs != null) {
        try {
          dfs.shutdown();
        } catch (Exception e) {}
      }
    }
  }

  public static void main(String[] args) throws IOException {
    new TestJobTrackerRestartWithLostTracker().testRestartWithLostTracker();
  }
}