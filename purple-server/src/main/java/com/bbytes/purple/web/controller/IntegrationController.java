package com.bbytes.purple.web.controller;

import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.domain.Integration;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.integration.JiraBasicCredentials;
import com.bbytes.purple.rest.dto.models.IntegrationRequestDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.IntegrationService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.SuccessHandler;

import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.Project;
import net.rcarz.jiraclient.Role;
import net.rcarz.jiraclient.RoleActor;

/**
 * Integration Controller
 * 
 * @author akshay
 *
 */
@RestController
public class IntegrationController {

	private static final Logger logger = LoggerFactory.getLogger(IntegrationController.class);

	private static final String AUTHORIZATION = "Authorization";

	private static final String BASIC = "Basic ";

	@Autowired
	private UserService userService;

	@Autowired
	private IntegrationService integrationService;

	@RequestMapping(value = "/api/v1/integration/jira/addAuthentication", method = RequestMethod.POST)
	public RestResponse connectToJIRA(@RequestBody IntegrationRequestDTO integrationRequestDTO) throws PurpleException {

		User user = userService.getLoggedInUser();
		final String JIRA_CONNECTION_MSG = "jira is connected successfully";
		int statusCode;
		String jiraBaseURL, authHeader, jiraUsername;
		try {

			jiraBaseURL = integrationRequestDTO.getJiraBaseUrl();
			jiraUsername = integrationRequestDTO.getUserName();

			HttpGet request = new HttpGet(jiraBaseURL);
			String authString = jiraUsername + ":" + integrationRequestDTO.getPassword();
			byte[] encodedAuth = Base64.encodeBase64(authString.getBytes(Charset.forName("ISO-8859-1")));
			authHeader = BASIC + new String(encodedAuth);
			request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(request);

			statusCode = response.getStatusLine().getStatusCode();

		} catch (Throwable e) {
			throw new PurpleException("Failed : HTTP Connection ", ErrorHandler.BAD_GATEWAY);
		}
		if (statusCode != 200) {
			if (statusCode == 502)
				throw new PurpleException("Failed : HTTP Connection ", ErrorHandler.BAD_GATEWAY);
			else if (statusCode == 401)
				throw new PurpleException("Failed : HTTP Connection : ", ErrorHandler.AUTH_FAILURE);
		} else {
			integrationService.connectToJIRA(user, jiraUsername, authHeader, jiraBaseURL);
		}

		logger.debug("User with email  '" + jiraUsername + "' is connected to JIRA successfully");
		RestResponse response = new RestResponse(RestResponse.SUCCESS, JIRA_CONNECTION_MSG,
				SuccessHandler.JIRA_CONNECTION_SUCCESS);

		return response;
	}

	@RequestMapping(value = "/api/v1/integration/jira/getAuthentication", method = RequestMethod.GET)
	public RestResponse getJIRAConnection() throws PurpleException {

		User user = userService.getLoggedInUser();
		int statusCode;
		final String JIRA_CONNECTION_MSG = "jira is connected successfully";
		RestResponse jiraRestResponse;
		try {

			Integration integration = integrationService.getJIRAConnection(user);
			if (integration == null) {
				jiraRestResponse = new RestResponse(RestResponse.FAILED, "Failed : HTTP Connection : ",
						ErrorHandler.JIRA_CONNECTION_FAILED);

				return jiraRestResponse;
			}

			String basicAuthHeader = integration.getJiraBasicAuthHeader();
			if (basicAuthHeader.isEmpty() || basicAuthHeader == null) {
				jiraRestResponse = new RestResponse(RestResponse.FAILED, "Failed : HTTP Connection : ",
						ErrorHandler.JIRA_CONNECTION_FAILED);
				return jiraRestResponse;
			}

			HttpGet request = new HttpGet(integration.getJiraBaseURL());
			request.setHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader);

			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(request);

			statusCode = response.getStatusLine().getStatusCode();
		} catch (Throwable e) {
			throw new PurpleException("Failed : HTTP Connection ", ErrorHandler.BAD_GATEWAY);
		}

		if (statusCode != 200) {
			if (statusCode == 502) {
				jiraRestResponse = new RestResponse(RestResponse.FAILED, "Failed : HTTP Connection : ",
						ErrorHandler.BAD_GATEWAY);

				return jiraRestResponse;
			} else if (statusCode == 401) {
				jiraRestResponse = new RestResponse(RestResponse.FAILED, "Failed : HTTP Connection : ",
						ErrorHandler.AUTH_FAILURE);

				return jiraRestResponse;
			}
		}

		logger.debug("User is connected to JIRA successfully");
		jiraRestResponse = new RestResponse(RestResponse.SUCCESS, JIRA_CONNECTION_MSG,
				SuccessHandler.JIRA_CONNECTION_SUCCESS);

		return jiraRestResponse;
	}

	@RequestMapping(value = "/api/v1/integration/jira/getprojects", method = RequestMethod.GET)
	public RestResponse getJIRAProjects() throws PurpleException {

		final String JIRA_ADD_PROJECT_MSG = "jira projects are added successfully";
		User user = userService.getLoggedInUser();
		try {
			Integration integration = integrationService.getJIRAConnection(user);

			List<Project> jiraProjects = integrationService.syncJiraProjectWithUser(integration);

			integrationService.addJiraProjects(jiraProjects, user);

		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("User is connected to JIRA successfully");
		RestResponse response = new RestResponse(RestResponse.SUCCESS, JIRA_ADD_PROJECT_MSG,
				SuccessHandler.JIRA_ADD_PROJECTS_SUCCESS);

		return response;
	}

	@RequestMapping(value = "/api/v1/integration/jira/getUsers", method = RequestMethod.GET)
	public RestResponse getJIRAUsers() throws PurpleException {

		final String JIRA_ADD_PROJECT_MSG = "jira users are added successfully";
		User user = userService.getLoggedInUser();
		try {
			Integration integration = integrationService.getJIRAConnection(user);

			JiraBasicCredentials creds = new JiraBasicCredentials(integration.getJiraUserName(),
					integration.getJiraBasicAuthHeader());
			JiraClient jira = new JiraClient(integration.getJiraBaseURL(), creds);
			List<Project> jiraProjects = jira.getProjects();
			for (Project project : jiraProjects) {
				Project projectDetail = jira.getProject(project.getKey());
				for (String role : projectDetail.getRoles().keySet()) {
					System.out.println(projectDetail.getRoles().get(role));
				}
			}
			integrationService.addJiraProjects(jiraProjects, user);

		} catch (Throwable e) {
			e.printStackTrace();
		}

		logger.debug("User is connected to JIRA successfully");
		RestResponse response = new RestResponse(RestResponse.SUCCESS, JIRA_ADD_PROJECT_MSG,
				SuccessHandler.JIRA_ADD_PROJECTS_SUCCESS);

		return response;
	}

}
