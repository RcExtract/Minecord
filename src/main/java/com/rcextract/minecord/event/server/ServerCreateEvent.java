package com.rcextract.minecord.event.server;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Channel;
//import com.rcextract.minecord.RankManager;
import com.rcextract.minecord.Server;
import com.rcextract.minecord.event.MinecordEvent;

/**
 * Represents the creation of a {@link Server}.
 */
public class ServerCreateEvent extends MinecordEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private String name;
	private String desc;
	private Boolean approvement;
	private Boolean invitation;
	private final Set<Channel> channels;
	private Channel main;
	//private RankManager rankManager;
	public ServerCreateEvent(String name, String desc, Boolean approvement, Boolean invitation/*, RankManager rankManager*/, Channel main, Channel ... channels) {
		this.name = name;
		this.desc = desc;
		this.approvement = approvement;
		this.invitation = invitation;
		//this.rankManager = rankManager;
		this.main = main;
		this.channels = new HashSet<Channel>(Arrays.asList(channels));
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
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
	public Boolean isApprovement() {
		return approvement;
	}
	public void setApprovement(Boolean approvement) {
		this.approvement = approvement;
	}
	public Boolean isInvitation() {
		return invitation;
	}
	public void setInvitation(Boolean invitation) {
		this.invitation = invitation;
	}
	/*public RankManager getRankManager() {
		return rankManager;
	}
	public void setRankManager(RankManager rankManager) {
		this.rankManager = rankManager;
	}*/

	public Channel getMain() {
		return main;
	}

	public void setMain(Channel main) {
		this.main = main;
	}

	public Set<Channel> getChannels() {
		return channels;
	}
}
