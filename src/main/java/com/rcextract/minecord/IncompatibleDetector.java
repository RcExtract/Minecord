package com.rcextract.minecord;

import java.util.logging.Level;

import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.scheduler.BukkitRunnable;

public class IncompatibleDetector extends BukkitRunnable {

	private Minecord minecord;
	public IncompatibleDetector(Minecord minecord) {
		this.minecord = minecord;
	}
	@Override
	public void run() {
		for (RegisteredListener listener : AsyncPlayerChatEvent.getHandlerList().getRegisteredListeners()) 
			if (listener.getPriority() == EventPriority.HIGHEST && !(listener.getPlugin() == minecord)) 
				minecord.getLogger().log(Level.WARNING, "An org.bukkit.event.player.AsyncPlayerChatEvent handler is found to be incompatible in " + listener.getListener().getClass().getName() + " in plugin " + listener.getPlugin().getName() + ".");
	}
}
