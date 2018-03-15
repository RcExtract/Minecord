package com.rcextract.minecord.sql;

/**
 * Thrown when an error occurs while attempting to communicate to the SQL server due to 
 * invalid connection while the database is explicitly selected. If the database is not 
 * explicitly selected, please throw {@link SQLConnectException} instead.
 */
public class DatabaseAccessException extends SQLConnectException {

	private static final long serialVersionUID = -3631495269111386528L;

	public DatabaseAccessException() {
		super();
	}
	
	public DatabaseAccessException(Throwable cause) {
		super(cause);
	}
}
