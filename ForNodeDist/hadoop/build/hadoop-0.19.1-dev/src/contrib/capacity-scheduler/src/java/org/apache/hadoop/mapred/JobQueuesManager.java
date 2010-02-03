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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.mapred.JobQueueJobInProgressListener.JobSchedulingInfo;
import org.apache.hadoop.mapred.JobStatusChangeEvent.EventType;

/**
 * A {@link JobInProgressListener} that maintains the jobs being managed in
 * one or more queues. 
 */
class JobQueuesManager extends JobInProgressListener {

  /* 
   * If a queue supports priorities, jobs must be 
   * sorted on priorities, and then on their start times (technically, 
   * their insertion time.  
   * If a queue doesn't support priorities, jobs are
   * sorted based on their start time.  
   */
  
  // comparator for jobs in queues that don't support priorities
  private static final Comparator<JobSchedulingInfo> STARTTIME_JOB_COMPARATOR
    = new Comparator<JobSchedulingInfo>() {
    public int compare(JobSchedulingInfo o1, JobSchedulingInfo o2) {
      // the job that started earlier wins
      if (o1.getStartTime() < o2.getStartTime()) {
        return -1;
      } else {
        return (o1.getStartTime() == o2.getStartTime() 
                ? o1.getJobID().compareTo(o2.getJobID()) 
                : 1);
      }
    }
  };
  
  // class to store queue info
  private static class QueueInfo {

    // whether the queue supports priorities
    boolean supportsPriorities;
    Map<JobSchedulingInfo, JobInProgress> waitingJobs; // for waiting jobs
    Map<JobSchedulingInfo, JobInProgress> runningJobs; // for running jobs
    
    QueueInfo(boolean prio) {
      this.supportsPriorities = prio;
      if (supportsPriorities) {
        // use the default priority-aware comparator
        this.waitingJobs = 
          new TreeMap<JobSchedulingInfo, JobInProgress>(
              JobQueueJobInProgressListener.FIFO_JOB_QUEUE_COMPARATOR);
        this.runningJobs = 
          new TreeMap<JobSchedulingInfo, JobInProgress>(
              JobQueueJobInProgressListener.FIFO_JOB_QUEUE_COMPARATOR);
      }
      else {
        this.waitingJobs = 
          new TreeMap<JobSchedulingInfo, JobInProgress>(STARTTIME_JOB_COMPARATOR);
        this.runningJobs = 
          new TreeMap<JobSchedulingInfo, JobInProgress>(STARTTIME_JOB_COMPARATOR);
      }
    }
  }
  
  // we maintain a hashmap of queue-names to queue info
  private Map<String, QueueInfo> jobQueues = 
    new HashMap<String, QueueInfo>();
  private static final Log LOG = LogFactory.getLog(JobQueuesManager.class);
  private CapacityTaskScheduler scheduler;

  
  JobQueuesManager(CapacityTaskScheduler s) {
    this.scheduler = s;
  }
  
  /**
   * create an empty queue with the default comparator
   * @param queueName The name of the queue
   * @param supportsPriotities whether the queue supports priorities
   */
  public void createQueue(String queueName, boolean supportsPriotities) {
    jobQueues.put(queueName, new QueueInfo(supportsPriotities));
  }
  
  /**
   * Returns the queue of running jobs associated with the name
   */
  public Collection<JobInProgress> getRunningJobQueue(String queueName) {
    return jobQueues.get(queueName).runningJobs.values();
  }
  
  /**
   * Returns the queue of waiting jobs associated with the name
   */
  public Collection<JobInProgress> getWaitingJobQueue(String queueName) {
    return jobQueues.get(queueName).waitingJobs.values();
  }
  
  @Override
  public void jobAdded(JobInProgress job) {
    LOG.info("Job submitted to queue " + job.getProfile().getQueueName());
    // add job to the right queue
    QueueInfo qi = jobQueues.get(job.getProfile().getQueueName());
    if (null == qi) {
      // job was submitted to a queue we're not aware of
      LOG.warn("Invalid queue " + job.getProfile().getQueueName() + 
          " specified for job" + job.getProfile().getJobID() + 
          ". Ignoring job.");
      return;
    }
    // add job to waiting queue. It will end up in the right place, 
    // based on priority. 
    qi.waitingJobs.put(new JobSchedulingInfo(job), job);
    // let scheduler know. 
    scheduler.jobAdded(job);
  }

  private void jobCompleted(JobInProgress job, JobSchedulingInfo oldInfo, 
                            QueueInfo qi) {
    LOG.info("Job " + job.getJobID().toString() + " submitted to queue " 
             + job.getProfile().getQueueName() + " has completed");
    // job could be in running or waiting queue
    if (qi.runningJobs.remove(oldInfo) != null) {
      qi.waitingJobs.remove(oldInfo);
    }
    // let scheduler know
    scheduler.jobCompleted(job);
  }
  
  // Note that job is removed when the job completes i.e in jobUpated()
  @Override
  public void jobRemoved(JobInProgress job) {}
  
  // This is used to reposition a job in the queue. A job can get repositioned 
  // because of the change in the job priority or job start-time.
  private void reorderJobs(JobInProgress job, JobSchedulingInfo oldInfo, 
                           QueueInfo qi) {
    
    JobSchedulingInfo newInfo = new JobSchedulingInfo(job);
    if (qi.waitingJobs.remove(oldInfo) == null) {
      qi.runningJobs.remove(oldInfo);
      // Add back to the running queue
      qi.runningJobs.put(newInfo, job);
    } else {
      // Add back to the waiting queue
      qi.waitingJobs.put(newInfo, job);
    }
  }
  
  // This is used to move a job from the waiting queue to the running queue.
  private void makeJobRunning(JobInProgress job, JobSchedulingInfo oldInfo, 
                              QueueInfo qi) {
    // Remove from the waiting queue
    qi.waitingJobs.remove(oldInfo);
    
    // Add the job to the running queue
    qi.runningJobs.put(new JobSchedulingInfo(job), job);
  }
  
  // Update the scheduler as job's state has changed
  private void jobStateChanged(JobStatusChangeEvent event, QueueInfo qi) {
    JobInProgress job = event.getJobInProgress();
    JobSchedulingInfo oldJobStateInfo = 
      new JobSchedulingInfo(event.getOldStatus());
    // Check if the ordering of the job has changed
    // For now priority and start-time can change the job ordering
    if (event.getEventType() == EventType.PRIORITY_CHANGED 
        || event.getEventType() == EventType.START_TIME_CHANGED) {
      // Make a priority change
      reorderJobs(job, oldJobStateInfo, qi);
    } else if (event.getEventType() == EventType.RUN_STATE_CHANGED) {
      // Check if the job is complete
      int runState = job.getStatus().getRunState();
      if (runState == JobStatus.SUCCEEDED
          || runState == JobStatus.FAILED
          || runState == JobStatus.KILLED) {
        jobCompleted(job, oldJobStateInfo, qi);
      } else if (runState == JobStatus.RUNNING) {
        makeJobRunning(job, oldJobStateInfo, qi);
      }
    }
  }
  
  @Override
  public void jobUpdated(JobChangeEvent event) {
    JobInProgress job = event.getJobInProgress();
    QueueInfo qi = jobQueues.get(job.getProfile().getQueueName());
    if (null == qi) {
      // can't find queue for job. Shouldn't happen. 
      LOG.warn("Could not find queue " + job.getProfile().getQueueName() + 
          " when updating job " + job.getProfile().getJobID());
      return;
    }
    
    // Check if this is the status change
    if (event instanceof JobStatusChangeEvent) {
      jobStateChanged((JobStatusChangeEvent)event, qi);
    }
  }
  
}
