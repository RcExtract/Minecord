package com.rcextract.minecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import com.rcextract.minecord.sql.SQLList;
import com.rcextract.minecord.sql.SerializableAs;
import com.rcextract.minecord.utils.ArrayMap;
import com.rcextract.minecord.utils.ComparativeSet;

@XmlAccessorType(XmlAccessType.FIELD)
@SerializableAs("conversable")
public abstract class Conversable implements Sendable {

	@XmlID
	private final UUID id = UUID.randomUUID();
	private String name;
	private String desc;
	@XmlIDREF
	private final ComparativeSet<ChannelOptions> options;
	@XmlIDREF
	private Channel main;
	
	public Conversable(String name, String desc, Channel main, ChannelOptions ... options) {
		this.name = name;
		this.desc = desc;
		this.options = new ComparativeSet<ChannelOptions>(Arrays.asList(options));
		this.options.setFilter(option -> this.options.getIf(o -> o.getChannel() == option.getChannel()).isEmpty());
		this.main = main;
	}
	
	@SuppressWarnings("unchecked")
	public Conversable(ArrayMap<String, Object> map) {
		Map<String, Object> internal = map.toMap();
		this.name = (String) internal.get("name");
		this.desc = (String) internal.get("desc");
		options = new ComparativeSet<ChannelOptions>((Collection<ChannelOptions>) internal.get("coptions"));
		options.setFilter(option -> this.options.getIf(o -> o.getChannel() == option.getChannel()).isEmpty());
		this.main = (Channel) internal.get("main");
	}
	
	@Override
	public UUID getIdentifier() {
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
	public ComparativeSet<ChannelOptions> getChannelOptions() {
		return options;
	}
	public Set<SendableOptions> getSendableOptions() {
		Set<SendableOptions> options = new HashSet<SendableOptions>();
		for (Server server : Minecord.getServers()) 
			options.add(server.getSendableOptions().getIf(option -> option.getSendable() == this).get());
		options.removeIf(option -> option == null);
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
		map.put("coptions", new SQLList<ChannelOptions>(ChannelOptions.class, options));
		map.put("main", main);
		return map;
	}
	
}
