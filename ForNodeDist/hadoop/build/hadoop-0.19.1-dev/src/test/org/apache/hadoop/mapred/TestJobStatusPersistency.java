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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

public class TestJobStatusPersistency extends ClusterMapReduceTestCase {
  private JobID runJob() throws Exception {
    OutputStream os = getFileSystem().create(new Path(getInputDir(), "text.txt"));
    Writer wr = new OutputStreamWriter(os);
    wr.write("hello1\n");
    wr.write("hello2\n");
    wr.write("hello3\n");
    wr.write("hello4\n");
    wr.close();

    JobConf conf = createJobConf();
    conf.setJobName("mr");

    conf.setInputFormat(TextInputFormat.class);

    conf.setMapOutputKeyClass(LongWritable.class);
    conf.setMapOutputValueClass(Text.class);

    conf.setOutputFormat(TextOutputFormat.class);
    conf.setOutputKeyClass(LongWritable.class);
    conf.setOutputValueClass(Text.class);

    conf.setMapperClass(org.apache.hadoop.mapred.lib.IdentityMapper.class);
    conf.setReducerClass(org.apache.hadoop.mapred.lib.IdentityReducer.class);

    FileInputFormat.setInputPaths(conf, getInputDir());

    FileOutputFormat.setOutputPath(conf, getOutputDir());

    return JobClient.runJob(conf).getID();
  }

  public void testNonPersistency() throws Exception {
    JobID jobId = runJob();
    JobClient jc = new JobClient(createJobConf());
    RunningJob rj = jc.getJob(jobId);
    assertNotNull(rj);
    stopCluster();
    startCluster(false, null);
    jc = new JobClient(createJobConf());
    rj = jc.getJob(jobId);
    assertNull(rj);
  }

  public void testPersistency() throws Exception {
    Properties config = new Properties();
    config.setProperty("mapred.job.tracker.persist.jobstatus.active", "true");
    config.setProperty("mapred.job.tracker.persist.jobstatus.hours", "1");
    stopCluster();
    startCluster(false, config);
    JobID jobId = runJob();
    JobClient jc = new JobClient(createJobConf());
    RunningJob rj0 = jc.getJob(jobId);
    assertNotNull(rj0);
    boolean sucessfull0 = rj0.isSuccessful();
    String jobName0 = rj0.getJobName();
    Counters counters0 = rj0.getCounters();
    TaskCompletionEvent[] events0 = rj0.getTaskCompletionEvents(0);

    stopCluster();
    startCluster(false, config);
     
    jc = new JobClient(createJobConf());
    RunningJob rj1 = jc.getJob(jobId);
    assertNotNull(rj1);
    assertEquals(sucessfull0, rj1.isSuccessful());
    assertEquals(jobName0, rj0.getJobName());
    assertEquals(counters0.size(), rj1.getCounters().size());

    TaskCompletionEvent[] events1 = rj1.getTaskCompletionEvents(0);
    assertEquals(events0.length, events1.length);    
    for (int i = 0; i < events0.length; i++) {
      assertEquals(events0[i].getTaskAttemptId(), events1[i].getTaskAttemptId());
      assertEquals(events0[i].getTaskStatus(), events1[i].getTaskStatus());
    }
  }

}
