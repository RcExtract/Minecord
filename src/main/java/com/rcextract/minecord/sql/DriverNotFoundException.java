package com.rcextract.minecord.sql;

import java.sql.SQLException;

public class DriverNotFoundException extends SQLException {

	private static final long serialVersionUID = 7559113230833684270L;

	public DriverNotFoundException() {
		super();
	}
	
	public DriverNotFoundException(Throwable cause) {
		super(cause);
	}
}
