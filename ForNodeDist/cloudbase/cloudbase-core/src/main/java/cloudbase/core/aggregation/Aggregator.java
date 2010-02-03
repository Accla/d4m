package cloudbase.core.aggregation;

import cloudbase.core.data.Value;

public interface  Aggregator {
    void reset();
    void collect(Value value);
    Value aggregate();
}
