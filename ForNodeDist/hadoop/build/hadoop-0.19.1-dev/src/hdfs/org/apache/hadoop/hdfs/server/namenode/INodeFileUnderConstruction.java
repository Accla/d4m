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
package org.apache.hadoop.hdfs.server.namenode;

import java.io.IOException;

import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.server.namenode.BlocksMap.BlockInfo;


public class INodeFileUnderConstruction extends INodeFile {
  StringBytesWritable clientName = null;         // lease holder
  StringBytesWritable clientMachine = null;
  DatanodeDescriptor clientNode = null; // if client is a cluster node too.

  private int primaryNodeIndex = -1; //the node working on lease recovery
  private DatanodeDescriptor[] targets = null;   //locations for last block
  private long lastRecoveryTime = 0;
  
  INodeFileUnderConstruction() {}

  INodeFileUnderConstruction(PermissionStatus permissions,
                             short replication,
                             long preferredBlockSize,
                             long modTime,
                             String clientName,
                             String clientMachine,
                             DatanodeDescriptor clientNode) 
                             throws IOException {
    super(permissions.applyUMask(UMASK), 0, replication, modTime, modTime,
        preferredBlockSize);
    this.clientName = new StringBytesWritable(clientName);
    this.clientMachine = new StringBytesWritable(clientMachine);
    this.clientNode = clientNode;
  }

  public INodeFileUnderConstruction(byte[] name,
                             short blockReplication,
                             long modificationTime,
                             long preferredBlockSize,
                             BlockInfo[] blocks,
                             PermissionStatus perm,
                             String clientName,
                             String clientMachine,
                             DatanodeDescriptor clientNode)
                             throws IOException {
    super(perm, blocks, blockReplication, modificationTime, modificationTime,
          preferredBlockSize);
    setLocalName(name);
    this.clientName = new StringBytesWritable(clientName);
    this.clientMachine = new StringBytesWritable(clientMachine);
    this.clientNode = clientNode;
  }

  String getClientName() throws IOException {
    return clientName.getString();
  }

  String getClientMachine() throws IOException {
    return clientMachine.getString();
  }

  DatanodeDescriptor getClientNode() {
    return clientNode;
  }

  /**
   * Is this inode being constructed?
   */
  @Override
  boolean isUnderConstruction() {
    return true;
  }

  DatanodeDescriptor[] getTargets() {
    return targets;
  }

  void setTargets(DatanodeDescriptor[] targets) {
    this.targets = targets;
    this.primaryNodeIndex = -1;
  }

  //
  // converts a INodeFileUnderConstruction into a INodeFile
  // use the modification time as the access time
  //
  INodeFile convertToInodeFile() {
    INodeFile obj = new INodeFile(getPermissionStatus(),
                                  getBlocks(),
                                  getReplication(),
                                  getModificationTime(),
                                  getModificationTime(),
                                  getPreferredBlockSize());
    return obj;
    
  }

  /**
   * remove a block from the block list. This block should be
   * the last one on the list.
   */
  void removeBlock(Block oldblock) throws IOException {
    if (blocks == null) {
      throw new IOException("Trying to delete non-existant block " + oldblock);
    }
    int size_1 = blocks.length - 1;
    if (!blocks[size_1].equals(oldblock)) {
      throw new IOException("Trying to delete non-last block " + oldblock);
    }

    //copy to a new list
    BlockInfo[] newlist = new BlockInfo[size_1];
    System.arraycopy(blocks, 0, newlist, 0, size_1);
    blocks = newlist;
    
    // Remove the block locations for the last block.
    targets = null;
  }

  synchronized void setLastBlock(BlockInfo newblock, DatanodeDescriptor[] newtargets
      ) throws IOException {
    if (blocks == null) {
      throw new IOException("Trying to update non-existant block (newblock="
          + newblock + ")");
    }
    blocks[blocks.length - 1] = newblock;
    setTargets(newtargets);
    lastRecoveryTime = 0;
  }

  /**
   * Initialize lease recovery for this object
   */
  void assignPrimaryDatanode() {
    //assign the first alive datanode as the primary datanode

    if (targets.length == 0) {
      NameNode.stateChangeLog.warn("BLOCK*"
        + " INodeFileUnderConstruction.initLeaseRecovery:"
        + " No blocks found, lease removed.");
    }

    int previous = primaryNodeIndex;
    //find an alive datanode beginning from previous
    for(int i = 1; i <= targets.length; i++) {
      int j = (previous + i)%targets.length;
      if (targets[j].isAlive) {
        DatanodeDescriptor primary = targets[primaryNodeIndex = j]; 
        primary.addBlockToBeRecovered(blocks[blocks.length - 1], targets);
        NameNode.stateChangeLog.info("BLOCK* " + blocks[blocks.length - 1]
          + " recovery started, primary=" + primary);
        return;
      }
    }
  }
  
  /**
   * Update lastRecoveryTime if expired.
   * @return true if lastRecoveryTimeis updated. 
   */
  synchronized boolean setLastRecoveryTime(long now) {
    boolean expired = now - lastRecoveryTime > NameNode.LEASE_RECOVER_PERIOD;
    if (expired) {
      lastRecoveryTime = now;
    }
    return expired;
  }
}
