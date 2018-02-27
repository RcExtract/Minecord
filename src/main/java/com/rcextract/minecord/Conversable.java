package com.rcextract.minecord;

import java.util.HashSet;
import java.util.Set;

import com.rcextract.minecord.getters.ChannelPreferenceGetter;
import com.rcextract.minecord.utils.ComparativeSet;
import com.rcextract.minecord.utils.Pair;

public class Conversable implements ConversationSender, ChannelPreferenceGetter {

	private final int id;
	private String name;
	private String desc;
	private final ComparativeSet<ChannelPreference> preferences;
	private Channel main;
	
	@SafeVarargs
	public Conversable(int id, String name, String desc, Channel main, ChannelPreference ... ChannelPreferences) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		try {
			this.preferences = new ComparativeSet<ChannelPreference>(ChannelPreference.class, new Pair<String, Boolean>("getChannel", true));
		} catch (NoSuchMethodException | SecurityException | IllegalArgumentException e) {
			//This exception is never thrown.
			throw new UnsupportedOperationException();
		}
		this.main = main;
	}
	
	@Override
	public int getIdentifier() {
		return id;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String getDescription() {
		return desc;
	}
	@Override
	public void setDescription(String desc) {
		this.desc = desc;
	}
	@Override
	public Set<ChannelPreference> getChannelPreferences() {
		return preferences;
	}
	@Override
	public ChannelPreference getChannelPreference(Channel channel) {
		for (ChannelPreference preference : preferences) 
			if (preference.getChannel() == channel) 
				return preference;
		return null;
	}
	@Override
	public Set<ChannelPreference> getChannelPreferences(boolean notify) {
		Set<ChannelPreference> channelPreferences = new HashSet<ChannelPreference>();
		for (ChannelPreference preference : preferences) 
			if (preference.isNotify() == notify) 
				channelPreferences.add(preference);
		return channelPreferences;
	}
	public Set<ConversablePreference> getConversablePreferences() {
		Set<ConversablePreference> cps = new HashSet<ConversablePreference>();
		for (Server server : Minecord.getServerManager().getServers()) 
			if (server.getPreference(this) != null) 
				cps.add(server.getPreference(this));
		return cps;
	}
	@Override
	public Set<Server> getServers() {
		Set<Server> servers = new HashSet<Server>();
		for (ConversablePreference p : getConversablePreferences()) 
			servers.add(p.getServer());
		return servers;
	}
	@Override
	public Channel getMain() {
		return main;
	}
	@Override
	public void setMain(Channel main) {
		this.main = main;
	}

}
