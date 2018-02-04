package com.rcextract.minecord;

import java.util.Set;

public interface ListenerHolder {

	public Set<Listener> getListeners();
	public Listener getListener(Channel channel);
	public Set<Listener> getListeners(boolean notify);
}
