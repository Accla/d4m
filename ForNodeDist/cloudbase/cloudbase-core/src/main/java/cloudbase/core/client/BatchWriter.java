package cloudbase.core.client;

import cloudbase.core.data.Mutation;

/**
 * Send Mutations to a single Table in Cloudbase.
 */
public interface BatchWriter {

	public void addMutation(Mutation m);

	/**
	 * Send any buffered mutations to Cloudbase immediately.
	 */
	public void flush();
	
	/**
	 * Flush and release any resources.
	 * 
	 */
	public void close() throws MutationsRejectedException;

	public void addMutations(Iterable<Mutation> iterable);
}
