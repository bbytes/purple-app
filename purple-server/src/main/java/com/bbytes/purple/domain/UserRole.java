package com.bbytes.purple.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

/**
 * UserRole Domain Object
 * @author akshay
 */

@Data
@Document
public class UserRole {
	
	@Id
	private String roleId;
	
	@Field("role_name")
	private String roleName;
	
	// embedded list 
	private List<Permission> permissions;

	public UserRole(String roleName) {
		this.roleName = roleName;
	}

	public static UserRole NORMAL_USER_ROLE = new UserRole("NORMAL");
	public static UserRole ADMIN_USER_ROLE = new UserRole("ADMIN");
}
