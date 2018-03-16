package com.rcextract.minecord.getters;

import java.util.Set;

import com.rcextract.minecord.Sendable;
import com.rcextract.minecord.utils.ComparativeSet;

public interface SendableGetter {

	public ComparativeSet<Sendable> getSendables();
	public Sendable getSendable(int id);
	public Set<Sendable> getSendables(String name);
}
