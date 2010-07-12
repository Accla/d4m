package edu.mit.ll.d4m.db.cloud;


import cloudbase.core.client.TableExistsException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import cloudbase.core.client.BatchScanner;
import java.nio.charset.CharacterCodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.mit.ll.cloud.connection.CloudbaseConnection;
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
import java.util.SortedSet;
import java.util.TreeMap;

/**
 *
 * @author wi20909
 */

public class D4mDbTableOperations
{


    public D4mDbTableOperations() {}

    private String host = "localhost";
    private String userName = "root";
    private String password = "secret";
    private String tableName = "";
    private String delimiter = "";

    public String rowReturnString    = "";
    public String columnReturnString = "";
    public String valueReturnString  = "";


    public D4mDbTableOperations(String host) {
        this.host = host;
    }

    public static void main(String[] args) throws CBException, CBSecurityException, TableNotFoundException {
        if (args.length < 1) {
            return;
        }

        String hostName = args[0];
        D4mDbTableOperations ci = new D4mDbTableOperations(hostName);
        ci.createTable("test_table200");
        ci.deleteTable("test_table200");
    }


    public void createTable(String tableName)
    {
        CloudbaseConnection cbConnection = null;
        try {
            cbConnection = new CloudbaseConnection(this.host, this.userName, this.password);
        } catch (CBException ex) {
            Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CBSecurityException ex) {
            Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            cbConnection.createTable(tableName);
            System.out.println("The " +tableName+ " table was created.");
        } catch (CBException ex) {
            Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CBSecurityException ex) {
            Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TableExistsException ex) {
            System.out.println("The " +tableName+ " table already Exists.");
        }
    }

    public void deleteTable(String tableName)
    {
        CloudbaseConnection cbConnection = null;
        try {
            cbConnection = new CloudbaseConnection(this.host, this.userName, this.password);
        } catch (CBException ex) {
            Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CBSecurityException ex) {
            Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            cbConnection.deleteTable(tableName);
            System.out.println("The " +tableName+ " table was deleted.");
        } catch (CBException ex) {
            Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CBSecurityException ex) {
            Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TableNotFoundException ex) {
            Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

