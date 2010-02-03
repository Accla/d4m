package cloudbase.core.client;

public class TableNotFoundException extends Exception
{
	/**
	 * Exception to throw if an operation is attempted on a table that doesn't exist.
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TableNotFoundException(String table)
	{
		super("Table "+table+" does not exist");
	}

	public TableNotFoundException(String table, Throwable e)
	{
		super("Table "+table+" does not exist", e);
	}
}
