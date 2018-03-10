package com.rcextract.minecord.sql;

public abstract class SQLObjectConnectorException extends Exception {

	private static final long serialVersionUID = 8302097697658581915L;

	public SQLObjectConnectorException() {
		super();
	}
	
	public SQLObjectConnectorException(Throwable cause) {
		super(cause);
	}
}
