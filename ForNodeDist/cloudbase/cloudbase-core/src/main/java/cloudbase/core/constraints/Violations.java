package cloudbase.core.constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import cloudbase.core.data.ConstraintViolationSummary;

public class Violations {
	
	private static class CVSKey {
		private String className;
		private short vcode;
		
		CVSKey(ConstraintViolationSummary cvs){
			this.className = cvs.constrainClass;
			this.vcode = cvs.violationCode;
		}
		
		
		public int hashCode(){
			return className.hashCode() + vcode;
		}
		
		public boolean equals(Object o){
			CVSKey ocvsk = (CVSKey) o;
			return className.equals(ocvsk.className) && vcode == ocvsk.vcode;
		}
	}
	
	private HashMap<CVSKey, ConstraintViolationSummary> cvsmap;
	
	public Violations(){
		cvsmap = new HashMap<CVSKey, ConstraintViolationSummary>();
	}
	
	private void add(CVSKey cvsk, ConstraintViolationSummary cvs){
		ConstraintViolationSummary existingCvs = cvsmap.get(cvsk);
		
		if(existingCvs == null){
			cvsmap.put(cvsk, cvs);
		}else{
			existingCvs.numberOfViolatingMutations += cvs.numberOfViolatingMutations;
		}
	}
	
	public void add(ConstraintViolationSummary cvs) {
		CVSKey cvsk = new CVSKey(cvs);
		add(cvsk, cvs);
	}

	public void add(Violations violations) {
		Set<Entry<CVSKey, ConstraintViolationSummary>> es = violations.cvsmap.entrySet();
		
		for (Entry<CVSKey, ConstraintViolationSummary> entry : es) {
			add(entry.getKey(), entry.getValue());
		}
		
	}

	public void add(List<ConstraintViolationSummary> cvsList) {
		for (ConstraintViolationSummary constraintViolationSummary : cvsList) {
			add(constraintViolationSummary);
		}
		
	}
	
	public List<ConstraintViolationSummary> asList() {
		return new ArrayList<ConstraintViolationSummary>(cvsmap.values());
	}
	
}
