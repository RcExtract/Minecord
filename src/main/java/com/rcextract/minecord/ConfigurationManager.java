package com.rcextract.minecord;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

public interface ConfigurationManager {

	public boolean generateDataFolder();
	public boolean generateConfigurationFile() throws IOException;
	public void loadConfiguration() throws FileNotFoundException, IOException, InvalidConfigurationException;
	public void saveConfiguration() throws IOException;
	public default FileConfiguration getConfiguration() {
		throw new UnsupportedOperationException("The configuration manager does not support obtaining the FileConfiguration.");
	}
}
