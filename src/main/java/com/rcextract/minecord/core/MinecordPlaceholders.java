package com.rcextract.minecord.core;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import com.rcextract.minecord.Minecord;
import com.rcextract.minecord.Sendable;
import com.rcextract.minecord.Server;
import com.rcextract.minecord.User;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class MinecordPlaceholders extends PlaceholderExpansion {

	@Override
	public String getAuthor() {
		return "RcExtract";
	}

	@Override
	public String getIdentifier() {
		return "minecord";
	}

	@Override
	public String getPlugin() {
		return Minecord.getPlugin().getName();
	}

	@Override
	public String getVersion() {
		return Minecord.getPlugin().getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		for (Sendable sendable : Minecord.getSendables()) 
			if (sendable instanceof User) {
				User user = (User) sendable;
				if (user.getOnlinePlayer() == player) {
					if (identifier.equalsIgnoreCase("servers")) {
						String string = "";
						for (Server server : user.getServers()) 
							string += server.getName() + ", ";
						return string.substring(0, string.length() - 3);
					}
					if (identifier.equalsIgnoreCase("view")) 
						return user.getMain().getName();
					if (identifier.equalsIgnoreCase("name")) 
						return user.getName();
					if (identifier.equalsIgnoreCase("nickname")) 
						return user.getNickName();
					if (identifier.equalsIgnoreCase("uuid")) 
						return user.getOnlinePlayer().getUniqueId().toString();
					try {
						return user.getOnlinePlayer().getClass().getMethod(identifier).invoke(user.getOnlinePlayer()).toString();
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException e) {
						return "";
					}
					
				}
			}
		return "";
	}

}
