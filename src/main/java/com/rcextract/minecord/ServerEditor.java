package com.rcextract.minecord;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ServerEditor implements Listener {

	private Server server;
	private Inventory inventory;
	private ServerElement property;
	private HumanEntity editor;
	private boolean closed;
	public ServerEditor(Server server, Minecord minecord) {
		this.server = server;
		this.inventory = Bukkit.createInventory(null, 27, "Server Editor: " + server.getIdentifier());
		ItemStack name = new ItemStack(Material.CONCRETE);
		ItemMeta namemeta = name.getItemMeta();
		namemeta.setDisplayName("Name: " + server.getName());
		namemeta.setLore(Arrays.asList(new String[] {
				"Click me to edit the server name!"
		}));
		name.setItemMeta(namemeta);
		ItemStack desc = new ItemStack(Material.CONCRETE);
		ItemMeta descmeta = desc.getItemMeta();
		descmeta.setDisplayName("Description: " + server.getDescription());
		descmeta.setLore(Arrays.asList(new String[] {
				"Click me to edit the server description!"
		}));
		desc.setItemMeta(descmeta);
		inventory.setItem(10, name);
		inventory.setItem(11, desc);
		Bukkit.getPluginManager().registerEvents(this, minecord);
	}
	public Server getServer() {
		return server;
	}
	public Inventory getInventory() {
		return inventory;
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		if (inventory.getName().startsWith("Server Editor: ")) {
			if (Integer.parseInt(inventory.getName().substring(15)) == server.getIdentifier()) {
				if (closed == true) throw new IllegalStateException();
				ItemStack itemstack = inventory.getItem(event.getSlot());
				String name = itemstack.getItemMeta().getDisplayName();
				editor = event.getWhoClicked();
				if (name.startsWith("Name: ")) {
					property = ServerElement.NAME;
					return;
				}
				if (name.startsWith("Description: ")) {
					property = ServerElement.DESCRIPTION;
					return;
				}
				if (InventoryClickEvent.getHandlerList().getRegisteredListeners().length == 1) {
					event.setCancelled(true);
					event.getWhoClicked().closeInventory();
				}
			}
		}
	}
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) throws IllegalStateException {
		Player player = event.getPlayer();
		String message = event.getMessage();
		if (editor == player) {
			if (closed == true) throw new IllegalStateException();
			event.setCancelled(true);
			switch (property) {
			case NAME: server.setName(message);
			break;
			case DESCRIPTION: server.setDescription(message);
			break;
			default: return;
			}
		}
	}
	public void close() {
		HandlerList.unregisterAll(this);
		closed = true;
	}
}
