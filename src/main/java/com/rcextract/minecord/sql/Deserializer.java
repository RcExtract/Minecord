package com.rcextract.minecord.sql;

@Deprecated
@FunctionalInterface
public interface Deserializer<A, R> {

	public R deserialize(A a);

	/*@FunctionalInterface
	public static interface FromBoolean<R> extends Deserializer<Boolean, R> {
		public R deserialize(Boolean b);
	}
	
	@FunctionalInterface
	public static interface FromCharacter<R> extends Deserializer<Character, R> {
		public R deserialize(Character c);
	}
	
	@FunctionalInterface
	public static interface FromDouble<R> extends Deserializer<Double, R> {
		public R deserialize(Double d);
	}
	
	@FunctionalInterface
	public static interface FromFloat<R> extends Deserializer<Float, R> {
		public R deserialize(Float f);
	}
	
	@FunctionalInterface
	public static interface FromInteger<R> extends Deserializer<Integer, R> {
		public R deserialize(Integer i);
	}
	
	@FunctionalInterface
	public static interface FromLong<R> extends Deserializer<Long, R> {
		public R deserialize(Long l);
	}
	
	@FunctionalInterface
	public static interface FromShort<R> extends Deserializer<Short, R> {
		public R deserialize(Short s);
	}
	
	@FunctionalInterface
	public static interface FromString<R> extends Deserializer<String, R> {
		public R deserialize(String string);
	}*/
}
