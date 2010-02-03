package cloudbase.core.util;
import java.io.IOException;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
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
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.IdentityReducer;


public class CheckpointMapReduce extends MapReduceBase implements 
		Mapper<LongWritable,Text,Text,Text>
		{
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JobConf conf = new JobConf(CheckpointMapReduce.class);
		conf.setMapperClass(CheckpointMapReduce.class);
		conf.setReducerClass(IdentityReducer.class);
		conf.setSpeculativeExecution(false);
		FileOutputFormat.setOutputPath(conf, new Path("/cloudbase_checkpoint_foo"));
		
		FileInputFormat.addInputPath(conf, new Path("/cloudbase_checkpoint_file_lists"));
		try {
			JobClient.runJob(conf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	FileSystem fs = null;
	
	public void configure(JobConf conf)
	{
		try {
			fs = FileSystem.get(conf);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		
		String filename = value.toString().substring(10);
		
		Path oldPath = new Path("/cloudbase"+filename);
		Path newPath = new Path("/cloudbase_checkpoint"+filename);
		
		FSDataInputStream oldFile = fs.open(oldPath);
		FSDataOutputStream newFile = fs.create(newPath);
		
		int bytesRead = 0;
		byte [] buffer = new byte[1<<22];
		while((bytesRead = oldFile.read(buffer)) > 0)
		{
			newFile.write(buffer,0,bytesRead);
		}
		oldFile.close();
		newFile.close();
	}

}
