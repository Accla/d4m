package cloudbase.core.client.mapreduce.bulk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner;

/**
 * Hadoop partitioner that uses ranges instead of hashing.
 * 
 *
 */

@SuppressWarnings("hiding")
public class RangePartitioner<WritableComparable,Writable> implements Partitioner<WritableComparable,Writable> {

	Text cutPointArray[];
	
	public int getPartition(WritableComparable key, Writable valsetOutputCompressorClassue,
			int numPartitions) {
		
		int index  = Arrays.binarySearch(cutPointArray, (Text)key);
		if(index < 0){
			return (index * -1 ) - 1;
		}else{
			return index;
		}
	}

	public void configure(JobConf job) {
		
		String cutFileName = job.get("cloudbase.core.mapred.bulk.RangePartitioner.cutFile");
		
		
		Path[] cf;
		try {
			cf = DistributedCache.getLocalCacheFiles(job);
			
			ArrayList<Text> cutPoints = new ArrayList<Text>();
			
			if(cf != null){
				for (int i = 0; i < cf.length; i++) {
					if(cf[i].toString().endsWith(cutFileName)){
						BufferedReader in = new BufferedReader(new FileReader(cf[i].toString()));
						String line;
						while((line = in.readLine()) != null){
							cutPoints.add(new Text(line));
						}
						
						in.close();
						
						Collections.sort(cutPoints);
						
						cutPointArray = cutPoints.toArray(new Text[cutPoints.size()]);
					}
				}
			}else{
				throw new RuntimeException("Could not find cut point file "+cf);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} 

	}

	public static void setFileName(String file, JobConf conf) {
		int index = file.lastIndexOf('/');
		if(index >= 0){
			file = file.substring(index+1);
		}
		conf.set("cloudbase.core.mapred.bulk.RangePartitioner.cutFile", file);
		
	}

}
