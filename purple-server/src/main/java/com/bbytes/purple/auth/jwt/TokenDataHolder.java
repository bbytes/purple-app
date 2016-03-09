package com.bbytes.purple.auth.jwt;

import org.springframework.security.core.userdetails.User;

import lombok.Data;

@Data
public final class TokenDataHolder {

	private final User user;
	private final String tenantId;

	public TokenDataHolder(User user, String tenantId) {
		this.user = user;
		this.tenantId = tenantId;
	}
}
