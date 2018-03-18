package com.rcextract.minecord.sql;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;

import com.rcextract.minecord.Minecord;
import com.rcextract.minecord.utils.ArrayMap;
import com.rcextract.minecord.utils.ComparativeSet;
import com.rcextract.minecord.utils.Pair;
import com.rcextract.minecord.utils.Table;

/*
 * +-----------------+----------------------------------+-------------+
 * |   TABLE NAME    |        IMPLEMENTING CLASS        | OMIT COLUMN |
 * +-----------------+----------------------------------+-------------+
 * |     server      |  com.rcextract.minecord.Server   |    FALSE    |
 * +-----------------+----------------------------------+-------------+
 * |    subserver    | com.rcextract.minecord.SubServer |    FALSE    |
 * +-----------------+----------------------------------+-------------+
 * |    channels     |  com.rcextract.minecord.Channel  |    FALSE    |
 * +-----------------+----------------------------------+-------------+
 * |     players     |          java.util.UUID          |    TRUE     |
 * +-----------------+----------------------------------+-------------+
 * 
 * +------+----------+--------------------------+---------------------------------+
 * | NAME |   DESC   |         CHANNELS         |           MAIN CHANNEL          |
 * +------+----------+--------------------------+---------------------------------+
 * | test | default  | list_from_table_channels | object_from_table_channels_at_1 |
 * +------+----------+--------------------------+---------------------------------+
 * 
 * +-------+------------------+
 * | DUMMY | INHERITING ENTRY |
 * +-------+------------------+
 * | false |        1         |
 * +-------+------------------+
 * 
 * +------+------+-------------------------+
 * | NAME | DESC |         PLAYERS         |
 * +------+------+-------------------------+
 * | test | test | list_from_table_players |
 * +------+------+-------------------------+
 * 
 * List Reference Syntax: list from table [table name] (range [fromIndex] to [toIndex]) ...
 * use range [fromIndex] to [toIndex] to limit entries to which their entry counts are in range.
 * 
 * Object Reference Syntax: object from table [table name] at [index]
 */
/**
 * An advanced converter class which provides utility methods for saving objects to the 
 * desired database and loading objects from it without using the "CREATE TYPE".
 * <p>
 * The reason why "CREATE TYPE" is not implemented, is that firstly, not all SQL 
 * servers support this feature, and secondly, SQL is not made for us to save data like 
 * that. Columns should represent fields in a class in Java, and rows should represent 
 * objects of that class. Therefore, a converter is built for programmers to save Java 
 * objects without rewriting what is here.
 * <p>
 * This converter also supports Java object referencing. For example, when both object 
 * A and object B stores the same object C before saving, it will still be the same 
 * after saving and loading. This system applies to all objects saved, except for 
 * numbers and booleans, because they will always return true when compared with == 
 * while returning true when compared with {@link Object#equals(Object)}.
 * <p>
 * Besides, this converter does not serialize an Object storing elements into a complex 
 * string. Instead, it will make a string storing the objects being referenced by the 
 * object, and save them if not done. This mechanics currently applies interally to 
 * {@link ArrayList}, {@link HashMap}, {@link SQLList}, and {@link ArrayMap}.
 * <p>
 * It is strongly recommended to put an {@link SQLList} into the map returning from the 
 * {@link DatabaseSerializable#serialize()} than {@link ArrayList}, so the converter 
 * can get the element type most safely.
 * <p>
 * Besides, this class also supports serializing and deserializing objects which type 
 * is not implementing the interface, by adding converters to this converter.
 */
public class SQLObjectConverter {
	
	private ComparativeSet<TypeConverter<?, ?>> converters;
	private final Connection connection;
	private Map<String, Class<?>> tables = new HashMap<String, Class<?>>();
	private Map<Object, Pair<String, Integer>> savedObjects = new HashMap<Object, Pair<String, Integer>>();
	private Map<Object, Pair<String, Integer>> loadedObjects = new HashMap<Object, Pair<String, Integer>>();
	//The third column is dummy
	private Table<Object, TypeConverter<?, ?>, Object> needToSaveConverters = new Table<Object, TypeConverter<?, ?>, Object>();
	/*
	 * This is a field to determine the last method type called, whether it is saving 
	 * or loading method. If the method type does not matches the state, initialization 
	 * for the field will be done.
	 */
	private boolean load;
	
	public SQLObjectConverter(Connection connection, TypeConverter<?, ?> ... converters) {
		Validate.notNull(connection);
		this.connection = connection;
		this.converters = new ComparativeSet<TypeConverter<?, ?>>(converter -> {
			return this.converters.getIf(c -> c.getInputClass() == converter.getInputClass() && c.getOutputClass() == converter.getOutputClass()).isEmpty();
		}, Arrays.asList(converters));
	}
	
	public SQLObjectConverter(Connection connection) {
		this(connection, new TypeConverter[0]);
	}

	private String getTable(Class<?> implementer) {
		for (Map.Entry<String, Class<?>> entry : tables.entrySet()) 
			if (entry.getValue() == implementer) 
				return entry.getKey();
		return null;
	}
	
	public Connection getConnection() {
		return connection;
	}
	public ComparativeSet<TypeConverter<?, ?>> getConverters() {
		return converters;
	}

	@SuppressWarnings("unchecked")
	public <T> List<? extends T> loadAll(String table, Class<T> clazz) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		if (tables.get(table) != clazz) throw new ClassCastException();
		return (List<? extends T>) loadAll(table);
	}
	
	public void initializeFieldsForLoading() throws SQLTimeoutException, DatabaseAccessException, DataLoadException {
		initializeFields();
		loadedObjects.clear();
	}
	public void initializeFieldsForSaving() throws SQLTimeoutException, DatabaseAccessException, DataLoadException {
		initializeFields();
		savedObjects.clear();
	}
	public void initializeFields() throws SQLTimeoutException, DatabaseAccessException, DataLoadException {
		try (Statement statement = connection.createStatement()) {
			ResultSet set = statement.executeQuery("SELECT * FROM tables_info");
			while (set.next()) 
				tables.put(set.getString("table_name"), Class.forName(set.getString("implementing_class")));
			load = false;
		} catch (SQLTimeoutException e) {
			throw e;
		} catch (SQLException e) {
			throw new DatabaseAccessException();
		} catch (ClassNotFoundException e) {
			throw new DataLoadException(e);
		}
	}
	@SuppressWarnings("unchecked")
	public <T> List<T> loadAll(Class<T> clazz) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		List<T> list = new ArrayList<T>();
		for (Map.Entry<String, Class<?>> entry : tables.entrySet()) 
			if (entry.getValue().isAssignableFrom(clazz)) 
				list.addAll((Collection<? extends T>) loadAll(entry.getKey()));
		return list;
	}
	/**
	 * Loads all objects from a single table.
	 * @param table The desired table name.
	 * @return The objects loaded.
	 * @throws SQLTimeoutException when the driver has determined that the timeout value that was specified by the setQueryTimeout method has been exceeded and has at least attempted to cancel the currently running Statement
	 * @throws DatabaseAccessException if a database access error occurs
	 * @throws DataLoadException when an error occurred while deserializing a row, which other exceptions cannot represent.
	 * @throws Throwable when an exception is thrown while constructing an object from itself.
	 */
	public List<Object> loadAll(String table) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		if (!(load)) 
			initializeFieldsForLoading();
		List<Object> list = new ArrayList<Object>();
		try (Statement statement = connection.createStatement()) {
			ResultSet set = statement.executeQuery("SELECT * FROM" + table);
			for (int i = 1; i <= set.getFetchSize(); i++) 
				list.add(loadObject(table, i, true));
		} 
		return list;
	}

	/**
	 * Loads an object at a specified row from a single table.
	 * @param table The desired table name.
	 * @param row The desired row count.
	 * @return The object loaded.
	 * @throws SQLTimeoutException when the driver has determined that the timeout value that was specified by the setQueryTimeout method has been exceeded and has at least attempted to cancel the currently running Statement
	 * @throws DatabaseAccessException if a database access error occurs
	 * @throws DataLoadException when an error occurred while deserializing a row, which other exceptions cannot represent.
	 * @throws Throwable when an exception is thrown while constructing an object from itself.
	 */
	public Object loadObject(String table, int row, boolean remove) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		if (!(load)) 
			initializeFieldsForLoading();
		for (Map.Entry<Object, Pair<String, Integer>> entry : loadedObjects.entrySet()) 
			if (entry.getValue().getKey() == table && entry.getValue().getValue() == row) 
				return entry.getKey();
		ArrayMap<String, Object> map = new ArrayMap<String, Object>();
		try (Statement statement = connection.createStatement()) {
			ResultSet set = statement.executeQuery("SELECT * FROM " + table);
			set.absolute(row);
			ResultSetMetaData md = set.getMetaData();
			for (int i = 0; i <= md.getColumnCount(); i++) {
				Class<?> type = Class.forName(md.getColumnClassName(i));
				Object object = deserialize(set.getObject(i, type));
				map.put(md.getColumnName(i), object);
			}
			ResultSet meta = statement.executeQuery("SELECT * FROM tables_info WHERE table_name = " + table);
			meta.absolute(1);
			Class<?> clazz = Class.forName(meta.getString("implementing_class"));
			boolean omit = meta.getBoolean("omit") && md.getColumnCount() == 1;
			if (!(omit)) {
				if (clazz.isAssignableFrom(DatabaseSerializable.class)) {
					DatabaseSerializable object = (DatabaseSerializable) clazz.getConstructor(ArrayMap.class).newInstance(map);
					loadedObjects.put(object, new Pair<String, Integer>(table, row));
					return object;
				}
			}
			loadedObjects.put(map.get(1).getValue(), new Pair<String, Integer>(table, row));
			if (remove) set.deleteRow();
			return map.get(1).getValue();
		} catch (SQLTimeoutException e) {
			throw e;
		} catch (SQLException e) {
			throw new DatabaseAccessException();
		} catch (ClassNotFoundException e) {
			throw new DataLoadException(e);
		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException e) {
			//These exceptions are never thrown.
			return null;
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new InvalidTypeException(e);
		}
	}

	public Object deserialize(Object object) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		if (object instanceof String) {
			String string = (String) object;
			if ((string + ", ").matches("list from table .+ (range [0-9]+ to [0-9]+, )+")) 
				return loadList(string);
			if ((string.split("; ")[0] + ", ").matches("map keys from table .+ (range [0-9]+ to [0-9]+, )+") &&
					(string.split("; ") + ", ").matches("values from table .+ (range [0-9]+ to [0-9]+, )+")) 
				return loadArrayMap(string);
			if (string.matches("object from table .+ at [0-9]+")) 
				return loadObjectReference(string);
		}
		try (Statement statement = connection.createStatement(); PreparedStatement stmt = connection.prepareStatement("SELECT ? from tables_info")) {
			ResultSet set = statement.executeQuery("SELECT * FROM converters_cache WHERE target_table = " + getTable(object.getClass()));
			set.absolute(1);
			List<Class<?>> types = new ArrayList<Class<?>>();
			String fullconvertersindex = set.getString("target_converter_input_types");
			for (String i : fullconvertersindex.split(", ")) {
				stmt.setInt(1, Integer.parseInt(i));
				ResultSet info = stmt.executeQuery();
				info.absolute(1);
				types.add(Class.forName(info.getString("implementing_class")));
			}
			Object o = object;
			for (Class<?> type : types) {
				Set<TypeConverter<?, ?>> converters = this.converters.getIf(converter -> converter.getOutputClass() == object.getClass() && converter.getInputClass() == type);
				TypeConverter<?, ?> converter = converters.toArray(new TypeConverter[converters.size()])[0];
				o = converter.getClass().getMethod("deserialize", object.getClass()).invoke(converter, object);
			}
			return o;
		}
	}
	public List<?> loadList(String statement) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		return loadList(statement, tables.get(statement.split("(list from table | range | to |, )")[0]));
	}
	
	private <E> SQLList<E> loadList(String statement, Class<E> type) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		String[] args = statement.split("(list from table | range | to |, )");
		SQLList<E> list = SQLList.create(type);
		for (int i = 1; i < args.length; i += 2) 
			for (int ui = Integer.parseInt(args[i]); i <= Integer.parseInt(args[i + 1]); i++) 
				list.add(list.getDeclaringClass().cast(loadObject(args[0], ui, true)));
		return list;
	}
	
	public Object loadObjectReference(String statement) throws SQLTimeoutException, DatabaseAccessException, NumberFormatException, DataLoadException, Throwable {
		String[] args = statement.split("(object from table | at ");
		return loadObject(args[0], Integer.parseInt(args[1]), true);
	}
	
	public ArrayMap<?, ?> loadArrayMap(String statement) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		ArrayMap<Object, Object> map = new ArrayMap<Object, Object>();
		String[] keyargs = statement.split("; ")[0].split("(map keys from table | range | to |, )");
		String[] valueargs = statement.split("; ")[1].split("(values from table | range | to |, )");
		for (int i = 1; i <= keyargs.length; i += 2) {
			List<Object> keys = new ArrayList<Object>();
			for (int ui = Integer.parseInt(keyargs[i]); i <= Integer.parseInt(keyargs[i + 1]); i++) 
				keys.add(loadObject(keyargs[0], ui, true));
			List<Object> values = new ArrayList<Object>();
			for (int ui = Integer.parseInt(valueargs[i]); i <= Integer.parseInt(valueargs[i + 1]); i++) 
				keys.add(loadObject(valueargs[0], ui, true));
			for (int io = 0; i < keys.size(); i++) 
				map.put(keys.get(io), values.get(io));
		}
		return map;
	}
	
	/**
	 * Saves a list of objects.
	 * @param object The desired list of objects.
	 * @return The locations of the objects stored. The key is the table name, and the value is the row.
	 * @throws SQLTimeoutException when the driver has determined that the timeout value that was specified by the setQueryTimeout method has been exceeded and has at least attempted to cancel the currently running Statement
	 * @throws DatabaseAccessException if a database access error occurs
	 * @throws DataLoadException when an error occurred while serializing rows, which other exceptions cannot represent.
	 * @throws Throwable when an exception is thrown while calling necessary methods for serialization.
	 */
	public ArrayMap<String, Integer> saveObjects(List<? extends Object> list) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		ArrayMap<String, Integer> map = new ArrayMap<String, Integer>();
		for (Object object : list) {
			Pair<String, Integer> pair = saveObject(object);
			map.put(pair.getKey(), pair.getValue());
		};
		return map;
	}
	
	/**
	 * Saves an object.
	 * @param object The desired object.
	 * @return The location of the object stored. The key is the table name, and the value is the row.
	 * @throws SQLTimeoutException when the driver has determined that the timeout value that was specified by the setQueryTimeout method has been exceeded and has at least attempted to cancel the currently running Statement
	 * @throws DatabaseAccessException if a database access error occurs
	 * @throws DataLoadException when an error occurred while serializing a row, which other exceptions cannot represent.
	 * @throws Throwable when an exception is thrown while calling necessary methods for serialization.
	 */
	public Pair<String, Integer> saveObject(Object object) throws Throwable, SQLTimeoutException, DatabaseAccessException, DataLoadException {
		if (load) initializeFieldsForSaving();
		if (savedObjects.containsKey(object)) return savedObjects.get(object);
		int rowcount = 0;
		ArrayMap<String, Object> map = serialize(object);
		processSerializedArrayMap(map);
		ArrayMap<String, Class<?>> columns = map.apply(null, o -> { return object.getClass(); });
		SerializableAs a = object.getClass().getDeclaredAnnotation(SerializableAs.class);
		String name = (a == null) ? object.getClass().getSimpleName().toLowerCase() : a.name();
		try (Statement statement = connection.createStatement()) {
			ResultSet set = statement.executeQuery("SELECT * FROM tables_info WHERE implementing_class = " + object.getClass().getName());
			set.absolute(1);
			String sqlname = set.getString("table_name");
			ArrayMap<String, Class<?>> sqlcolumns = new ArrayMap<String, Class<?>>();
			for (int i = 1; i <= set.getMetaData().getColumnCount(); i++) 
				sqlcolumns.put(set.getMetaData().getColumnName(i), Class.forName(set.getMetaData().getColumnClassName(i)));
			if (!(columns.equals(sqlcolumns) && name.equals(sqlname))) {
				//Create table
				statement.executeUpdate("DROP TABLE " + sqlname);
				statement.executeUpdate("DELETE FROM tables_info WHERE table_name = " + sqlname); 
				String createsql = "CREATE TABLE " + name + " (";
				for (int i = 0; i < columns.size(); i++) {
					Pair<String, Class<?>> pair = columns.get(i);
					createsql += pair.getKey() + " ";
					String typesimplename = pair.getValue().getSimpleName();
					if (typesimplename.equalsIgnoreCase("string")) {
						String string = (String) object;
						if (string.length() > 16777215) typesimplename = "longtext";
						else if (string.length() > 65535) typesimplename = "mediumtext";
						else if (string.length() > 255) typesimplename = "text";
						//255 is used as the length and not the length of the string to minimize the times required to regenerate table
						else typesimplename = "varchar(255)";
					}
					if (typesimplename.equalsIgnoreCase("byte")) 
						typesimplename = "tinyint";
					if (typesimplename.equalsIgnoreCase("short")) 
						typesimplename = "smallint";
					if (typesimplename.equalsIgnoreCase("long")) 
						typesimplename = "bigint";
					createsql += typesimplename.toUpperCase() + ", ";
				}
				statement.executeUpdate(createsql.substring(0, createsql.length() - 3) + ")");
				statement.executeUpdate("INSERT INTO tables_info VALUES (" + name + ", " + object.getClass().getName() + ")");
			} else {
				//Alter table with column types changed
				ResultSet newset = statement.executeQuery("SELECT * FROM " + name);
				ResultSetMetaData md = newset.getMetaData();
				rowcount = md.getColumnCount();
				boolean resize = false;
				String altertable = "ALTER TABLE " + name + " MODIFY ";
				for (int i = 0; i <= rowcount; i++) {
					String original = md.getColumnTypeName(i);
					String typename = original;
					if (typename.equalsIgnoreCase("tinyint")) 
						typename = "byte";
					if (typename.equalsIgnoreCase("smallint")) 
						typename = "short";
					if (typename.equalsIgnoreCase("bigint")) 
						typename = "long";
					typename = Minecord.capitalizeFirstLetter(typename);
					Class<?> type = Class.forName(md.getColumnClassName(i));
					if (type.isAssignableFrom(Number.class)) {
						Class<?> realtype = Class.forName("java.lang." + typename);
						Field field = realtype.getField("MAX_VALUE");
						//Change from unsigned max value to signed max value
						double max = ((double) field.getClass().getMethod("get" + typename, Object.class).invoke(field, new Object[] { null }) + 1) / 2 - 1;
						if ((double) map.get(i).getValue() > max) {
							altertable += md.getColumnName(i) + " ";
							if (typename.equalsIgnoreCase("byte")) 
								altertable += "SMALLINT";
							if (typename.equalsIgnoreCase("short")) 
								altertable += "INT";
							if (typename.equalsIgnoreCase("int")) 
								altertable += "BIGINT";
							if (typename.equalsIgnoreCase("float")) 
								altertable += "DOUBLE";
							altertable += ", ";
							resize = true;
						}
					} else if (type.isAssignableFrom(String.class)) {
						int length = ((String) map.get(i).getValue()).length();
						String newtype = null;
						if (length > 255 && typename.equalsIgnoreCase("varchar(255)")) 
							newtype = "text";
						if (length > 65535 && (typename.equalsIgnoreCase("text") || typename.equalsIgnoreCase("varchar(255)"))) 
							newtype = "mediumtext";
						if (length > 16777215 && !(typename.equalsIgnoreCase("longtext"))) 
							newtype = "longtext";
						if (newtype != null) {
							altertable += md.getColumnName(i) + " " + newtype.toUpperCase() + ", ";
							resize = true;
						}
					}
				}
				if (resize) 
					statement.executeUpdate(altertable.substring(altertable.length() - 3));
			}
			String savesql = "INSERT INTO " + name + " VALUES (";
			for (Object o : map.valueList()) 
				savesql += o.toString() + ", ";
			statement.executeUpdate(savesql.substring(0, savesql.length() - 3) + ")");
			Pair<String, Integer> pair = new Pair<String, Integer>(name, set.getMetaData().getColumnCount());
			savedObjects.put(object, pair);
			if (needToSaveConverters.aSet().keySet().contains(object)) 
				saveConverters(object);
			return pair;
		} catch (SQLTimeoutException e) {
			throw e;
		} catch (SQLException e) {
			throw new DatabaseAccessException();
		} catch (ClassNotFoundException e) {
			throw new DataLoadException(e);
		} catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
			//These exceptions are never thrown.
			return null;
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

	public void saveConverters(Object object) throws SQLTimeoutException, DatabaseAccessException, DataLoadException {
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS converters_cache (target_table INT UNSIGNED NOT NULL, target_converter_input_types TEXT(65535) NOT NULL)");
			String sql = "INSERT INTO converters_cache VALUES (" + getTable(object.getClass()) + ", ";
			ResultSet set = statement.executeQuery("SELECT * FROM tables_info");
			for (TypeConverter<?, ?> converter : needToSaveConverters.a(object).keySet()) {
				while (set.next()) 
					if (Class.forName(set.getString("implementing_class")) == converter.getInputClass()) 
						sql += Integer.toString(set.getRow()) + ", ";
				set.absolute(0);
			}
			statement.executeUpdate(sql.substring(0, sql.length() - 3) + ")");
		} catch (SQLTimeoutException e) {
			throw e;
		} catch (SQLException e) {
			throw new DatabaseAccessException();
		} catch (ClassNotFoundException e) {
			throw new DataLoadException(e);
		}
	}
	
	public ArrayMap<String, Object> serialize(Object object) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		if (object instanceof DatabaseSerializable) 
			return serializeAsDatabaseSerializable((DatabaseSerializable) object);
		if (object.getClass().isArray()) 
			return serializeAsArray((Object[]) object);
		if (object instanceof SQLList) 
			return serializeAsSQLList((SQLList<?>) object);
		if (object instanceof Map) 
			return serializeAsMap((Map<?, ?>) object);
		if (object instanceof ArrayMap) 
			return serializeAsArrayMap((ArrayMap<?, ?>) object);
		if (object instanceof String) 
			return serializeAsString((String) object);
		return serializeWithExternalConverter(object);
	}

	public ArrayMap<String, Object> serializeWithExternalConverter(Object object) {
		ArrayMap<String, Object> map = new ArrayMap<String, Object>();
		Set<TypeConverter<?, ?>> converters = this.converters.getIf(converter -> object.getClass() == converter.getInputClass());
		TypeConverter<?, ?> converter = converters.toArray(new TypeConverter[converters.size()])[0];
		try {
			map.put(object.getClass().getSimpleName(), converter.getClass().getMethod("serialize", object.getClass()).invoke(converter, object));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			//These exceptions are not thrown.
		}
		needToSaveConverters.put(object, converter, null);
		return map;
	}
	public ArrayMap<String, Object> serializeAsString(String string) {
		ArrayMap<String, Object> map = new ArrayMap<String, Object>();
		map.put("value", string);
		return map;
	}
	
	public ArrayMap<String, Object> serializeAsDatabaseSerializable(DatabaseSerializable object) {
		return object.serialize();
	}
	
	public ArrayMap<String, Object> serializeAsArray(Object[] array) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		return serializeAsSQLList(SQLList.fromArray(array));
	}
	
	public ArrayMap<String, Object> serializeAsSQLList(SQLList<?> list) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		ArrayMap<String, Object> map = new ArrayMap<String, Object>();
		List<Integer> counts = new ArrayList<Integer>();
		for (Object o : list) {
			int count = saveObject(o).getValue();
			if (!(counts.size() > 0 && counts.get(counts.size() - 1) + 1 >= count)) 
				counts.add(count);
		}
		String range = "";
		boolean to = false;
		for (int count : counts) 
			if (to) range += " to " + Integer.toString(count) + ", ";
			else range += "range " + Integer.toString(count);
		map.put("list", "list from table " + getTable(list.getDeclaringClass()) + " " + range);
		return map;
	}
	
	public ArrayMap<String, Object> serializeAsMap(Map<?, ?> map) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		return serializeAsArrayMap(ArrayMap.create(map));
	}
	
	public ArrayMap<String, Object> serializeAsArrayMap(ArrayMap<?, ?> map) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		ArrayMap<String, Object> columns = new ArrayMap<String, Object>();
		List<Integer> keycounts = new ArrayList<Integer>();
		String keytable = null;
		for (Object o : columns.keyList()) {
			Pair<String, Integer> pair = saveObject(o);
			keytable = pair.getKey();
			if (!(keycounts.size() > 0 && keycounts.get(keycounts.size() - 1) + 1 >= pair.getValue())) 
				keycounts.add(pair.getValue());
		}
		String keyrange = "";
		boolean to = false;
		for (int count : keycounts) 
			if (to) keyrange += " to " + Integer.toString(count) + ", ";
			else keyrange += "range " + Integer.toString(count);
		List<Integer> valuecounts = new ArrayList<Integer>();
		String valuetable = null;
		for (Object o : columns.valueList()) {
			Pair<String, Integer> pair = saveObject(o);
			keytable = pair.getKey();
			if (!(valuecounts.size() > 0 && valuecounts.get(valuecounts.size() - 1) + 1 >= pair.getValue())) 
				valuecounts.add(pair.getValue());
		}
		String valuerange = "";
		boolean valueto = false;
		for (int count : valuecounts) 
			if (valueto) valuerange += " to " + Integer.toString(count) + ", ";
			else valuerange += "range " + Integer.toString(count);
		columns.put("map", "map keys from table " + keytable + " " + keyrange + "; values from table " + valuetable + " " + valuerange);
		return columns;
	}
	
	public ArrayMap<String, Object> serializeUltimately(Object object) {
		ArrayMap<String, Object> map = new ArrayMap<String, Object>();
		map.put("untitled", object.toString());
		return map;
	}
	
	public void processSerializedArrayMap(ArrayMap<String, Object> map) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, Throwable {
		for (int i = 0; i < map.size(); i++) {
			Object value = map.get(i).getValue();
			if (!(value instanceof Number && value instanceof Boolean)) {
				Pair<String, Integer> pair = saveObject(value);
				map.setValue(i, "object from table " + pair.getKey() + " at " + Integer.toString(pair.getValue()));
			}
		}
	}

	/*public List<?> loadAllold(String table) throws DatabaseAccessException, SQLTimeoutException, InvocationTargetException, DataLoadException {
		try (Statement statement = connection.createStatement()) {
			List<Object> objects = new ArrayList<Object>();
			ResultSet rs = statement.executeQuery("SELECT * FROM tables_info WHERE table_name = " + table);
			rs.absolute(1);
			Class<?> clazz = Class.forName(rs.getString("implementing_class"));
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
	public Map<String, ?> loadFieldsold(String table, int index) throws SQLTimeoutException, DatabaseAccessException, DataLoadException, InvocationTargetException {
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
				if (object instanceof String && ((String) object).matches("list_from_table_.+_ranged_from_[0-9]+_to_[0-9]+")) {
					String[] args = ((String) object).split("(list_from_table_|_ranged_from_|_to_)");
					object = loadAll(args[0]).subList(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
				}
				if (object instanceof String && ((String) object).matches("object_from_table_.+_at_[0-9]+")) {
					String[] args = ((String) object).split("(object_from_table_|_at_)");
					object = loadAll(args[0]).get(Integer.parseInt(args[1]));
				}
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
		} catch (ClassNotFoundException | NumberFormatException e) {
				throw new DataLoadException(e);
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
			throw new InvalidTypeException();
		} catch (InvocationTargetException e) {
			throw e;
		} catch (DataLoadException e) {
			throw e;
		} 
	}

	public String save(SQLList<? extends DatabaseSerializable> list) throws SQLTimeoutException, DatabaseAccessException {
		Class<? extends DatabaseSerializable> clazz = list.getDeclaringClass();
		if (!(validateClass(clazz))) throw new InvalidTypeException();
		SerializableAs annotation = clazz.getDeclaredAnnotation(SerializableAs.class);
		String tablename = annotation.name();
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
	public String initializeTable(DatabaseSerializable test) throws SQLException, ClassNotFoundException {
		Class<? extends DatabaseSerializable> clazz = test.getClass();
		if (!(validateClass(clazz))) throw new InvalidTypeException();
		String currenttable = clazz.getDeclaredAnnotation(SerializableAs.class).name();
		try (Statement statement = connection.createStatement()) {
			ResultSet metadata = statement.executeQuery("SELECT * FROM tables_info WHERE implementing_class = \'" + clazz.getName() + "\'");
			metadata.absolute(1);
			String table = metadata.getString("table_name");
			ResultSet set = statement.executeQuery("SELECT * FROM " + table);
			ResultSetMetaData md = set.getMetaData();
			ArrayMap<String, Class<?>> columns = new ArrayMap<String, Class<?>>();
			for (int i = 1; i <= md.getColumnCount(); i++) 
				columns.put(md.getColumnName(i), Class.forName(md.getColumnClassName(i)));
			ArrayMap<String, Class<?>> currentcolumns = test.serialize().apply(key -> { return key; }, value -> { return value.getClass(); });
			if (!(columns.equals(currentcolumns) && currenttable.equals(table))) {
				for (Pair<String, Class<?>> pair : currentcolumns.entryList()) {
					if (pair.getValue().isAssignableFrom(SQLList.class)) {
						((SQLList<? extends DatabaseSerializable>) pair)
					}
				}
			}
		}
	}
	public boolean validateClass(Class<?> clazz) {
		try {
			clazz.getConstructor(Map.class);
			clazz.getDeclaredMethod("serialize");
			return clazz.getDeclaredAnnotationsByType(SerializableAs.class)[0].name().isEmpty();
		} catch (NoSuchMethodException | SecurityException | ArrayIndexOutOfBoundsException | NullPointerException e) {
			return false;
		}
	}*/
	
}
