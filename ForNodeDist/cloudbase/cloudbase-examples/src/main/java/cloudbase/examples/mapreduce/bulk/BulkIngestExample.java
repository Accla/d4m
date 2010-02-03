package cloudbase.examples.mapreduce.bulk;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import cloudbase.core.client.Instance;
import cloudbase.core.client.MasterInstance;
import cloudbase.core.client.mapreduce.bulk.BulkOperations;
import cloudbase.core.client.mapreduce.bulk.RangePartitioner;
import cloudbase.core.data.Key;
import cloudbase.core.data.Value;
import cloudbase.core.security.thrift.AuthInfo;

/**
 * Example map reduce job that bulk ingest data into a
 * cloudbase table.  The expected input is text files
 * containing tab separated key value pairs on each line.
 * 
 *
 */

public class BulkIngestExample extends Configured implements Tool {

	public static class MapClass extends MapReduceBase
	    implements Mapper<LongWritable, Text, Text, Text> {
	 	     
		  Text outputKey = new Text();
		  Text outputValue = new Text();
		  
	    public void map(LongWritable key, Text value, 
	                    OutputCollector<Text, Text> output, 
	                    Reporter reporter) throws IOException {
	    	
	    	//split on tab
	    	int index = -1;
	    	for(int i = 0; i < value.getLength(); i++){
	    		if(value.getBytes()[i] == '\t'){
	    			index = i;
	    			break;
	    		}
	    	}
	    	
	    	if(index > 0){
	    		outputKey.set(value.getBytes(), 0, index);
	    		outputValue.set(value.getBytes(), index+1, value.getLength() - (index + 1));
	    		output.collect(outputKey, outputValue);
	    	}
	    }
	  }
	  
	  public static class ReduceClass extends MapReduceBase
	  	implements Reducer<Text, Text, Key, Value>{

		public void reduce(Text key, Iterator<Text> values,
				OutputCollector<Key, Value> output,
				Reporter reporter) throws IOException {
			
			//be careful with the timestamp... if you run on a cluster
			//where the time is whacked you may not see your updates in
			//cloudbase if there is already an existing value with a later
			//timestamp in cloudbase... so make sure ntp is running on the
			//cluster or consider using logical time
			long timestamp = System.currentTimeMillis();
			
			int index = 0;
			while(values.hasNext()) {
				Text value = values.next();
				Key outputKey = new Key(key, new Text("foo"), new Text(""+index), timestamp);
				index++;
				
				//cloudbase stores DeletableImmutableBytesWritable objects
				//in its map files instead of ImmutableBytesWritable because
				//it needs to store the additional information about whether
				//something was deleted... 
				Value outputValue = 
					new Value(value.getBytes(), 0, value.getLength());
				
				output.collect(outputKey, outputValue);
			}
			
		}
		  
	  }
	  
	  
	public int run(String[] args) throws Exception {
		   JobConf conf = new JobConf(getConf(), BulkIngestExample.class);
		    conf.setJobName("bulk ingest example");

		    conf.setMapOutputKeyClass(Text.class);
		    conf.setMapOutputValueClass(Text.class);
		    
		    conf.setOutputKeyClass(conf.getClassByName("cloudbase.core.data.Key"));
		    conf.setOutputValueClass(conf.getClassByName("cloudbase.core.data.Value"));

		    conf.setMapperClass(MapClass.class);   
		    conf.setReducerClass(ReduceClass.class);
		    
		    conf.setCompressMapOutput(true);
		    //conf.setMapOutputCompressionType(CompressionType.BLOCK);
		    
		    //conf.setOutputKeyComparatorClass(Text.Comparator.class);
		    
		    List<String> other_args = new ArrayList<String>();
		    for(int i=0; i < args.length; ++i) {
		      try {
		        if ("-m".equals(args[i])) {
		          conf.setNumMapTasks(Integer.parseInt(args[++i])); 
		        }else {
		          other_args.add(args[i]);
		        }
		      } catch (NumberFormatException except) {
		        System.out.println("ERROR: Integer expected instead of " + args[i]);
		        return printUsage();
		      } catch (ArrayIndexOutOfBoundsException except) {
		        System.out.println("ERROR: Required parameter missing from " +
		                           args[i-1]);
		        return printUsage();
		      }
		    }
	
		    if (other_args.size() != 6) {
		      System.out.println("ERROR: Wrong number of parameters: " +
		                         other_args.size() + " instead of 6.");
		      return printUsage();
		    }
		    
		    Instance instance = new MasterInstance(other_args.get(0));
		    String user = other_args.get(1);
		    byte[] pass = other_args.get(2).getBytes();
		    String tableName = other_args.get(3);
		    String inputDir = other_args.get(4);
		    String workDir = other_args.get(5);
		    
		    AuthInfo credentials = new AuthInfo(user, pass);
		    
		    FileInputFormat.setInputPaths(conf,new Path(inputDir));
		    FileOutputFormat.setOutputPath(conf,new Path(workDir+"/mapFiles"));
		    
		    FileSystem fs = FileSystem.get(conf);
		    PrintStream out = new PrintStream(
		    		new BufferedOutputStream(
		    				fs.create(new Path(workDir+"/splits.txt"))));
		    
		    Collection<Text> splits = BulkOperations.getSplits(instance, credentials, tableName, 100);
		    for (Text split : splits)
				out.println(split);
		    
		    conf.setNumReduceTasks(splits.size() + 1);
		    
		    out.close();
		    
		    conf.setPartitionerClass(RangePartitioner.class);
		    RangePartitioner.setFileName(workDir+"/splits.txt", conf);
		    DistributedCache.addCacheFile(new URI(workDir+"/splits.txt"), conf);

		    conf.setOutputFormat((Class<? extends OutputFormat>) conf.getClassByName("cloudbase.core.client.mapreduce.CBMapFileOutputFormat"));
		    
		    // MapFileOutputFormat.setCompressOutput(conf, true);
		    //SequenceFileOutputFormat.setOutputCompressionType(conf, CompressionType.BLOCK);
		    //MapFileOutputFormat.setOutputCompressorClass(conf, GzipCodec.class);
		    
		    JobClient.runJob(conf);
		    
		    BulkOperations.bringMapFilesOnline(instance,
		    		credentials,
		    		tableName, 
		    		new Path(workDir+"/mapFiles"), 
		    		new Path(workDir+"/failures"),
		    		20,
		    		4,
		    		false,
		    		false);
		    
		    return 0;
	}
	
	
	 private int printUsage() {
		System.out.println("bulkIngestExample [-m <#maps>] <master> <username> <password> <table> <input dir> <work dir> ");
		return 0;
	}


	public static void main(String[] args) throws Exception {
		 int res = ToolRunner.run(new Configuration(), new BulkIngestExample(), args);
		 System.exit(res);
	 }
}
