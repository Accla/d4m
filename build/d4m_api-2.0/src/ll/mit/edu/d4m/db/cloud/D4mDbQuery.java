package ll.mit.edu.d4m.db.cloud;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import cloudbase.core.client.BatchScanner;
import java.nio.charset.CharacterCodingException;
import ll.mit.edu.cloud.connection.CloudbaseConnection;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.master.MasterNotRunningException;
import java.util.Map.Entry;
import org.apache.hadoop.io.Text;
import cloudbase.core.client.Scanner;
import cloudbase.core.data.Key;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeMap;

/**
 *
 * @author wi20909
 */

public class D4mDbQuery
{


    public D4mDbQuery() {}

    private String host = "localhost";
    private String userName = "root";
    private String password = "secret";
    private String tableName = "";
    private String delimiter = "";

    public String rowReturnString    = "";
    public String columnReturnString = "";
    public String valueReturnString  = "";


    public D4mDbQuery(String table) {
        this.tableName = table;
    }

    public D4mDbQuery(String host, String table) {
        this.host = host;
        this.tableName = table;
    }

    public D4mDbResultSet getAllData() throws CBException, TableNotFoundException, CBSecurityException {


        D4mDbResultSet results = new D4mDbResultSet();
        ArrayList rowList = new ArrayList();
        CloudbaseConnection cbConnection = new CloudbaseConnection(this.host, this.userName, this.password);
        Scanner scanner = cbConnection.getScanner(tableName);

        Text t = null;
        Date startDate = new Date();
        long start = System.currentTimeMillis();
        Iterator scannerIter = scanner.iterator();

        while (scannerIter.hasNext()) {
            Entry<Key, Value> entry = (Entry<Key, Value>) scannerIter.next();

            String rowKey = new String(entry.getKey().toStringNoTime());
            String value = new String(entry.getValue().get());
            String column = new String(entry.getKey().getColumnQualifier().toString());

            String[] finalRowKey = rowKey.split(" ");

            D4mDbRow row = new D4mDbRow();
            row.setRow(finalRowKey[0]);
            row.setColumn(column.replace("vertexfamilyValue:", ""));
            row.setValue(value);
            rowList.add(row);

        }
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
        String delimiter = (String) rowMap.get("delimiter");
        this.delimiter = delimiter;

        HashMap resultMap = new HashMap();

        for (int i = 0; i < rowArray.length; i++) {
            resultMap.put(rowArray[i], columnArray[i]);
        }
        return resultMap;

    }

    public D4mDbResultSet doMatlabQuery(String rowString, String columnString) throws CBException, CBSecurityException, TableNotFoundException {

        HashMap rowMap = this.assocColumnWithRow(rowString, columnString);
        String delim = (String) rowMap.get("delimiter");

        D4mDbResultSet results = new D4mDbResultSet();
        ArrayList rowList = new ArrayList();
        CloudbaseConnection cbConnection = new CloudbaseConnection(this.host, this.userName, this.password);
        Scanner scanner = cbConnection.getScanner(tableName);

        Text t = null;
        Date startDate = new Date();
        long start = System.currentTimeMillis();

        StringBuilder sbRowReturn = new StringBuilder();
        StringBuilder sbColumnReturn = new StringBuilder();
        StringBuilder sbValueReturn = new StringBuilder();

        int counter = 0;

        Iterator scannerIter = scanner.iterator();

        while (scannerIter.hasNext()) {
            Entry<Key, Value> entry = (Entry<Key, Value>) scannerIter.next();
            String rowKey = entry.getKey().getRow().toString();

            String entityCategory = entry.getKey().getColumnFamily().toString();
            String column = new String(entry.getKey().getColumnQualifier().toString());
            String value = new String(entry.getValue().get());
            counter++;

            String finalColumn = column.replace("vertexfamilyValue:", "");
            String[] finalRowKey = rowKey.split(" ");


            if ((rowMap.containsKey(finalRowKey[0])) && (rowMap.containsValue(finalColumn))) {
                D4mDbRow row = new D4mDbRow();
                row.setRow(finalRowKey[0]);
                row.setColumn(finalColumn);
                row.setValue(value);
                rowList.add(row);

                sbRowReturn.append(finalRowKey[0]+this.delimiter);
                sbColumnReturn.append(finalColumn+this.delimiter);
                sbValueReturn.append(value+this.delimiter);
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

    /***
    public D4mDbResultSet doMatlabTestQuery(String rowString, String columnString) {

        HashMap rowMap = this.assocColumnWithRow(rowString, columnString);

        D4mDbResultSet results = new D4mDbResultSet();
        ArrayList rowList = new ArrayList();
        Scanner scanner = new Scanner(table);

        Text t = null;
        Date startDate = new Date();
        long start = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        int counter = 0;

        while (scanner.hasNext()) {
            Entry<IKey, ImmutableBytesWritable> entry = scanner.next();
            counter++;

            String rowKey = new String(entry.getKey().toStringNoTime());
            String value = new String(entry.getValue().get());
            String column = new String(entry.getKey().getColumn().toString());
            String finalColumn = column.replace("vertexfamily:", "");
            String[] finalRowKey = rowKey.split(" ");


            if ((rowMap.containsKey(finalRowKey[0])) && (rowMap.containsValue(finalColumn))) {
                D4mDbRow row = new D4mDbRow();
                row.setRow(finalRowKey[0]);
                row.setColumn(finalColumn);
                row.setValue(value);
                rowList.add(row);
            }

        }
        double elapsed = (System.currentTimeMillis() - start);
        results.setQueryTime(elapsed / 1000);
        results.setMatlabDbRow(rowList);

        if(this.table!=null) { this.table.close(); }

        return results;
    }
******/

    public static void main(String[] args) throws CBException, CBSecurityException, TableNotFoundException {
        if (args.length < 1) {
            System.out.println("Usage: D4mDbQuery host table rowString colString");
            return;
        }


        String hostName =  args[0];
        String tableName = args[1];
        String rowString = args[2];
        String colString = args[3];

        Date searchDate = new Date();

        D4mDbQuery tool = new D4mDbQuery(hostName, tableName);
        D4mDbResultSet resultSet = tool.doMatlabQuery(rowString, colString);
        double totalQueryTime = resultSet.getQueryTime();
        int resultSize = resultSet.getTotalResultSize();

        ArrayList rows = resultSet.getMatlabDbRow();


        Iterator it = rows.iterator();
        System.out.println("");
        System.out.println("");
        String firstResult = "";


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

        

}

