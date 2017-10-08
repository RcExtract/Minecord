package com.rcextract.minecord;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class CommandHandler implements CommandExecutor {

	private Minecord minecord;
	public CommandHandler(Minecord minecord) {
		this.minecord = minecord;
	}
	public static final Map<Player, Boolean> gui = new HashMap<Player, Boolean>();
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
				sender.sendMessage("There " + (server.getMembers().size() == 1 ? "is" : "are") + " " + Integer.toString(server.getMembers().size()) + "member" + (server.getMembers().size() == 1 ? "" : "s") + "online.");
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
					yeah = yeah && server.getMembers().containsAll(members);
					if (yeah) {
						sender.sendMessage("Server #" + Integer.toString(server.getIdentifier()) + ":");
						sender.sendMessage("Name: " + server.getName());
						sender.sendMessage("Description: " + server.getDescription());
						sender.sendMessage("Approvement: " + (server.needApprovement() ? "activated" : "deactivated"));
						sender.sendMessage("Invitation: " + (server.needInvitation() ? "activated" : "deactivated"));
						sender.sendMessage("Permanent: " + (server.isPermanent() ? "activated" : "deactivated"));
						sender.sendMessage("The server is " + (!(server.ready()) ? "not" : "") + " ready to join.");
						sender.sendMessage("There " + (server.getMembers().size() == 1 ? "is" : "are") + " " + Integer.toString(server.getMembers().size()) + "member" + (server.getMembers().size() == 1 ? "" : "s") + "online.");
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
				sender.sendMessage("Server #" + Integer.toString(server.getIdentifier()) + ":");
				sender.sendMessage("Name: " + server.getName());
				sender.sendMessage("Description: " + server.getDescription());
				sender.sendMessage("Approvement: " + (server.needApprovement() ? "activated" : "deactivated"));
				sender.sendMessage("Invitation: " + (server.needInvitation() ? "activated" : "deactivated"));
				sender.sendMessage("Permanent: " + (server.isPermanent() ? "activated" : "deactivated"));
				sender.sendMessage("The server is " + (!(server.ready()) ? "not" : "") + " ready to join.");
				sender.sendMessage("There " + (server.getMembers().size() == 1 ? "is" : "are") + " " + Integer.toString(server.getMembers().size()) + "member" + (server.getMembers().size() == 1 ? "" : "s") + "online.");
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
					yeah = yeah && channel.getMembers().containsAll(members);
					if (yeah) {
						sender.sendMessage((channel.isMain() ? "Main" : "") + "Channel #" + Integer.toString(channel.getIdentifier()) + ":");
						sender.sendMessage("Name: " + channel.getName());
						sender.sendMessage("Description: " + channel.getDescription());
						sender.sendMessage("The channel is " + (!(channel.ready()) ? "not" : "") + " ready to join.");
						sender.sendMessage("There " + (channel.getMembers().size() == 1 ? "is" : "are") + " " + Integer.toString(channel.getMembers().size()) + "member" + (channel.getMembers().size() == 1 ? "" : "s") + "online.");
					}
				}
				return true;
			}
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
				for (User target : users) {
					sender.sendMessage("User #" + Integer.toString(target.getIdentifier()) + ":");
					sender.sendMessage("Name: " + target.getName());
					sender.sendMessage("Nickname: " + target.getNickName());
					sender.sendMessage("Description: " + target.getDescription());
					sender.sendMessage("UUID: " + target.getUUID().toString());
					sender.sendMessage("Server: " + target.getChannel().getChannelManager().getServer().getName());
					sender.sendMessage("Channel: " + target.getChannel().getName());
					sender.sendMessage("Rank: " + target.getRank().getName());
				}
				return true;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command can only be executed as a player!");
				return true;
			}
			Player player = (Player) sender;
			User user = Minecord.getUserManager().getUser(player.getUniqueId());
			if (args[0].equalsIgnoreCase("gui")) {
				if (gui.get(player) == null) gui.put(player, false);
				gui.put(player, !(gui.get(player)));
				player.sendMessage(ChatColor.GREEN + "You have selected to use the " + (gui.get(player) ? "GUI" : "Command System") + "!");
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
				if (server.getMembers().contains(user)) {
					player.sendMessage(ChatColor.YELLOW + "You are already in the server!");
					return true;
				}
				Channel channel = server.getChannelManager().getMainChannel();
				if (args.length == 3) {
					Channel custom = server.getChannelManager().getChannel(args[2]);
					if (custom == null) {
						player.sendMessage(ChatColor.RED + "Failed to find a channel with that name in the server!");
						player.sendMessage(ChatColor.YELLOW + "Assigning you to the default channel.");
					} else {
						channel = custom;
					}
				}
				if (!(channel.ready())) {
					player.sendMessage(ChatColor.RED + "Failed to join the locked channel!");
					return true;
				}
				if (channel.getMembers().contains(user)) {
					player.sendMessage(ChatColor.YELLOW + "You are already in the channel!");
					return true;
				}
				if (user.setChannel(channel)) {
					player.sendMessage(ChatColor.GREEN + "You have successfully joined the server!");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("leave")) {
				if (user.setChannel(null)) 
					player.sendMessage(ChatColor.GREEN + "You have successfully left the channel!");
				return true;
			}
			return true;
		}
		return false;
	}
}
