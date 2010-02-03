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
package org.apache.hadoop.hdfs.server.datanode;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.commons.logging.Log;
import org.apache.hadoop.fs.FSInputChecker;
import org.apache.hadoop.fs.FSOutputSummer;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.DataTransferProtocol;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.FSConstants;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Daemon;
import org.apache.hadoop.util.DataChecksum;
import org.apache.hadoop.util.StringUtils;
import static org.apache.hadoop.hdfs.server.datanode.DataNode.DN_CLIENTTRACE_FORMAT;

/** A class that receives a block and writes to its own disk, meanwhile
 * may copies it to another site. If a throttler is provided,
 * streaming throttling is also supported.
 **/
class BlockReceiver implements java.io.Closeable, FSConstants {
  public static final Log LOG = DataNode.LOG;
  static final Log ClientTraceLog = DataNode.ClientTraceLog;
  
  private Block block; // the block to receive
  protected boolean finalized;
  private DataInputStream in = null; // from where data are read
  private DataChecksum checksum; // from where chunks of a block can be read
  private OutputStream out = null; // to block file at local disk
  private DataOutputStream checksumOut = null; // to crc file at local disk
  private int bytesPerChecksum;
  private int checksumSize;
  private ByteBuffer buf; // contains one full packet.
  private int bufRead; //amount of valid data in the buf
  private int maxPacketReadLen;
  protected long offsetInBlock;
  protected final String inAddr;
  protected final String myAddr;
  private String mirrorAddr;
  private DataOutputStream mirrorOut;
  private Daemon responder = null;
  private BlockTransferThrottler throttler;
  private FSDataset.BlockWriteStreams streams;
  private boolean isRecovery = false;
  private String clientName;
  DatanodeInfo srcDataNode = null;
  private Checksum partialCrc = null;
  private DataNode datanode = null;

  BlockReceiver(Block block, DataInputStream in, String inAddr,
                String myAddr, boolean isRecovery, String clientName, 
                DatanodeInfo srcDataNode, DataNode datanode) throws IOException {
    try{
      this.block = block;
      this.in = in;
      this.inAddr = inAddr;
      this.myAddr = myAddr;
      this.isRecovery = isRecovery;
      this.clientName = clientName;
      this.offsetInBlock = 0;
      this.checksum = DataChecksum.newDataChecksum(in);
      this.bytesPerChecksum = checksum.getBytesPerChecksum();
      this.checksumSize = checksum.getChecksumSize();
      this.srcDataNode = srcDataNode;
      this.datanode = datanode;
      //
      // Open local disk out
      //
      streams = datanode.data.writeToBlock(block, isRecovery);
      this.finalized = datanode.data.isValidBlock(block);
      if (streams != null) {
        this.out = streams.dataOut;
        this.checksumOut = new DataOutputStream(new BufferedOutputStream(
                                                  streams.checksumOut, 
                                                  SMALL_BUFFER_SIZE));
        // If this block is for appends, then remove it from periodic
        // validation.
        if (datanode.blockScanner != null && isRecovery) {
          datanode.blockScanner.deleteBlock(block);
        }
      }
    } catch(IOException ioe) {
      IOUtils.closeStream(this);
      throw ioe;
    }
  }

  /**
   * close files.
   */
  public void close() throws IOException {

    IOException ioe = null;
    // close checksum file
    try {
      if (checksumOut != null) {
        checksumOut.flush();
        checksumOut.close();
        checksumOut = null;
      }
    } catch(IOException e) {
      ioe = e;
    }
    // close block file
    try {
      if (out != null) {
        out.flush();
        out.close();
        out = null;
      }
    } catch (IOException e) {
      ioe = e;
    }
    // disk check
    if(ioe != null) {
      datanode.checkDiskError(ioe);
      throw ioe;
    }
  }

  /**
   * Flush block data and metadata files to disk.
   * @throws IOException
   */
  void flush() throws IOException {
    if (checksumOut != null) {
      checksumOut.flush();
    }
    if (out != null) {
      out.flush();
    }
  }

  /**
   * While writing to mirrorOut, failure to write to mirror should not
   * affect this datanode unless a client is writing the block.
   */
  private void handleMirrorOutError(IOException ioe) throws IOException {
    LOG.info(datanode.dnRegistration + ":Exception writing block " +
             block + " to mirror " + mirrorAddr + "\n" +
             StringUtils.stringifyException(ioe));
    mirrorOut = null;
    //
    // If stream-copy fails, continue
    // writing to disk for replication requests. For client
    // writes, return error so that the client can do error
    // recovery.
    //
    if (clientName.length() > 0) {
      throw ioe;
    }
  }
  
  /**
   * Verify multiple CRC chunks. 
   */
  private void verifyChunks( byte[] dataBuf, int dataOff, int len, 
                             byte[] checksumBuf, int checksumOff ) 
                             throws IOException {
    while (len > 0) {
      int chunkLen = Math.min(len, bytesPerChecksum);
      
      checksum.update(dataBuf, dataOff, chunkLen);

      if (!checksum.compare(checksumBuf, checksumOff)) {
        if (srcDataNode != null) {
          try {
            LOG.info("report corrupt block " + block + " from datanode " +
                      srcDataNode + " to namenode");
            LocatedBlock lb = new LocatedBlock(block, 
                                            new DatanodeInfo[] {srcDataNode});
            datanode.namenode.reportBadBlocks(new LocatedBlock[] {lb});
          } catch (IOException e) {
            LOG.warn("Failed to report bad block " + block + 
                      " from datanode " + srcDataNode + " to namenode");
          }
        }
        throw new IOException("Unexpected checksum mismatch " + 
                              "while writing " + block + " from " + inAddr);
      }

      checksum.reset();
      dataOff += chunkLen;
      checksumOff += checksumSize;
      len -= chunkLen;
    }
  }

  /**
   * Makes sure buf.position() is zero without modifying buf.remaining().
   * It moves the data if position needs to be changed.
   */
  private void shiftBufData() {
    if (bufRead != buf.limit()) {
      throw new IllegalStateException("bufRead should be same as " +
                                      "buf.limit()");
    }
    
    //shift the remaining data on buf to the front
    if (buf.position() > 0) {
      int dataLeft = buf.remaining();
      if (dataLeft > 0) {
        byte[] b = buf.array();
        System.arraycopy(b, buf.position(), b, 0, dataLeft);
      }
      buf.position(0);
      bufRead = dataLeft;
      buf.limit(bufRead);
    }
  }
  
  /**
   * reads upto toRead byte to buf at buf.limit() and increments the limit.
   * throws an IOException if read does not succeed.
   */
  private int readToBuf(int toRead) throws IOException {
    if (toRead < 0) {
      toRead = (maxPacketReadLen > 0 ? maxPacketReadLen : buf.capacity())
               - buf.limit();
    }
    
    int nRead = in.read(buf.array(), buf.limit(), toRead);
    
    if (nRead < 0) {
      throw new EOFException("while trying to read " + toRead + " bytes");
    }
    bufRead = buf.limit() + nRead;
    buf.limit(bufRead);
    return nRead;
  }
  
  
  /**
   * Reads (at least) one packet and returns the packet length.
   * buf.position() points to the start of the packet and 
   * buf.limit() point to the end of the packet. There could 
   * be more data from next packet in buf.<br><br>
   * 
   * It tries to read a full packet with single read call.
   * Consecutive packets are usually of the same length.
   */
  private int readNextPacket() throws IOException {
    /* This dances around buf a little bit, mainly to read 
     * full packet with single read and to accept arbitarary size  
     * for next packet at the same time.
     */
    if (buf == null) {
      /* initialize buffer to the best guess size:
       * 'chunksPerPacket' calculation here should match the same 
       * calculation in DFSClient to make the guess accurate.
       */
      int chunkSize = bytesPerChecksum + checksumSize;
      int chunksPerPacket = (datanode.writePacketSize - DataNode.PKT_HEADER_LEN - 
                             SIZE_OF_INTEGER + chunkSize - 1)/chunkSize;
      buf = ByteBuffer.allocate(DataNode.PKT_HEADER_LEN + SIZE_OF_INTEGER +
                                Math.max(chunksPerPacket, 1) * chunkSize);
      buf.limit(0);
    }
    
    // See if there is data left in the buffer :
    if (bufRead > buf.limit()) {
      buf.limit(bufRead);
    }
    
    while (buf.remaining() < SIZE_OF_INTEGER) {
      if (buf.position() > 0) {
        shiftBufData();
      }
      readToBuf(-1);
    }
    
    /* We mostly have the full packet or at least enough for an int
     */
    buf.mark();
    int payloadLen = buf.getInt();
    buf.reset();
    
    if (payloadLen == 0) {
      //end of stream!
      buf.limit(buf.position() + SIZE_OF_INTEGER);
      return 0;
    }
    
    // check corrupt values for pktLen, 100MB upper limit should be ok?
    if (payloadLen < 0 || payloadLen > (100*1024*1024)) {
      throw new IOException("Incorrect value for packet payload : " +
                            payloadLen);
    }
    
    int pktSize = payloadLen + DataNode.PKT_HEADER_LEN;
    
    if (buf.remaining() < pktSize) {
      //we need to read more data
      int toRead = pktSize - buf.remaining();
      
      // first make sure buf has enough space.        
      int spaceLeft = buf.capacity() - buf.limit();
      if (toRead > spaceLeft && buf.position() > 0) {
        shiftBufData();
        spaceLeft = buf.capacity() - buf.limit();
      }
      if (toRead > spaceLeft) {
        byte oldBuf[] = buf.array();
        int toCopy = buf.limit();
        buf = ByteBuffer.allocate(toCopy + toRead);
        System.arraycopy(oldBuf, 0, buf.array(), 0, toCopy);
        buf.limit(toCopy);
      }
      
      //now read:
      while (toRead > 0) {
        toRead -= readToBuf(toRead);
      }
    }
    
    if (buf.remaining() > pktSize) {
      buf.limit(buf.position() + pktSize);
    }
    
    if (pktSize > maxPacketReadLen) {
      maxPacketReadLen = pktSize;
    }
    
    return payloadLen;
  }
  
  /** 
   * Receives and processes a packet. It can contain many chunks.
   * returns size of the packet.
   */
  private int receivePacket() throws IOException {
    
    int payloadLen = readNextPacket();
    
    if (payloadLen <= 0) {
      return payloadLen;
    }
    
    buf.mark();
    //read the header
    buf.getInt(); // packet length
    offsetInBlock = buf.getLong(); // get offset of packet in block
    long seqno = buf.getLong();    // get seqno
    boolean lastPacketInBlock = (buf.get() != 0);
    
    int endOfHeader = buf.position();
    buf.reset();
    
    if (LOG.isDebugEnabled()){
      LOG.debug("Receiving one packet for block " + block +
                " of length " + payloadLen +
                " seqno " + seqno +
                " offsetInBlock " + offsetInBlock +
                " lastPacketInBlock " + lastPacketInBlock);
    }
    
    setBlockPosition(offsetInBlock);
    
    //First write the packet to the mirror:
    if (mirrorOut != null) {
      try {
        mirrorOut.write(buf.array(), buf.position(), buf.remaining());
        mirrorOut.flush();
      } catch (IOException e) {
        handleMirrorOutError(e);
      }
    }

    buf.position(endOfHeader);        
    int len = buf.getInt();
    
    if (len < 0) {
      throw new IOException("Got wrong length during writeBlock(" + block + 
                            ") from " + inAddr + " at offset " + 
                            offsetInBlock + ": " + len); 
    } 

    if (len == 0) {
      LOG.debug("Receiving empty packet for block " + block);
    } else {
      offsetInBlock += len;

      int checksumLen = ((len + bytesPerChecksum - 1)/bytesPerChecksum)*
                                                            checksumSize;

      if ( buf.remaining() != (checksumLen + len)) {
        throw new IOException("Data remaining in packet does not match " +
                              "sum of checksumLen and dataLen");
      }
      int checksumOff = buf.position();
      int dataOff = checksumOff + checksumLen;
      byte pktBuf[] = buf.array();

      buf.position(buf.limit()); // move to the end of the data.

      /* skip verifying checksum iff this is not the last one in the 
       * pipeline and clientName is non-null. i.e. Checksum is verified
       * on all the datanodes when the data is being written by a 
       * datanode rather than a client. Whe client is writing the data, 
       * protocol includes acks and only the last datanode needs to verify 
       * checksum.
       */
      if (mirrorOut == null || clientName.length() == 0) {
        verifyChunks(pktBuf, dataOff, len, pktBuf, checksumOff);
      }

      try {
        if (!finalized) {
          //finally write to the disk :
          out.write(pktBuf, dataOff, len);

          // If this is a partial chunk, then verify that this is the only
          // chunk in the packet. Calculate new crc for this chunk.
          if (partialCrc != null) {
            if (len > bytesPerChecksum) {
              throw new IOException("Got wrong length during writeBlock(" + 
                                    block + ") from " + inAddr + " " +
                                    "A packet can have only one partial chunk."+
                                    " len = " + len + 
                                    " bytesPerChecksum " + bytesPerChecksum);
            }
            partialCrc.update(pktBuf, dataOff, len);
            byte[] buf = FSOutputSummer.convertToByteStream(partialCrc, checksumSize);
            checksumOut.write(buf);
            LOG.debug("Writing out partial crc for data len " + len);
            partialCrc = null;
          } else {
            checksumOut.write(pktBuf, checksumOff, checksumLen);
          }
          datanode.myMetrics.bytesWritten.inc(len);
        }
      } catch (IOException iex) {
        datanode.checkDiskError(iex);
        throw iex;
      }
    }

    /// flush entire packet before sending ack
    flush();

    // put in queue for pending acks
    if (responder != null) {
      ((PacketResponder)responder.getRunnable()).enqueue(seqno,
                                      lastPacketInBlock); 
    }
    
    if (throttler != null) { // throttle I/O
      throttler.throttle(payloadLen);
    }
    
    return payloadLen;
  }

  void writeChecksumHeader(DataOutputStream mirrorOut) throws IOException {
    checksum.writeHeader(mirrorOut);
  }
 

  void receiveBlock(
      DataOutputStream mirrOut, // output to next datanode
      DataInputStream mirrIn,   // input from next datanode
      DataOutputStream replyOut,  // output to previous datanode
      String mirrAddr, BlockTransferThrottler throttlerArg,
      int numTargets) throws IOException {

      mirrorOut = mirrOut;
      mirrorAddr = mirrAddr;
      throttler = throttlerArg;

    try {
      // write data chunk header
      if (!finalized) {
        BlockMetadataHeader.writeHeader(checksumOut, checksum);
      }
      if (clientName.length() > 0) {
        responder = new Daemon(datanode.threadGroup, 
                               new PacketResponder(this, block, mirrIn, 
                                                   replyOut, numTargets));
        responder.start(); // start thread to processes reponses
      }

      /* 
       * Receive until packet length is zero.
       */
      while (receivePacket() > 0) {}

      // flush the mirror out
      if (mirrorOut != null) {
        try {
          mirrorOut.writeInt(0); // mark the end of the block
          mirrorOut.flush();
        } catch (IOException e) {
          handleMirrorOutError(e);
        }
      }

      // wait for all outstanding packet responses. And then
      // indicate responder to gracefully shutdown.
      if (responder != null) {
        ((PacketResponder)responder.getRunnable()).close();
      }

      // if this write is for a replication request (and not
      // from a client), then finalize block. For client-writes, 
      // the block is finalized in the PacketResponder.
      if (clientName.length() == 0) {
        // close the block/crc files
        close();

        // Finalize the block. Does this fsync()?
        block.setNumBytes(offsetInBlock);
        datanode.data.finalizeBlock(block);
        datanode.myMetrics.blocksWritten.inc();
      }

    } catch (IOException ioe) {
      LOG.info("Exception in receiveBlock for block " + block + 
               " " + ioe);
      IOUtils.closeStream(this);
      if (responder != null) {
        responder.interrupt();
      }
      throw ioe;
    } finally {
      if (responder != null) {
        try {
          responder.join();
        } catch (InterruptedException e) {
          throw new IOException("Interrupted receiveBlock");
        }
        responder = null;
      }
    }
  }

  /**
   * Sets the file pointer in the local block file to the specified value.
   */
  private void setBlockPosition(long offsetInBlock) throws IOException {
    if (finalized) {
      if (!isRecovery) {
        throw new IOException("Write to offset " + offsetInBlock +
                              " of block " + block +
                              " that is already finalized.");
      }
      if (offsetInBlock > datanode.data.getLength(block)) {
        throw new IOException("Write to offset " + offsetInBlock +
                              " of block " + block +
                              " that is already finalized and is of size " +
                              datanode.data.getLength(block));
      }
      return;
    }

    if (datanode.data.getChannelPosition(block, streams) == offsetInBlock) {
      return;                   // nothing to do 
    }
    long offsetInChecksum = BlockMetadataHeader.getHeaderSize() +
                            offsetInBlock / bytesPerChecksum * checksumSize;
    if (out != null) {
     out.flush();
    }
    if (checksumOut != null) {
      checksumOut.flush();
    }

    // If this is a partial chunk, then read in pre-existing checksum
    if (offsetInBlock % bytesPerChecksum != 0) {
      LOG.info("setBlockPosition trying to set position to " +
               offsetInBlock +
               " for block " + block +
               " which is not a multiple of bytesPerChecksum " +
               bytesPerChecksum);
      computePartialChunkCrc(offsetInBlock, offsetInChecksum, bytesPerChecksum);
    }

    LOG.info("Changing block file offset of block " + block + " from " + 
        datanode.data.getChannelPosition(block, streams) +
             " to " + offsetInBlock +
             " meta file offset to " + offsetInChecksum);

    // set the position of the block file
    datanode.data.setChannelPosition(block, streams, offsetInBlock, offsetInChecksum);
  }

  /**
   * reads in the partial crc chunk and computes checksum
   * of pre-existing data in partial chunk.
   */
  private void computePartialChunkCrc(long blkoff, long ckoff, 
                                      int bytesPerChecksum) throws IOException {

    // find offset of the beginning of partial chunk.
    //
    int sizePartialChunk = (int) (blkoff % bytesPerChecksum);
    int checksumSize = checksum.getChecksumSize();
    blkoff = blkoff - sizePartialChunk;
    LOG.info("computePartialChunkCrc sizePartialChunk " + 
              sizePartialChunk +
              " block " + block +
              " offset in block " + blkoff +
              " offset in metafile " + ckoff);

    // create an input stream from the block file
    // and read in partial crc chunk into temporary buffer
    //
    byte[] buf = new byte[sizePartialChunk];
    byte[] crcbuf = new byte[checksumSize];
    FSDataset.BlockInputStreams instr = null;
    try { 
      instr = datanode.data.getTmpInputStreams(block, blkoff, ckoff);
      IOUtils.readFully(instr.dataIn, buf, 0, sizePartialChunk);

      // open meta file and read in crc value computer earlier
      IOUtils.readFully(instr.checksumIn, crcbuf, 0, crcbuf.length);
    } finally {
      IOUtils.closeStream(instr);
    }

    // compute crc of partial chunk from data read in the block file.
    partialCrc = new CRC32();
    partialCrc.update(buf, 0, sizePartialChunk);
    LOG.info("Read in partial CRC chunk from disk for block " + block);

    // paranoia! verify that the pre-computed crc matches what we
    // recalculated just now
    if (partialCrc.getValue() != FSInputChecker.checksum2long(crcbuf)) {
      String msg = "Partial CRC " + partialCrc.getValue() +
                   " does not match value computed the " +
                   " last time file was closed " +
                   FSInputChecker.checksum2long(crcbuf);
      throw new IOException(msg);
    }
    //LOG.debug("Partial CRC matches 0x" + 
    //            Long.toHexString(partialCrc.getValue()));
  }
  
  
  /**
   * Processed responses from downstream datanodes in the pipeline
   * and sends back replies to the originator.
   */
  class PacketResponder implements Runnable, FSConstants {   

    //packet waiting for ack
    private LinkedList<Packet> ackQueue = new LinkedList<Packet>(); 
    private volatile boolean running = true;
    private Block block;
    DataInputStream mirrorIn;   // input from downstream datanode
    DataOutputStream replyOut;  // output to upstream datanode
    private int numTargets;     // number of downstream datanodes including myself
    private BlockReceiver receiver; // The owner of this responder.

    public String toString() {
      return "PacketResponder " + numTargets + " for Block " + this.block;
    }

    PacketResponder(BlockReceiver receiver, Block b, DataInputStream in, 
                    DataOutputStream out, int numTargets) {
      this.receiver = receiver;
      this.block = b;
      mirrorIn = in;
      replyOut = out;
      this.numTargets = numTargets;
    }

    /**
     * enqueue the seqno that is still be to acked by the downstream datanode.
     * @param seqno
     * @param lastPacketInBlock
     */
    synchronized void enqueue(long seqno, boolean lastPacketInBlock) {
      if (running) {
        LOG.debug("PacketResponder " + numTargets + " adding seqno " + seqno +
                  " to ack queue.");
        ackQueue.addLast(new Packet(seqno, lastPacketInBlock));
        notifyAll();
      }
    }

    /**
     * wait for all pending packets to be acked. Then shutdown thread.
     */
    synchronized void close() {
      while (running && ackQueue.size() != 0 && datanode.shouldRun) {
        try {
          wait();
        } catch (InterruptedException e) {
          running = false;
        }
      }
      LOG.debug("PacketResponder " + numTargets +
               " for block " + block + " Closing down.");
      running = false;
      notifyAll();
    }

    private synchronized void lastDataNodeRun() {
      long lastHeartbeat = System.currentTimeMillis();
      boolean lastPacket = false;

      while (running && datanode.shouldRun && !lastPacket) {
        long now = System.currentTimeMillis();
        try {

            // wait for a packet to be sent to downstream datanode
            while (running && datanode.shouldRun && ackQueue.size() == 0) {
              long idle = now - lastHeartbeat;
              long timeout = (datanode.socketTimeout/2) - idle;
              if (timeout <= 0) {
                timeout = 1000;
              }
              try {
                wait(timeout);
              } catch (InterruptedException e) {
                if (running) {
                  LOG.info("PacketResponder " + numTargets +
                           " for block " + block + " Interrupted.");
                  running = false;
                }
                break;
              }
          
              // send a heartbeat if it is time.
              now = System.currentTimeMillis();
              if (now - lastHeartbeat > datanode.socketTimeout/2) {
                replyOut.writeLong(-1); // send heartbeat
                replyOut.flush();
                lastHeartbeat = now;
              }
            }

            if (!running || !datanode.shouldRun) {
              break;
            }
            Packet pkt = ackQueue.removeFirst();
            long expected = pkt.seqno;
            notifyAll();
            LOG.debug("PacketResponder " + numTargets +
                      " for block " + block + 
                      " acking for packet " + expected);

            // If this is the last packet in block, then close block
            // file and finalize the block before responding success
            if (pkt.lastPacketInBlock) {
              if (!receiver.finalized) {
                receiver.close();
                block.setNumBytes(receiver.offsetInBlock);
                datanode.data.finalizeBlock(block);
                datanode.myMetrics.blocksWritten.inc();
                datanode.notifyNamenodeReceivedBlock(block, 
                    DataNode.EMPTY_DEL_HINT);
                if (ClientTraceLog.isInfoEnabled() &&
                    receiver.clientName.length() > 0) {
                  ClientTraceLog.info(String.format(DN_CLIENTTRACE_FORMAT,
                        receiver.inAddr, receiver.myAddr, block.getNumBytes(),
                        "HDFS_WRITE", receiver.clientName,
                        datanode.dnRegistration.getStorageID(), block));
                } else {
                  LOG.info("Received block " + block + 
                           " of size " + block.getNumBytes() + 
                           " from " + receiver.inAddr);
                }
              }
              lastPacket = true;
            }

            replyOut.writeLong(expected);
            replyOut.writeShort(DataTransferProtocol.OP_STATUS_SUCCESS);
            replyOut.flush();
        } catch (Exception e) {
          if (running) {
            LOG.info("PacketResponder " + block + " " + numTargets + 
                     " Exception " + StringUtils.stringifyException(e));
            running = false;
          }
        }
      }
      LOG.info("PacketResponder " + numTargets + 
               " for block " + block + " terminating");
    }

    /**
     * Thread to process incoming acks.
     * @see java.lang.Runnable#run()
     */
    public void run() {

      // If this is the last datanode in pipeline, then handle differently
      if (numTargets == 0) {
        lastDataNodeRun();
        return;
      }

      boolean lastPacketInBlock = false;
      while (running && datanode.shouldRun && !lastPacketInBlock) {

        try {
            short op = DataTransferProtocol.OP_STATUS_SUCCESS;
            boolean didRead = false;
            long expected = -2;
            try { 
              // read seqno from downstream datanode
              long seqno = mirrorIn.readLong();
              didRead = true;
              if (seqno == -1) {
                replyOut.writeLong(-1); // send keepalive
                replyOut.flush();
                LOG.debug("PacketResponder " + numTargets + " got -1");
                continue;
              } else if (seqno == -2) {
                LOG.debug("PacketResponder " + numTargets + " got -2");
              } else {
                LOG.debug("PacketResponder " + numTargets + " got seqno = " + 
                    seqno);
                Packet pkt = null;
                synchronized (this) {
                  while (running && datanode.shouldRun && ackQueue.size() == 0) {
                    if (LOG.isDebugEnabled()) {
                      LOG.debug("PacketResponder " + numTargets + 
                                " seqno = " + seqno +
                                " for block " + block +
                                " waiting for local datanode to finish write.");
                    }
                    wait();
                  }
                  pkt = ackQueue.removeFirst();
                  expected = pkt.seqno;
                  notifyAll();
                  LOG.debug("PacketResponder " + numTargets + " seqno = " + seqno);
                  if (seqno != expected) {
                    throw new IOException("PacketResponder " + numTargets +
                                          " for block " + block +
                                          " expected seqno:" + expected +
                                          " received:" + seqno);
                  }
                  lastPacketInBlock = pkt.lastPacketInBlock;
                }
              }
            } catch (Throwable e) {
              if (running) {
                LOG.info("PacketResponder " + block + " " + numTargets + 
                         " Exception " + StringUtils.stringifyException(e));
                running = false;
              }
            }

            if (Thread.interrupted()) {
              /* The receiver thread cancelled this thread. 
               * We could also check any other status updates from the 
               * receiver thread (e.g. if it is ok to write to replyOut). 
               * It is prudent to not send any more status back to the client
               * because this datanode has a problem. The upstream datanode
               * will detect a timout on heartbeats and will declare that
               * this datanode is bad, and rightly so.
               */
              LOG.info("PacketResponder " + block +  " " + numTargets +
                       " : Thread is interrupted.");
              running = false;
              continue;
            }
            
            if (!didRead) {
              op = DataTransferProtocol.OP_STATUS_ERROR;
            }
            
            // If this is the last packet in block, then close block
            // file and finalize the block before responding success
            if (lastPacketInBlock && !receiver.finalized) {
              receiver.close();
              block.setNumBytes(receiver.offsetInBlock);
              datanode.data.finalizeBlock(block);
              datanode.myMetrics.blocksWritten.inc();
              datanode.notifyNamenodeReceivedBlock(block, 
                  DataNode.EMPTY_DEL_HINT);
              if (ClientTraceLog.isInfoEnabled() &&
                  receiver.clientName.length() > 0) {
                ClientTraceLog.info(String.format(DN_CLIENTTRACE_FORMAT,
                      receiver.inAddr, receiver.myAddr, block.getNumBytes(),
                      "HDFS_WRITE", receiver.clientName,
                      datanode.dnRegistration.getStorageID(), block));
              } else {
                LOG.info("Received block " + block + 
                         " of size " + block.getNumBytes() + 
                         " from " + receiver.inAddr);
              }
            }

            // send my status back to upstream datanode
            replyOut.writeLong(expected); // send seqno upstream
            replyOut.writeShort(DataTransferProtocol.OP_STATUS_SUCCESS);

            LOG.debug("PacketResponder " + numTargets + 
                      " for block " + block +
                      " responded my status " +
                      " for seqno " + expected);

            // forward responses from downstream datanodes.
            for (int i = 0; i < numTargets && datanode.shouldRun; i++) {
              try {
                if (op == DataTransferProtocol.OP_STATUS_SUCCESS) {
                  op = mirrorIn.readShort();
                  if (op != DataTransferProtocol.OP_STATUS_SUCCESS) {
                    LOG.debug("PacketResponder for block " + block +
                              ": error code received from downstream " +
                              " datanode[" + i + "] " + op);
                  }
                }
              } catch (Throwable e) {
                op = DataTransferProtocol.OP_STATUS_ERROR;
              }
              replyOut.writeShort(op);
            }
            replyOut.flush();
            LOG.debug("PacketResponder " + block + " " + numTargets + 
                      " responded other status " + " for seqno " + expected);

            // If we were unable to read the seqno from downstream, then stop.
            if (expected == -2) {
              running = false;
            }
            // If we forwarded an error response from a downstream datanode
            // and we are acting on behalf of a client, then we quit. The 
            // client will drive the recovery mechanism.
            if (op == DataTransferProtocol.OP_STATUS_ERROR && receiver.clientName.length() > 0) {
              running = false;
            }
        } catch (IOException e) {
          if (running) {
            LOG.info("PacketResponder " + block + " " + numTargets + 
                     " Exception " + StringUtils.stringifyException(e));
            running = false;
          }
        } catch (RuntimeException e) {
          if (running) {
            LOG.info("PacketResponder " + block + " " + numTargets + 
                     " Exception " + StringUtils.stringifyException(e));
            running = false;
          }
        }
      }
      LOG.info("PacketResponder " + numTargets + 
               " for block " + block + " terminating");
    }
  }
  
  /**
   * This information is cached by the Datanode in the ackQueue.
   */
  static private class Packet {
    long seqno;
    boolean lastPacketInBlock;

    Packet(long seqno, boolean lastPacketInBlock) {
      this.seqno = seqno;
      this.lastPacketInBlock = lastPacketInBlock;
    }
  }
}
