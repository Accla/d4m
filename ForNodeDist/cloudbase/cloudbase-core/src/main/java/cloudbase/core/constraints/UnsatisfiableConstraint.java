package cloudbase.core.constraints;

import java.util.Collections;
import java.util.List;

import cloudbase.core.data.Mutation;

public class UnsatisfiableConstraint implements Constraint {

	private List<Short> violations;
	private String vDesc;
	
	public UnsatisfiableConstraint(short vcode, String violationDescription){
		this.violations = Collections.unmodifiableList(Collections.singletonList((short)vcode));
		this.vDesc = violationDescription;
	}
	
	public List<Short> check(Mutation mutation) {
		return violations;
	}

	public String getViolationDescription(short violationCode) {
		return vDesc;
	}

}
