package com.rcextract.minecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.bukkit.plugin.Plugin;

import com.google.common.io.ByteStreams;

public class ConfigManager {

	private File file;
	private Plugin plugin;
	/**
	 * This constructor is reserved for initialization.
	 */
	protected ConfigManager(Plugin plugin) {
		this.plugin = plugin;
		File dir = plugin.getDataFolder();
		if (!(dir.exists())) dir.mkdir();
		file = new File(dir, "minecord.properties");
	}
	/**
	 * Initializes the files.
	 */
	private void init() {
		if (!(file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			InputStream i = plugin.getResource("minecord.properties");
			OutputStream o = null;
			try {
				o = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				ByteStreams.copy(i, o);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Loads the basic options of the Minecord system.
	 * <p>
	 * This must be called before loading data from the database.
	 * @throws FileNotFoundException 
	 * @throws IOException Thrown when an error occurred while attempting to load the options.
	 */
	public void load(Properties properties) throws FileNotFoundException, IOException {
		init();
		properties.load(new FileInputStream(file));
	}
	/**
	 * Saves the options and the records.
	 * @throws FileNotFoundException 
	 * @throws IOException Thrown when an error occurred while attempting to save the options.
	 */
	public void save(Properties properties) throws FileNotFoundException, IOException {
		init();
		properties.store(new FileOutputStream(file), null);
	}
}
