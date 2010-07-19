package edu.mit.ll.sql.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class SqlConnection {

	private static final Logger logger = Logger.getLogger(SqlConnection.class);
	public String databaseAddress = "jdbc:mysql://localhost:5432/someDb";
	public String user = "user";
	public String password = "pass";
	public String driverName = "org.mysql.jdbc.Driver";

	private Statement statement;

	protected Connection dbConnection;

	public SqlConnection(String driverName, String databaseAddress, String user, String password) {
		if (driverName != null)
			this.driverName = driverName;

		if (databaseAddress != null)
			this.databaseAddress = databaseAddress;

		if (user != null)
			this.user = user;

		if (password != null)
			this.password = password;
	}

	/**
	 * Initiate a connection with the database -- throw a SQLException if error
	 * occurs. If error occurs, the connection is null.
	 */
	public Connection connect() throws SQLException {
		logger.info("Connecting to " + databaseAddress + " with user " + user + " and password " + password);
		dbConnection = null;

		try {
			// Load jdbc driver
			Class.forName(driverName);

			// Connect to database (using DriverManager)
			dbConnection = DriverManager.getConnection(databaseAddress, user, password);
		}
		catch (ClassNotFoundException e) {
			logger.error("Could not find driver: " + driverName, e);
			dbConnection = null;
		}

		return dbConnection;
	}

	/**
	 * Return current connection to database (without attempting to connect).
	 */
	public Connection getConnection() {
		return dbConnection;
	}

	/**
	 * Closes connection to database.
	 */
	public void close() {
		try {
			if (dbConnection != null)
				dbConnection.close();
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			dbConnection = null;
		}
	}

	public ResultSet exectueQuery(String sql) throws SQLException {
		Statement statement = (Statement) dbConnection.createStatement();
		ResultSet ret = (ResultSet) statement.executeQuery(sql);
		statement.close();
		return ret;
	}

	public int exectueUpdate(String sql) throws SQLException {
		Statement statement = (Statement) dbConnection.createStatement();
		int ret = statement.executeUpdate(sql);
		statement.close();
		return ret;
	}

	public Object exectue(String sql) throws SQLException {
		if (statement != null) {
			statement.close();
		}
		statement = (Statement) dbConnection.createStatement();
		boolean ret = statement.execute(sql);

		if (ret)
			return (ResultSet) statement.getResultSet();
		else
			return statement.getUpdateCount();
	}
}

/*
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 
 * % D4M: Dynamic Distributed Dimensional Data Model 
 * % MIT Lincoln Laboratory
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 
 * % (c) <2010> Massachusetts Institute of Technology
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 */
