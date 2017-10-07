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
import com.rcextract.minecord.event.UserEvent;
import com.rcextract.minecord.event.UserMessageEvent;

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
	private static Minecord minecord;
	
	@Override
	public void onEnable() {
		minecord = this;
		cm = new ConfigManager(this);
		panel = new InternalManager();
		properties = new Properties();
		try {
			getLogger().log(Level.INFO, "Loading properties from minecord.properties...");
			cm.load(properties);
			getLogger().log(Level.INFO, "Properties are successfully loaded.");
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "An error occurred while attempting to load the properties.", e);
			errorDisable = true;
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {

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
				}
			}
			
		});
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {

			@Override
			public void run() {
				Updater.checkForUpdate(minecord);
			}
			
		});
		Bukkit.getPluginManager().registerEvents(new EventManager(), this);
		new IncompatibleDetector(this).runTask(this);
		getCommand("minecord").setExecutor(new CommandHandler(this));
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
				dm.getConnection().close();
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
		OfflinePlayer off = Bukkit.getOfflinePlayer(user.getUUID());
		if (off.isOnline()) {
			Player player = off.getPlayer();
			if (wash) for (int i = 0; i <= 25; i++) player.sendMessage("");
			for (UserEvent e : user.getRecords()) {
				if (e instanceof UserMessageEvent) {
					UserMessageEvent event = (UserMessageEvent) e;
					User sender = event.getUser();
					JSONMessage message = JSONMessage.create().suggestCommand("@uuid:" + sender.getUUID().toString() + " ");
					message.then(applyFormat(sender.getName(), sender.getNickName(), sender.getUUID().toString(), event.getMessage(), event.getDate().toString()));
					message.send(player);
				}
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
		format.replaceAll("playername", name);
		format.replaceAll("playernickname", nickname);
		format.replaceAll("playeruuid", uuid);
		format.replaceAll("message", message);
		format.replaceAll("time", date);
		format.replaceAll("&", "¡±");
		return format;
	}
	private static void initialize() {
		for (OfflinePlayer player : Bukkit.getOfflinePlayers()) 
			if (!(Minecord.getUserManager().isRegistered(player.getUniqueId()))) Minecord.getUserManager().registerPlayer(player, null, null);
		for (User user : Minecord.getUserManager().getUsers()) {
			if (user.getChannel() == null) user.setChannel(null);
			if (user.getRank() == null) user.setRank(null);
		}
	}
}
