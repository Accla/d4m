package cloudbase.core.client;

public class CBSecurityException extends Exception
{
	/**
	 * A generic Cloudbase Exception for cloudbase security failures.
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String user;
	public boolean baduserpass;
	
	public CBSecurityException(String user, boolean badUserCreds)
	{
		this.user = user;
		this.baduserpass = badUserCreds;
	}	
	
	public CBSecurityException(String user, boolean badUserCreds, Throwable cause)
	{ 
		super(cause);
		this.user = user;
		this.baduserpass = badUserCreds;
	}
	
	public String toString()
	{
		return (this.baduserpass ? "Bad username/password combination for user " : "Permission denied for user ")+user;
	}
}
