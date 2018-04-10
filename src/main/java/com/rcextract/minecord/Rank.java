package com.rcextract.minecord;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;

import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;

import com.rcextract.minecord.sql.DatabaseSerializable;
import com.rcextract.minecord.sql.SerializableAs;
import com.rcextract.minecord.utils.ArrayMap;

@XmlAccessorType(XmlAccessType.FIELD)
@SerializableAs("rank")
public class Rank implements DatabaseSerializable {

	@XmlID
	private final UUID id = UUID.randomUUID();
	private String name;
	private String desc;
	private ChatColor color;
	private boolean admin;
	private boolean override;
	private final Set<Permission> permissions;
	public Rank(String name, String desc, ChatColor color, boolean admin, boolean override, Set<Permission> permissions) {
		this.name = name;
		this.desc = desc;
		this.color = color;
		this.admin = admin;
		this.override = override;
		this.permissions = permissions;
	}
	@SuppressWarnings("unchecked")
	public Rank(ArrayMap<String, Object> map) {
		Map<String, Object> internal = map.toMap();
		this.name = (String) internal.get("name");
		this.desc = (String) internal.get("desc");
		this.color = (ChatColor) internal.get("color");
		this.admin = (boolean) internal.get("admin");
		this.override = (boolean) internal.get("override");
		this.permissions = (Set<Permission>) internal.get("permissions");
	}
	public UUID getIdentifier() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return desc;
	}
	public void setDescription(String desc) {
		this.desc = desc;
	}
	public ChatColor getColor() {
		return color;
	}
	public void setColor(ChatColor color) {
		this.color = color;
	}
	public boolean isAdministrative() {
		return admin;
	}
	public void setAdministrative(boolean admin) {
		this.admin = admin;
	}
	public boolean isOverride() {
		return override;
	}
	public void setOverride(boolean override) {
		this.override = override;
	}
	public Set<Permission> getPermissions() {
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
		return permissions;
	}
	public Server getServer() {
		for (Server server : Minecord.getServers()) 
			if (server.getRanks().contains(this)) 
				return server;
		return null;
	}
	@Deprecated
	public boolean isMain() {
		return getServer().getMainRank() == this;
	}
	@Override
	public ArrayMap<String, Object> serialize() {
		ArrayMap<String, Object> map = new ArrayMap<String, Object>();
		map.put("name", name);
		map.put("desc", desc);
		map.put("color", color);
		map.put("admin", admin);
		map.put("override", override);
		map.put("permissions", permissions);
		return map;
	}
}
