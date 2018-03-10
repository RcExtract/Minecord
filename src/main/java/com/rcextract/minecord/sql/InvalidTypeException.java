package com.rcextract.minecord.sql;

/**
 * Thrown when an invalid type is used in serializations or deserializations. An 
 * invalid type means when it does not have all components required for serializations 
 * and deserializations. View {@link SQLCustomTypeUtils} for more information.
 * <p>
 * This exception is ridiculously inheriting {@link RuntimeException} because this 
 * exception is always thrown for invalid types, which is related to runtime, and not 
 * causes outside the Java Runtime Environment.
 */
public class InvalidTypeException extends RuntimeException {

	private static final long serialVersionUID = 1887604144699967565L;

	public InvalidTypeException() {
		super();
	}
	
	public InvalidTypeException(Throwable cause) {
		super(cause);
	}

}
