package cloudbase.core.client;

public class TableExistsException extends Exception
{
	/**
	 * Exception to throw if an operation is attempted on a table that already exists.
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TableExistsException(String table)
	{
		super("Table "+table+" exists");
	}

	public TableExistsException(String table, Throwable e)
	{
		super("Table "+table+" exists", e);
	}
}
