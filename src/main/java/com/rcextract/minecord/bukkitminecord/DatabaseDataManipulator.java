package com.rcextract.minecord.bukkitminecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permission;

import com.rcextract.minecord.DataManipulator;
import com.rcextract.minecord.Minecord;
import com.rcextract.minecord.Sendable;
import com.rcextract.minecord.Server;
import com.rcextract.minecord.sql.DataLoadException;
import com.rcextract.minecord.sql.DatabaseAccessException;
import com.rcextract.minecord.sql.DriverNotFoundException;
import com.rcextract.minecord.sql.SQLConnectException;
import com.rcextract.minecord.sql.SQLObjectConverter;
import com.rcextract.minecord.sql.TypeConverter;

public class DatabaseDataManipulator implements DataManipulator {

	public static final String PROTOCOL = "jdbc:mysql://";
	
	private SQLObjectConverter converter;
	private Connection connection;
	
	public DatabaseDataManipulator(String url, String user, String password) throws SQLTimeoutException, SQLConnectException, DriverNotFoundException {
		this(url, user, password, true);
	}
	
	public DatabaseDataManipulator(String url, String user, String password, boolean autoreconnect) throws SQLTimeoutException, SQLConnectException, DriverNotFoundException {
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
		this.converter = new SQLObjectConverter(connection, new TypeConverter<UUID, String>(UUID.class, String.class) {

			@Override
			public String serialize(UUID input) {
				return input.toString();
			}

			@Override
			public UUID deserialize(String output) {
				return UUID.fromString(output);
			}
			
		}, new TypeConverter<OfflinePlayer, UUID>(OfflinePlayer.class, UUID.class) {

			@Override
			public UUID serialize(OfflinePlayer input) {
				return input.getUniqueId();
			}

			@Override
			public OfflinePlayer deserialize(UUID output) {
				return Bukkit.getOfflinePlayer(output);
			}
			
		}, new TypeConverter<Permission, String>(Permission.class, String.class) {

			@Override
			public String serialize(Permission input) {
				return input.getName();
			}

			@Override
			public Permission deserialize(String output) {
				return new Permission(output);
			}
			
		}, new TypeConverter<ChatColor, String>(ChatColor.class, String.class) {

			@Override
			public String serialize(ChatColor input) {
				return input.toString();
			}

			@Override
			public ChatColor deserialize(String output) {
				return ChatColor.valueOf(output);
			}
			
		});
	}

	public String getName() {
		return "minecord";
	}
	public double getLatestVersion() {
		return Double.parseDouble(Minecord.databaseVersion().replaceAll("dot", "."));
	}
	public double getSupportingOldVersion() {
		return Double.parseDouble(Minecord.oldDatabaseVersion().replaceAll("dot", "."));
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
				statement.executeQuery("CREATE DATABASE minecord" + Minecord.databaseVersion());
				dbname = "minecord" + Minecord.databaseVersion();
				return null;
			} else if (isDataOld()) 
				dbname = "minecord" + Minecord.oldDatabaseVersion();
			else {
				dbname = "minecord" + Minecord.databaseVersion();
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
	public synchronized void load() throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		Minecord.getServers().addAll(converter.loadAll(Server.class).valueList());
		Minecord.getSendables().addAll(converter.loadAll(Sendable.class).valueList());
	}
	
	public synchronized void save() throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		converter.saveObjects(new ArrayList<Server>(Minecord.getServers()));
		converter.saveObjects(new ArrayList<Sendable>(Minecord.getSendables()));
	}
	
	public boolean exists() {
		return isDataOld() != null;
	}
	
	public Boolean isDataOld() {
		try (Statement statement = connection.createStatement()) {
			ResultSet set = statement.executeQuery("SHOW DATABASES");
			while (set.next()) {
				String name = set.getString(1);
				if (name.startsWith("minecord")) {
					if (name.substring(8) == Minecord.databaseVersion()) return true;
					if (name.substring(8) == Minecord.oldDatabaseVersion()) return false;
				}
			}
			return null;
		} catch (SQLException e) {
			return null;
		}
	}
}