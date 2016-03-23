package com.bbytes.purple.utils;

import org.springframework.util.Base64Utils;

public final class TokenUtils {
	private TokenUtils() {
	}

	public static String encode(String token) {
		return Base64Utils.encodeToString(token.getBytes());
	}

	public static String decode(String token) {
		return new String(Base64Utils.decodeFromString(token));
	}
}
