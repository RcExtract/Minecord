package com.rcextract.minecord;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * A serializer and a deserializer of data between the database and the Minecord system.
 * <p>
 * The DatabaseManager creates a connection based on the host, username and password from the 
 * Minecord system, which are loaded from the options.yml.
 * <p>
 * Calling the load method when the data is loaded successfully will reload the data.
 */
public class DatabaseManager {

	private Connection connection;
	/**
	 * This constructor is reserved for initialization.
	 * @throws ClassNotFoundException 
	 */
	protected DatabaseManager() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		this.connection = DriverManager.getConnection("jdbc:mysql://" + Minecord.getHost() + "?autoReconnect=true&useSSL=false", Minecord.getUsername(), Minecord.getPassword());
	}
	public void init() throws SQLException {
		
		DatabaseMetaData dbmd = connection.getMetaData();
		ResultSet databases = dbmd.getCatalogs();
		Set<String> databasenames = new HashSet<String>();
		while (databases.next()) databasenames.add(databases.getString("TABLE_CAT"));
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.executeUpdate("CREATE DATABASE IF NOT EXISTS minecord;");
			statement.execute("USE minecord;");
			statement.execute("CREATE TABLE IF NOT EXISTS users (id INT UNSIGNED PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE KEY, nickname VARCHAR(255) NOT NULL, description VARCHAR(255), uuid VARCHAR(255) NOT NULL);");
			statement.execute("CREATE TABLE IF NOT EXISTS channels (id INT UNSIGNED PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE KEY, description VARCHAR(255), locked BOOLEAN NOT NULL, onlines TEXT(65535) NOT NULL);");
			statement.execute("CREATE TABLE IF NOT EXISTS servers (id INT UNSIGNED PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE KEY, description VARCHAR(255), approvement BOOLEAN NOT NULL, invitation BOOLEAN NOT NULL, permanent BOOLEAN NOT NULL, locked BOOLEAN NOT NULL, channels TEXT(65535) NOT NULL, main INT UNSIGNED NOT NULL);");
		} finally {
			if (statement != null) statement.close();
		}
	}
	/**
	 * Loads the data from the database.
	 * @throws SQLException If an error occurred while attempting to load the data.
	 */
	public void load() throws SQLException {
		/* User:
		 * id, name, nickname, desc, uuid;
		 * Channel:
		 * id, name, desc, locked, onlines
		 * Server:
		 * id, name, desc, approvement, invitation, permanent, locked, ChannelManager:(channels, main)
		 */
		Statement statement = null;
		try {
			statement = connection.createStatement();
			ResultSet a = statement.executeQuery("SELECT * FROM users;");
			Set<User> unrecordedusers = new HashSet<User>();
			while (a.next()) {
				int id = a.getInt("id");
				String name = a.getString("name");
				String nickname = a.getString("nickname");
				String desc = a.getString("description");
				if (desc == null) desc = "A default user description.";
				OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(a.getString("uuid")));
				unrecordedusers.add(new User(id, name, nickname, desc, player));
			}
			Minecord.getControlPanel().addAllUsers(unrecordedusers);
			ResultSet b = statement.executeQuery("SELECT * FROM channels;");
			Set<Channel> unrecordedchannels = new HashSet<Channel>();
			while (b.next()) {
				int id = b.getInt("id");
				String name = b.getString("name");
				String desc = b.getString("description");
				if (desc == null) desc = "A default channel description.";
				boolean locked = b.getBoolean("locked");
				Channel channel = new Channel(id, name, desc, locked);
				for (User user : unrecordedusers) 
					if (Arrays.asList(b.getString("onlines").split(",")).contains(Integer.toString(user.getIdentifier()))) 
						user.switchChannel(channel);
				unrecordedchannels.add(channel);
			}
			ResultSet c = statement.executeQuery("SELECT * FROM servers;");
			Set<Server> unrecordedservers = new HashSet<Server>();
			while (c.next()) {
				int id = c.getInt("id");
				String name = c.getString("name");
				String desc = c.getString("description");
				if (desc == null) desc = "A default server description.";
				boolean approvement = c.getBoolean("approvement");
				boolean invitation = c.getBoolean("invitation");
				boolean permanent = c.getBoolean("permanent");
				boolean locked = c.getBoolean("locked");
				Server server = new Server(id, name, desc, approvement, invitation, permanent, locked, new ChannelManager(), null);
				for (Channel channel : unrecordedchannels) {
					if (Arrays.asList(c.getString("channels").split(",")).contains(Integer.toString(channel.getIdentifier()))) 
						server.getChannelManager().getModifiableChannels().add(channel);
					if (c.getInt("main") == channel.getIdentifier()) 
						server.getChannelManager().setMainChannel(channel);
				}
				unrecordedservers.add(server);
			}
			Minecord.getControlPanel().addAllServers(unrecordedservers);
		} finally {
			if (statement != null) statement.close();
		}
	}
	/**
	 * Saves the data to the database.
	 * @throws SQLException If an error occurred while attempting to save the data.
	 */
	public void save() throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("DROP DATABASE minecord;");
		init();
		for (Server server : Minecord.getServerManager().getServers()) {
			PreparedStatement one = connection.prepareStatement("INSERT INTO servers VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
			one.setInt(1, server.getIdentifier());
			one.setString(2, server.getName());
			one.setString(3, server.getDescription());
			one.setBoolean(4, server.needApprovement());
			one.setBoolean(5, server.needInvitation());
			one.setBoolean(6, server.isPermanent());
			one.setBoolean(7, !(server.ready()));
			Set<String> channelids = new HashSet<String>();
			for (Channel channel : server.getChannelManager().getChannels()) {
				channelids.add(Integer.toString(channel.getIdentifier()));
				PreparedStatement two = connection.prepareStatement("INSERT INTO channels VALUES (?, ?, ?, ?, ?);");
				two.setInt(1, channel.getIdentifier());
				two.setString(2, channel.getName());
				two.setString(3, channel.getDescription());
				two.setBoolean(4, !(channel.ready()));
				Set<String> userids = new HashSet<String>();
				for (User user : channel.getMembers()) 
					userids.add(Integer.toString(user.getIdentifier()));
				two.setString(5, String.join(",", userids));
				two.executeUpdate();
				two.close();
			}
			one.setString(8, String.join(",", channelids));
			one.setInt(9, server.getChannelManager().getMainChannel().getIdentifier());
			one.executeUpdate();
			one.close();
		}
		for (User user : Minecord.getUserManager().getUsers()) {
			PreparedStatement statement = connection.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?, ?);");
			statement.setInt(1, user.getIdentifier());
			statement.setString(2, user.getName());
			statement.setString(3, user.getNickName());
			statement.setString(4, user.getDescription());
			statement.setString(5, user.getPlayer().getUniqueId().toString());
			statement.executeUpdate();
			statement.close();
		}
	}
	public void close() throws SQLException {
		connection.close();
	}
}
