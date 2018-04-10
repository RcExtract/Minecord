package com.rcextract.minecord;

public interface DataManipulator {

	public default String getName() {
		return "minecord";
	}
	
	public double getLatestVersion();
	public double getSupportingOldVersion();
	public Double getVersion() throws Throwable;
	public default Boolean initialize() throws Throwable {
		throw new UnsupportedOperationException("This DataManipulator does not require any initializations.");
	}
	public void load() throws Throwable;
	public void save() throws Throwable;
	public boolean exists();
	public Boolean isDataOld();
}
