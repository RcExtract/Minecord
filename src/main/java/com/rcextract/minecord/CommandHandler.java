package com.rcextract.minecord;

import java.sql.SQLException;
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
import org.bukkit.plugin.java.JavaPlugin;

public class CommandHandler implements CommandExecutor {

	public static final Map<Player, Boolean> gui = new HashMap<Player, Boolean>();
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("minecord")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify an argument!");
				return true;
			}
			if (args[0].equalsIgnoreCase("info")) {
				PluginDescriptionFile desc = JavaPlugin.getPlugin(Minecord.class).getDescription();
				sender.sendMessage(ChatColor.BLUE + desc.getName() + ChatColor.GRAY + " " + desc.getVersion());
				return true;
			}
			if (args[0].equalsIgnoreCase("reload")) {
				if (args.length == 1) {
					Minecord.reloadConfiguration();
					try {
						Minecord.getDatabaseManager().load();
					} catch (SQLException e) {
						sender.sendMessage(ChatColor.RED + "An error occured while attempting to load data from database.");
						e.printStackTrace();
					}
					return true;
				}
				if (args[1].equalsIgnoreCase("config")) {
					Minecord.reloadConfiguration();
					return true;
				}
				if (args[1].equalsIgnoreCase("database") || args[1].equalsIgnoreCase("db")) {
					try {
						Minecord.getDatabaseManager().load();
					} catch (SQLException e) {
						sender.sendMessage(ChatColor.RED + "An error occured while attempting to load data from database.");
						e.printStackTrace();
					}
					return true;
				}
				sender.sendMessage(ChatColor.RED + "Invalid type of data.");
				return true;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can choose if they want to control with GUI!");
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
					if (!(args.length > i)) break;
					members.addAll(Minecord.getUserManager().getUsers(args[i]));
				}
				for (Server server : Minecord.getServerManager().getServers()) {
					boolean yeah = desc == null ? true : server.getDescription().equals(desc);
					yeah = yeah == true ? (approvement == null ? true : server.needApprovement() == approvement) : false;
					yeah = yeah == true ? (invitation == null ? true : server.needInvitation()) : false;
					yeah = yeah == true ? (permanent == null ? true : server.isPermanent() == permanent) : false;
					yeah = yeah == true ? server.getMembers().containsAll(members) : false;
					if (yeah) {
						JSONMessage message = JSONMessage.create();
						message.tooltip(JSONMessage.create().color(ChatColor.AQUA).then("Click me to join the server"));
						message.then(server.getName()).then(" ").then(Integer.toString(server.getMembers().size())).then(" members online");
						message.send(player);
					}
				}
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
				System.out.println(user == null);
				System.out.println(channel == null);
				if (user.switchChannel(channel)) {
					player.sendMessage(ChatColor.GREEN + "You have successfully joined the server!");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("leave")) {
				if (user.switchChannel(null)) 
					player.sendMessage(ChatColor.GREEN + "You have successfully left the channel!");
				return true;
			}
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("users")) {
			sender.sendMessage("users:");
			for (User user : Minecord.getUserManager().getUsers()) {
				sender.sendMessage(Integer.toString(user.getIdentifier()) + user.getName() + user.getNickName());
			}
			return true;
		}
		return false;
	}
}
