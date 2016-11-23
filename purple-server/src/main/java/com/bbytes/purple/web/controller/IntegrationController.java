package com.bbytes.purple.web.controller;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.auth.jwt.TokenAuthenticationProvider;
import com.bbytes.purple.domain.Integration;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.IntegrationRequestDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.IntegrationService;
import com.bbytes.purple.service.NotificationService;
import com.bbytes.purple.service.PasswordHashService;
import com.bbytes.purple.service.ProjectService;
import com.bbytes.purple.service.TenantResolverService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.StringUtils;
import com.bbytes.purple.utils.SuccessHandler;

import net.rcarz.jiraclient.Project;

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
	private ProjectService projectService;

	@Autowired
	private IntegrationService integrationService;

	@Autowired
	protected TokenAuthenticationProvider tokenAuthenticationProvider;

	@Autowired
	private PasswordHashService passwordHashService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private TenantResolverService tenantResolverService;

	@Value("${base.url}")
	private String baseUrl;

	@Value("${email.invite.subject}")
	private String inviteSubject;

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
		try {

			Integration integration = integrationService.getJIRAConnection(loggedInUser);
			if (integration == null) {
				jiraRestResponse = new RestResponse(RestResponse.FAILED, "Failed : HTTP Connection : ",
						ErrorHandler.JIRA_CONNECTION_FAILED);

				return jiraRestResponse;
			}

			String basicAuthHeader = integration.getJiraBasicAuthHeader();
			if (basicAuthHeader.isEmpty()) {
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
			Integration integration = integrationService.getJIRAConnection(loggedInUser);

			List<Project> jiraProjects = integrationService.syncJiraProjectWithUser(integration);

			integrationService.addJiraProjects(jiraProjects, loggedInUser);

		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("Jira Projects are sync successfully");
		RestResponse response = new RestResponse(RestResponse.SUCCESS, JIRA_ADD_PROJECT_MSG,
				SuccessHandler.JIRA_SYNC_PROJECTS_SUCCESS);

		return response;
	}

	@RequestMapping(value = "/api/v1/integration/jira/syncUsers", method = RequestMethod.GET)
	public RestResponse syncJIRAUsers() throws PurpleException {

		final String JIRA_ADD_PROJECT_MSG = "Jira project to users are sync successfully";
		final String template = GlobalConstants.EMAIL_INVITE_TEMPLATE;
		DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

		User loggedInUser = userService.getLoggedInUser();
		try {
			// checking jira is connected or not
			Integration integration = integrationService.getJIRAConnection(loggedInUser);

			Map<String, List<User>> projectToUsersMap = integrationService.getJiraProjectWithUserList(integration);
			// iterating project to users map
			for (Map.Entry<String, List<User>> entry : projectToUsersMap.entrySet()) {
				// checking project from JIRA is present in db
				if (projectService.findByProjectName(entry.getKey()) != null) {
					// looping all user of project
					for (User jiraUser : entry.getValue()) {
						User userFromDB = userService.getUserByEmail(jiraUser.getEmail());
						if (userFromDB != null) {
							// fetching user from db and adding to project
							com.bbytes.purple.domain.Project projectFromDb = projectService
									.findByProjectName(entry.getKey());
							projectFromDb.addUser(userFromDB);
							projectService.save(projectFromDb);
						} else {

							// creating random generated password string
							String generatePassword = StringUtils.nextSessionId();

							// saving jira user to statusnap user list
							Organization org = loggedInUser.getOrganization();
							jiraUser.setOrganization(org);
							jiraUser.setPassword(passwordHashService.encodePassword(generatePassword));
							jiraUser.setStatus(User.PENDING);
							jiraUser.setTimePreference(User.DEFAULT_EMAIL_REMINDER_TIME);
							if (!tenantResolverService.emailExist(jiraUser.getEmail())) {
								User savedUser = userService.addUsers(jiraUser);
								// after saving user to db, this user is getting
								// added to project
								com.bbytes.purple.domain.Project projectFromDb = projectService
										.findByProjectName(entry.getKey());
								projectFromDb.addUser(savedUser);
								projectService.save(projectFromDb);

								// since user is getting created, sending
								// invitation
								// email to activate account
								final String xauthToken = tokenAuthenticationProvider
										.getAuthTokenForUser(savedUser.getEmail(), 720);
								String postDate = dateFormat.format(new Date());
								List<String> emailList = new ArrayList<String>();
								emailList.add(savedUser.getEmail());

								Map<String, Object> emailBody = new HashMap<>();
								emailBody.put(GlobalConstants.USER_NAME, savedUser.getName());
								emailBody.put(GlobalConstants.SUBSCRIPTION_DATE, postDate);
								emailBody.put(GlobalConstants.PASSWORD, generatePassword);
								emailBody.put(GlobalConstants.ACTIVATION_LINK,
										baseUrl + GlobalConstants.TOKEN_URL + xauthToken);

								notificationService.sendTemplateEmail(emailList, inviteSubject, template, emailBody);
							}
						}

					}
				}
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}

		logger.debug("Jira Projects to users are sync successfully");
		RestResponse response = new RestResponse(RestResponse.SUCCESS, JIRA_ADD_PROJECT_MSG,
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
		RestResponse response = new RestResponse(RestResponse.SUCCESS, "Slack connection deleted successfully");
		return response;
	}

	@RequestMapping(value = "/api/v1/integration/jira", method = RequestMethod.DELETE)
	public RestResponse deleteJiraIntegration() throws PurpleException {
		User user = userService.getLoggedInUser();
		Integration integration = integrationService.getJIRAConnection(user);
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
