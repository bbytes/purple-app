package com.bbytes.purple.auth.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.bbytes.purple.database.MultiTenantDbFactory;
import com.google.common.base.Preconditions;

public class StatelessAuthenticationFilter extends GenericFilterBean {

	private final TokenAuthenticationService tokenAuthenticationService;

	public StatelessAuthenticationFilter(TokenAuthenticationService tokenAuthenticationService) {
		this.tokenAuthenticationService = Preconditions.checkNotNull(tokenAuthenticationService);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		Authentication authentication = tokenAuthenticationService.getAuthentication((HttpServletRequest) request);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		// if authentication is null then token expired so send http 401 back
		if (authentication != null) {
			// Very important for multi tenant to work : set tenant to current db resolver after successful verification
			MultiTenantDbFactory
					.setDatabaseNameForCurrentThread(((MultiTenantAuthenticationToken) authentication).getTenantId());
			((HttpServletResponse) response).setStatus(HttpServletResponse.SC_OK);
		} else {
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		}
		filterChain.doFilter(request, response); // always continue
	}
}
