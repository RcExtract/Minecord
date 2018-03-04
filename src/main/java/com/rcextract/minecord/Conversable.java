package com.rcextract.minecord;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rcextract.minecord.utils.ComparativeSet;
import com.rcextract.minecord.utils.Pair;

public abstract class Conversable implements Sendable {

	static {
		//DataManipulator.register(Conversable.class);
	}
	private final int id;
	private String name;
	private String desc;
	private final ComparativeSet<ChannelOptions> options;
	private Channel main;
	
	@SafeVarargs
	public Conversable(int id, String name, String desc, Channel main, ChannelOptions ... options) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		try {
			this.options = new ComparativeSet<ChannelOptions>(ChannelOptions.class, new Pair<String, Boolean>("getChannel", true));
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
		for (Server server : Minecord.getServerManager().getServers()) 
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
	public List<Object> values() {
		return Arrays.asList(new Object[] {
				id, name, desc, main.getIdentifier()
		});
	}

}
