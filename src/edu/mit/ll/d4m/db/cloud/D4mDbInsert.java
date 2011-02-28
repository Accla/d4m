package edu.mit.ll.d4m.db.cloud;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;

import org.apache.log4j.Logger;

import org.apache.hadoop.io.Text;

import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.MutationsRejectedException;
import cloudbase.core.client.TableExistsException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Value;
import cloudbase.core.security.ColumnVisibility;
import edu.mit.ll.cloud.connection.CloudbaseConnection;
import edu.mit.ll.cloud.connection.ConnectionProperties;

/**
 * @author William Smith
 */

public class D4mDbInsert {

	private static Logger log = Logger.getLogger(D4mDbInsert.class);
	private String tableName = "";
	private String startVertexString = "";
	private String endVertexString = "";
	private String weightString = "";
	//private String columnFamilyName="vertexfamily";
	//private String columnQualifierPrefix="vertexfamilyValue:";
	//private String columnVisibility="";
	static final boolean doTest = false;
	static final boolean printOutput = false;
	static final int maxMutationsToCache = 10000;
	static final int numThreads = 50;

	private ConnectionProperties connProps = new ConnectionProperties();

	private ColumnBean columnBean= new ColumnBean(); 



	/**
	 * Constructor that may use MasterInstance or ZooKeeperInstance to connect
	 * to CB.
	 * 
	 * @param connProps
	 * @param tableName
	 * @param startVertexString
	 * @param endVertexString
	 * @param weightString
	 * @throws CBException
	 * @throws CBSecurityException
	 * @throws TableExistsException
	 */
	public D4mDbInsert(ConnectionProperties connProps, String tableName, String startVertexString, String endVertexString, String weightString) throws CBException, CBSecurityException, TableExistsException {
		this.tableName = tableName;
		this.startVertexString = startVertexString;
		this.endVertexString = endVertexString;
		this.weightString = weightString;

		this.connProps = connProps;
	}

	/**
	 * @param instanceName
	 * @param hostName
	 * @param username
	 * @param password
	 * @param tableName
	 * @param startVertexString
	 * @param endVertexString
	 * @param weightString
	 * @throws CBException
	 * @throws CBSecurityException
	 * @throws TableExistsException
	 */
	public D4mDbInsert(String instanceName, String hostName, String tableName, String username, String password, String startVertexString, String endVertexString, String weightString) throws CBException, CBSecurityException, TableExistsException {
		this.tableName = tableName;
		this.startVertexString = startVertexString;
		this.endVertexString = endVertexString;
		this.weightString = weightString;

		this.connProps.setHost(hostName);
		this.connProps.setInstanceName(instanceName);
		this.connProps.setUser(username);
		this.connProps.setPass(password);
	}

	public D4mDbInsert(String instanceName, String hostName,
			String tableName, String username, String password) throws CBException, CBSecurityException, TableExistsException {
		this.tableName = tableName;

		this.connProps.setHost(hostName);
		this.connProps.setInstanceName(instanceName);
		this.connProps.setUser(username);
		this.connProps.setPass(password);
	}


	/**
	 * @param instanceName    instance of cloudbase
	 * @param hostName     host of the cloudbase
	 * @param tableName        table name
	 * @param username         user of cloudbase
	 * @param password   
	 * @param authorizations   A user's authorizations to the table
	 * @throws CBException
	 * @throws CBSecurityException
	 * @throws TableExistsException
	 */
	public D4mDbInsert(String instanceName, String hostName,
			String tableName, String username, String password,
			String [] authorizations) throws CBException, CBSecurityException, TableExistsException {
		this.tableName = tableName;

		this.connProps.setHost(hostName);
		this.connProps.setInstanceName(instanceName);
		this.connProps.setUser(username);
		this.connProps.setPass(password);
		this.connProps.setAuthorizations(authorizations);

	}

	public static void main(String[] args) throws FileNotFoundException, IOException, CBException, CBSecurityException, TableNotFoundException, MutationsRejectedException, TableExistsException {

		if (args.length < 5) {
			return;
		}

		String hostName = args[0];
		String tableName = args[1];
		String startVertexString = args[2];
		String endVertexString = args[3];
		String weightString = args[4];

		D4mDbInsert ci = new D4mDbInsert("cloudbase", hostName, tableName, "yee", "secret", startVertexString, endVertexString, weightString);
		String [] authorizations ={"s","ts","sci"};
		String visibility="s|ts|sci";
		String columnFamily="d4mFamily";
		String columnQualifier="d4mFamilyValue";
		ci.doProcessing(authorizations,visibility,columnFamily,columnQualifier);
	}
	/*
	 * 
	 *  columnVisibility   The columnVisibility is an expression of the rights to view this mutation.
	 *  A valid expression looks like
	 *      A
	 *      A|B
	 *      (A|B)&(C|D)
	 *      orange | (red & yellow)
	 */

	public void doProcessing(String [] authorizations, String columnVisibility,String columnFamily, String columnQualifier) throws IOException, CBException, CBSecurityException, TableNotFoundException, MutationsRejectedException {
		if(authorizations != null)
			this.connProps.setAuthorizations(authorizations);
		if(columnVisibility != null)
			this.columnBean.setColumnVisibility(columnVisibility);
		if(columnFamily != null)
			this.columnBean.setColumnFamily(columnFamily);
		if(columnQualifier != null)
			this.columnBean.setColumnQualifier(columnQualifier);
		doProcessing();
	}

	/*
	 * 
	 *  authorizations  Authorizations of the user.  These authorizations will used for the column visibility
	 *  columnFamily    Column family name.  The columnFamily will also serve as the column qualifier.
	 *           "Value:" will be appended to the columnQualifier
	 */
	public void doProcessing(String [] authorizations,String columnFamily) throws IOException, CBException, CBSecurityException, TableNotFoundException, MutationsRejectedException {

		if(authorizations != null) {
			this.connProps.setAuthorizations(authorizations);
			this.columnBean.setColumnVisibility(authorizations);
		}
		if(columnFamily != null) {
			this.columnBean.setColumnFamily(columnFamily);
			this.columnBean.setColumnQualifier(columnFamily+"Value:");
		}
		doProcessing();
	}

	public void doProcessing() throws IOException, CBException, CBSecurityException, TableNotFoundException, MutationsRejectedException {

		// this.doLoadTest();
		this.createTable();
		Date startDate = new Date();
		long start = System.currentTimeMillis();

		CloudbaseConnection  cbConnection = new CloudbaseConnection(this.connProps);
		BatchWriter batchWriter = cbConnection.getBatchWriter(tableName);

		HashMap<String, Object> startVertexMap = this.processParam(startVertexString);
		HashMap<String, Object> endVertexMap = this.processParam(endVertexString);
		HashMap<String, Object> weightMap = this.processParam(weightString);

		String[] startVertexArr = (String[]) startVertexMap.get("content");
		String[] endVertexArr = (String[]) endVertexMap.get("content");
		String[] weightArr = (String[]) weightMap.get("content");

		for (int i = 0; i < startVertexArr.length; i++) {

			String startVertexValue = startVertexArr[i];
			String endVertexValue = endVertexArr[i];
			String weightValue = weightArr[i];

			Text columnFamily = new Text(this.columnBean.getColumnFamily());
			Text columnQualifier = new Text( this.columnBean.getColumnQualifier()+ endVertexValue);
			Mutation m = new Mutation(new Text(startVertexValue));
			m.put(columnFamily,
					columnQualifier, 
					new ColumnVisibility(this.columnBean.getColumnVisibility()), 
					new Value(weightValue.getBytes()));
			batchWriter.addMutation(m);
			m = null;
		}
		batchWriter.close();

		double elapsed = (System.currentTimeMillis() - start);
		Date endDate = new Date();
		long endSeconds = System.currentTimeMillis();
		System.out.println("Time = " + elapsed / 1000 + "," + start / 1000 + "," + endSeconds / 1000 + "," + startDate + "," + endDate);
	}

	/**
	 * @return   columnVisibility
	 */
	public String getColumnVisibility() {
		return this.columnBean.getColumnVisibility();
	}

	/**
	 * @param columnVisibility
	 */
	public void setColumnVisibility(String columnVisibility) {
		this.columnBean.setColumnVisibility(columnVisibility);
	}
	public String getColumnFamilyName() {
		return columnBean.getColumnFamily();
	}

	public void setColumnFamilyName(String columnFamilyName) {
		this.columnBean.setColumnFamily(columnFamilyName);
	}

	public String getColumnQualifierPrefix() {
		return columnBean.getColumnQualifier();
	}

	public void setColumnQualifierPrefix(String columnQualifierPrefix) {
		this.columnBean.setColumnQualifier( columnQualifierPrefix);
	}

	public void setAuthorizations (String [] authorizations) {
		if(log.isDebugEnabled()) {
			for(int i= 0; i < authorizations.length; i++) {
				log.debug("   authorization[ "+i+"] ="+authorizations[i]);
			}
		}
		this.connProps.setAuthorizations(authorizations);

	}
	public HashMap<String, Object> processParam(String param) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String content = param.substring(0, param.length() - 1);
		String delimiter = param.replace(content, "");
		map.put("delimiter", delimiter);
		if (delimiter.equals("|")) {
			delimiter = "\\" + delimiter;
		}
		map.put("content", content.split(delimiter));
		map.put("length", content.length());
		return map;
	}

	public void createTable() throws CBException, CBSecurityException {

		if (this.doesTableExistFromMetadata(tableName) == false) {
			try {
				CloudbaseConnection cbConnection = new CloudbaseConnection(this.connProps);
				cbConnection.createTable(tableName);
			}
			catch (TableExistsException ex) {
				System.out.println("Table already exists.");
			}
		}
	}

	public boolean doesTableExistFromMetadata(String tableName) {
		boolean exist = false;
		D4mDbInfo info = new D4mDbInfo(this.connProps);
		String tableNames = "";
		try {
			tableNames = info.getTableList();
			if (tableNames.contains(tableName)) {
				exist = true;
			}

		}
		catch (CBException ex) {
			log.warn(ex);
		}
		catch (CBSecurityException ex) {
			log.warn("Security problem", ex);
		}
		return exist;
	}

	public void doLoadTest() {
		int loops = 1000000;
		int capacity = loops;

		StringBuilder sb1 = new StringBuilder(capacity);
		StringBuilder sb2 = new StringBuilder(capacity);
		StringBuilder sb3 = new StringBuilder(capacity);

		System.out.println("Creating test data for " + loops + " entries.");
		for (int i = 1; i < loops + 1; i++) {
			sb1.append(i + " ");
			sb2.append(i + " ");
			sb3.append(i + " ");
		}

		this.startVertexString = sb1.toString();
		this.endVertexString = sb2.toString();
		this.weightString = sb3.toString();
		System.out.println("Completed creation of test data for " + loops + " entries.");
	}


	public void doProcessing(String startVertexString, String endVertexString, String weightString ) throws IOException, CBException, CBSecurityException, TableNotFoundException, MutationsRejectedException {
		this.startVertexString = startVertexString;
		this.endVertexString = endVertexString;
		this.weightString = weightString;
		doProcessing();

	}

	/*
	 *  partitionKey     a string or comma-separated list
	 */
	/*
    public void splitTable(String partitionKey)  throws IOException, CBException, CBSecurityException, TableNotFoundException {
	String [] pKeys = partitionKey.split(",");
	//System.out.println(" *** Number of partition keys = "+ pKeys.length);
	splitTable(pKeys);
    }
	 */
	/*
	 *  partitionKeys  array of strings
	 */
	/*
    public void splitTable(String [] partitionKeys)  throws IOException, CBException, CBSecurityException, TableNotFoundException {
	ArrayList<String> list = new ArrayList<String>();
	for(int i =0; i < partitionKeys.length; i++) {
	    list.add(partitionKeys[i]);
	}
	splitTable(list);
    }
	 */

	/*
	 *   partitionKeys   - list of keys (eg.  java.util.ArrayList)
	 */
	/*
    public void splitTable(List<String> partitionKeys) throws IOException, CBException, CBSecurityException, TableNotFoundException {

	CloudbaseConnection  cbConnection = new CloudbaseConnection(this.connProps);
	cbConnection.splitTable(this.tableName, partitionKeys);
    }
	 */
}
/*
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 
 * % D4M: Dynamic Distributed Dimensional Data Model 
 * % MIT Lincoln Laboratory
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 
 * % (c) <2010> Massachusetts Institute of Technology
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 */
