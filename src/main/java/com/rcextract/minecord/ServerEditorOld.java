package com.rcextract.minecord;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ServerEditorOld implements Listener {

	public static final Map<User, Server> editors = new HashMap<User, Server>();
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		User user = Minecord.getUserManager().getUser(event.getPlayer());
		if (editors.keySet().contains(user)) {
			event.setCancelled(true);
			String[] args = event.getMessage().split(" ");
			for (int i = 0; i < args.length - 1; i += 2) {
				switch (args[i].toLowerCase()) {
				case "name": {
					
				}
				}
			}
		}
	}
}
