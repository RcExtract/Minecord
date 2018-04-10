package com.rcextract.minecord.bukkitminecord;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLTimeoutException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.rcextract.minecord.Channel;
import com.rcextract.minecord.ChannelOptions;
import com.rcextract.minecord.CommandExpansion;
import com.rcextract.minecord.CommandHandler;
import com.rcextract.minecord.ConfigurationManager;
import com.rcextract.minecord.Conversable;
import com.rcextract.minecord.DataManipulator;
import com.rcextract.minecord.Message;
import com.rcextract.minecord.Minecord;
import com.rcextract.minecord.MinecordPlugin;
import com.rcextract.minecord.Rank;
import com.rcextract.minecord.Sendable;
import com.rcextract.minecord.SendableOptions;
import com.rcextract.minecord.Server;
import com.rcextract.minecord.User;
import com.rcextract.minecord.event.user.UserMessageEvent;
import com.rcextract.minecord.sql.DataLoadException;
import com.rcextract.minecord.sql.DatabaseAccessException;
import com.rcextract.minecord.sql.DriverNotFoundException;
import com.rcextract.minecord.sql.SQLConnectException;
import com.rcextract.minecord.utils.ComparativeSet;
import com.rcextract.minecord.utils.EnhancedSet;

import net.milkbowl.vault.permission.Permission;

/**
 * The core class of Minecord, also the core class of the plugin.
 * <p>
 * All Minecord system preferences are saved here.
 */
public class BukkitMinecord extends JavaPlugin implements MinecordPlugin {

	private ConfigurationManager cm;
	private DataManipulator dm;
	private PreferencesManager pm;
	private ComparativeSet<RegisteredCommandHandler> ce;
	private Updater updater;
	private String format;
	private long configurationDuration;
	private long dataDuration;
	private Runnable dataLoad;
	private ConfigurationLoader cloader;
	private Runnable dataSave;
	private Runnable configurationSave;
	private SaveOptions saveConfiguration;
	private boolean saveData;
	private Scanner scanner;
	protected String dbversion;
	protected String olddbversion;
	private EnhancedSet<Server> servers;
	private Server main;
	private EnhancedSet<Sendable> sendables;

	@Override
	public void onEnable() {
		/*
		 * if !generate source 1
		 *     goto load source 2
		 * if load source 1
		 *     set save to source 1
		 * else
		 *     set save to source 2
		 *     if load source 2
		 *         set save to both sources
		 *     else
		 *         set do not save
		 *         use default data
		 */
		dbversion = "7dot0";
		olddbversion = "6dot0";
		ce = new ComparativeSet<RegisteredCommandHandler>();
		ce.setFilter(rch -> ce.getIf(o -> o.getExecutor() == rch.getExecutor()).isEmpty());
		ce.close();
		servers = new EnhancedSet<Server>();
		sendables = new EnhancedSet<Sendable>();
		cm = new SimpleConfigurationManager(this);
		pm = new PreferencesManager();
		updater = new Updater(this);
		cloader = new ConfigurationLoader(this);
		Logger logger = getLogger();
		
		dataLoad = new Runnable() {

			@Override
			public void run() {
				try {
					logger.log(Level.INFO, "Attempting to connect to database...");
					dm = new DatabaseDataManipulator(pm.getHost(), pm.getUser(), pm.getPassword());
					logger.log(Level.INFO, "Loading data from database...");
					dm.load();
					logger.log(Level.INFO, "Data is successfully loaded.");
					saveData = true;
					if (servers.isEmpty()) {
						logger.log(Level.WARNING, "Data is empty. Ignore this warning if you are using Minecord for the first time.");
						logger.log(Level.INFO, "Generating default data.");
						Server server = new Server("default", "This is the main server.", false, false, false, false, null, new HashSet<Channel>(), null, new HashSet<Rank>(), new ComparativeSet<SendableOptions>());
						servers.add(server);
						main = server;
					}
					return;
				} catch (SQLTimeoutException | SQLConnectException e) {
					logger.log(Level.SEVERE, "Failed to connect to database.", e);
					logger.log(Level.SEVERE, "Make sure you have provided the correct host, user and password.");
					logger.log(Level.SEVERE, "Also, make sure the SQL server is running.");
				} catch (DriverNotFoundException e) {
					logger.log(Level.SEVERE, "Failed to connect to database.", e);
					logger.log(Level.SEVERE, "Make sure you are using MySQL.");
				} catch (DataLoadException e) {
					logger.log(Level.SEVERE, "An error occurred while loading necessary resources.", e);
				} catch (Throwable e) {
					logger.log(Level.SEVERE, "An exception has been thrown by one of the deserializers while loading data.", e);
					logger.log(Level.SEVERE, "Please open an issue at https://github.com/RcExtract/Minecord/issues.");
					logger.log(Level.SEVERE, "You will be referred to the developer of the deserializer if possible.");
				}
				logger.log(Level.WARNING, "Default data will be used temporaily. It will not replace the data in the database, if exists.");
				Server server = new Server("default", "This is the main server.", false, false, false, false, null, new HashSet<Channel>(), null, new HashSet<Rank>(), new ComparativeSet<SendableOptions>());
				servers.add(server);
				main = server;
			}
			
		};
		dataSave = new Runnable() {

			@Override
			public void run() {
				if (!(saveData)) return;
				try {
					logger.log(Level.INFO, "Saving data to database...");
					dm.save();
					logger.log(Level.INFO, "Data is successfully saved.");
					return;
				} catch (SQLTimeoutException | DatabaseAccessException e) {
					logger.log(Level.SEVERE, "Failed to connect to database.", e);
					logger.log(Level.SEVERE, "Make sure you have provided the correct host, user and password.");
					logger.log(Level.SEVERE, "Also, make sure the SQL server is running.");
				} catch (DataLoadException e) {
					logger.log(Level.SEVERE, "An error occurred while loading necessary resources.", e);
				} catch (Throwable e) {
					logger.log(Level.SEVERE, "An exception has been thrown by one of the serializers while saving data.", e);
					logger.log(Level.SEVERE, "Please open an issue at https://github.com/RcExtract/Minecord/issues.");
					logger.log(Level.SEVERE, "You will be referred to the developer of the serializer if possible.");
				}
				//backup saving method: XML
			}
			
		};
		configurationSave = new Runnable() {

			@Override
			public void run() {
				switch (saveConfiguration) {
				case DO_NOT_SAVE:
					return;
				case SAVE_SOURCE_ONE: 
					try {
						saveToFile();
					} catch (IOException e) {
						logger.log(Level.SEVERE, "An unexpected error occurred while attempting to save configuration.", e);
						logger.log(Level.SEVERE, "This is usually caused by the relationship between JRE and System.");
						logger.log(Level.SEVERE, "For example, JRE does not have permission to write to file on the disk.");
						saveToPreference();
					}
				case SAVE_SOURCE_TWO:
					saveToPreference();
				case SAVE_ALL: {
					try {
						saveToFile();
					} catch (IOException e) {
						logger.log(Level.SEVERE, "An unexpected error occurred while attempting to save configuration.", e);
						logger.log(Level.SEVERE, "This is usually caused by the relationship between JRE and System.");
						logger.log(Level.SEVERE, "For example, JRE does not have permission to write to file on the disk.");
					}
					saveToPreference();
				}
				default:
					break;
				}
			}
			
			public void saveToFile() throws IOException {
				logger.log(Level.INFO, "Saving configuration...");
				Configuration config = cm.getConfiguration();
				config.set("format", format);
				config.set("auto-save-configuration", configurationDuration);
				config.set("auto-save-data", dataDuration);
				cm.saveConfiguration();
				logger.log(Level.INFO, "Configuration is successfully saved.");
			}
			
			public void saveToPreference() {
				logger.log(Level.INFO, "Saving configuration as backup...");
				pm.setBackupFormat(format);
				pm.setBackupConfigurationDuration(configurationDuration);
				pm.setBackupDataDuration(dataDuration);
				logger.log(Level.INFO, "Configuration is successfully saved as backup.");
			}
			
		};
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {

			@Override
			public void run() {
				cloader.load();
			}
			
		});
		scanner = new Scanner(System.in);
		BukkitMinecord minecord = this;
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {

			@Override
			public void run() {
				ConsoleCommandSender sender = Bukkit.getConsoleSender();
				String command = scanner.nextLine();
				if (command.startsWith("minecord connect")) {
					String[] args = command.split("(minecord connect | )");
					if (args.length < 3) {
						sender.sendMessage(ChatColor.RED + "Please specify host, user and password!");
						run();
						return;
					}
					pm.setHost(args[0]);
					pm.setUser(args[1]);
					pm.setPassword(args[2]);
					sender.sendMessage(ChatColor.GREEN + "Host, user and password are successfully saved.");
					if (dm == null) 
						Bukkit.getScheduler().runTaskAsynchronously(minecord, dataLoad);
					run();
				}
			}
			
		});
		try {
			if (!(pm.isConfigured())) {
				Bukkit.getScheduler().runTask(this, new Runnable() {

					@Override
					public void run() {
						System.out.println("+----------------------------------------------------------------------------------------------------+");
						System.out.println("                                   Thank you for choosing Minecord!                                   ");
						System.out.println("                                                                                                      ");
						System.out.println("          Starting from version 1.1.3, Database connection options will not be stored inside          ");
						System.out.println("                     a configuration file due to security reasons. Please execute                     ");
						System.out.println("          /minecord connect <host> <user> <password>. This is only required to complete once          ");
						System.out.println("                              unless you want to change the preferences.                              ");
						System.out.println("+----------------------------------------------------------------------------------------------------+");
					}
					
				});
			} else 
				Bukkit.getScheduler().runTaskAsynchronously(this, dataLoad);		
		} catch (BackingStoreException e) {
			getLogger().log(Level.SEVERE, "An error occurred while attempting to read the secured configurations.", e);
			saveConfiguration = SaveOptions.DO_NOT_SAVE;
			saveData = false;
		}
		new IncompatibleDetector(this).runTask(this);
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) 
			if (new MinecordPlaceholders().register())
				logger.log(Level.INFO, "Placeholders are successfully registered.");
			else 
				logger.log(Level.INFO, "An unexpected error occurred while attempting to register the placeholders.");
		else 
			logger.log(Level.INFO, "Could not find plugin PlaceHolderAPI. Placeholders\' registration cancelled.");
		if (dataDuration > 0L) 
			Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
	
				@Override
				public void run() {
					configurationSave.run();
					dataSave.run();
				}
				
			}, dataDuration * 20L, dataDuration * 20L);
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	@Override
	public void onDisable() {
		dataSave.run();
		configurationSave.run();
		scanner.close();
	}
	public ConfigurationManager getConfigurationManager() {
		return cm;
	}
	public void setConfigurationManager(ConfigurationManager configurationManager) {
		Validate.notNull(configurationManager);
		this.cm = configurationManager;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public void setConfigurationAutoSaveInterval(long length) {
		this.configurationDuration = length;
	}
	public void setDataAutoSaveInterval(long length) {
		this.dataDuration = length;
	}
	public DataManipulator getDataManipulator() {
		return dm;
	}
	public void setDataManipulator(DataManipulator dataManipulator) {
		Validate.notNull(dataManipulator);
		this.dm = dataManipulator;
	}
	public PreferencesManager getPreferencesManager() {
		return pm;
	}
	public Set<RegisteredCommandHandler> getRegisteredCommandHandlers() {
		return Collections.unmodifiableSet(ce);
	}
	public boolean registerCommandExpansion(CommandExpansion expansion, Plugin plugin) {
		Class<? extends CommandExpansion> clazz = expansion.getClass();
		for (Method method : clazz.getMethods()) {
			if (method.getReturnType() == boolean.class && method.isAccessible() && method.getExceptionTypes().length == 0) {
				CommandHandler handler = method.getAnnotation(CommandHandler.class);
				Class<?>[] parametertypes = method.getParameterTypes();
				if (handler != null && parametertypes[0] == CommandSender.class && parametertypes[1] == String[].class) {
					ce.add(new RegisteredCommandHandler(handler.value(), handler.priority(), plugin, expansion, (CommandSender sender, String[] args) -> { try {
						return (boolean) method.invoke(expansion, sender, args);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						return false;
					} }));
					return true;
				}
			}
		}
		return false;
	}
	
	public void unregisterCommandExpansion(CommandExpansion expansion) {
		ce.forEach(rch -> {
			if (rch.getExpansion() == expansion) ce.remove(rch);
		});
	}
	
	public Permission getPermissionManager() {
		if (Bukkit.getServicesManager().isProvidedFor(Permission.class)) 
			return Bukkit.getServicesManager().getRegistration(Permission.class).getProvider();
		return null;
	}
	public Boolean isUpdateAvailable() {
		switch (updater.check()) {
		case CONNECTION_FAILURE:
			return null;
		case DATA_ACCESSED:
			return null;
		case UPDATE_AVAILABLE:
			return true;
		case UP_TO_DATE:
			return false;	
		default:
			return null;
		}
	}
	@Override
	public boolean generateDataFolder() {
		return cm.generateDataFolder();
	}
	@Override
	public boolean generateConfigurationFile() throws IOException {
		return cm.generateConfigurationFile();
	}
	@Override
	public void loadConfiguration() throws FileNotFoundException, IOException, InvalidConfigurationException {
		cm.loadConfiguration();
	}
	@Override
	public void saveConfiguration() throws IOException {
		cm.saveConfiguration();
	}
	@Override
	public String databaseVersion() {
		return dbversion;
	}
	@Override
	public String oldDatabaseVersion() {
		return olddbversion;
	}
	public String getFormat() {
		return format;
	}
	
	@Deprecated
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

	public EnhancedSet<Server> getServers() {
		return servers;
	}

	public EnhancedSet<Sendable> getSendables() {
		return sendables;
	}

	@Override
	public Channel getChannel(UUID id) {
		for (Server server : servers) 
			for (Channel channel : server.getChannels()) 
				if (channel.getIdentifier().equals(id)) 
					return channel;
		return null;
	}
	@Override
	public Server getMain() {
		return main;
	}
	@Override
	public void setMain(Server main) {
		Validate.notNull(main);
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("minecord")) {
			if (args.length == 0 || args[0].equalsIgnoreCase("info")) {
				sender.sendMessage("This server is currently running " + ChatColor.AQUA + getName() + " " + ChatColor.YELLOW + getDescription().getVersion());
				return true;
			}
			if (args[0].equalsIgnoreCase("status")) {
				if (dm == null) 
					sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Minecord is not ready to use!");
				else 
					sender.sendMessage(ChatColor.GREEN + "Minecord is ready to use.");
				return true;
			}
			if (args[0].equalsIgnoreCase("connect")) {
				//Handled externally by a Scanner.
				return true;
			}
			EventPriority[] priorities = EventPriority.values();
			ArrayUtils.reverse(priorities);
			EnhancedSet<RegisteredCommandHandler> rchs = ce.getIf(rch -> rch.getName().equalsIgnoreCase(args[0]));
			for (EventPriority priority : priorities) 
				for (RegisteredCommandHandler rch : rchs.getIf(rch -> rch.getPriority() == priority)) 
					if (rch.getExecutor().apply(sender, Arrays.copyOfRange(args, 2, args.length))) return true;
					else continue;
			sender.sendMessage("Command " + args[0] + " does not exist!");
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		Minecord.getSendables().add(new User(player.getName(), "A default user description", player.getCustomName(), player, getMain().getMainChannel(), new ChannelOptions(getMain().getMainChannel(), true, 0)));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		User sender = getSendables().getIf(sendable -> sendable instanceof User && ((User) sendable).getPlayer() == event.getPlayer()).toArray(new User[1])[0];
		Channel main = sender.getMain();
		//This set stores conversables which applyMessage should be executed instantly
		Set<Conversable> conversables = new HashSet<Conversable>();
		sender.getMain().getServer().getSendableOptions().forEach(soptions -> {
			Sendable sendable = soptions.getSendable();
			if (sendable instanceof Conversable && sendable.getMain() == main) 
				conversables.add((Conversable) sendable);
		});
		UserMessageEvent e = new UserMessageEvent(event.getMessage(), main, sender, conversables);
		Bukkit.getPluginManager().callEvent(e);
		if (e.isCancelled()) return;
		main.getMessages().add(new Message(sender, e.getMessage(), e.getDate()));
		conversables.forEach(conversable -> conversable.applyMessage());
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		getSendables().getIf(sendable -> sendable instanceof User && ((User) sendable).getPlayer() == event.getPlayer()).toArray(new User[1])[0].applyMessage();
	}
}
