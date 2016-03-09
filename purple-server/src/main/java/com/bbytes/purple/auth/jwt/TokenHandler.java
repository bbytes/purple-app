package com.bbytes.purple.auth.jwt;

import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public final class TokenHandler {

	private final String secret;

	public TokenHandler(String secret) {
		this.secret = StringUtils.checkNotBlank(secret);
	}

	public String createJWTStringTokenForUser(TokenDataHolder tokenDataHolder) {
		Claims claims = Jwts.claims().setSubject(tokenDataHolder.getUser().getUsername());
		claims.put(GlobalConstants.HEADER_TENANT_ID, tokenDataHolder.getTenantId());
		// user can have only one role so directly fetch it
		String role = tokenDataHolder.getUser().getAuthorities().iterator().next().getAuthority();
		claims.put(GlobalConstants.USER_ROLE, role);

		// expire the token after a day .
		// Time to expire is 24 hrs from issue time
		return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret)
				.setExpiration(DateTime.now().plusDays(1).toDate()).compact();
	}

	public TokenDataHolder parseJWTStringTokenForUser(String jwtStringToken) throws AuthenticationServiceException {
		try {
			Claims body = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwtStringToken).getBody();

			String email = body.getSubject();
			String tenantId = (String) body.get(GlobalConstants.HEADER_TENANT_ID);
			String role = (String) body.get(GlobalConstants.USER_ROLE);
			// need to add expired , account locked etc to
			// mongo db user domain object
			if (email != null && tenantId != null && !tenantId.trim().isEmpty() && role != null
					&& !role.trim().isEmpty()) {
				User userDetail = new User(email, "N/A", AuthorityUtils.createAuthorityList(role));
				TokenDataHolder tokenDataHolder = new TokenDataHolder(userDetail, tenantId);
				return tokenDataHolder;
			} else {
				throw new JwtException("Auth Token not valid, missing key values");
			}

		} catch (Exception e) {
			throw new AuthenticationServiceException("Auth Token not valid or expired or tampered");
		}

	}
}
