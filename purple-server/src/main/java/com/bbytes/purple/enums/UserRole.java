package com.bbytes.purple.enums;

public enum UserRole {

	ADMIN("Admin"), NORMAL("Team Member"), MANAGER("Manager");

	String displayName;

	private UserRole(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
