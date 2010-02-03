package cloudbase.examples.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cloudbase.core.constraints.Constraint;
import cloudbase.core.data.ColumnUpdate;
import cloudbase.core.data.Mutation;

public class NumericValueConstraint implements Constraint {

	private static final short NON_NUMERIC_VALUE = 1;
	
	private boolean isNumeric(byte bytes[]){
		for (byte b : bytes) {
			boolean ok = (b >= '0' && b <= '9');
			if(!ok) return false;
		}
		
		return true;
	}
	
	private List<Short> addViolation(List<Short> violations, short violation) {
		if(violations == null){
			violations = new ArrayList<Short>();
			violations.add(violation);
		}else if(!violations.contains(violation)){
			violations.add(violation);
		}
		return violations;
	}
	
	@Override
	public List<Short> check(Mutation mutation) {
		List<Short> violations = null;
		
		Collection<ColumnUpdate> updates = mutation.getUpdates();
		
		for (ColumnUpdate columnUpdate : updates) {
			if(!isNumeric(columnUpdate.getValue()))
				violations = addViolation(violations, NON_NUMERIC_VALUE);
		}
		
		return violations;
	}

	@Override
	public String getViolationDescription(short violationCode) {
		
		switch(violationCode){
		case NON_NUMERIC_VALUE:
			return "Value is not numeric";
		}
		
		return null;
	}


}
