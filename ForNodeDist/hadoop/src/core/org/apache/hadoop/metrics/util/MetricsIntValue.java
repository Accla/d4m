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
package org.apache.hadoop.metrics.util;

import org.apache.hadoop.metrics.MetricsRecord;
import org.apache.hadoop.util.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The MetricsIntValue class is for a metric that is not time varied
 * but changes only when it is set. 
 * Each time its value is set, it is published only *once* at the next update
 * call.
 *
 */
public class MetricsIntValue {  

  private static final Log LOG =
    LogFactory.getLog("org.apache.hadoop.metrics.util");

  private String name;
  private int value;
  private boolean changed;
  
  /**
   * Constructor - create a new metric
   * @param nam the name of the metrics to be used to publish the metric
   */
  public MetricsIntValue(final String nam) {
    name = nam;
    value = 0;
    changed = false;
  }
  
  /**
   * Set the value
   * @param newValue
   */
  public synchronized void set(final int newValue) {
    value = newValue;
    changed = true;
  }
  
  /**
   * Get value
   * @return the value last set
   */
  public synchronized int get() { 
    return value;
  } 

  /**
   * Inc metrics for incr vlaue
   * @param incr - value to be added
   */
  public synchronized void inc(final int incr) {
    value += incr;
    changed = true;
  }
  
  /**
   * Inc metrics by one
   */
  public synchronized void inc() {
    value++;
    changed = true;
  }

  /**
   * Inc metrics for incr vlaue
   * @param decr - value to subtract
   */
  public synchronized void dec(final int decr) {
    value -= decr;
    if (value < 0)
      value = 0;
    changed = true;
  }
  
  /**
   * Dec metrics by one
   */
  public synchronized void dec() {
    value--;
    if (value < 0)
      value = 0;
    changed = true;
  }

  /**
   * Push the metric to the mr.
   * The metric is pushed only if it was updated since last push
   * 
   * Note this does NOT push to JMX
   * (JMX gets the info via {@link #get()}
   *
   * @param mr
   */
  public synchronized void pushMetric(final MetricsRecord mr) {
    if (changed) {
      try {
        mr.setMetric(name, value);
      } catch (Exception e) {
        LOG.info("pushMetric failed for " + name + "\n" +
            StringUtils.stringifyException(e));
      }
    }
    changed = false;
  }
}
