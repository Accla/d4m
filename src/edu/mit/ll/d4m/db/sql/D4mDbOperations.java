package edu.mit.ll.d4m.db.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.nio.charset.CharacterCodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Map.Entry;


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

import edu.mit.ll.sql.connection.SQLProperties;
import edu.mit.ll.sql.connection.SQLConnection;

/**
 *
 * @author sa20039
 */


public class D4mDbOperations
{


    public D4mDbOperations() {}

    private String driverName = "com.jnetdirect.jsql.JSQLDriver";
    private String host = "localhost";
    private String userName = "root";
    private String password = "secret";
    private String tableName = "";
    private String delimiter = "";

    public String rowReturnString    = "";
    public String columnReturnString = "";
    public String valueReturnString  = "";


    public D4mDbOperations(String host) {
        this.host = host;
        this.userName = (String) SQLProperties.get("username");
        this.password = (String) SQLProperties.get("password");
    }

    public static void main(String[] args) throws SQLException {
        if (args.length < 1) {
            return;
        }

        Test(args);

    }

    public static void Test(String[] args)
    {
        String hostName = args[0];
        D4mDbOperations ci = new D4mDbOperations(hostName);
        try {
        ci.createTable("test_table200");
        ci.deleteTable("test_table200");
        }
        catch (SQLException ex)
        {
            Logger.getLogger(D4mDbOperations.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        }
    }

    public void createTable(String tableName) throws SQLException
    {
        SQLConnection cbConnection = null;
        try {
            cbConnection = new SQLConnection(this.host, this.userName, this.password);
        } catch (SQLException ex) {
            Logger.getLogger(D4mDbOperations.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } 
        
        try {
            cbConnection.createTable(tableName,"");
            System.out.println("The " +tableName+ " table was created.");
        } catch (SQLException ex) {
            Logger.getLogger(D4mDbOperations.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } 
    }
    
    public void createTable(String tableName, String schema) throws SQLException
    {
        SQLConnection cbConnection = null;
        try {
            cbConnection = new SQLConnection(this.host, this.userName, this.password);
        } catch (SQLException ex) {
            Logger.getLogger(D4mDbOperations.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } 
        
        try {
            cbConnection.createTable(tableName,schema);
            System.out.println("The " +tableName+ " table was created.");
        } catch (SQLException ex) {
            Logger.getLogger(D4mDbOperations.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } 
    }

    public void deleteTable(String tableName)
    {
        SQLConnection cbConnection = null;
        try {
            cbConnection = new SQLConnection(this.host, this.userName, this.password);
        } catch (SQLException ex) {
            Logger.getLogger(D4mDbOperations.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } 
        
        try {
            cbConnection.deleteTable(tableName);
            System.out.println("The " +tableName+ " table was deleted.");
        } catch (SQLException ex) {
            Logger.getLogger(D4mDbOperations.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } 
    }
    
    /**
     * Get a string containing Table Names of all the tables
     * @return
     * @throws SQLException
     */
    public String getTableList() throws SQLException
    {
        SQLConnection cbConnection = new SQLConnection(this.host, this.userName, this.password);
        ResultSet res = cbConnection.getTables();
        
        StringBuilder sb = new StringBuilder();
        while(res.next())
        {
            String tableName = res.getString(3);
            sb.append(tableName + " ");
        }
        return sb.toString();
    }
    
    public boolean exitsTable(String tableName) {
        boolean exist = false;
        
        String tableNames = "";
        try {
            tableNames = getTableList();
            if (tableNames.contains(tableName)) {
                exist = true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(D4mDbOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return exist;
    }
    
    public boolean Insert(String rVal, String cVal, String Val) throws SQLException {    	

    	return true;
    }
    
    public boolean Insert(int rVal, String cVal, String Val) throws SQLException {    	

    	return true;
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

