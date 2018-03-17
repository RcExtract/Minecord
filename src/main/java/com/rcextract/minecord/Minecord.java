package com.rcextract.minecord;

import java.util.Set;

import com.rcextract.minecord.core.ConfigurationManager;
import com.rcextract.minecord.utils.ComparativeSet;

import net.milkbowl.vault.permission.Permission;

public class Minecord {

	private static MinecordPlugin minecord;
	
	public static MinecordPlugin getPlugin() {
		return minecord;
	}
	
	protected static void setPlugin(MinecordPlugin minecord) {
		Minecord.minecord = minecord;
	}
	
	public static ComparativeSet<Server> getServers() {
		return minecord.getServers();
	}

	public static Server getServer(int id) {
		return minecord.getServer(id);
	}

	public static Server getServer(String name) {
		return minecord.getServer(name);
	}

	public static Set<Server> getServers(Sendable sendable) {
		return minecord.getServers(sendable);
	}

	public static Server getServer(Channel channel) {
		return minecord.getServer(channel);
	}

	public static ComparativeSet<Sendable> getSendables() {
		return minecord.getSendables();
	}

	public static Sendable getSendable(int id) {
		return minecord.getSendable(id);
	}

	public static Set<Sendable> getSendables(String name) {
		return minecord.getSendables(name);
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

	public static void loadConfiguration() {
		minecord.loadConfiguration();
	}

	public static void saveConfiguration() {
		minecord.saveConfiguration();
	}

	public static void loadData() {
		minecord.loadData();
	}

	public static void saveData() {
		minecord.saveData();
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

}
