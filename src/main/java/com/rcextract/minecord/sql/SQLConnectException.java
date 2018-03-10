package com.rcextract.minecord.sql;

import java.sql.SQLException;

public class SQLConnectException extends SQLException {

	private static final long serialVersionUID = 6251026970225795331L;

	public SQLConnectException() {
		super();
	}
	
	public SQLConnectException(Throwable cause) {
		super(cause);
	}
}
