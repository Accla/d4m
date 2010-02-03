package cloudbase.core.util;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileInputFormat;

import cloudbase.core.CBConstants;
import cloudbase.core.data.Value;
import cloudbase.core.data.Key;

public class CountRowKeys extends MapReduceBase implements Mapper<Key,Value,Text,NullWritable>, Reducer<Text, NullWritable, Text, Text> {
	public Text k = new Text();

	public enum Count {
		uniqueRows
	}
	
	public void map(Key key, Value value, OutputCollector<Text,NullWritable> output,
			Reporter reporter) throws IOException {
		output.collect(key.getRow(k), NullWritable.get());
	}
	
/*	public static class CountRowKeysCombine extends MapReduceBase implements Reducer<Text,NullWritable,Text,NullWritable> {
		public void reduce(Text key, Iterator<NullWritable> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
			output.collect(key, NullWritable.get());
		}
	}
*/	
	public void reduce(Text key, Iterator<NullWritable> values, OutputCollector<Text,Text> output,
			Reporter reporter) throws IOException {
		reporter.incrCounter(Count.uniqueRows, 1);
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		if(args.length < 2) {
			System.out.println("Usage: CountRowKeys tableName outputPath");
			return;
		}
		
		JobConf conf = new JobConf(CountRowKeys.class);
		FileInputFormat.addInputPath(conf, new Path(CBConstants.TABLES_DIR + "/" + args[0] + "/*/*/data"));
		
        conf.setMapperClass(CountRowKeys.class);
//        conf.setCombinerClass(CountRowKeysCombine.class);
        conf.setReducerClass(CountRowKeys.class);
        
        conf.setCompressMapOutput(true);        
        conf.setMapOutputCompressorClass(GzipCodec.class);

        conf.setMapOutputKeyClass(Text.class);
        conf.setMapOutputValueClass(NullWritable.class);
        
        conf.setInputFormat(SequenceFileInputFormat.class);
        
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));
        
        JobClient.runJob(conf);

	}

}
