package cloudbase.core.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;

import cloudbase.core.data.Key;
import cloudbase.core.data.MyMapFile;
import cloudbase.core.data.Value;
import cloudbase.core.data.MyMapFile.Writer;
import cloudbase.core.data.MySequenceFile.CompressionType;

public class CreateRandomAssMapFile {
	
	public static byte[] createValue(long rowid, int dataSize){
		Random r = new Random(rowid);
		byte value[] = new byte[dataSize];
		
		r.nextBytes(value);
		
		//transform to printable chars
		for(int j = 0; j < value.length; j++) {
			value[j] = (byte)(( (0xff & value[j]) % 92) + ' ');
		}
		
		return value;
	}
	
	public static void main(String[] args) throws IOException {
		
		String file = args[0];
		int num = Integer.parseInt(args[1]);
		
		long rands[] = new long[num]; 
		
		Random r = new Random();
		
		for (int i = 0; i < rands.length; i++) {
			rands[i] = Math.abs(r.nextLong()) % 10000000000l;
		}
		
		Arrays.sort(rands);
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Writer mfw = new MyMapFile.Writer(conf, fs, file, Key.class, Value.class, CompressionType.BLOCK);
		
		for (int i = 0; i < rands.length; i++) {
			Text row = new Text(String.format("row_%010d", rands[i]));
			Key key = new Key(row);
			
			Value dv = new Value(createValue(rands[i], 40));
			
			mfw.append(key, dv);
		}
		
		mfw.close();
	}

}
