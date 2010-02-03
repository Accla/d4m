package cloudbase.core.security;

public enum TablePermission
{
	// One can add new permissions, with new numbers, but please don't change or use numbers previously assigned
	CREATE_LOCALE(0),
	DROP_LOCALE(1),
	READ(2),
	WRITE(3),
	BULK_IMPORT(4);
	
	private int permID;

	private TablePermission(int id)
	{ this.permID = id; }
	
	public int getId()
	{ return this.permID; }
	
	public static TablePermission getPermissionById(int id)
	{
		for (TablePermission p : TablePermission.values())
			if (p.permID == id)
				return p;
		throw new IndexOutOfBoundsException("No such table permission");
	}

}
