package com.rcextract.minecord.bukkitminecord;

import java.util.function.BiFunction;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import com.rcextract.minecord.CommandExpansion;

public class RegisteredCommandHandler {

	private String name;
	private EventPriority priority;
	private final Plugin plugin;
	private final CommandExpansion expansion;
	private final BiFunction<CommandSender, String[], Boolean> executor;
	
	public RegisteredCommandHandler(String name, EventPriority priority, Plugin plugin, CommandExpansion expansion, BiFunction<CommandSender, String[], Boolean> executor) {
		this.name = name;
		this.priority = priority;
		this.plugin = plugin;
		this.expansion = expansion;
		this.executor = executor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public EventPriority getPriority() {
		return priority;
	}

	public void setPriority(EventPriority priority) {
		this.priority = priority;
	}
	
	public Plugin getPlugin() {
		return plugin;
	}

	public CommandExpansion getExpansion() {
		return expansion;
	}

	public BiFunction<CommandSender, String[], Boolean> getExecutor() {
		return executor;
	}
}
