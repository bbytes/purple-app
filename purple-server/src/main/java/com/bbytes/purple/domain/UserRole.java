package com.bbytes.purple.domain;

import java.util.List;

import lombok.Data;

/**
 * UserRole Domain Object
 * @author akshay
 */

@Data
public class UserRole {
	
	private String roleName;
	
	// embedded list 
	private List<Permission> permissions;

	public UserRole(String roleName) {
		this.roleName = roleName;
	}

	public static UserRole NORMAL_USER_ROLE = new UserRole("NORMAL");
	public static UserRole ADMIN_USER_ROLE = new UserRole("ADMIN");
}
