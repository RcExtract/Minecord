package com.rcextract.minecord;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.InvalidConfigurationException;

import com.rcextract.minecord.utils.EnhancedSet;

import net.milkbowl.vault.permission.Permission;

public class Minecord {

	private static MinecordPlugin minecord;
	
	public static MinecordPlugin getPlugin() {
		return minecord;
	}
	
	protected static void setPlugin(MinecordPlugin minecord) {
		Minecord.minecord = minecord;
	}
	
	public static EnhancedSet<Server> getServers() {
		return minecord.getServers();
	}
	
	public static Channel getChannel(UUID id) {
		return minecord.getChannel(id);
	}

	public static EnhancedSet<Sendable> getSendables() {
		return minecord.getSendables();
	}

	public static Server getMain() {
		return minecord.getMain();
	}

	public static ConfigurationManager getConfigurationManager() {
		return minecord.getConfigurationManager();
	}

	public static DataManipulator getDataManipulator() {
		return minecord.getDataManipulator();
	}

	public static Permission getPermissionManager() {
		return minecord.getPermissionManager();
	}

	public boolean initializeConfigurationManager() throws IOException {
		return minecord.generateDataFolder() && minecord.generateConfigurationFile();
	}
	
	public static void loadConfiguration() throws FileNotFoundException, IOException, InvalidConfigurationException {
		minecord.loadConfiguration();
	}

	public static void saveConfiguration() throws IOException {
		minecord.saveConfiguration();
	}

	public static String databaseVersion() {
		return minecord.databaseVersion();
	}

	public static String oldDatabaseVersion() {
		return minecord.oldDatabaseVersion();
	}
	
	public static String getFormat() {
		return minecord.getFormat();
	}
	
	public static String capitalizeFirstLetter(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}
	
	public static <T> T[] reverse(T[] array) {
		for (int i = 0; i < array.length; i++) {
			T t = array[0];
			array[0] = array[array.length - 1 - i];
			array[array.length - 1 - i] = t;
		}
		return array;
	}

}
