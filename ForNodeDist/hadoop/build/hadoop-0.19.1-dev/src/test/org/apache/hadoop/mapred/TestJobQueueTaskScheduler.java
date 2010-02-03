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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.hadoop.io.BytesWritable;

public class TestJobQueueTaskScheduler extends TestCase {
  
  private static int jobCounter;
  private static int taskCounter;
  
  static class FakeJobInProgress extends JobInProgress {
    
    private FakeTaskTrackerManager taskTrackerManager;
    
    public FakeJobInProgress(JobConf jobConf,
        FakeTaskTrackerManager taskTrackerManager) throws IOException {
      super(new JobID("test", ++jobCounter), jobConf);
      this.taskTrackerManager = taskTrackerManager;
      this.startTime = System.currentTimeMillis();
      this.status = new JobStatus(getJobID(), 0f, 0f, JobStatus.PREP);
      this.status.setJobPriority(JobPriority.NORMAL);
      this.status.setStartTime(startTime);
    }
    
    @Override
    public synchronized void initTasks() throws IOException {
      // do nothing
    }

    @Override
    public Task obtainNewMapTask(final TaskTrackerStatus tts, int clusterSize,
        int ignored) throws IOException {
      TaskAttemptID attemptId = getTaskAttemptID(true);
      Task task = new MapTask("", attemptId, 0, "", new BytesWritable()) {
        @Override
        public String toString() {
          return String.format("%s on %s", getTaskID(), tts.getTrackerName());
        }
      };
      taskTrackerManager.update(tts.getTrackerName(), task);
      runningMapTasks++;
      return task;
    }
    
    @Override
    public Task obtainNewReduceTask(final TaskTrackerStatus tts,
        int clusterSize, int ignored) throws IOException {
      TaskAttemptID attemptId = getTaskAttemptID(false);
      Task task = new ReduceTask("", attemptId, 0, 10) {
        @Override
        public String toString() {
          return String.format("%s on %s", getTaskID(), tts.getTrackerName());
        }
      };
      taskTrackerManager.update(tts.getTrackerName(), task);
      runningReduceTasks++;
      return task;
    }
    
    private TaskAttemptID getTaskAttemptID(boolean isMap) {
      JobID jobId = getJobID();
      return new TaskAttemptID(jobId.getJtIdentifier(),
          jobId.getId(), isMap, ++taskCounter, 0);
    }
  }
  
  static class FakeTaskTrackerManager implements TaskTrackerManager {
    
    int maps = 0;
    int reduces = 0;
    int maxMapTasksPerTracker = 2;
    int maxReduceTasksPerTracker = 2;
    List<JobInProgressListener> listeners =
      new ArrayList<JobInProgressListener>();
    QueueManager queueManager;
    
    private Map<String, TaskTrackerStatus> trackers =
      new HashMap<String, TaskTrackerStatus>();

    public FakeTaskTrackerManager() {
      JobConf conf = new JobConf();
      queueManager = new QueueManager(conf);
      trackers.put("tt1", new TaskTrackerStatus("tt1", "tt1.host", 1,
          new ArrayList<TaskStatus>(), 0,
          maxMapTasksPerTracker, maxReduceTasksPerTracker));
      trackers.put("tt2", new TaskTrackerStatus("tt2", "tt2.host", 2,
          new ArrayList<TaskStatus>(), 0,
          maxMapTasksPerTracker, maxReduceTasksPerTracker));
    }
    
    @Override
    public ClusterStatus getClusterStatus() {
      int numTrackers = trackers.size();
      return new ClusterStatus(numTrackers, maps, reduces,
          numTrackers * maxMapTasksPerTracker,
          numTrackers * maxReduceTasksPerTracker,
          JobTracker.State.RUNNING);
    }

    @Override
    public int getNumberOfUniqueHosts() {
      return 0;
    }

    @Override
    public Collection<TaskTrackerStatus> taskTrackers() {
      return trackers.values();
    }


    @Override
    public void addJobInProgressListener(JobInProgressListener listener) {
      listeners.add(listener);
    }

    @Override
    public void removeJobInProgressListener(JobInProgressListener listener) {
      listeners.remove(listener);
    }
    
    @Override
    public QueueManager getQueueManager() {
      return queueManager;
    }
    
    @Override
    public int getNextHeartbeatInterval() {
      return MRConstants.HEARTBEAT_INTERVAL_MIN;
    }
    
    // Test methods
    
    public void submitJob(JobInProgress job) {
      for (JobInProgressListener listener : listeners) {
        listener.jobAdded(job);
      }
    }
    
    public TaskTrackerStatus getTaskTracker(String trackerID) {
      return trackers.get(trackerID);
    }
    
    public void update(String taskTrackerName, final Task t) {
      if (t.isMapTask()) {
        maps++;
      } else {
        reduces++;
      }
      TaskStatus status = new TaskStatus() {
        @Override
        public boolean getIsMap() {
          return t.isMapTask();
        }
      };
      status.setRunState(TaskStatus.State.RUNNING);
      trackers.get(taskTrackerName).getTaskReports().add(status);
    }
    
  }
  
  protected JobConf jobConf;
  protected TaskScheduler scheduler;
  private FakeTaskTrackerManager taskTrackerManager;

  @Override
  protected void setUp() throws Exception {
    jobCounter = 0;
    taskCounter = 0;
    jobConf = new JobConf();
    jobConf.setNumMapTasks(10);
    jobConf.setNumReduceTasks(10);
    taskTrackerManager = new FakeTaskTrackerManager();
    scheduler = createTaskScheduler();
    scheduler.setConf(jobConf);
    scheduler.setTaskTrackerManager(taskTrackerManager);
    scheduler.start();
  }
  
  @Override
  protected void tearDown() throws Exception {
    if (scheduler != null) {
      scheduler.terminate();
    }
  }
  
  protected TaskScheduler createTaskScheduler() {
    return new JobQueueTaskScheduler();
  }
  
  protected void submitJobs(int number, int state)
    throws IOException {
    for (int i = 0; i < number; i++) {
      JobInProgress job = new FakeJobInProgress(jobConf, taskTrackerManager);
      job.getStatus().setRunState(state);
      taskTrackerManager.submitJob(job);
    }
  }

  public void testTaskNotAssignedWhenNoJobsArePresent() throws IOException {
    assertNull(scheduler.assignTasks(tracker("tt1")));
  }

  public void testNonRunningJobsAreIgnored() throws IOException {
    submitJobs(1, JobStatus.PREP);
    submitJobs(1, JobStatus.SUCCEEDED);
    submitJobs(1, JobStatus.FAILED);
    submitJobs(1, JobStatus.KILLED);
    assertNull(scheduler.assignTasks(tracker("tt1")));
  }
  
  public void testDefaultTaskAssignment() throws IOException {
    submitJobs(2, JobStatus.RUNNING);
    
    // All slots are filled with job 1
    checkAssignment("tt1", "attempt_test_0001_m_000001_0 on tt1");
    checkAssignment("tt1", "attempt_test_0001_m_000002_0 on tt1");
    checkAssignment("tt1", "attempt_test_0001_r_000003_0 on tt1");
    checkAssignment("tt1", "attempt_test_0001_r_000004_0 on tt1");
    checkAssignment("tt2", "attempt_test_0001_m_000005_0 on tt2");
    checkAssignment("tt2", "attempt_test_0001_m_000006_0 on tt2");
    checkAssignment("tt2", "attempt_test_0001_r_000007_0 on tt2");
    checkAssignment("tt2", "attempt_test_0001_r_000008_0 on tt2");
  }

  protected TaskTrackerStatus tracker(String taskTrackerName) {
    return taskTrackerManager.getTaskTracker(taskTrackerName);
  }
  
  protected void checkAssignment(String taskTrackerName,
      String expectedTaskString) throws IOException {
    List<Task> tasks = scheduler.assignTasks(tracker(taskTrackerName));
    assertNotNull(expectedTaskString, tasks);
    assertEquals(expectedTaskString, 1, tasks.size());
    assertEquals(expectedTaskString, tasks.get(0).toString());
  }
  
}
