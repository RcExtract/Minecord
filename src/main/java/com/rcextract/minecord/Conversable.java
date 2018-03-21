package com.rcextract.minecord;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.rcextract.minecord.getters.ChannelOptionsGetter;
import com.rcextract.minecord.sql.SQLList;
import com.rcextract.minecord.utils.ArrayMap;
import com.rcextract.minecord.utils.ComparativeSet;

public abstract class Conversable implements Sendable, ChannelOptionsGetter {

	private final int id;
	private String name;
	private String desc;
	private final ComparativeSet<ChannelOptions> options;
	private Channel main;
	
	public Conversable(int id, String name, String desc, Channel main, ChannelOptions ... options) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.options = new ComparativeSet<ChannelOptions>((ChannelOptions element) -> getChannelOptions(element.getChannel()) == null);
		this.main = main;
	}
	
	@SuppressWarnings("unchecked")
	public Conversable(ArrayMap<String, Object> map) {
		Map<String, Object> internal = map.toMap();
		this.id = Minecord.generateSendableIdentifier();
		this.name = (String) internal.get("name");
		this.desc = (String) internal.get("desc");
		this.options = new ComparativeSet<ChannelOptions>((ChannelOptions element) -> getChannelOptions(element.getChannel()) == null, (Collection<ChannelOptions>) internal.get("coptions"));
		this.main = (Channel) internal.get("main");
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
	public Set<ChannelOptions> getChannelOptions() {
		return options;
	}
	@Override
	public ChannelOptions getChannelOptions(Channel channel) {
		for (ChannelOptions preference : options) 
			if (preference.getChannel() == channel) 
				return preference;
		return null;
	}
	@Override
	public Set<ChannelOptions> getChannelOptions(boolean notify) {
		Set<ChannelOptions> channeloptions = new HashSet<ChannelOptions>();
		for (ChannelOptions preference : options) 
			if (preference.isNotify() == notify) 
				channeloptions.add(preference);
		return channeloptions;
	}
	public Set<SendableOptions> getSendableOptions() {
		Set<SendableOptions> options = new HashSet<SendableOptions>();
		for (Server server : Minecord.getServers()) 
			if (server.getSendableOption(this) != null) 
				options.add(server.getSendableOption(this));
		return options;
	}
	@Override
	public Set<Server> getServers() {
		Set<Server> servers = new HashSet<Server>();
		for (SendableOptions options : getSendableOptions()) 
			servers.add(options.getServer());
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
	public abstract void clear();
	public abstract void applyMessage();
	
	@Override
	public ArrayMap<String, Object> serialize() {
		ArrayMap<String, Object> map = new ArrayMap<String, Object>();
		map.put("name", name);
		map.put("desc", desc);
		map.put("options", new SQLList<ChannelOptions>(ChannelOptions.class, options));
		map.put("main", main);
		return map;
	}
	
}
