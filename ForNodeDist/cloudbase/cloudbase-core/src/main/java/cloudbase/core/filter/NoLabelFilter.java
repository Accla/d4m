package cloudbase.core.filter;

import java.util.Map;

import cloudbase.core.data.Key;
import cloudbase.core.data.Value;
import cloudbase.core.security.LabelExpression;
import cloudbase.core.tabletserver.iterators.OptionDescriber;

public class NoLabelFilter implements Filter, OptionDescriber {

		@Override
		public boolean accept(Key k, Value v) {
			LabelExpression label = new LabelExpression(k.getColumnVisibility());
			return !label.hasNext();

		}
		
		@Override
		public void init(Map<String, String> options) {
			// No Options to set
		}
		
		@Override
		public IteratorOptions describeOptions() {
			return new IteratorOptions("nolabel","NoLabelFilter hides entries without a classification", null, null);
		}

		@Override
		public boolean validateOptions(Map<String, String> options) { return true; }
}


