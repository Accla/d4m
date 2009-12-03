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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class D4mDbInsert {

    static String hostName = "localhost";
    static String userName = "root";
    static String password = "secret";
    static String tableName = "";

    static String startVertexString = "";
    static String endVertexString = "";
    static String weightString = "";
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
        this.createTable();
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

    public void createTable() throws CBException, CBSecurityException, TableExistsException
    {
        CloudbaseConnection cbConnection = new CloudbaseConnection(this.hostName, this.userName, this.password);
        cbConnection.createTable(tableName);
    }
}