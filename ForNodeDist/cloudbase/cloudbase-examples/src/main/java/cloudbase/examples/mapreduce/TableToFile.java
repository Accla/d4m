package cloudbase.examples.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import cloudbase.core.client.mapreduce.CBInputFormat;
import cloudbase.core.data.Key;
import cloudbase.core.data.Value;

/**
 * Takes a table and outputs the specified column to a set of part files on hdfs
 * hadoop jar cbexamples.jar <tablename> <column> <hdfs-output-path>
 *
 */


public class TableToFile extends Configured implements Tool {	 
	  /**
	   * The Mapper class that given a row number, will generate the appropriate
	   * output line.
	   */
	  public static class TTFMapper implements Mapper<Key, Value, Text, Value>
	  {

		  public void map(Key row, Value data,
	                    OutputCollector<Text, Value> output,
	                    Reporter reporter) throws IOException 
	      {		   			  
		      output.collect(row.getRow(), new Value(data.get()));
		      reporter.setStatus("Outputed Value");
	      }

		  @Override
		  public void configure(JobConf job) {       

		  }

	      @Override
	      public void close() throws IOException { }

	      }

	  // Args
	  // 0 Table Name
	  // 1 Columns
	  // 2 Path to put file.
	  public static void main(String[] args) throws Exception {
		  ToolRunner.run(new Configuration(), new TableToFile(), args);	
		  
	  }

	@Override
	public int run(String[] args) throws Exception {
		JobConf job = new JobConf(TableToFile.class);
	    job.setJobName("TableToFile");
	    
	    job.setJarByClass(TableToFile.class);

	    job.setMapperClass(TTFMapper.class);
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(Value.class);

	    job.setNumReduceTasks(0);

	    job.setInputFormat(CBInputFormat.class);
	    job.setOutputFormat(TextOutputFormat.class);
	    
	    CBInputFormat.setTableName(job, args[0]);
	    CBInputFormat.setColumns(job, args[1]);
	    CBInputFormat.setStartRow(job, "");
	    CBInputFormat.setStopRow(job, "");
	    CBInputFormat.setAuthorizations(job, "");
	    
	    FileOutputFormat.setOutputPath(job, new Path(args[2]));
		    
	    JobClient.runJob(job);

		return 0;
	}


	
}
