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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * Maintains a hierarchy of pools.
 */
public class PoolManager {
  public static final Log LOG = LogFactory.getLog(
    "org.apache.hadoop.mapred.PoolManager");

  /** Time to wait between checks of the allocation file */
  public static final long ALLOC_RELOAD_INTERVAL = 10 * 1000;
  
  /**
   * Time to wait after the allocation has been modified before reloading it
   * (this is done to prevent loading a file that hasn't been fully written).
   */
  public static final long ALLOC_RELOAD_WAIT = 5 * 1000; 
  
  // Map and reduce minimum allocations for each pool
  private Map<String, Integer> mapAllocs = new HashMap<String, Integer>();
  private Map<String, Integer> reduceAllocs = new HashMap<String, Integer>();
  
  // Max concurrent running jobs for each pool and for each user; in addition,
  // for users that have no max specified, we use the userMaxJobsDefault.
  private Map<String, Integer> poolMaxJobs = new HashMap<String, Integer>();
  private Map<String, Integer> userMaxJobs = new HashMap<String, Integer>();
  private int userMaxJobsDefault = Integer.MAX_VALUE;

  private String allocFile; // Path to XML file containing allocations
  private String poolNameProperty; // Jobconf property to use for determining a
                                   // job's pool name (default: mapred.job.queue.name)
  
  private Map<String, Pool> pools = new HashMap<String, Pool>();
  
  private long lastReloadAttempt; // Last time we tried to reload the pools file
  private long lastSuccessfulReload; // Last time we successfully reloaded pools
  private boolean lastReloadAttemptFailed = false;

  public PoolManager(Configuration conf) throws IOException, SAXException,
      AllocationConfigurationException, ParserConfigurationException {
    this.poolNameProperty = conf.get(
        "mapred.fairscheduler.poolnameproperty", "mapred.job.queue.name");
    this.allocFile = conf.get("mapred.fairscheduler.allocation.file");
    if (allocFile == null) {
      LOG.warn("No mapred.fairscheduler.allocation.file given in jobconf - " +
          "the fair scheduler will not use any queues.");
    }
    reloadAllocs();
    lastSuccessfulReload = System.currentTimeMillis();
    lastReloadAttempt = System.currentTimeMillis();
    // Create the default pool so that it shows up in the web UI
    getPool(Pool.DEFAULT_POOL_NAME);
  }
  
  /**
   * Get a pool by name, creating it if necessary
   */
  public synchronized Pool getPool(String name) {
    Pool pool = pools.get(name);
    if (pool == null) {
      pool = new Pool(name);
      pools.put(name, pool);
    }
    return pool;
  }

  /**
   * Reload allocations file if it hasn't been loaded in a while
   */
  public void reloadAllocsIfNecessary() {
    long time = System.currentTimeMillis();
    if (time > lastReloadAttempt + ALLOC_RELOAD_INTERVAL) {
      lastReloadAttempt = time;
      try {
        File file = new File(allocFile);
        long lastModified = file.lastModified();
        if (lastModified > lastSuccessfulReload &&
            time > lastModified + ALLOC_RELOAD_WAIT) {
          reloadAllocs();
          lastSuccessfulReload = time;
          lastReloadAttemptFailed = false;
        }
      } catch (Exception e) {
        // Throwing the error further out here won't help - the RPC thread
        // will catch it and report it in a loop. Instead, just log it and
        // hope somebody will notice from the log.
        // We log the error only on the first failure so we don't fill up the
        // JobTracker's log with these messages.
        if (!lastReloadAttemptFailed) {
          LOG.error("Failed to reload allocations file - " +
              "will use existing allocations.", e);
        }
        lastReloadAttemptFailed = true;
      }
    }
  }
  
  /**
   * Updates the allocation list from the allocation config file. This file is
   * expected to be in the following whitespace-separated format:
   * 
   * <code>
   * poolName1 mapAlloc reduceAlloc
   * poolName2 mapAlloc reduceAlloc
   * ...
   * </code>
   * 
   * Blank lines and lines starting with # are ignored.
   *  
   * @throws IOException if the config file cannot be read.
   * @throws AllocationConfigurationException if allocations are invalid.
   * @throws ParserConfigurationException if XML parser is misconfigured.
   * @throws SAXException if config file is malformed.
   */
  public void reloadAllocs() throws IOException, ParserConfigurationException, 
      SAXException, AllocationConfigurationException {
    if (allocFile == null) return;
    // Create some temporary hashmaps to hold the new allocs, and we only save
    // them in our fields if we have parsed the entire allocs file successfully.
    Map<String, Integer> mapAllocs = new HashMap<String, Integer>();
    Map<String, Integer> reduceAllocs = new HashMap<String, Integer>();
    Map<String, Integer> poolMaxJobs = new HashMap<String, Integer>();
    Map<String, Integer> userMaxJobs = new HashMap<String, Integer>();
    int userMaxJobsDefault = Integer.MAX_VALUE;
    
    // Remember all pool names so we can display them on web UI, etc.
    List<String> poolNamesInAllocFile = new ArrayList<String>();
    
    // Read and parse the allocations file.
    DocumentBuilderFactory docBuilderFactory =
      DocumentBuilderFactory.newInstance();
    docBuilderFactory.setIgnoringComments(true);
    DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
    Document doc = builder.parse(new File(allocFile));
    Element root = doc.getDocumentElement();
    if (!"allocations".equals(root.getTagName()))
      throw new AllocationConfigurationException("Bad allocations file: " + 
          "top-level element not <allocations>");
    NodeList elements = root.getChildNodes();
    for (int i = 0; i < elements.getLength(); i++) {
      Node node = elements.item(i);
      if (!(node instanceof Element))
        continue;
      Element element = (Element)node;
      if ("pool".equals(element.getTagName())) {
        String poolName = element.getAttribute("name");
        poolNamesInAllocFile.add(poolName);
        NodeList fields = element.getChildNodes();
        for (int j = 0; j < fields.getLength(); j++) {
          Node fieldNode = fields.item(j);
          if (!(fieldNode instanceof Element))
            continue;
          Element field = (Element) fieldNode;
          if ("minMaps".equals(field.getTagName())) {
            String text = ((Text)field.getFirstChild()).getData().trim();
            int val = Integer.parseInt(text);
            mapAllocs.put(poolName, val);
          } else if ("minReduces".equals(field.getTagName())) {
            String text = ((Text)field.getFirstChild()).getData().trim();
            int val = Integer.parseInt(text);
            reduceAllocs.put(poolName, val);
          } else if ("maxRunningJobs".equals(field.getTagName())) {
            String text = ((Text)field.getFirstChild()).getData().trim();
            int val = Integer.parseInt(text);
            poolMaxJobs.put(poolName, val);
          }
        }
      } else if ("user".equals(element.getTagName())) {
        String userName = element.getAttribute("name");
        NodeList fields = element.getChildNodes();
        for (int j = 0; j < fields.getLength(); j++) {
          Node fieldNode = fields.item(j);
          if (!(fieldNode instanceof Element))
            continue;
          Element field = (Element) fieldNode;
          if ("maxRunningJobs".equals(field.getTagName())) {
            String text = ((Text)field.getFirstChild()).getData().trim();
            int val = Integer.parseInt(text);
            userMaxJobs.put(userName, val);
          }
        }
      } else if ("userMaxJobsDefault".equals(element.getTagName())) {
        String text = ((Text)element.getFirstChild()).getData().trim();
        int val = Integer.parseInt(text);
        userMaxJobsDefault = val;
      } else {
        LOG.warn("Bad element in allocations file: " + element.getTagName());
      }
    }
    
    // Commit the reload; also create any pool defined in the alloc file
    // if it does not already exist, so it can be displayed on the web UI.
    synchronized(this) {
      this.mapAllocs = mapAllocs;
      this.reduceAllocs = reduceAllocs;
      this.poolMaxJobs = poolMaxJobs;
      this.userMaxJobs = userMaxJobs;
      this.userMaxJobsDefault = userMaxJobsDefault;
      for (String name: poolNamesInAllocFile) {
        getPool(name);
      }
    }
  }

  /**
   * Get the allocation for a particular pool
   */
  public int getAllocation(String pool, TaskType taskType) {
    Map<String, Integer> allocationMap = (taskType == TaskType.MAP ?
        mapAllocs : reduceAllocs);
    Integer alloc = allocationMap.get(pool);
    return (alloc == null ? 0 : alloc);
  }
  
  /**
   * Add a job in the appropriate pool
   */
  public synchronized void addJob(JobInProgress job) {
    getPool(getPoolName(job)).addJob(job);
  }
  
  /**
   * Remove a job
   */
  public synchronized void removeJob(JobInProgress job) {
    getPool(getPoolName(job)).removeJob(job);
  }
  
  /**
   * Change the pool of a particular job
   */
  public synchronized void setPool(JobInProgress job, String pool) {
    removeJob(job);
    job.getJobConf().set(poolNameProperty, pool);
    addJob(job);
  }

  /**
   * Get a collection of all pools
   */
  public synchronized Collection<Pool> getPools() {
    return pools.values();
  }
  
  /**
   * Get the pool name for a JobInProgress from its configuration. This uses
   * the "project" property in the jobconf by default, or the property set with
   * "mapred.fairscheduler.poolnameproperty".
   */
  public String getPoolName(JobInProgress job) {
    JobConf conf = job.getJobConf();
    return conf.get(poolNameProperty, Pool.DEFAULT_POOL_NAME).trim();
  }

  /**
   * Get all pool names that have been seen either in the allocation file or in
   * a MapReduce job.
   */
  public synchronized Collection<String> getPoolNames() {
    List<String> list = new ArrayList<String>();
    for (Pool pool: getPools()) {
      list.add(pool.getName());
    }
    Collections.sort(list);
    return list;
  }

  public int getUserMaxJobs(String user) {
    if (userMaxJobs.containsKey(user)) {
      return userMaxJobs.get(user);
    } else {
      return userMaxJobsDefault;
    }
  }

  public int getPoolMaxJobs(String pool) {
    if (poolMaxJobs.containsKey(pool)) {
      return poolMaxJobs.get(pool);
    } else {
      return Integer.MAX_VALUE;
    }
  }
}
