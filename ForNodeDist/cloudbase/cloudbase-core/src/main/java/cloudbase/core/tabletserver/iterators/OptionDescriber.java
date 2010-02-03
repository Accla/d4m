package cloudbase.core.tabletserver.iterators;

import java.util.List;
import java.util.Map;

public interface OptionDescriber {
	public static class IteratorOptions {
		public Map<String,String> fixedOptions;
		public List<String> optionalOptionDescriptions;
		public String name;
		public String description;
		
		public IteratorOptions(String name, String description, Map<String,String> fixedOptions, List<String> optionalOptionDescriptions) {
			this.name = name;
			this.fixedOptions = fixedOptions;
			this.optionalOptionDescriptions = optionalOptionDescriptions;
			this.description = description;
		}
		
	}
	
	public IteratorOptions describeOptions();
	public boolean validateOptions(Map<String,String> options);
}
