package com.rcextract.minecord;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;

import com.rcextract.minecord.utils.Pair;
import com.rcextract.minecord.utils.Table;

/*
 * servers: id, name, description, approvement, invitation, permanent, locked
 * channels: server, id, name, description, locked, main
 * ranks: server, name, description, tag, admin, override, permissions, main
 * sendables: arg1, arg2, arg3...
 * sendables_tableinfo: column_id, column_type, sendable_class, arg_id
 * channelopts: user, channel, notify, i
 * sendableopts: sendable, server, state("none"/"invited"/"approving"/"joined"), rank, permissions
 * permissions: id, permission
 */

public final class DataManipulator implements Listener {

	private static final String PROTOCOL = "jdbc:mysql://";
	private static final Set<SendableClass<? extends Sendable>> classes = new HashSet<SendableClass<? extends Sendable>>();
	public static String getProtocol() {
		return PROTOCOL;
	}
	public static Set<SendableClass<? extends Sendable>> getRegisteredClasses() {
		return Collections.unmodifiableSet(classes);
	}
	public static <T extends Sendable> boolean register(Class<T> clazz, String serializer, String deserializer) throws NoSuchMethodException, SecurityException {
		Method method = null;
		for (Method m : clazz.getDeclaredMethods()) 
			if (m.getName() == serializer) {
				method = m;
				break;
			}
		Method de = null;
		for (Method m : clazz.getDeclaredMethods()) 
			if (m.getName() == deserializer && Modifier.isStatic(m.getModifiers()) && m.getReturnType() == clazz) {
				method = m;
				break;
			}
		return classes.add(new SendableClass<T>(clazz, method, de));
	}
	public static boolean unregister(Class<?> clazz) {
		for (SendableClass<? extends Sendable> s : classes) 
			if (s.getDeclaringClass() == clazz) 
				return classes.remove(s);
		return false;
	}
	public static SendableClass<? extends Sendable> getSendableClass(Class<?> clazz) {
		for (SendableClass<? extends Sendable> s : classes) 
			if (s.getDeclaringClass() == clazz) 
				return s;
		return null;
	}
	private final Connection connection;
	public DataManipulator(String url, String user, String password) throws SQLTimeoutException, SQLConnectException {
		this(url, user, password, true);
	}
	public DataManipulator(String url, String user, String password, boolean autoreconnect) throws SQLTimeoutException, SQLConnectException {
		Validate.notNull(url);
		try {
			this.connection = DriverManager.getConnection(PROTOCOL + url + "?autoReconnect=" + Boolean.toString(autoreconnect) + "&useSSL=false", user, password);
		} catch (SQLTimeoutException e) {
			throw e;
		} catch (SQLException e) {
			throw new SQLConnectException();
		}
	}
	public Connection getConnection() {
		return connection;
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
	public synchronized void load() throws DataLoadException {
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
				//Server missing main, channels, and sendable options
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
				//Channel done
				if (server != null) server.getChannels().add(channel);
				if (channelset.getBoolean("main")) server.setMain(channel);
				//Server missing sendable options
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
						perms.add(permissions.get(Integer.parseInt(permission) - 1));
				Rank rank = new Rank(name, desc, tag, admin, override, perms);
				//Rank done
				if (server != null) server.getRankManager().ranks.add(rank);
				if (rankset.getBoolean("main")) server.getRankManager().setMain(rank);
			}
			ResultSet sendableset = statement.executeQuery("SELECT * FROM sendables");
			ResultSet info = statement.executeQuery("SELECT * FROM sendables_tableinfo");
			//The first one is for storing the column id, or index in the table, exists due to entries inside Table does not store in orders.
			//The second one is for storing the column type converted from the SQL type, helping to use the correct get method in sendableset ResultSet.
			//The third one is a pair
			//In the pair, the left one is for storing which class is responsible for this column. In SQL table, if the entry is representing an instance of Class B, while the class responsible for the column is A, the entry should have null on this column.
			//In the pair, the right one is used to determine the sequence of the arguments when putting them into a suitable constructor of the class responsible.
			Table<Integer, Class<?>, Pair<Class<?>, Integer>> table = new Table<Integer, Class<?>, Pair<Class<?>, Integer>>();
			while (info.next()) {
				int column = info.getInt("column_id");
				Class<?> type = Class.forName(info.getString("column_type").replaceAll("type", ""));
				Class<?> clazz = Class.forName(info.getString("sendable_class").replaceAll("type", ""));
				if (getSendableClass(clazz) == null) throw new ClassNotFoundException();
				int argid = info.getInt("arg_id");
				table.put(column, type, new Pair<Class<?>, Integer>(clazz, argid));
			}
			while (sendableset.next()) {
				Map<Integer, Object> objects = new HashMap<Integer, Object>();
				Class<?> clazz = null;
				for (int column : table.aSet().keySet()) {
					Class<?> type = table.a(column).keySet().iterator().next();
					Object object = sendableset.getClass().getMethod("get" + type.getSimpleName(), String.class).invoke(sendableset, "arg" + Integer.toString(column));
					Pair<Class<?>, Integer> value = table.ab(column, type);
					int argid = value.getValue();
					if (object != null) {
						objects.put(argid, object);
						if (clazz == null) 
							clazz = value.getKey();
						else if (clazz != value.getKey()) 
							throw new DataLoadException("Error!");
					}
				}
				List<Object> list = new ArrayList<Object>();
				List<Class<?>> parameters = new ArrayList<Class<?>>();
				for (Map.Entry<Integer, Object> entry : objects.entrySet()) 
					if (entry.getKey() == list.size()) {
						list.add(entry.getValue());
						parameters.add(entry.getValue().getClass());
					}
				//The sendable object.
				Minecord.getControlPanel().sendables.add((Sendable) getSendableClass(clazz).getDeserializer().invoke(null, list));
				//Sendables done
			}
			ResultSet sendableoptset = statement.executeQuery("SELECT * FROM sendableopts");
			while (sendableoptset.next()) {
				Sendable sendable = Minecord.getUserManager().getSendable(sendableoptset.getInt("sendable"));
				Server server = Minecord.getServerManager().getServer(sendableoptset.getInt("server"));
				JoinState state = JoinState.valueOf(sendableoptset.getString("state"));
				Rank rank = server.getRankManager().getRank(sendableoptset.getString("rank"));
				Set<Permission> perms = new HashSet<Permission>();
				if (!(rankset.getString("permissions").isEmpty())) 
					for (String permission : rankset.getString("permissions").split(",")) 
						perms.add(permissions.get(Integer.parseInt(permission) - 1));
				server.getSendableOptions().add(new SendableOptions(sendable, state, rank, perms.toArray(new Permission[perms.size()])));
			}
		} catch (SQLException | ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new DataLoadException(e);
		}
	}
	public synchronized void save() throws DataLoadException {
		List<Permission> savedperms = new ArrayList<Permission>();
		try (PreparedStatement permstmt = connection.prepareStatement("INSERT INTO permissions VALUES (DEFAULT, ?);")) {
			try (PreparedStatement serverstmt = connection.prepareStatement("INSERT INTO servers VALUES (?, ?, ?, ?, ?, ?, ?);"); 
					PreparedStatement channelstmt = connection.prepareStatement("INSERT INTO channels VALUES (?, ?, ?, ?, ?, ?);"); 
					PreparedStatement rankstmt = connection.prepareStatement("INSERT INTO ranks VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
					PreparedStatement sendableoptstmt = connection.prepareStatement("INSERT INTO sendableopts VALUES (?, ?, ?, ?, ?);")) {
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
					for (SendableOptions option : server.getSendableOptions()) {
						sendableoptstmt.setInt(1, option.getSendable().getIdentifier());
						sendableoptstmt.setInt(2, server.getIdentifier());
						sendableoptstmt.setString(3, option.getState().toString().toLowerCase());
						sendableoptstmt.setString(4, option.getRank().getTag());
						StringBuilder sb = new StringBuilder();
						for (Permission permission : option.getPermissions()) 
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
						sendableoptstmt.setString(5, permissions);
						sendableoptstmt.executeUpdate();
					}
				}
			}
			Table<Integer, Class<?>, Pair<Class<?>, Integer>> table = new Table<Integer, Class<?>, Pair<Class<?>, Integer>>();
			try (PreparedStatement infostmt = connection.prepareStatement("INSERT INTO sendables_tableinfo VALUES (?, ?, ?, ?")) {
				loop:
				for (Sendable sendable : Minecord.getUserManager().getSendables()) {
					for (Pair<Class<?>, Integer> pair : table.cSet().keySet()) 
						if (pair.getKey() == sendable.getClass()) continue loop;
					Class<? extends Sendable> clazz = sendable.getClass();
					int current = 0;
					for (int i : table.aSet().keySet()) 
						if (i > current) current = i;
					current++;
					int loop = 0;
					infostmt.setInt(1, current);
					infostmt.setString(3, "type" + clazz.getName() + "type");
					for (Class<?> param : getSendableClass(clazz).getDeserializer().getParameterTypes()) {
						table.put(current, param, new Pair<Class<?>, Integer>(clazz, loop));
						infostmt.setString(2, "type" + param.getName() + "type");
						infostmt.setInt(4, loop);
						infostmt.executeUpdate();
						loop++;
					}
				}
			}
			int columns;
			try (Statement statement = connection.createStatement()) {
				columns = reconstructSendablesTable(table, statement);
			}
			String stmt = "INSERT INTO sendables VALUES (";
			for (int i = 0; i < columns; i++) stmt += "?,";
			stmt = stmt.substring(0, stmt.length() - 2) + ");";
			boolean exception = false;
			try (PreparedStatement sendablestmt = connection.prepareStatement(stmt)) {
				for (Sendable sendable : Minecord.getUserManager().getSendables()) {
					List<Object> list = sendable.values();
					Class<? extends Sendable> clazz = sendable.getClass();
					List<Class<?>> params = new ArrayList<Class<?>>();
					for (Object o : list) params.add(o.getClass());
					if (!(Arrays.asList(getSendableClass(clazz).getDeserializer().getParameterTypes()).containsAll(list) && Arrays.asList(getSendableClass(clazz).getDeserializer().getParameterTypes()).containsAll(list))) 
						exception = exception || true;
					for (Object o : list) {
						Class<?> type = o.getClass();
						int argid = list.indexOf(o);
						int columnid = table.bc(type, new Pair<Class<?>, Integer>(clazz, argid));
						sendablestmt.getClass().getMethod("get" + type.getSimpleName(), int.class, type).invoke(sendablestmt, columnid, o);
					}
					sendablestmt.executeUpdate();
				}
			}
			//Not done!
		} catch (SQLException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new DataLoadException(e);
		}
	}
	public int reconstructSendablesTable(Table<Integer, Class<?>, Pair<Class<?>, Integer>> format, Statement statement) throws SQLException {
		statement.executeUpdate("DROP TABLE sendables;");
		StringBuilder stmt = new StringBuilder("CREATE TABLE sendables (");
		int columns = 0;
		for (Table.Entry<Integer, Class<?>, Pair<Class<?>, Integer>> entry : format.getEntries()) {
			stmt.append("arg" + Integer.toString(entry.getA())).append(" ")
			.append(entry.getB().getSimpleName()).append(", ");
			columns++;
		}
		statement.executeUpdate(stmt.toString().substring(0, stmt.length() - 3) + ");");
		return columns;
	}
}
