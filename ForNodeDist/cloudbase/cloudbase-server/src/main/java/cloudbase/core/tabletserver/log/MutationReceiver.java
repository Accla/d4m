package cloudbase.core.tabletserver.log;

import cloudbase.core.data.Mutation;

public interface MutationReceiver {
	void receive(Mutation m);
}
