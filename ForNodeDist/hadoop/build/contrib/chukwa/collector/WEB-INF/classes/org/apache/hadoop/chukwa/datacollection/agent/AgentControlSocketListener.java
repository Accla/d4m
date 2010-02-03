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

package org.apache.hadoop.chukwa.datacollection.agent;
import java.net.*;
import java.io.*;

import org.apache.hadoop.chukwa.datacollection.adaptor.*;
import org.apache.hadoop.chukwa.conf.ChukwaConfiguration;
import org.apache.log4j.Logger;
import java.util.Map;

/**
 * Class to handle the agent control protocol.
 * This is a simple line-oriented ASCII protocol, that is designed
 * to be easy to work with both programmatically and via telnet.
 *
 *  The port to bind to can be specified by setting option
 *     chukwaAgent.agent.control.port
 */
public class AgentControlSocketListener extends Thread {


  static Logger log= Logger.getLogger(AgentControlSocketListener.class);
  
  ChukwaAgent agent;
  int portno;
  ServerSocket s= null;
  boolean closing = false;
  
  private class ListenThread extends Thread
  {
    Socket connection;
    ListenThread(Socket conn)  {
      connection = conn;
      this.setName("listen thread for " + connection.getRemoteSocketAddress());
    }
    
    public void run()  {
      try {
      InputStream in = connection.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      PrintStream out = new PrintStream(new BufferedOutputStream(connection.getOutputStream()));
      String cmd = null;
      while((cmd = br.readLine()) != null)  {
        processCommand(cmd, out);
      }
      log.info("control connection closed");
      }
      catch(SocketException e ) {
        if(e.getMessage().equals("Socket Closed"))
          log.info("control socket closed");
      } catch(IOException e)  {
        log.warn("a control connection broke", e);
      }
    }
    
    /**
     * process a protocol command
     * @param cmd the command given by the user
     * @param out  a PrintStream writing to the socket
     * @throws IOException
     */
    public void processCommand(String cmd, PrintStream out) throws IOException  {
      String[] words = cmd.split(" ");
      log.info("command from " + connection.getRemoteSocketAddress() + ":"+ cmd);
      
      if(words[0].equalsIgnoreCase("help"))  {
        out.println("you're talking to the Chukwa agent.  Commands available: ");
        out.println("add [adaptorname] [args] [offset] -- start an adaptor");
        out.println("shutdown [adaptornumber]  -- graceful stop");
        out.println("stop [adaptornumber]  -- abrupt stop");
        out.println("list -- list running adaptors");
        out.println("close -- close this connection");
        out.println("stopagent -- stop the whole agent process");
        out.println("help -- print this message");
        out.println("\t Command names are case-blind.");
      }
      else if(words[0].equalsIgnoreCase("close"))  {
        connection.close();
      }
      else if(words[0].equalsIgnoreCase("add"))   {
        long newID = agent.processCommand(cmd);
        if(newID != -1)
          out.println("OK add completed; new ID is " +newID);
        else
          out.println("failed to start adaptor...check logs for details");
      }
      else if(words[0].equalsIgnoreCase("shutdown"))  {
        if(words.length < 2) {
          out.println("need to specify an adaptor to shut down, by number");
        }
        else {
          long num = Long.parseLong(words[1]);
          long offset = agent.stopAdaptor(num, true);
          if(offset != -1)
            out.println("OK adaptor "+ num+ " stopping gracefully at " + offset);
          else
            out.println("FAIL: perhaps adaptor " + num + " does not exist");
        }
      }     
      else if(words[0].equalsIgnoreCase("stop"))  {
        if(words.length < 2) {
          out.println("need to specify an adaptor to shut down, by number");
        } else {
          long num = Long.parseLong(words[1]);
          agent.stopAdaptor(num, false);
          out.println("OK adaptor "+ num+ " stopped");
        }
      }
      else if(words[0].equalsIgnoreCase("list") )  {
        Map<Long, Adaptor> adaptorsByNumber = agent.getAdaptorList();
        System.out.println("number of adaptors: " + adaptorsByNumber.size());
        synchronized(adaptorsByNumber)   {
          for(Map.Entry<Long, Adaptor> a: adaptorsByNumber.entrySet())  {
            try{
              out.print(a.getKey());
              out.print(") ");
              out.print(" ");
              out.println(formatAdaptorStatus(a.getValue()));
            }  catch(AdaptorException e)  {
              log.error(e);
            }
          }
          out.println("");
        }
      } else if(words[0].equalsIgnoreCase("stopagent")) {
        out.println("stopping agent process.");
        connection.close();
        agent.shutdown(true);
      }
      else  {
        log.warn("unknown command " + words[0]);
        out.println("unknown command" + words[0]);
        out.println("say 'help' for a list of legal commands");
      }
      out.flush();
    }
    
  }
  /**
   * Initializes listener, but does not bind to socket.
   * @param a the agent to control
   */
  public AgentControlSocketListener(ChukwaAgent a)
  {
    ChukwaConfiguration conf = new ChukwaConfiguration();
    this.setDaemon(false); //to keep the local agent alive
    agent = a;
    portno = conf.getInt("chukwaAgent.agent.control.port", 9093);
    log.info("AgentControlSocketListerner port set to " + portno);
    this.setName("control socket listener");
  }
  
  public String formatAdaptorStatus(Adaptor a)  throws AdaptorException  {
    return a.getClass().getCanonicalName() + " " + a.getCurrentStatus() + " " + agent.getOffset(a);
  }

  /**
   * Binds to socket, starts looping listening for commands
   */
  public void run()  {
    try {
      if(!isBound()) 
        tryToBind();
    } catch(IOException e) {
      return;
    }
    
    while(!closing)
    {
      try {
        Socket connection = s.accept();
        log.info("new connection from " + connection.getInetAddress());
        ListenThread l = new ListenThread(connection);
        l.setDaemon(true);
        l.start();
      } catch(IOException e)  {
        if(!closing)
          log.warn("control socket error: ",e );
        else {
          log.info("shutting down listen thread due to shutdown() call");
          break;
        }
      }
    }//end while
  }
  /**
   * Close the control socket, and exit. Triggers graceful thread shutdown.
   */
  public void shutdown()  {
    closing = true;
    try{
      if(s != null)
        s.close();
      s = null;
    }
    catch(IOException e)
    {}  //ignore exception on close
  }

  public boolean isBound() {
    return s!= null &&  s.isBound();
  }

  public void tryToBind() throws IOException
  {
    s= new ServerSocket(portno);
    if(s.isBound())
      log.debug("socket bound to " + portno);
    else
      log.debug("socket isn't bound");
     
  }
  
}
