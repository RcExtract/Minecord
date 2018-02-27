//Not implemented!
package com.rcextract.minecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import java.sql.Types;
import java.util.ArrayList;
//import java.util.Collection;
import java.util.HashSet;
import java.util.List;
//import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permission;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

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
	public Boolean loadDataFromOldDb = false;
	/**
	 * This constructor is reserved for initialization.
	 * @throws ClassNotFoundException 
	 */
	protected DatabaseManager() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		this.connection = DriverManager.getConnection("jdbc:mysql://" + Minecord.getHost() + "?autoReconnect=true&useSSL=false", Minecord.getUsername(), Minecord.getPassword());
	}
	public synchronized void initialize() throws SQLException {
		ResultSet databases = connection.getMetaData().getCatalogs();
		Set<String> dbnames = new HashSet<String>();
		while (databases.next()) 
			dbnames.add(databases.getString("TABLE_CAT"));
		if (dbnames.contains("minecord5dot1")) loadDataFromOldDb = true;
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName() + ";");
		}
		connection.setCatalog(loadDataFromOldDb ? oldDatabaseName() : databaseName());
		if (loadDataFromOldDb) return;
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS servers (id INT UNSIGNED PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE KEY, description VARCHAR(255), approvement BOOLEAN NOT NULL, invitation BOOLEAN NOT NULL, permanent BOOLEAN NOT NULL, locked BOOLEAN NOT NULL);");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS channels (server INT UNSIGNED NOT NULL, id INT UNSIGNED PRIMARY KEY, name VARCHAR(255) NOT NULL, description VARCHAR(255), locked BOOLEAN NOT NULL, main BOOLEAN NOT NULL);");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS ranks (server INT UNSIGNED NOT NULL, name VARCHAR(255) NOT NULL, description VARCHAR(255), tag VARCHAR(255) PRIMARY KEY, admin BOOLEAN NOT NULL, override BOOLEAN NOT NULL, permissions TEXT(65535), main BOOLEAN NOT NULL);");
			//statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (server INT UNSIGNED NOT NULL, channel INT UNSIGNED NOT NULL, rank VARCHAR(255) NOT NULL, id INT UNSIGNED PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE KEY, nickname VARCHAR(255) NOT NULL, description VARCHAR(255), uuid VARCHAR(255) NOT NULL);");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (id INT UNSIGNED PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE KEY, nickname VARCHAR(255) NOT NULL, description VARCHAR(255), uuid VARCHAR(255) NOT NULL);");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS identities (id INT UNSIGNED NOT NULL, user INT UNSIGNED NOT NULL, server INT UNSIGNED NOT NULL, activated BOOLEAN NOT NULL, rank VARCHAR(255) NOT NULL);");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS ChannelPreferences (identity INT UNSIGNED NOT NULL, server INT UNSIGNED NOT NULL, channel INT UNSIGNED NOT NULL, notify BOOLEAN NOT NULL, count INT UNSIGNED NOT NULL, user INT UNSIGNED);");
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
				Server server = new Server(id, name, desc, approvement, invitation, permanent, locked, new RankManager(), null);
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
				if (server != null) server.getChannels().add(channel);
				if (channelset.getBoolean("main")) server.setMain(channel);
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
				if (rankset.getBoolean("main")) server.getRankManager().setMain(rank);
			}
			ResultSet userset = statement.executeQuery("SELECT * FROM users;");
			while (userset.next()) {
				//Server server = Minecord.getServerManager().getServer(userset.getInt("server"));
				//Channel channel = server == null ? null : server.getChannel(userset.getInt("channel"));
				//Rank rank = server == null ? null : server.getRankManager().getRankByTag(userset.getString("rank"));
				int id = userset.getInt("id");
				String name = userset.getString("name");
				String nickname = userset.getString("nickname");
				String desc = userset.getString("description");
				OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(userset.getString("uuid")));
				User user = new User(id, name, nickname, desc, player, null);
				Minecord.getControlPanel().users.add(user);
			}
			Table<Integer, User, ChannelPreference> ChannelPreferences = HashBasedTable.create();
			ResultSet ChannelPreferenceset = statement.executeQuery("SELECT * FROM ChannelPreferences;");
			while (ChannelPreferenceset.next()) {
				Integer identityid = ChannelPreferenceset.getInt("identity");
				Server server = Minecord.getServerManager().getServer(ChannelPreferenceset.getInt("server"));
				Channel channel = server == null ? null : server.getChannel(ChannelPreferenceset.getInt("channel"));
				boolean notify = ChannelPreferenceset.getBoolean("notify");
				int index = ChannelPreferenceset.getInt("count");
				User user = Minecord.getUserManager().getUser(ChannelPreferenceset.getInt("id"));
				ChannelPreferences.put(identityid, user, new ChannelPreference(channel, notify, index));
			}
			/*Table<User, ServerIdentity, Integer> identities = HashBasedTable.create();
			ResultSet identityset = statement.executeQuery("SELECT * FROM identities;");
			while (identityset.next()) {
				User user = Minecord.getUserManager().getUser(identityset.getInt("user"));
				Server server = Minecord.getServerManager().getServer(identityset.getInt("server"));
				boolean activated = identityset.getBoolean("activated");
				Rank rank = server == null ? null : server.getRankManager().getRankByTag(identityset.getString("rank"));
				int id = identityset.getInt("id");
				Collection<ChannelPreference> putChannelPreferences = ChannelPreferences.row(id).values();
				identities.put(user, new ServerIdentity(server, activated, rank, putChannelPreferences.toArray(new ChannelPreference[putChannelPreferences.size()])), id);
			}
			for (User user : Minecord.getUserManager().getUsers()) {
				for (ServerIdentity identity : identities.row(user).keySet())
					user.addIdentity(identity);
				user.setMain(ChannelPreferences.column(user).values().iterator().next());
			}*/
		}
	}
	public synchronized void loadFromOld() throws SQLException {
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
				Server server = new Server(id, name, desc, approvement, invitation, permanent, locked, new RankManager(), null);
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
				if (server != null) server.getChannels().add(channel);
				if (channelset.getBoolean("main")) server.setMain(channel);
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
				if (rankset.getBoolean("main")) server.getRankManager().setMain(rank);
			}
			/*ResultSet userset = statement.executeQuery("SELECT * FROM users;");
			while (userset.next()) {
				Server server = Minecord.getServerManager().getServer(userset.getInt("server"));
				Channel channel = server == null ? null : server.getChannel(userset.getInt("channel"));
				Rank rank = server == null ? null : server.getRankManager().getRankByTag(userset.getString("rank"));
				int id = userset.getInt("id");
				String name = userset.getString("name");
				String nickname = userset.getString("nickname");
				String desc = userset.getString("description");
				OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(userset.getString("uuid")));
				ChannelPreference ChannelPreference = new ChannelPreference(channel, true, 0);
				User user = new User(id, name, nickname, desc, player, ChannelPreference, new ServerIdentity(server, true, rank, ChannelPreference));
				Minecord.getControlPanel().users.add(user);
			}*/
		}
	}
	public synchronized void save() throws SQLException {
		List<Permission> savedperms = new ArrayList<Permission>();
		try (PreparedStatement permstmt = connection.prepareStatement("INSERT INTO permissions VALUES (DEFAULT, ?);")) {
			try (PreparedStatement serverstmt = connection.prepareStatement("INSERT INTO servers VALUES (?, ?, ?, ?, ?, ?, ?);"); 
					PreparedStatement channelstmt = connection.prepareStatement("INSERT INTO channels VALUES (?, ?, ?, ?, ?, ?);"); 
					PreparedStatement rankstmt = connection.prepareStatement("INSERT INTO ranks VALUES (?, ?, ?, ?, ?, ?, ?, ?);")) {
				for (Server server : Minecord.getServerManager().getServers()) {
					serverstmt.setInt(1, server.getIdentifier());
					serverstmt.setString(2, server.getName());
					serverstmt.setString(3, server.getDescription());
					serverstmt.setBoolean(4, server.needApprovement());
					serverstmt.setBoolean(5, server.needInvitation());
					serverstmt.setBoolean(6, server.isPermanent());
					serverstmt.setBoolean(7, !(server.ready()));
					serverstmt.executeUpdate();
					for (Channel channel : server.getChannels()) {
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
						rankstmt.setBoolean(8, rank.isMain());
						rankstmt.executeUpdate();
					}
				}
			}
			try (PreparedStatement userstmt = connection.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?, ?);")) {
				for (User user : Minecord.getUserManager().getUsers()) {
					userstmt.setInt(1, user.getIdentifier());
					userstmt.setString(2, user.getName());
					userstmt.setString(3, user.getNickName());
					userstmt.setString(4, user.getDescription());
					userstmt.setString(5, user.getPlayer().getUniqueId().toString());
					userstmt.executeUpdate();
					try (PreparedStatement istmt = connection.prepareStatement("INSERT INTO identities VALUES (?, ?, ?, ?, ?)")) {
						/*Set<Integer> usedids = new HashSet<Integer>();
						for (ServerIdentity identity : user.getIdentities()) {
							int id = new Random().nextInt();
							while (usedids.contains(id)) id = new Random().nextInt();
							istmt.setInt(1, id);
							istmt.setInt(2, user.getIdentifier());
							istmt.setInt(3, identity.getServer().getIdentifier());
							istmt.setBoolean(4, identity.isJoined());
							istmt.setString(5, identity.getRank().getTag());
							try (PreparedStatement lstmt = connection.prepareStatement("INSERT INTO ChannelPreferences VALUES (?, ?, ?, ?, ?, ?)")) {
								for (ChannelPreference ChannelPreference : identity.getChannelPreferences()) {
									lstmt.setInt(1, id);
									lstmt.setInt(2, identity.getServer().getIdentifier());
									lstmt.setInt(3, ChannelPreference.getChannel().getIdentifier());
									lstmt.setBoolean(4, ChannelPreference.isNotify());
									lstmt.setInt(5, ChannelPreference.getIndex());
									if (ChannelPreference.isMain()) 
										lstmt.setInt(6, user.getIdentifier());
									else 
										lstmt.setNull(6, Types.INTEGER);
								}
							}
						}*/
					}
				}
			}
		}
	}
	public void close() throws SQLException {
		connection.close();
	}
	public void dropDatabase() throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("DROP DATABASE " + databaseName());
		}
	}
	public String databaseName() {
		return "minecord" + Minecord.dbversion;
	}
	public String oldDatabaseName() {
		return "minecord" + Minecord.olddbversion;
	}
}
