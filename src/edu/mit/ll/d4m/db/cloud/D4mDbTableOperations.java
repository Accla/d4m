package edu.mit.ll.d4m.db.cloud;

import java.util.logging.Level;
import java.util.logging.Logger;

import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.TableExistsException;
import cloudbase.core.client.TableNotFoundException;
import edu.mit.ll.cloud.connection.CloudbaseConnection;
import edu.mit.ll.cloud.connection.ConnectionProperties;

/**
 * @author wi20909
 */
public class D4mDbTableOperations {
	
	public String rowReturnString = "";
	public String columnReturnString = "";
	public String valueReturnString = "";

	private ConnectionProperties connProps = new ConnectionProperties();

	public D4mDbTableOperations() {
	}
	
	public D4mDbTableOperations(ConnectionProperties connProps) {
		this.connProps = connProps;
	}
	
	public D4mDbTableOperations(String instanceName, String host, String username, String password) {
		this.connProps.setHost(host);
		this.connProps.setInstanceName(instanceName);
		this.connProps.setUser(username);
		this.connProps.setPass(password);
	}

	public static void main(String[] args) throws CBException, CBSecurityException, TableNotFoundException {
		if (args.length < 1) {
			return;
		}

		String hostName = args[0];
		D4mDbTableOperations ci = new D4mDbTableOperations("", hostName, "root", "secret");
		ci.createTable("test_table200");
		ci.deleteTable("test_table200");
	}

	public void createTable(String tableName) {
		CloudbaseConnection cbConnection = null;
		try {
			cbConnection = new CloudbaseConnection(this.connProps);
		}
		catch (CBException ex) {
			Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (CBSecurityException ex) {
			Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
		}
		try {
			cbConnection.createTable(tableName);
			System.out.println("The " + tableName + " table was created.");
		}
		catch (CBException ex) {
			Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (CBSecurityException ex) {
			Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (TableExistsException ex) {
			System.out.println("The " + tableName + " table already Exists.");
		}
	}

	public void deleteTable(String tableName) {
		CloudbaseConnection cbConnection = null;
		try {
			cbConnection = new CloudbaseConnection(this.connProps);
		}
		catch (CBException ex) {
			Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (CBSecurityException ex) {
			Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
		}
		try {
			cbConnection.deleteTable(tableName);
			System.out.println("The " + tableName + " table was deleted.");
		}
		catch (CBException ex) {
			Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (CBSecurityException ex) {
			Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (TableNotFoundException ex) {
			Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
/*
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
*/

