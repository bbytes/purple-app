package com.bbytes.purple.auth.jwt;

import java.io.IOException;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.bbytes.purple.database.MultiTenantDbFactory;
import com.bbytes.purple.service.TenantResolverService;

public class StatelessLoginFilter extends AbstractAuthenticationProcessingFilter {

	private final TokenAuthenticationService tokenAuthenticationService;
	private final AuthUserDetailsService userDetailsService;
	private final TenantResolverService tenantResolverService;

	protected StatelessLoginFilter(String urlMapping, TokenAuthenticationService tokenAuthenticationService,
			AuthUserDetailsService userDetailsService, TenantResolverService tenantResolverService,
			AuthenticationManager authManager) {
		super(new AntPathRequestMatcher(urlMapping));
		this.userDetailsService = userDetailsService;
		this.tokenAuthenticationService = tokenAuthenticationService;
		this.tenantResolverService = tenantResolverService;
		setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		if (request.getParameter("username")==null) {
			throw new AuthenticationServiceException("Username missing");
		}
		if (request.getParameter("password")==null) {
			throw new AuthenticationServiceException("Password missing");
		}

		final MultiTenantAuthenticationToken loginToken = new MultiTenantAuthenticationToken(
				request.getParameter("username").toString(), request.getParameter("password").toString(),
				tenantResolverService.getTenantIdForUser(request.getParameter("username").toString()));

		return getAuthenticationManager().authenticate(loginToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {

		// Lookup the complete User object from the database and create an
		// Authentication for it
		MultiTenantAuthenticationToken authenticationToken = (MultiTenantAuthenticationToken) authentication;
		MultiTenantDbFactory.setDatabaseNameForCurrentThread(authenticationToken.getTenantId());
		final User authenticatedUser = userDetailsService.loadUserByUsername(authentication.getName());
		final MultiTenantAuthenticationToken userAuthentication = new MultiTenantAuthenticationToken(
				authenticationToken.getTenantId(), authenticatedUser);

		// Add the custom token as HTTP header to the response
		tokenAuthenticationService.addAuthentication(response, userAuthentication);

		// Add the authentication to the Security context
		SecurityContextHolder.getContext().setAuthentication(userAuthentication);
		((HttpServletResponse) response).getWriter().append("");

	}
}