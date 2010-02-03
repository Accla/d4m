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

import java.io.*;
import java.util.*;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.filecache.*;
import java.net.URI;

public class MRCaching {
  static String testStr = "This is a test file " + "used for testing caching "
    + "jars, zip and normal files.";

  /**
   * Using the wordcount example and adding caching to it. The cache
   * archives/files are set and then are checked in the map if they have been
   * localized or not.
   */
  public static class MapClass extends MapReduceBase
    implements Mapper<LongWritable, Text, Text, IntWritable> {
    
    JobConf conf;

    private final static IntWritable one = new IntWritable(1);

    private Text word = new Text();

    public void configure(JobConf jconf) {
      conf = jconf;
      try {
        Path[] localArchives = DistributedCache.getLocalCacheArchives(conf);
        Path[] localFiles = DistributedCache.getLocalCacheFiles(conf);
        // read the cached files (unzipped, unjarred and text)
        // and put it into a single file TEST_ROOT_DIR/test.txt
        String TEST_ROOT_DIR = jconf.get("test.build.data","/tmp");
        Path file = new Path("file:///", TEST_ROOT_DIR);
        FileSystem fs = FileSystem.getLocal(conf);
        if (!fs.mkdirs(file)) {
          throw new IOException("Mkdirs failed to create " + file.toString());
        }
        Path fileOut = new Path(file, "test.txt");
        fs.delete(fileOut, true);
        DataOutputStream out = fs.create(fileOut);
        for (int i = 0; i < localArchives.length; i++) {
          // read out the files from these archives
          File f = new File(localArchives[i].toString());
          File txt = new File(f, "test.txt");
          FileInputStream fin = new FileInputStream(txt);
          DataInputStream din = new DataInputStream(fin);
          String str = din.readLine();
          din.close();
          out.writeBytes(str);
          out.writeBytes("\n");
        }
        for (int i = 0; i < localFiles.length; i++) {
          // read out the files from these archives
          File txt = new File(localFiles[i].toString());
          FileInputStream fin = new FileInputStream(txt);
          DataInputStream din = new DataInputStream(fin);
          String str = din.readLine();
          out.writeBytes(str);
          out.writeBytes("\n");
        }
        out.close();
      } catch (IOException ie) {
        System.out.println(StringUtils.stringifyException(ie));
      }
    }

    public void map(LongWritable key, Text value,
                    OutputCollector<Text, IntWritable> output,
                    Reporter reporter) throws IOException {
      String line = value.toString();
      StringTokenizer itr = new StringTokenizer(line);
      while (itr.hasMoreTokens()) {
        word.set(itr.nextToken());
        output.collect(word, one);
      }

    }
  }

  /**
   * A reducer class that just emits the sum of the input values.
   */
  public static class ReduceClass extends MapReduceBase
    implements Reducer<Text, IntWritable, Text, IntWritable> {

    public void reduce(Text key, Iterator<IntWritable> values,
                       OutputCollector<Text, IntWritable> output,
                       Reporter reporter) throws IOException {
      int sum = 0;
      while (values.hasNext()) {
        sum += values.next().get();
      }
      output.collect(key, new IntWritable(sum));
    }
  }

  public static class TestResult {
    public RunningJob job;
    public boolean isOutputOk;
    TestResult(RunningJob job, boolean isOutputOk) {
      this.job = job;
      this.isOutputOk = isOutputOk;
    }
  }

  public static TestResult launchMRCache(String indir,
                                         String outdir, String cacheDir, 
                                         JobConf conf, String input)
    throws IOException {
    String TEST_ROOT_DIR = new Path(System.getProperty("test.build.data","/tmp"))
      .toString().replace(' ', '+');
    //if (TEST_ROOT_DIR.startsWith("C:")) TEST_ROOT_DIR = "/tmp";
    conf.set("test.build.data", TEST_ROOT_DIR);
    final Path inDir = new Path(indir);
    final Path outDir = new Path(outdir);
    FileSystem fs = FileSystem.get(conf);
    fs.delete(outDir, true);
    if (!fs.mkdirs(inDir)) {
      throw new IOException("Mkdirs failed to create " + inDir.toString());
    }
    {
      System.out.println("HERE:"+inDir);
      DataOutputStream file = fs.create(new Path(inDir, "part-0"));
      file.writeBytes(input);
      file.close();
    }
    conf.setJobName("cachetest");

    // the keys are words (strings)
    conf.setOutputKeyClass(Text.class);
    // the values are counts (ints)
    conf.setOutputValueClass(IntWritable.class);

    conf.setMapperClass(MRCaching.MapClass.class);
    conf.setCombinerClass(MRCaching.ReduceClass.class);
    conf.setReducerClass(MRCaching.ReduceClass.class);
    FileInputFormat.setInputPaths(conf, inDir);
    FileOutputFormat.setOutputPath(conf, outDir);
    conf.setNumMapTasks(1);
    conf.setNumReduceTasks(1);
    conf.setSpeculativeExecution(false);
    Path localPath = new Path("build/test/cache");
    Path txtPath = new Path(localPath, new Path("test.txt"));
    Path jarPath = new Path(localPath, new Path("test.jar"));
    Path zipPath = new Path(localPath, new Path("test.zip"));
    Path tarPath = new Path(localPath, new Path("test.tgz"));
    Path tarPath1 = new Path(localPath, new Path("test.tar.gz"));
    Path tarPath2 = new Path(localPath, new Path("test.tar"));
    Path cachePath = new Path(cacheDir);
    fs.delete(cachePath, true);
    if (!fs.mkdirs(cachePath)) {
      throw new IOException("Mkdirs failed to create " + cachePath.toString());
    }
    fs.copyFromLocalFile(txtPath, cachePath);
    fs.copyFromLocalFile(jarPath, cachePath);
    fs.copyFromLocalFile(zipPath, cachePath);
    fs.copyFromLocalFile(tarPath, cachePath);
    fs.copyFromLocalFile(tarPath1, cachePath);
    fs.copyFromLocalFile(tarPath2, cachePath);
    // setting the cached archives to zip, jar and simple text files
    URI uri1 = fs.getUri().resolve(cachePath + "/test.jar");
    URI uri2 = fs.getUri().resolve(cachePath + "/test.zip");
    URI uri3 = fs.getUri().resolve(cachePath + "/test.txt");
    URI uri4 = fs.getUri().resolve(cachePath + "/test.tgz");
    URI uri5 = fs.getUri().resolve(cachePath + "/test.tar.gz");
    URI uri6 = fs.getUri().resolve(cachePath + "/test.tar");

    DistributedCache.addCacheArchive(uri1, conf);
    DistributedCache.addCacheArchive(uri2, conf);
    DistributedCache.addCacheFile(uri3, conf);
    DistributedCache.addCacheArchive(uri4, conf);
    DistributedCache.addCacheArchive(uri5, conf);
    DistributedCache.addCacheArchive(uri6, conf);
    RunningJob job = JobClient.runJob(conf);
    int count = 0;
    // after the job ran check to see if the the input from the localized cache
    // match the real string. check if there are 3 instances or not.
    Path result = new Path(TEST_ROOT_DIR + "/test.txt");
    {
      BufferedReader file = new BufferedReader
         (new InputStreamReader(FileSystem.getLocal(conf).open(result)));
      String line = file.readLine();
      while (line != null) {
        if (!testStr.equals(line))
          return new TestResult(job, false);
        count++;
        line = file.readLine();

      }
      file.close();
    }
    if (count != 6)
      return new TestResult(job, false);

    return new TestResult(job, true);

  }
}
