package com.rcextract.minecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permission;

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
	public synchronized void initialize() throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("CREATE DATABASE IF NOT EXISTS minecord;");
		}
		connection.setCatalog("minecord");
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS servers (id INT UNSIGNED PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE KEY, description VARCHAR(255), approvement BOOLEAN NOT NULL, invitation BOOLEAN NOT NULL, permanent BOOLEAN NOT NULL, locked BOOLEAN NOT NULL);");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS channels (server INT UNSIGNED NOT NULL, id INT UNSIGNED PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE KEY, description VARCHAR(255), locked BOOLEAN NOT NULL, main BOOLEAN NOT NULL);");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS ranks (server INT UNSIGNED NOT NULL, name VARCHAR(255) NOT NULL UNIQUE KEY, description VARCHAR(255), tag VARCHAR(255) PRIMARY KEY, admin BOOLEAN NOT NULL, override BOOLEAN NOT NULL, permissions TEXT(65535));");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (server INT UNSIGNED NOT NULL, channel INT UNSIGNED NOT NULL, rank VARCHAR(255) NOT NULL, id INT UNSIGNED PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE KEY, nickname VARCHAR(255) NOT NULL, description VARCHAR(255), uuid VARCHAR(255) NOT NULL);");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS permissions (id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, permission VARCHAR(255) NOT NULL UNIQUE KEY);");
		}
	}
	public synchronized void load() throws SQLException {
		try (Statement statement = connection.createStatement()) {
			ResultSet permset = statement.executeQuery("SELECT * FROM permissions;");
			List<Permission> permissions = new ArrayList<Permission>();
			while (permset.next()) 
				permissions.add(new Permission(permset.getString("permission")));
			ResultSet serverset = statement.executeQuery("SELECT * FROM servers;");
			while (serverset.next()) {
				int id = serverset.getInt("id");
				String name = serverset.getString("name");
				String desc = serverset.getString("description");
				boolean approvement = serverset.getBoolean("approvement");
				boolean invitation = serverset.getBoolean("invitation");
				boolean permanent = serverset.getBoolean("permanent");
				boolean locked = serverset.getBoolean("locked");
				Server server = new Server(id, name, desc, approvement, invitation, permanent, locked, new ChannelManager(), new RankManager());
				Minecord.getControlPanel().servers.add(server); 
			}
			ResultSet channelset = statement.executeQuery("SELECT * FROM channels;");
			while (channelset.next()) {
				Server server = Minecord.getServerManager().getServer(channelset.getInt("server"));
				int id = channelset.getInt("id");
				String name = channelset.getString("name");
				String desc = channelset.getString("description");
				boolean locked = channelset.getBoolean("locked");
				Channel channel = new Channel(id, name, desc, locked);
				if (server != null) server.getChannelManager().channels.add(channel);
				if (channelset.getBoolean("main")) server.getChannelManager().setMainChannel(channel);
			}
			ResultSet rankset = statement.executeQuery("SELECT * FROM ranks");
			while (rankset.next()) {
				Server server = Minecord.getServerManager().getServer(rankset.getInt("server"));
				String name = rankset.getString("name");
				String desc = rankset.getString("description");
				String tag = rankset.getString("tag");
				boolean admin = rankset.getBoolean("admin");
				boolean override = rankset.getBoolean("override");
				Set<Permission> perms = new HashSet<Permission>();
				if (!(rankset.getString("permissions").isEmpty())) 
					for (String permission : rankset.getString("permissions").split(",")) 
						perms.add(permissions.get(Integer.parseInt(permission) + 1));
				Rank rank = new Rank(name, desc, tag, admin, override, perms);
				if (server != null) server.getRankManager().ranks.add(rank);
			}
			ResultSet userset = statement.executeQuery("SELECT * FROM users;");
			while (userset.next()) {
				Server server = Minecord.getServerManager().getServer(userset.getInt("server"));
				Channel channel = server == null ? null : server.getChannelManager().getChannel(userset.getInt("channel"));
				Rank rank = server == null ? null : server.getRankManager().getRankByTag(userset.getString("rank"));
				int id = userset.getInt("id");
				String name = userset.getString("name");
				String nickname = userset.getString("nickname");
				String desc = userset.getString("description");
				OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(userset.getString("uuid")));
				/*Set<Permission> perms = new HashSet<Permission>();
				if (!(userset.getString("permissions").isEmpty())) 
					for (String permission : userset.getString("permissions").split(",")) 
						perms.add(permissions.get(Integer.parseInt(permission) + 1));*/
				User user = new User(id, name, nickname, desc, player, channel, rank/*, perms*/);
				Minecord.getControlPanel().users.add(user);
			}
		}
	}
	public synchronized void save() throws SQLException {
		List<Permission> savedperms = new ArrayList<Permission>();
		try (PreparedStatement permstmt = connection.prepareStatement("INSERT INTO permissions VALUES (DEFAULT, ?);")) {
			try (PreparedStatement serverstmt = connection.prepareStatement("INSERT INTO servers VALUES (?, ?, ?, ?, ?, ?, ?);"); 
					PreparedStatement channelstmt = connection.prepareStatement("INSERT INTO channels VALUES (?, ?, ?, ?, ?, ?);"); 
					PreparedStatement rankstmt = connection.prepareStatement("INSERT INTO ranks VALUES (?, ?, ?, ?, ?, ?, ?);")) {
				for (Server server : Minecord.getServerManager().getServers()) {
					serverstmt.setInt(1, server.getIdentifier());
					serverstmt.setString(2, server.getName());
					serverstmt.setString(3, server.getDescription());
					serverstmt.setBoolean(4, server.needApprovement());
					serverstmt.setBoolean(5, server.needInvitation());
					serverstmt.setBoolean(6, server.isPermanent());
					serverstmt.setBoolean(7, !(server.ready()));
					serverstmt.executeUpdate();
					for (Channel channel : server.getChannelManager().getChannels()) {
						channelstmt.setInt(1, server.getIdentifier());
						channelstmt.setInt(2, channel.getIdentifier());
						channelstmt.setString(3, channel.getName());
						channelstmt.setString(4, channel.getDescription());
						channelstmt.setBoolean(5, !(channel.ready()));
						channelstmt.setBoolean(6, channel.isMain());
						channelstmt.executeUpdate();
					}
					for (Rank rank : server.getRankManager().getRanks()) {
						rankstmt.setInt(1, server.getIdentifier());
						rankstmt.setString(2, rank.getName());
						rankstmt.setString(3, rank.getDescription());
						rankstmt.setString(4, rank.getTag());
						rankstmt.setBoolean(5, rank.isAdministrative());
						rankstmt.setBoolean(6, rank.isOverride());
						StringBuilder sb = new StringBuilder();
						for (Permission permission : rank.getPermissions()) 
							if (!(savedperms.contains(permission))) {
								permstmt.setString(1, permission.getName());
								permstmt.executeUpdate();
								savedperms.add(permission);
								sb.append(savedperms.size()).append(',');
							} else {
								sb.append(savedperms.indexOf(permission) + 1).append(',');
							}
						String permissions = sb.toString();
						if (permissions.length() > 0) 
							permissions = permissions.substring(0, permissions.length() - 2);
						rankstmt.setString(7, permissions);
						rankstmt.executeUpdate();
					}
				}
			}
			try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, ?);")) {
				for (User user : Minecord.getUserManager().getUsers()) {
					statement.setInt(1, user.getChannel().getChannelManager().getServer().getIdentifier());
					statement.setInt(2, user.getChannel().getIdentifier());
					statement.setString(3, user.getRank().getTag());
					statement.setInt(4, user.getIdentifier());
					statement.setString(5, user.getName());
					statement.setString(6, user.getNickName());
					statement.setString(7, user.getDescription());
					statement.setString(8, user.getPlayer().getUniqueId().toString());
					/*StringBuilder sb = new StringBuilder();
					for (Permission permission : user.getPermissions()) 
						if (!(savedperms.contains(permission))) {
							permstmt.setString(1, permission.getName());
							permstmt.executeUpdate();
							savedperms.add(permission);
							sb.append(savedperms.size()).append(',');
						} else {
							sb.append(savedperms.indexOf(permission) + 1).append(',');
						}*/
					statement.executeUpdate();
				}
			}
		}
	}
	public void close() throws SQLException {
		connection.close();
	}
	public void dropDatabase() throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("DROP DATABASE minecord;");
		}
	}
}
