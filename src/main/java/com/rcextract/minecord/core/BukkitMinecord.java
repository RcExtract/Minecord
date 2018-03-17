//Not implemented!
package com.rcextract.minecord.core;

import java.io.IOException;
import java.sql.SQLTimeoutException;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.rcextract.minecord.Channel;
import com.rcextract.minecord.DataManipulator;
import com.rcextract.minecord.MinecordPlugin;
import com.rcextract.minecord.Sendable;
import com.rcextract.minecord.Server;
import com.rcextract.minecord.Updater;
import com.rcextract.minecord.sql.DriverNotFoundException;
import com.rcextract.minecord.sql.SQLConnectException;
import com.rcextract.minecord.utils.ComparativeSet;

import net.milkbowl.vault.permission.Permission;

/**
 * The core class of Minecord, also the core class of the plugin.
 * <p>
 * All Minecord system preferences are saved here.
 */
public class BukkitMinecord extends JavaPlugin implements MinecordPlugin {

	private ConfigurationManager cm;
	private DataManipulator dm;
	protected Properties properties;
	private boolean errorDisable;
	private Permission permission;
	protected String dbversion;
	protected String olddbversion;
	protected BukkitMinecord minecord;
	private ComparativeSet<Server> servers;
	private Server main;
	private ComparativeSet<Sendable> sendables;

	@Override
	public void onEnable() {
		minecord = this;
		dbversion = "7dot0";
		olddbversion = "6dot0";
		cm = new ConfigurationManager(this);
		servers = new ComparativeSet<Server>(server -> getServer(server.getIdentifier()) == null && getServer(server.getName()) == null);
		sendables = new ComparativeSet<Sendable>(sendable -> getSendable(sendable.getIdentifier()) == null);
		permission = Bukkit.getServicesManager().getRegistration(Permission.class).getProvider();
		loadProperties();
		loadData();
		checkUpdate();
		new IncompatibleDetector(this).runTask(this);
		new MinecordPlaceholders().register();
		
	}
	@Override
	public void onDisable() {
		if (errorDisable) {
			getLogger().log(Level.WARNING, "Minecord is disabling due to an error occurred while initializing.");
			getLogger().log(Level.WARNING, "This is purposed to keep your data protected from being overrided.");
		} else {
			try {
				getLogger().log(Level.INFO, "Saving properties to minecord.properties...");
				cm.save(properties);
				getLogger().log(Level.INFO, "Properties are successfully saved.");
			} catch (IOException e) {
				getLogger().log(Level.SEVERE, "An error occurred while attempting to save the properties.", e);
			}
			saveDataInternal();
		}
	}
	public ConfigurationManager getConfigurationManager() {
		return cm;
	}
	public DataManipulator getDataManipulator() {
		return dm;
	}
	/**
	 * Gets the format of a message.
	 * @return The format of a message.
	 */
	public String getFormat() {
		return properties.getProperty("format");
	}
	protected String getHost() {
		return properties.getProperty("host");
	}
	protected String getUsername() {
		return properties.getProperty("username");
	}
	protected String getPassword() {
		return properties.getProperty("password");
	}
	/**
	 * Gets the amount of messages to be loaded for a user.
	 * @return The amount of messages to be loaded for a user.
	 */
	@Deprecated
	public int getMessageLoadCount() {
		return Integer.parseInt(properties.getProperty("message-load-count"));
	}
	public void reloadConfiguration() {
		//String format = getFormat();
		try {
			cm.load(properties);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		//if (!(getFormat().equals(format))) 
			//for (User user : Minecord.getUserManager().getUsers()) 
				//updateMessage(user, true);
	}
	public String applyFormat(String name, String nickname, String uuid, String message, String date) {
		String format = new String(getFormat());
		format = format.replaceAll("playername", name);
		format = format.replaceAll("playernickname", nickname);
		format = format.replaceAll("playeruuid", uuid);
		format = format.replaceAll("message", message);
		format = format.replaceAll("time", date);
		format = format.replaceAll("&", "¡±");
		return format;
	}
	/*
	public void initialize() {
		ready();
		//if (dm == null) throw new IllegalStateException("this is not ready.");
		Bukkit.getPluginManager().registerEvents(new EventManager(), this);
		this.getCommand("this").setExecutor(new CommandHandler(this));
		if (panel.getServer("default") == null)
			try {
				panel.createServer("default", null, null, null, null, null);
			} catch (DuplicatedException e) {
				//This exception is never thrown.
			}
		//for (OfflinePlayer player : Bukkit.getOfflinePlayers()) 
			//if (!(this.getUserManager().isRegistered(player))) 
				//this.getUserManager().registerPlayer(null, null, null, player, new Listener(panel.getMain().getMain(), true, 0));
		panel.initialize();
	}*/
	public void loadProperties() {
		properties = new Properties();
		try {
			this.getLogger().log(Level.INFO, "Loading properties from this.properties...");
			cm.load(properties);
			this.getLogger().log(Level.INFO, "Properties are successfully loaded.");
		} catch (IOException e) {
			this.getLogger().log(Level.SEVERE, "An error occurred while attempting to load the properties.", e);
			errorDisable = true;
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
	}
	public void loadData() {
		ready();
		Bukkit.getScheduler().runTaskAsynchronously(minecord, new Runnable() {

			@Override
			public void run() {
				try {
					minecord.getLogger().log(Level.INFO, "Loading data from database...");
					dm = new DataManipulator(getHost(), getUsername(), getPassword());
					dm.initialize();
					dm.load();
					minecord.getLogger().log(Level.INFO, "Data are successfully loaded.");
				} catch (RuntimeException | SQLTimeoutException | SQLConnectException | DriverNotFoundException e) {
					minecord.getLogger().log(Level.SEVERE, "An error occured while attempting to load the data.", e);
					errorDisable = true;
					Bukkit.getPluginManager().disablePlugin(minecord);
					return;
				} finally {
					//initialize();
				}
			}
			
		});
	}
	public void saveData() {
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {

			@Override
			public void run() {
				saveDataInternal();
			}
			
		});
	}
	private void saveDataInternal() {
		try {
			this.getLogger().log(Level.INFO, "Saving data to database...");
			dm.save();
			this.getLogger().log(Level.INFO, "Data are successfully saved.");
		} catch (RuntimeException e) {
			this.getLogger().log(Level.SEVERE, "An error occurred while attempting to save the data.", e);
		}
	}
	public void checkUpdate() {
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {

			@Override
			public void run() {
				switch (new Updater(minecord).check()) {
				case CONNECTION_FAILURE: {
					minecord.getLogger().log(Level.SEVERE, "An error occured while attempting to check for an update.");
					minecord.getLogger().log(Level.INFO, "Usually this error is caused by failure on connecting to spigot server.");
				}
					break;
				case DATA_ACCESSED:
					break;
				case UPDATE_AVAILABLE: minecord.getLogger().log(Level.INFO, "An update is available at https://www.spigotmc.org/resources/this.44055");
					break;
				case UP_TO_DATE: minecord.getLogger().log(Level.INFO, "Your version is up to date.");
					break;
				default:
					break;

				}

			}
			
		});
	}
	public Permission getPermissionManager() {
		return permission;
	}
	public void ready() {
		if (properties == null) throw new IllegalStateException("Properties are not loaded.");
	}
	@Override
	public ComparativeSet<Server> getServers() {
		return servers;
	}
	@Override
	public Server getServer(int id) {
		return servers.getIf(server -> server.getIdentifier() == id).toArray(new Server[servers.size()])[0];
	}
	@Override
	public Server getServer(String name) {
		return servers.getIf(server -> server.getName().equals(name)).toArray(new Server[servers.size()])[0];
	}
	@Override
	public Set<Server> getServers(Sendable sendable) {
		return servers.getIf(server -> server.getSendables().contains(sendable));
	}
	@Override
	public Server getServer(Channel channel) {
		return servers.getIf(server -> server.getChannels().contains(channel)).toArray(new Server[servers.size()])[0];
	}
	@Override
	public ComparativeSet<Sendable> getSendables() {
		return sendables;
	}
	@Override
	public Sendable getSendable(int id) {
		return sendables.getIf(sendable -> sendable.getIdentifier() == id).toArray(new Sendable[sendables.size()])[0];
	}
	@Override
	public Set<Sendable> getSendables(String name) {
		return sendables.getIf(sendable -> sendable.getName().equals(name));
	}
	@Override
	public Server getMain() {
		return main;
	}
	@Override
	public void loadConfiguration() {
		loadProperties();
	}
	@Override
	public void saveConfiguration() {
		try {
			getLogger().log(Level.INFO, "Saving properties to minecord.properties...");
			cm.save(properties);
			getLogger().log(Level.INFO, "Properties are successfully saved.");
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "An error occurred while attempting to save the properties.", e);
		}
	}
	@Override
	public String databaseVersion() {
		return dbversion;
	}
	@Override
	public String oldDatabaseVersion() {
		return olddbversion;
	}
}
