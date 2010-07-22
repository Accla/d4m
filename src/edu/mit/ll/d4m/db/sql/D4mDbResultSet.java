package edu.mit.ll.d4m.db.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import edu.mit.ll.sql.connection.SQLProperties;
import edu.mit.ll.sql.connection.SQLConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TreeMap;

/**
 *
 * @author wi20909, sa20039
 */
public class D4mDbResultSet {

    private ArrayList MatlabDbRow = null;
    private Hashtable MatlabDbTable = new Hashtable();
    private double queryTime = 0;
    private int totalResultSize = 0;
    private int resultRange = 0;
    private int resultRangeStart = 0;
    private String searchValue = "";
    private String searchDate = "";
    private HashMap MatlabDbRows = null;
    private TreeMap MatlabDbRowsTree = null;

    public String getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(String searchDate) {
        this.searchDate = searchDate;
    }

    public Hashtable getMatlabDbTable() {
        return MatlabDbTable;
    }

    public void setMatlabDbTable(Hashtable MatlabDbTable) {
        this.MatlabDbTable = MatlabDbTable;
    }

    public TreeMap getMatlabDbRowsTree() {
        return MatlabDbRowsTree;
    }

    public void setMatlabDbRowsTree(TreeMap MatlabDbRowsTree) {
        this.MatlabDbRowsTree = MatlabDbRowsTree;
    }


    public HashMap getMatlabDbRows() {
        return MatlabDbRows;
    }

    public void setMatlabDbRows(HashMap MatlabDbRows) {
        this.MatlabDbRows = MatlabDbRows;
    }


    public int getResultRange() {
        return resultRange;
    }

    public void setResultRange(int resultRange) {
        this.resultRange = resultRange;
    }

    public int getResultRangeStart() {
        return resultRangeStart;
    }

    public void setResultRangeStart(int resultRangeStart) {
        this.resultRangeStart = resultRangeStart;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public int getTotalResultSize() {
        return totalResultSize;
    }

    public void setTotalResultSize(int totalResultSize) {
        this.totalResultSize = totalResultSize;
    }

    public double getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(double queryTime) {
        this.queryTime = queryTime;
    }

    public ArrayList getMatlabDbRow() {
        return MatlabDbRow;
    }

    public void setMatlabDbRow(ArrayList MatlabDbRow) {
        this.MatlabDbRow = MatlabDbRow;
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

