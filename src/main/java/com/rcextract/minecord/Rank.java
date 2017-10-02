package com.rcextract.minecord;

import java.util.Set;

public class Rank {

	private String name;
	private String desc;
	private String tag;
	private boolean admin;
	private boolean override;
	private Set<Permission> permissions;
	protected Rank(String name, String desc, String tag, boolean admin, boolean override, Set<Permission> permissions) {
		this.name = name;
		this.desc = desc;
		this.tag = tag;
		this.admin = admin;
		this.override = override;
		this.permissions = permissions;
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
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public boolean isAdministrative() {
		return admin;
	}
	public void setAdministrative(boolean admin) {
		this.admin = admin;
	}
	public boolean isOverride() {
		return override;
	}
	public void setOverride(boolean override) {
		this.override = override;
	}
	public Set<Permission> getPermissions() {
		return permissions;
	}
}
