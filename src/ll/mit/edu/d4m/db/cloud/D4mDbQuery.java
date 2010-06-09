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
    public final String newline = System.getProperty("line.separator");
    public boolean doTest = false;
    private static final String NUMERIC_RANGE = "NUMERIC_RANGE";
    private static final String KEY_RANGE = "KEY_RANGE";
    private static final String REGEX_RANGE = "REGEX_RANGE";
    private static final String POSITIVE_INFINITY_RANGE = "POSITIVE_INFINITY_RANGE";
    private static final String NEGATIVE_INFINITY_RANGE = "NEGATIVE_INFINITY_RANGE";
    private static final String COLON = ":";

    private D4mDbQuery() {
    }

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
        this.delimiter = newline;
        HashMap resultMap = new HashMap();
        for (int i = 0; i < rowArray.length; i++) {
            resultMap.put(rowArray[i], rowArray[i]);
        }
        return resultMap;
    }

    public boolean isRangeQuery(String[] paramContent) {
        boolean rangeQuery = false;
        /*
        Range Querys are the following
        a,:,b,
        a,:,end,
        ,:,b,    Note; Negative Infinity Range
        a*,
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
        Range Querys are the following
        a,:,b,
        a,:,end,
        ,:,b,    Note; Negative Infinity Range
        a*,
         */
        String rangeQueryType = "";
        if (paramContent[0].contains("*")) {
            rangeQueryType = this.REGEX_RANGE;
        }
        if (paramContent.length == 3) {
            if (paramContent[1].contains(":")) {
                rangeQueryType = this.KEY_RANGE;
            }
        }
        if (paramContent.length == 3) {
            if (paramContent[1].contains(":") && paramContent[2].toLowerCase().contains("end")) {
                rangeQueryType = this.POSITIVE_INFINITY_RANGE;
            }
            if(paramContent[1].contains(":") && paramContent[0].equals("")) {
                rangeQueryType = this.NEGATIVE_INFINITY_RANGE;
            }
        }
        return rangeQueryType;
    }

    public D4mDbResultSet doMatlabQuery(String rowString, String columnString) throws CBException, CBSecurityException, TableNotFoundException {

        if ((!rowString.equals(":")) && (columnString.equals(":"))) {

            HashMap rowMap = this.processParam(rowString);
            String[] paramContent = (String[]) rowMap.get("content");
            //System.out.println("this.isRangeQuery(paramContent)="+this.isRangeQuery(paramContent));
            if (this.isRangeQuery(paramContent)) {
                return this.doMatlabRangeQueryOnRows(rowString, columnString);
            } else {
                return this.doMatlabQueryOnRows(rowString, columnString);
            }
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

    public D4mDbResultSet doMatlabRangeQueryOnRows(String rowString, String columnString) throws CBException, CBSecurityException, TableNotFoundException {

        HashMap rowMap = this.processParam(rowString);
        String[] rowArray = (String[]) rowMap.get("content");

        HashSet<Range> ranges = new HashSet<Range>();
        CloudbaseConnection cbConnection = new CloudbaseConnection(this.instance, this.host, this.userName, this.password);
        BatchScanner scanner = cbConnection.getBatchScanner(this.tableName, this.numberOfThreads);

        if (this.getRangeQueryType(rowArray).equals(this.KEY_RANGE)) {
            //System.out.println("queryType="+this.KEY_RANGE+ " rowArray[0]="+rowArray[0]+" rowArray[2]="+rowArray[2]+"<");
            Key startKey = new Key(new Text(rowArray[0]));
            Key endKey = new Key(new Text(rowArray[2]));
            Range range = new Range(startKey, true, endKey.followingKey(1), false);
            ranges.add(range);
            scanner.setRanges(ranges);
            // Note; there is a bug in CB 1.1 for ranges including end key,
            // use "endKey.followingKey(1), false" work around
        }

        if (this.getRangeQueryType(rowArray).equals(this.POSITIVE_INFINITY_RANGE)) {
            //System.out.println("queryType="+this.POSITIVE_INFINITY_RANGE+ " rowArray[0]="+rowArray[0]);
            Key startKey = new Key(new Text(rowArray[0]));
            Range range = new Range(startKey, true, null, true);
            ranges.add(range);
            scanner.setRanges(ranges);
        }

        if (this.getRangeQueryType(rowArray).equals(this.NEGATIVE_INFINITY_RANGE)) {
            //System.out.println("queryType="+this.NEGATIVE_INFINITY_RANGE+ " rowArray[0]="+rowArray[0]);
            Key endKey = new Key(new Text(rowArray[2]));
            Range range = new Range(null, true, endKey.followingKey(1), false);
            ranges.add(range);
            scanner.setRanges(ranges);
            // Note; there is a bug in CB 1.1 for ranges including end key,
            // use "endKey.followingKey(1), false" work around
        }

        if (this.getRangeQueryType(rowArray).equals(this.REGEX_RANGE)) {
            //System.out.println("queryType="+this.REGEX_RANGE+ " rowArray[0]="+rowArray[0]);
            String regexParams = this.regexMapper(rowArray[0]);
            scanner.setRowRegex(regexParams);
            Range range = new Range();
            ranges.add(range);
            scanner.setRanges(ranges);
        }

        D4mDbResultSet results = new D4mDbResultSet();
        ArrayList rowList = new ArrayList();
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

        System.out.println("RowReturnString=" + tool.getRowReturnString());
        System.out.println("ColumnReturnString=" + tool.getColumnReturnString());
        System.out.println("ValueReturnString=" + tool.getValueReturnString());
    }

    public HashMap processParam(String param) {
        HashMap map = new HashMap();
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

    private String regexMapper(String regex) {

        String charStr = regex.replace("*", "");
        String reg = "^"+charStr+"*|^"+charStr+".";
        return reg;
    }

}

