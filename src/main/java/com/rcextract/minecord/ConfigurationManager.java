package com.rcextract.minecord;

import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;

public interface ConfigurationManager {

	public boolean generateDataFolder();
	public boolean generateConfigurationFile() throws IOException;
	public void loadConfiguration();
	public void saveConfiguration() throws IOException;
	public default FileConfiguration getConfiguration() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("The configuration manager does not support obtaining the FileConfiguration.");
	}
}
