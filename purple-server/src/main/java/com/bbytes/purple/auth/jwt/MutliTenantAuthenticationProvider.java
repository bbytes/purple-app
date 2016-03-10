package com.bbytes.purple.auth.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bbytes.purple.service.PasswordHashService;
import com.bbytes.purple.utils.TenancyContextHolder;

public class MutliTenantAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private PasswordHashService passwordHashService;

	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {

		MultiTenantAuthenticationToken token = (MultiTenantAuthenticationToken) auth;

		String username = (auth.getPrincipal() == null) ? null : auth.getPrincipal().toString();
		String password = (auth.getCredentials() == null) ? null : auth.getCredentials().toString();
		
		if(username==null)
			throw new UsernameNotFoundException("Login request missing username");
		
		if(password==null)
			throw new BadCredentialsException("Login request missing password");

		TenancyContextHolder.setTenant(token.getTenantId());
		
		UserDetails userFromDB = userDetailsService.loadUserByUsername(username);
		
		if(userFromDB==null)
			throw new UsernameNotFoundException("User not found with email '" + username+"'");
		
		if (!passwordHashService.passwordMatches(password, userFromDB.getPassword())) {
			throw new BadCredentialsException("Login Failed. Bad credentials");
		}

		return token;

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return MultiTenantAuthenticationToken.class.isAssignableFrom(authentication);
	}


}