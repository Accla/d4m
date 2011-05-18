package edu.mit.ll.d4m.db.cloud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.apache.hadoop.io.Text;

import cloudbase.core.client.BatchScanner;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Scanner;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.data.Key;
import cloudbase.core.data.PartialKey;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;
import edu.mit.ll.cloud.connection.CloudbaseConnection;
import edu.mit.ll.cloud.connection.ConnectionProperties;

/**
 * @author William Smith
 */
public class D4mDbQuery {

	private static Logger log = Logger.getLogger(D4mDbQuery.class);
	private String tableName = "";
	private int numberOfThreads = 50;
	public String rowReturnString = "";
	public String columnReturnString = "";
	public String valueReturnString = "";
	public final String newline =   "\n"; // "\n" is necessary for correct parsing. //System.getProperty("line.separator");
	public boolean doTest = false;
	private static final String KEY_RANGE = "KEY_RANGE";
	private static final String REGEX_RANGE = "REGEX_RANGE";
	private static final String POSITIVE_INFINITY_RANGE = "POSITIVE_INFINITY_RANGE";
	private static final String NEGATIVE_INFINITY_RANGE = "NEGATIVE_INFINITY_RANGE";

	private ConnectionProperties connProps = new ConnectionProperties();
	private String family = "";
	private int limit=0; // limit is used to limit the number of results returned; a Limit of zero means get all results.
	StringBuilder sbRowReturn = null;//new StringBuilder();
	StringBuilder sbColumnReturn = null; //new StringBuilder();
	StringBuilder sbValueReturn = null; //new StringBuilder();
	public int numReturnStringsProcessed=0;
	/**
	 * Constructor that may use ZooKeeperInstance or MasterInstance to connect
	 * to CB.
	 * 
	 * @param connProps
	 * @param table
	 */
	public D4mDbQuery(ConnectionProperties connProps, String table) {
		this.tableName = table;
		this.connProps = connProps;
	}

	/**
	 * Constructor that uses ZooKeeperInstance to connect to CB.
	 * 
	 * @param instanceName
	 * @param host
	 * @param table
	 * @param username
	 * @param password
	 */
	public D4mDbQuery(String instanceName, String host, String table, String username, String password) {
		this.tableName = table;
		this.connProps.setHost(host);
		this.connProps.setInstanceName(instanceName);
		this.connProps.setUser(username);
		this.connProps.setPass(password);
	}
	public void getSomeData(int limit) throws CBException, CBSecurityException, TableNotFoundException {
		int count =0;
		this.limit= limit;
		CloudbaseConnection cbConnection = new CloudbaseConnection(this.connProps);
		Scanner scanner = cbConnection.getScanner(tableName);
		scanner.fetchColumnFamily(new Text(this.family));
		if(this.limit > 0) 
			scanner.setBatchSize(limit);
		Iterator<Entry<Key, Value>> scannerIter = scanner.iterator();
		while (scannerIter.hasNext()) {
			Entry<Key, Value> entry = (Entry<Key, Value>) scannerIter.next();
			String rowKey = entry.getKey().getRow().toString();
			String value = entry.getValue().get().toString();//new String(entry.getValue().get());
			String column = entry.getKey().getColumnQualifier().toString();//new String(entry.getKey().getColumnQualifier().toString());
			count++;
			log.info(count+":: row="+rowKey+", column="+column+", value="+value);
		}
	}

	public void getSomeData(String rowRegex, String colRegex, int limit) throws CBException, CBSecurityException, TableNotFoundException, IOException {
		int count =0;
		this.limit= limit;
		CloudbaseConnection cbConnection = new CloudbaseConnection(this.connProps);
		Scanner scanner = cbConnection.getScanner(tableName);
		scanner.setupRegex("regex", 1);
		scanner.setRowRegex(rowRegex);
		scanner.setColumnQualifierRegex(colRegex);
		scanner.fetchColumnFamily(new Text(this.family));
		if(this.limit > 0) 
			scanner.setBatchSize(limit);
		Iterator<Entry<Key, Value>> scannerIter = scanner.iterator();
		printScanResults(scanner);
		while (scannerIter.hasNext()) {
			Entry<Key, Value> entry = (Entry<Key, Value>) scannerIter.next();
			String rowKey = entry.getKey().getRow().toString();
			String value = entry.getValue().get().toString();//new String(entry.getValue().get());
			String column = entry.getKey().getColumnQualifier().toString();//new String(entry.getKey().getColumnQualifier().toString());
			count++;
			log.info(count+":: row="+rowKey+", column="+column+", value="+value);
		}
		log.info("Number of entries="+count);

	}

	public void printScanResults(Scanner scanner) {
		for(Entry<Key,Value> entry : scanner) {
			log.info(entry.getKey().toString()+" ---> "+entry.getValue().toString());
		}
	}

	public D4mDbResultSet getAllData() throws CBException, TableNotFoundException, CBSecurityException {
		D4mDbResultSet results = new D4mDbResultSet();
		ArrayList<D4mDbRow> rowList =null;
		CloudbaseConnection cbConnection = new CloudbaseConnection(this.connProps);
		Scanner scanner = cbConnection.getScanner(tableName);
		scanner.fetchColumnFamily(new Text(this.family));
		//		if(this.limit > 0) 
		//			scanner.setBatchSize(limit);
		long start = System.currentTimeMillis();

		Iterator<Entry<Key, Value>> scannerIter = scanner.iterator();
		rowList = extractResults(scannerIter, null, false,false);

		this.setRowReturnString(sbRowReturn.toString());
		this.setColumnReturnString(sbColumnReturn.toString());
		this.setValueReturnString(sbValueReturn.toString());
		double elapsed = (System.currentTimeMillis() - start);
		results.setQueryTime(elapsed / 1000);
		results.setMatlabDbRow(rowList);
		return results;
	}

	public D4mDbResultSet SAVE_ORIGINAL_getAllData() throws CBException, TableNotFoundException, CBSecurityException {
		int count =0;
		D4mDbResultSet results = new D4mDbResultSet();
		ArrayList<D4mDbRow> rowList = rowList = new ArrayList<D4mDbRow>();
		CloudbaseConnection cbConnection = new CloudbaseConnection(this.connProps);
		Scanner scanner = cbConnection.getScanner(tableName);
		scanner.fetchColumnFamily(new Text(this.family));
		//		if(this.limit > 0) 
		//			scanner.setBatchSize(limit);
		long start = System.currentTimeMillis();

		this.sbRowReturn = new StringBuilder();
		this.sbColumnReturn = new StringBuilder();
		this.sbValueReturn = new StringBuilder();

		Iterator<Entry<Key, Value>> scannerIter = scanner.iterator();
		while (scannerIter.hasNext()) {
			Entry<Key, Value> entry = (Entry<Key, Value>) scannerIter.next();
			String rowKey = entry.getKey().getRow().toString();
			String value = entry.getValue().get().toString();//new String(entry.getValue().get());
			String column = entry.getKey().getColumnQualifier().toString();//new String(entry.getKey().getColumnQualifier().toString());
			if(this.limit == 0 || count < this.limit)
				this.setReturnStrings(rowKey, column.replace(this.family, ""), value);
			else if(count > this.limit) break;
			count++;
			if (this.doTest) {
				D4mDbRow row = new D4mDbRow();
				row.setRow(rowKey);
				row.setColumn(column.replace(this.family, ""));
				row.setValue(value);
				rowList.add(row);
			}

			//sbRowReturn.append(rowKey + newline);
			//sbColumnReturn.append(column.replace(this.family, "") + newline);
			//sbValueReturn.append(value + newline);
		}

		this.setRowReturnString(sbRowReturn.toString());
		this.setColumnReturnString(sbColumnReturn.toString());
		this.setValueReturnString(sbValueReturn.toString());
		log.info("Num results = "+ count);
		double elapsed = (System.currentTimeMillis() - start);
		results.setQueryTime(elapsed / 1000);
		results.setMatlabDbRow(rowList);
		return results;
	}



	/* extractResults
	 * This method is used to extract results from the Scanner's iterator.
	 * It looks like every "query" method has the same while-loop with some caveats.
	 * 
	 * Can I have one method to iterate over the results?
	 */
	private ArrayList<D4mDbRow> extractResults (Iterator<Entry<Key, Value>> scannerIter, HashMap<String,String> rowMap, 
			boolean checkRowKey, boolean checkFinalCol) {
		//ArrayList<Entry<Key,Value>> resultsList = new ArrayList<Entry<Key,Value>>();

		//Iterator<Entry<Key, Value>> scannerIter = scanner.iterator();
		D4mDbResultSet results = new D4mDbResultSet();
		ArrayList<D4mDbRow> rowList =null;
		rowList = new ArrayList<D4mDbRow>();
		this.sbRowReturn = new StringBuilder();
		this.sbColumnReturn = new StringBuilder();
		this.sbValueReturn = new StringBuilder();

		int count=0;
		while (scannerIter.hasNext()) {
			Entry<Key, Value> entry = (Entry<Key, Value>) scannerIter.next();
			String rowKey = entry.getKey().getRow().toString();
			String column = entry.getKey().getColumnQualifier().toString(); //new String(entry.getKey().getColumnQualifier().toString());
			String value = entry.getValue().get().toString();//new String(entry.getValue().get());
			String finalColumn = column.replace(this.family, "");
			boolean saveIt=false;

			if(rowMap != null) {
				if(checkRowKey && checkFinalCol) {
					if ((rowMap.containsKey(rowKey)) && (rowMap.containsValue(finalColumn))) {
						saveIt=true;
						//count++;
					}
				}
				else if(checkRowKey && rowMap.containsKey(rowKey)) {
					saveIt = true;
					//count++;
				}
				else if(checkFinalCol && rowMap.containsValue(finalColumn)) {
					saveIt=true;
					//count++;
				}

			} else {
				//Greedy - save everything
				saveIt = true;
				//count++;
			}

			if(saveIt) {

				if (this.doTest) {
					D4mDbRow row = new D4mDbRow();
					row.setRow(rowKey);
					row.setColumn(finalColumn);
					row.setValue(value);
					rowList.add(row);
				}

				if ( this.limit == 0 || (this.limit > 0 && count < this.limit) )  {
					setReturnStrings(rowKey,finalColumn,value);
				}
				else if(count > this.limit) break;
				count++;
			}
		}
		//		System.out.println("Number of entries retrieved = "+count);

		log.info("Number of entries retrieved = "+count);
		return rowList;
	}
	public HashMap<String, String> assocColumnWithRow(String rows, String cols) {

		HashMap<String, Object> rowMap = this.processParam(rows);
		HashMap<String, Object> columnMap = this.processParam(cols);
		String[] rowArray = (String[]) rowMap.get("content");
		String[] columnArray = (String[]) columnMap.get("content");

		HashMap<String, String> resultMap = new HashMap<String, String>();
		for (int i = 0; i < rowArray.length; i++) {
			resultMap.put(rowArray[i], columnArray[i]);
		}
		return resultMap;
	}

	public HashMap<String, String> loadColumnMap(String cols) {

		HashMap<String, Object> columnMap = this.processParam(cols);
		String[] columnArray = (String[]) columnMap.get("content");

		HashMap<String, String> resultMap = new HashMap<String, String>();
		for (int i = 0; i < columnArray.length; i++) {
			resultMap.put(columnArray[i], columnArray[i]);
		}
		return resultMap;
	}

	public HashMap<String, String> loadRowMap(String rows) {

		HashMap<String, Object> rowMap = this.processParam(rows);
		String[] rowArray = (String[]) rowMap.get("content");

		HashMap<String, String> resultMap = new HashMap<String, String>();
		for (int i = 0; i < rowArray.length; i++) {
			resultMap.put(rowArray[i], rowArray[i]);
		}
		return resultMap;
	}

	public boolean isRangeQuery(String[] paramContent) {
		boolean rangeQuery = false;
		/*
		 * Range Querys are the following a,:,b, a,:,end, ,:,b, Note; Negative
		 * Infinity Range a*,
		 */

		if (paramContent.length == 1) {
			if (paramContent[0].contains("*")) {
				rangeQuery = true;
			}
		}
		if (paramContent.length == 3) {
			if (paramContent[1].contains(":")) {
				rangeQuery = true;
			}
		}
		return rangeQuery;
	}

	public String getRangeQueryType(String[] paramContent) {
		/*
		 * Range Querys are the following a,:,b, a,:,end, ,:,b, Note; Negative
		 * Infinity Range a*,
		 */
		String rangeQueryType = "";
		if (paramContent[0].contains("*")) {
			rangeQueryType = D4mDbQuery.REGEX_RANGE;
		}
		if (paramContent.length == 3) {
			if (paramContent[1].contains(":")) {
				rangeQueryType = D4mDbQuery.KEY_RANGE;
			}
		}
		if (paramContent.length == 3) {
			if (paramContent[1].contains(":") && paramContent[2].toLowerCase().contains("end")) {
				rangeQueryType = D4mDbQuery.POSITIVE_INFINITY_RANGE;
			}
			if (paramContent[1].contains(":") && paramContent[0].equals("")) {
				rangeQueryType = D4mDbQuery.NEGATIVE_INFINITY_RANGE;
			}
		}
		return rangeQueryType;
	}

	/**
	 * @param rows     row keys
	 * @param cols     column (qualifiers) 
	 * @param family   column family
	 * @param authorizations     security authorization for the user, a comma-sep
	 * @return
	 * @throws CBException
	 * @throws CBSecurityException
	 * @throws TableNotFoundException
	 */
	public D4mDbResultSet doMatlabQuery(String rows, String cols, String family, String authorizations) throws CBException, CBSecurityException, TableNotFoundException {
		this.family = family;
		connProps.setAuthorizations(authorizations.split(","));
		return doMatlabQuery(rows, cols);
	}

	private D4mDbResultSet doMatlabQuery(String rows, String cols) throws CBException, CBSecurityException, TableNotFoundException {
		int count=0;
		if ((!rows.equals(":")) && (cols.equals(":"))) {

			HashMap<String, Object> rowMap = this.processParam(rows);
			String[] paramContent = (String[]) rowMap.get("content");
			// System.out.println("this.isRangeQuery(paramContent)="+this.isRangeQuery(paramContent));
			if (this.isRangeQuery(paramContent)) {
				return this.doMatlabRangeQueryOnRows(rows, cols,rowMap);
			}
			else {
				return this.doMatlabQueryOnRows(rows, cols);
			}
		}
		if ((rows.equals(":")) && (!cols.equals(":"))) {
			return this.doMatlabQueryOnColumns(rows, cols);
		}
		if ((rows.equals(":")) && (cols.equals(":"))) {
			return this.getAllData();
		}

		HashMap<String, String> rowMap = this.assocColumnWithRow(rows, cols);
		D4mDbResultSet results = new D4mDbResultSet();
		ArrayList<D4mDbRow> rowList = null; //new ArrayList<D4mDbRow>();
		CloudbaseConnection cbConnection = new CloudbaseConnection(this.connProps);
		HashSet<Range> ranges = this.loadRanges(rowMap);
		BatchScanner scanner = null;
		try {
			scanner =cbConnection.getBatchScanner(this.tableName, this.numberOfThreads);
			scanner.fetchColumnFamily(new Text(this.family));
			scanner.setRanges(ranges);

			long start = System.currentTimeMillis();

			Iterator<Entry<Key, Value>> scannerIter = scanner.iterator();
			rowList = extractResults(scannerIter, rowMap, true,true);

			this.setRowReturnString(sbRowReturn.toString());
			this.setColumnReturnString(sbColumnReturn.toString());
			this.setValueReturnString(sbValueReturn.toString());

			double elapsed = (System.currentTimeMillis() - start);
			results.setQueryTime(elapsed / 1000);
			results.setMatlabDbRow(rowList);
		}
		finally {
			if(scanner != null)
				scanner.close();
		}
		return results;
	}


	private D4mDbResultSet SAVE_ORIGINAL_doMatlabQuery(String rows, String cols) throws CBException, CBSecurityException, TableNotFoundException {
		int count=0;
		if ((!rows.equals(":")) && (cols.equals(":"))) {

			HashMap<String, Object> rowMap = this.processParam(rows);
			String[] paramContent = (String[]) rowMap.get("content");
			// System.out.println("this.isRangeQuery(paramContent)="+this.isRangeQuery(paramContent));
			if (this.isRangeQuery(paramContent)) {
				return this.doMatlabRangeQueryOnRows(rows, cols);
			}
			else {
				return this.doMatlabQueryOnRows(rows, cols);
			}
		}
		if ((rows.equals(":")) && (!cols.equals(":"))) {
			return this.doMatlabQueryOnColumns(rows, cols);
		}
		if ((rows.equals(":")) && (cols.equals(":"))) {
			return this.getAllData();
		}

		HashMap<String, String> rowMap = this.assocColumnWithRow(rows, cols);
		D4mDbResultSet results = new D4mDbResultSet();
		ArrayList<D4mDbRow> rowList = new ArrayList<D4mDbRow>();
		CloudbaseConnection cbConnection = new CloudbaseConnection(this.connProps);
		HashSet<Range> ranges = this.loadRanges(rowMap);
		BatchScanner scanner = cbConnection.getBatchScanner(this.tableName, this.numberOfThreads);
		scanner.fetchColumnFamily(new Text(this.family));
		scanner.setRanges(ranges);

		long start = System.currentTimeMillis();
		this.sbRowReturn = new StringBuilder();
		this.sbColumnReturn = new StringBuilder();
		this.sbValueReturn = new StringBuilder();

		Iterator<Entry<Key, Value>> scannerIter = scanner.iterator();

		while (scannerIter.hasNext()) {
			Entry<Key, Value> entry = (Entry<Key, Value>) scannerIter.next();
			String rowKey = entry.getKey().getRow().toString();
			String column = entry.getKey().getColumnQualifier().toString(); //new String(entry.getKey().getColumnQualifier().toString());
			String value = entry.getValue().get().toString();//new String(entry.getValue().get());
			String finalColumn = column.replace(this.family, "");


			if ((rowMap.containsKey(rowKey)) && (rowMap.containsValue(finalColumn))) {


				if (this.doTest) {
					D4mDbRow row = new D4mDbRow();
					row.setRow(rowKey);
					row.setColumn(finalColumn);
					row.setValue(value);
					rowList.add(row);
				}
				if ( this.limit == 0 || (this.limit > 0 && count < this.limit) )  {
					setReturnStrings(rowKey,finalColumn,value);
				}
				else if(count > this.limit) break;
				//sbRowReturn.append(rowKey + newline);
				//sbColumnReturn.append(finalColumn + newline);
				//sbValueReturn.append(value + newline);
				count++;
			}
		}

		this.setRowReturnString(sbRowReturn.toString());
		this.setColumnReturnString(sbColumnReturn.toString());
		this.setValueReturnString(sbValueReturn.toString());

		double elapsed = (System.currentTimeMillis() - start);
		results.setQueryTime(elapsed / 1000);
		results.setMatlabDbRow(rowList);
		return results;
	}


	private void setReturnStrings(String rowKey, String finalColumn, String value) {
		this.sbRowReturn.append(rowKey + newline);
		this.sbColumnReturn.append(finalColumn + newline);
		this.sbValueReturn.append(value + newline);
		this.numReturnStringsProcessed++;
	}
	public D4mDbResultSet doMatlabQueryOnRows(String rows, String cols, String family, String authorizations) throws CBException, CBSecurityException, TableNotFoundException {
		this.family = family;
		connProps.setAuthorizations(authorizations.split(","));
		return doMatlabQueryOnRows(rows, cols);
	}

	private D4mDbResultSet doMatlabQueryOnRows(String rows, String cols) throws CBException, CBSecurityException, TableNotFoundException {
		int count =0;
		HashMap<String, String> rowMap = this.loadRowMap(rows);
		D4mDbResultSet results = new D4mDbResultSet();
		ArrayList<D4mDbRow> rowList = null; // new ArrayList<D4mDbRow>();
		CloudbaseConnection cbConnection = new CloudbaseConnection(this.connProps);
		HashSet<Range> ranges = this.loadRanges(rowMap);
		BatchScanner  scanner =null;
		try {
			scanner =cbConnection.getBatchScanner(this.tableName, this.numberOfThreads);
			scanner.fetchColumnFamily(new Text(this.family));
			scanner.setRanges(ranges);
			long start = System.currentTimeMillis();

			Iterator<Entry<Key, Value>> scannerIter = scanner.iterator();
			rowList = extractResults(scannerIter, rowMap, true, false);
			this.setRowReturnString(sbRowReturn.toString());
			this.setColumnReturnString(sbColumnReturn.toString());
			this.setValueReturnString(sbValueReturn.toString());

			double elapsed = (System.currentTimeMillis() - start);
			results.setQueryTime(elapsed / 1000);
			results.setMatlabDbRow(rowList);

		} 
		finally {
			scanner.close();
		}
		sbRowReturn   = null;
		sbColumnReturn= null;
		sbValueReturn = null;
		return results;
	}

	private D4mDbResultSet SAVE_ORIGINAL_doMatlabQueryOnRows(String rows, String cols) throws CBException, CBSecurityException, TableNotFoundException {
		int count =0;
		HashMap<String, String> rowMap = this.loadRowMap(rows);
		D4mDbResultSet results = new D4mDbResultSet();
		ArrayList<D4mDbRow> rowList = null; // new ArrayList<D4mDbRow>();
		CloudbaseConnection cbConnection = new CloudbaseConnection(this.connProps);
		HashSet<Range> ranges = this.loadRanges(rowMap);
		BatchScanner scanner = cbConnection.getBatchScanner(this.tableName, this.numberOfThreads);
		scanner.fetchColumnFamily(new Text(this.family));
		scanner.setRanges(ranges);
		long start = System.currentTimeMillis();

		sbRowReturn = new StringBuilder();
		sbColumnReturn = new StringBuilder();
		sbValueReturn = new StringBuilder();
		try {
			Iterator<Entry<Key, Value>> scannerIter = scanner.iterator();
			while (scannerIter.hasNext()) {
				Entry<Key, Value> entry = (Entry<Key, Value>) scannerIter.next();
				String rowKey = entry.getKey().getRow().toString();
				String column = entry.getKey().getColumnQualifier().toString();//new String(entry.getKey().getColumnQualifier().toString());
				String value = entry.getValue().get().toString();//new String(entry.getValue().get());
				String finalColumn = column.replace(this.family, "");

				if (rowMap.containsKey(rowKey)) {
					if (this.doTest) {
						D4mDbRow row = new D4mDbRow();
						row.setRow(rowKey);
						row.setColumn(finalColumn);
						row.setValue(value);
						rowList.add(row);
					}
					if ( this.limit == 0 || (this.limit > 0 && count < this.limit) )
						this.setReturnStrings(rowKey, finalColumn, value);
					else if ( count > this.limit) break;
					//sbRowReturn.append(rowKey + newline);
					//sbColumnReturn.append(finalColumn + newline);
					//sbValueReturn.append(value + newline);
					count++;
				}
			}
		} 
		finally {
			scanner.close();
		}
		this.setRowReturnString(sbRowReturn.toString());
		this.setColumnReturnString(sbColumnReturn.toString());
		this.setValueReturnString(sbValueReturn.toString());

		double elapsed = (System.currentTimeMillis() - start);
		results.setQueryTime(elapsed / 1000);
		results.setMatlabDbRow(rowList);
		sbRowReturn   = null;
		sbColumnReturn= null;
		sbValueReturn = null;
		return results;
	}


	public D4mDbResultSet doMatlabRangeQueryOnRows(String rows, String cols, String family, String authorizations) throws CBException, CBSecurityException, TableNotFoundException {
		this.family = family;
		connProps.setAuthorizations(authorizations.split(","));
		return doMatlabRangeQueryOnRows(rows, cols);
	}

	private D4mDbResultSet doMatlabRangeQueryOnRows(String rows, String cols) throws CBException, CBSecurityException, TableNotFoundException {

		HashMap<String, Object> rowMap = this.processParam(rows);
		D4mDbResultSet results = doMatlabRangeQueryOnRows(rows,cols,rowMap);

		return results;
	}

	private D4mDbResultSet doMatlabRangeQueryOnRows(String rows, String cols, HashMap<String,Object> rowMap) throws CBException, CBSecurityException, TableNotFoundException {

		//		HashMap<String, Object> rowMap = this.processParam(rows);
		String[] rowArray = (String[]) rowMap.get("content");

		HashSet<Range> ranges = new HashSet<Range>();
		CloudbaseConnection cbConnection = new CloudbaseConnection(this.connProps);
		BatchScanner scanner = null; //cbConnection.getBatchScanner(this.tableName, this.numberOfThreads);
		D4mDbResultSet results = new D4mDbResultSet();

		try {
			scanner = cbConnection.getBatchScanner(this.tableName, this.numberOfThreads);

			if (this.getRangeQueryType(rowArray).equals(D4mDbQuery.KEY_RANGE)) {
				// System.out.println("queryType="+this.KEY_RANGE+
				// " rowArray[0]="+rowArray[0]+" rowArray[2]="+rowArray[2]+"<");
				Key startKey = new Key(new Text(rowArray[0]));
				Key endKey = new Key(new Text(rowArray[2]));
				Range range = new Range(startKey, true, 
						endKey.followingKey(PartialKey.getByDepth(1)), false);
				ranges.add(range);
				scanner.setRanges(ranges);
				// Note; there is a bug in CB 1.1 for ranges including end key,
				// use "endKey.followingKey(1), false" work around
			}

			if (this.getRangeQueryType(rowArray).equals(D4mDbQuery.POSITIVE_INFINITY_RANGE)) {
				// System.out.println("queryType="+this.POSITIVE_INFINITY_RANGE+
				// " rowArray[0]="+rowArray[0]);
				Key startKey = new Key(new Text(rowArray[0]));
				Range range = new Range(startKey, true, null, true);
				ranges.add(range);
				scanner.setRanges(ranges);
			}

			if (this.getRangeQueryType(rowArray).equals(D4mDbQuery.NEGATIVE_INFINITY_RANGE)) {
				// System.out.println("queryType="+this.NEGATIVE_INFINITY_RANGE+
				// " rowArray[0]="+rowArray[0]);
				Key endKey = new Key(new Text(rowArray[2]));
				Range range = new Range(null, true, 
						endKey.followingKey(PartialKey.getByDepth(1)),//endKey.followingKey(1),
						false);
				ranges.add(range);
				scanner.setRanges(ranges);
				// Note; there is a bug in CB 1.1 for ranges including end key,
				// use "endKey.followingKey(1), false" work around
			}

			if (this.getRangeQueryType(rowArray).equals(D4mDbQuery.REGEX_RANGE)) {
			    //	System.out.println("queryType="+this.REGEX_RANGE+
			    //		" rowArray[0]="+rowArray[0]);
				String regexParams = this.regexMapper(rowArray[0]);
				scanner.setRowRegex(regexParams);
				Range range = new Range();
				ranges.add(range);
				scanner.setRanges(ranges);
			}

			ArrayList<D4mDbRow> rowList = null; //new ArrayList<D4mDbRow>();
			long start = System.currentTimeMillis();

			Iterator<Entry<Key, Value>> scannerIter = scanner.iterator();
			rowList = extractResults(scannerIter, null, false,false);
			this.setRowReturnString(sbRowReturn.toString());
			this.setColumnReturnString(sbColumnReturn.toString());
			this.setValueReturnString(sbValueReturn.toString());

			double elapsed = (System.currentTimeMillis() - start);
			results.setQueryTime(elapsed / 1000);
			results.setMatlabDbRow(rowList);

		} finally {
			if(scanner != null)
				scanner.close();
		}
		return results;
	}

	private D4mDbResultSet SAVE_ORIGINAL_doMatlabRangeQueryOnRows(String rows, String cols) throws CBException, CBSecurityException, TableNotFoundException {
		int count =0;
		HashMap<String, Object> rowMap = this.processParam(rows);
		String[] rowArray = (String[]) rowMap.get("content");

		HashSet<Range> ranges = new HashSet<Range>();
		CloudbaseConnection cbConnection = new CloudbaseConnection(this.connProps);
		BatchScanner scanner = cbConnection.getBatchScanner(this.tableName, this.numberOfThreads);

		if (this.getRangeQueryType(rowArray).equals(D4mDbQuery.KEY_RANGE)) {
			// System.out.println("queryType="+this.KEY_RANGE+
			// " rowArray[0]="+rowArray[0]+" rowArray[2]="+rowArray[2]+"<");
			Key startKey = new Key(new Text(rowArray[0]));
			Key endKey = new Key(new Text(rowArray[2]));
			Range range = new Range(startKey, true, endKey.followingKey(1), false);
			ranges.add(range);
			scanner.setRanges(ranges);
			// Note; there is a bug in CB 1.1 for ranges including end key,
			// use "endKey.followingKey(1), false" work around
		}

		if (this.getRangeQueryType(rowArray).equals(D4mDbQuery.POSITIVE_INFINITY_RANGE)) {
			// System.out.println("queryType="+this.POSITIVE_INFINITY_RANGE+
			// " rowArray[0]="+rowArray[0]);
			Key startKey = new Key(new Text(rowArray[0]));
			Range range = new Range(startKey, true, null, true);
			ranges.add(range);
			scanner.setRanges(ranges);
		}

		if (this.getRangeQueryType(rowArray).equals(D4mDbQuery.NEGATIVE_INFINITY_RANGE)) {
			// System.out.println("queryType="+this.NEGATIVE_INFINITY_RANGE+
			// " rowArray[0]="+rowArray[0]);
			Key endKey = new Key(new Text(rowArray[2]));
			Range range = new Range(null, true, endKey.followingKey(1), false);
			ranges.add(range);
			scanner.setRanges(ranges);
			// Note; there is a bug in CB 1.1 for ranges including end key,
			// use "endKey.followingKey(1), false" work around
		}

		if (this.getRangeQueryType(rowArray).equals(D4mDbQuery.REGEX_RANGE)) {
			// System.out.println("queryType="+this.REGEX_RANGE+
			// " rowArray[0]="+rowArray[0]);
			String regexParams = this.regexMapper(rowArray[0]);
			scanner.setRowRegex(regexParams);
			Range range = new Range();
			ranges.add(range);
			scanner.setRanges(ranges);
		}

		D4mDbResultSet results = new D4mDbResultSet();
		ArrayList<D4mDbRow> rowList = null; //new ArrayList<D4mDbRow>();
		long start = System.currentTimeMillis();

		sbRowReturn = new StringBuilder();
		sbColumnReturn = new StringBuilder();
		sbValueReturn = new StringBuilder();
		try {
			Iterator<Entry<Key, Value>> scannerIter = scanner.iterator();
			//rowList = extractResults(scannerIter, null, false,false);
			while (scannerIter.hasNext()) {
				Entry<Key, Value> entry = (Entry<Key, Value>) scannerIter.next();
				String rowKey = entry.getKey().getRow().toString();
				String column = entry.getKey().getColumnQualifier().toString();//new String(entry.getKey().getColumnQualifier().toString());
				String value = entry.getValue().get().toString();//new String(entry.getValue().get());
				String finalColumn = column.replace(this.family, "");

				if (this.doTest) {
					D4mDbRow row = new D4mDbRow();
					row.setRow(rowKey);
					row.setColumn(finalColumn);
					row.setValue(value);
					rowList.add(row);
				}
				if (  this.limit == 0 || count < this.limit ) 
					this.setReturnStrings(rowKey,finalColumn,value);
				else if(count > this.limit) break;
				//sbRowReturn.append(rowKey + newline);
				//sbColumnReturn.append(finalColumn + newline);
				//sbValueReturn.append(value + newline);
				count++;
			}
			this.setRowReturnString(sbRowReturn.toString());
			this.setColumnReturnString(sbColumnReturn.toString());
			this.setValueReturnString(sbValueReturn.toString());

			double elapsed = (System.currentTimeMillis() - start);
			results.setQueryTime(elapsed / 1000);
			results.setMatlabDbRow(rowList);

		} finally {
			scanner.close();
		}
		return results;
	}




	public D4mDbResultSet doMatlabQueryOnColumns(String rows, String cols, String family, String authorizations) throws CBException, CBSecurityException, TableNotFoundException {
		this.family = family;
		connProps.setAuthorizations(authorizations.split(","));
		return doMatlabQueryOnColumns(rows, cols);
	}

	private D4mDbResultSet doMatlabQueryOnColumns(String rows, String cols) throws CBException, CBSecurityException, TableNotFoundException {
		int count = 0;
		HashMap<String, String> Map = this.loadColumnMap(cols);
		D4mDbResultSet results = new D4mDbResultSet();
		ArrayList<D4mDbRow> rowList = null; //new ArrayList<D4mDbRow>();
		CloudbaseConnection cbConnection = new CloudbaseConnection(this.connProps);
		Scanner scanner = cbConnection.getScanner(tableName);
		scanner.fetchColumnFamily(new Text(this.family));
		long start = System.currentTimeMillis();

		Iterator<Entry<Key, Value>> scannerIter = scanner.iterator();
		rowList = extractResults(scannerIter, Map, false, true);
		this.setRowReturnString(sbRowReturn.toString());
		this.setColumnReturnString(sbColumnReturn.toString());
		this.setValueReturnString(sbValueReturn.toString());

		double elapsed = (System.currentTimeMillis() - start);
		results.setQueryTime(elapsed / 1000);
		results.setMatlabDbRow(rowList);
		return results;
	}

	private D4mDbResultSet SAVE_ORIGINAL_doMatlabQueryOnColumns(String rows, String cols) throws CBException, CBSecurityException, TableNotFoundException {
		int count = 0;
		HashMap<String, String> rowMap = this.loadColumnMap(cols);
		D4mDbResultSet results = new D4mDbResultSet();
		ArrayList<D4mDbRow> rowList = new ArrayList<D4mDbRow>();
		CloudbaseConnection cbConnection = new CloudbaseConnection(this.connProps);
		Scanner scanner = cbConnection.getScanner(tableName);
		scanner.fetchColumnFamily(new Text(this.family));
		long start = System.currentTimeMillis();

		sbRowReturn = new StringBuilder();
		sbColumnReturn = new StringBuilder();
		sbValueReturn = new StringBuilder();

		Iterator<Entry<Key, Value>> scannerIter = scanner.iterator();

		while (scannerIter.hasNext()) {
			Entry<Key, Value> entry = (Entry<Key, Value>) scannerIter.next();
			String rowKey = entry.getKey().getRow().toString();
			String column = entry.getKey().getColumnQualifier().toString();//new String(entry.getKey().getColumnQualifier().toString());
			String value = entry.getValue().get().toString(); // new String(entry.getValue().get());
			String finalColumn = column.replace(this.family, "");

			if (rowMap.containsValue(finalColumn)) {
				if (this.doTest) {
					D4mDbRow row = new D4mDbRow();
					row.setRow(rowKey);
					row.setColumn(finalColumn);
					row.setValue(value);
					rowList.add(row);
				}
				if (  this.limit == 0 || count < this.limit )
					this.setReturnStrings(rowKey, finalColumn, value);
				//sbRowReturn.append(rowKey + newline);
				//sbColumnReturn.append(finalColumn + newline);
				//sbValueReturn.append(value + newline);
				count++;
			}

		}
		this.setRowReturnString(sbRowReturn.toString());
		this.setColumnReturnString(sbColumnReturn.toString());
		this.setValueReturnString(sbValueReturn.toString());

		double elapsed = (System.currentTimeMillis() - start);
		results.setQueryTime(elapsed / 1000);
		results.setMatlabDbRow(rowList);
		return results;
	}


	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
		//System.out.println("^^^^^ LIMIT ="+this.limit +" ^^^^^^^");
	}

	/*
	 * Search by row and by column
	 *   row   row key is  used to set 
	 *   col   column key/qualifier to set the search criteria for the column
	 */
	public void searchByRowAndColumn(String []rows, String col) throws CBException, CBSecurityException, TableNotFoundException {
		//Set the Range
		// Create HashSet ranges
		// Set scanner for the column family and column qualifier
		//   scanner.fetchColumn(Text colFam, Text colQual)
		HashSet<Range> ranges = new HashSet<Range>();

		CloudbaseConnection cbConnection = new CloudbaseConnection(this.connProps);
		BatchScanner scanner = cbConnection.getBatchScanner(this.tableName, this.numberOfThreads);

		//		Scanner scanner = cbConnection.getScanner(tableName);
		for(String row : rows) {
			Range range = null;
			range = new Range(new Text(row));
			ranges.add(range);
		}
		try {
			//Set the range(s)
			scanner.setRanges(ranges);
			String colRegex = regexMapper(col);
			scanner.setColumnQualifierRegex(colRegex);
			//Set the column criteria
			Text columnFamily = new Text(this.family);
			//Text columnQualifier = new Text(col);
			//scanner.fetchColumn(columnFamily, columnQualifier);
			scanner.fetchColumn(columnFamily, new Text(col));
			//scanner.setColumnQualifierRegex(".");
			//Iterate through results

			Iterator<Entry<Key, Value>> scannerIter = scanner.iterator();
			while (scannerIter.hasNext()) {
				Entry<Key, Value> entry = (Entry<Key, Value>) scannerIter.next();
				String rowKey = entry.getKey().getRow().toString();
				String column = entry.getKey().getColumnQualifier().toString();//new String(entry.getKey().getColumnQualifier().toString());
				String value = entry.getValue().get().toString(); // new String(entry.getValue().get());

				log.info("Row = "+rowKey+" , column = "+column+ ", value = "+value);
			}
		}
		finally {
			scanner.close();
		}
	}
	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			System.out.println("Usage: D4mDbQuery host table rows cols");
			return;
		}

		String hostName = args[0];
		String tableName = args[1];
		String rows = args[2];
		String cols = args[3];
		Integer limit = Integer.valueOf(args[4]);

		D4mDbQuery tool = new D4mDbQuery("cloudbase", hostName, tableName, "root", "ALL4114ALL");
		tool.doTest = false;
		tool.setLimit(limit);
		D4mDbResultSet resultSet = tool.doMatlabQuery(rows, cols);
		double totalQueryTime = resultSet.getQueryTime();
		System.out.println("totalQueryTime = " + totalQueryTime);
		ArrayList<?> rowsArr = resultSet.getMatlabDbRow();

		Iterator<?> it = rowsArr.iterator();
		System.out.println("");
		System.out.println("");

		int rowsToPrint = 20 + 1;
		int counter = 0;
		if(tool.doTest) {
			while (it.hasNext()) {
				counter++;
				D4mDbRow row = (D4mDbRow) it.next();
				String rowNumber = row.getRow();
				String column = row.getColumn();
				String value = row.getValue();
				String modified = row.getModified();
				if (counter < rowsToPrint) {
					System.out.println("Row; " + rowNumber);
					System.out.println("Column; " + column);
					System.out.println("Value; " + value);
					System.out.println("Modified; " + modified);
					System.out.println("");
					System.out.println("");
				}
			}
		}
		log.info("Number of Return Strings processed = "+tool.numReturnStringsProcessed);
		System.out.println("RowReturnString=" + tool.getRowReturnString());
		System.out.println("ColumnReturnString=" + tool.getColumnReturnString());
		System.out.println("ValueReturnString=" + tool.getValueReturnString());
		System.out.println("\n\n***************************************\n");
		testSearchRowAndColumn(hostName, tableName);
		System.out.println("\n\n***************************************\n");
		System.out.println("***************************************\n");

		testGetSomeData(hostName, tableName, rows, cols, limit);
	}

	public static void testSearchRowAndColumn(String hostName, String tableName) throws CBException, CBSecurityException, TableNotFoundException {
		String [] Rows={"19960820_2286.txt"};

		String Column="NE_LOCATION";

		String ValueReturnString="[B@424ecfdd";
		D4mDbQuery tool = new D4mDbQuery("cloudbase", hostName, tableName, "root", "ALL4114ALL");
		tool.searchByRowAndColumn(Rows, Column);

	}
	public static void testGetSomeData(String hostName, String tableName, String row, String col, int limit) throws CBException, CBSecurityException, TableNotFoundException, IOException {
		D4mDbQuery tool = new D4mDbQuery("cloudbase", hostName, tableName, "root", "ALL4114ALL");
		String s= "";
		if(row.equals(":")) {

			Pattern pat = Pattern.compile("^txt^|txt.");
			row= pat.toString();
		}
		if(col.equals(":")) {
			Pattern pat = Pattern.compile(".");
			col=pat.toString();
		}

		tool.getSomeData(row, col,limit);
	}
	public HashMap<String, Object> processParam(String param) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String content = param.substring(0, param.length() - 1);
		String delim = param.replace(content, "");
		map.put("delimiter", delim);
		if (delim.equals("|")) {
			delim = "\\" + delim;
		}
		map.put("content", content.split(delim));
		map.put("length", content.length());
		return map;
	}

	public String getColumnReturnString() {
		return columnReturnString;
	}

	public void setColumnReturnString(String columnReturnString) {
		this.columnReturnString = columnReturnString;
	}

	public String getRowReturnString() {
		return rowReturnString;
	}

	public void setRowReturnString(String rowReturnString) {
		this.rowReturnString = rowReturnString;
	}

	public String getValueReturnString() {
		return valueReturnString;
	}

	public void setValueReturnString(String valueReturnString) {
		this.valueReturnString = valueReturnString;
	}

	public void setAuthorizations(String[] authorizations) {
		this.connProps.setAuthorizations(authorizations);
	}

	public HashSet<Range> loadRanges(HashMap<String, String> queryMap) {
		HashSet<Range> ranges = new HashSet<Range>();
		Iterator<String> it = queryMap.keySet().iterator();
		while (it.hasNext()) {
			String rowId = (String) it.next();
			Key key = new Key(new Text(rowId));
			Range range = new Range(key, true, key.followingKey(1), false);
			ranges.add(range);
		}
		return ranges;
	}

	private String regexMapper(String regex) {

		String charStr = regex.replace("*", "");
		String reg = "^" + charStr + "*|^" + charStr + ".";
		return reg;
	}
}
/*
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 
 * % D4M: Dynamic Distributed Dimensional Data Model 
 * % MIT Lincoln Laboratory
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 
 * % (c) <2010>  Massachusetts Institute of Technology
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 */

