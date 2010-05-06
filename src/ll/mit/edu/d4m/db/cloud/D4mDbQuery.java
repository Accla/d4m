package ll.mit.edu.d4m.db.cloud;

import cloudbase.core.client.BatchScanner;
import ll.mit.edu.cloud.connection.CloudbaseConnection;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.TableNotFoundException;
import java.util.Map.Entry;
import org.apache.hadoop.io.Text;
import cloudbase.core.client.Scanner;
import cloudbase.core.data.Key;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


/**
 * @author William Smith
 */

public class D4mDbQuery {

    private String host = "localhost";
    private String userName = "root";
    private String password = "secret";
    private String tableName = "";
    private String delimiter = "";
    private String instance = "cloudbase";
    private int numberOfThreads = 50;
    public String rowReturnString = "";
    public String columnReturnString = "";
    public String valueReturnString = "";
    public String newline = System.getProperty("line.separator");
    public boolean doTest = false;


    private D4mDbQuery() {}

    public D4mDbQuery(String table) {
        this.tableName = table;
    }

    public D4mDbQuery(String host, String table) {
        this.host = host;
        this.tableName = table;
    }

    public D4mDbQuery(String instance, String host, String table) {
        this.instance = instance;
        this.host = host;
        this.tableName = table;
    }

    public D4mDbResultSet getAllData() throws CBException, TableNotFoundException, CBSecurityException {

        D4mDbResultSet results = new D4mDbResultSet();
        ArrayList rowList = new ArrayList();
        CloudbaseConnection cbConnection = new CloudbaseConnection(this.instance, this.host, this.userName, this.password);
        Scanner scanner = cbConnection.getScanner(tableName);
        long start = System.currentTimeMillis();

        StringBuilder sbRowReturn = new StringBuilder();
        StringBuilder sbColumnReturn = new StringBuilder();
        StringBuilder sbValueReturn = new StringBuilder();
        this.delimiter = ":";

        Iterator scannerIter = scanner.iterator();
        while (scannerIter.hasNext()) {
            Entry<Key, Value> entry = (Entry<Key, Value>) scannerIter.next();
            String rowKey = entry.getKey().getRow().toString();
            String value = new String(entry.getValue().get());
            String column = new String(entry.getKey().getColumnQualifier().toString());

            if (this.doTest) {
                D4mDbRow row = new D4mDbRow();
                row.setRow(rowKey);
                row.setColumn(column.replace("vertexfamilyValue:", ""));
                row.setValue(value);
                rowList.add(row);
            }

            sbRowReturn.append(rowKey + newline);
            sbColumnReturn.append(column.replace("vertexfamilyValue:", "") + newline);
            sbValueReturn.append(value + newline);
        }

        this.setRowReturnString(sbRowReturn.toString());
        this.setColumnReturnString(sbColumnReturn.toString());
        this.setValueReturnString(sbValueReturn.toString());

        double elapsed = (System.currentTimeMillis() - start);
        results.setQueryTime(elapsed / 1000);
        results.setMatlabDbRow(rowList);
        return results;
    }

    public HashMap assocColumnWithRow(String rowString, String columnString) {

        HashMap rowMap = this.processParam(rowString);
        HashMap columnMap = this.processParam(columnString);

        String[] rowArray = (String[]) rowMap.get("content");
        String[] columnArray = (String[]) columnMap.get("content");
        //String rowMapDelimiter = (String) rowMap.get("delimiter");
        //String colMapDelimiter = (String) columnMap.get("delimiter");
        this.delimiter = newline;
        HashMap resultMap = new HashMap();
        for (int i = 0; i < rowArray.length; i++) {
            resultMap.put(rowArray[i], columnArray[i]);
        }
        return resultMap;

    }

    public HashMap loadColumnMap(String columnString) {

        HashMap columnMap = this.processParam(columnString);
        String[] columnArray = (String[]) columnMap.get("content");
        //String colMapDelimiter = (String) columnMap.get("delimiter");
        this.delimiter = newline;
        HashMap resultMap = new HashMap();
        for (int i = 0; i < columnArray.length; i++) {
            resultMap.put(columnArray[i], columnArray[i]);
        }
        return resultMap;

    }

    public HashMap loadRowMap(String rowString) {

        HashMap rowMap = this.processParam(rowString);
        String[] rowArray = (String[]) rowMap.get("content");
        //String rowMapDelimiter = (String) rowMap.get("delimiter");
        this.delimiter = newline;
        HashMap resultMap = new HashMap();
        for (int i = 0; i < rowArray.length; i++) {
            resultMap.put(rowArray[i], rowArray[i]);
        }
        return resultMap;

    }

    public D4mDbResultSet doMatlabQuery(String rowString, String columnString) throws CBException, CBSecurityException, TableNotFoundException {

        if ((!rowString.equals(":")) && (columnString.equals(":"))) {
            return this.doMatlabQueryOnRows(rowString, columnString);
        }
        if ((rowString.equals(":")) && (!columnString.equals(":"))) {
            return this.doMatlabQueryOnColumns(rowString, columnString);
        }
        if ((rowString.equals(":")) && (columnString.equals(":"))) {
            return this.getAllData();
        }

        HashMap rowMap = this.assocColumnWithRow(rowString, columnString);
        D4mDbResultSet results = new D4mDbResultSet();
        ArrayList rowList = new ArrayList();
        CloudbaseConnection cbConnection = new CloudbaseConnection(this.instance, this.host, this.userName, this.password);
        HashSet ranges = this.loadRanges(rowMap);
        BatchScanner scanner = cbConnection.getBatchScanner(this.tableName, this.numberOfThreads);
        scanner.setRanges(ranges);

        long start = System.currentTimeMillis();
        StringBuilder sbRowReturn = new StringBuilder();
        StringBuilder sbColumnReturn = new StringBuilder();
        StringBuilder sbValueReturn = new StringBuilder();

        Iterator scannerIter = scanner.iterator();
        while (scannerIter.hasNext()) {
            Entry<Key, Value> entry = (Entry<Key, Value>) scannerIter.next();
            String rowKey = entry.getKey().getRow().toString();
            String column = new String(entry.getKey().getColumnQualifier().toString());
            String value = new String(entry.getValue().get());
            String finalColumn = column.replace("vertexfamilyValue:", "");

            if ((rowMap.containsKey(rowKey)) && (rowMap.containsValue(finalColumn))) {

                if (this.doTest) {
                    D4mDbRow row = new D4mDbRow();
                    row.setRow(rowKey);
                    row.setColumn(finalColumn);
                    row.setValue(value);
                    rowList.add(row);
                }

                sbRowReturn.append(rowKey + newline);
                sbColumnReturn.append(finalColumn + newline);
                sbValueReturn.append(value + newline);
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

    public D4mDbResultSet doMatlabQueryOnRows(String rowString, String columnString) throws CBException, CBSecurityException, TableNotFoundException {

        HashMap rowMap = this.loadRowMap(rowString);
        D4mDbResultSet results = new D4mDbResultSet();
        ArrayList rowList = new ArrayList();
        CloudbaseConnection cbConnection = new CloudbaseConnection(this.instance, this.host, this.userName, this.password);
        HashSet ranges = this.loadRanges(rowMap);
        BatchScanner scanner = cbConnection.getBatchScanner(this.tableName, this.numberOfThreads);
        scanner.setRanges(ranges);
        long start = System.currentTimeMillis();

        StringBuilder sbRowReturn = new StringBuilder();
        StringBuilder sbColumnReturn = new StringBuilder();
        StringBuilder sbValueReturn = new StringBuilder();

        Iterator scannerIter = scanner.iterator();
        while (scannerIter.hasNext()) {
            Entry<Key, Value> entry = (Entry<Key, Value>) scannerIter.next();
            String rowKey = entry.getKey().getRow().toString();
            String column = new String(entry.getKey().getColumnQualifier().toString());
            String value = new String(entry.getValue().get());
            String finalColumn = column.replace("vertexfamilyValue:", "");

            if (rowMap.containsKey(rowKey)) {

                if (this.doTest) {
                    D4mDbRow row = new D4mDbRow();
                    row.setRow(rowKey);
                    row.setColumn(finalColumn);
                    row.setValue(value);
                    rowList.add(row);
                }

                sbRowReturn.append(rowKey + newline);
                sbColumnReturn.append(finalColumn + newline);
                sbValueReturn.append(value + newline);
            }

        }
        scanner.close();
        this.setRowReturnString(sbRowReturn.toString());
        this.setColumnReturnString(sbColumnReturn.toString());
        this.setValueReturnString(sbValueReturn.toString());

        double elapsed = (System.currentTimeMillis() - start);
        results.setQueryTime(elapsed / 1000);
        results.setMatlabDbRow(rowList);
        return results;
    }

    public D4mDbResultSet doMatlabQueryOnColumns(String rowString, String columnString) throws CBException, CBSecurityException, TableNotFoundException {

        HashMap rowMap = this.loadColumnMap(columnString);
        D4mDbResultSet results = new D4mDbResultSet();
        ArrayList rowList = new ArrayList();
        CloudbaseConnection cbConnection = new CloudbaseConnection(this.instance, this.host, this.userName, this.password);
        Scanner scanner = cbConnection.getScanner(tableName);
        long start = System.currentTimeMillis();

        StringBuilder sbRowReturn = new StringBuilder();
        StringBuilder sbColumnReturn = new StringBuilder();
        StringBuilder sbValueReturn = new StringBuilder();

        Iterator scannerIter = scanner.iterator();
        while (scannerIter.hasNext()) {
            Entry<Key, Value> entry = (Entry<Key, Value>) scannerIter.next();
            String rowKey = entry.getKey().getRow().toString();
            String column = new String(entry.getKey().getColumnQualifier().toString());
            String value = new String(entry.getValue().get());
            String finalColumn = column.replace("vertexfamilyValue:", "");

            if (rowMap.containsValue(finalColumn)) {

                if (this.doTest) {
                    D4mDbRow row = new D4mDbRow();
                    row.setRow(rowKey);
                    row.setColumn(finalColumn);
                    row.setValue(value);
                    rowList.add(row);
                }

                sbRowReturn.append(rowKey + newline);
                sbColumnReturn.append(finalColumn + newline);
                sbValueReturn.append(value + newline);
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

    public static void main(String[] args) throws CBException, CBSecurityException, TableNotFoundException {

        if (args.length < 1) {
            System.out.println("Usage: D4mDbQuery host table rowString colString");
            return;
        }

        String hostName = args[0];
        String tableName = args[1];
        String rowString = args[2];
        String colString = args[3];

        D4mDbQuery tool = new D4mDbQuery(hostName, tableName);
        tool.doTest = false;
        D4mDbResultSet resultSet = tool.doMatlabQuery(rowString, colString);
        double totalQueryTime = resultSet.getQueryTime();
        int resultSize = resultSet.getTotalResultSize();
        System.out.println("totalQueryTime = " + totalQueryTime);
        ArrayList rows = resultSet.getMatlabDbRow();

        Iterator it = rows.iterator();
        System.out.println("");
        System.out.println("");

        int rowsToPrint = 20 + 1;
        int counter = 0;
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

    System.out.println("RowReturnString="+tool.getRowReturnString());
    System.out.println("ColumnReturnString="+tool.getColumnReturnString());
    System.out.println("ValueReturnString="+tool.getValueReturnString());
    }

    public HashMap processParam(String param) {
        HashMap map = new HashMap();
        String content = param.substring(0, param.length() - 1);
        String delim = param.replace(content, "");
        map.put("delimiter", delim);
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

    public HashSet<Range> loadRanges(HashMap<String, String> queryMap) {
        HashSet<Range> ranges = new HashSet<Range>();
        Iterator it = queryMap.keySet().iterator();
        while (it.hasNext()) {
            String rowId = (String) it.next();
            Key key = new Key(new Text(rowId));
            Range range = new Range(key, true, key.followingKey(1), false);
            ranges.add(range);
        }
        return ranges;
    }

}

