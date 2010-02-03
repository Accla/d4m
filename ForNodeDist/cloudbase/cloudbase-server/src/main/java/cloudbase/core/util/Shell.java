package cloudbase.core.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleReader;
import jline.History;
import jline.SimpleCompletor;

import org.apache.hadoop.io.Text;

import cloudbase.core.CBConstants;
import cloudbase.core.aggregation.Aggregator;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Instance;
import cloudbase.core.client.MasterInstance;
import cloudbase.core.client.Scanner;
import cloudbase.core.client.TableExistsException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.client.impl.Writer;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.ConstraintViolationSummary;
import cloudbase.core.data.Key;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;
import cloudbase.core.filter.AgeOffFilter;
import cloudbase.core.filter.RegExFilter;
import cloudbase.core.security.Authenticator;
import cloudbase.core.security.LabelConversions;
import cloudbase.core.security.LabelExpression;
import cloudbase.core.security.LabelExpressionFormatException;
import cloudbase.core.security.SystemPermission;
import cloudbase.core.security.TablePermission;
import cloudbase.core.security.ZKAuthenticator;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.tabletserver.iterators.AggregatingIterator;
import cloudbase.core.tabletserver.iterators.FilteringIterator;
import cloudbase.core.tabletserver.iterators.IteratorUtil;
import cloudbase.core.tabletserver.iterators.NoLabelIterator;
import cloudbase.core.tabletserver.iterators.OptionDescriber;
import cloudbase.core.tabletserver.iterators.RegExIterator;
import cloudbase.core.tabletserver.iterators.VersioningIterator;
import cloudbase.core.tabletserver.iterators.IteratorUtil.IteratorScope;
import cloudbase.core.tabletserver.iterators.OptionDescriber.IteratorOptions;
import cloudbase.core.tabletserver.thrift.ConstraintViolationException;


public class Shell {
    
    private final String COMMAND_SUFFIX = "Command";
    
    private Text tableName;
    private Scanner scanner;
    private Connector connector;
    private String prompt;
    private ConsoleReader reader;
    private Instance instance;
    private AuthInfo credentials;
    
    public static void main(String[] args)
    throws IOException
    {
    	if (args.length == 1)
    		new Shell(new HdfsZooInstance(), args[0]).start();
    	else if (args.length == 3 && args[0].equals("-m"))
    		new Shell(new MasterInstance(args[1]), args[2]).start();
    	else
    		System.err.println("Usage: Shell [-m <master>] <username>");
 	}
 	
    private Shell(Instance instance, String user)
    throws IOException
    {
    	this.tableName = new Text("");
		this.reader = new ConsoleReader();
    	this.instance = instance;
    	byte[] pass = reader.readLine("Enter current password for '"+user+"': ", '*').getBytes();
    	this.credentials = new AuthInfo(user, pass);
    	try {
			connector = new Connector(instance, user, pass);
		} catch (CBException e) {
			System.err.println(e);
			throw new IOException(e);
		} catch (CBSecurityException e) {
			System.err.println(e);
			throw new IOException(e);
		}
    }

 	public void start()
 	throws IOException
 	{
 	    String input;
		System.out.println("CBShell " + CBConstants.VERSION + " - type 'help' for a list of available commands");
		History history = new History();
		File cbDir = new File(System.getenv("HOME")+"/.cloudbase/");
		cbDir.mkdirs();
		history.setHistoryFile(new File(System.getenv("HOME")+"/.cloudbase/shell_history.txt" ));
		reader.setHistory(history);
		
		String[] commands = getCommandListFromMethodNames();

		while(true)
		{
			Set<String> tablenames;
			try {
				tablenames = connector.tableOperations().list();
			} catch (CBException e) {
				tablenames = Collections.emptySet();
			} catch (CBSecurityException e) {
				tablenames = Collections.emptySet();
			}
			ArgumentCompletor argcom = new ArgumentCompletor(new Completor[]{ new SimpleCompletor(commands), new SimpleCompletor(tablenames.toArray(new String[0]))});
			reader.addCompletor(argcom);
			
            try {
				scanner=null;
				prompt = tableName+"> ";
				
				input = reader.readLine(prompt);
				if(input == null)
					break;
				
				String fields[] = input.split("\\s+");
				String command = fields[0];
				
                if(command.equals("quit") || command.equals("exit"))
                    return;
                
                if(command.length() > 0)
                {
                    Method method = null;
                    try {
                        method = Shell.class.getMethod(command + COMMAND_SUFFIX, fields.getClass());
                    } catch (NoSuchMethodException ex) {
                        // ignored
                    }
                    
                    if (method != null)
                        method.invoke(this, (Object)fields);
                    else
                        System.err.println("unknown command");
                }

            } catch (IllegalArgumentException e) {
				System.err.println("illegal argument: "+e);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.err.println("illegal access: "+e);
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				System.err.println("invocation target exception: "+e);
				e.printStackTrace();
            }
            reader.removeCompletor(argcom);
		}
	}

    private String[] getCommandListFromMethodNames()
    {
        ArrayList<String> commandsFromMethods = new ArrayList<String>();
		for (Method method : Shell.class.getMethods())
		{
		    String methodName = method.getName();
            if (methodName.endsWith(COMMAND_SUFFIX))
            {
		        String commandName = methodName.substring(0, methodName.length() - COMMAND_SUFFIX.length());
		        commandsFromMethods.add(commandName);
		    }
		}
		String[] commands = commandsFromMethods.toArray(new String[0]);
		Arrays.sort(commands);
        return commands;
    }

    public void userCommand(String[] fields)
    throws IOException
    {
    	final String USAGE = "Incorrect usage: user <list | tablepermissions | systempermissions | authenticate <username>\n"
    						+"      | create <username> (<authorizations>) | drop <username> | passwd <username> | setauths <username> (<authorizations>)\n"
    						+"      | getauths <username> | permissions <username> | grant <username> [<table>] <permission> | revoke <username> [<table>] <permission> >";
    	
    	if (fields.length == 2 && fields[1].equals("list"))
    	{
    		try {
	        	Set<String> userlist = connector.securityOperations().listUsers();
	        	for (String user : userlist)
	        		System.out.println("        "+user);
			} catch (CBException e) {
				System.err.println("cannot connect to cloudbase: "+e);
			} catch (CBSecurityException e) {
				System.err.println("security violation occurred: "+e);
	    	}
    		return;
    	}
    	else if (fields.length == 2 && fields[1].equals("tablepermissions"))
    	{
    		for (TablePermission p : TablePermission.values())
    			System.out.println("        Table."+p.toString());
			return;
		}
    	else if (fields.length == 2 && fields[1].equals("systempermissions"))
    	{
    		for (SystemPermission p : SystemPermission.values())
    			System.out.println("        System."+p.toString());
			return;
		}
    	else if (fields.length == 3 && fields[1].equals("authenticate"))
		{
        	String user = fields[2];
        	byte[] password = reader.readLine("Enter current password for '"+user+"': ", '*').getBytes();
        	try {
				System.out.println("User credentials for '"+user+"' are " + (connector.securityOperations().authenticateUser(user, password) ? "" : "not ") + "valid");
    		} catch (CBException e) {
    			System.err.println("cannot connect to cloudbase: "+e);
    		} catch (CBSecurityException e) {
    			System.err.println("security violation occurred: "+e);
			}
        	return;
		}
    	else if (fields.length == 4 && fields[1].equals("create") && fields[3].startsWith("(") && fields[3].endsWith(")"))
        {
        	String user = fields[2];
        	byte[] password = reader.readLine("Enter new password for '"+user+"': ", '*').getBytes();
        	try {
        		Set<Short> auths = parseAuthorizations(fields[3].substring(1, fields[3].length()-1));
        		connector.securityOperations().createUser(user, password, auths);
        		System.out.print("Created user " + user + " with record-level authorizations (");
        		Iterator<Short> iter = auths.iterator();
        		while (iter.hasNext())
        			System.out.print(iter.next()+(iter.hasNext() ? "," : ""));
        		System.out.println(")");
			} catch (CBSecurityException e) {
	    		if (!e.baduserpass)
	    			System.err.println("Creating user failed: Perhaps "+ user+" exists");
	    		else
					System.err.println("security violation occurred: "+e);
			} catch (CBException e) {
				System.err.println("cannot connect to cloudbase: "+e);
	    	}
        	return;
        }
        else if (fields.length == 3 && fields[1].equals("drop"))
        {
	    	String user = fields[2];
	    	try {
	    		connector.securityOperations().dropUser(user);
	        	System.out.println("Deleted user " + user);
    		} catch (CBException e) {
    			System.err.println("cannot connect to cloudbase: "+e);
    		} catch (CBSecurityException e) {
	    		if (!e.baduserpass)
	    			System.err.println("Deleting user failed: "+ user+" doesn't exist");
	    		else
	    			System.err.println("security violation occurred: "+e);
			}
        	return;
        }
        else if (fields.length == 3 && fields[1].equals("passwd"))
        {
	    	String user = fields[2];
	    	try {
	    		byte[] pass = reader.readLine("Enter new password for '"+user+"': ", '*').getBytes();
				connector.securityOperations().changeUserPassword(user, pass);
				credentials = new AuthInfo(user, pass);
				connector = new Connector(instance, user, pass);
	        	System.out.println("Changed password for user " + user);
    		} catch (CBException e) {
    			System.err.println("cannot connect to cloudbase: "+e);
    		} catch (CBSecurityException e) {
    			System.err.println("security violation occurred: "+e);
			}
        	return;
        }
        else if (fields.length == 4 && fields[1].equals("setauths"))
        {
	    	String user = fields[2];
	    	try {
				connector.securityOperations().changeUserAuthorizations(user, parseAuthorizations(fields[3].substring(1, fields[3].length()-1)));
	        	System.out.println("Changed record-level authorizations for user " + user);
			} catch (NumberFormatException e) {
				System.err.println("authorizations are not in the correct format: try 'help'");
    		} catch (CBException e) {
    			System.err.println("cannot connect to cloudbase: "+e);
    		} catch (CBSecurityException e) {
    			System.err.println("security violation occurred: "+e);
			}
        	return;
        }
        else if (fields.length == 3 && fields[1].equals("getauths"))
        {
	    	String user = fields[2];
	    	// if shell is moved to client side, this won't work anymore, and we'd need to add thrift call for it
	    	Authenticator authenticator = new ZKAuthenticator();
	    	Iterator<Short> auths;
			try {
				auths = authenticator.getUserAuthorizations(credentials, user).iterator();
		    	System.out.print("(");
		    	while (auths.hasNext())
		    		System.out.print(auths.next()+(auths.hasNext() ? "," : ""));
		    	System.out.println(")");
    		} catch (CBSecurityException e) {
    			System.err.println("security violation occurred: "+e);
			}
        	return;
        }
        else if (fields.length == 3 && fields[1].equals("permissions"))
        {
	    	String user = fields[2];
	    	try {
		    	for (SystemPermission p : SystemPermission.values())
		    		if (connector.securityOperations().hasSystemPermission(user, p))
		    			System.out.println("        System."+p.toString());
		    	
		    	for (String t : connector.tableOperations().list())
		    	{
	    			System.out.println("\n        "+t+":");
			    	for (TablePermission p : TablePermission.values())
			    		if (connector.securityOperations().hasTablePermission(user, t, p))
			    			System.out.println("                Table."+p.toString());
		    	}
    		} catch (CBException e) {
    			System.err.println("cannot connect to cloudbase: "+e);
    		} catch (CBSecurityException e) {
    			System.err.println("security violation occurred: "+e);
    		}
        	return;
        }
        else if (fields.length == 4 && fields[1].equals("grant") && fields[3].indexOf(".") != -1)
        {
        	try {
		    	String user = fields[2];
		    	String permission[] = fields[3].split("\\.",2);
		    	if (permission[0].equals("System"))
		    	{
		    		for (SystemPermission p : SystemPermission.values())
		    			if (p.toString().equals(permission[1]))
						{
		    				connector.securityOperations().grantSystemPermission(user, p);
		    				System.out.println("Granted "+user+" the "+fields[3]+" permission");
		    				return;
						}
		    	}
			} catch (CBException e) {
				System.err.println("cannot connect to cloudbase: "+e);
			} catch (CBSecurityException e) {
				System.err.println("security violation occurred: "+e);
			}
        }
        else if (fields.length == 5 && fields[1].equals("grant") && fields[4].indexOf(".") != -1)
        {
        	try {
		    	String user = fields[2];
		    	String tablename =fields[3];
		    	String permission[] = fields[4].split("\\.",2);
		    	if (permission[0].equals("Table"))
		    	{
		    		for (TablePermission p : TablePermission.values())
		    		{
		    			if (p.toString().equals(permission[1]))
						{
		    				connector.securityOperations().grantTablePermission(user, tablename, p);
		    				System.out.println("Granted "+user+" the "+fields[4]+" permission on table "+fields[3]);
		    				return;
						}
		    		}
		    	}
			} catch (CBException e) {
				System.err.println("cannot connect to cloudbase: "+e);
			} catch (CBSecurityException e) {
				System.err.println("security violation occurred: "+e);
        	}
        }
        else if (fields.length == 4 && fields[1].equals("revoke") && fields[3].indexOf(".") != -1)
        {
        	try {
		    	String user = fields[2];
		    	String permission[] = fields[3].split("\\.",2);
		    	if (permission[0].equals("System"))
		    	{
		    		for (SystemPermission p : SystemPermission.values())
		    			if (permission[1].equals(p.toString()))
						{
		    				connector.securityOperations().revokeSystemPermission(user, p);
		    				System.out.println("Revoked from "+user+" the "+fields[3]+" permission");
		    				return;
						}
		    	}
			} catch (CBException e) {
				System.err.println("cannot connect to cloudbase: "+e);
			} catch (CBSecurityException e) {
				System.err.println("security violation occurred: "+e);
        	}
        }
        else if (fields.length == 5 && fields[1].equals("revoke") && fields[4].indexOf(".") != -1)
        {
        	try {
		    	String user = fields[2];
		    	String tablename =fields[3];
		    	String permission[] = fields[4].split("\\.",2);
		    	if (permission[0].equals("Table"))
		    	{
		    		for (TablePermission p : TablePermission.values())
		    			if (permission[1].equals(p.toString()))
						{
		    				connector.securityOperations().revokeTablePermission(user, tablename, p);
		    				System.out.println("Revoked from "+user+" the "+fields[4]+" permission");
		    				return;
						}
		    	}
			} catch (CBException e) {
				System.err.println("cannot connect to cloudbase: "+e);
			} catch (CBSecurityException e) {
				System.err.println("security violation occurred: "+e);
        	}
        }

    	System.out.println(USAGE);
    }

    static final String HELP[][] = 
    {
     { "help","help - provides information of available commands" },
     {"connect", "connect - connects to a specific cloudbase instance"},
     {"tables", "tables - list the tables in the database"},
     {"table", "table - switches to a table with an optional username"},
     {"createtable", "createtable - adds a table to the database (can specify optional aggregators, as well as an optional file containing pre-split points)"},
     {"deletetable", "deletetable - removes a table from the database"},
     {"insert", "insert - adds the fully qualified key/value pair to the current table"},
     {"selectrow", "selectrow - returns all key/value pairs associated a row from the current table"},
     {"select", "select - returns the value associated with a fully qualified key"},
     {"scan", "scan - scans the table, beginning at a particular row"},
     {"setuser", "setuser - sets the shell user to the specified name"},
     {"user", "list - lists users on the system (only root can do this)\n"+
    	 "tablepermissions - lists all possible table permissions\n"+
         "systempermissions - lists all possible system permissions\n"+
         "authenticate - verifies a user's username/password is correct\n"+
    	 "create - adds a user to the system\n"+
         "drop - removes a user from the system\n"+
    	 "passwd - changes the user's password\n"+
    	 "setauths - views/sets/changes the user's record-level authorizations\n"+
    	 "permissions - lists a user's current permissions\n"+
         "grant - grants the use of a permission to a user (only root can do this)\n"+
         "revoke - revokes a permission from a user (only root can do this)"
     },
     {"flush", "Makes a best effort to flush a table from memory to disk"},
     {"config", "config - prints system and table specific properties, can set table specific properties"},
     {"setiter", "setiter - sets table specific iterator properties"},
     {"deleteiter", "deleteiter - deletes table specific properties"},
     {"whoami", "whoami - reports the current user name"}
    };
                       
    static final String USAGE = 
        "available commands, <parameters> are required, [options] are optional:\n" +
        "help <command>\n" +
        "connect <master host>[:<port>]{,<master host>[:<port>]}\n" +
        "tables\n" +
        "table <table> [username]\n" +
        "createtable <table> {AGG <columnfamily>[:<columnqualifier>] <aggregation class>} [SPLITS <filename>]\n" +
        "deletetable <table>\n" +
        "flush <pattern>\n" +
        "insert <row> <columnfamily> <columnqualifier> <value> [label expression]\n" +
        "delete <row> <columnfamily> <columnqualifier>\n" +
        "selectrow <row> [-s(comma-delimited authorizations)]\n" +
        "select <row> <columnfamily> <columnqualifier> [comma-delimited authorizations]\n" +
        "setuser <username>\n" +
        "user list\n" +
        "user tablepermissions\n" +
        "user systempermissions\n" +
        "user authenticate <username>\n" +
        "user create <username> (<authorization>{,<authorizations})\n" +
        "user drop <username>\n" +
        "user passwd <username>\n" +
        "user setauths <username (<authorization>{,<authorizations})\n" +
        "user permissions <username>\n" +
        "user grant <username> [<table>] <permission>\n" +
        "user revoke <username> [<table>] <permission>\n" +
        "scan [-s(comma-delimited authorizations)] [start row]  [columns]\n" +
        "config [-d] [-t <table>]  [<property name>[=<property value>]]\n" +
        "setiter <table> <minc|majc|scan> <priority> [distinguishingName]\n" +
        "deleteiter <table> <itername> <minc|majc|scan>"
        ;

    public void helpCommand(String[] fields)
    {
        if (fields.length >= 2)
        {
            String msg = null;
            for (String[] help : HELP)
            {
                if (help[0].equals(fields[1]))
                {
                    msg = help[1];
                    break;
                }
            } 
            if (msg != null)
                System.out.println(msg);
            else
                System.err.println("command does not exist; type 'help' for a list of available commands");                            
        }
        else
            System.out.println(USAGE);
    }

    public void deleteiterCommand(String[] fields)
    throws CBException, CBSecurityException
    {
        if (fields.length != 4) {
        	System.out.println("usage: 'deleteiter <table> <itername> <scope>'");
        	return;
        }
        
        CBConfiguration config = CBConfiguration.getInstance(fields[1]);
        
        String stem = IteratorUtil.ITERATOR_PROPERTY+fields[3]+"."+fields[2];
        for (Entry<String, String> entry : config)
        {
        	if (entry.getKey().startsWith(stem))
        	{
        		System.out.println("removing property "+entry.getKey());
        		connector.tableOperations().removeProperty(fields[1], entry.getKey());
        	}
        }
    }
    
    public void setiterCommand(String[] fields)
    throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, CBException, CBSecurityException
    {
    	String input;
        if (fields.length < 4 || fields.length > 5)
        {
        	System.out.println("usage: 'setiter <table> <scope> <priority> [distinguishingName]'");
        	return;
        }
        String table = fields[1];
        
        IteratorScope scope = null;
        
        for (IteratorScope is : IteratorScope.values())
        {
        	if (fields[2].equals(is.name()))
        	{
        		scope = is;
        		break;
        	}
        }
        
        int priority = Integer.parseInt(fields[3]);
        
        prompt = table+"> enter iterator class <agg|filter|regex|version|nolabel|className>: ";
        
        input = reader.readLine(prompt);
        if(input == null)
        	return;
        
        String name;
        Map<String,String> options = new HashMap<String,String>();
        boolean filter = false;
        if (input.equals("filter") || input.equals(FilteringIterator.class.getName()))
        {
        	filter = true;
        	input = FilteringIterator.class.getName();
        }
        else if (input.equals("agg"))
        	input = AggregatingIterator.class.getName();
        else if (input.equals("regex"))
        	input = RegExIterator.class.getName();
        else if (input.equals("version"))
        	input = VersioningIterator.class.getName();
        else if (input.equals("nolabel"))
        	input = NoLabelIterator.class.getName();
        
        name = setUpOptions(reader, table, input, options);
        if (fields.length > 4)
        	name = fields[4];
        
        if (filter)
        {
        	Map<String,String> updates = new HashMap<String,String>();
        	for (String key : options.keySet()) {
        		String c = options.get(key);
        		if (c.equals("ageoff"))
        		{
        			c = AgeOffFilter.class.getName();
        			options.put(key,c);
        		}
        		else if (c.equals("regex"))
        		{
        			c = RegExFilter.class.getName();
        			options.put(key,c);
        		}
        		Map<String,String> filterOptions = new HashMap<String,String>();
        		setUpOptions(reader,table,c,filterOptions);
        		for (Entry<String,String> e : filterOptions.entrySet())
        			updates.put(key+"."+e.getKey(), e.getValue());
        	}
        	options.putAll(updates);
        }
        
        
        String stem = String.format("%s%s.%s", IteratorUtil.ITERATOR_PROPERTY, scope.name(), name);
        System.out.println("setting property "+stem+" to "+priority+","+input);
        connector.tableOperations().setProperty(table, stem, priority+","+input);
        stem += ".opt.";
        for (Entry<String, String> e : options.entrySet())
        {
        	System.out.println("setting property "+stem+e.getKey()+" to "+e.getValue());
        	connector.tableOperations().setProperty(table, stem+e.getKey(), e.getValue());
        }
    }

    public void configCommand(String[] fields) throws CBException, CBSecurityException
    {
        String table = null;
        String property = null;
        String value = null;
        boolean delete = false;
        CBConfiguration config = null;
        
        ArrayList<String> arguments = new ArrayList<String>();
        
        for (int i = 0; i < fields.length; i++)
        {
        	if (fields[i].equals("-t"))
        		table = fields[++i];
        	else if (fields[i].equals("-d"))
        		delete = true;
        	else
        		arguments.add(fields[i]);
        }
        
        if (arguments.size() == 2)
        	property = arguments.get(1);
        
        if (table == null && property != null && (property.contains("=") || delete))
        {
        	System.out.println("ERROR : Cannot modify system properties here, edit the xml file");
        	return;
        }
        
        if(property != null && property.contains("="))
        {
        	String sa[] = property.split("=", 2);
        	property = sa[0];
        	value = sa[1];
        }
        
        config = (table == null) ? CBConfiguration.getInstance() : CBConfiguration.getInstance(table);
        
        if (value == null && !delete)
        {
        	CBConfiguration systemConfig = CBConfiguration.getInstance();
        
        	System.out.printf("%-10s | %-60s | %s\n", "TYPE","NAME","VALUE");
        	System.out.printf("%10s-+-%60s-+-%20s\n", repeat("-", 10), repeat("-", 60), repeat("-", 20));
        
        	ArrayList<String> props = new ArrayList<String>();
        
        	for (Entry<String, String> entry : config)
        	{
        		if(!entry.getKey().startsWith("cloudbase"))
        			return;
        	
        		props.add(entry.getKey());
        	}
        
        	Collections.sort(props);
        
        	for (String prop : props)
        	{
        		if(property != null && !prop.contains(property))
        			return;
        	
        		String type = "system";
        		String sysVal = systemConfig.get(prop); 
        		if(sysVal == null || !sysVal.equals(config.get(prop)))
        			type = "table";
        		
        		System.out.printf("%-10s | %-60s | %s\n", type, prop, config.get(prop));
        	}
        }
        else
        {
        	if (property.equals("cloudbase.security.cellLevel.defaultLabelExpression"))
        	{
        		try {
        			LabelConversions.expressionStringToBytes(value);
        		} catch (LabelExpressionFormatException e) {
        			System.err.println("Incorrectly formatted label expression."
        					+ " Example usage: CNF[(9)(3,200)]");
        			return;
        		}
        	}
        	
        	if (delete)
        		connector.tableOperations().removeProperty(table, property);
        	else
        		connector.tableOperations().setProperty(table, property, value);
        	
        }
    }

    public void setuserCommand(String[] fields)
    {
    	// save old credentials and connection in case of failure
    	AuthInfo prevCreds = credentials;
    	Connector prevConn = connector;
        if (fields.length == 2)
        {
        	boolean failed = false;
        	String user = fields[1];
        	byte[] pass;
			try {
				pass = reader.readLine("Enter password for user "+user+": ", '*').getBytes();
	        	credentials = new AuthInfo(user, pass);
	        	connector = new Connector(instance, user, pass);
			} catch (IOException e) {
				failed = true;
				System.err.println("unknown IO exception: "+e);
			} catch (CBException e) {
				failed = true;
				System.err.println("cannot connect to cloudbase: "+e);
			} catch (CBSecurityException e) {
				failed = true;
				System.err.println("security violation occurred: "+e);
			}
			// revert to saved credentials if failure occurred
			if (failed)
			{
				credentials = prevCreds;
				connector = prevConn;
			}
        }
        else
        	System.err.println("Example usage: setuser nobody");
    }

    public void scanCommand(String[] fields)
    throws CBException, CBSecurityException, TableNotFoundException, IOException
    {
        if (tableName.equals(new Text("")))
        {
        	System.out.println("not connected to a table. use 'table <name>'");
        	return;
        }
        
        int rowPos = 1;
        Text startRow = null;
        if (fields.length > 1)
        {
        	try
        	{
	        	if (fields[1].startsWith("-s") && fields[1].length() > 4)
	        	{
	       	        scanner = connector.createScanner(tableName.toString(), parseAuthorizations(fields[1].substring(3,fields[1].length()-1)));
	       			rowPos = 2;
	        	}
	        	else
	        	{
        			System.out.println("Incorrectly formatted credentials.  Example usage: -s(3,16)");
        			return;
	        	}
        	}
        	catch(NumberFormatException e)
        	{
    			System.out.println("Incorrectly formatted credentials.  Example usage: -s(3,16)");
    			return;        		
        	}
        }
        else
        {
        	scanner = connector.createScanner(tableName.toString(), CBConstants.NO_AUTHS);
        }
        
        Text minCf = null;
        Text minCq = null;
        
        if (fields.length > rowPos) {						
        	// handle start row
        	startRow = new Text(fields[rowPos]);
        	
        	// handle columns
        	if(fields.length > rowPos+1) 
        		for(int i=rowPos+1; i < fields.length; i++){
        			String sa[] = fields[i].split(":",2);
        			if(sa.length == 1){
        				Text cf = new Text(fields[i]);
        				if(minCf == null || cf.compareTo(minCf) <= 0){
        					minCf = cf;
        					minCq = null;
        				}
        				scanner.fetchColumnFamily(cf);
        			}else{
        				Text cf = new Text(sa[0]); 
        				Text cq = new Text(sa[1]);
        				
        				if(minCf == null || cf.compareTo(minCf) < 0){
        					minCf = cf;
        					minCq = cq;
        				}else if(minCf != null && cf.compareTo(minCf) == 0 && minCq != null && cq.compareTo(minCq) < 0){
        					minCf = cf;
        					minCq = cq;
        				}
        				
        				scanner.fetchColumn(cf, cq);
        			}
        		}
        	
        }
        
        if (startRow == null)
        	scanner.setRange(new Range(new Key(), null));
        else if (minCf == null)
        	scanner.setRange(new Range(new Key(startRow), null));
        else if (minCq == null)
        	scanner.setRange(new Range(new Key(startRow, minCf), null));
        else
        	scanner.setRange(new Range(new Key(startRow, minCf, minCq), null));
        
        int lines = 1;
        String output, phrase = " ----------- hit any key to continue or q to quit -----------";
        int termWidth= reader.getTermwidth();
        int termSize = (int) (reader.getTermheight()-Math.ceil(phrase.length()*1.0/termWidth));
        prompt = "";
        
        Iterator<Entry<Key, Value>> si = scanner.iterator();
        
        while(si.hasNext()) {
        	Entry<Key, Value> entry = si.next();
        	output = formatEntry(entry);
        	System.out.println(output);
        	lines+=Math.ceil(output.length()*1.0/termWidth);
        	
        	if(lines >= termSize && si.hasNext()) {
        		lines = 1;
        		System.out.print(phrase);
        		if(Character.toUpperCase((char)reader.readVirtualKey())=='Q') {
        			System.out.println();
        			break;
        		}
        		System.out.println();
        		termWidth= reader.getTermwidth();
        		termSize= (int) (reader.getTermheight()-Math.ceil(phrase.length()*1.0/termWidth));
        	}
        }
    }

    public void deleteCommand(String[] fields)
    throws CBException, CBSecurityException, ConstraintViolationException, TableNotFoundException
    {
        if(tableName.equals(new Text(""))) {
        	System.err.println("no table specified. use 'table <name>'");
        	return;
        }
        // split into row col value
        if (fields.length < 4) {
        	System.err.println("error: must include <row> <colfamily> <colqualifier>");
        	return;
        }
        Mutation m = new Mutation(new Text(fields[1]));
        
        m.remove(new Text(fields[2]), new Text(fields[3]));
        
        Writer writer = new Writer(instance, credentials, tableName);
        if(writer.update(m)) 
        	System.out.println("delete successful");
        else
        	System.err.println("error on delete");
    }

    public void flushCommand(String[] fields)
    throws CBException, CBSecurityException
    {
        String pattern = ".*";
        if (fields.length > 1) {
            pattern = fields[1];
        }

        for (String table : connector.tableOperations().list())
        {
            if (table.matches(pattern))
            {
                try {
					connector.tableOperations().flush(table);
                    System.out.println("Flush of table " + table + " initiated...");
                    if(table.equals(CBConstants.METADATA_TABLE_NAME))
                    {
                        System.out.println("WARN : May need to flush !METADATA table multiple times.");
                        System.out.println("       Flushing !METADATA causes writes to !METADATA and\n" +
                                           "       minor compactions, which also cause writes to !METADATA.\n" +
                                           "       Check the Master web page and give it time to settle.");
                    }
				} catch (Throwable e) {
                    System.err.println("failed to flush table " + fields[1]);
				}
            }
        }
    }

    public void selectCommand(String[] fields)
    throws CBException, CBSecurityException, TableNotFoundException, UnsupportedEncodingException
    {
        if(tableName.equals(new Text("")))
        {
        	System.out.println("not connected to a table. use 'table <name>'");
        	return;
        }
        if(fields.length < 4)
        {
        	System.err.println("must specify a <row> <colfamily> <colqualifier>");
        	return;
        }
        
        if (fields.length == 5) {
            Set<Short> auths = parseAuthorizations(fields[4]);
            if (auths == null)
            {
                System.err.println("Incorrectly formatted authorizations. Example usage: 3,16");
                return;
            }
            scanner = connector.createScanner(tableName.toString(), auths);
        }
        scanner = connector.createScanner(tableName.toString(), CBConstants.NO_AUTHS);
        
        Key key = new Key(new Text(fields[1]), new Text(fields[2]), new Text(fields[3]));
        scanner.setRange(new Range(key, key.followingKey(3)));
        
        Iterator<Entry<Key, Value>> si = scanner.iterator();
        
        if(!si.hasNext())
        	System.out.println("no results");
        else {
        	// TODO may not always want to convert to a string here ...
        	System.out.println(Value.bytesToString(si.next().getValue().get()));
        }
    }

    public void selectrowCommand(String[] fields)
    throws CBException, CBSecurityException, TableNotFoundException
    {
        if (tableName.equals(new Text("")))
        {
        	System.err.println("no table specified. use 'table <name>'");
        	return;
        }
        
        if (fields.length>=3 && fields[2].startsWith("-s") && fields[2].length() > 4) {
        	Set<Short> auths = parseAuthorizations(fields[2].substring(3,fields[2].length()-1));
        	if (auths != null)
                scanner = connector.createScanner(tableName.toString(), auths);
        	else
        	{
        		System.out.println("Incorrectly formatted authorizations.  Example usage: -s(3,16)");
        		return;
        	}
        }
        else
            scanner = connector.createScanner(tableName.toString(), CBConstants.NO_AUTHS);
        
        Key row = new Key(new Text(fields[1]));
        scanner.setRange(new Range(row, row.endKey(1)));
        
        Iterator<Entry<Key, Value>> si = scanner.iterator();
        
        if (!si.hasNext())
        {
        	System.err.println("no results");
        	return;
        }
        Entry<Key, Value> entry;
        while(si.hasNext())
        { 
        	entry = si.next();
        	Key key = entry.getKey();
        	if (!key.getRow().equals(row))
        		break;
        	System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }

    public void insertCommand(String[] fields) throws LabelExpressionFormatException, CBException, TableNotFoundException
    {
        if (tableName.equals(new Text("")))
        {
        	System.err.println("no table specified. use 'table <name>'");
        	return;
        }

        // split into row col value
        if (fields.length < 5)
        {
        	System.err.println("error: must include <row> <colfamily> <colqualifier> <data>");
        	return;
        }

        Mutation m = new Mutation(new Text(fields[1]));
        
        if (fields.length == 6)
        {
        	LabelExpression le = new LabelExpression(LabelConversions.expressionStringToBytes(fields[5]));
        	if (le!= null)
        		System.out.println("Security labels will be set to: " + le.toString());
        	else 
        		System.out.println("Warning: security labels are empty.");
        	
        	m.put(new Text(fields[2]), new Text(fields[3]), le,new Value(fields[4].getBytes()) );
        }
        else
        	m.put(new Text(fields[2]), new Text(fields[3]), new Value(fields[4].getBytes()));

        try {
            Writer writer = new Writer(instance, credentials, tableName);
        	if(writer.update(m)) 
        		System.out.println("insert successful");
        	else
        		System.err.println("error on insert");
        } catch (CBSecurityException e) {
        	System.err.println("Authentication failure.");
        } catch (ConstraintViolationException cve){
        	System.err.println("Insert failed, it violated table constraints\n");
        	System.err.printf("%-50s | %14s | %-80s\n", "Constraint class","Violation code", "Violation Description");
        	System.err.printf("%50s-+-%14s-+-%80s\n", repeat("-", 50), repeat("-", 14), repeat("-", 80));
        	for (ConstraintViolationSummary cvs : cve.violationSummaries) {
        		System.err.printf("%-50s | %14d | %-80s\n", cvs.constrainClass, cvs.violationCode, cvs.violationDescription);
        	}
        	System.err.println();
        }
    }

    public void deletetableCommand(String[] fields)
    {
        if (tableName.toString().equals(fields[1]))
        	tableName = new Text("");

        try {
			connector.tableOperations().delete(fields[1]);
		} catch (CBException e) {
			System.err.println("cannot connect to cloudbase: "+e);
		} catch (CBSecurityException e) {
			System.err.println("security violation occurred: "+e);
		} catch (TableNotFoundException e) {
			System.err.println("cannot delete table: "+e);
		}
    }

	public void createtableCommand(String[] fields)
    {
        try {
        	SortedSet<Text> partitions = new TreeSet<Text>();
        	Map<Text, Class<? extends Aggregator>> aggregators = new HashMap<Text, Class<? extends Aggregator>>();
        	
        	for(int i = 2; i < fields.length; i++){
        		if(fields[i].equals("AGG")){
        			String col = fields[++i];
        			String className = fields[++i];

        			Class<? extends Aggregator> clazz = (Class<? extends Aggregator>) Class.forName(className);
        			aggregators.put(new Text(col), clazz);
        		}
        		else if(fields[i].equals("SPLITS")){
        			String line;
       				java.util.Scanner file = new java.util.Scanner(new File(fields[++i]));
        			while (file.hasNextLine()) {
        				line = file.nextLine();
        				if (!line.isEmpty())
        					partitions.add(new Text(line));
        			}
        		}
        	}
        	
        	connector.tableOperations().create(fields[1], partitions, aggregators);
        	tableName = new Text(fields[1]);
        	
        } catch (IOException e) {
        	System.err.println("error creating table. error: "+e);
		} catch (ClassNotFoundException e) {
			System.err.println("cannot find aggregator class: "+e);
		} catch (CBException e) {
			System.err.println("cannot connect to cloudbase: "+e);
		} catch (CBSecurityException e) {
			System.err.println("security violation occurred: "+e);
		} catch (TableExistsException e) {
			System.err.println("cannot create table: "+e);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("missing argument: try 'help'"); 
		}
    }

    public void tablesCommand(String[] fields)
    {
        try {
			for (String table : connector.tableOperations().list())
			    System.out.println(table);
		} catch (CBException e) {
			System.err.println("cannot connect to cloudbase: "+e);
		} catch (CBSecurityException e) {
			System.err.println("security violation occurred: "+e);
		}
    }

    public void tableCommand(String[] fields)
    throws CBException, CBSecurityException
    {
    	// TODO find a way to handle 'unable to find any files in root table loc dir'
    	if (fields.length != 2)
    		System.out.println("Usage : table <table>");
    	else if (connector.tableOperations().exists(fields[1]))
    	{
    		System.out.println("Connecting as user: " + connector.whoami());
    		tableName = new Text(fields[1]);
    	}
    	else
    		System.err.println("no such table: " + fields[1]);
    }

	public void whoamiCommand(String[] fields)
	{
		System.out.println(connector.whoami());
	}
	
	protected static String formatEntry(Entry<Key, Value> entry)
	{
		String output;
		String row = toPrintableString(entry.getKey().getRow());
		String cf = toPrintableString(entry.getKey().getColumnFamily());
		String cq = toPrintableString(entry.getKey().getColumnQualifier());
		String cv = (new LabelExpression(entry.getKey().getColumnVisibility())).toString();
		
		String key = row+" "+cf+":"+cq+" "+cv;
		
		output = key + "\t" + toPrintableString(entry.getValue());
		return output;
	}

	private static String repeat(String s, int c)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < c; i++)
			sb.append(s);
		return sb.toString();
	}

	private static Set<Short> parseAuthorizations(String field)
	throws NumberFormatException
	{
		Set<Short> auths = new HashSet<Short>();
		if (field == null || field.isEmpty())
			return auths;
		String[] labels = field.split(",");
		for (String s : labels)
			auths.add(Short.parseShort(s));
		return auths;
	}
	
	private static String toPrintableString(byte ba[], int offset, int len)
	{
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < len; i++)
		{
			int c = 0xff & ba[offset+i]; 
			if(c >=32 && c <= 126)
				sb.append((char)c);
			else
				sb.append('.');
		}
		return sb.toString();
	}
	
	private static String toPrintableString(Text t)
	{
		return toPrintableString(t.getBytes(), 0, t.getLength());
	}
	
	private static String toPrintableString(Value value)
	{
		return toPrintableString(value.get(), 0, value.get().length);
	}
	
	private String setUpOptions(ConsoleReader reader, String table, String className, Map<String, String> options)
	throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
	{
		String input;
		Class<?> clazz = Class.forName(className);
		Object skvi = clazz.newInstance();
		
		if (!(skvi instanceof OptionDescriber))
		{
			System.out.println(className+" does not implement an OptionDescriber, it is not configured to set properties via the shell");
			return null;
		}
			
		IteratorOptions itopts = ((OptionDescriber)skvi).describeOptions();
		
		System.out.println(itopts.description);
		
		if (itopts.name==null)
			throw new IllegalArgumentException(className+" described its default distinguishing name as null");
		
		if (itopts.fixedOptions!=null)
		{
			for (Entry<String, String> e : itopts.fixedOptions.entrySet())
			{
				prompt = table+"> set "+className+" parameter "+e.getKey()+", "+e.getValue()+": ";
				
				input = reader.readLine(prompt);
				
				if (input.length()>0)
					options.put(e.getKey(), input);
			}
		}
		
		if (itopts.optionalOptionDescriptions!=null)
		{
			for (String desc : itopts.optionalOptionDescriptions)
			{
				System.out.println(table+"> entering options: "+desc);
				input = "start";
				while (true)
				{
					prompt = table+"> set "+className+" option (<name> <value>, hit enter to skip): ";
					
					input = reader.readLine(prompt);
					
					if (input.length()==0)
						break;
					
					String[] sa = input.split(" ", 2);
					options.put(sa[0],sa[1]);
				}
			}
		}
		
		if (!((OptionDescriber)skvi).validateOptions(options))
		{
			System.out.println("invalid options for "+clazz.getName());
			return null;
		}
		
		return itopts.name;
	}
}
