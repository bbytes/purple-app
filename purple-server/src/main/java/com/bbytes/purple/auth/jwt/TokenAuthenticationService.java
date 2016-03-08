package com.bbytes.purple.auth.jwt;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import com.bbytes.purple.service.TenantResolverService;
import com.bbytes.purple.utils.GlobalConstants;

public class TokenAuthenticationService {

	@Value("${token.handler.secret}")
	private String secret;

	@Autowired
	private AuthUserDetailsService userService;
	
	@Autowired
	private TenantResolverService tenantResolverService;
	
	private TokenHandler tokenHandler;
	
	@PostConstruct
	public void setupTokenHandler() {
		tokenHandler = new TokenHandler(secret, userService);
	}

	public String addAuthentication(HttpServletResponse response, MultiTenantAuthenticationToken authentication) {
		final User user = authentication.getDetails();
		String token = tokenHandler.createTokenForUser(user);
		response.addHeader(GlobalConstants.HEADER_AUTH_TOKEN, token);
		return token;
	}

	public Authentication getAuthentication(HttpServletRequest request) {
		final String token = request.getHeader(GlobalConstants.HEADER_AUTH_TOKEN);
		if (token != null) {
			final User user = tokenHandler.parseUserFromToken(token);
			if (user != null) {
				return new MultiTenantAuthenticationToken(tenantResolverService.getTenantIdForUser(user.getUsername()), user);
			}
		}
		return null;
	}
}
