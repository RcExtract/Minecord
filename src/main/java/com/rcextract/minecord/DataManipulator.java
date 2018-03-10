package com.rcextract.minecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

import org.apache.commons.lang.Validate;

import com.rcextract.minecord.sql.DriverNotFoundException;
import com.rcextract.minecord.sql.SQLConnectException;
import com.rcextract.minecord.sql.SQLObjectConvertor;

public class DataManipulator {

	public static final String PROTOCOL = "jdbc:mysql://";
	
	@SuppressWarnings("unused")
	private SQLObjectConvertor convertor;
	private Connection connection;
	
	public DataManipulator(String url, String user, String password) throws SQLTimeoutException, SQLConnectException, DriverNotFoundException {
		this(url, user, password, true);
	}
	
	public DataManipulator(String url, String user, String password, boolean autoreconnect) throws SQLTimeoutException, SQLConnectException, DriverNotFoundException {
		Validate.notNull(url);
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new DriverNotFoundException();
		}
		try {
			this.connection = DriverManager.getConnection(PROTOCOL + url + "?autoReconnect=" + Boolean.toString(autoreconnect) + "&useSSL=false", user, password);
		} catch (SQLTimeoutException e) {
			throw e;
		} catch (SQLException e) {
			throw new SQLConnectException();
		}
		this.convertor = new SQLObjectConvertor(connection);
	}

	public String getName() {
		return "minecord";
	}
	public double getLatestVersion() {
		return Double.parseDouble(Minecord.dbversion.replaceAll("dot", "."));
	}
	public double getSupportingOldVersion() {
		return Double.parseDouble(Minecord.olddbversion.replaceAll("dot", "."));
	}
	public Double getVersion() throws SQLConnectException {
		try (ResultSet databaseset = connection.getMetaData().getCatalogs()) {
			while (databaseset.next()) {
				String dbname = databaseset.getString("TABLE_CAT");
				if (dbname.startsWith("minecord")) 
					return Double.parseDouble(dbname.substring(8).replaceAll("dot", "."));
			}
			return null;
		} catch (SQLException e) {
			throw new SQLConnectException();
		}
	}
	public synchronized void initialize() {
		
	}
	public synchronized void load() {
		
	}
}
