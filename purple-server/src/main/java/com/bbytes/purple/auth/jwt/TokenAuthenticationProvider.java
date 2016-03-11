package com.bbytes.purple.auth.jwt;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import com.bbytes.purple.service.TenantResolverService;
import com.bbytes.purple.utils.GlobalConstants;

public class TokenAuthenticationProvider {

	private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationProvider.class);

	@Value("${token.handler.secret}")
	private String secret;

	@Autowired
	private TenantResolverService tenantResolverService;

	@Autowired
	private AuthUserDetailsService authUserDetailsService;

	private TokenHandler tokenHandler;

	@PostConstruct
	public void setupTokenHandler() {
		tokenHandler = new TokenHandler(secret);
	}

	/**
	 * The method is called after successful login to add the jwt token header
	 * 
	 * @param response
	 * @param authentication
	 * @return
	 */
	public String addAuthentication(HttpServletResponse response, MultiTenantAuthenticationToken authentication) {
		final User user = authentication.getDetails();
		final String userTenantId = tenantResolverService.findTenantIdForUserEmail(user.getUsername());
		final TokenDataHolder tokenDataHolder = new TokenDataHolder(user, userTenantId);
		String jwtStringToken = tokenHandler.createJWTStringTokenForUser(tokenDataHolder);
		logger.debug("Auth token for user :-  " + user.getUsername());
		logger.debug("Auth token :-  " + jwtStringToken);
		response.addHeader(GlobalConstants.HEADER_AUTH_TOKEN, jwtStringToken);
		return jwtStringToken;
	}

	/**
	 * Method called from filter to verify every request in the app for jwt auth
	 * token
	 * 
	 * @param request
	 * @return
	 */
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationServiceException {
		String jwtStringToken = request.getHeader(GlobalConstants.HEADER_AUTH_TOKEN);
		
		// if direct access without login based on url access token : Use case email embedded link to ui 
		if (jwtStringToken == null) {
			jwtStringToken = request.getParameter(GlobalConstants.URL_AUTH_TOKEN);
			if (jwtStringToken != null)
				response.addHeader(GlobalConstants.HEADER_AUTH_TOKEN, jwtStringToken);
		}

		if (jwtStringToken != null) {
			final TokenDataHolder tokenDataHolder = tokenHandler.parseJWTStringTokenForUser(jwtStringToken);
			if (tokenDataHolder != null) {
				return new MultiTenantAuthenticationToken(tokenDataHolder.getTenantId(), tokenDataHolder.getUser());
			}
		}

		throw new AuthenticationServiceException("Auth token header missing");
	}

	public String getAuthTokenForUser(String email, Integer tokenValidityInHrs) {
		User user = authUserDetailsService.loadUserByUsername(email);
		final String userTenantId = tenantResolverService.findTenantIdForUserEmail(email);
		final TokenDataHolder tokenDataHolder = new TokenDataHolder(user, userTenantId);
		return tokenHandler.createJWTStringTokenForUser(tokenDataHolder);
	}
}
