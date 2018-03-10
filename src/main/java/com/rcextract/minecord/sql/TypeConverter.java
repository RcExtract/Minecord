package com.rcextract.minecord.sql;

/**
 * A utility class designed for conversions between types an SQL server is supporting 
 * by default, and a Java type which is not implementing the 
 * {@link DatabaseSerializable} interface. This is like an implementation of the 
 * SQLObjectConverter API of a class but outside it.
 * <p>
 * To create a <code>TypeConverter</code>, you should first create a class inheriting 
 * it. It can be an anonymous class. Then, specify the types you want to convert 
 * between, and implement the method {@see #serialize(Object)} and 
 * {@see #deserialize(Object)}. Be careful that the type parameter I is the input type 
 * for {@see #serialize(Object)} and O is the input type for 
 * {@see #deserialize(Object)}. Below is an example of <code>TypeConverter</code> 
 * between {@link UUID} and {@link String}:
 * <code>
 * 	public class ExampleConverter extends TypeConverter<UUID, String> {
 * 
 * 		public ExampleConverter() {
 * 			super(UUID.class, String.class);
 * 		}
 * 
 * 		@Override
 * 		public String serialize(UUID uuid) {
 * 			return uuid.toString();
 * 		}
 * 
 * 		@Override
 * 		public UUID deserialize(String string) {
 * 			return UUID.fromString(string);
 * 		}
 * 
 * 	}
 * </code>
 * <p>
 * To make it work for an {@link SQLObjectConverter}, get the instance and do 
 * <code>
 * connector.getConverters().add(converter);
 * </code>
 * where connector is the {@link SQLObjectConverter} instance and the converter is the 
 * <code>TypeConverter</code> instance. If you want all {@link SQLObjectConverter} 
 * instances to have the <code>TypeConverter</code> at construction, add it to the 
 * <code>DEFAULT_CONVERTERS</code> {@link ComparativeSet} constant which is accessible 
 * through <code>getDefaultConvertors</code>.
 * <p>
 * If the {@link SQLObjectConverter} instance already contains a 
 * <code>TypeConverter</code> with the same input or output type as what you are 
 * providing, it will not be added. Even converters with only either the same input 
 * type or the output type is not allowed, because the {@link SQLObjectConnector} does 
 * not know which one to use.
 * <p>
 * {@link SQLObjectConverter} supports multiple times of serialization or 
 * deserialization on the same object. This means an {@link OfflinePlayer} can first be 
 * serialized to {@link UUID} with <code>TypeConverter<OfflinePlayer, UUID></code>, 
 * then can be again serialized from {@link UUID} to {@link String} with 
 * <code>TypeConverter<UUID, String></code>.
 * @param <I> The input type of {@see #serialize(Object)} and the output type of 
 * {@see #deserialize(Object)}.
 * @param <O> The input type of {@see #deserialize(Object)} and the output type of 
 * {@see #serialize(Object)}.
 */
public abstract class TypeConverter<I, O> {

	private final Class<I> input;
	private final Class<O> output;
	
	/**
	 * Constructs a new <code>TypeConverter</code>
	 * @param input The class of the type parameter I.
	 * @param output The class of the type parameter O.
	 */
	public TypeConverter(Class<I> input, Class<O> output) {
		this.input = input;
		this.output = output;
	}

	/**
	 * Gets the class of the type parameter I.
	 * @return The class of the type parameter I.
	 */
	public Class<I> getInputClass() {
		return input;
	}

	/**
	 * Gets the class of the type parameter O.
	 * @return The class of the type parameter O.
	 */
	public Class<O> getOutputClass() {
		return output;
	}

	/**
	 * Serializes the input.
	 * @param input The input.
	 * @return The output.
	 */
	public abstract O serialize(I input);
	
	/**
	 * Deserializes the input.
	 * @param output The input.
	 * @return The output.
	 */
	public abstract I deserialize(O output);
}
