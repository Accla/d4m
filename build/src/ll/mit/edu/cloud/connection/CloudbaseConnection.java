package ll.mit.edu.cloud.connection;

import cloudbase.core.CBConstants;
import cloudbase.core.client.BatchScanner;
import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Scanner;
import cloudbase.core.client.TableExistsException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.admin.TableOperations;
import cloudbase.core.data.Key;
import cloudbase.core.data.Range;
import java.util.List;
import java.util.SortedSet;
import org.apache.hadoop.io.Text;

/**
 *
 * @author wi20909
 */

public class CloudbaseConnection {

    private Connector connector = null;
    private String tableName = "";

    public CloudbaseConnection(String master, String user, String pass) throws CBException, CBSecurityException {
        this.connector = new Connector(master, user, pass.getBytes());
    }

    public void createTable(String tableName) throws CBException, CBSecurityException, TableExistsException {
        this.connector.tableOperations().create(tableName);
    }

    public Scanner getScanner() throws TableNotFoundException, CBException, CBSecurityException {
        Scanner scanner = connector.createScanner(this.tableName, CBConstants.NO_AUTHS);
        return scanner;
    }

    public Scanner getScanner(String tableName) throws TableNotFoundException, CBException, CBSecurityException {
        Scanner scanner = connector.createScanner(tableName, CBConstants.NO_AUTHS);
        return scanner;
    }

    public BatchScanner getBatchScanner(int numberOfThreads) throws TableNotFoundException, CBException, CBSecurityException {
        BatchScanner scanner = connector.createBatchScanner(this.tableName, CBConstants.NO_AUTHS, numberOfThreads);
        return scanner;
    }

    public BatchScanner getBatchScanner(String tableName, int numberOfThreads) throws TableNotFoundException, CBException, CBSecurityException {
        BatchScanner scanner = connector.createBatchScanner(tableName, CBConstants.NO_AUTHS, numberOfThreads);
        return scanner;
    }


    public Scanner getRow(String tableName, String row) throws CBException, CBSecurityException, TableNotFoundException
	{
		// Create a scanner
		Scanner scanner = this.getScanner(tableName);
		Key key = new Key(new Text(row));
		// Say start key is the one with key of row
		// and end key is the one that immediately follows the row
		scanner.setRange(new Range(key, true, key.followingKey(1), false));
		return scanner;
	}

    public void deleteTable(String tableName) throws CBException, CBSecurityException, TableNotFoundException {

        this.connector.tableOperations().delete(tableName);

    }

    public boolean doesTableExist(String tableName) throws CBException, CBSecurityException, TableNotFoundException {

        if (this.connector.tableOperations().exists(tableName)) {
            return true;
        } else {
            return false;
        }
    }

    public SortedSet getTableList() throws CBException, CBSecurityException
    {
        TableOperations ops = this.connector.tableOperations();
        SortedSet set = ops.list();
        return set;
    }

    public BatchWriter getBatchWriter(String tableName) throws CBException, CBSecurityException, TableNotFoundException
    {
        BatchWriter bw = this.connector.createBatchWriter(tableName, 100000, 30, 1);
        return bw;
    }


}
