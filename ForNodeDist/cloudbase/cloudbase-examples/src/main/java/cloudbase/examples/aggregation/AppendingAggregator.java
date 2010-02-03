package cloudbase.examples.aggregation;

import java.util.ArrayList;

import cloudbase.core.aggregation.Aggregator;
import cloudbase.core.data.Value;

public class AppendingAggregator implements Aggregator{

	//this is an example that is going for simplicity,
	//but not efficiency... an ArrayList of Byte objects
	//is not the best way to do this...
	ArrayList<Byte> bytes = new ArrayList<Byte>();

	public Value aggregate() {
		byte ba[] = new byte[bytes.size()];
		
		for (int i = 0; i < ba.length; i++) {
			ba[i] = bytes.get(ba.length - (i+1));
		}
		
		return new Value(ba);
	}

	public void collect(Value value) {
		byte[] ba = value.get();
		for (int i = 0; i < ba.length; i++) {
			bytes.add(ba[i]);
		}
	}

	public void reset() {
		bytes.clear();
	}

}
