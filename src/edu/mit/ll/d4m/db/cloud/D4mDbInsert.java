package edu.mit.ll.d4m.db.cloud;

import java.util.logging.Level;
import java.util.logging.Logger;
import edu.mit.ll.cloud.connection.CloudbaseConnection;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.MutationsRejectedException;
import cloudbase.core.client.TableExistsException;
import cloudbase.core.client.TableNotFoundException;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.hadoop.io.Text;
import cloudbase.core.client.BatchWriter;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Value;
import java.util.Date;
import java.util.HashMap;

/**
 * @author William Smith
 */
public class D4mDbInsert {

    static String hostName = "localhost";
    static String userName = "root";
    static String password = "secret";
    static String tableName = "";
    static String instance = "cloudbase";
    String startVertexString = "";
    String endVertexString = "";
    String weightString = "";
    static final boolean doTest = false;
    static final boolean printOutput = false;
    static final int maxMutationsToCache = 10000;
    static final int numThreads = 50;

    private D4mDbInsert() {
    }

    public D4mDbInsert(String hostName, String tableName, String startVertexString, String endVertexString, String weightString) throws CBException, CBSecurityException, TableExistsException {
        this.hostName = hostName;
        this.tableName = tableName;
        this.startVertexString = startVertexString;
        this.endVertexString = endVertexString;
        this.weightString = weightString;
    }

    public D4mDbInsert(String instance, String hostName, String tableName, String startVertexString, String endVertexString, String weightString) throws CBException, CBSecurityException, TableExistsException {
        this.instance = instance;
        this.hostName = hostName;
        this.tableName = tableName;
        this.startVertexString = startVertexString;
        this.endVertexString = endVertexString;
        this.weightString = weightString;
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

        D4mDbInsert ci = new D4mDbInsert(hostName, tableName, startVertexString, endVertexString, weightString);
        ci.doProcessing();
    }

    public void doProcessing() throws IOException, CBException, CBSecurityException, TableNotFoundException, MutationsRejectedException {

        //this.doLoadTest();
        this.createTable();
        Date startDate = new Date();
        long start = System.currentTimeMillis();

        CloudbaseConnection cbConnection = new CloudbaseConnection(this.instance, this.hostName, this.userName, this.password);
        BatchWriter batchWriter = cbConnection.getBatchWriter(tableName);

        HashMap startVertexMap = this.processParam(startVertexString);
        HashMap endVertexMap = this.processParam(endVertexString);
        HashMap weightMap = this.processParam(weightString);

        String[] startVertexArr = (String[]) startVertexMap.get("content");
        String[] endVertexArr = (String[]) endVertexMap.get("content");
        String[] weightArr = (String[]) weightMap.get("content");

        for (int i = 0; i < startVertexArr.length; i++) {

            String startVertexValue = startVertexArr[i];
            String endVertexValue = endVertexArr[i];
            String weightValue = weightArr[i];

            Text columnFamily = new Text("vertexfamily");
            Text columnQualifier = new Text("vertexfamilyValue:" + endVertexValue);
            Mutation m = new Mutation(new Text(startVertexValue));
            m.put(columnFamily, columnQualifier, new Value(weightValue.getBytes()));
            batchWriter.addMutation(m);
            m = null;
        }
        batchWriter.close();

        double elapsed = (System.currentTimeMillis() - start);
        Date endDate = new Date();
        long endSeconds = System.currentTimeMillis();
        System.out.println("Time = " + elapsed / 1000 + "," + start / 1000 + "," + endSeconds / 1000 + "," + startDate + "," + endDate);
    }

    public HashMap processParam(String param) {
        HashMap map = new HashMap();
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
                CloudbaseConnection cbConnection = new CloudbaseConnection(this.instance, this.hostName, this.userName, this.password);
                cbConnection.createTable(tableName);
            } catch (TableExistsException ex) {
                System.out.println("Table already exists.");
            }
        }
    }

    public boolean doesTableExistFromMetadata(String tableName) {
        boolean exist = false;
        D4mDbInfo info = new D4mDbInfo(this.hostName);
        String tableNames = "";
        try {
            tableNames = info.getTableList();
            if (tableNames.contains(tableName)) {
                exist = true;
            }

        } catch (CBException ex) {
            Logger.getLogger(D4mDbInsert.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CBSecurityException ex) {
            Logger.getLogger(D4mDbInsert.class.getName()).log(Level.SEVERE, null, ex);
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
}
/*
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
*/

