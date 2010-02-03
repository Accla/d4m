package cloudbase.core.constraints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cloudbase.core.data.ComparableBytes;
import cloudbase.core.data.ConstraintViolationSummary;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Mutation;

public class ConstraintChecker {
	
	private ArrayList<Constraint> constrains;
	private static Logger log = Logger.getLogger(ConstraintChecker.class.getName());
	
	public ConstraintChecker(){
		constrains = new ArrayList<Constraint>();
	}
	
	public void addConstraint(Constraint c){
		constrains.add(c);
	}
	
	public Violations check(KeyExtent extent, Mutation m){
		
		Violations violations = null;
		
		if(!extent.contains(new ComparableBytes(m.getRow()))){
			if(violations == null)
				violations = new Violations();
			
			ConstraintViolationSummary cvs = new ConstraintViolationSummary("cloudbase.core.constraints.SystemConstraints", (short)-1, "Mutation outside of tablet extent", 1);
			violations.add(cvs);
			
			//do not bother with further checks since this mutation does not go with this tablet
			return violations;
		}
		
		for (Constraint constraint : constrains) {
			List<Short> violationCodes = null;
			Throwable throwable = null;
			
			try{
				violationCodes = constraint.check(m);
			}catch (Throwable t){
				throwable = t;
				log.warn("CONSTRAINT FAILED : "+throwable.getMessage(), t);
			}
			
			if(violationCodes != null){
				for (Short vcode : violationCodes) {
					ConstraintViolationSummary cvs = new ConstraintViolationSummary(constraint.getClass().getName(), vcode, constraint.getViolationDescription(vcode), 1);
					if(violations == null)
						violations = new Violations();
					violations.add(cvs);
				}
			}else if(throwable != null){
				//constraint failed in some way, do not allow mutation to pass
				
				short vcode;
				String msg;
				
				if (throwable instanceof NullPointerException) {
					vcode = -1;
					msg = "threw NullPointerException";
				}else if (throwable instanceof ArrayIndexOutOfBoundsException) {
					vcode = -2;
					msg = "threw ArrayIndexOutOfBoundsException";
				}else if (throwable instanceof NumberFormatException){ 
					vcode = -3;
					msg = "threw NumberFormatException";
				}else if (throwable instanceof IOException){
					vcode = -4;
					msg = "threw IOException (or subclass of)";
				}else{
					vcode = -100;
					msg = "threw some Exception";
				}
		
				
				ConstraintViolationSummary cvs = new ConstraintViolationSummary(constraint.getClass().getName(), vcode, "CONSTRAINT FAILED : "+msg, 1);
				if(violations == null)
					violations = new Violations();
				violations.add(cvs);
			}
		}
		
		return violations;
	}
}
