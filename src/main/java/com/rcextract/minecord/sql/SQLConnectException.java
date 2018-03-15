package com.rcextract.minecord.sql;

import java.sql.SQLException;

/**
 * Thrown when an error occurs while attempting to communicate to the SQL server due to 
 * invalid connection. If the database is explicitly selected, please throw 
 * {@link DatabaseAccessException} instead.
 */
public class SQLConnectException extends SQLException {

	private static final long serialVersionUID = 6251026970225795331L;

	public SQLConnectException() {
		super();
	}
	
	public SQLConnectException(Throwable cause) {
		super(cause);
	}
}
