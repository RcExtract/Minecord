package com.rcextract.minecord;

import org.bukkit.plugin.Plugin;

import com.rcextract.minecord.bukkitminecord.PreferencesManager;
import com.rcextract.minecord.getters.SendableGetter;
import com.rcextract.minecord.getters.ServerGetter;

import net.milkbowl.vault.permission.Permission;

public interface MinecordPlugin extends Plugin, ServerGetter, SendableGetter, ConfigurationManager {

	public Server getMain();
	public void setMain(Server server);
	public ConfigurationManager getConfigurationManager();
	public void setConfigurationManager(ConfigurationManager configurationManager);
	public DataManipulator getDataManipulator();
	public void setDataManipulator(DataManipulator dataManipulator);
	public PreferencesManager getPreferencesManager();
	public CommandExpansion getCommandExpansion();
	public void setCommandExpansion(CommandExpansion commandExpansion);
	public Permission getPermissionManager();
	public Boolean isUpdateAvailable();
	public String databaseVersion();
	public String oldDatabaseVersion();
	public String getFormat();
}
