package edu.mit.ll.d4m.db.sql;


import java.util.Iterator;
import java.util.SortedSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import edu.mit.ll.sql.connection.SQLProperties;
import edu.mit.ll.sql.connection.SQLConnection;


/**
 *
 * @author sa20039
 */

/**
 * 
 */
public class D4mDbInfo
{


    public D4mDbInfo() {}

    private String instance = "unknown";
    private String host = "localhost";
    private String userName = "root";
    private String password = "secret";
    private String tableName = "";
    private String delimiter = "";

    public String rowReturnString    = "";
    public String columnReturnString = "";
    public String valueReturnString  = "";


    public D4mDbInfo(String host) {
        this.host = host;
        this.userName = (String) SQLProperties.get("username");
        this.password = (String) SQLProperties.get("password");
    }

    public D4mDbInfo(String instance, String host) {
        this.instance = instance;
        this.host = host;
        this.userName = (String) SQLProperties.get("username");
        this.password = (String) SQLProperties.get("password");        
    }


    public static void main(String[] args) throws SQLException {
        if (args.length < 1) {
            return;
        }

        String hostName = args[0];
        D4mDbInfo ci = new D4mDbInfo(hostName);
        String tableList = ci.getTableList();
        System.out.println(ci.userName+ " " + ci.password);
        System.out.println(tableList);

    }

    public String getTableList() throws SQLException
    {
        SQLConnection cbConnection = new SQLConnection(this.instance, this.host, this.userName, this.password);
        SortedSet set = cbConnection.getTableList();
        Iterator it = set.iterator();
        StringBuilder sb = new StringBuilder();
        while(it.hasNext())
        {
            String tableName = (String) it.next();
            sb.append(tableName + " ");
        }
        return sb.toString();
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
