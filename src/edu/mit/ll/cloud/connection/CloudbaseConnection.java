package edu.mit.ll.cloud.connection;

import cloudbase.core.CBConstants;
import cloudbase.core.client.BatchScanner;
import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Scanner;
import cloudbase.core.client.TableExistsException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.ZooKeeperInstance;
import cloudbase.core.client.MasterInstance;
import cloudbase.core.client.admin.TableOperations;
import cloudbase.core.client.impl.TabletServerBatchReader;
import cloudbase.core.data.Key;
import cloudbase.core.data.Range;
import cloudbase.core.iterators.CountingIterator;
import cloudbase.core.security.Authorizations;
import cloudbase.core.util.ArgumentChecker;
import java.util.SortedSet;
import org.apache.hadoop.io.Text;

/**
 *
 * @author wi20909
 */

public class CloudbaseConnection {

    private Connector connector = null;
    private String tableName = "";
    private String instance = "cloudbase";

    public CloudbaseConnection(String master, String user, String pass) throws CBException, CBSecurityException {
        this.instance = (String) CloudbaseProperties.get("instance");		

		  String[] hosts = master.split("/");
		  if(hosts.length > 1) 
            master = hosts[1];

        ZooKeeperInstance instanceObj = new ZooKeeperInstance(this.instance, master);
        this.connector = new Connector(instanceObj, user, pass.getBytes());
    }
 
    public CloudbaseConnection(String instance, String master, String user, String pass) throws CBException, CBSecurityException {
        String[] hosts = master.split("/");
        if(hosts.length > 1) 
            master = hosts[1];

        ZooKeeperInstance instanceObj = new ZooKeeperInstance(instance, master);
        this.connector = new Connector(instanceObj, user, pass.getBytes());
    }

    public CloudbaseConnection(String cbMaster, String user, String pass) throws CBException, CBSecurityException {

        MasterInstance instanceObj = new MasterInstance(cbMaster);
        this.connector = new Connector(instanceObj, user, pass.getBytes());
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

    /*** This was Depricated and was not changed in the migrsation to 1.1 from 1.0
    for d4M so I (William Smith 4/26/2010) changed to below.
    public BatchWriter getBatchWriter(String tableName) throws CBException, CBSecurityException, TableNotFoundException
    {
        BatchWriter bw = this.connector.createBatchWriter(tableName, 100000, 30, 1);
        return bw;
    }
     ***/

    public BatchWriter getBatchWriter(String tableName) throws CBException, CBSecurityException, TableNotFoundException
    {
        BatchWriter bw = this.connector.createBatchWriter(tableName, Long.valueOf("100000"), Long.valueOf("30"), 1);
        return bw;
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

