package com.bbytes.purple.web.controller;

import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.domain.Integration;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.exception.PurpleIntegrationException;
import com.bbytes.purple.rest.dto.models.IntegrationRequestDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.IntegrationService;
import com.bbytes.purple.service.JiraIntegrationService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.ConnectioUtil;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.SuccessHandler;

/**
 * Integration Controller
 * 
 * @author akshay
 *
 */
@RestController
public class IntegrationController {

	private static final Logger logger = LoggerFactory.getLogger(IntegrationController.class);

	private static final String BASIC = "Basic ";

	@Autowired
	private UserService userService;

	@Autowired
	private IntegrationService integrationService;

	@Autowired
	private JiraIntegrationService jiraIntegrationService;

	@Value("${ssl.cert.validation.disable}")
	private String disableCertificateValidation;

	@RequestMapping(value = "/api/v1/integration/jira/addAuthentication", method = RequestMethod.POST)
	public RestResponse connectToJIRA(@RequestBody IntegrationRequestDTO integrationRequestDTO) throws PurpleException {

		User loggedInUser = userService.getLoggedInUser();
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

			HttpClient client = null;

			if (disableCertificateValidation != null && Boolean.valueOf(disableCertificateValidation)) {
				client = ConnectioUtil.getSeftSSLTrustHttpClient();
			} else {
				client = ConnectioUtil.getDefaultHttpClient();
			}

			HttpResponse response = client.execute(request);
			statusCode = response.getStatusLine().getStatusCode();

		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw new PurpleException("Failed : HTTP Connection ", ErrorHandler.BAD_GATEWAY);
		}
		if (statusCode != 200) {
			if (statusCode == 502)
				throw new PurpleException("Failed : HTTP Connection ", ErrorHandler.BAD_GATEWAY);
			else if (statusCode == 401)
				throw new PurpleException("Failed : HTTP Connection : ", ErrorHandler.AUTH_FAILURE);
		} else {
			integrationService.connectToJIRA(loggedInUser, jiraUsername, authHeader, jiraBaseURL);
		}

		logger.debug("User with email  '" + jiraUsername + "' is connected to JIRA successfully");
		RestResponse response = new RestResponse(RestResponse.SUCCESS, JIRA_CONNECTION_MSG,
				SuccessHandler.JIRA_CONNECTION_SUCCESS);

		return response;
	}

	@RequestMapping(value = "/api/v1/integration/jira/getAuthentication", method = RequestMethod.GET)
	public RestResponse getJIRAConnection() throws PurpleException {

		User loggedInUser = userService.getLoggedInUser();
		int statusCode;
		final String JIRA_CONNECTION_MSG = "jira is connected successfully";
		RestResponse jiraRestResponse;
		HttpClient client = null;

		try {

			Integration integration = integrationService.getIntegrationForUser(loggedInUser);
			if (integration == null) {
				jiraRestResponse = new RestResponse(RestResponse.FAILED, "Failed : HTTP Connection : ",
						ErrorHandler.JIRA_CONNECTION_FAILED);

				return jiraRestResponse;
			}

			String basicAuthHeader = integration.getJiraBasicAuthHeader();
			if (basicAuthHeader.isEmpty()) {
				logger.debug("Jira basic auth exception ");
				jiraRestResponse = new RestResponse(RestResponse.FAILED, "Failed : HTTP Connection : ",
						ErrorHandler.JIRA_CONNECTION_FAILED);
				return jiraRestResponse;
			}

			HttpGet request = new HttpGet(integration.getJiraBaseURL());
			request.setHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader);

			if (disableCertificateValidation != null && Boolean.valueOf(disableCertificateValidation)) {
				client = ConnectioUtil.getSeftSSLTrustHttpClient();
			} else {
				client = ConnectioUtil.getDefaultHttpClient();
			}

			HttpResponse response = client.execute(request);
			statusCode = response.getStatusLine().getStatusCode();
		} catch (Throwable e) {
			logger.debug(e.getMessage(), e);
			throw new PurpleException("Failed : HTTP Connection ", ErrorHandler.JIRA_CONNECTION_FAILED);
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

	@RequestMapping(value = "/api/v1/integration/jira/syncProjects", method = RequestMethod.GET)
	public RestResponse syncJIRAProjects() throws PurpleException {

		final String JIRA_ADD_PROJECT_MSG = "Jira projects are added successfully";
		User loggedInUser = userService.getLoggedInUser();
		try {
			Integration integration = integrationService.getIntegrationForUser(loggedInUser);
			jiraIntegrationService.syncJiraProjects(integration, loggedInUser);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("Jira Projects are sync successfully");
		RestResponse response = new RestResponse(RestResponse.SUCCESS, JIRA_ADD_PROJECT_MSG,
				SuccessHandler.JIRA_SYNC_PROJECTS_SUCCESS);

		return response;
	}

	@RequestMapping(value = "/api/v1/integration/jira/syncTask", method = RequestMethod.GET)
	public RestResponse syncJIRATask() throws PurpleException, InterruptedException, ExecutionException {
		final String JIRA_ADD_TASK_MSG = "Jira project to jira task sync successful";
		RestResponse response = null;

		User loggedInUser = userService.getLoggedInUser();
		jiraIntegrationService.syncJIRATasks(loggedInUser);

		logger.debug("Jira Projects to task sync done successfully");
		response = new RestResponse(RestResponse.SUCCESS, JIRA_ADD_TASK_MSG,
				SuccessHandler.JIRA_SYNC_PROJECTS_AND_USERS_SUCCESS);

		return response;
	}

	@RequestMapping(value = "/api/v1/integration/jira/syncUsers", method = RequestMethod.GET)
	public RestResponse syncJIRAUsers()
			throws PurpleException, PurpleIntegrationException, InterruptedException, ExecutionException {

		final String JIRA_ADD_PROJECT_MSG = "Jira project to users are sync successfully";
		RestResponse response = null;

		User loggedInUser = userService.getLoggedInUser();
		jiraIntegrationService.syncJIRAUsers(loggedInUser);

		logger.debug("Jira Projects to users are sync successfully");
		response = new RestResponse(RestResponse.SUCCESS, JIRA_ADD_PROJECT_MSG,
				SuccessHandler.JIRA_SYNC_PROJECTS_AND_USERS_SUCCESS);

		return response;
	}

	@RequestMapping(value = "/api/v1/integration/slack/name", method = RequestMethod.GET)
	public RestResponse getSlackConnection() throws PurpleException {
		String slackUserName = integrationService.getSlackUserName();
		RestResponse response;
		if (slackUserName == null) {
			response = new RestResponse(RestResponse.FAILED, "Slack not connected", ErrorHandler.NOT_CONNECTED);
			return response;
		}
		response = new RestResponse(RestResponse.SUCCESS, slackUserName);
		return response;
	}

	@RequestMapping(value = "/api/v1/integration/slack/name", method = RequestMethod.PUT)
	public RestResponse saveSlackConnection(@RequestParam("slackUserName") String slackUserName)
			throws PurpleException {

		RestResponse response;
		if (slackUserName == null || slackUserName.isEmpty()) {
			response = new RestResponse(RestResponse.FAILED, "Slack not connected", ErrorHandler.NOT_CONNECTED);
			return response;
		}
		integrationService.saveSlackUserName(slackUserName);

		response = new RestResponse(RestResponse.SUCCESS, slackUserName);
		return response;
	}

	// @RequestMapping(value = "/api/v1/integration/slack/channel/{channelId}",
	// method = RequestMethod.POST)
	// public RestResponse setSlackChannels(@PathVariable("channelId") String
	// channelId) throws PurpleException {
	// integrationService.setSlackChannel(channelId);
	// RestResponse response = new RestResponse(RestResponse.SUCCESS, "Slack
	// Channel updated successfully");
	// return response;
	// }

	@RequestMapping(value = "/api/v1/integration/slack", method = RequestMethod.DELETE)
	public RestResponse deleteSlackIntegration() throws PurpleException {
		integrationService.deleteSlackConnection();
		integrationService.clearSlackUserName();
		RestResponse response = new RestResponse(RestResponse.SUCCESS, "Slack connection deleted successfully");
		return response;
	}

	@RequestMapping(value = "/api/v1/integration/jira", method = RequestMethod.DELETE)
	public RestResponse deleteJiraIntegration() throws PurpleException {
		User user = userService.getLoggedInUser();
		Integration integration = integrationService.getIntegrationForUser(user);
		if (integration != null) {
			integration.setJiraBaseURL(null);
			integration.setJiraBasicAuthHeader(null);
			integration.setJiraUserName(null);
			integrationService.save(integration);
		}

		RestResponse response = new RestResponse(RestResponse.SUCCESS, "Slack connection deleted successfully");
		return response;
	}

	@RequestMapping(value = "/api/v1/integration/github", method = RequestMethod.DELETE)
	public RestResponse deleteGithubIntegration() throws PurpleException {
		integrationService.deleteGithubConnection();
		RestResponse response = new RestResponse(RestResponse.SUCCESS, "Github connection deleted successfully");
		return response;
	}

	@RequestMapping(value = "/api/v1/integration/bitbucket", method = RequestMethod.DELETE)
	public RestResponse deleteBitBucketIntegration() throws PurpleException {
		integrationService.deleteBitbucketConnection();
		RestResponse response = new RestResponse(RestResponse.SUCCESS, "Bitbucket connection deleted successfully");
		return response;
	}

	@RequestMapping(value = "/api/v1/integration/hipchat", method = RequestMethod.DELETE)
	public RestResponse deleteHipchatIntegration() throws PurpleException {
		integrationService.deleteHipChatConnection();
		RestResponse response = new RestResponse(RestResponse.SUCCESS, "Hipchat connection deleted successfully");
		return response;
	}

}
