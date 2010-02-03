package cloudbase.core.master;

public class MasterNotRunningException extends Exception {

	/**
	 * eclipse generated this ... 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	public MasterNotRunningException(String msg) {
		message = new String(msg);
	}

	public String getMessage() {
		return message;
	}

}


