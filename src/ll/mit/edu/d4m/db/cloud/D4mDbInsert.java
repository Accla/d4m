package ll.mit.edu.d4m.db.cloud;


import java.util.logging.Level;
import java.util.logging.Logger;
import ll.mit.edu.cloud.connection.CloudbaseConnection;
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
import cloudbase.core.master.MasterNotRunningException;
import java.io.StringReader;
import java.lang.CharSequence;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;

public class D4mDbInsert {

    static String hostName = "localhost";
    static String userName = "root";
    static String password = "secret";
    static String tableName = "";

    String startVertexString = "";
    String endVertexString = "";
    String weightString = "";
    static final boolean doTest = false;
    static final boolean printOutput = true;
    static final int maxMutationsToCache = 10000;
    static final int numThreads = 50;




    public D4mDbInsert(String hostName, String tableName, String startVertexString, String endVertexString, String weightString) throws CBException, CBSecurityException, TableExistsException {
        this.hostName = hostName;
        this.tableName = tableName;
        
        this.startVertexString = startVertexString;
        this.endVertexString = endVertexString;
        this.weightString = weightString;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, CBException, CBSecurityException, TableNotFoundException, MutationsRejectedException, TableExistsException {
        if (args.length < 3) {
            return;
        }

        String hostName = args[0];
        String tableName = args[1];
        String startVertexString = args[2];
        String endVertexString = args[3];
        String weightString = args[4];

        /***
        hostName="localhost";
        tableName="test_table20";
        startVertexString="";
        endVertexString="";
        weightString="";
         * **/


        D4mDbInsert ci = new D4mDbInsert(hostName, tableName, startVertexString, endVertexString, weightString);
        ci.doProcessing();

    }

    @SuppressWarnings("static-access")
    public void doProcessing() throws IOException, CBException, CBSecurityException, TableNotFoundException, MutationsRejectedException {

        if (doTest) {
            System.out.println("starting ingester");
            System.out.println("arg 1 = " + this.hostName);
            System.out.println("arg 2 = " + this.tableName);
            System.out.println("arg 3 = " + this.startVertexString);
            System.out.println("arg 4 = " + this.endVertexString);
            System.out.println("arg 5 = " + this.weightString);
        }

        //this.doLoadTest();
        this.createTable();

        CloudbaseConnection cbConnection = new CloudbaseConnection(this.hostName, this.userName, this.password);
        BatchWriter batchWriter = cbConnection.getBatchWriter(tableName);

        int startVertexLength = startVertexString.length();
        int endVertexLength = endVertexString.length();
        int weightLength = weightString.length();

        int[] lengths = {startVertexLength, endVertexLength, weightLength};
        Arrays.sort(lengths);
        int BUFF_SIZE = lengths[lengths.length-1];

        char[] cBuf1 = new char[BUFF_SIZE];
        char[] cBuf2 = new char[BUFF_SIZE];
        char[] cBuf3 = new char[BUFF_SIZE];

        String contentStartVertex = startVertexString.substring(0, startVertexString.length() - 1);
        String contentEndVertex = endVertexString.substring(0, endVertexString.length() - 1);
        String contentWeight = weightString.substring(0, weightString.length() - 1);

        /**
        String delimiterStartVertexIndex = startVertexString.replace(contentStartVertex, "");
        String delimiterEndVertexIndex = endVertexString.replace(contentEndVertex, "");
        String delimiterWeightIndex = weightString.replace(contentWeight, "");
        **/

        String delimiterStartVertexIndex = startVertexString.substring(startVertexString.length() - 1, startVertexString.length());
        String delimiterEndVertexIndex = endVertexString.substring(endVertexString.length() - 1, endVertexString.length());
        String delimiterWeightIndex = weightString.substring(weightString.length() - 1, weightString.length());

        char delimiterStartVertex = delimiterStartVertexIndex.charAt(0);
        char delimiterEndVertex = delimiterEndVertexIndex.charAt(0);
        char delimiterWeight = delimiterWeightIndex.charAt(0);

        //System.out.println("contentStartVertex=>"+contentStartVertex+"<");
        //System.out.println("BUFF_SIZE=>"+BUFF_SIZE+"<");
        //System.out.println("delimiterStartVertexIndex=>"+delimiterStartVertexIndex+"<");
        //System.out.println("delimiterStartVertex=>"+delimiterStartVertex+"<");

        StringReader reader1 = new StringReader(contentStartVertex);
        StringReader reader2 = new StringReader(contentEndVertex);
        StringReader reader3 = new StringReader(contentWeight);

        int rowsInserted = 0;
        int rowsProcessedForInsertion = 0;
        int rowsRemovedFromHash = 0;
        int rowNumber = 1;

        Date startDate = null;
        long start = 0;

        startDate = new Date();
        start = System.currentTimeMillis();

        boolean processed = false;
        int endChar = BUFF_SIZE;
        int startChar = 0;
        Hashtable resultsTable = new Hashtable();

        int rowNumberStartVertex = 0;
        int rowNumberEndVertex = 0;
        int rowNumberWeight = 0;

        StringBuilder tmpSB = new StringBuilder();
        StringBuilder tmpSB2 = new StringBuilder();
        StringBuilder tmpSB3 = new StringBuilder();
        char c = '\0';

         while (!processed) {

            int len1 = reader1.read(cBuf1, startChar, endChar);
            int len2 = reader2.read(cBuf2, startChar, endChar);
            int len3 = reader3.read(cBuf3, startChar, endChar);

            for (int i = 0; i < BUFF_SIZE; i++) {

                char cBuf1Val = cBuf1[i];
                char cBuf2Val = cBuf2[i];
                char cBuf3Val = cBuf3[i];

                //System.out.println("cBuf3Val=" + cBuf3Val);
                //System.out.println("rowNumberStartVertex+_s=" + rowNumberStartVertex + "_s" + " VAL=" + tmpSB.toString());

                if (cBuf1Val != delimiterStartVertex && cBuf1Val!=c) {
                    tmpSB.append(cBuf1Val);
                    
                    //System.out.println("APPENDING");
                    //System.out.println("cBuf1Val=" + cBuf1Val);
                    //System.out.println("delimiterStartVertex=" + delimiterStartVertex);
                    //System.out.println("tmpSB.toString()=" + tmpSB.toString().replace(" " , ""));
                    //System.out.println("c=" + c);
                    //System.out.println("DONE APPENDING");
                    
                } else {
                    //System.out.println("PUTTING");
                    //if(cBuf1Val!=c)
                   // {
                    rowNumberStartVertex++;
                    resultsTable.put(rowNumberStartVertex + "_s", tmpSB.toString());
                    //System.out.println("PUTTING tmpSB.toString()=" + tmpSB.toString());

                    tmpSB.setLength(0);
                    rowsProcessedForInsertion++;
                    //System.out.println("DONE PUTTING");
                   // }
                }

                if (cBuf2Val != delimiterEndVertex && cBuf2Val!=c) {
                    tmpSB2.append(cBuf2Val);
                } else {
                    //if(cBuf2Val!=c)
                   // {
                    rowNumberEndVertex++;
                    //System.out.println("rowNumberEndVertex+_e=" + rowNumberEndVertex + "_e" + " VAL=" + tmpSB2.toString());
                    resultsTable.put(rowNumberEndVertex + "_e", tmpSB2.toString());
                    tmpSB2.setLength(0);
                    //}
                }


                if (cBuf3Val != delimiterWeight && cBuf3Val!=c) {
                    tmpSB3.append(cBuf3Val);
                } else {
                   // if(cBuf3Val!=c)
                   // {
                    rowNumberWeight++;
                    //System.out.println("rowNumberWeight+_s=" + rowNumberWeight + "_w" + " VAL=" + tmpSB3.toString());
                    resultsTable.put(rowNumberWeight + "_w", tmpSB3.toString());
                    tmpSB3.setLength(0);
                   // }
                }

                if ((resultsTable.containsKey(rowNumber + "_s")) && (resultsTable.containsKey(rowNumber + "_e") && (resultsTable.containsKey(rowNumber + "_w")))) {
                    String startVertexValue = (String) resultsTable.get(rowNumber + "_s");
                    String endVertexValue = (String) resultsTable.get(rowNumber + "_e");
                    String weightValue = (String) resultsTable.get(rowNumber + "_w");
                    //System.out.println("Row " + rowNumber + " startVertexValue=" + startVertexValue + " endVertexValue=" + endVertexValue + " weightValue=" + weightValue);
                        Text columnFamily = new Text("vertexfamily");
                        Text pathColumnQualifier = new Text("vertexfamilyValue:" + endVertexValue);

                        Mutation m = new Mutation(new Text(startVertexValue));
                        m.put(columnFamily, pathColumnQualifier, new Value(weightValue.getBytes()));

                        // add the mutation to the list to send
                        batchWriter.addMutation(m);
                        m = null;
                        rowsInserted++;
        
                    resultsTable.remove(rowNumber + "_s");
                    resultsTable.remove(rowNumber + "_e");
                    resultsTable.remove(rowNumber + "_w");
                    rowsRemovedFromHash++;
                    rowNumber++;
                }
                //System.out.println("rowsInserted="+rowsInserted+ " rowsProcessedForInsertion="+rowsProcessedForInsertion+ " rowsRemovedFromHash="+rowsRemovedFromHash);
            //if(resultsTable.isEmpty()) {processed = true;}
            }// END for

            processed = true;

        }//END WHILE

        reader1.close();
        reader2.close();
        reader3.close();

        // ensure pending mutations are sent
        if (!doTest) {
            batchWriter.close();
        }

    //System.out.println(" rowNumberStartVertex = " + rowNumberStartVertex);
    //System.out.println(" rowNumberEndVertex = " + rowNumberEndVertex);
    //System.out.println(" rowNumberWeight = " + rowNumberWeight);

    double elapsed = (System.currentTimeMillis() - start);
    //Date endDate = new Date();
    //long endSeconds = System.currentTimeMillis();
    System.out.println("Time to Process = " + elapsed / 1000 );
    //linesInserted = rowNumber - 1;
    //System.out.println(linesInserted + "," + elapsed / 1000 + "," + start / 1000 + "," + endSeconds / 1000 + "," + startDate + "," + endDate);

    }







        @SuppressWarnings("static-access")
    public void doProcessingOld() throws IOException, CBException, CBSecurityException, TableNotFoundException, MutationsRejectedException {

        if (doTest) {
            System.out.println("starting ingester");
            System.out.println("arg 1 = " + this.hostName);
            System.out.println("arg 2 = " + this.tableName);
            System.out.println("arg 3 = " + this.startVertexString);
            System.out.println("arg 4 = " + this.endVertexString);
            System.out.println("arg 5 = " + this.weightString);
        }

        this.createTable();

        CloudbaseConnection cbConnection = new CloudbaseConnection(this.hostName, this.userName, this.password);
        BatchWriter batchWriter = cbConnection.getBatchWriter(tableName);

        HashMap startVertexMap = this.processParam(startVertexString);
        HashMap endVertexMap = this.processParam(endVertexString);
        HashMap weightMap = this.processParam(weightString);

        String[] startVertexArr = (String[]) startVertexMap.get("content");
        String[] endVertexArr = (String[]) endVertexMap.get("content");
        String[] weightArr = (String[]) weightMap.get("content");
        String delimiter = (String) startVertexMap.get("delimiter");

        int linesInserted = 0;
        int rowNumber = 1;

        Date startDate = null;
        long start = 0;

        startDate = new Date();
        start = System.currentTimeMillis();
        for (int i = 0; i < startVertexArr.length; i++) {

            String startVertexValue = startVertexArr[i];
            String endVertexValue = endVertexArr[i];
            String weightValue = weightArr[i];

            if (!doTest) {
                Text columnFamily = new Text("vertexfamily");
                Text columnQualifier = new Text("vertexfamilyValue:" + endVertexValue);

                Mutation m = new Mutation(new Text(startVertexValue));
                m.put(columnFamily, columnQualifier, new Value(weightValue.getBytes()));

                batchWriter.addMutation(m);
                batchWriter.flush();
                m = null;
            } else {
                if (printOutput) {
                    System.out.println("Row " + rowNumber + " startVertexValue=" + startVertexValue + " endVertexValue=" + endVertexValue + " weightValue=" + weightValue);
                }
            }
            rowNumber++;
        }// END for

        // ensure pending mutations are sent
        if (!doTest) {
            batchWriter.close();

        }

    //System.out.println(" rowNumberStartVertex = " + rowNumberStartVertex);
    //System.out.println(" rowNumberEndVertex = " + rowNumberEndVertex);
    //System.out.println(" rowNumberWeight = " + rowNumberWeight);

    //double elapsed = (System.currentTimeMillis() - start);
    //Date endDate = new Date();
    //long endSeconds = System.currentTimeMillis();

    //linesInserted = rowNumber - 1;
    //System.out.println(linesInserted + "," + elapsed / 1000 + "," + start / 1000 + "," + endSeconds / 1000 + "," + startDate + "," + endDate);

    }


    public HashMap processParam(String param) {
        HashMap map = new HashMap();
        String content = param.substring(0, param.length() - 1);
        //System.out.println("content="+content);
        String delimiter = param.replace(content, "");
        //System.out.println("delimiter="+delimiter);
        map.put("delimiter", delimiter);
        map.put("content", content.split(delimiter));
        map.put("length", content.length());
        return map;

    }

    public void createTable() throws CBException, CBSecurityException {

        if (this.doesTableExistFromMetadata(tableName) == false) {
            try {
                CloudbaseConnection cbConnection = new CloudbaseConnection(this.hostName, this.userName, this.password);
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
            sb1.append(i+" ");
            sb2.append(i+" ");
            sb3.append(i+" ");
        }

        this.startVertexString = sb1.toString();
        this.endVertexString = sb2.toString();
        this.weightString = sb3.toString();
        System.out.println("Completed creation of test data for " + loops + " entries.");

    }


        @SuppressWarnings("static-access")
    public void doProcessingSAVE() throws IOException, CBException, CBSecurityException, TableNotFoundException, MutationsRejectedException {

        if (doTest) {
            System.out.println("starting ingester");
            System.out.println("arg 1 = " + this.hostName);
            System.out.println("arg 2 = " + this.tableName);
            System.out.println("arg 3 = " + this.startVertexString);
            System.out.println("arg 4 = " + this.endVertexString);
            System.out.println("arg 5 = " + this.weightString);
        }

        this.doLoadTest();
        this.createTable();

        CloudbaseConnection cbConnection = new CloudbaseConnection(this.hostName, this.userName, this.password);
        BatchWriter batchWriter = cbConnection.getBatchWriter(tableName);

        int startVertexLength = startVertexString.length();
        int endVertexLength = endVertexString.length();
        int weightLength = weightString.length();

        int[] lengths = {startVertexLength, endVertexLength, weightLength};
        Arrays.sort(lengths);
        int BUFF_SIZE = lengths[lengths.length-1];

        //char[] cBuf1 = new char[BUFF_SIZE];
        //char[] cBuf2 = new char[BUFF_SIZE];
       // char[] cBuf3 = new char[BUFF_SIZE];


        String contentStartVertex = startVertexString.substring(0, startVertexString.length() - 1);
        String contentEndVertex = endVertexString.substring(0, endVertexString.length() - 1);
        String contentWeight = weightString.substring(0, weightString.length() - 1);

        String delimiterStartVertexIndex = startVertexString.replace(contentStartVertex, "");
        String delimiterEndVertexIndex = endVertexString.replace(contentEndVertex, "");
        String delimiterWeightIndex = weightString.replace(contentWeight, "");

        char delimiterStartVertex = delimiterStartVertexIndex.charAt(0);
        char delimiterEndVertex = delimiterEndVertexIndex.charAt(0);
        char delimiterWeight = delimiterWeightIndex.charAt(0);

        StringReader reader1 = new StringReader(contentStartVertex);
        StringReader reader2 = new StringReader(contentEndVertex);
        StringReader reader3 = new StringReader(contentWeight);


        int rowsInserted = 0;
        int rowsProcessedForInsertion = 0;
        int rowsRemovedFromHash = 0;
        int rowNumber = 1;

        Date startDate = null;
        long start = 0;

        startDate = new Date();
        start = System.currentTimeMillis();

        boolean processed = false;
        int endChar = BUFF_SIZE;
        int startChar = 0;
        Hashtable resultsTable = new Hashtable();

        int len1 = 0;
        int len2 = 0;
        int len3 = 0;

        int rowNumberStartVertex = 0;
        int rowNumberEndVertex = 0;
        int rowNumberWeight = 0;

        StringBuilder tmpSB = new StringBuilder();
        StringBuilder tmpSB2 = new StringBuilder();
        StringBuilder tmpSB3 = new StringBuilder();
        char c = '\0';
        int i =0;

         while (processed!=true) {

            len1 = reader1.read();
            len2 = reader2.read();
            len3 = reader3.read();

            System.out.println("len1 =" + (char)len1);

            //if(rowsInserted!=rowsProcessedForInsertion)


                char cBuf1Val = (char)len1;
                char cBuf2Val = (char)len2;
                char cBuf3Val = (char)len3;



                //System.out.println("cBuf3Val=" + cBuf3Val);
                //System.out.println("rowNumberStartVertex+_s=" + rowNumberStartVertex + "_s" + " VAL=" + tmpSB.toString());

                if (cBuf1Val != delimiterStartVertex && cBuf1Val!=c) {
                    tmpSB.append(cBuf1Val);

                    System.out.println("APPENDING");
                    System.out.println("cBuf1Val=" + cBuf1Val);
                    System.out.println("delimiterStartVertex=" + delimiterStartVertex);
                    System.out.println("tmpSB.toString()=" + tmpSB.toString().replace(" " , ""));
                    System.out.println("c=" + c);
                    System.out.println("DONE APPENDING");

                } else {
                    //System.out.println("PUTTING");
                    rowNumberStartVertex++;
                    resultsTable.put(rowNumberStartVertex + "_s", tmpSB.toString());
                    //System.out.println("PUTTING tmpSB.toString()=" + tmpSB.toString().replace(" " , ""));

                    tmpSB.setLength(0);
                    rowsProcessedForInsertion++;
                    //System.out.println("DONE PUTTING");
                }



                if (cBuf2Val != delimiterEndVertex && cBuf2Val!=c) {
                    tmpSB2.append(cBuf2Val);
                } else {
                    rowNumberEndVertex++;
                    //System.out.println("rowNumberEndVertex+_e=" + rowNumberEndVertex + "_e" + " VAL=" + tmpSB2.toString());
                    resultsTable.put(rowNumberEndVertex + "_e", tmpSB2.toString());
                    tmpSB2.setLength(0);
                }


                if (cBuf3Val != delimiterWeight && cBuf3Val!=c) {
                    tmpSB3.append(cBuf3Val);
                } else {
                    rowNumberWeight++;
                    //System.out.println("rowNumberWeight+_s=" + rowNumberWeight + "_w" + " VAL=" + tmpSB3.toString());
                    resultsTable.put(rowNumberWeight + "_w", tmpSB3.toString());
                    tmpSB3.setLength(0);
                }

                if ((resultsTable.containsKey(rowNumber + "_s")) && (resultsTable.containsKey(rowNumber + "_e") && (resultsTable.containsKey(rowNumber + "_w")))) {
                    String startVertexValue = (String) resultsTable.get(rowNumber + "_s");
                    String endVertexValue = (String) resultsTable.get(rowNumber + "_e");
                    String weightValue = (String) resultsTable.get(rowNumber + "_w");
                    //System.out.println("Row " + rowNumber + " startVertexValue=" + startVertexValue + " endVertexValue=" + endVertexValue + " weightValue=" + weightValue);
                    if (!doTest) {
                        Text columnFamily = new Text("vertexfamily");
                        Text pathColumnQualifier = new Text("vertexfamilyValue:" + endVertexValue);

                        Mutation m = new Mutation(new Text(startVertexValue));
                        m.put(columnFamily, pathColumnQualifier, new Value(weightValue.getBytes()));

                        // add the mutation to the list to send
                        batchWriter.addMutation(m);
                        m = null;
                        rowsInserted++;
                    } else {
                        if (printOutput) {
                            System.out.println("Row " + rowNumber + " startVertexValue=" + startVertexValue + " endVertexValue=" + endVertexValue + " weightValue=" + weightValue);
                        }
                    }

                    resultsTable.remove(rowNumber + "_s");
                    resultsTable.remove(rowNumber + "_e");
                    resultsTable.remove(rowNumber + "_w");
                    rowsRemovedFromHash++;
                    rowNumber++;
                }
                //System.out.println("rowsInserted="+rowsInserted+ " rowsProcessedForInsertion="+rowsProcessedForInsertion+ " rowsRemovedFromHash="+rowsRemovedFromHash);

                if(rowsInserted==rowsRemovedFromHash) {processed = true;}
            //}// END for


        }//END WHILE

        reader1.close();
        reader2.close();
        reader3.close();

        // ensure pending mutations are sent
        if (!doTest) {
            batchWriter.close();
        }

    //System.out.println(" rowNumberStartVertex = " + rowNumberStartVertex);
    //System.out.println(" rowNumberEndVertex = " + rowNumberEndVertex);
    //System.out.println(" rowNumberWeight = " + rowNumberWeight);

    double elapsed = (System.currentTimeMillis() - start);
    //Date endDate = new Date();
    //long endSeconds = System.currentTimeMillis();
    System.out.println("Time to Process = " + elapsed / 1000 );
    //linesInserted = rowNumber - 1;
    //System.out.println(linesInserted + "," + elapsed / 1000 + "," + start / 1000 + "," + endSeconds / 1000 + "," + startDate + "," + endDate);

    }



            @SuppressWarnings("static-access")
    public void doProcessingTEST() throws IOException, CBException, CBSecurityException, TableNotFoundException, MutationsRejectedException {

        if (doTest) {
            System.out.println("starting ingester");
            System.out.println("arg 1 = " + this.hostName);
            System.out.println("arg 2 = " + this.tableName);
            System.out.println("arg 3 = " + this.startVertexString);
            System.out.println("arg 4 = " + this.endVertexString);
            System.out.println("arg 5 = " + this.weightString);
        }

        //this.doLoadTest();
        this.createTable();

        CloudbaseConnection cbConnection = new CloudbaseConnection(this.hostName, this.userName, this.password);
        BatchWriter batchWriter = cbConnection.getBatchWriter(tableName);

        int startVertexLength = startVertexString.length();
        int endVertexLength = endVertexString.length();
        int weightLength = weightString.length();

        int[] lengths = {startVertexLength, endVertexLength, weightLength};
        Arrays.sort(lengths);
        int BUFF_SIZE = lengths[lengths.length-1];

        //char[] cBuf1 = new char[BUFF_SIZE];
        //char[] cBuf2 = new char[BUFF_SIZE];
       // char[] cBuf3 = new char[BUFF_SIZE];


        String contentStartVertex = startVertexString.substring(0, startVertexString.length() - 1);
        String contentEndVertex = endVertexString.substring(0, endVertexString.length() - 1);
        String contentWeight = weightString.substring(0, weightString.length() - 1);

        String delimiterStartVertexIndex = startVertexString.replace(contentStartVertex, "");
        String delimiterEndVertexIndex = endVertexString.replace(contentEndVertex, "");
        String delimiterWeightIndex = weightString.replace(contentWeight, "");

        char delimiterStartVertex = delimiterStartVertexIndex.charAt(0);
        char delimiterEndVertex = delimiterEndVertexIndex.charAt(0);
        char delimiterWeight = delimiterWeightIndex.charAt(0);

        StringReader reader1 = new StringReader(contentStartVertex);
        StringReader reader2 = new StringReader(contentEndVertex);
        StringReader reader3 = new StringReader(contentWeight);


        int rowsInserted = 0;
        int rowsProcessedForInsertion = 0;
        int rowsRemovedFromHash = 0;
        int rowNumber = 1;

        Date startDate = null;
        long start = 0;

        startDate = new Date();
        start = System.currentTimeMillis();

        boolean processed = false;
        int endChar = BUFF_SIZE;
        int startChar = 0;
        Hashtable resultsTable = new Hashtable();

        int len1 = 0;
        int len2 = 0;
        int len3 = 0;

        int rowNumberStartVertex = 0;
        int rowNumberEndVertex = 0;
        int rowNumberWeight = 0;

        StringBuilder tmpSB = new StringBuilder();
        StringBuilder tmpSB2 = new StringBuilder();
        StringBuilder tmpSB3 = new StringBuilder();
        char c = '\0';
        int i =0;

         while (processed!=true) {

            len1 = reader1.read();
            len2 = reader2.read();
            len3 = reader3.read();

            System.out.println("len1 =" + (char)len1);

            //if(rowsInserted!=rowsProcessedForInsertion)


                char cBuf1Val = (char)len1;
                char cBuf2Val = (char)len2;
                char cBuf3Val = (char)len3;



                //System.out.println("cBuf3Val=" + cBuf3Val);
                //System.out.println("rowNumberStartVertex+_s=" + rowNumberStartVertex + "_s" + " VAL=" + tmpSB.toString());

                if (cBuf1Val != delimiterStartVertex && cBuf1Val!=c) {
                    tmpSB.append(cBuf1Val);

                    System.out.println("APPENDING");
                    System.out.println("cBuf1Val=" + cBuf1Val);
                    System.out.println("delimiterStartVertex=" + delimiterStartVertex);
                    System.out.println("tmpSB.toString()=" + tmpSB.toString().replace(" " , ""));
                    System.out.println("c=" + c);
                    System.out.println("DONE APPENDING");

                } else {
                    //System.out.println("PUTTING");
                    rowNumberStartVertex++;
                    resultsTable.put(rowNumberStartVertex + "_s", tmpSB.toString());
                    //System.out.println("PUTTING tmpSB.toString()=" + tmpSB.toString().replace(" " , ""));

                    tmpSB.setLength(0);
                    rowsProcessedForInsertion++;
                    //System.out.println("DONE PUTTING");
                }



                if (cBuf2Val != delimiterEndVertex && cBuf2Val!=c) {
                    tmpSB2.append(cBuf2Val);
                } else {
                    rowNumberEndVertex++;
                    //System.out.println("rowNumberEndVertex+_e=" + rowNumberEndVertex + "_e" + " VAL=" + tmpSB2.toString());
                    resultsTable.put(rowNumberEndVertex + "_e", tmpSB2.toString());
                    tmpSB2.setLength(0);
                }


                if (cBuf3Val != delimiterWeight && cBuf3Val!=c) {
                    tmpSB3.append(cBuf3Val);
                } else {
                    rowNumberWeight++;
                    //System.out.println("rowNumberWeight+_s=" + rowNumberWeight + "_w" + " VAL=" + tmpSB3.toString());
                    resultsTable.put(rowNumberWeight + "_w", tmpSB3.toString());
                    tmpSB3.setLength(0);
                }

                if ((resultsTable.containsKey(rowNumber + "_s")) && (resultsTable.containsKey(rowNumber + "_e") && (resultsTable.containsKey(rowNumber + "_w")))) {
                    String startVertexValue = (String) resultsTable.get(rowNumber + "_s");
                    String endVertexValue = (String) resultsTable.get(rowNumber + "_e");
                    String weightValue = (String) resultsTable.get(rowNumber + "_w");
                    //System.out.println("Row " + rowNumber + " startVertexValue=" + startVertexValue + " endVertexValue=" + endVertexValue + " weightValue=" + weightValue);
                    if (!doTest) {
                        Text columnFamily = new Text("vertexfamily");
                        Text pathColumnQualifier = new Text("vertexfamilyValue:" + endVertexValue);

                        Mutation m = new Mutation(new Text(startVertexValue));
                        m.put(columnFamily, pathColumnQualifier, new Value(weightValue.getBytes()));

                        // add the mutation to the list to send
                        batchWriter.addMutation(m);
                        m = null;
                        rowsInserted++;
                    } else {
                        if (printOutput) {
                            System.out.println("Row " + rowNumber + " startVertexValue=" + startVertexValue + " endVertexValue=" + endVertexValue + " weightValue=" + weightValue);
                        }
                    }

                    resultsTable.remove(rowNumber + "_s");
                    resultsTable.remove(rowNumber + "_e");
                    resultsTable.remove(rowNumber + "_w");
                    rowsRemovedFromHash++;
                    rowNumber++;
                }
                //System.out.println("rowsInserted="+rowsInserted+ " rowsProcessedForInsertion="+rowsProcessedForInsertion+ " rowsRemovedFromHash="+rowsRemovedFromHash);

                if(rowsInserted==rowsRemovedFromHash) {processed = true;}
            //}// END for


        }//END WHILE

        reader1.close();
        reader2.close();
        reader3.close();

        // ensure pending mutations are sent
        if (!doTest) {
            batchWriter.close();
        }

    //System.out.println(" rowNumberStartVertex = " + rowNumberStartVertex);
    //System.out.println(" rowNumberEndVertex = " + rowNumberEndVertex);
    //System.out.println(" rowNumberWeight = " + rowNumberWeight);

    double elapsed = (System.currentTimeMillis() - start);
    //Date endDate = new Date();
    //long endSeconds = System.currentTimeMillis();
    System.out.println("Time to Process = " + elapsed / 1000 );
    //linesInserted = rowNumber - 1;
    //System.out.println(linesInserted + "," + elapsed / 1000 + "," + start / 1000 + "," + endSeconds / 1000 + "," + startDate + "," + endDate);

    }



}