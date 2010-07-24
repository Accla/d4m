package edu.mit.ll.d4m.db.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.mit.ll.cloud.connection.CloudbaseConnection;
import edu.mit.ll.d4m.db.cloud.D4mDbResultSet;
import edu.mit.ll.d4m.db.cloud.D4mDbRow;
import edu.mit.ll.sql.connection.SQLConnection;
import edu.mit.ll.sql.connection.SQLProperties;

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
    
    public final String newline = System.getProperty("line.separator");
    public boolean doTest = false;
    private static final String NUMERIC_RANGE = "NUMERIC_RANGE";
    private static final String KEY_RANGE = "KEY_RANGE";
    private static final String REGEX_RANGE = "REGEX_RANGE";
    private static final String POSITIVE_INFINITY_RANGE = "POSITIVE_INFINITY_RANGE";
    private static final String NEGATIVE_INFINITY_RANGE = "NEGATIVE_INFINITY_RANGE";
    private static final String COLON = ":";


    public D4mDbOperations(String host) {
        this.host = host;
        this.userName = (String) SQLProperties.get("username");
        this.password = (String) SQLProperties.get("password");
    }

    /*
    public D4mDbOperations(String host, String table) {
        this.host = host;
        this.tableName = table;
        this.userName = (String) SQLProperties.get("username");
        this.password = (String) SQLProperties.get("password");
    }
    */
    
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

    /**
     * Create a Table
     * @param tableName
     * @throws SQLException
     */
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
    
    /**
     * 
     * @param tableName
     * @param schema
     * @throws SQLException
     */
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

    /**
     * Drop the table
     * @param tableName
     */
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
    
    /**
     * Check if the Table already Exists
     * @param tableName
     * @return
     */
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
    
    
    /*
     * Insert Operations
     * 
     */
    
    /**
     * Insert triple (r,c,v) 
     * @param rVal String
     * @param cVal String
     * @param Val String
     * @return
     * @throws SQLException
     */
    public int Insert(String rVal, String cVal, String Val) throws SQLException {    	

    	return 1;
    }
    
    /**
     * Insert triple (r,c,v) 
     * @param rVal int
     * @param cVal String
     * @param Val String
     * @return
     * @throws SQLException
     */
    
    public int Insert(int rVal, String cVal, String Val) throws SQLException {    	

    	return 1;
    }
    
    
    /*
     * Query Operations
     */
    

    /**
     * Return all the data from the table
     * @param tableName
     * @return
     * @throws SQLException
     */
    public ResultSet getAllData(String tableName) throws SQLException {

        ResultSet results = null;
        return results;
    }
    
    
    
    public boolean isRangeQuery(String[] paramContent) {
        boolean rangeQuery = false;
        /*
        Range Query are the following
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
    
    
    /*
     * TODO
     */
    public ResultSet doMatlabQuery(String table, String rowString, String columnString) throws SQLException {

    	ResultSet res = null;
    	
        if ((!rowString.equals(":")) && (columnString.equals(":"))) {
        	
            return this.doMatlabQueryOnRows(rowString, columnString);

        }
        
        if ((rowString.equals(":")) && (!columnString.equals(":"))) {
            return this.doMatlabQueryOnColumns(rowString, columnString);
        }
        
        if ((rowString.equals(":")) && (columnString.equals(":"))) {
            return this.getAllData();
        }


        return results;
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

