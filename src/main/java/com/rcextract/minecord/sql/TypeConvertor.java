package com.rcextract.minecord.sql;

import com.rcextract.minecord.utils.Convertor;

public class TypeConvertor<A, R> {

	private Convertor<A, R> serializer;
	private Convertor<R, A> deserializer;
	private final Class<A> a;
	private final Class<R> r;
	public TypeConvertor(Convertor<A, R> serializer, Convertor<R, A> deserializer, Class<A> a, Class<R> r) {
		this.serializer = serializer;
		this.deserializer = deserializer;
		this.a = a;
		this.r = r;
	}
	public Convertor<A, R> getSerializer() {
		return serializer;
	}
	public void setSerializer(Convertor<A, R> serializer) {
		this.serializer = serializer;
	}
	public Convertor<R, A> getDeserializer() {
		return deserializer;
	}
	public void setDeserializer(Convertor<R, A> deserializer) {
		this.deserializer = deserializer;
	}
	public Class<A> getA() {
		return a;
	}
	public Class<R> getR() {
		return r;
	}
}
