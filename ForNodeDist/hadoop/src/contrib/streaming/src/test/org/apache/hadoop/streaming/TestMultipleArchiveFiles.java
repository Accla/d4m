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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.zip.ZipEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipOutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.hdfs.MiniDFSCluster;

/**
 * This class tests cacheArchive option of streaming 
 * The test case creates 2 archive files, ships it with hadoop
 * streaming and compares the output with expected output
 */
public class TestMultipleArchiveFiles extends TestStreaming
{

  private StreamJob job;
  private String INPUT_FILE = "input.txt";
  private String CACHE_ARCHIVE_1 = "cacheArchive1.zip";
  private File CACHE_FILE_1 = null;
  private String CACHE_ARCHIVE_2 = "cacheArchive2.zip";
  private File CACHE_FILE_2 = null;
  private String expectedOutput = null;
  private String OUTPUT_DIR = "out";
  private Configuration conf = null;
  private MiniDFSCluster dfs = null;
  private MiniMRCluster mr = null;
  private FileSystem fileSys = null;
  private String strJobTracker = null;
  private String strNamenode = null;
  private String namenode = null;

  public TestMultipleArchiveFiles() throws IOException {
    CACHE_FILE_1 = new File("cacheArchive1");
    CACHE_FILE_2 = new File("cacheArchive2");
    input = "HADOOP";
    expectedOutput = "HADOOP\t\nHADOOP\t\n";
    try {
      conf = new Configuration();      
      dfs = new MiniDFSCluster(conf, 1, true, null);      
      fileSys = dfs.getFileSystem();
      namenode = fileSys.getUri().getAuthority();
      mr  = new MiniMRCluster(1, namenode, 3);
      strJobTracker = "mapred.job.tracker=" + "localhost:" + mr.getJobTrackerPort();
      strNamenode = "fs.default.name=" + namenode;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  protected void createInput() throws IOException
  {

    DataOutputStream dos = fileSys.create(new Path(INPUT_FILE));
    String inputFileString = "symlink1/cacheArchive1\nsymlink2/cacheArchive2";
    dos.write(inputFileString.getBytes("UTF-8"));
    dos.close();
    
    DataOutputStream out = fileSys.create(new Path(CACHE_ARCHIVE_1.toString()));
    ZipOutputStream zos = new ZipOutputStream(out);
    ZipEntry ze = new ZipEntry(CACHE_FILE_1.toString());
    zos.putNextEntry(ze);
    zos.write(input.getBytes("UTF-8"));
    zos.closeEntry();
    zos.close();

    out = fileSys.create(new Path(CACHE_ARCHIVE_2.toString()));
    zos = new ZipOutputStream(out);
    ze = new ZipEntry(CACHE_FILE_2.toString());
    zos.putNextEntry(ze);
    zos.write(input.getBytes("UTF-8"));
    zos.closeEntry();
    zos.close();
  }

  protected String[] genArgs() {
    String cacheArchiveString1 = null;
    String cacheArchiveString2 = null;
    try {
      cacheArchiveString1 = fileSys.getUri().toString()+fileSys.getWorkingDirectory().toString()+"/"+CACHE_ARCHIVE_1+"#symlink1";
      cacheArchiveString2 = fileSys.getUri().toString()+fileSys.getWorkingDirectory().toString()+"/"+CACHE_ARCHIVE_2+"#symlink2";
    } catch (Exception e) {
      e.printStackTrace();
    }

    return new String[] {
      "-input", INPUT_FILE.toString(),
      "-output", OUTPUT_DIR,
      "-mapper", "xargs cat", 
      "-reducer", "cat",
      "-jobconf", "mapred.reduce.tasks=1",
      "-cacheArchive", cacheArchiveString1, 
      "-cacheArchive", cacheArchiveString2,
      "-jobconf", strNamenode,
      "-jobconf", strJobTracker,
      "-jobconf", "stream.tmpdir=" + System.getProperty("test.build.data","/tmp")
    };
  }

  public void testCommandLine() {
    try {
      createInput();
      job = new StreamJob(genArgs(), true);
      if(job.go() != 0) {
        throw new Exception("Job Failed");
      }
      StringBuffer output = new StringBuffer(256);
      Path[] fileList = FileUtil.stat2Paths(fileSys.listStatus(
                                            new Path(OUTPUT_DIR)));
      for (int i = 0; i < fileList.length; i++){
        BufferedReader bread =
          new BufferedReader(new InputStreamReader(fileSys.open(fileList[i])));
        output.append(bread.readLine());
        output.append("\n");
        output.append(bread.readLine());
        output.append("\n");
      }
      assertEquals(expectedOutput, output.toString());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      CACHE_FILE_1.delete();
      CACHE_FILE_2.delete();
    }
  }

  public static void main(String[]args) throws Exception
  {
    new TestMultipleArchiveFiles().testCommandLine();
  }
}
