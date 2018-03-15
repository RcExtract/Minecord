package com.rcextract.minecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.lang.Validate;

import com.rcextract.minecord.sql.DriverNotFoundException;
import com.rcextract.minecord.sql.SQLConnectException;
import com.rcextract.minecord.sql.SQLObjectConverter;

public class DataManipulator {

	public static final String PROTOCOL = "jdbc:mysql://";
	
	private SQLObjectConverter converter;
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
		this.converter = new SQLObjectConverter(connection);
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
	/**
	 * Initializes the process of loading data. This includes selecting to the correct 
	 * database, and generating database if not existed.
	 * <p>
	 * This method no longer generates tables due to now using the SQLObjectConverter 
	 * API for loading and saving.
	 * @return Whether loading is required. Null for not required, true for loading 
	 * using the {@see #load()} method, and false for loading using the 
	 * {@see #loadFromOld()} method.
	 * @throws SQLConnectException See {@link SQLConnectException}.
	 * @throws SQLTimeoutException See {@link SQLTimeoutException}.
	 */
	public synchronized Boolean initialize() throws SQLConnectException, SQLTimeoutException {
		try (Statement statement = connection.createStatement()) {
			boolean value = false;
			String dbname = null;
			if (!(exists())) {
				statement.executeQuery("CREATE DATABASE minecord" + Minecord.dbversion);
				dbname = "minecord" + Minecord.dbversion;
				return null;
			} else if (isDataOld()) 
				dbname = "minecord" + Minecord.olddbversion;
			else {
				dbname = "minecord" + Minecord.dbversion;
				value = true;
			}
			connection.setCatalog(dbname);
			return value;
			
		} catch (SQLTimeoutException e) {
			throw e;
		} catch (SQLException e) {
			throw new SQLConnectException();
		}
	}
	public synchronized void load() {
		try {
			Minecord.getControlPanel().servers.addAll(converter.loadAll("server", Server.class));
			Minecord.getControlPanel().sendables.addAll(converter.loadAll("user", Sendable.class));
		} catch (Throwable e) {
			//These exceptions are never thrown.
		}
	}
	
	public synchronized void save() {
		try {
			converter.saveObjects(new ArrayList<Server>(Minecord.getControlPanel().servers));
			converter.saveObjects(new ArrayList<Sendable>(Minecord.getControlPanel().sendables));
		} catch (Throwable e) {
			//These exceptions are never thrown.
		}
	}
	
	public boolean exists() throws SQLTimeoutException, SQLConnectException {
		return isDataOld() != null;
	}
	
	public Boolean isDataOld() throws SQLConnectException, SQLTimeoutException {
		try (Statement statement = connection.createStatement()) {
			ResultSet set = statement.executeQuery("SHOW DATABASES");
			while (set.next()) {
				String name = set.getString(1);
				if (name.startsWith("minecord")) {
					if (name.substring(8) == Minecord.dbversion) return true;
					if (name.substring(8) == Minecord.olddbversion) return false;
				}
			}
			return null;
		} catch (SQLTimeoutException e) {
			throw e;
		} catch (SQLException e) {
			throw new SQLConnectException();
		}
	}
}
