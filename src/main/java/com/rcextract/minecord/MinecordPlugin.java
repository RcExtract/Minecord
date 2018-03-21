package com.rcextract.minecord;

import java.util.Set;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.rcextract.minecord.bukkitminecord.PreferencesManager;
import com.rcextract.minecord.getters.SendableGetter;
import com.rcextract.minecord.getters.ServerGetter;

import net.milkbowl.vault.permission.Permission;

public interface MinecordPlugin extends Plugin, Listener, ServerGetter, SendableGetter, ConfigurationManager {

	public Server getMain();
	public void setMain(Server server);
	public ConfigurationManager getConfigurationManager();
	public void setConfigurationManager(ConfigurationManager configurationManager);
	public DataManipulator getDataManipulator();
	public void setDataManipulator(DataManipulator dataManipulator);
	public PreferencesManager getPreferencesManager();
	public Set<CommandExpansion> getCommandExpansions();
	public Permission getPermissionManager();
	public Boolean isUpdateAvailable();
	public String databaseVersion();
	public String oldDatabaseVersion();
	public String getFormat();
}
