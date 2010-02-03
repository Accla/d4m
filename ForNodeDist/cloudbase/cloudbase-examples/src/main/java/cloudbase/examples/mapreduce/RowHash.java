package cloudbase.examples.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.MD5Hash;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import cloudbase.core.data.Key;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Value;


public class RowHash extends Configured implements Tool {	 
	  /**
	   * The Mapper class that given a row number, will generate the appropriate
	   * output line.
	   */
	  public static class HashDataMapper implements Mapper<Key, Value, Text, Mutation>
	  {
		  String sinkTable;
		  public void map(Key row, Value data,
	                    OutputCollector<Text, Mutation> output,
	                    Reporter reporter) throws IOException 
	      {			  
		      MD5Hash md5 = new MD5Hash();
		      md5 = MD5Hash.digest(data.toString());
		      Mutation m = new Mutation(row.getRow());
		      m.put(new Text("column"), new Text("columnqual"), new Value(md5.getDigest()));
		      Text table = new Text(sinkTable);
		      output.collect(table, m);
		      reporter.setStatus("Added value to table " + sinkTable);
	      }

		  @Override
		  public void configure(JobConf job) {       
			  sinkTable = job.get("rowhash.sinktable");
		  }

	      @Override
	      public void close() throws IOException { }

	      }

	  // Args
	  // 0 Instance
	  // 1 Username
	  // 2 Password
	  // 3 Table to use as input
	  // 4 Comma Separated list of columns
	  // 5 Table to use as output
	  // 
	  public static void main(String[] args) throws Exception {
		  ToolRunner.run(new Configuration(), new RowHash(), args);		
	  }

	@Override
	public int run(String[] args) throws Exception {
		JobConf job = new JobConf(getConf(), RowHash.class);
	    job.setJobName("CloudRowHash");

	    job.setMapperClass(HashDataMapper.class);
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(job.getClassByName("cloudbase.core.data.Mutation"));

	    job.setNumReduceTasks(0);

	    Class<?> cbi = job.getClassByName("cloudbase.core.client.mapreduce.CBInputFormat");
	    job.setInputFormat((Class<? extends InputFormat>) cbi);
	
	    Class<?> cbo = job.getClassByName("cloudbase.core.client.mapreduce.CBOutputFormat");
	    job.setOutputFormat((Class<? extends OutputFormat>) cbo);

	    cbo.getMethod("setInstance", job.getClass(), args[0].getClass()).invoke(null, job, args[0]);
	    cbo.getMethod("setUsername", job.getClass(), args[1].getClass()).invoke(null, job, args[1]);
	    cbo.getMethod("setPassword", job.getClass(), args[2].getBytes().getClass()).invoke(null, job, args[2].getBytes());
	    cbo.getMethod("enableCreateTables", job.getClass()).invoke(null, job);

//	    This following lines should work but there's a bug in the hadoop classloader where it seems you aren't
//	    able to call static methods of classes you've included
	    
//	    CBOutputFormat.setInstance(job, args[0]);
//	    CBOutputFormat.setUsername(job, args[1]);
//	    CBOutputFormat.setPassword(job, args[2].getBytes());
//	    CBOutputFormat.enableCreateTables(job);
	    
	    String empty = "";	  
	    cbi.getMethod("setInstance", Configuration.class, args[0].getClass()).invoke(null, job, args[0]);
	    cbi.getMethod("setUsername", Configuration.class, args[1].getClass()).invoke(null, job, args[1]);
	    cbi.getMethod("setPassword", Configuration.class, args[2].getBytes().getClass()).invoke(null, job, args[2].getBytes());
	    cbi.getMethod("setTableName", Configuration.class, args[3].getClass()).invoke(null, job, args[3]);
	    cbi.getMethod("setColumns", Configuration.class, args[4].getClass()).invoke(null, job, args[4]);
	    cbi.getMethod("setStartRow", Configuration.class, empty.getClass()).invoke(null, job, empty);
	    cbi.getMethod("setStopRow", Configuration.class, empty.getClass()).invoke(null, job, empty);
	    cbi.getMethod("setAuthorizations", Configuration.class, empty.getClass()).invoke(null, job, empty);
//	    cbi.getMethod("enableCreateTables", Configuration.class).invoke(null, job);
	    cbi.getMethod("setMaxMaps", Configuration.class, Integer.TYPE).invoke(null, job, Integer.parseInt(args[6]));
	    
//	    Same as above
	    
//	    CBInputFormat.setTableName(job, args[3]);
//	    CBInputFormat.setColumns(job, args[4]);
//	    CBInputFormat.setStartRow(job, "");
//	    CBInputFormat.setStopRow(job, "");
//	    CBInputFormat.setAuthorizations(job, "");
//	    CBInputFormat.setMaxMaps(job, Integer.parseInt(args[6]));
	    
	    job.set("rowhash.sinktable", args[5]);
	    
	    JobClient.runJob(job);

		return 0;
	}


	
}
