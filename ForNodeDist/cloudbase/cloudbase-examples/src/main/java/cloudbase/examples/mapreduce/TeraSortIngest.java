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

package cloudbase.examples.mapreduce;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import cloudbase.core.data.Mutation;
import cloudbase.core.data.Value;


/**
 * Generate the *almost* official terasort input data set.  (See below)
 * The user specifies the number of rows and the output directory and this
 * class runs a map/reduce program to generate the data.
 * The format of the data is:
 * <ul>
 * <li>(10 bytes key) (10 bytes rowid) (78 bytes filler) \r \n
 * <li>The keys are random characters from the set ' ' .. '~'.
 * <li>The rowid is the right justified row id as a int.
 * <li>The filler consists of 7 runs of 10 characters from 'A' to 'Z'.
 * </ul>
 *
 * This TeraSort is slightly modified to allow for variable length 
 * key sizes and value sizes.  The row length isn't variable.  To generate a 
 * terabyte of data in the same way TeraSort does use 10000000000 rows and 
 * 10/10 byte key length and 78/78 byte value length.  Along with the 10 byte row id and \r\n  
 * this gives you 100 byte row * 10000000000 rows = 1tb.  Min/Max ranges for key
 * and value parameters are inclusive/inclusive respectively.
 * 	    	
 *	Params <numrows> <minkeylength> <maxkeylength> <minvaluelength> <maxvaluelength> <tablename> <instance> <username> <password> [rows_per_split]
 *	rows_per_split allows you specify how many rows you want each split to 
 *  contain (and consequently each mapper to handle at one time)
 * 
 * 
 */
public class TeraSortIngest extends Configured implements Tool {

	
	
  /**
   * An input format that assigns ranges of longs to each mapper.
   */
  static class RangeInputFormat
       implements InputFormat<LongWritable, NullWritable> {
	  
	

    /**
     * An input split consisting of a range on numbers.
     */
    static class RangeInputSplit implements InputSplit {
      long firstRow;
      long rowCount;

      public RangeInputSplit() { }

      public RangeInputSplit(long offset, long length) {
        firstRow = offset;
        rowCount = length;
      }

      public long getLength() throws IOException {
        return 0;
      }

      public String[] getLocations() throws IOException {
        return new String[]{};
      }

      public void readFields(DataInput in) throws IOException {
        firstRow = WritableUtils.readVLong(in);
        rowCount = WritableUtils.readVLong(in);
      }

      public void write(DataOutput out) throws IOException {
        WritableUtils.writeVLong(out, firstRow);
        WritableUtils.writeVLong(out, rowCount);
      }
    }

    /**
     * A record reader that will generate a range of numbers.
     */
    static class RangeRecordReader
          implements RecordReader<LongWritable, NullWritable> {
      long startRow;
      long finishedRows;
      long totalRows;

      public RangeRecordReader(RangeInputSplit split) {
        startRow = split.firstRow;
        finishedRows = 0;
        totalRows = split.rowCount;
      }

      public void close() throws IOException {
        // NOTHING
      }

      public LongWritable createKey() {
        return new LongWritable();
      }

      public NullWritable createValue() {
         // return new Text();
        return NullWritable.get();
      }

      public long getPos() throws IOException {
        return finishedRows;
      }

      public float getProgress() throws IOException {
        return finishedRows / (float) totalRows;
      }

      public boolean next(LongWritable key,
                          NullWritable value) {
        if (finishedRows < totalRows) {
          key.set(startRow + finishedRows);
          finishedRows += 1;
          return true;
        } else {
          return false;
        }
      }

    }

    public RecordReader<LongWritable, NullWritable>
      getRecordReader(InputSplit split, JobConf job,
                      Reporter reporter) throws IOException {
        // reporter.setStatus("Creating record reader");
      return new RangeRecordReader((RangeInputSplit) split);
    }

    /**
     * Create the desired number of splits, dividing the number of rows
     * between the mappers.
     */
    public InputSplit[] getSplits(JobConf job,
                                  int _numSplits) {
      long totalRows = getNumberOfRows(job);
      int numSplits = getNumSplits(job) > 0 ? getNumSplits(job) : _numSplits;
      long rowsPerSplit = totalRows / numSplits;
      System.out.println("Generating " + totalRows + " using " + numSplits +
                         " maps with step of " + rowsPerSplit);
      InputSplit[] splits = new InputSplit[numSplits];
      long currentRow = 0;
      for(int split=0; split < numSplits-1; ++split) {
        splits[split] = new RangeInputSplit(currentRow, rowsPerSplit);
        currentRow += rowsPerSplit;
      }
      splits[numSplits-1] = new RangeInputSplit(currentRow,
                                                totalRows - currentRow);
      System.out.println("Done Generating.");
      return splits;
    }

  }

  private static String ROWSPERSPLIT = "terasort.overridesplits";
  private static String NUMROWS = "terasort.numrows";
  
  private static long getNumberOfRows(JobConf job) {
    return job.getLong(NUMROWS, 0);
  }
  
  private static int getNumSplits(JobConf job) {
	  return job.getInt(ROWSPERSPLIT, -1);
  }

  static class RandomGenerator {
    private long seed = 0;
    private static final long mask32 = (1l<<32) - 1;
    /**
     * The number of iterations separating the precomputed seeds.
     */
    private static final int seedSkip = 128 * 1024 * 1024;
    /**
     * The precomputed seed values after every seedSkip iterations.
     * There should be enough values so that a 2**32 iterations are
     * covered.
     */
    private static final long[] seeds = new long[]{0L,
                                                   4160749568L,
                                                   4026531840L,
                                                   3892314112L,
                                                   3758096384L,
                                                   3623878656L,
                                                   3489660928L,
                                                   3355443200L,
                                                   3221225472L,
                                                   3087007744L,
                                                   2952790016L,
                                                   2818572288L,
                                                   2684354560L,
                                                   2550136832L,
                                                   2415919104L,
                                                   2281701376L,
                                                   2147483648L,
                                                   2013265920L,
                                                   1879048192L,
                                                   1744830464L,
                                                   1610612736L,
                                                   1476395008L,
                                                   1342177280L,
                                                   1207959552L,
                                                   1073741824L,
                                                   939524096L,
                                                   805306368L,
                                                   671088640L,
                                                   536870912L,
                                                   402653184L,
                                                   268435456L,
                                                   134217728L,
                                                  };

    /**
     * Start the random number generator on the given iteration.
     * @param initalIteration the iteration number to start on
     */
    RandomGenerator(long initalIteration) {
      int baseIndex = (int) ((initalIteration & mask32) / seedSkip);
      seed = seeds[baseIndex];
      for(int i=0; i < initalIteration % seedSkip; ++i) {
        next();
      }
    }

    RandomGenerator() {
      this(0);
    }

    long next() {
      seed = (seed * 3141592621l + 663896637) & mask32;
      return seed;
    }
  }

  /**
   * The Mapper class that given a row number, will generate the appropriate
   * output line.
   */
  public static class SortGenMapper implements Mapper<LongWritable, NullWritable, Text, Mutation>
  {
    private Text table = null;
    private int minkeylength = 0;
    private int maxkeylength = 0;
    private int minvaluelength = 0;
    private int maxvaluelength = 0;

    private Text key = new Text();
    private Text value = new Text();
    private RandomGenerator rand;
    private byte[] keyBytes; // = new byte[12];
    private byte[] spaces = "          ".getBytes();
    private byte[][] filler = new byte[26][];
    {
      for(int i=0; i < 26; ++i) {
        filler[i] = new byte[10];
        for(int j=0; j<10; ++j) {
          filler[i][j] = (byte) ('A' + i);
        }
      }
    }

    /**
     * Add a random key to the text
     * @param rowId
     */
    private Random random = new Random();
    private void addKey() {
        int range = random.nextInt(maxkeylength - minkeylength + 1);
        int keylen = range + minkeylength;
        int keyceil = keylen + (4 - (keylen%4));
        keyBytes = new byte[keyceil];
       
        long temp = 0;
        for(int i=0; i<keyceil/4; i++) {
            temp = rand.next() / 52;
            keyBytes[3 + 4*i] = (byte) (' ' + (temp % 95));
            temp /= 95;
            keyBytes[2 + 4*i] = (byte) (' ' + (temp % 95));
            temp /= 95;
            keyBytes[1 + 4*i] = (byte) (' ' + (temp % 95));
            temp /= 95;
            keyBytes[4*i] = (byte) (' ' + (temp % 95));
          }       
        key.set(keyBytes, 0, keylen);
    }

    /**
     * Add the rowid to the row.
     * @param rowId
     */
    private void addRowId(long rowId) {
      byte[] rowid = Integer.toString((int) rowId).getBytes();
      int padSpace = 10 - rowid.length;
      if (padSpace > 0) {
        value.append(spaces, 0, 10 - rowid.length);
      }
      value.append(rowid, 0, Math.min(rowid.length, 10));
    }

    /**
     * Add the required filler bytes. Each row consists of 7 blocks of
     * 10 characters and 1 block of 8 characters.
     * @param rowId the current row number
     */
    private void addFiller(long rowId) {
      int base = (int) ((rowId * 8) % 26);

      // Get Random var
      Random random = new Random(rand.seed);
    
      int range = random.nextInt(maxvaluelength - minvaluelength + 1);
      int valuelen = range + minvaluelength;

      while ( valuelen > 10 )
      {
          value.append(filler[(base + valuelen) % 26], 0, 10);
          valuelen -= 10;   
      }
     
      if (valuelen > 0)
    	  value.append(filler[(base + valuelen) % 26], 0, valuelen);
    }

    public void map(LongWritable row, NullWritable ignored,
                    OutputCollector<Text, Mutation> output,
                    Reporter reporter) throws IOException {
      reporter.setStatus("Entering");
      long rowId = row.get();
      if (rand == null) {
        // we use 3 random numbers per a row
        rand = new RandomGenerator(rowId*3);
      }
      addKey();
      value.clear();
      addRowId(rowId);
      addFiller(rowId);
    
      // New
      Mutation m = new Mutation(key);
      m.put(new Text("column"), new Text("columnqual"), new Value(value.toString().getBytes()));
    
      reporter.setStatus("About to add to cloudbase");
      output.collect(table, m);
      reporter.setStatus("Added to cloudbase "+key.toString());
    }

    @Override
    public void configure(JobConf job) {       
        minkeylength = job.getInt("cloudgen.minkeylength", 0);
        maxkeylength = job.getInt("cloudgen.maxkeylength", 0);
        minvaluelength = job.getInt("cloudgen.minvaluelength", 0);
        maxvaluelength = job.getInt("cloudgen.maxvaluelength", 0);
        table = new Text(job.get("cloudgen.tablename"));
    }

    @Override
    public void close() throws IOException { }

  }

  public static void main(String[] args) throws Exception {     
	  int res = ToolRunner.run(new Configuration(), new TeraSortIngest(), args);
	  System.exit(res);
  }

	@Override
	public int run(String[] args) throws Exception {
		//Thread.currentThread().setContextClassLoader(getConf().getClassLoader());
		JobConf job = new JobConf(getConf(), TeraSortIngest.class);
	    job.setJobName("TeraSortCloud");
	
	    job.setMapperClass(SortGenMapper.class);
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(job.getClassByName("cloudbase.core.data.Mutation"));

	
	    job.setNumReduceTasks(0);
	    
	    // Possibly add these classes directly into the jobconf	
	    job.setInputFormat(RangeInputFormat.class);
	    // job.setOutputFormat(CBOutputFormat.class);
	    Class<?> cl = job.getClassByName("cloudbase.core.client.mapreduce.CBOutputFormat");
	    job.setOutputFormat((Class<? extends OutputFormat>) cl) ;
	
	    cl.getMethod("setInstance", job.getClass(), args[6].getClass()).invoke(null, job, args[6]);
	    cl.getMethod("setUsername", job.getClass(), args[7].getClass()).invoke(null, job, args[7]);
	    cl.getMethod("setPassword", job.getClass(), args[8].getBytes().getClass()).invoke(null, job, args[8].getBytes());
	    cl.getMethod("enableCreateTables", job.getClass()).invoke(null, job);

	    // This is how the CBOutputFormat should be used but a hadoop bug prevents them from 
	    // being called correctly
	    
	    // CBOutputFormat.setInstance(job, args[6]);
        // CBOutputFormat.setUsername(job, args[7]);
	    // CBOutputFormat.setPassword(job, args[8].getBytes());
	    // CBOutputFormat.enableCreateTables(job);
	
	    job.setLong(NUMROWS, Long.parseLong(args[0]));
	    job.setInt("cloudgen.minkeylength"  , Integer.parseInt(args[1]));
	    job.setInt("cloudgen.maxkeylength"    , Integer.parseInt(args[2]));
	    job.setInt("cloudgen.minvaluelength", Integer.parseInt(args[3]));
	    job.setInt("cloudgen.maxvaluelength", Integer.parseInt(args[4]));
	    job.set("cloudgen.tablename", args[5]);	  
	    // System.out.println(args.length);
	    if (args.length > 9)
	    	job.setInt(ROWSPERSPLIT, Integer.parseInt(args[9]));
	    
	    JobClient.runJob(job);
		return 0;
	}	
} 