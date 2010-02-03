package cloudbase.core.tabletserver.log;

import java.io.IOException;

import cloudbase.core.data.Mutation;

class NullLogger extends TabletLog {
	public void log(Mutation arg0) throws IOException {}
	public void minorCompactionFinished(String arg0) throws IOException {}
	public void minorCompactionStarted(String arg0) throws IOException {}
	public void close() {}
	public void open(MutationReceiver mr) throws IOException {}
}