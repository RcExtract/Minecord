package com.rcextract.minecord.getters;

import java.util.Set;

import com.rcextract.minecord.Sendable;

public interface UserGetter {

	public Set<Sendable> getSendables();
	public Sendable getSendable(int id);
	public Set<Sendable> getUsers(String name);
}
