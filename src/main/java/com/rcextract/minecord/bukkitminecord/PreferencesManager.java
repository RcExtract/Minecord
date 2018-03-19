package com.rcextract.minecord.bukkitminecord;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PreferencesManager {
	
	private Preferences preferences;
	
	protected PreferencesManager() {
		this.preferences = Preferences.userRoot().node("com.rcextract.minecord.core");
	}

	public boolean isConfigured() throws BackingStoreException {
		return preferences.keys().length == 0;
	}
	
	protected String getHost() {
		return preferences.get("host", "localhost:3306");
	}
	
	protected String getUser() {
		return preferences.get("user", "root");
	}
	
	protected String getPassword() {
		return preferences.get("password", "admin");
	}
	
	protected void setHost(String host) {
		preferences.put("host", host);
	}
	
	protected void setUser(String user) {
		preferences.put("user", user);
	}
	
	protected void setPassword(String password) {
		preferences.put("password", password);
	}
	
	protected String getBackupFormat() {
		return preferences.get("format", null);
	}
	
	protected void setBackupFormat(String format) {
		preferences.put("format", format);
	}
	
	protected long getBackupDuration() {
		return preferences.getLong("duration", Long.MIN_VALUE);
	}
	
	protected void setBackupDuration(long duration) {
		preferences.putLong("duration", duration);
	}
	
}
