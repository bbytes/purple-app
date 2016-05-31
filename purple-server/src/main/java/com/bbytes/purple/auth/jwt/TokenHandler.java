package com.bbytes.purple.auth.jwt;

import org.joda.time.DateTime;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.StringUtils;
import com.bbytes.purple.utils.TokenUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public final class TokenHandler {

	private final String secret;

	private final boolean saasMode;

	public TokenHandler(String secret, boolean saasMode) {
		this.secret = StringUtils.checkNotBlank(secret);
		this.saasMode = saasMode;
	}

	/**
	 * Create a jwt token for the given user that is valid for next 24 hrs
	 * 
	 * @param tokenDataHolder
	 * @return
	 */
	public String createJWTStringTokenForUser(TokenDataHolder tokenDataHolder) {
		return createJWTStringTokenForUser(tokenDataHolder, 720);
	}

	/**
	 * Create a jwt token for the given user that is valid for next
	 * tokenValidityInHrs hrs. Base 64 encoded token is returned
	 * 
	 * @param tokenDataHolder
	 * @param tokenValidityInHrs
	 * @return
	 */
	public String createJWTStringTokenForUser(TokenDataHolder tokenDataHolder, Integer tokenValidityInHrs) {
		Claims claims = Jwts.claims().setSubject(tokenDataHolder.getUser().getUsername());
		claims.put(GlobalConstants.HEADER_TENANT_ID, tokenDataHolder.getTenantId());
		// user can have only one role so directly fetch it
		String role = tokenDataHolder.getUser().getAuthorities().iterator().next().getAuthority();
		claims.put(GlobalConstants.USER_ROLE, role);

		// expire the token after a day .
		// Time to expire is 24 hrs from issue time
		String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, secret)
				.setExpiration(DateTime.now().plusHours(tokenValidityInHrs).toDate()).compact();

		return TokenUtils.encode(token);
	}

	/**
	 * Parse the jst token and extract the token data ie user 
	 * 
	 * @param jwtStringToken
	 * @return
	 * @throws AuthenticationServiceException
	 */
	public TokenDataHolder parseJWTStringTokenForUser(String jwtStringToken) throws AuthenticationServiceException {

		try {
			String token = TokenUtils.decode(jwtStringToken);
			Claims body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

			String email = body.getSubject();
			String tenantId = (String) body.get(GlobalConstants.HEADER_TENANT_ID);
			String role = (String) body.get(GlobalConstants.USER_ROLE);
			// need to add expired , account locked etc to
			// mongo db user domain object
			if (email != null && role != null && !role.trim().isEmpty()) {

				// if saas mode then auth token should have tenant info
				if (saasMode) {
					if (tenantId == null || tenantId.trim().isEmpty())
						throw new JwtException("Auth Token not valid, missing key values");
				}

				User userDetail = new User(email, "N/A", AuthorityUtils.createAuthorityList(role));
				TokenDataHolder tokenDataHolder = new TokenDataHolder(userDetail, tenantId);
				return tokenDataHolder;
			}

			throw new JwtException("Auth Token not valid, missing key values");

		} catch (Exception e) {
			throw new AuthenticationServiceException("Auth Token not valid or expired or tampered");
		}

	}
}
