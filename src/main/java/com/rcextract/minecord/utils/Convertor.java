package com.rcextract.minecord.utils;

@FunctionalInterface
public interface Convertor<I, O> {

	public O convert(I in);
}
