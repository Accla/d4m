package edu.mit.ll.d4m.db.sql;

import edu.mit.ll.sql.connection.SQLProperties;
import edu.mit.ll.sql.connection.SQLConnection;

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

/**
 *
 * @author wi20909,sa20039
 */


public class D4mDbTableOperations
{


    public D4mDbTableOperations() {}

    private String driverName = "com.jnetdirect.jsql.JSQLDriver";
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
        D4mDbTableOperations ci = new D4mDbTableOperations(hostName);
        try {
        ci.createTable("test_table200");
        ci.deleteTable("test_table200");
        }
        catch (SQLException ex)
        {
            Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        }
    }

    public void createTable(String tableName) throws SQLException
    {
        SQLConnection cbConnection = null;
        try {
            cbConnection = new SQLConnection(this.host, this.userName, this.password);
        } catch (SQLException ex) {
            Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } 
        
        try {
            cbConnection.createTable(tableName,"");
            System.out.println("The " +tableName+ " table was created.");
        } catch (SQLException ex) {
            Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } 
    }
    
    public void createTable(String tableName, String schema) throws SQLException
    {
        SQLConnection cbConnection = null;
        try {
            cbConnection = new SQLConnection(this.host, this.userName, this.password);
        } catch (SQLException ex) {
            Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } 
        
        try {
            cbConnection.createTable(tableName,schema);
            System.out.println("The " +tableName+ " table was created.");
        } catch (SQLException ex) {
            Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } 
    }

    public void deleteTable(String tableName)
    {
        SQLConnection cbConnection = null;
        try {
            cbConnection = new SQLConnection(this.host, this.userName, this.password);
        } catch (SQLException ex) {
            Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } 
        
        try {
            cbConnection.deleteTable(tableName);
            System.out.println("The " +tableName+ " table was deleted.");
        } catch (SQLException ex) {
            Logger.getLogger(D4mDbTableOperations.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } 
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

