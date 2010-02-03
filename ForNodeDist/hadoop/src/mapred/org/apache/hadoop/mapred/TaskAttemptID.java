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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * TaskAttemptID represents the immutable and unique identifier for 
 * a task attempt. Each task attempt is one particular instance of a Map or
 * Reduce Task identified by its TaskID. 
 * 
 * TaskAttemptID consists of 2 parts. First part is the 
 * {@link TaskID}, that this TaskAttemptID belongs to.
 * Second part is the task attempt number. <br> 
 * An example TaskAttemptID is : 
 * <code>attempt_200707121733_0003_m_000005_0</code> , which represents the
 * zeroth task attempt for the fifth map task in the third job 
 * running at the jobtracker started at <code>200707121733</code>.
 * <p>
 * Applications should never construct or parse TaskAttemptID strings
 * , but rather use appropriate constructors or {@link #forName(String)} 
 * method. 
 * 
 * @see JobID
 * @see TaskID
 */
public class TaskAttemptID extends ID {
  private static final String ATTEMPT = "attempt";
  private TaskID taskId;
  private static final char UNDERSCORE = '_';
  
  /**
   * Constructs a TaskAttemptID object from given {@link TaskID}.  
   * @param taskId TaskID that this task belongs to  
   * @param id the task attempt number
   */
  public TaskAttemptID(TaskID taskId, int id) {
    super(id);
    if(taskId == null) {
      throw new IllegalArgumentException("taskId cannot be null");
    }
    this.taskId = taskId;
  }
  
  /**
   * Constructs a TaskId object from given parts.
   * @param jtIdentifier jobTracker identifier
   * @param jobId job number 
   * @param isMap whether the tip is a map 
   * @param taskId taskId number
   * @param id the task attempt number
   */
  public TaskAttemptID(String jtIdentifier, int jobId, boolean isMap, 
                       int taskId, int id) {
    this(new TaskID(jtIdentifier, jobId, isMap, taskId), id);
  }
  
  private TaskAttemptID() { }
  
  /** Returns the {@link JobID} object that this task attempt belongs to */
  public JobID getJobID() {
    return taskId.getJobID();
  }
  
  /** Returns the {@link TaskID} object that this task attempt belongs to */
  public TaskID getTaskID() {
    return taskId;
  }
  
  /**Returns whether this TaskAttemptID is a map ID */
  public boolean isMap() {
    return taskId.isMap();
  }
  
  @Override
  public boolean equals(Object o) {
    if(o == null)
      return false;
    if(o.getClass().equals(TaskAttemptID.class)) {
      TaskAttemptID that = (TaskAttemptID)o;
      return this.id==that.id
             && this.taskId.equals(that.taskId);
    }
    else return false;
  }
  
  /**Compare TaskIds by first tipIds, then by task numbers. */
  @Override
  public int compareTo(ID o) {
    TaskAttemptID that = (TaskAttemptID)o;
    int tipComp = this.taskId.compareTo(that.taskId);
    if(tipComp == 0) {
      return this.id - that.id;
    }
    else return tipComp;
  }
  @Override
  public String toString() { 
    StringBuilder builder = new StringBuilder();
    return builder.append(ATTEMPT).append(UNDERSCORE)
      .append(toStringWOPrefix()).toString();
  }

  StringBuilder toStringWOPrefix() {
    StringBuilder builder = new StringBuilder();
    return builder.append(taskId.toStringWOPrefix())
                  .append(UNDERSCORE).append(id);
  }
  
  @Override
  public int hashCode() {
    return toStringWOPrefix().toString().hashCode();
  }
  
  @Override
  public void readFields(DataInput in) throws IOException {
    super.readFields(in);
    this.taskId = TaskID.read(in);
  }

  @Override
  public void write(DataOutput out) throws IOException {
    super.write(out);
    taskId.write(out);
  }
  
  public static TaskAttemptID read(DataInput in) throws IOException {
    TaskAttemptID taskId = new TaskAttemptID();
    taskId.readFields(in);
    return taskId;
  }
  
  /** Construct a TaskAttemptID object from given string 
   * @return constructed TaskAttemptID object or null if the given String is null
   * @throws IllegalArgumentException if the given string is malformed
   */
  public static TaskAttemptID forName(String str) throws IllegalArgumentException {
    if(str == null)
      return null;
    try {
      String[] parts = str.split("_");
      if(parts.length == 6) {
        if(parts[0].equals(ATTEMPT)) {
          boolean isMap = false;
          if(parts[3].equals("m")) isMap = true;
          else if(parts[3].equals("r")) isMap = false;
          else throw new Exception();
          return new TaskAttemptID(parts[1], Integer.parseInt(parts[2]),
              isMap, Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
        }
      }
    }catch (Exception ex) {//fall below
    }
    throw new IllegalArgumentException("TaskAttemptId string : " + str 
        + " is not properly formed");
  }
  
  /** 
   * Returns a regex pattern which matches task attempt IDs. Arguments can 
   * be given null, in which case that part of the regex will be generic.  
   * For example to obtain a regex matching <i>all task attempt IDs</i> 
   * of <i>any jobtracker</i>, in <i>any job</i>, of the <i>first 
   * map task</i>, we would use :
   * <pre> 
   * TaskAttemptID.getTaskAttemptIDsPattern(null, null, true, 1, null);
   * </pre>
   * which will return :
   * <pre> "attempt_[^_]*_[0-9]*_m_000001_[0-9]*" </pre> 
   * @param jtIdentifier jobTracker identifier, or null
   * @param jobId job number, or null
   * @param isMap whether the tip is a map, or null 
   * @param taskId taskId number, or null
   * @param attemptId the task attempt number, or null
   * @return a regex pattern matching TaskAttemptIDs
   */
  public static String getTaskAttemptIDsPattern(String jtIdentifier,
      Integer jobId, Boolean isMap, Integer taskId, Integer attemptId) {
    StringBuilder builder = new StringBuilder(ATTEMPT).append(UNDERSCORE);
    builder.append(getTaskAttemptIDsPatternWOPrefix(jtIdentifier, jobId,
        isMap, taskId, attemptId));
    return builder.toString();
  }
  
  static StringBuilder getTaskAttemptIDsPatternWOPrefix(String jtIdentifier
      , Integer jobId, Boolean isMap, Integer taskId, Integer attemptId) {
    StringBuilder builder = new StringBuilder();
    builder.append(TaskID.getTaskIDsPatternWOPrefix(jtIdentifier
        , jobId, isMap, taskId))
        .append(UNDERSCORE)
        .append(attemptId != null ? attemptId : "[0-9]*");
    return builder;
  }
  
}
