package cloudbase.examples.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import cloudbase.core.client.mapreduce.CBOutputFormat;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Value;

/**
 * A simple map reduce job that inserts word counts
 * into cloudbase.  See the README for instructions
 * on how to run this.
 * 
 *
 */

public class WordCount extends Configured implements Tool {

	public static class MapClass extends MapReduceBase
	implements Mapper<LongWritable, Text, Text, Mutation> {

		public void map(LongWritable key, Text value, 
				OutputCollector<Text, Mutation> output, 
				Reporter reporter) throws IOException {

			String[] words = value.toString().split("\\s+");

			for (String word : words) {
				
				Mutation mutation = new Mutation(new Text(word));
				mutation.put(new Text("count"), new Text("20080906"), new Value("1".getBytes()));

				output.collect(null, mutation);
			}

		}
	}


	public int run(String[] args) throws Exception {
		JobConf conf = new JobConf(getConf(), WordCount.class);
		conf.setJobName("word count example");
		
		if (args.length != 3) {
			System.out.println("ERROR: Wrong number of parameters: " +
					args.length + " instead of 3.");
			return printUsage();
		}
		
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Mutation.class);

		conf.setMapperClass(MapClass.class);   

		conf.setNumReduceTasks(0);
		
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(CBOutputFormat.class);
		
		CBOutputFormat.setInstance(conf, args[0]);
		FileInputFormat.setInputPaths(conf,new Path(args[1]));
		CBOutputFormat.setDefaultTableName(conf, args[2]);
		CBOutputFormat.setUsername(conf, "root");
		CBOutputFormat.setPassword(conf, "secret".getBytes());

		JobClient.runJob(conf);


		return 0;
	}


	private int printUsage() {
		System.out.println("wordCount [-m <#maps>] <master> <input dir> <output table>  ");
		return 0;
	}


	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new WordCount(), args);
		System.exit(res);
	}
	
}


