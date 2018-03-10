package com.rcextract.minecord.utils;

/**
 * The combination of {@see java.util.function.Consumer} and {@see java.util.function.Supplier}.
 * This is a functional interface whose functional method is {@link #convert(Object)}.
 * @param <I> The input argument type.
 * @param <O> The output argument type.
 */
@FunctionalInterface
public interface Convertor<I, O> {

	/**
	 * Converts the input argument.
	 * @param in The input argument.
	 * @return The converted argument.
	 */
	public O convert(I in);
}
