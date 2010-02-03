package cloudbase.core.security;

public enum SystemPermission
{
	// One can add new permissions, with new numbers, but please don't change or use numbers previously assigned
	GRANT(0),
	CREATE_TABLE(1),
	DROP_TABLE(2),
	ALTER_TABLE(3),
	CREATE_USER(4),
	DROP_USER(5),
	ALTER_USER(6),
	SYSTEM(7);
	
	private int permID;

	private SystemPermission(int id)
	{ this.permID = id; }
	
	public int getId()
	{ return this.permID; }
	
	public static SystemPermission getPermissionById(int id)
	{
		for (SystemPermission p : SystemPermission.values())
			if (p.permID == id)
				return p;
		throw new IndexOutOfBoundsException("No such system permission");
	}
}
