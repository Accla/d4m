package cloudbase.core.util;

import java.util.Iterator;
import java.util.Random;

import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.MutationsRejectedException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Value;

public class RandomWriter {
	
	private static String table_name = "test_write_table";
	private static int num_columns_per_row = 1;
	private static int num_payload_bytes = 1024;
	private static Logger log = Logger.getLogger(RandomWriter.class.getName());
	
	public static class RandomMutationGenerator implements Iterable<Mutation>, Iterator<Mutation>
	{
		private long max_mutations;
		private int mutations_so_far = 0;
		private Random r = new Random();
		private static Logger log = Logger.getLogger(RandomMutationGenerator.class.getName());

		public RandomMutationGenerator(long num_mutations)
		{
			max_mutations = num_mutations;
		}


		public boolean hasNext() {
			// TODO Auto-generated method stub
			return mutations_so_far < max_mutations;
		}

		public Mutation next() {
			// TODO Auto-generated method stub
			Text row_value = new Text(Long.toString((Math.abs(r.nextLong())/177)%100000000000l));
			Mutation m = new Mutation(row_value);
			for(int column = 0; column < num_columns_per_row; column++)
			{
				Text column_fam = new Text("col_fam");
				byte [] bytes = new byte[num_payload_bytes];
				r.nextBytes(bytes);
				m.put(column_fam, new Text(""+column), new Value(bytes));
			}
			mutations_so_far++;
			if(mutations_so_far %1000000 == 0)
			{
				log.info("Created "+mutations_so_far+" mutations so far");
			}
			return m;
		}

		public void remove() {
			// TODO Auto-generated method stub
			mutations_so_far++;
		}


		@Override
		public Iterator<Mutation> iterator() {
			return this;
		}
	}
	
	/**
	 * @param args
	 * @throws CBException 
	 * @throws CBSecurityException 
	 * @throws TableNotFoundException 
	 */
	public static void main(String[] args)
	throws MutationsRejectedException, CBException, CBSecurityException, TableNotFoundException
	{
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();
		if (args.length==2) {
			log.info("Insufficient paratmers, needs username and password");
			return;
		}
		log.info("starting at " + start + " for user " + args[0]);
		try {
		    Connector connector = new Connector(new HdfsZooInstance(), args[0], args[1].getBytes());
			BatchWriter bw = connector.createBatchWriter(table_name, 50000000, 10 * 60, 10);
			long num_mutations = Long.parseLong(args[0]);
			log.info("Writing "+num_mutations+" mutations...");
			bw.addMutations(new RandomMutationGenerator(num_mutations));
			bw.close();
		} catch (MutationsRejectedException e) {
			log.error(e);
			throw e;
		} catch (CBException e) {
			log.error(e);
			throw e;
		} catch (CBSecurityException e) {
			log.error(e);
			throw e;
		} catch (TableNotFoundException e) {
			log.error(e);
			throw e;
		} 
		long stop = System.currentTimeMillis();
		
		log.info("stopping at " + stop);
		log.info("elapsed: " + (((double)stop - (double)start)/1000.0));
	}

}
