package com.rcextract.minecord.sql;

public class DatabaseAccessException extends SQLConnectException {

	private static final long serialVersionUID = -3631495269111386528L;

	public DatabaseAccessException() {
		super();
	}
	
	public DatabaseAccessException(Throwable cause) {
		super(cause);
	}
}
