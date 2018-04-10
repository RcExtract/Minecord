package com.rcextract.minecord.bukkitminecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.ByteStreams;
import com.rcextract.minecord.ConfigurationManager;
import com.rcextract.minecord.MinecordPlugin;

public class SimpleConfigurationManager implements ConfigurationManager {

	private final File file;
	private final MinecordPlugin minecord;
	private FileConfiguration configuration;
	
	public SimpleConfigurationManager(MinecordPlugin minecord) {
		this.minecord = minecord;
		this.file = new File(minecord.getDataFolder(), "minecord.yml");
		this.configuration = new YamlConfiguration();
	}

	@Override
	public boolean generateDataFolder() {
		File dataFolder = minecord.getDataFolder();
		if (!(dataFolder.exists())) return dataFolder.mkdir();
		return false;
	}

	@Override
	public boolean generateConfigurationFile() throws IOException {
		if (file.createNewFile()) 
			try (InputStream i = minecord.getResource("minecord.yml"); OutputStream o = new FileOutputStream(file)) {
				ByteStreams.copy(i, o);
				return true;
			} catch (FileNotFoundException e) {
				//This exception is never thrown.
				return false;
			}
		return false;
	}

	@Override
	public void loadConfiguration() throws FileNotFoundException, IOException, InvalidConfigurationException {
		configuration.load(file);
	}

	@Override
	public void saveConfiguration() throws IOException {
		configuration.save(file);
	}
	
	@Override
	public FileConfiguration getConfiguration() {
		return configuration;
	}

}
