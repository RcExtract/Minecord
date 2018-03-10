package com.rcextract.minecord.sql;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.rcextract.minecord.utils.ComparativeSet;
import com.rcextract.minecord.utils.Table;

public class SQLObjectConvertor {

	private static final Set<Class<?>> primitives;
	private static final Set<TypeConverter<?, ?>> DEFAULT_CONVERTERS;
	static {
		primitives = new HashSet<Class<?>>();
		primitives.add(Boolean.class);
		primitives.add(Character.class);
		primitives.add(Number.class);
		primitives.add(String.class);
		DEFAULT_CONVERTERS = new HashSet<TypeConverter<?, ?>>();
		DEFAULT_CONVERTERS.add(new TypeConverter<UUID, String>(UUID.class, String.class) {

			@Override
			public String serialize(UUID input) {
				return input.toString();
			}

			@Override
			public UUID deserialize(String output) {
				return UUID.fromString(output);
			}
			
		});
		DEFAULT_CONVERTERS.add(new TypeConverter<OfflinePlayer, UUID>(OfflinePlayer.class, UUID.class) {

			@Override
			public UUID serialize(OfflinePlayer input) {
				return input.getUniqueId();
			}

			@Override
			public OfflinePlayer deserialize(UUID output) {
				return Bukkit.getOfflinePlayer(output);
			}
			
		});
	}
	public static Set<TypeConverter<?, ?>> getDefaultConverters() {
		return DEFAULT_CONVERTERS;
	}
	
	private ComparativeSet<TypeConverter<?, ?>> converters;
	private final Connection connection;

	public SQLObjectConvertor(String url, String user, String password) throws SQLException {
		this(DriverManager.getConnection(url, user, password));
	}
	
	public SQLObjectConvertor(Connection connection) {
		Validate.notNull(connection);
		this.connection = connection;
		this.converters = new ComparativeSet<TypeConverter<?, ?>>(converter -> {
			for (TypeConverter<?, ?> c : converters) 
				if (converter.getInputClass() == c.getInputClass() || converter.getOutputClass() == c.getOutputClass()) 
					return false;
			return true;
		});
		this.converters.addAll(DEFAULT_CONVERTERS);
	}
	
	public Connection getConnection() {
		return connection;
	}
	public ComparativeSet<TypeConverter<?, ?>> getConverters() {
		return converters;
	}
	public List<?> load(String table) throws DatabaseAccessException, SQLTimeoutException, InvocationTargetException, DataLoadException {
		try (Statement statement = connection.createStatement()) {
			List<Object> objects = new ArrayList<Object>();
			ResultSet rs = statement.executeQuery("SELECT * FROM tables_info WHERE table_name = " + table);
			Class<?> clazz = null;
			while (rs.next()) clazz = Class.forName(rs.getString("implementing_class"));
			ResultSet set = statement.executeQuery("SELECT * FROM " + table);
			while (set.next()) 
				objects.add(clazz.getConstructor(Map.class).newInstance(loadFields(table, set.getRow())));
			return objects;
		} catch (SQLTimeoutException e) {
			throw e;
		} catch (SQLException e) {
			throw new DatabaseAccessException();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
			throw new InvalidTypeException();
		} catch (InvocationTargetException e) {
			throw e;
		} catch (DataLoadException e) {
			throw e;
		} catch (ClassNotFoundException e) {
			throw new DataLoadException(e);
		}
	}
	public Map<String, ?> loadFields(String table, int index) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, InvocationTargetException {
		try (Statement statement = connection.createStatement()) {
			ResultSet set = statement.executeQuery("SELECT * FROM " + table);
			set.absolute(index);
			Map<String, Object> map = new HashMap<String, Object>();
			ResultSetMetaData metadata = set.getMetaData();
			for (int i = 1; i <= metadata.getColumnCount(); i++) {
				Class<?> type = Class.forName(metadata.getColumnClassName(i));
				String name = metadata.getColumnName(i);
				Object object = set.getObject(i, type);
				if (name.equals("inheriting_entry")) {
					//"table_name entry_index
					String[] params = set.getString(i).split(" ");
					map.putAll(loadFields(params[0], Integer.parseInt(params[1])));
				}
				if (object instanceof String && ((String) object).startsWith("list_from_table_")) 
					object = load(((String) object).substring(15));
				if (primitives.contains(type)) 
					for (int in = 0; in < converters.size(); in++) {
						TypeConverter<?, ?> c = converters.toArray(new TypeConverter<?, ?>[converters.size()])[in];
						if (c.getOutputClass() == type) {
							object = c.getClass().getMethod("deserialize", type).invoke(c, object);
							type = c.getInputClass();
							in = 0;
						}
				}
				map.put(name, object);
			}
			return map;
		} catch (SQLTimeoutException e) {
			throw e;
		} catch (SQLException e) {
			throw new DatabaseAccessException();
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
			throw new InvalidTypeException();
		} catch (InvocationTargetException e) {
			throw e;
		} catch (DataLoadException e) {
			throw e;
		} catch (ClassNotFoundException e) {
			throw new DataLoadException(e);
		}
	}

	public String save(SQLList<? extends DatabaseSerializable> list) throws SQLTimeoutException, DatabaseAccessException {
		Class<? extends DatabaseSerializable> clazz = list.getDeclaringClass();
		if (!(validateClass(clazz))) throw new InvalidTypeException();
		SerializableAs annotation = clazz.getDeclaredAnnotation(SerializableAs.class);
		String tablename = annotation.value();
		try (Statement statement = connection.createStatement()) {
			ResultSet set = statement.executeQuery("SELECT * FROM tables_info WHERE implementing_class = \'" + clazz.getName() + "\'");
			set.absolute(1);
			statement.executeUpdate("DROP TABLE " + set.getString("table_name"));
			set.deleteRow();
			String sql = "CREATE TABLE " + tablename + " (";
			String savesql = "INSERT INTO " + tablename + " VALUES (";
			for (DatabaseSerializable ds : list) {
				Table<Integer, String, Object> table = new Table<Integer, String, Object>();
				Map<String, Object> map = ds.serialize();
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					String columnname = entry.getKey();
					Object object = entry.getValue();
					table.put(table.size() + 1, columnname, object);
					String typename = object.getClass().getSimpleName();
					if (typename.equalsIgnoreCase("String")) typename = "varchar";
					if (typename.equalsIgnoreCase("Long")) typename = "longvarbinary";
					if (typename.equalsIgnoreCase("Short")) typename = "double";
					sql += columnname + " " + typename.toUpperCase() + ", ";
					savesql += "?, ";
					break;
				}
				statement.executeUpdate(sql.substring(0, sql.length() - 3) + ");");
				statement.executeUpdate("UPDATE tables_info SET table_name = " + tablename + " WHERE implementing_class = \'" + clazz.getName() + "\'");
				try (PreparedStatement stmt = connection.prepareStatement(savesql)) {
					for (Map.Entry<String, Object> entry : map.entrySet()) {
						Object object = entry.getValue();
						if (object.getClass().isArray()) 
							object = Arrays.asList((Object[]) object);
						if (object instanceof SQLList) 
							object = "list_from_table_" + save((SQLList<?>) object);
						Class<?> type = object.getClass();
						if (!(primitives.contains(type))) 
							for (int i = 0; i < converters.size(); i++) {
								TypeConverter<?, ?> c = converters.toArray(new TypeConverter<?, ?>[converters.size()])[i];
								if (c.getInputClass() == type) {
									object = c.getClass().getMethod("serialize", type).invoke(c, object);
									type = c.getInputClass();
									i = 0;
								}
							}
						stmt.getClass().getMethod("set" + object.getClass().getSimpleName(), int.class, object.getClass()).invoke(stmt, table.bc(entry.getKey(), object), object);
					}
					stmt.executeUpdate();
				}
			}
		} catch (SQLTimeoutException e) {
			throw e;
		} catch (SQLFeatureNotSupportedException e) {
			//This exception is never thrown.
		} catch (SQLException e) {
			throw new DatabaseAccessException();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			//These exceptions are never thrown.
		}
		return tablename;
	}
	public boolean validateClass(Class<?> clazz) {
		try {
			clazz.getConstructor(Map.class);
			clazz.getDeclaredMethod("serialize");
			return clazz.getDeclaredAnnotationsByType(SerializableAs.class)[0].value().isEmpty();
		} catch (NoSuchMethodException | SecurityException | ArrayIndexOutOfBoundsException | NullPointerException e) {
			return false;
		}
	}
}
