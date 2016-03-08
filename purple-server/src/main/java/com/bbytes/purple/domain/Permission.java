package com.bbytes.purple.domain;

import lombok.Data;

/**
 * Permission Domain Object
 * @author akshay
 */

@Data
public class Permission {

	private String permissionName;

	public Permission(String permissionName) {
		this.permissionName = permissionName;
	}

}
