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

import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.HttpContext;
import org.mortbay.jetty.servlet.ServletHandler;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.examples.WordCount;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.DataOutputStream;
import java.util.Date;

/**
 * Base class to test Job end notification in local and cluster mode.
 *
 * Starts up hadoop on Local or Cluster mode (by extending of the
 * HadoopTestCase class) and it starts a servlet engine that hosts
 * a servlet that will receive the notification of job finalization.
 *
 * The notification servlet returns a HTTP 400 the first time is called
 * and a HTTP 200 the second time, thus testing retry.
 *
 * In both cases local file system is used (this is irrelevant for
 * the tested functionality)
 *
 * 
 */
public abstract class NotificationTestCase extends HadoopTestCase {

  private static void stdPrintln(String s) {
    //System.out.println(s);
  }

  protected NotificationTestCase(int mode) throws IOException {
    super(mode, HadoopTestCase.LOCAL_FS, 1, 1);
  }

  private int port;
  private String contextPath = "/notification";
  private Class servletClass = NotificationServlet.class;
  private String servletPath = "/mapred";
  private HttpServer server;

  private void startHttpServer() throws Exception {

    // Create the server
    if (server != null) {
      server.stop();
      server = null;
    }
    server = new HttpServer();

    // Create a port listener
    SocketListener listener = new SocketListener();
    listener.setPort(0); // letting OS to pickup the PORT
    server.addListener(listener);

    // create context
    HttpContext context = new HttpContext();
    context.setContextPath(contextPath + "/*");

    // create servlet handler
    ServletHandler handler = new ServletHandler();
    handler.addServlet(servletClass.getName(), servletPath,
                       servletClass.getName());

    // bind servlet handler to context
    context.addHandler(handler);

    // bind context to servlet engine
    server.addContext(context);

    // Start server
    server.start();
    port = listener.getPort();

  }

  private void stopHttpServer() throws Exception {
    if (server != null) {
      server.stop();
      server.destroy();
      server = null;
    }
  }

  public static class NotificationServlet extends HttpServlet {
    public static int counter = 0;

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      if (counter == 0) {
        stdPrintln((new Date()).toString() +
                   "Receiving First notification for [" + req.getQueryString() +
                   "], returning error");
        res.sendError(HttpServletResponse.SC_BAD_REQUEST, "forcing error");
      }
      else {
        stdPrintln((new Date()).toString() +
                   "Receiving Second notification for [" + req.getQueryString() +
                   "], returning OK");
        res.setStatus(HttpServletResponse.SC_OK);
      }
      counter++;
    }
  }

  private String getNotificationUrlTemplate() {
    return "http://localhost:" + port + contextPath + servletPath +
      "?jobId=$jobId&amp;jobStatus=$jobStatus";
  }

  protected JobConf createJobConf() {
    JobConf conf = super.createJobConf();
    conf.setJobEndNotificationURI(getNotificationUrlTemplate());
    conf.setInt("job.end.retry.attempts", 3);
    conf.setInt("job.end.retry.interval", 200);
    return conf;
  }


  protected void setUp() throws Exception {
    super.setUp();
    startHttpServer();
  }

  protected void tearDown() throws Exception {
    stopHttpServer();
    super.tearDown();
  }

  public void testMR() throws Exception {
    System.out.println(launchWordCount(this.createJobConf(),
                                       "a b c d e f g h", 1, 1));
    synchronized(Thread.currentThread()) {
      stdPrintln("Sleeping for 2 seconds to give time for retry");
      Thread.currentThread().sleep(2000);
    }
    assertEquals(2, NotificationServlet.counter);
  }

  private String launchWordCount(JobConf conf,
                                 String input,
                                 int numMaps,
                                 int numReduces) throws IOException {
    Path inDir = new Path("testing/wc/input");
    Path outDir = new Path("testing/wc/output");

    // Hack for local FS that does not have the concept of a 'mounting point'
    if (isLocalFS()) {
      String localPathRoot = System.getProperty("test.build.data","/tmp")
        .toString().replace(' ', '+');;
      inDir = new Path(localPathRoot, inDir);
      outDir = new Path(localPathRoot, outDir);
    }

    FileSystem fs = FileSystem.get(conf);
    fs.delete(outDir, true);
    if (!fs.mkdirs(inDir)) {
      throw new IOException("Mkdirs failed to create " + inDir.toString());
    }
    {
      DataOutputStream file = fs.create(new Path(inDir, "part-0"));
      file.writeBytes(input);
      file.close();
    }
    conf.setJobName("wordcount");
    conf.setInputFormat(TextInputFormat.class);

    // the keys are words (strings)
    conf.setOutputKeyClass(Text.class);
    // the values are counts (ints)
    conf.setOutputValueClass(IntWritable.class);

    conf.setMapperClass(WordCount.MapClass.class);
    conf.setCombinerClass(WordCount.Reduce.class);
    conf.setReducerClass(WordCount.Reduce.class);

    FileInputFormat.setInputPaths(conf, inDir);
    FileOutputFormat.setOutputPath(conf, outDir);
    conf.setNumMapTasks(numMaps);
    conf.setNumReduceTasks(numReduces);
    JobClient.runJob(conf);
    return TestMiniMRWithDFS.readOutput(outDir, conf);
  }

}
