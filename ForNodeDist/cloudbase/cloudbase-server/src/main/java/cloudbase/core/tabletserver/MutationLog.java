package cloudbase.core.tabletserver;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import cloudbase.core.data.Mutation;

public class MutationLog {
	private FSDataOutputStream logout;
	//private Path logfile;
	
	public static final byte MUTATION_EVENT = 1;
	public static final byte CLOSE_EVENT = 2;
	
	public MutationLog(Path logfile) throws IOException{
		
		//this.logfile = logfile;
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		if(!fs.exists(logfile))
			logout = fs.create(logfile);
	}
	
	public void log(Mutation m) throws IOException
	{
		//write event type
		logout.writeByte(MUTATION_EVENT);
		
		//write event
		m.write(logout);
		logout.flush();
	}
	
	public void close() throws IOException{
		logout.writeByte(CLOSE_EVENT);
		logout.close();
	}
	
	public static Iterator<Mutation> replay(Path logfile, Tablet t, long min_timestamp) throws IOException
	{
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		//TODO buffer input?
		final FSDataInputStream login = fs.open(logfile);
		
		final Mutation mutation = new Mutation();
		
		return new Iterator<Mutation>(){

			byte eventType;
			
			{
				eventType = login.readByte();
			}
			
			public boolean hasNext() {
				return eventType != CLOSE_EVENT;
			}

			public Mutation next() {
				try {
					mutation.readFields(login);
					eventType = login.readByte();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				
				return mutation;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}};
	}
}
