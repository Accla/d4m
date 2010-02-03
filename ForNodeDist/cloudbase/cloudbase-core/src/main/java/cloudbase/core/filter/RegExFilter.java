package cloudbase.core.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cloudbase.core.data.Key;
import cloudbase.core.data.Value;
import cloudbase.core.tabletserver.iterators.OptionDescriber;
import cloudbase.core.util.ByteArrayBackedCharSequence;

public class RegExFilter implements Filter, OptionDescriber {
	
	public static final String ROW_REGEX = "rowRegex";
	public static final String COLF_REGEX = "colfRegex";
	public static final String COLQ_REGEX = "colqRegex";
	public static final String VALUE_REGEX = "valueRegex";
	
	private Matcher rowMatcher;
	private Matcher colfMatcher;
	private Matcher colqMatcher;
	private Matcher valueMatcher;
	
	private ByteArrayBackedCharSequence babcs = new ByteArrayBackedCharSequence();
	
	private boolean matches(Matcher matcher, byte data[], int offset, int len){
		if(matcher != null){
			babcs.set(data, offset, len);
			matcher.reset(babcs);
			return matcher.matches();
		}
		
		return true;
	}
	
	@Override
	public boolean accept(Key key, Value value) {
		return matches(rowMatcher, key.getKeyData(), key.getRowOffset(), key.getRowLen()) &&
		matches(colfMatcher, key.getKeyData(), key.getColumnFamilyOffset(), key.getColumnFamilyLen()) &&
		matches(colqMatcher, key.getKeyData(), key.getColumnQualifierOffset(), key.getColumnQualifierLen()) &&
		matches(valueMatcher, value.get(), 0, value.get().length);
	}
	
	@Override
	public void init(Map<String, String> options) {
		if(options.containsKey(ROW_REGEX)){
			rowMatcher = Pattern.compile(options.get(ROW_REGEX)).matcher("");
		}else{
			rowMatcher = null;
		}
		
		if(options.containsKey(COLF_REGEX)){
			colfMatcher = Pattern.compile(options.get(COLF_REGEX)).matcher("");
		}else{
			colfMatcher = null;
		}
		
		if(options.containsKey(COLQ_REGEX)){
			colqMatcher = Pattern.compile(options.get(COLQ_REGEX)).matcher("");
		}else{
			colqMatcher = null;
		}
		
		if(options.containsKey(VALUE_REGEX)){
			valueMatcher = Pattern.compile(options.get(VALUE_REGEX)).matcher("");
		}else{
			valueMatcher = null;
		}
	}
	
	@Override
	public IteratorOptions describeOptions() {
		Map<String, String> options = new HashMap<String, String>();		
		options.put(RegExFilter.ROW_REGEX, "regular expression on row");
		options.put(RegExFilter.COLF_REGEX, "regular expression on column family");
		options.put(RegExFilter.COLQ_REGEX, "regular expression on column qualifier");
		options.put(RegExFilter.VALUE_REGEX, "regular expression on value");
		
		return new IteratorOptions("regex",
				"The RegExFilter/Iterator allows you to filter for key/value pairs based on regular expressions",
				options,
				null);
	}
	
	@Override
	public boolean validateOptions(Map<String, String> options) {
		if(options.containsKey(ROW_REGEX))
			Pattern.compile(options.get(ROW_REGEX)).matcher("");
		
		if(options.containsKey(COLF_REGEX))
			Pattern.compile(options.get(COLF_REGEX)).matcher("");
		
		if(options.containsKey(COLQ_REGEX))
			Pattern.compile(options.get(COLQ_REGEX)).matcher("");
		
		if(options.containsKey(VALUE_REGEX))
			Pattern.compile(options.get(VALUE_REGEX)).matcher("");
		
		return true;
	}
	
}
