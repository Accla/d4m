package cloudbase.core.tabletserver;

import org.apache.log4j.Level;

@SuppressWarnings("serial")
public class TLevel extends Level {

	public static Level TABLET_HIST = new TLevel();
	
	protected TLevel() {
		super(Level.DEBUG_INT + 100, "TABLET_HIST", Level.DEBUG_INT + 100);
	}

}
