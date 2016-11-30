package com.bbytes.purple.integration;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;

import net.rcarz.jiraclient.ICredentials;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.RestClient;

/**
 * Basic HTTP authentication credentials.
 */
public class JiraBasicCredentials implements ICredentials {

	private String basicAuthHeader;
	
	private String userName;

	/**
	 * Creates new basic HTTP credentials.
	 *
	 * @param username
	 * @param password
	 */
	public JiraBasicCredentials(String userName,String basicAuthHeader) {
		this.basicAuthHeader = basicAuthHeader;
		this.userName =userName;
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

	@Override
	public String getLogonName() {
		return userName;
	}

	@Override
	public void initialize(RestClient client) throws JiraException {
		
	}

	@Override
	public void logout(RestClient client) throws JiraException {
		
	}

}
