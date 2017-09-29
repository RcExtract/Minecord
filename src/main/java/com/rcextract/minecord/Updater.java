package com.rcextract.minecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

public class Updater {

	private static final String API_KEY = "98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4";
	private static final String REQUEST_METHOD = "POST";
	private static final String LINK = "http://www.spigotmc.org/api/general.php";
	
	public static void checkForUpdate(JavaPlugin plugin) {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(LINK).openConnection();
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "An error occured while attempting to check for an update.", e);
			plugin.getLogger().log(Level.INFO, "Usually this error is caused by failure on connecting to spigot server.");
			return;
		}
		connection.setDoOutput(true);
		try {
			connection.setRequestMethod(REQUEST_METHOD);
			connection.getOutputStream().write(("key=" + API_KEY + "&resource=44055").getBytes("UTF-8"));
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "An error occured while attempting to check for an update.", e);
			plugin.getLogger().log(Level.INFO, "Usually this error is caused by failure on connecting to spigot server.");
			return;
		}
		String label = null;
		try {
			label = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
		} catch (IOException e) {
			//This exception is never thrown.
			e.printStackTrace();
		}
		String current = plugin.getDescription().getVersion();
		if (!(label.startsWith("Beta ") || current.startsWith("Beta "))) {
			if (Double.parseDouble(label) > Double.parseDouble(current)) {
				plugin.getLogger().log(Level.INFO, "A new version is available! Please update as soon as possible.");
				return;
			}
			plugin.getLogger().log(Level.INFO, "Your plugin is up to date.");
			return;
		}
		if (!(label.startsWith("Beta ")) && current.startsWith("Beta ")) {
			plugin.getLogger().log(Level.INFO, "A new version is available! Please update as soon as possible.");
			return;
		}
		if (Double.parseDouble(label.substring(5)) > Double.parseDouble(current.substring(5))) {
			plugin.getLogger().log(Level.INFO, "A new version is available! Please update as soon as possible.");
			return;
		}
		plugin.getLogger().log(Level.INFO, "Your plugin is up to date.");
	}
}
