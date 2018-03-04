package com.rcextract.minecord;

import java.lang.reflect.Method;

public class SendableClass<T extends Sendable> {

	private final Class<T> clazz;
	private Method serializer;
	private Method deserializer;
	public SendableClass(Class<T> clazz, Method serializer, Method deserializer) {
		this.clazz = clazz;
		this.serializer = serializer;
		this.deserializer = deserializer;
	}
	public Class<T> getDeclaringClass() {
		return clazz;
	}
	public Method getSerializer() {
		return serializer;
	}
	public void setSerializer(Method serializer) {
		this.serializer = serializer;
	}
	public Method getDeserializer() {
		return deserializer;
	}
	public void setDeserializer(Method deserializer) {
		this.deserializer = deserializer;
	}
}
