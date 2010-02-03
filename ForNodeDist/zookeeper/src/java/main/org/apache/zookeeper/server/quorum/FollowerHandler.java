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

package org.apache.zookeeper.server.quorum;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.jute.BinaryInputArchive;
import org.apache.jute.BinaryOutputArchive;
import org.apache.jute.Record;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException.SessionExpiredException;
import org.apache.zookeeper.ZooDefs.OpCode;
import org.apache.zookeeper.server.Request;
import org.apache.zookeeper.server.ZooTrace;
import org.apache.zookeeper.server.quorum.Leader.Proposal;
import org.apache.zookeeper.server.util.SerializeUtils;
import org.apache.zookeeper.txn.TxnHeader;

/**
 * There will be an instance of this class created by the Leader for each
 * follower.All communication for a given Follower will be handled by this
 * class.
 */
public class FollowerHandler extends Thread {
    private static final Logger LOG = Logger.getLogger(FollowerHandler.class);

    public final Socket sock;

    final Leader leader;

    long tickOfLastAck;
    
    /**
     * ZooKeeper server identifier of this follower
     */
    protected long sid = 0;
    
    long getSid(){
        return sid;
    }

    /**
     * The packets to be sent to the follower
     */
    final LinkedBlockingQueue<QuorumPacket> queuedPackets =
        new LinkedBlockingQueue<QuorumPacket>();

    private BinaryInputArchive ia;

    private BinaryOutputArchive oa;

    private BufferedOutputStream bufferedOutput;

    FollowerHandler(Socket sock, Leader leader) throws IOException {
        super("FollowerHandler-" + sock.getRemoteSocketAddress());
        this.sock = sock;
        this.leader = leader;
        leader.addFollowerHandler(this);
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("FollowerHandler ").append(sock);
        sb.append(" tickOfLastAck:").append(tickOfLastAck());
        sb.append(" synced?:").append(synced());
        sb.append(" queuedPacketLength:").append(queuedPackets.size());
        return sb.toString();
    }

    /**
     * If this packet is queued, the sender thread will exit
     */
    final QuorumPacket proposalOfDeath = new QuorumPacket();

    /**
     * This method will use the thread to send packets added to the
     * queuedPackets list
     *
     * @throws InterruptedException
     */
    private void sendPackets() throws InterruptedException {
        long traceMask = ZooTrace.SERVER_PACKET_TRACE_MASK;
        while (true) {
            try {
                QuorumPacket p;
                p = queuedPackets.poll();
                if (p == null) {
                    bufferedOutput.flush();
                    p = queuedPackets.take();
                }

                if (p == proposalOfDeath) {
                    // Packet of death!
                    break;
                }
                if (p.getType() == Leader.PING) {
                    traceMask = ZooTrace.SERVER_PING_TRACE_MASK;
                }
                if (LOG.isTraceEnabled()) {
                    ZooTrace.logQuorumPacket(LOG, traceMask, 'o', p);
                }
                oa.writeRecord(p, "packet");
            } catch (IOException e) {
                if (!sock.isClosed()) {
                    LOG.warn("Unexpected exception",e);
                }
                break;
            }
        }
    }

    static public String packetToString(QuorumPacket p) {
        if (true)
            return null;
        String type = null;
        String mess = null;
        Record txn = null;
        
        switch (p.getType()) {
        case Leader.ACK:
            type = "ACK";
            break;
        case Leader.COMMIT:
            type = "COMMIT";
            break;
        case Leader.FOLLOWERINFO:
            type = "FOLLOWERINFO";
            break;
        case Leader.NEWLEADER:
            type = "NEWLEADER";
            break;
        case Leader.PING:
            type = "PING";
            break;
        case Leader.PROPOSAL:
            type = "PROPOSAL";
            BinaryInputArchive ia = BinaryInputArchive
                    .getArchive(new ByteArrayInputStream(p.getData()));
            TxnHeader hdr = new TxnHeader();
            try {
                txn = SerializeUtils.deserializeTxn(ia, hdr);
                // mess = "transaction: " + txn.toString();
            } catch (IOException e) {
                LOG.warn("Unexpected exception",e);
            }
            break;
        case Leader.REQUEST:
            type = "REQUEST";
            break;
        case Leader.REVALIDATE:
            type = "REVALIDATE";
            ByteArrayInputStream bis = new ByteArrayInputStream(p.getData());
            DataInputStream dis = new DataInputStream(bis);
            try {
                long id = dis.readLong();
                mess = " sessionid = " + id;
            } catch (IOException e) {
                LOG.warn("Unexpected exception", e);
            }

            break;
        case Leader.UPTODATE:
            type = "UPTODATE";
            break;
        default:
            type = "UNKNOWN" + p.getType();
        }
        String entry = null;
        if (type != null) {
            entry = type + " " + Long.toHexString(p.getZxid()) + " " + mess;
        }
        return entry;
    }

    /**
     * This thread will receive packets from the follower and process them and
     * also listen to new connections from new followers.
     */
    @Override
    public void run() {
        try {

            ia = BinaryInputArchive.getArchive(new BufferedInputStream(sock
                    .getInputStream()));
            bufferedOutput = new BufferedOutputStream(sock.getOutputStream());
            oa = BinaryOutputArchive.getArchive(bufferedOutput);

            QuorumPacket qp = new QuorumPacket();
            ia.readRecord(qp, "packet");
            if(qp.getType() != leader.FOLLOWERINFO){
            	LOG.error("First packet " + qp.toString()
                        + " is not FOLLOWERINFO!");
                return;
            }
            if (qp.getData() != null) {
            	ByteBuffer bbsid = ByteBuffer.wrap(qp.getData());
                this.sid = bbsid.getLong();
            } else {
            	this.sid = leader.followerCounter.getAndDecrement();
            }
            LOG.info("The follower sid: " + this.sid);
            
            long peerLastZxid = qp.getZxid();
            
            int packetToSend = Leader.SNAP;
            boolean logTxns = true;

            long zxidToSend = 0;
            // we are sending the diff
            synchronized(leader.zk.committedLog) {
                if (leader.zk.committedLog.size() != 0) {
                    if ((leader.zk.maxCommittedLog >= peerLastZxid)
                            && (leader.zk.minCommittedLog <= peerLastZxid)) {
                        packetToSend = Leader.DIFF;
                        zxidToSend = leader.zk.maxCommittedLog;
                        for (Proposal propose: leader.zk.committedLog) {
                            if (propose.packet.getZxid() > peerLastZxid) {
                                queuePacket(propose.packet);
                                QuorumPacket qcommit = new QuorumPacket(Leader.COMMIT, propose.packet.getZxid(),
                                        null, null);
                                queuePacket(qcommit);

                            }
                        }
                    }
                }
                else {
                    logTxns = false;
                }            }
            long leaderLastZxid = leader.startForwarding(this, peerLastZxid);
            QuorumPacket newLeaderQP = new QuorumPacket(Leader.NEWLEADER,
                    leaderLastZxid, null, null);
            oa.writeRecord(newLeaderQP, "packet");
            bufferedOutput.flush();
            // a special case when both the ids are the same
            if (peerLastZxid == leaderLastZxid) {
                packetToSend = Leader.DIFF;
                zxidToSend = leaderLastZxid;
            }
            //check if we decided to send a diff or we need to send a truncate
            // we avoid using epochs for truncating because epochs make things
            // complicated. Two epochs might have the last 32 bits as same.
            // only if we know that there is a committed zxid in the queue that
            // is less than the one the peer has we send a trunc else to make
            // things simple we just send sanpshot.
            if (logTxns && (peerLastZxid > leader.zk.maxCommittedLog)) {
                // this is the only case that we are sure that
                // we can ask the follower to truncate the log
                packetToSend = Leader.TRUNC;
                zxidToSend = leader.zk.maxCommittedLog;

            }
            oa.writeRecord(new QuorumPacket(packetToSend, zxidToSend, null, null), "packet");
            bufferedOutput.flush();
            // only if we are not truncating or fast sycning
            if (packetToSend == Leader.SNAP) {
                LOG.warn("Sending snapshot last zxid of peer is 0x"
                        + Long.toHexString(peerLastZxid) + " " 
                        + " zxid of leader is 0x"
                        + Long.toHexString(leaderLastZxid));
                // Dump data to follower
                leader.zk.serializeSnapshot(oa);
                oa.writeString("BenWasHere", "signature");
            }
            bufferedOutput.flush();
            //
            // Mutation packets will be queued during the serialize,
            // so we need to mark when the follower can actually start
            // using the data
            //
            queuedPackets
                    .add(new QuorumPacket(Leader.UPTODATE, -1, null, null));

            // Start sending packets
            new Thread() {
                public void run() {
                    Thread.currentThread().setName(
                            "Sender-" + sock.getRemoteSocketAddress());
                    try {
                        sendPackets();
                    } catch (InterruptedException e) {
                        LOG.warn("Unexpected interruption",e);
                    }
                }
            }.start();

            while (true) {
                qp = new QuorumPacket();
                ia.readRecord(qp, "packet");

                long traceMask = ZooTrace.SERVER_PACKET_TRACE_MASK;
                if (qp.getType() == Leader.PING) {
                    traceMask = ZooTrace.SERVER_PING_TRACE_MASK;
                }
                if (LOG.isTraceEnabled()) {
                    ZooTrace.logQuorumPacket(LOG, traceMask, 'i', qp);
                }
                tickOfLastAck = leader.self.tick;


                ByteBuffer bb;
                long sessionId;
                int cxid;
                int type;

                switch (qp.getType()) {
                case Leader.ACK:
                    leader.processAck(this.sid, qp.getZxid(), sock.getLocalSocketAddress());
                    break;
                case Leader.PING:
                    // Process the touches
                    ByteArrayInputStream bis = new ByteArrayInputStream(qp
                            .getData());
                    DataInputStream dis = new DataInputStream(bis);
                    while (dis.available() > 0) {
                        long sess = dis.readLong();
                        int to = dis.readInt();
                        leader.zk.touch(sess, to);
                    }
                    break;
                case Leader.REVALIDATE:
                    bis = new ByteArrayInputStream(qp.getData());
                    dis = new DataInputStream(bis);
                    long id = dis.readLong();
                    int to = dis.readInt();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(bos);
                    dos.writeLong(id);
                    boolean valid = leader.zk.touch(id, to);
                    if (valid) {
                        try {
                            leader.zk.setOwner(id, this);
                        } catch (SessionExpiredException e) {
                            LOG.error("Somehow session " + Long.toHexString(id) + " expired right after being renewed! (impossible)", e);
                        }
                    }
                    if (LOG.isTraceEnabled()) {
                        ZooTrace.logTraceMessage(LOG,
                                                 ZooTrace.SESSION_TRACE_MASK,
                                                 "Session 0x" + Long.toHexString(id)
                                                 + " is valid: "+ valid);
                    }
                    dos.writeBoolean(valid);
                    qp.setData(bos.toByteArray());
                    queuedPackets.add(qp);
                    break;
                case Leader.REQUEST:
                    bb = ByteBuffer.wrap(qp.getData());
                    sessionId = bb.getLong();
                    cxid = bb.getInt();
                    type = bb.getInt();
                    bb = bb.slice();
                    if(type == OpCode.sync){
                     	leader.zk.submitRequest(new FollowerSyncRequest(this, sessionId, cxid, type, bb,
                                qp.getAuthinfo()));
                    } else {
                        Request si = new Request(null, sessionId, cxid, type, bb, qp.getAuthinfo());
                        si.setOwner(this);
                        leader.zk.submitRequest(si);
                    }
                    break;
                default:
                }
            }
        } catch (IOException e) {
            if (sock != null && !sock.isClosed()) {
                LOG.error("Unexpected exception causing shutdown while sock "
                        + "still open", e);
            }
        } catch (InterruptedException e) {
            LOG.error("Unexpected exception causing shutdown", e);
        } finally {
            LOG.warn("******* GOODBYE " 
                    + (sock != null ? sock.getRemoteSocketAddress() : "<null>")
                    + " ********");
            // Send the packet of death
            try {
                queuedPackets.put(proposalOfDeath);
            } catch (InterruptedException e) {
                LOG.warn("Ignoring unexpected exception", e);
            }
            shutdown();
        }
    }

    public void shutdown() {
        try {
            if (sock != null && !sock.isClosed()) {
                sock.close();
            }
        } catch (IOException e) {
            LOG.warn("Ignoring unexpected exception during socket close", e);
        }
        leader.removeFollowerHandler(this);
    }

    public long tickOfLastAck() {
        return tickOfLastAck;
    }

    /**
     * ping calls from the leader to the followers
     */
    public void ping() {
        long id;
        synchronized(leader) {
            id = leader.lastProposed;
        }
        QuorumPacket ping = new QuorumPacket(Leader.PING, id,
                null, null);
        queuePacket(ping);
    }

    void queuePacket(QuorumPacket p) {
        queuedPackets.add(p);
    }

    public boolean synced() {
        return isAlive()
                && tickOfLastAck >= leader.self.tick - leader.self.syncLimit;
    }
}
