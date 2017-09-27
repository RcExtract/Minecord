package com.rcextract.minecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

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
		file = new File(dir, "options.yml");
	}
	/**
	 * Initializes the files.
	 */
	private void initFile() {
		if (!(file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			InputStream i = plugin.getResource("options.yml");
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
	 * @throws IOException Thrown when an error occurred while attempting to load the options.
	 */
	public List<String> load() throws IOException {
		initFile();
		List<String> data = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;
			while ((line = reader.readLine()) != null) 
				data.add(line);
		} finally {
			if (reader != null) reader.close();
		}
		return data;
	}
	/**
	 * Saves the options and the records.
	 * @throws IOException Thrown when an error occurred while attempting to save the options.
	 */
	public void save() throws IOException {
		initFile();
		List<String> data = new ArrayList<String>();
		data.add(Minecord.getHost());
		data.add(Minecord.getUsername());
		data.add(Minecord.getPassword());
		data.add(Minecord.getFormat());
		data.add(Integer.toString(Minecord.getMessageLoadCount()));
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(file));
			for (String line : data) 
				writer.write(line + "\n");
		} finally {
			if (writer != null) writer.close();
		}
	}
}
