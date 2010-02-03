package cloudbase.core.client.impl;

import java.util.HashMap;

import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Instance;
import cloudbase.core.client.MultiTableBatchWriter;
import cloudbase.core.client.MutationsRejectedException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.data.Mutation;
import cloudbase.core.security.thrift.AuthInfo;

public class MultiTableBatchWriterImpl implements MultiTableBatchWriter {

	private class TableBatchWriter implements BatchWriter {

		private String table;

		TableBatchWriter(String table) {
			this.table = table;
		}
		
		@Override
		public void addMutation(Mutation m) {
			bw.addMutation(table, m);
		}

		@Override
		public void addMutations(Iterable<Mutation> iterable) {
			bw.addMutation(table, iterable.iterator());
		}

		@Override
		public void close() {
			throw new UnsupportedOperationException("Must close all tables, can not close an individual table");
		}


		@Override
		public void flush() {
			throw new UnsupportedOperationException("Must flush all tables, can not flush an individual table");
		}
		
	}
	
	private TabletServerBatchWriter bw;
	private HashMap<String, BatchWriter> tableWriters;
    private Instance instance;
    private AuthInfo credentials;

	public MultiTableBatchWriterImpl(Instance instance, AuthInfo credentials, long maxMemory, int maxLatency, int maxWriteThreads){
	    this.instance = instance;
	    this.credentials = credentials;
		this.bw = new TabletServerBatchWriter(instance, credentials, maxMemory, maxWriteThreads);
		tableWriters = new HashMap<String, BatchWriter>();
	}
	
	public void close() throws MutationsRejectedException {
		bw.close();
	}

	@Override
	public synchronized BatchWriter getBatchWriter(String table)
	throws CBException, CBSecurityException, TableNotFoundException
	{
		BatchWriter tbw = tableWriters.get(table);
		
		Connector conn = new Connector(instance, credentials.user, credentials.password);
		
		if (tbw == null)
		{
			if (!conn.tableOperations().exists(table))
				throw new TableNotFoundException(table);
			
			tbw = new TableBatchWriter(table);
			tableWriters.put(table, tbw);
		}
		
		return tbw;
	}

	@Override
	public void flush() {
		bw.flush();
	}

}
