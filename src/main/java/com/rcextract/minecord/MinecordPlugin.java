package com.rcextract.minecord;

import org.bukkit.plugin.Plugin;

import com.rcextract.minecord.core.ConfigurationManager;
import com.rcextract.minecord.getters.SendableGetter;
import com.rcextract.minecord.getters.ServerGetter;

import net.milkbowl.vault.permission.Permission;

public interface MinecordPlugin extends Plugin, ServerGetter, SendableGetter {

	public Server getMain();
	public ConfigurationManager getConfigurationManager();
	public DataManipulator getDataManipulator();
	public Permission getPermissionManager();
	public void loadConfiguration();
	public void saveConfiguration();
	public void loadData();
	public void saveData();
	public String databaseVersion();
	public String oldDatabaseVersion();
	public String getFormat();
}
