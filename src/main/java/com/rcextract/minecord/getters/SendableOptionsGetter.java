package com.rcextract.minecord.getters;

import java.util.Set;

import com.rcextract.minecord.JoinState;
import com.rcextract.minecord.Rank;
import com.rcextract.minecord.Sendable;
import com.rcextract.minecord.SendableOptions;
import com.rcextract.minecord.utils.ComparativeSet;

public interface SendableOptionsGetter {

	public Set<Sendable> getSendables();
	public ComparativeSet<SendableOptions> getSendableOptions();
	public SendableOptions getSendableOption(Sendable sendable);
	public Set<SendableOptions> getSendableOptions(JoinState state);
	public Set<SendableOptions> getSendableOptions(Rank rank);
}
