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

import org.apache.hadoop.io.*;

import java.io.*;
import java.util.*;

/**************************************************
 * A TaskTrackerStatus is a MapReduce primitive.  Keeps
 * info on a TaskTracker.  The JobTracker maintains a set
 * of the most recent TaskTrackerStatus objects for each
 * unique TaskTracker it knows about.
 *
 **************************************************/
class TaskTrackerStatus implements Writable {

  static {                                        // register a ctor
    WritableFactories.setFactory
      (TaskTrackerStatus.class,
       new WritableFactory() {
         public Writable newInstance() { return new TaskTrackerStatus(); }
       });
  }

  String trackerName;
  String host;
  int httpPort;
  int failures;
  List<TaskStatus> taskReports;
    
  volatile long lastSeen;
  private int maxMapTasks;
  private int maxReduceTasks;
   
  /**
   * Class representing a collection of resources on this tasktracker.
   */
  static class ResourceStatus implements Writable {
    
    private long freeVirtualMemory;
    private long totalMemory;
    private long availableSpace;
    
    ResourceStatus() {
      freeVirtualMemory = JobConf.DISABLED_VIRTUAL_MEMORY_LIMIT;
      totalMemory = JobConf.DISABLED_VIRTUAL_MEMORY_LIMIT;
      availableSpace = Long.MAX_VALUE;
    }
    
    /**
     * Set the amount of free virtual memory that is available for running
     * a new task
     * @param freeVMem amount of free virtual memory in kilobytes
     */
    void setFreeVirtualMemory(long freeVmem) {
      freeVirtualMemory = freeVmem;
    }

    /**
     * Get the amount of free virtual memory that will be available for
     * running a new task. 
     * 
     * If this is {@link JobConf.DISABLED_VIRTUAL_MEMORY_LIMIT}, it should 
     * be ignored and not used in computation.
     * 
     *@return amount of free virtual memory in kilobytes.
     */
    long getFreeVirtualMemory() {
      return freeVirtualMemory;
    }

    /**
     * Set the maximum amount of virtual memory on the tasktracker.
     * @param vmem maximum amount of virtual memory on the tasktracker in kilobytes.
     */
    void setTotalMemory(long totalMem) {
      totalMemory = totalMem;
    }
    
    /**
     * Get the maximum amount of virtual memory on the tasktracker.
     * 
     * If this is
     * {@link JobConf.DISABLED_VIRTUAL_MEMORY_LIMIT}, it should be ignored 
     * and not used in any computation.
     * 
     * @return maximum amount of virtual memory on the tasktracker in kilobytes. 
     */    
    long getTotalMemory() {
      return totalMemory;
    }
    
    void setAvailableSpace(long availSpace) {
      availableSpace = availSpace;
    }
    
    /**
     * Will return LONG_MAX if space hasn't been measured yet.
     * @return bytes of available local disk space on this tasktracker.
     */    
    long getAvailableSpace() {
      return availableSpace;
    }
    
    public void write(DataOutput out) throws IOException {
      WritableUtils.writeVLong(out, freeVirtualMemory);
      WritableUtils.writeVLong(out, totalMemory);
      WritableUtils.writeVLong(out, availableSpace);
    }
    
    public void readFields(DataInput in) throws IOException {
      freeVirtualMemory = WritableUtils.readVLong(in);;
      totalMemory = WritableUtils.readVLong(in);;
      availableSpace = WritableUtils.readVLong(in);;
    }
  }
  
  private ResourceStatus resStatus;
  
  /**
   */
  public TaskTrackerStatus() {
    taskReports = new ArrayList<TaskStatus>();
    resStatus = new ResourceStatus();
  }

  /**
   */
  public TaskTrackerStatus(String trackerName, String host, 
                           int httpPort, List<TaskStatus> taskReports, 
                           int failures, int maxMapTasks,
                           int maxReduceTasks) {
    this.trackerName = trackerName;
    this.host = host;
    this.httpPort = httpPort;

    this.taskReports = new ArrayList<TaskStatus>(taskReports);
    this.failures = failures;
    this.maxMapTasks = maxMapTasks;
    this.maxReduceTasks = maxReduceTasks;
    this.resStatus = new ResourceStatus();
  }

  /**
   */
  public String getTrackerName() {
    return trackerName;
  }
  /**
   */
  public String getHost() {
    return host;
  }

  /**
   * Get the port that this task tracker is serving http requests on.
   * @return the http port
   */
  public int getHttpPort() {
    return httpPort;
  }
    
  /**
   * Get the number of tasks that have failed on this tracker.
   * @return The number of failed tasks
   */
  public int getFailures() {
    return failures;
  }
    
  /**
   * Get the current tasks at the TaskTracker.
   * Tasks are tracked by a {@link TaskStatus} object.
   * 
   * @return a list of {@link TaskStatus} representing 
   *         the current tasks at the TaskTracker.
   */
  public List<TaskStatus> getTaskReports() {
    return taskReports;
  }
    
  /**
   * Return the current MapTask count
   */
  public int countMapTasks() {
    int mapCount = 0;
    for (Iterator it = taskReports.iterator(); it.hasNext();) {
      TaskStatus ts = (TaskStatus) it.next();
      TaskStatus.State state = ts.getRunState();
      if (ts.getIsMap() &&
          ((state == TaskStatus.State.RUNNING) ||
           (state == TaskStatus.State.UNASSIGNED))) {
        mapCount++;
      }
    }
    return mapCount;
  }

  /**
   * Return the current ReduceTask count
   */
  public int countReduceTasks() {
    int reduceCount = 0;
    for (Iterator it = taskReports.iterator(); it.hasNext();) {
      TaskStatus ts = (TaskStatus) it.next();
      TaskStatus.State state = ts.getRunState();
      if ((!ts.getIsMap()) &&
          ((state == TaskStatus.State.RUNNING) ||  
           (state == TaskStatus.State.UNASSIGNED))) {
        reduceCount++;
      }
    }
    return reduceCount;
  }

  /**
   */
  public long getLastSeen() {
    return lastSeen;
  }
  /**
   */
  public void setLastSeen(long lastSeen) {
    this.lastSeen = lastSeen;
  }

  /**
   * Get the maximum concurrent tasks for this node.  (This applies
   * per type of task - a node with maxTasks==1 will run up to 1 map
   * and 1 reduce concurrently).
   * @return maximum tasks this node supports
   */
  public int getMaxMapTasks() {
    return maxMapTasks;
  }
  public int getMaxReduceTasks() {
    return maxReduceTasks;
  }  
  
  /**
   * Return the {@link ResourceStatus} object configured with this
   * status.
   * 
   * @return the resource status
   */
  ResourceStatus getResourceStatus() {
    return resStatus;
  }
  
  ///////////////////////////////////////////
  // Writable
  ///////////////////////////////////////////
  public void write(DataOutput out) throws IOException {
    UTF8.writeString(out, trackerName);
    UTF8.writeString(out, host);
    out.writeInt(httpPort);
    out.writeInt(failures);
    out.writeInt(maxMapTasks);
    out.writeInt(maxReduceTasks);
    resStatus.write(out);
    out.writeInt(taskReports.size());

    for (TaskStatus taskStatus : taskReports) {
      TaskStatus.writeTaskStatus(out, taskStatus);
    }
  }

  public void readFields(DataInput in) throws IOException {
    this.trackerName = UTF8.readString(in);
    this.host = UTF8.readString(in);
    this.httpPort = in.readInt();
    this.failures = in.readInt();
    this.maxMapTasks = in.readInt();
    this.maxReduceTasks = in.readInt();
    resStatus.readFields(in);
    taskReports.clear();
    int numTasks = in.readInt();

    for (int i = 0; i < numTasks; i++) {
      taskReports.add(TaskStatus.readTaskStatus(in));
    }
  }
}
