package com.bbytes.purple.auth.jwt;

import org.joda.time.DateTime;
import org.springframework.security.core.userdetails.User;

import com.bbytes.purple.utils.StringUtils;
import com.google.common.base.Preconditions;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public final class TokenHandler {

	private final String secret;
	private final AuthUserDetailsService userService;

	public TokenHandler(String secret, AuthUserDetailsService userService) {
		this.secret = StringUtils.checkNotBlank(secret);
		this.userService = Preconditions.checkNotNull(userService);
	}

	public User parseUserFromToken(String token) {
		String username = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
		return userService.loadUserByUsername(username);
	}

	public String createTokenForUser(User user) {
		// expire the token after a day .
		// Time to expire is 24 hrs from issue time
		return Jwts.builder().setSubject(user.getUsername()).signWith(SignatureAlgorithm.HS512, secret)
				.setExpiration(DateTime.now().plusDays(1).toDate()).compact();
	}
}
