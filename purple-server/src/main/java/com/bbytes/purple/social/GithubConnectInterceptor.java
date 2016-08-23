package com.bbytes.purple.social;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.github.api.GitHub;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

public class GithubConnectInterceptor implements ConnectInterceptor<GitHub> {

	@Override
	public void preConnect(ConnectionFactory<GitHub> connectionFactory, MultiValueMap<String, String> parameters,
			WebRequest request) {
		System.out.println(connectionFactory.getProviderId());
		
	}

	@Override
	public void postConnect(Connection<GitHub> connection, WebRequest request) {
		System.out.println(connection.getDisplayName());
		
		if (connection.getApi() instanceof GitHub) {
			GitHub gitHubapi = (GitHub) connection;
			System.out.println(gitHubapi.userOperations().getUserProfile().getLocation());
		}
	}

}
