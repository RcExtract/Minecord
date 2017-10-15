package com.rcextract.minecord;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.rcextract.minecord.event.MinecordEvent;
import com.rcextract.minecord.event.UserMessageEvent;

import net.milkbowl.vault.permission.Permission;

/**
 * The core class of Minecord, also the core class of the plugin.
 * <p>
 * All Minecord system preferences are saved here.
 */
public class Minecord extends JavaPlugin {

	private static InternalManager panel;
	private static ConfigManager cm;
	private static DatabaseManager dm;
	protected static Properties properties;
	private static boolean errorDisable;
	protected static Minecord minecord;
	private static Permission permission;
	
	@Override
	public void onEnable() {
		minecord = this;
		cm = new ConfigManager(this);
		panel = new InternalManager();
		permission = Bukkit.getServicesManager().getRegistration(Permission.class).getProvider();
		loadProperties();
		loadData();
		checkUpdate();
		new IncompatibleDetector(this).runTask(this);
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
			try {
				getLogger().log(Level.INFO, "Saving data to database...");
				dm.dropDatabase();
				dm.initialize();
				dm.save();
				dm.close();
				getLogger().log(Level.INFO, "Data are successfully saved.");
			} catch (SQLException e) {
				getLogger().log(Level.SEVERE, "An error occurred while attempting to save the data.", e);
			}
		}
	}
	/**
	 * Gets the record manager.
	 * @return The record manager.
	 */
	public static Recordable<MinecordEvent> getRecordManager() {
		return panel;
	}
	/**
	 * Gets the control panel of Minecord system.
	 * @return The control panel of Minecord system.
	 */
	protected static InternalManager getControlPanel() {
		return panel;
	}
	/**
	 * Gets the database manager.
	 * @return The database manager.
	 */
	public static DatabaseManager getDatabaseManager() {
		return dm;
	}
	/**
	 * Gets the server manager inside the control panel.
	 * @return The server manager inside the control panel.
	 */
	public static ServerManager getServerManager() {
		return panel;
	}
	/**
	 * Gets the user manager inside the control panel.
	 * @return The user manager inside the control panel.
	 */
	public static UserManager getUserManager() {
		return panel;
	}
	/**
	 * Gets the format of a message.
	 * @return The format of a message.
	 */
	public static String getFormat() {
		return properties.getProperty("format");
	}
	protected static String getHost() {
		return properties.getProperty("host");
	}
	protected static String getUsername() {
		return properties.getProperty("username");
	}
	protected static String getPassword() {
		return properties.getProperty("password");
	}
	/**
	 * Gets the amount of messages to be loaded for a user.
	 * @return The amount of messages to be loaded for a user.
	 */
	public static int getMessageLoadCount() {
		return Integer.parseInt(properties.getProperty("message-load-count"));
	}
	/**
	 * Loads messages for a user.
	 * @param user The target user.
	 * @param wash Determination of clearing out old messages.
	 */
	public static void updateMessage(User user, boolean wash) {
		if (user.isOnline()) {
			Player player = user.getOnlinePlayer();
			if (wash) user.clear();
			for (UserMessageEvent event : panel.getRecords(UserMessageEvent.class)) {
				if (event.getChannel() == user.getChannel()) 
					player.sendMessage(event.getMessage());
			}
		}
	}
	public static void reloadConfiguration() {
		String format = getFormat();
		try {
			cm.load(properties);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		if (!(getFormat().equals(format))) 
			for (User user : Minecord.getUserManager().getUsers()) 
				updateMessage(user, true);
	}
	public static String applyFormat(String name, String nickname, String uuid, String message, String date) {
		String format = new String(getFormat());
		format = format.replaceAll("playername", name);
		format = format.replaceAll("playernickname", nickname);
		format = format.replaceAll("playeruuid", uuid);
		format = format.replaceAll("message", message);
		format = format.replaceAll("time", date);
		format = format.replaceAll("&", "¡±");
		return format;
	}
	public static void initialize() {
		if (minecord == null) throw new IllegalStateException("Minecord is not ready.");
		if (dm == null) throw new IllegalStateException("Minecord is not ready.");
		Bukkit.getPluginManager().registerEvents(new EventManager(), minecord);
		minecord.getCommand("minecord").setExecutor(new CommandHandler(minecord));
		if (panel.getServer("default") == null)
			try {
				panel.createServer("default", null, null, null, null, null);
			} catch (DuplicatedException e) {
				//This exception is never thrown.
			}
		for (OfflinePlayer player : Bukkit.getOfflinePlayers()) 
			if (!(Minecord.getUserManager().isRegistered(player))) 
				Minecord.getUserManager().registerPlayer(player, null, null);
		panel.initialize();
	}
	public static void loadProperties() {
		if (minecord == null) throw new IllegalStateException("Minecord is not ready.");
		properties = new Properties();
		try {
			minecord.getLogger().log(Level.INFO, "Loading properties from minecord.properties...");
			cm.load(properties);
			minecord.getLogger().log(Level.INFO, "Properties are successfully loaded.");
		} catch (IOException e) {
			minecord.getLogger().log(Level.SEVERE, "An error occurred while attempting to load the properties.", e);
			errorDisable = true;
			Bukkit.getPluginManager().disablePlugin(minecord);
			return;
		}
	}
	public static void loadData() {
		if (minecord == null) throw new IllegalStateException("Minecord is not ready.");
		if (properties == null) throw new IllegalStateException("Properties are not loaded.");
		Bukkit.getScheduler().runTaskAsynchronously(minecord, new Runnable() {

			@Override
			public void run() {
				try {
					minecord.getLogger().log(Level.INFO, "Loading data from database...");
					dm = new DatabaseManager();
					dm.initialize();
					dm.load();
					minecord.getLogger().log(Level.INFO, "Data are successfully loaded.");
					initialize();
				} catch (ClassNotFoundException | SQLException e) {
					minecord.getLogger().log(Level.SEVERE, "An error occured while attempting to load the data.", e);
					errorDisable = true;
					Bukkit.getPluginManager().disablePlugin(minecord);
					return;
				} finally {
					initialize();
				}
			}
			
		});
	}
	public static void checkUpdate() {
		Bukkit.getScheduler().runTaskAsynchronously(minecord, new Runnable() {

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
				case UPDATE_AVAILABLE: minecord.getLogger().log(Level.INFO, "An update is available at https://www.spigotmc.org/resources/minecord.44055");
					break;
				case UP_TO_DATE: minecord.getLogger().log(Level.INFO, "Your version is up to date.");
					break;
				default:
					break;

				}

			}
			
		});
	}
	public static Permission getPermissionManager() {
		return permission;
	}
}
