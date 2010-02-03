package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import cloudbase.core.data.Column;
import cloudbase.core.data.Value;
import cloudbase.core.data.Key;
import cloudbase.core.filter.ColumnFilter;
import cloudbase.core.filter.VisibilityFilter;

public class SystemScanIterator extends FilteringIterator {
	public SystemScanIterator(
			SortedKeyValueIterator<Key, Value> iterator,
			byte[] authorizations, byte[] defaultLabels, HashSet<Column> hsc) throws IOException {
		super(iterator, Arrays.asList(new ColumnFilter(hsc), new VisibilityFilter(authorizations, defaultLabels)));
	}

}
