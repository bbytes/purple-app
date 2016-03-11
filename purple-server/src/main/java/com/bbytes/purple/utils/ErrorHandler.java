package com.bbytes.purple.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bbytes.purple.rest.dto.models.RestResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ErrorHandler {

	private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

	public static final String AUTH_FAILURE = "authentication_failure";

	public static final String USER_NOT_FOUND = "user_not_found";

	public static final String PASSWORD_INCORRECT = "bad_credentials";
	
	public static final String SIGN_UP_FAILED = "sign_up_failed";
	
	public static final String ORG_NOT_UNIQUE = "organization_not_unique";
	
	public static final String EMAIL_NOT_UNIQUE = "email_not_unique";

	public static String resolveAuthError(AuthenticationException authEx) {
		try {
			RestResponse errorResponse;
			if (authEx instanceof UsernameNotFoundException) {
				errorResponse = new RestResponse(false, authEx.getMessage(), ErrorHandler.USER_NOT_FOUND);
			} else if (authEx instanceof BadCredentialsException) {
				errorResponse = new RestResponse(false, authEx.getMessage(), ErrorHandler.PASSWORD_INCORRECT);
			} else {
				errorResponse = new RestResponse(false, authEx.getMessage(), ErrorHandler.AUTH_FAILURE);
			}

			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(errorResponse);
		} catch (JsonProcessingException jsonEx) {
			logger.error(jsonEx.getMessage(), jsonEx);
		}
		return "Server Failure";

	}
}
