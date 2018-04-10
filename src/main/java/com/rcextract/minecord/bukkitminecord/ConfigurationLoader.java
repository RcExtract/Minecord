package com.rcextract.minecord.bukkitminecord;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;

import com.rcextract.minecord.ConfigurationManager;

public class ConfigurationLoader {

	private BukkitMinecord minecord;
	
	public ConfigurationLoader(BukkitMinecord minecord) {
		this.minecord = minecord;
	}
	
	public SaveOptions load() {
		Logger logger = minecord.getLogger();
		ConfigurationManager cm = minecord.getConfigurationManager();
		logger.log(Level.INFO, "Loading configuration file...");
		try {
			cm.generateDataFolder();
		} catch (SecurityException e) {
			logger.log(Level.SEVERE, "A permission error occurred while attempting to generate the data folder.", e);
			return loadBackupConfiguration(false);
		}
		try {
			cm.generateConfigurationFile();
		} catch (IOException | SecurityException e) {
			logger.log(Level.SEVERE, "A" + (e instanceof IOException ? "n I/O" : " permission") + " error occurred while attempting to generate the configuration file.", e);
			return loadBackupConfiguration(false);
		}
		try {
			cm.loadConfiguration();
		} catch (FileNotFoundException e) {
			//This exception is never thrown unless configuration file generation failed above. Therefore, no logging is required.
			return loadBackupConfiguration(false);
		} catch (IOException | InvalidConfigurationException e) {
			logger.log(Level.SEVERE, "A" + (e instanceof IOException ? "n I/O" : " parsing") + " error occurred while attempting to load the configuration file.");
			return loadBackupConfiguration(true);
		}
		Configuration config = cm.getConfiguration();
		minecord.setFormat(config.getString("format"));
		minecord.setConfigurationAutoSaveInterval(config.getLong("auto-save-configuration"));
		minecord.setDataAutoSaveInterval(config.getLong("auto-save-data"));
		return SaveOptions.SAVE_SOURCE_ONE;
	}
	
	public SaveOptions loadBackupConfiguration(boolean fileExists) {
		Logger logger = minecord.getLogger();
		PreferencesManager pm = minecord.getPreferencesManager();
		logger.log(Level.SEVERE, "Loading backup configuration...");
		try {
			if (pm.isConfigured()) {
				minecord.setFormat(pm.getBackupFormat());
				minecord.setConfigurationAutoSaveInterval(pm.getBackupConfigurationDuration());
				minecord.setDataAutoSaveInterval(pm.getBackupDataDuration());
				logger.log(Level.INFO, "Backup configuration is successfully loaded.");
				return fileExists ? SaveOptions.SAVE_SOURCE_TWO : SaveOptions.SAVE_ALL;
			}
		} catch (BackingStoreException e) {}
		logger.log(Level.SEVERE, "An error occurred while attempting to load the backup configuration.");
		minecord.setFormat("%minecord_nickname% > %minecord_message%");
		minecord.setConfigurationAutoSaveInterval(300);
		minecord.setDataAutoSaveInterval(300);
		logger.log(Level.WARNING, "Default format will be used temporaily. It will not replace the value in configuration file or backup configuration, if exists.");
		return fileExists ? SaveOptions.DO_NOT_SAVE : SaveOptions.SAVE_SOURCE_ONE;
	}
}
