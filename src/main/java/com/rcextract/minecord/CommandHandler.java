package com.rcextract.minecord;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;

import com.rcextract.minecord.event.MinecordEvent;
import com.rcextract.minecord.event.server.ServerCreateEvent;

public class CommandHandler implements CommandExecutor {

	private Map<User, Object> editingTarget = new HashMap<User, Object>();
	private Minecord minecord;
	public CommandHandler(Minecord minecord) {
		this.minecord = minecord;
	}
	public static final Map<Player, Boolean> gui = new HashMap<Player, Boolean>();
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("minecord")) {
			if (args.length == 0 || args[0].equalsIgnoreCase("info")) {
				PluginDescriptionFile desc = minecord.getDescription();
				sender.sendMessage(ChatColor.AQUA + desc.getName() + ChatColor.GRAY + " " + desc.getVersion());
				sender.sendMessage("Download available at https://www.spigotmc.org/resources/minecord.44055");
				return true;
			}
			if (args[0].equalsIgnoreCase("reload")) {
				if (args.length == 1) {
					Minecord.loadProperties();
					Minecord.loadData();
					return true;
				}
				if (args[1].equalsIgnoreCase("config")) {
					Minecord.loadProperties();
					return true;
				}
				if (args[1].equalsIgnoreCase("database") || args[1].equalsIgnoreCase("db")) {
					Minecord.loadData();
					return true;
				}
				sender.sendMessage(ChatColor.RED + "Invalid type of data. Please choose config / database (db), or blank out the option for reloading both config and database.");
				return true;
			}
			if (args[0].equalsIgnoreCase("server")) {
				if (args.length == 1) {
					sender.sendMessage(ChatColor.RED + "Please specify a server name!");
					return true;
				}
				Server server = Minecord.getServerManager().getServer(args[1]);
				if (server == null) {
					sender.sendMessage(ChatColor.RED + "The server does not exist!");
					return true;
				}
				sender.sendMessage("Server #" + Integer.toString(server.getIdentifier()) + ":");
				sender.sendMessage("Name: " + server.getName());
				sender.sendMessage("Description: " + server.getDescription());
				sender.sendMessage("Approvement: " + (server.needApprovement() ? "activated" : "deactivated"));
				sender.sendMessage("Invitation: " + (server.needInvitation() ? "activated" : "deactivated"));
				sender.sendMessage("Permanent: " + (server.isPermanent() ? "activated" : "deactivated"));
				sender.sendMessage("The server is " + (!(server.ready()) ? "not" : "") + " ready to join.");
				sender.sendMessage("There " + (server.getActiveMembers().size() == 1 ? "is" : "are") + " " + Integer.toString(server.getActiveMembers().size()) + "member" + (server.getActiveMembers().size() == 1 ? "" : "s") + "online.");
				return true;
			}
			if (args[0].equalsIgnoreCase("servers")) {
				String desc = null;
				if (args.length > 1) 
					if (!(args[1].equals("null"))) 
						desc = args[1];
				Boolean approvement = null;
				if (args.length > 2) 
					if (args[2].equals("true") || args[2].equals("false")) 
						approvement = Boolean.parseBoolean(args[2]);
				Boolean invitation = null;
				if (args.length > 3) 
					if (args[3].equals("true") || args[3].equals("false")) 
						approvement = Boolean.parseBoolean(args[3]);
				Boolean permanent = null;
				if (args.length > 4) 
					if (args[4].equals("true") || args[4].equals("false")) 
						approvement = Boolean.parseBoolean(args[4]);
				Set<User> members = new HashSet<User>();
				for (int i = 5; i < args.length; i++) {
					members.addAll(Minecord.getUserManager().getUsers(args[i]));
				}
				for (Server server : Minecord.getServerManager().getServers()) {
					boolean yeah = desc == null ? true : server.getDescription().equals(desc);
					yeah = yeah && (approvement == null ? true : server.needApprovement() == approvement);
					yeah = yeah && (invitation == null ? true : server.needInvitation());
					yeah = yeah && (permanent == null ? true : server.isPermanent() == permanent);
					yeah = yeah && server.getActiveMembers().containsAll(members);
					if (yeah) {
						sender.sendMessage("Server #" + Integer.toString(server.getIdentifier()) + ":");
						sender.sendMessage("Name: " + server.getName());
						sender.sendMessage("Description: " + server.getDescription());
						sender.sendMessage("Approvement: " + (server.needApprovement() ? "activated" : "deactivated"));
						sender.sendMessage("Invitation: " + (server.needInvitation() ? "activated" : "deactivated"));
						sender.sendMessage("Permanent: " + (server.isPermanent() ? "activated" : "deactivated"));
						sender.sendMessage("The server is " + (!(server.ready()) ? "not " : "") + "ready to join.");
						sender.sendMessage("There " + (server.getActiveMembers().size() == 1 ? "is" : "are") + " " + Integer.toString(server.getActiveMembers().size()) + " member" + (server.getActiveMembers().size() == 1 ? "" : "s") + " online.");
					}
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("channel")) {
				if (args.length == 1) {
					sender.sendMessage(ChatColor.RED + "Please specify a server and channel name!");
					return true;
				}
				Server server = Minecord.getServerManager().getServer(args[1]);
				if (server == null) {
					sender.sendMessage(ChatColor.RED + "The server does not exist!");
					sender.sendMessage(ChatColor.YELLOW + "Make sure the first argument is server name and second argument is channel name.");
					return true;
				}
				if (args.length == 2) {
					sender.sendMessage(ChatColor.RED + "Please specify a channel name!");
					return true;
				}
				Channel channel = server.getChannelManager().getChannel(args[2]);
				if (channel == null) {
					sender.sendMessage(ChatColor.RED + "The channel does not exist!");
					return true;
				}
				sender.sendMessage("Server #" + Integer.toString(server.getIdentifier()) + ",");
				sender.sendMessage("Channel #" + Integer.toString(channel.getIdentifier()) + ":");
				sender.sendMessage("Name: " + channel.getName());
				sender.sendMessage("Description: " + channel.getDescription());
				sender.sendMessage("The channel is " + (!(channel.ready()) ? "not" : "") + " ready to join.");
				int size = channel.getActiveMembers().size();
				sender.sendMessage("There " + (size == 1 ? "is" : "are") + " " + Integer.toString(size) + "member" + (size == 1 ? "" : "s") + "online.");
				return true;
			}
			if (args[0].equalsIgnoreCase("channels")) {
				if (args.length == 1) {
					sender.sendMessage(ChatColor.RED + "Please specify a server!");
					return true;
				}
				Server server = Minecord.getServerManager().getServer(args[1]);
				if (server == null) {
					sender.sendMessage(ChatColor.RED + "The server does not exist!");
					return true;
				}
				String desc = null;
				if (args.length > 2) 
					if (!(args[2].equals("null"))) 
						desc = args[1];
				Boolean main = null;
				if (args.length > 3) 
					if (!(args[3].equals("null"))) 
						main = Boolean.parseBoolean(args[3]);
				Set<User> members = new HashSet<User>();
				for (int i = 4; i < args.length; i++) {
					members.addAll(Minecord.getUserManager().getUsers(args[i]));
				}
				for (Channel channel : server.getChannelManager().getChannels()) {
					boolean yeah = desc == null ? true : channel.getDescription().equals(desc);
					yeah = yeah && (main == null ? true : channel.isMain() == main);
					yeah = yeah && channel.getActiveMembers().containsAll(members);
					if (yeah) {
						sender.sendMessage((channel.isMain() ? "Main " : "") + "Channel #" + Integer.toString(channel.getIdentifier()) + ":");
						sender.sendMessage("Name: " + channel.getName());
						sender.sendMessage("Description: " + channel.getDescription());
						sender.sendMessage("The channel is " + (!(channel.ready()) ? "not" : "") + " ready to join.");
						int size = channel.getActiveMembers().size();
						sender.sendMessage("There " + (size == 1 ? "is" : "are") + " " + Integer.toString(size) + " member" + (size == 1 ? "" : "s") + " online.");
					}
				}
				return true;
			}
			//Deprecated
			if (args[0].equalsIgnoreCase("profile")) {
				Set<User> users = new HashSet<User>();
				if (args.length == 1) {
					sender.sendMessage(ChatColor.RED + "Please specify a user name!");
					return true;
				}
				users.addAll(Minecord.getUserManager().getUsers(args[1]));
				if (users.isEmpty()) {
					sender.sendMessage(ChatColor.YELLOW + "No users found.");
					return true;
				}
				/*Deprecation Message*/sender.sendMessage(ChatColor.YELLOW + "This command is deprecated.");
				for (User target : users) {
					sender.sendMessage("User #" + Integer.toString(target.getIdentifier()) + ":");
					sender.sendMessage("Name: " + target.getName());
					sender.sendMessage("Nickname: " + target.getNickName());
					sender.sendMessage("Description: " + target.getDescription());
					sender.sendMessage("UUID: " + target.getPlayer().getUniqueId().toString());
					sender.sendMessage("Viewing Server: " + target.getMain().getChannel().getChannelManager().getServer());
					sender.sendMessage("Channel: " + target.getMain().getChannel().getName());
					sender.sendMessage("Rank: " + target.getRank().getName());
				}
				return true;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command can only be executed as a player!");
				return true;
			}
			Player player = (Player) sender;
			User user = Minecord.getUserManager().getUser(player);
			if (args[0].equalsIgnoreCase("gui")) {
				if (gui.get(player) == null) gui.put(player, false);
				gui.put(player, !(gui.get(player)));
				player.sendMessage(ChatColor.GREEN + "You have selected to use the " + (gui.get(player) ? "GUI" : "Command System") + "!");
				return true;
			}
			if (args[0].equalsIgnoreCase("create")) {
				if (args.length == 1) {
					sender.sendMessage(ChatColor.RED + "Please specify a name!");
					return true;
				}
				String name = args[2];
				String desc = null;
				Boolean approvement = null;
				Boolean invitation = null;
				if (args.length > 3 && !(args[3].equals("null"))) 
					desc = args[2];
				if (args.length > 4 && !(args[4].equals("null"))) 
					approvement = Boolean.parseBoolean(args[3]);
				if (args.length > 5 && !(args[5].equals("null"))) 
					invitation = Boolean.parseBoolean(args[4]);
				ServerCreateEvent event = new ServerCreateEvent(name, desc, approvement, invitation, new ChannelManager(), new RankManager());
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) return true;
				Server server = null;
				try {
					server = Minecord.getServerManager().createServer(event.getName(), event.getDescription(), event.isApprovement(), event.isInvitation(), event.getChannelManager(), event.getRankManager());
				} catch (DuplicatedException e) {
					sender.sendMessage(ChatColor.RED + "A server with that name already exists!");
					return true;
				}
				sender.sendMessage(ChatColor.GREEN + "A server has been successfully created!");
				Bukkit.dispatchCommand(sender, "minecord join " + server.getName());
				return true;
			}
			if (args[0].equalsIgnoreCase("join")) {
				if (args.length == 1) {
					player.sendMessage(ChatColor.RED + "Please specify a server to join!");
					return true;
				}
				Server server = Minecord.getServerManager().getServer(args[1]);
				if (server == null) {
					player.sendMessage(ChatColor.RED + "Failed to find a server with that name!");
					return true;
				}
				if (!(server.ready())) {
					player.sendMessage(ChatColor.RED + "Failed to join the locked server!");
					return true;
				}
				ServerIdentity identity = user.getIdentity(server);
				if (identity != null && identity.isJoined()) {
					player.sendMessage(ChatColor.RED + "You are already in the server!");
					return true;
				}
				user.join(server);
				if (args.length != 3) {
					player.sendMessage(ChatColor.GREEN + "You have successfully joined the server!");
					return true;
				}
				Channel channel = server.getChannelManager().getChannel(args[2]);
				if (channel == null) {
					player.sendMessage(ChatColor.RED + "Failed to find a channel with that name in the server!");
					player.sendMessage(ChatColor.YELLOW + "Setting your view to the default channel.");
					channel = server.getChannelManager().getMainChannel();
				}
				Bukkit.dispatchCommand(sender, "minecord switchview " + args[2]);
				return true;
			}
			if (args[0].equalsIgnoreCase("switchview")) {
				if (args.length == 1) {
					player.sendMessage(ChatColor.RED + "Please specify a server!");
					return true;
				}
				Server server = Minecord.getServerManager().getServer(args[1]);
				ServerIdentity identity = user.getIdentity(server);
				if (server == null || identity == null || !(identity.isJoined())) {
					player.sendMessage(ChatColor.RED + "Failed to find the server in your list of joined servers!");
					return true;
				}
				if (args.length == 2) {
					player.sendMessage(ChatColor.RED + "Please specify a channel!");
					return true;
				}
				Channel channel = server.getChannelManager().getChannel(args[2]);
				Listener listener = identity.getListener(channel);
				if (channel == null) {
					player.sendMessage(ChatColor.RED + "Failed to find a channel with this name!");
					return true;
				}
				if (!(channel.ready())) {
					player.sendMessage(ChatColor.RED + "Failed to switch view to a locked channel!");
					return true;
				}
				user.setMain(listener);
				player.sendMessage(ChatColor.GREEN + "You have successfully switched view to channel " + channel.getName());
				player.sendMessage(ChatColor.YELLOW + "Loading messages...");
				return true;
			}
			if (args[0].equalsIgnoreCase("leave")) {
				if (args.length == 1) {
					player.sendMessage(ChatColor.RED + "Please specify the server of the channel to leave!");
					return true;
				}
				Server server = Minecord.getServerManager().getServer(args[1]);
				if (server == null) {
					player.sendMessage(ChatColor.RED + "Failed to find a server with that name!");
					return true;
				}
				if (!(server.getActiveMembers().contains(user))) {
					player.sendMessage(ChatColor.YELLOW + "You are not in the server!");
					return true;
				}
				if (args.length == 2) {
					player.sendMessage(ChatColor.RED + "Please specify the channel to leave!");
					return true;
				}
				Channel channel = server.getChannelManager().getChannel(args[2]);
				if (channel == null) {
					player.sendMessage(ChatColor.RED + "Failed to find a channel with that name!");
					return true;
				}
				if (!(channel.getActiveMembers().contains(user))) {
					player.sendMessage(ChatColor.RED + "You are not in the channel!");
					return true;
				}
				
				/*ListenerStatusUpdateEvent event = new ListenerStatusUpdateEvent(user.getListener(channel), ListenerStatus.DEACTIVATED);
				Bukkit.getPluginManager().callEvent(event);
				if (!(event.isCancelled())) {
					user.leave(channel);*/
					player.sendMessage(ChatColor.GREEN + "You have successfully left the channel!");
					/*return true;
				}*/
				return true;
			}
			if (args[0].equalsIgnoreCase("editserver")) {
				if (args.length == 1) {
					player.sendMessage(ChatColor.RED + "Please specify a server!");
					return true;
				}
				Server server = Minecord.getServerManager().getServer(args[1]);
				if (server == null) {
					player.sendMessage(ChatColor.RED + "The server does not exist!");
					return true;
				}
				ServerEditor editor = new ServerEditor(server, minecord);
				player.openInventory(editor.getInventory());
				return true;
			}
			if (args[0].equalsIgnoreCase("select")) {
				if (args.length == 1) {
					Object obj = editingTarget.get(user);
					Class<?> clazz = obj.getClass();
					try {
						player.sendMessage("Selected " + clazz.getSimpleName().toLowerCase() + " " + clazz.getMethod("getName").invoke(obj) + (clazz == Channel.class ? " in server " + Minecord.getServerManager().getServer((Channel) obj).getName() : "") + ".");
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException e) {
						player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "An error occurred while attemping to do this. Please contact server administrator for further information.");
						Minecord.minecord.getLogger().log(Level.SEVERE, "Please open an issue with the following error code at https://github.com/RcExtract/Minecord/issues");
						e.printStackTrace();
						return true;
					} catch (NullPointerException e) {
						player.sendMessage(ChatColor.RED + "Please specify an editing target!");
					}
					return true;
				}
				Server server = Minecord.getServerManager().getServer(args[1]);
				Channel channel = null;
				if (server == null) {
					player.sendMessage(ChatColor.RED + "The server does not exist!");
					player.sendMessage(ChatColor.YELLOW + "Make sure the first argument is server name and second argument is channel name.");
					return true;
				}
				if (args.length == 3) {
					channel = server.getChannelManager().getChannel(args[2]);
					if (channel == null) {
						player.sendMessage(ChatColor.YELLOW + "The channel does not exist. Selection has changed to the server " + server.getName() + "itself.");
					}
				}
				editingTarget.put(user, channel != null ? channel : server);
				player.sendMessage(ChatColor.GREEN + "You have successfully selected " + (channel != null ? "channel " + channel.getName() + " in " : "") + "server " + server.getName() + ".");
				return true;
			}
			if (args[0].equalsIgnoreCase("deselect")) {
				if (!(editingTarget.containsKey(user))) {
					player.sendMessage(ChatColor.YELLOW + "You haven't selected an editing target.");
					return true;
				}
				editingTarget.remove(user);
				return true;
			}
			/*
			 * server:
			 *   <server>:
			 *     disband, setname, setdescription, setapprovement, setinvitation, setpermanent, lock, unlock
			 *     actions:
			 *       kick, invite, approve, join-on-lock, stay-on-lock
			 *     channel:
			 *       create, setmain
			 *       <channel>:
			 *         disband, setname, setdescription, lock, unlock
			 *         actions:
			 *           join-on-lock, stay-on-lock, chat, chat-on-lock
			 *     rank:
			 *       create, setmain
			 *       <rank>:
			 *         delete, rename, redescribe, settag, setadmin, setoverride, editpermissions
			 *   create, setmain
			 *       
			 */
			//Deprecated
			if (!(editingTarget.keySet().contains(user))) {
				player.sendMessage(ChatColor.RED + "You haven't selected an editing target! Please select it with /minecord select <server> <channel|null>.");
				return true;
			}
			Object obj = editingTarget.get(user);
			boolean contains = false;
			Class<?> clazz = obj.getClass();
			for (Method method : clazz.getDeclaredMethods())
				contains = contains || method.getName().equalsIgnoreCase(args[0]);
			if (!(contains)) {
				player.sendMessage(ChatColor.RED + args[0] + "-ing is not available on your editing target!");
				return true;
			}
			boolean permitted = false;
			/*Deprecation Message*/for (Permission permission : user.getRank().getPermissions())
				try {
					permitted = permitted || permission.getName().equalsIgnoreCase("minecord." + clazz.getSimpleName().toLowerCase() + "." + (String) clazz.getMethod("getName").invoke(obj) + "." + args[0]);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "An error occurred while attemping to do this. Please contact server administrator for further information.");
					Minecord.minecord.getLogger().log(Level.SEVERE, "Please open an issue with the following error code at https://github.com/RcExtract/Minecord/issues");
					e.printStackTrace();
					return true;
				}
			if (!(permitted)) {
				try {
					player.sendMessage(ChatColor.RED + "You are not permitted to do " + args[0] + "-ing in " + clazz.getSimpleName() + " " + (String) clazz.getMethod("getName").invoke(obj) + "!");
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "An error occurred while attemping to do this. Please contact server administrator for further information.");
					Minecord.minecord.getLogger().log(Level.SEVERE, "Please open an issue with the following error code at https://github.com/RcExtract/Minecord/issues");
					e.printStackTrace();
					return true;
				}
			}
			Method method = null;
			for (Method methods : clazz.getDeclaredMethods()) 
				if (methods.getName().equalsIgnoreCase(args[0])) 
					if (methods.getName().equalsIgnoreCase(args[0])) 
						method = methods;
			if (args.length - 1 < method.getParameterCount()) {
				player.sendMessage(ChatColor.RED + "Not enough arguments!");
				String correctusage = ChatColor.YELLOW + "Correct usage: /minecord " + args[0];
				for (Parameter parameter : method.getParameters()) 
					correctusage += " " + parameter.getName();
				player.sendMessage(correctusage);
				player.sendMessage(ChatColor.YELLOW + "Please fill the arguments you don't want to fill in with \"null\".");
				return true;
			}
			List<Object> params = new ArrayList<Object>();
			for (int i = 1; i < args.length; i++) {
				if (method.getParameterTypes()[i - 1] == boolean.class) {
					if (!(args[i] == "true" || args[i] == "false")) {
						player.sendMessage(ChatColor.RED + "Argument No." + Integer.toString(i) + " must be either \"true\" or \"false\"! (Boolean argument)");
						return true;
					}
					params.add(Boolean.parseBoolean(args[i]));
					continue;
				}
				params.add(args[i]);
			}
			args[0] = args[0].toLowerCase();
			args[0] = args[0].substring(0, 1).toUpperCase() + args[0].substring(1, 3) + args[0].substring(3, 4).toUpperCase() + args[0].substring(4);
			MinecordEvent event;
			try {
				event = MinecordEvent.class.cast(Class.forName("com.rcextract.minecord.event." + clazz.getSimpleName() + args[0] + "Event").getConstructors()[0].newInstance(params.toArray()));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | SecurityException | ClassNotFoundException e) {
				player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "An error occurred while attemping to do this. Please contact server administrator for further information.");
				Minecord.minecord.getLogger().log(Level.SEVERE, "Please open an issue with the following error code at https://github.com/RcExtract/Minecord/issues");
				e.printStackTrace();
				return true;
			}
			Bukkit.getPluginManager().callEvent(event);
			if (!(event.isCancelled())) 
				try {
					method.invoke(obj, params);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "An error occurred while attemping to do this. Please contact server administrator for further information.");
					Minecord.minecord.getLogger().log(Level.SEVERE, "Please open an issue with the following error code at https://github.com/RcExtract/Minecord/issues");
					e.printStackTrace();
					return true;
				}
			player.sendMessage(ChatColor.GREEN + "Configuration has been successfully applied.");
			return true;
		}
		return false;
	}
}
