package cloudbase.core.filter;

import java.util.Map;

import cloudbase.core.data.Key;
import cloudbase.core.data.Value;
import cloudbase.core.security.CellLevelAuthenticator;

public class VisibilityFilter implements Filter {
	private byte[] authorizations;
	private byte[] defaultSecurityLabels;
	
	public VisibilityFilter(byte[] authorizations, byte[] defaultLabels) {
		this.init(authorizations, defaultLabels);
	}
	
	public void init(byte[] authorizations, byte[] defaultLabels) {
		this.authorizations = authorizations;
		this.defaultSecurityLabels = defaultLabels;
	}
	
	public boolean accept(Key k, Value v) {
		byte[] securityLabels = k.getColumnVisibility();
		if (securityLabels.length == 0) {
			securityLabels = this.defaultSecurityLabels;
		}
		return CellLevelAuthenticator.authorize(authorizations,securityLabels);
	}
	
	@Override
	public void init(Map<String, String> options) {
		// TODO Auto-generated method stub
		
	}
}