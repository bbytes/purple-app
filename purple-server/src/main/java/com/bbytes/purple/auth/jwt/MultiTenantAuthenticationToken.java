package com.bbytes.purple.auth.jwt;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import com.bbytes.purple.domain.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class MultiTenantAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -7643009266392747593L;

	private Object credentials;
	private Object principal;
	private String tenantId;

	private User user;
	private boolean authenticated = true;

	public MultiTenantAuthenticationToken(String tenantId, User user) {
		super(null);
		this.tenantId = tenantId;
		this.user = user;
	}

	public MultiTenantAuthenticationToken(Object principal, Object credentials, String tenantId) {
		super(null);
		this.principal = principal;
		this.credentials = credentials;
		this.tenantId = tenantId;
	}

	@JsonIgnore
	public Object getCredentials() {
		if (this.credentials != null)
			return this.credentials;
		else
			return this.user.getPassword();
	}

	public Object getPrincipal() {
		if (this.principal != null)
			return this.principal;
		else
			return this.user.getUsername();

	}

	public String getTenantId() {
		return tenantId;
	}

	@Override
	public String getName() {
		return getPrincipal().toString();
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		if (this.user != null)
			return user.getAuthorities();

		return AuthorityUtils.createAuthorityList(UserRole.NORMAL_USER_ROLE.getRoleName());
	}

	@Override
	@JsonIgnore
	public User getDetails() {
		return user;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
}
