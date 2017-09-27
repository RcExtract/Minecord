package com.rcextract.minecord;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
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

	private static String format;
	private static InternalManager panel;
	private static ConfigManager cm;
	private static DatabaseManager dm;
	private static String host;
	private static String user;
	private static String password;
	private static int MESSAGE_LOAD_COUNT;
	private static boolean errorDisable;
	
	@Override
	public void onEnable() {
		cm = new ConfigManager(this);
		panel = new InternalManager();
		List<String> lines = null;
		try {
			lines = cm.load();
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Failed to load data.");
			e.printStackTrace();
			errorDisable = true;
			Bukkit.getPluginManager().disablePlugin(this);
		}
		host = lines.get(0);
		user = lines.get(1);
		password = lines.get(2);
		format = lines.get(3);
		MESSAGE_LOAD_COUNT = Integer.parseInt(lines.get(4));
		try {
			dm = new DatabaseManager();
			dm.init();
			dm.load();
		} catch (SQLException | ClassNotFoundException e) {
			getLogger().log(Level.SEVERE, "Failed to load data.");
			e.printStackTrace();
			errorDisable = true;
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		Bukkit.getPluginManager().registerEvents(new EventManager(), this);
		getCommand("minecord").setExecutor(new CommandHandler());
		getCommand("users").setExecutor(new CommandHandler());
		for (User user : Minecord.getUserManager().getUsers()) 
			if (user.getChannel() == null) user.switchChannel(null);
	}
	@Override
	public void onDisable() {
		if (!(errorDisable)) {
			try {
				cm.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dm.init();
				dm.save();
				dm.close();
			} catch (SQLException e) {
				getLogger().log(Level.SEVERE, "Failed to save data.");
				e.printStackTrace();
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
		return format;
	}
	protected static String getHost() {
		return host;
	}
	protected static String getUsername() {
		return user;
	}
	protected static String getPassword() {
		return password;
	}
	/**
	 * Gets the amount of messages to be loaded for a user.
	 * @return The amount of messages to be loaded for a user.
	 */
	public static int getMessageLoadCount() {
		return MESSAGE_LOAD_COUNT;
	}
	/**
	 * Loads messages for a user.
	 * @param user The target user.
	 * @param wash Determination of clearing out old messages.
	 */
	public static void updateMessage(User user, boolean wash) {
		OfflinePlayer off = user.getPlayer();
		if (off.isOnline()) {
			Player player = off.getPlayer();
			if (wash) for (int i = 0; i <= 25; i++) player.sendMessage("");
			for (UserEvent e : user.getRecords()) {
				if (e instanceof UserMessageEvent) {
					UserMessageEvent event = (UserMessageEvent) e;
					User sender = event.getUser();
					JSONMessage message = JSONMessage.create().suggestCommand("@uuid:" + sender.getPlayer().getUniqueId().toString() + " ");
					message.then(applyFormat(sender.getName(), sender.getNickName(), sender.getPlayer().getUniqueId().toString(), event.getMessage(), event.getDate().toString()));
					message.send(player);
				}
			}
		}
	}
	public static void reloadConfiguration() {
		List<String> lines = null;
		try {
			lines = cm.load();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		host = lines.get(0);
		user = lines.get(1);
		password = lines.get(2);
		String format = lines.get(3);
		if (!(getFormat().equals(format))) 
			for (User user : Minecord.getUserManager().getUsers()) 
				updateMessage(user, true);
		Minecord.format = format;
		MESSAGE_LOAD_COUNT = Integer.parseInt(lines.get(4));
	}
	public static String applyFormat(String name, String nickname, String uuid, String message, String date) {
		String format = new String(Minecord.format);
		format.replaceAll("<playername>", name);
		format.replaceAll("<playernickname", nickname);
		format.replaceAll("<playeruuid>", uuid);
		format.replaceAll("<message>", message);
		format.replaceAll("<time>", date);
		format.replaceAll("&", "¡±");
		return format;
	}
}
