package cloudbase.core.gc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;

public class SSTableFilter implements PathFilter {
	private Pattern namePat;
	private Matcher nameMatch;
	private int expectedDepth;
	
	@SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(SSTableFilter.class.getName());
	
 	public SSTableFilter(Path cloudbaseDir) {
 		namePat = Pattern.compile("map_[0-9]+_[0-9]+$");
 		nameMatch = namePat.matcher("");
 		expectedDepth = cloudbaseDir.depth() + 5;
 	}
 	
 	private boolean looksLikeTheRootTablet(Path dir) {
 	    return dir.toString().contains(CBConstants.ROOT_TABLET_DIR);
 	}

 	public boolean accept(Path dir) {
		nameMatch.reset(dir.getName());
        if (dir.depth()<expectedDepth)
 			return !looksLikeTheRootTablet(dir);
 		 
        return dir.depth()==expectedDepth && nameMatch.matches() && !looksLikeTheRootTablet(dir);
 	}
}