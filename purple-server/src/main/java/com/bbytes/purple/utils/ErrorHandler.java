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

	public static final String BAD_GATEWAY = "bad_gateway";

	public static final String ACCOUNT_ACTIVATION_FAILURE = "account_inactive";

	public static final String USER_NOT_FOUND = "user_not_found";

	public static final String PASSWORD_MISMATCH = "password_mistach";

	public static final String PROJECT_NOT_FOUND = "project_not_found";

	public static final String PROJECT_ALREADY_EXIST = "project_already_exist";

	public static final String STATUS_NOT_FOUND = "status_not_found";

	public static final String COMMENT_NOT_FOUND = "comment_not_found";

	public static final String REPLY_NOT_FOUND = "reply_not_found";

	public static final String NOTIFICATION_FAILED = "notification_failed";

	public static final String ADD_USER_FAILED = "add_user_failed";

	public static final String DELETE_USER_FAILED = "delete_user_failed";

	public static final String GET_USER_FAILED = "get_user_failed";

	public static final String ADD_PROJECT_FAILED = "add_project_failed";

	public static final String DELETE_PROJECT_FAILED = "delete_project_failed";

	public static final String GET_PROJECT_FAILED = "get_project_failed";

	public static final String UPDATE_PROJECT_FAILED = "update_project_failed";

	public static final String ADD_STATUS_FAILED = "add_status_failed";

	public static final String DELETE_STATUS_FAILED = "delete_status_failed";

	public static final String GET_STATUS_FAILED = "get_status_failed";

	public static final String GET_COMMENT_FAILED = "get_comment_failed";

	public static final String DELETE_COMMENT_FAILED = "delete_comment_failed";

	public static final String UPDATE_STATUS_FAILED = "update_status_failed";

	public static final String PASSWORD_INCORRECT = "bad_credentials";

	public static final String SIGN_UP_FAILED = "sign_up_failed";

	public static final String ORG_NOT_UNIQUE = "organization_not_unique";

	public static final String EMAIL_NOT_UNIQUE = "email_not_unique";

	public static final String SERVER_ERROR = "server_error";

	public static final String ADD_COMMENT_FAILED = "add_comment_failed";

	public static final String ADD_REPLY_FAILED = "add_reply_failed";

	public static final String UPDATE_REPLY_FAILED = "update_reply_failed";

	public static final String DELETE_REPLY_FAILED = "delete_reply_failed";

	public static final String UPDATE_COMMENT_FAILED = "update_comment_failed";

	public static final String UPDATE_SETTING_FAILED = "update_setting_failed";

	public static final String HOURS_EXCEEDED = "hours_exceeded";

	public static final String UPDATE_USERROLE_FAILED = "update_userrole_failed";

	public static final String PASS_DUEDATE_STATUS_EDIT = "pass_duedate_status_edit";

	public static final String FUTURE_DATE_STATUS_EDIT = "future_date_status_edit";

	public static final String JIRA_CONNECTION_FAILED = "jira_connection_failed";

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
