package com.rcextract.minecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.rcextract.minecord.permissions.Permission;

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
			statement.execute("CREATE TABLE IF NOT EXISTS servers (id INT UNSIGNED PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE KEY, description VARCHAR(255), approvement BOOLEAN NOT NULL, invitation BOOLEAN NOT NULL, permanent BOOLEAN NOT NULL, locked BOOLEAN NOT NULL);");
			statement.execute("CREATE TABLE IF NOT EXISTS channels (server INT UNSIGNED NOT NULL, id INT UNSIGNED PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE KEY, description VARCHAR(255), locked BOOLEAN NOT NULL, main BOOLEAN NOT NULL);");
			statement.execute("CREATE TABLE IF NOT EXISTS ranks (server INT UNSIGNED NOT NULL, name VARCHAR(255) NOT NULL UNIQUE KEY, description VARCHAR(255), tag VARCHAR(255) PRIMARY KEY, admin BOOLEAN NOT NULL, override BOOLEAN NOT NULL, permissions TEXT(65535));");
			statement.execute("CREATE TABLE IF NOT EXISTS users (server INT UNSIGNED, channel INT UNSIGNED, rank VARCHAR(255), id INT UNSIGNED PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE KEY, nickname VARCHAR(255) NOT NULL, description VARCHAR(255), uuid VARCHAR(255) NOT NULL);");
		}
	}
	public synchronized void load() throws SQLException {
		try (Statement statement = connection.createStatement()) {
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
				Set<Permission> permissions = new HashSet<Permission>();
				if (!(rankset.getString("permissions").isEmpty())) 
					for (String permission : rankset.getString("permissions").split(",")) { 
						permissions.add(Permission.valueOf(Integer.parseInt(permission)));
					}
				Rank rank = new Rank(name, desc, tag, admin, override, permissions);
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
				User user = new User(id, name, nickname, desc, player.getUniqueId(), channel, rank);
				Minecord.getControlPanel().users.add(user);
			}
		}
	}
	public synchronized void save() throws SQLException {
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
						sb.append(permission.getIdentifier()).append(',');
					rankstmt.setString(7, sb.toString());
					rankstmt.executeUpdate();
				}
			}
		}
		try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, ?);")) {
			for (User user : Minecord.getUserManager().getUsers()) {
				try {
					statement.setInt(1, user.getChannel().getChannelManager().getServer().getIdentifier());
					statement.setInt(2, user.getChannel().getIdentifier());
					statement.setString(3, user.getRank().getTag());
				} catch (NullPointerException e) {
					statement.setNull(1, Types.INTEGER);
					statement.setNull(2, Types.INTEGER);
					statement.setNull(3, Types.VARCHAR);
				}
				statement.setInt(4, user.getIdentifier());
				statement.setString(5, user.getName());
				statement.setString(6, user.getNickName());
				statement.setString(7, user.getDescription());
				statement.setString(8, user.getUUID().toString());
				statement.executeUpdate();
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
