package com.rcextract.minecord;

public class DataLoadException extends Exception {

	private static final long serialVersionUID = 1315522023117907164L;

	public DataLoadException(Throwable cause) {
		super(cause);
	}
	public DataLoadException(String message) {
		super(message);
	}
}
