package com.bbytes.purple.web.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

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

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.bbytes.purple.domain.Integration;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.IntegrationRequestDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.IntegrationService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.GlobalConstants;
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
		String jiraBaseURL, authHeader;
		try {

			jiraBaseURL = integrationRequestDTO.getJiraBaseUrl();

			HttpGet request = new HttpGet(jiraBaseURL);
			String authString = integrationRequestDTO.getUserName() + ":" + integrationRequestDTO.getPassword();
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
			integrationService.connectToJIRA(user, authHeader, jiraBaseURL);
		}

		logger.debug(
				"User with email  '" + integrationRequestDTO.getUserName() + "' is connected to JIRA successfully");
		RestResponse response = new RestResponse(RestResponse.SUCCESS, JIRA_CONNECTION_MSG,
				SuccessHandler.JIRA_CONNECTION_SUCCESS);

		return response;
	}

	@RequestMapping(value = "/api/v1/integration/jira/getAuthentication", method = RequestMethod.GET)
	public RestResponse getJIRAConnection() throws PurpleException {

		User user = userService.getLoggedInUser();
		int statusCode;
		final String JIRA_CONNECTION_MSG = "jira is connected successfully";
		try {

			Integration integration = integrationService.getJIRAConnection(user);

			String basicAuthHeader = integration.getJiraBasicAuthHeader();
			if(basicAuthHeader == null)
				throw new PurpleException("Failed : HTTP Connection : ", ErrorHandler.AUTH_FAILURE);

			HttpGet request = new HttpGet(integration.getJiraBaseURL());
			request.setHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader);

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
		}

		logger.debug("User is connected to JIRA successfully");
		RestResponse response = new RestResponse(RestResponse.SUCCESS, JIRA_CONNECTION_MSG,
				SuccessHandler.JIRA_CONNECTION_SUCCESS);

		return response;
	}

	@RequestMapping(value = "/api/v1/integration/jira/getprojects", method = RequestMethod.GET)
	public RestResponse getJIRAProjects() throws PurpleException {

		final String JIRA_ADD_PROJECT_MSG = "jira projects are added successfully";
		User user = userService.getLoggedInUser();
		try {
			Integration integration = integrationService.getJIRAConnection(user);

			String jiraGetProjectsAPIURL = GlobalConstants.JIRA_GETPROJECTS_API_URL;
			String basicAuthHeader = integration.getJiraBasicAuthHeader();

			URL url = new URL(integration.getJiraBaseURL() + jiraGetProjectsAPIURL);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty(AUTHORIZATION, basicAuthHeader);
			int statusCode = urlConnection.getResponseCode();
			if (statusCode != 200) {
				if (statusCode == 401)
					throw new PurpleException("Failed : HTTP Connection : ", ErrorHandler.AUTH_FAILURE);
			}

			// Below Code is for JIRA REST Client -- Giving some issue
			
			/*URI jiraServerUri = URI.create(integration.getJiraBaseURL());

			AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
			JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, "username",
					"password");

			final int buildNumber = restClient.getMetadataClient().getServerInfo().claim().getBuildNumber(); 

			if (buildNumber >= 600) {
				final Iterable<BasicProject> allProjects = restClient.getProjectClient().getAllProjects().claim();
				for (BasicProject project : allProjects) {
					System.out.println(project);
				}
			}*/

			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

			StringBuilder sb = new StringBuilder();
			int cp;
			String jsonText = null;
			while ((cp = in.read()) != -1) {
				jsonText = sb.append((char) cp).toString();
			}

			integrationService.addJiraProjects(jsonText, user);

		} catch (Throwable e) {
			e.printStackTrace();
		}

		logger.debug("User is connected to JIRA successfully");
		RestResponse response = new RestResponse(RestResponse.SUCCESS, JIRA_ADD_PROJECT_MSG,
				SuccessHandler.JIRA_ADD_PROJECTS_SUCCESS);

		return response;
	}

}
