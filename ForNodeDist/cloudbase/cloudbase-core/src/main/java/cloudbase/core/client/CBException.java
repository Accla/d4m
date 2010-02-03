package cloudbase.core.client;

public class CBException extends Exception {

	/**
	 * A generic Cloudbase Exception for general cloudbase failures.
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CBException(String why)
	{ super(why); }

	public CBException(Throwable cause)
	{ super(cause); }

	public CBException(String why, Throwable cause)
	{ super(why, cause); }

}
