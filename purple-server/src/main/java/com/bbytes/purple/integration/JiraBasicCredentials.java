package com.bbytes.purple.integration;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;

/**
 * Basic HTTP authentication credentials.
 */
public class JiraBasicCredentials {

	private String basicAuthHeader;

	private String userName;

	/**
	 * Creates new basic HTTP credentials.
	 *
	 * @param username
	 * @param password
	 */
	public JiraBasicCredentials(String userName, String basicAuthHeader) {
		this.basicAuthHeader = basicAuthHeader;
		this.userName = userName;
	}

	/**
	 * Sets the Authorization header for the given request.
	 *
	 * @param req
	 *            HTTP request to authenticate
	 */
	public void authenticate(HttpRequest req) {
		req.setHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader);
	}

	public String getLogonName() {
		return userName;
	}

}
