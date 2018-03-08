package com.rcextract.minecord.sql;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rcextract.minecord.utils.ComparativeSet;
import com.rcextract.minecord.utils.Table;

public class SQLCustomTypeUtils {

	private ComparativeSet<TypeConvertor<?, ?>> convertors;
	private final Connection connection;

	public SQLCustomTypeUtils(String url, String user, String password) throws SQLException {
		this.connection = DriverManager.getConnection(url, user, password);
		this.convertors = new ComparativeSet<TypeConvertor<?, ?>>(convertor -> {
			for (TypeConvertor<?, ?> c : convertors) 
				if (convertor.getA() == c.getA() || convertor.getR() == c.getR()) 
					return false;
			return true;
		});
	}
	public Connection getConnection() {
		return connection;
	}
	public ComparativeSet<TypeConvertor<?, ?>> getConvertors() {
		return convertors;
	}
	public List<?> load(String table) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		try (Statement statement = connection.createStatement()) {
			List<Object> objects = new ArrayList<Object>();
			ResultSet rs = statement.executeQuery("SELECT * FROM tables_info WHERE table_name = " + table);
			Class<?> clazz = null;
			while (rs.next()) clazz = Class.forName(rs.getString("implementing_class"));
			ResultSet set = statement.executeQuery("SELECT * FROM " + table);
			while (set.next()) 
				objects.add(clazz.getConstructor(Map.class).newInstance(loadFields(table, set.getRow())));
			return objects;
		}
	}
	public Map<String, ?> loadFields(String table, int index) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
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
				final Class<?> newtype = object.getClass();
				Set<TypeConvertor<?, ?>> cs = convertors.getIf(convertor -> convertor.getR() == newtype);
				if (!(cs.isEmpty())) {
					TypeConvertor<?, ?> c = cs.iterator().next();
					object = c.getDeserializer().getClass().getMethod("convert", c.getR()).invoke(c.getDeserializer(), object);
				}
				map.put(name, object);
			}
			return map;
		}
	}

	public String save(SQLList<?> list) throws SQLException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<? extends DatabaseSerializable> clazz = list.getDeclaringClass();
		if (!(validateClass(clazz))) throw new IllegalArgumentException();
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
						final Class<?> type = object.getClass();
						Set<TypeConvertor<?, ?>> cs = convertors.getIf(convertor -> convertor.getA() == type);
						if (!(cs.isEmpty())) {
							TypeConvertor<?, ?> c = cs.iterator().next();
							object = c.getSerializer().getClass().getMethod("serialize", c.getA()).invoke(c.getSerializer(), object);
						}
						stmt.getClass().getMethod("set" + object.getClass().getSimpleName(), int.class, object.getClass()).invoke(stmt, table.bc(entry.getKey(), object), object);
					}
					stmt.executeUpdate();
				}
			}
		}
		return tablename;
	}
	private boolean validateClass(Class<?> clazz) {
		try {
			clazz.getConstructor(Map.class);
			clazz.getDeclaredMethod("serialize");
			return clazz.getDeclaredAnnotationsByType(SerializableAs.class)[0].value().isEmpty();
		} catch (NoSuchMethodException | SecurityException | ArrayIndexOutOfBoundsException | NullPointerException e) {
			return false;
		}
	}
}
