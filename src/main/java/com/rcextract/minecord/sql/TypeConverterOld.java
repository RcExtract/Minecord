package com.rcextract.minecord.sql;

import java.util.function.Function;

@Deprecated
public class TypeConverterOld<A, R> {

	private Function<A, R> serializer;
	private Function<R, A> deserializer;
	private final Class<A> a;
	private final Class<R> r;
	public TypeConverterOld(Function<A, R> serializer, Function<R, A> deserializer, Class<A> a, Class<R> r) {
		this.serializer = serializer;
		this.deserializer = deserializer;
		this.a = a;
		this.r = r;
	}
	public Function<A, R> getSerializer() {
		return serializer;
	}
	public void setSerializer(Function<A, R> serializer) {
		this.serializer = serializer;
	}
	public Function<R, A> getDeserializer() {
		return deserializer;
	}
	public void setDeserializer(Function<R, A> deserializer) {
		this.deserializer = deserializer;
	}
	public Class<A> getA() {
		return a;
	}
	public Class<R> getR() {
		return r;
	}
}
