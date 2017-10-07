package com.rcextract.minecord;

/*import java.util.ArrayList;*/
import java.util.Date;
import java.util.HashSet;
/*import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;*/
import java.util.Set;

import org.bukkit.Bukkit;
/*import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;*/
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

/*import com.rcextract.minecord.event.UserMessageEvent;
import com.rcextract.minecord.event.UserTagEvent;*/

public class EventManager implements Listener {

	/**
	 * Handles the registration for a new joined player.
	 * @param event The login event of a player.
	 */
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		if (Minecord.getUserManager().getUser(player.getUniqueId()) == null) 
			Minecord.getUserManager().registerPlayer(player, null, null);
		User user = Minecord.getUserManager().getUser(player.getUniqueId());
		if (user.getChannel() == null) user.setChannel(null);
	}
	/**
	 * Distributes the messages to suitable players and serialize them.
	 * @param event The message sent event.
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		//Handles the chat sender.
		event.setCancelled(true);
		//Raw sent message.
		String message = event.getMessage();
		//Sender in player form.
		Player player = event.getPlayer();
		//Sender in user form.
		User sender = Minecord.getUserManager().getUser(player.getUniqueId());
		//Message sent time.
		/*Date date = new Date();*/
		//Players that will instantly receive message after finish handling.
		/*Set<Player> recipients = new HashSet<Player>();*/
		/*Users that will receive message, regardless of its online state. Message will be sent to 
		online players instantly regarding to the above set. Offline players will receive the 
		message while loading messages when joined the server.*/
		Set<User> users = new HashSet<User>();
		//All user tags occured in the message.
		/*List<UserTagEvent> tags = new ArrayList<UserTagEvent>();*/
		users.addAll(sender.getChannel().getMembers());
		for (User user : Minecord.getUserManager().getUsers()) 
			if (Bukkit.getOfflinePlayer(user.getUUID()).isOnline()) 
				Bukkit.getPlayer(user.getUUID()).sendMessage(Minecord.applyFormat(user.getName(), user.getNickName(), user.getUUID().toString(), message, new Date().toString()));
		//Tag detection
		/*LinkedHashMap<String, Player> segments = new LinkedHashMap<String, Player>();
		for (String segment : event.getMessage().split("@")) segments.put(segment, null);
		boolean start = true;
		loop:
		for (Map.Entry<String, Player> entry : segments.entrySet()) {
			if (start) {
				start = false;
				continue;
			}
			if (entry.getKey().startsWith("name:") || entry.getKey().startsWith("uuid:")) {
				String possiblename = entry.getKey().substring(5);
				for (int i = possiblename.length() - 1; i >= 0; i--) {
					Player test = entry.getKey().startsWith("name:") ? Bukkit.getPlayer(possiblename) : Bukkit.getPlayer(UUID.fromString(possiblename));
					if (test != null) {
						entry.setValue(test);
						continue loop;
					}
					possiblename = possiblename.substring(0, i - 1);
				}
				continue;
			}
			if (entry.getKey().startsWith("id:")) {
				String possibleid = entry.getKey().substring(3);
				for (int i = possibleid.length() - 1; i >= 0; i--) {
					Integer id = null;
					try {
						id = Integer.parseInt(possibleid);
					} catch (NumberFormatException e) {
						continue loop;
					}
					User user = Minecord.getUserManager().getUser(id);
					if (user != null && user.getPlayer().isOnline()) {
						entry.setValue(user.getPlayer().getPlayer());
						continue loop;
					}
					possibleid = possibleid.substring(0, i - 1);
				}
				continue;
			}
		}
		JSONMessage tooltip = JSONMessage.create().color(ChatColor.AQUA).then("Click to reply to ").color(ChatColor.GREEN);
		JSONMessage msg = JSONMessage.create().suggestCommand("@name:" + sender.getName() + " ").tooltip(JSONMessage.create(tooltip).then(sender.getName()).color(ChatColor.AQUA).then("."));
		String format = Minecord.applyFormat(sender.getName(), sender.getNickName(), sender.getPlayer().getUniqueId().toString(), "<message>", date.toString());
		for (String segment : format.split("<message>")) {
			msg.then(segment);
			for (Map.Entry<String, Player> entry : segments.entrySet()) {
				if (entry.getValue() != null) {
					UserTagEvent ute = new UserTagEvent(sender, Minecord.getUserManager().getUser(entry.getValue()));
					Bukkit.getPluginManager().callEvent(ute);
					if (ute.getTarget() == null) {
						msg.then(entry.getKey());
						entry.setValue(null);
						continue;
					}
					tags.add(ute);
					JSONMessage actionbar = JSONMessage.create().color(ChatColor.GREEN).then(sender.getName()).color(ChatColor.AQUA).then(" tagged you in his message!");
					actionbar.send(ute.getTarget().getPlayer().getPlayer());
					msg.suggestCommand("@name:" + ute.getTarget().getName());
					msg.tooltip(JSONMessage.create(tooltip).then(ute.getTarget().getName()).color(ChatColor.AQUA).then("."));
					msg.then("@" + ute.getTarget().getName());
					msg.suggestCommand("@name:" + sender.getName()).tooltip(JSONMessage.create(tooltip).then(sender.getName()).color(ChatColor.AQUA).then("."));
				}
			}
		}
		int id = ThreadLocalRandom.current().nextInt();
		while (UserMessageEvent.REGISTERED_IDENTIFIERS.contains(id)) id = ThreadLocalRandom.current().nextInt();
		UserMessageEvent e = new UserMessageEvent(id, sender.getChannel(), sender, message, users, tags, msg);
		Bukkit.getPluginManager().callEvent(e);
		if (!(e.isCancelled())) {
			for (User user : users) if (user.getPlayer().isOnline()) recipients.add(user.getPlayer().getPlayer());
			msg.send(recipients.toArray(new Player[recipients.size()]));
		}
		*/
	}
	/**
	 * Loads previously dismissed messages. Format is not fixed.
	 * @param event The player join event.
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		User user = Minecord.getUserManager().getUser(event.getPlayer().getUniqueId());
		if (user.getChannel() != null)
			Minecord.updateMessage(Minecord.getUserManager().getUser(event.getPlayer().getUniqueId()), false);
	}
}
