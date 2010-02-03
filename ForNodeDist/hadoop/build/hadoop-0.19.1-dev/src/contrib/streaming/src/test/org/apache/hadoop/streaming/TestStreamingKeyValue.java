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

package org.apache.hadoop.streaming;

import junit.framework.TestCase;
import java.io.*;

/**
 * This class tests hadoopStreaming in MapReduce local mode.
 * This testcase looks at different cases of tab position in input. 
 */
public class TestStreamingKeyValue extends TestCase
{
  protected File INPUT_FILE = new File("input.txt");
  protected File OUTPUT_DIR = new File("stream_out");
  // First line of input has 'key' 'tab' 'value'
  // Second line of input starts with a tab character. 
  // So, it has empty key and the whole line as value.
  // Third line of input does not have any tab character.
  // So, the whole line is the key and value is empty.
  protected String input = 
    "roses are \tred\t\n\tviolets are blue\nbunnies are pink\n" +
    "this is for testing a big\tinput line\n" +
    "small input\n";
  protected String outputExpect = 
    "\tviolets are blue\nbunnies are pink\t\n" + 
    "roses are \tred\t\n" +
    "small input\t\n" +
    "this is for testing a big\tinput line\n";

  private StreamJob job;

  public TestStreamingKeyValue() throws IOException
  {
    UtilTest utilTest = new UtilTest(getClass().getName());
    utilTest.checkUserDir();
    utilTest.redirectIfAntJunit();
  }

  protected void createInput() throws IOException
  {
    DataOutputStream out = new DataOutputStream(
       new FileOutputStream(INPUT_FILE.getAbsoluteFile()));
    out.write(input.getBytes("UTF-8"));
    out.close();
  }

  protected String[] genArgs() {
    return new String[] {
      "-input", INPUT_FILE.getAbsolutePath(),
      "-output", OUTPUT_DIR.getAbsolutePath(),
      "-mapper", "cat",
      "-jobconf", "keep.failed.task.files=true",
      "-jobconf", "stream.non.zero.exit.is.failure=true",
      "-jobconf", "stream.tmpdir="+System.getProperty("test.build.data","/tmp")
    };
  }
  
  public void testCommandLine()
  {
    String outFileName = "part-00000";
    File outFile = null;
    try {
      try {
        OUTPUT_DIR.getAbsoluteFile().delete();
      } catch (Exception e) {
      }

      createInput();
      boolean mayExit = false;

      // During tests, the default Configuration will use a local mapred
      // So don't specify -config or -cluster
      job = new StreamJob(genArgs(), mayExit);      
      job.go();
      outFile = new File(OUTPUT_DIR, outFileName).getAbsoluteFile();
      String output = StreamUtil.slurp(outFile);
      System.err.println("outEx1=" + outputExpect);
      System.err.println("  out1=" + output);
      assertEquals(outputExpect, output);
    } catch(Exception e) {
      failTrace(e);
    } finally {
      outFile.delete();
      File outFileCRC = new File(OUTPUT_DIR,
                          "." + outFileName + ".crc").getAbsoluteFile();
      INPUT_FILE.delete();
      outFileCRC.delete();
      OUTPUT_DIR.getAbsoluteFile().delete();
    }
  }

  private void failTrace(Exception e)
  {
    StringWriter sw = new StringWriter();
    e.printStackTrace(new PrintWriter(sw));
    fail(sw.toString());
  }

  public static void main(String[]args) throws Exception
  {
    new TestStreamingKeyValue().testCommandLine();
  }

}
