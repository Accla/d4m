package cloudbase.core.client.impl;

import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Instance;
import cloudbase.core.client.MutationsRejectedException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.data.Mutation;
import cloudbase.core.security.thrift.AuthInfo;

public class BatchWriterImpl implements BatchWriter
{
	
	private String table;
	private TabletServerBatchWriter bw;

	public BatchWriterImpl(Instance instance, AuthInfo credentials, String table, int maxMemory, int maxLatency, int maxWriteThreads)
	throws CBException, CBSecurityException, TableNotFoundException
	{
		Connector conn = new Connector(instance, credentials.user, credentials.password);
		
		if(!conn.tableOperations().exists(table))
			throw new TableNotFoundException(table);

		this.table = table;
		
		this.bw = new TabletServerBatchWriter(instance, credentials, maxMemory, maxWriteThreads);
	}
	
	@Override
	public void addMutation(Mutation m)
	{
		bw.addMutation(table, m);
	}

	@Override
	public void addMutations(Iterable<Mutation> iterable)
	{
		bw.addMutation(table, iterable.iterator());
	}

	@Override
	public void close() throws MutationsRejectedException
	{
		bw.close();
	}

	@Override
	public void flush() {
		bw.flush();
	}

}
