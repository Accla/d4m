package cloudbase.core.client.mapreduce;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.RecordReader;

import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Instance;
import cloudbase.core.client.Scanner;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.data.Column;
import cloudbase.core.data.Key;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;

/**
 * This thing reads an input split (range of rows) from cloudbase
 * It tries to use only one instance of a Table object per JVM
 * to share metadata cache across map()s and reduce()s
 * 
 *
 */
public class CBRecordReader implements RecordReader<Key, Value>{

	private Scanner scanner;
	private int recordsRead;
	private Iterator<Entry<Key, Value>> scannerIterator;
	
	public static final Log LOG = LogFactory.getLog("org.apache.hadoop.mapred.CBInputFormat");
	
	public CBRecordReader(CBInputSplit split, Instance instance, String user, byte[] password, Set<Short> authorizations)
	throws CBException, CBSecurityException, TableNotFoundException
	{
		Connector conn = new Connector(instance, user, password);
		
		// setup a scanner within the bounds of this split
		scanner = conn.createScanner(split.getTableName(), authorizations);
		
		for (Column c : split.getColumns())
			scanner.fetchColumn(new Text(c.columnFamily), new Text(c.columnQualifier));
		
		Key startKey = (split.getStartRow().getBytes().length > 0) ? startKey = new Key(split.getStartRow()) : new Key();
		Key stopKey = (split.getStopRow().getBytes().length > 0) ? new Key(split.getStopRow()) : null;
		
		scanner.setRange(new Range(startKey, stopKey));
		
		recordsRead = 0;
		
		//do this last after setting all scanner options
		scannerIterator = scanner.iterator();
	}
	
	public void close() {
		
	}

	public Key createKey() {
		return new Key();
	}

	public Value createValue() {
		return new Value();
	}

	public long getPos() throws IOException {
		return recordsRead;
	}

	public float getProgress() throws IOException {
		// TODO will having no progress number be a problem?
		return ((float)recordsRead) / 10.0f;
	}

	/**
	 * The meat of the record reader
	 */
	public boolean next(Key key, Value value) throws IOException {
		if(scannerIterator.hasNext())
		{
			Entry<Key, Value> entry = scannerIterator.next();
			key.set(entry.getKey());
			value.set(entry.getValue().get());
			return true;
		}
		return false;
	}
}
