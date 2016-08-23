package com.bbytes.purple.social;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.github.api.GitHub;

public class SocialConnectionUserIdHelper implements ConnectionSignUp {

	@Override
	public String execute(Connection<?> connection) {
		if (connection.getApi() instanceof GitHub) {
			GitHub gitHubapi = (GitHub) connection.getApi();
			return gitHubapi.userOperations().getUserProfile().getEmail();
		}
		return null;
	}

}
