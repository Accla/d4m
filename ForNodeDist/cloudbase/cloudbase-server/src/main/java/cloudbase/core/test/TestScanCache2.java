package cloudbase.core.test;

import java.io.IOException;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;

import cloudbase.core.data.Value;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.tabletserver.iterators.ScanCache;
import cloudbase.core.tabletserver.iterators.SortedMapIterator;

public class TestScanCache2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SortedMap<Key, Value> m = new TreeMap<Key, Value>();
		
		Random r = new Random();
		final byte[] security = new byte[0];
		final Text columnf = new Text("colfam");
		final Text columnq = new Text("colqual");
		for(int i = 2; i <= 2000000; i += 2)
		{
			final Text row = new Text(String.format("row_%06d",i));
			final Text value = new Text("value"+i);
			final Key key = new Key(row,columnf,columnq,security,System.currentTimeMillis());
			final byte[] valBytes = value.getBytes();
			final Value dibw = new Value(valBytes);
			m.put(key,dibw);
		}
		
		System.out.println("created map of size "+m.size());
		
		SortedMapIterator<Key, Value> smi = new SortedMapIterator<Key, Value>(m);
		
		KeyExtent extent = new KeyExtent(new Text("tablename"),null,null);
		
		ScanCache sc = new ScanCache(10000000,extent);

		// test correctness of the scan cache
		
		for(int i = 1; i <= 100000; i++)
		{
			sc.setAuthorityIterator(smi);
			int startRow = Math.abs(r.nextInt() % 2000000);
			boolean skipStartRow = false;//r.nextBoolean();
			if(i % 7 == 0)
			{
				Text row = new Text(String.format("row_%06d", startRow+1));
				Key k = new Key(row, columnf, columnq, security,System.currentTimeMillis());
				final byte[] valBytes = new Text("added value "+k.toString()).getBytes();
				final Value dibw = new Value(valBytes);
				m.put(k, dibw);
				sc.invalidate(k);
				System.out.println("Added row "+k+", "+dibw);
			}
			if(skipStartRow)
			{
				System.out.println("Scanning after "+startRow);
			}
			else
			{
				System.out.println("Scanning from "+startRow);
			}
			Key startKey = new Key(new Text(String.format("row_%06d",startRow)), columnf, columnq, Long.MAX_VALUE);
			sc.seek(startKey);
			for(int j = 0; j < 20000; j++)
			{
				// scan through the cache
				if(!sc.hasTop())
				{
					System.out.println("end of cache reached with key="+startKey+" and j="+j);
					break;
				}
				else
				{
					if(j == 0)
						System.out.println("first key: "+sc.getTopKey()+"  value: "+sc.getTopValue());
					try {
						sc.next();
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
				}
			}
			if(sc.hasTop())
			{
				System.out.println("last key: "+sc.getTopKey()+"  value: "+sc.getTopValue());
			}
			sc.finishScan();
		}
		
		System.out.println("now scanning from the beginning of the table");
		Text firstRow = new Text("");//new Text(String.format("row_%06d", 0));
		Key firstKey = new Key(firstRow,columnf,columnq,Long.MAX_VALUE);
		// test scanning from the beginning of the table
		System.out.println("smi has "+m.size()+" entries");
		for(int i = 0; i < 1000; i++)
		{
			System.out.println("Scan "+(i+1));
			sc.setAuthorityIterator(smi);
			sc.seek(firstKey);
			Key lastKey = null;
			for(int j = 0; j < (i+1) * 1000 && j < 100000; j++)
			{
				if(!sc.hasTop())
				{
					System.out.println("finished at row "+j);
					break;
				}
				if(j == 0)
				{
					System.out.println("first key: "+sc.getTopKey());
				}
				lastKey = sc.getTopKey();
				try {
					sc.next();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			}
			System.out.println("last key: "+lastKey);
			sc.finishScan();
		}
	}

}
