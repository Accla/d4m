package cloudbase.core.client;

import java.util.ArrayList;
import java.util.List;

import cloudbase.core.data.ConstraintViolationSummary;
import cloudbase.core.data.KeyExtent;

/**
 * Communicate the failed mutations of a BatchWriter back to the client.
 *
 */
public class MutationsRejectedException extends Exception {
    private static final long serialVersionUID = 1L;

    private List<ConstraintViolationSummary> cvsl;
	private ArrayList<KeyExtent> af;
	
	public MutationsRejectedException(List<ConstraintViolationSummary> cvsList, ArrayList<KeyExtent> af) {
		super("# constraint violations : "+cvsList.size()+"  # authorization failures : "+af.size());
		this.cvsl = cvsList;
		this.af = af;
	}

	public List<ConstraintViolationSummary> getConstraintViolationSummaries(){
		return cvsl;
	}
	
	public List<KeyExtent> getAuthorizationFailures(){
		return af;
	}
}
