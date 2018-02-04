package com.rcextract.minecord.event;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.ChannelManager;
import com.rcextract.minecord.RankManager;
import com.rcextract.minecord.Server;

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
	private ChannelManager channelManager;
	private RankManager rankManager;
	public ServerCreateEvent(String name, String desc, Boolean approvement, Boolean invitation, ChannelManager channelManager, RankManager rankManager) {
		this.name = name;
		this.desc = desc;
		this.approvement = approvement;
		this.invitation = invitation;
		this.channelManager = channelManager;
		this.rankManager = rankManager;
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
	public ChannelManager getChannelManager() {
		return channelManager;
	}
	public void setChannelManager(ChannelManager channelManager) {
		this.channelManager = channelManager;
	}
	public RankManager getRankManager() {
		return rankManager;
	}
	public void setRankManager(RankManager rankManager) {
		this.rankManager = rankManager;
	}
}
