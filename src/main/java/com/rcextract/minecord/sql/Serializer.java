package com.rcextract.minecord.sql;

@Deprecated
@FunctionalInterface
public interface Serializer<A, R> {

	public R serialize(A a);

	/*@FunctionalInterface
	public static interface ToBoolean<A> extends Serializer<A, Boolean> {
		public Boolean serialize(A a);
	}
	
	@FunctionalInterface
	public static interface ToCharacter<A> extends Serializer<A, Character> {
		public Character serialize(A a);
	}
	
	@FunctionalInterface
	public static interface ToDouble<A> extends Serializer<A, Double> {
		public Double serialize(A a);
	}
	
	@FunctionalInterface
	public static interface ToFloat<A> extends Serializer<A, Float> {
		public Float serialize(A a);
	}
	
	@FunctionalInterface
	public static interface ToInteger<A> extends Serializer<A, Integer> {
		public Integer serialize(A a);
	}
	
	@FunctionalInterface
	public static interface ToLong<A> extends Serializer<A, Long> {
		public Long serialize(A a);
	}
	
	@FunctionalInterface
	public static interface ToShort<A> extends Serializer<A, Short> {
		public Short serialize(A a);
	}
	
	@FunctionalInterface
	public static interface ToString<A> extends Serializer<A, String> {
		public String serialize(A a);
	}*/
}
