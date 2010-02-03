/*
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

import junit.framework.TestCase;
import org.apache.hadoop.mapred.JobClient.RawSplit;

public class TestResourceEstimation extends TestCase {
  

  public void testResourceEstimator() throws Exception {
    final int maps = 100;
    final int reduces = 2;
    final int singleMapOutputSize = 1000;
    JobConf jc = new JobConf();
    JobID jid = new JobID("testJT", 0);
    jc.setNumMapTasks(maps);
    jc.setNumReduceTasks(reduces);
    
    JobInProgress jip = new JobInProgress(jid, jc);
    //unfortunately, we can't set job input size from here.
    ResourceEstimator re = new ResourceEstimator(jip);
    
    for(int i = 0; i < maps / 10 -1; ++i) {

      long estOutSize = re.getEstimatedMapOutputSize();
      System.out.println(estOutSize);
      assertEquals(0, estOutSize);
      
      TaskStatus ts = new MapTaskStatus();
      ts.setOutputSize(singleMapOutputSize);
      RawSplit split = new RawSplit();
      split.setDataLength(0);
      TaskInProgress tip = new TaskInProgress(jid, "", split, null, jc, jip, 0);
      re.updateWithCompletedTask(ts, tip);
    }
    
    assertEquals(2* singleMapOutputSize, re.getEstimatedMapOutputSize());
    assertEquals(2* singleMapOutputSize * maps / reduces, re.getEstimatedReduceInputSize());
    
  }

}
