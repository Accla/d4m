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

package org.apache.hadoop.tools;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.HarFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.SequenceFileRecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


/**
 * a archive creation utility.
 * This class provides methods that can be used 
 * to create hadoop archives. For understanding of 
 * Hadoop archives look at {@link HarFileSystem}.
 */
public class HadoopArchives implements Tool {
  private static final Log LOG = LogFactory.getLog(HadoopArchives.class);
  
  private static final String NAME = "har"; 
  static final String SRC_LIST_LABEL = NAME + ".src.list";
  static final String DST_DIR_LABEL = NAME + ".dest.path";
  static final String TMP_DIR_LABEL = NAME + ".tmp.dir";
  static final String JOB_DIR_LABEL = NAME + ".job.dir";
  static final String SRC_COUNT_LABEL = NAME + ".src.count";
  static final String TOTAL_SIZE_LABEL = NAME + ".total.size";
  static final String DST_HAR_LABEL = NAME + ".archive.name";
  // size of each part file
  // its fixed for now.
  static final long partSize = 2 * 1024 * 1024 * 1024l;

  private static final String usage = "archive"
  + " -archiveName NAME <src>* <dest>" +
  "\n";
  
 
  private JobConf conf;

  public void setConf(Configuration conf) {
    if (conf instanceof JobConf) {
      this.conf = (JobConf) conf;
    } else {
      this.conf = new JobConf(conf, HadoopArchives.class);
    }
  }

  public Configuration getConf() {
    return this.conf;
  }

  public HadoopArchives(Configuration conf) {
    setConf(conf);
  }

  // check the src paths
  private static void checkPaths(Configuration conf, List<Path> paths) throws
  IOException {
    for (Path p : paths) {
      FileSystem fs = p.getFileSystem(conf);
      if (!fs.exists(p)) {
        throw new FileNotFoundException("Source " + p + " does not exist.");
      }
    }
  }

  /**
   * this assumes that there are two types of files file/dir
   * @param fs the input filesystem
   * @param p the top level path 
   * @param out the list of paths output of recursive ls
   * @throws IOException
   */
  private void recursivels(FileSystem fs, Path p, List<FileStatus> out) 
  throws IOException {
    FileStatus fstatus = fs.getFileStatus(p);
    if (!fstatus.isDir()) {
      out.add(fstatus);
      return;
    }
    else {
      out.add(fstatus);
      FileStatus[] listStatus = fs.listStatus(p);
      for (FileStatus stat: listStatus) {
        recursivels(fs, stat.getPath(), out);
      }
    }
  }

  /**
   * Input format of a hadoop archive job responsible for 
   * generating splits of the file list
   */

  static class HArchiveInputFormat implements InputFormat<LongWritable, Text> {

    //generate input splits from the src file lists
    public InputSplit[] getSplits(JobConf jconf, int numSplits)
    throws IOException {
      String srcfilelist = jconf.get(SRC_LIST_LABEL, "");
      if ("".equals(srcfilelist)) {
          throw new IOException("Unable to get the " +
              "src file for archive generation.");
      }
      long totalSize = jconf.getLong(TOTAL_SIZE_LABEL, -1);
      if (totalSize == -1) {
        throw new IOException("Invalid size of files to archive");
      }
      //we should be safe since this is set by our own code
      Path src = new Path(srcfilelist);
      FileSystem fs = src.getFileSystem(jconf);
      FileStatus fstatus = fs.getFileStatus(src);
      ArrayList<FileSplit> splits = new ArrayList<FileSplit>(numSplits);
      LongWritable key = new LongWritable();
      Text value = new Text();
      SequenceFile.Reader reader = null;
      // the remaining bytes in the file split
      long remaining = fstatus.getLen();
      // the count of sizes calculated till now
      long currentCount = 0L;
      // the endposition of the split
      long lastPos = 0L;
      // the start position of the split
      long startPos = 0L;
      long targetSize = totalSize/numSplits;
      // create splits of size target size so that all the maps 
      // have equals sized data to read and write to.
      try {
        reader = new SequenceFile.Reader(fs, src, jconf);
        while(reader.next(key, value)) {
          if (currentCount + key.get() > targetSize && currentCount != 0){
            long size = lastPos - startPos;
            splits.add(new FileSplit(src, startPos, size, (String[]) null));
            remaining = remaining - size;
            startPos = lastPos;
            currentCount = 0L;
          }
          currentCount += key.get();
          lastPos = reader.getPosition();
        }
        // the remaining not equal to the target size.
        if (remaining != 0) {
          splits.add(new FileSplit(src, startPos, remaining, (String[])null));
        }
      }
      finally { 
        reader.close();
      }
      return splits.toArray(new FileSplit[splits.size()]);
    }

    public RecordReader<LongWritable, Text> getRecordReader(InputSplit split,
        JobConf job, Reporter reporter) throws IOException {
      return new SequenceFileRecordReader<LongWritable, Text>(job,
                 (FileSplit)split);
    }
  }

  private boolean checkValidName(String name) {
    Path tmp = new Path(name);
    if (tmp.depth() != 1) {
      return false;
    }
    if (name.endsWith(".har")) 
      return true;
    return false;
  }
  

  private Path largestDepth(List<Path> paths) {
    Path deepest = paths.get(0);
    for (Path p: paths) {
      if (p.depth() > deepest.depth()) {
        deepest = p;
      }
    }
    return deepest;
  }
  
  // this method is tricky. This method writes 
  // the top level directories in such a way so that 
  // the output only contains valid directoreis in archives.
  // so for an input path specified by the user 
  // as /user/hadoop
  // we need to index 
  // / as the root 
  // /user as a directory
  // /user/hadoop as a directory
  // so for multiple input paths it makes sure that it
  // does the right thing.
  // so if the user specifies the input directories as 
  // /user/harry and /user/hadoop
  // we need to write / and user as its child
  // and /user and harry and hadoop as its children
  private void writeTopLevelDirs(SequenceFile.Writer srcWriter, 
      List<Path> paths) throws IOException {
    //these are qualified paths 
    List<Path> justDirs = new ArrayList<Path>();
    for (Path p: paths) {
      if (!p.getFileSystem(getConf()).isFile(p)) {
        justDirs.add(new Path(p.toUri().getPath()));
      }
      else {
        justDirs.add(new Path(p.getParent().toUri().getPath()));
      }
    }
    
    //get the largest depth path
    // this is tricky
    TreeMap<String, HashSet<String>> allpaths = new TreeMap<String, HashSet<String>>();
    Path deepest = largestDepth(paths);
    Path root = new Path(Path.SEPARATOR);
    for (int i = 0; i < deepest.depth(); i++) {
      List<Path> parents = new ArrayList<Path>();
      for (Path p: justDirs) {
        if (p.compareTo(root) == 0){
          //don nothing
        }
        else {
          Path parent = p.getParent();
          if (allpaths.containsKey(parent.toString())) {
            HashSet<String> children = allpaths.get(parent.toString());
            children.add(p.getName());
          }
          else {
            HashSet<String> children = new HashSet<String>();
            children.add(p.getName());
            allpaths.put(parent.toString(), children);
          }
          parents.add(parent);
        }
      }
      justDirs = parents;
    }
    Set<Map.Entry<String, HashSet<String>>> keyVals = allpaths.entrySet();
    for (Map.Entry<String, HashSet<String>> entry : keyVals) {
      HashSet<String> children = entry.getValue();
      String toWrite = entry.getKey() + " dir ";
      StringBuffer sbuff = new StringBuffer();
      sbuff.append(toWrite);
      for (String child: children) {
        sbuff.append(child + " ");
      }
      toWrite = sbuff.toString();
      srcWriter.append(new LongWritable(0L), new Text(toWrite));
    }
  }
  
  /**archive the given source paths into
   * the dest
   * @param srcPaths the src paths to be archived
   * @param dest the dest dir that will contain the archive
   */
  public void archive(List<Path> srcPaths, String archiveName, Path dest) 
  throws IOException {
    checkPaths(conf, srcPaths);
    int numFiles = 0;
    long totalSize = 0;
    conf.set(DST_HAR_LABEL, archiveName);
    Path outputPath = new Path(dest, archiveName);
    FileOutputFormat.setOutputPath(conf, outputPath);
    FileSystem outFs = outputPath.getFileSystem(conf);
    if (outFs.exists(outputPath) || outFs.isFile(dest)) {
      throw new IOException("Invalid Output.");
    }
    conf.set(DST_DIR_LABEL, outputPath.toString());
    final String randomId = DistCp.getRandomId();
    Path jobDirectory = new Path(new JobClient(conf).getSystemDir(),
                          NAME + "_" + randomId);
    conf.set(JOB_DIR_LABEL, jobDirectory.toString());
    //get a tmp directory for input splits
    FileSystem jobfs = jobDirectory.getFileSystem(conf);
    jobfs.mkdirs(jobDirectory);
    Path srcFiles = new Path(jobDirectory, "_har_src_files");
    conf.set(SRC_LIST_LABEL, srcFiles.toString());
    SequenceFile.Writer srcWriter = SequenceFile.createWriter(jobfs, conf,
        srcFiles, LongWritable.class, Text.class, 
        SequenceFile.CompressionType.NONE);
    // get the list of files 
    // create single list of files and dirs
    try {
      // write the top level dirs in first 
      writeTopLevelDirs(srcWriter, srcPaths);
      srcWriter.sync();
      // these are the input paths passed 
      // from the command line
      // we do a recursive ls on these paths 
      // and then write them to the input file 
      // one at a time
      for (Path src: srcPaths) {
        FileSystem fs = src.getFileSystem(conf);
        ArrayList<FileStatus> allFiles = new ArrayList<FileStatus>();
        recursivels(fs, src, allFiles);
        for (FileStatus stat: allFiles) {
          String toWrite = "";
          long len = stat.isDir()? 0:stat.getLen();
          if (stat.isDir()) {
            toWrite = "" + fs.makeQualified(stat.getPath()) + " dir ";
            //get the children 
            FileStatus[] list = fs.listStatus(stat.getPath());
            StringBuffer sbuff = new StringBuffer();
            sbuff.append(toWrite);
            for (FileStatus stats: list) {
              sbuff.append(stats.getPath().getName() + " ");
            }
            toWrite = sbuff.toString();
          }
          else {
            toWrite +=  fs.makeQualified(stat.getPath()) + " file ";
          }
          srcWriter.append(new LongWritable(len), new 
              Text(toWrite));
          srcWriter.sync();
          numFiles++;
          totalSize += len;
        }
      }
    } finally {
      srcWriter.close();
    }
    //increase the replication of src files
    jobfs.setReplication(srcFiles, (short) 10);
    conf.setInt(SRC_COUNT_LABEL, numFiles);
    conf.setLong(TOTAL_SIZE_LABEL, totalSize);
    int numMaps = (int)(totalSize/partSize);
    //run atleast one map.
    conf.setNumMapTasks(numMaps == 0? 1:numMaps);
    conf.setNumReduceTasks(1);
    conf.setInputFormat(HArchiveInputFormat.class);
    conf.setOutputFormat(NullOutputFormat.class);
    conf.setMapperClass(HArchivesMapper.class);
    conf.setReducerClass(HArchivesReducer.class);
    conf.setMapOutputKeyClass(IntWritable.class);
    conf.setMapOutputValueClass(Text.class);
    conf.set("hadoop.job.history.user.location", "none");
    FileInputFormat.addInputPath(conf, jobDirectory);
    //make sure no speculative execution is done
    conf.setSpeculativeExecution(false);
    JobClient.runJob(conf);
    //delete the tmp job directory
    try {
      jobfs.delete(jobDirectory, true);
    } catch(IOException ie) {
      LOG.info("Unable to clean tmp directory " + jobDirectory);
    }
  }

  static class HArchivesMapper 
  implements Mapper<LongWritable, Text, IntWritable, Text> {
    private JobConf conf = null;
    int partId = -1 ; 
    Path tmpOutputDir = null;
    Path tmpOutput = null;
    String partname = null;
    FSDataOutputStream partStream = null;
    FileSystem destFs = null;
    byte[] buffer;
    int buf_size = 128 * 1024;
    
    // configure the mapper and create 
    // the part file.
    // use map reduce framework to write into
    // tmp files. 
    public void configure(JobConf conf) {
      this.conf = conf;
      // this is tightly tied to map reduce
      // since it does not expose an api 
      // to get the partition
      partId = conf.getInt("mapred.task.partition", -1);
      // create a file name using the partition
      // we need to write to this directory
      tmpOutputDir = FileOutputFormat.getWorkOutputPath(conf);
      // get the output path and write to the tmp 
      // directory 
      partname = "part-" + partId;
      tmpOutput = new Path(tmpOutputDir, partname);
      try {
        destFs = tmpOutput.getFileSystem(conf);
        //this was a stale copy
        if (destFs.exists(tmpOutput)) {
          destFs.delete(tmpOutput, false);
        }
        partStream = destFs.create(tmpOutput);
      } catch(IOException ie) {
        throw new RuntimeException("Unable to open output file " + tmpOutput);
      }
      buffer = new byte[buf_size];
    }

    // copy raw data.
    public void copyData(Path input, FSDataInputStream fsin, 
        FSDataOutputStream fout, Reporter reporter) throws IOException {
      try {
        for (int cbread=0; (cbread = fsin.read(buffer))>= 0;) {
          fout.write(buffer, 0,cbread);
          reporter.progress();
        }
      } finally {
        fsin.close();
      }
    }
    
    // the relative path of p. basically 
    // getting rid of schema. Parsing and doing 
    // string manipulation is not good - so
    // just use the path api to do it.
    private Path makeRelative(Path p) {
      Path retPath = new Path(p.toUri().getPath());
      return retPath;
    }
    
    static class MapStat {
      private String pathname;
      private boolean isDir;
      private List<String> children;
      public MapStat(String line) {
        String[] splits = line.split(" ");
        pathname = splits[0];
        if ("dir".equals(splits[1])) {
          isDir = true;
        }
        else {
          isDir = false;
        }
        if (isDir) {
          children = new ArrayList<String>();
          for (int i = 2; i < splits.length; i++) {
            children.add(splits[i]);
          }
        }
      }
    }
    // read files from the split input 
    // and write it onto the part files.
    // also output hash(name) and string 
    // for reducer to create index 
    // and masterindex files.
    public void map(LongWritable key, Text value,
        OutputCollector<IntWritable, Text> out,
        Reporter reporter) throws IOException {
      String line  = value.toString();
      MapStat mstat = new MapStat(line);
      Path srcPath = new Path(mstat.pathname);
      String towrite = null;
      Path relPath = makeRelative(srcPath);
      int hash = HarFileSystem.getHarHash(relPath);
      long startPos = partStream.getPos();
      if (mstat.isDir) { 
        towrite = relPath.toString() + " " + "dir none " + 0 + " " + 0 + " ";
        StringBuffer sbuff = new StringBuffer();
        sbuff.append(towrite);
        for (String child: mstat.children) {
          sbuff.append(child + " ");
        }
        towrite = sbuff.toString();
        //reading directories is also progress
        reporter.progress();
      }
      else {
        FileSystem srcFs = srcPath.getFileSystem(conf);
        FileStatus srcStatus = srcFs.getFileStatus(srcPath);
        FSDataInputStream input = srcFs.open(srcStatus.getPath());
        reporter.setStatus("Copying file " + srcStatus.getPath() + 
            " to archive.");
        copyData(srcStatus.getPath(), input, partStream, reporter);
        towrite = relPath.toString() + " file " + partname + " " + startPos
        + " " + srcStatus.getLen() + " ";
      }
      out.collect(new IntWritable(hash), new Text(towrite));
    }
    
    public void close() throws IOException {
      // close the part files.
      partStream.close();
    }
  }
  
  /** the reduce for creating the index and the master index 
   * 
   */
  static class HArchivesReducer implements Reducer<IntWritable, 
  Text, Text, Text> {
    private JobConf conf = null;
    private long startIndex = 0;
    private long endIndex = 0;
    private long startPos = 0;
    private Path masterIndex = null;
    private Path index = null;
    private FileSystem fs = null;
    private FSDataOutputStream outStream = null;
    private FSDataOutputStream indexStream = null;
    private int numIndexes = 1000;
    private Path tmpOutputDir = null;
    private int written = 0;
    private int keyVal = 0;
    
    // configure 
    public void configure(JobConf conf) {
      this.conf = conf;
      tmpOutputDir = FileOutputFormat.getWorkOutputPath(this.conf);
      masterIndex = new Path(tmpOutputDir, "_masterindex");
      index = new Path(tmpOutputDir, "_index");
      try {
        fs = masterIndex.getFileSystem(conf);
        if (fs.exists(masterIndex)) {
          fs.delete(masterIndex, false);
        }
        if (fs.exists(index)) {
          fs.delete(index, false);
        }
        indexStream = fs.create(index);
        outStream = fs.create(masterIndex);
        String version = HarFileSystem.VERSION + " \n";
        outStream.write(version.getBytes());
        
      } catch(IOException e) {
        throw new RuntimeException(e);
      }
    }
    
    // create the index and master index. The input to 
    // the reduce is already sorted by the hash of the 
    // files. SO we just need to write it to the index. 
    // We update the masterindex as soon as we update 
    // numIndex entries.
    public void reduce(IntWritable key, Iterator<Text> values,
        OutputCollector<Text, Text> out,
        Reporter reporter) throws IOException {
      keyVal = key.get();
      while(values.hasNext()) {
        Text value = values.next();
        String towrite = value.toString() + "\n";
        indexStream.write(towrite.getBytes());
        written++;
        if (written > numIndexes -1) {
          // every 1000 indexes we report status
          reporter.setStatus("Creating index for archives");
          reporter.progress();
          endIndex = keyVal;
          String masterWrite = startIndex + " " + endIndex + " " + startPos 
                              +  " " + indexStream.getPos() + " \n" ;
          outStream.write(masterWrite.getBytes());
          startPos = indexStream.getPos();
          startIndex = endIndex;
          written = 0;
        }
      }
    }
    
    public void close() throws IOException {
      //write the last part of the master index.
      if (written > 0) {
        String masterWrite = startIndex + " " + keyVal + " " + startPos  +
                             " " + indexStream.getPos() + " \n";
        outStream.write(masterWrite.getBytes());
      }
      // close the streams
      outStream.close();
      indexStream.close();
      // try increasing the replication 
      fs.setReplication(index, (short) 10);
      fs.setReplication(masterIndex, (short) 10);
    }
    
  }
  
  /** the main driver for creating the archives
   *  it takes at least two command line parameters. The src and the 
   *  dest. It does an lsr on the source paths.
   *  The mapper created archuves and the reducer creates 
   *  the archive index.
   */

  public int run(String[] args) throws Exception {
    try {
      List<Path> srcPaths = new ArrayList<Path>();
      Path destPath = null;
      // check we were supposed to archive or 
      // unarchive
      String archiveName = null;
      if (args.length < 4) {
        System.out.println(usage);
        throw new IOException("Invalid usage.");
      }
      if (!"-archiveName".equals(args[0])) {
        System.out.println(usage);
        throw new IOException("Archive Name not specified.");
      }
      archiveName = args[1];
      if (!checkValidName(archiveName)) {
        System.out.println(usage);
        throw new IOException("Invalid name for archives. " + archiveName);
      }
      for (int i = 2; i < args.length; i++) {
        if (i == (args.length - 1)) {
          destPath = new Path(args[i]);
        }
        else {
          srcPaths.add(new Path(args[i]));
        }
      }
      if (srcPaths.size() == 0) {
        System.out.println(usage);
        throw new IOException("Invalid Usage: No input sources specified.");
      }
      // do a glob on the srcPaths and then pass it on
      List<Path> globPaths = new ArrayList<Path>();
      for (Path p: srcPaths) {
        FileSystem fs = p.getFileSystem(getConf());
        FileStatus[] statuses = fs.globStatus(p);
        for (FileStatus status: statuses) {
          globPaths.add(fs.makeQualified(status.getPath()));
        }
      }
      archive(globPaths, archiveName, destPath);
    } catch(IOException ie) {
      System.err.println(ie.getLocalizedMessage());
      return -1;
    }
    return 0;
  }

  /** the main functions **/
  public static void main(String[] args) {
    JobConf job = new JobConf(HadoopArchives.class);
    HadoopArchives harchives = new HadoopArchives(job);
    try {
      int res = harchives.run(args);
      System.exit(res);
    } catch(Exception e) {
      System.err.println(e.getLocalizedMessage());
    }
  }
}
