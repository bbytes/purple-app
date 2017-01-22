package com.bbytes.purple.service;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.atlassian.httpclient.api.Request.Builder;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.ProjectRolesRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.UserRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.ProjectRole;
import com.atlassian.jira.rest.client.api.domain.RoleActor;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.bbytes.purple.auth.jwt.TokenAuthenticationProvider;
import com.bbytes.purple.domain.Integration;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.TaskItem;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.enums.TaskState;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.exception.PurpleIntegrationException;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.StringUtils;

@Service
public class JiraIntegrationService {

	private static final Logger logger = LoggerFactory.getLogger(JiraIntegrationService.class);

	private static final String JIRA_TASK = " jira ";

	private static final String TYPE_ATLASSIAN_USER_ROLE = "atlassian-user-role-actor";

	private static final String TYPE_ATLASSIAN_GROUP_ROLE = "atlassian-group-role-actor";

	final String template = GlobalConstants.EMAIL_INVITE_TEMPLATE;

	final List<String> statesToIgnore = new ArrayList<String>(Arrays.asList("done", "closed", "resolved"));

	final DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

	@Value("${base.url}")
	private String baseUrl;

	@Value("${email.invite.subject}")
	private String inviteSubject;

	@Autowired
	private TaskListService taskListService;

	@Autowired
	private TaskItemService taskItemService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordHashService passwordHashService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private TenantResolverService tenantResolverService;

	@Autowired
	protected TokenAuthenticationProvider tokenAuthenticationProvider;

	public JiraRestClient getJiraRestClient(final Integration integration) throws PurpleIntegrationException {
		try {
			final URI jiraServerUri = new URI(integration.getJiraBaseURL());
			final JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
			final JiraRestClient restClient = factory.create(jiraServerUri, new AuthenticationHandler() {

				@Override
				public void configure(Builder builder) {
					builder.setHeader(HttpHeaders.AUTHORIZATION, integration.getJiraBasicAuthHeader());

				}
			});

			return restClient;
		} catch (Exception e) {
			throw new PurpleIntegrationException(e);
		}

	}

	/**
	 * Sync jira projects from jira to statusnap
	 * 
	 * @param integration
	 * @param loggedInUser
	 * @throws PurpleIntegrationException
	 * @throws PurpleException
	 */
	public void syncJiraProjects(final Integration integration, User loggedInUser) throws PurpleIntegrationException, PurpleException {
		List<Project> jiraProjects = getJiraProjects(integration);
		List<String> jiraProjectList = new LinkedList<String>();
		List<String> finalProjectListToBeSaved = new LinkedList<String>();

		try {
			for (Project jiraProject : jiraProjects) {
				jiraProjectList.add(jiraProject.getProjectName());
			}
			List<Project> list = projectService.findAll();
			List<String> projectListFromDB = new ArrayList<String>();
			for (Project project : list) {
				projectListFromDB.add(project.getProjectName().toLowerCase());
			}

			for (String jiraProject : jiraProjectList) {
				// make sure we dont add project with same name but different
				// case Eg : ReCruiz and recruiz are same
				if (!projectListFromDB.contains(jiraProject.toLowerCase()))
					finalProjectListToBeSaved.add(jiraProject);
			}

			for (String project : finalProjectListToBeSaved) {
				Project addProject = new Project(project);
				addProject.setOrganization(loggedInUser.getOrganization());
				// added loggedIn user as owner of project
				addProject.setProjectOwner(loggedInUser);
				addProject = projectService.save(addProject);
			}
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.JIRA_CONNECTION_FAILED);
		}
	}

	/**
	 * Get all the projects from jira
	 * 
	 * @param integration
	 * @return
	 * @throws PurpleIntegrationException
	 */
	private List<Project> getJiraProjects(final Integration integration) throws PurpleIntegrationException {
		List<Project> jiraProjects = new ArrayList<Project>();
		if (integration == null)
			return jiraProjects;

		final JiraRestClient restClient = getJiraRestClient(integration);
		try {
			Promise<Iterable<BasicProject>> projects = restClient.getProjectClient().getAllProjects();
			for (BasicProject project : projects.claim()) {
				Project coreProject = new Project(project.getName());
				jiraProjects.add(coreProject);
			}

			return jiraProjects;
		} finally {
			if (restClient != null) {
				try {
					restClient.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Push update of task details like hours and state to jira from statusnap.
	 * This updates the tickets in jira
	 * 
	 * @param integration
	 * @param user
	 * @throws PurpleIntegrationException
	 */
	public void pushTaskUpdatesToJira(final Integration integration, User user) throws PurpleIntegrationException {
		if (integration == null || user == null)
			return;

		final JiraRestClient restClient = getJiraRestClient(integration);
		try {

			IssueRestClient issueRestClient = restClient.getIssueClient();

			List<TaskItem> taskItems = taskItemService.findByUsers(user);

			for (TaskItem taskItem : taskItems) {

				if (taskItem.getSpendHours() > 0) {
					logger.debug("Task Item name " + taskItem.getName());
					logger.debug("Spent hours " + taskItem.getSpendHours());
					logger.debug("Task state " + taskItem.getState());
					logger.debug("Task dirty " + taskItem.getDirty());
				}

				// sync only when dirty means the user has updated the hrs from
				// ui
				// and it is marked as dirty to sync to jira
				if (taskItem.getSpendHours() > 0 && taskItem.getDirty() && !TaskState.YET_TO_START.equals(taskItem.getState())) {
					final URI baseUri = UriBuilder.fromUri(integration.getJiraBaseURL()).path("/rest/api/latest").build();
					final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri).path("issue").path(taskItem.getJiraIssueKey())
							.path("worklog");

					Issue issue = issueRestClient.getIssue(taskItem.getJiraIssueKey()).claim();

					// check if the item is completed
					if (issue != null && TaskState.COMPLETED.equals(taskItem.getState())) {
						Iterable<Transition> transitions = issueRestClient.getTransitions(issue).claim();
						for (Transition transition : transitions) {
							if (statesToIgnore.contains(transition.getName().toLowerCase())) {
								// move ticket to close or done in jira for
								// statusnap completed state items
								issueRestClient.transition(issue, new TransitionInput(transition.getId()));
								break;
							}
						}
					}

					MultiValueMap<String, String> headers = new HttpHeaders();
					headers.add(HttpHeaders.AUTHORIZATION, integration.getJiraBasicAuthHeader());
					headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

					RestTemplate restTemplate = new RestTemplate();

					Integer timeSpendInSeconds = new Double(taskItem.getSpendHours() * 3600).intValue();
					String requestJson = " { \"timeSpentSeconds\" :  " + timeSpendInSeconds + "}";

					HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);

					try {
						restTemplate.postForEntity(uriBuilder.build().toURL().toURI(), entity, Void.class);
						taskItem.setDirty(false);
						taskItemService.save(taskItem);
					} catch (Exception e) {
						throw new PurpleIntegrationException(e);
					}
				}
			}
		} finally {
			if (restClient != null) {
				try {
					restClient.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

	}

	/**
	 * Sync jira issues to projects in statusnap side
	 * 
	 * @param integration
	 * @throws PurpleIntegrationException
	 */
	public void updateProjectWithJiraTask(Integration integration) throws PurpleIntegrationException {
		if (integration == null)
			return;

		Map<String, Map<String, List<Issue>>> projectToIssueListMap = getJiraProjectWithIssueTypeToIssueList(integration);
		for (String projectName : projectToIssueListMap.keySet()) {
			System.out.println("---------------------" + projectName + "-------------------");
			Project projectFromDb = projectService.findByProjectName(projectName);
			if (projectFromDb != null) {
				Map<String, List<Issue>> issueTypeToIssueList = projectToIssueListMap.get(projectName);

				for (String issueType : issueTypeToIssueList.keySet()) {
					String taskListName = projectFromDb.getProjectName() + JIRA_TASK + issueType.toLowerCase();
					List<Issue> issues = issueTypeToIssueList.get(issueType);
					for (Issue issue : issues) {
						taskListService.addJiraIssueToTaskList(taskListName, projectFromDb, issue);
					}
				}
			}
		}

		System.out.println("Done syncing project jira task items");
	}

	private Map<String, Map<String, List<Issue>>> getJiraProjectWithIssueTypeToIssueList(Integration integration)
			throws PurpleIntegrationException {
		Map<String, Map<String, List<Issue>>> projectNameToIssueList = new LinkedHashMap<String, Map<String, List<Issue>>>();

		if (integration == null)
			return projectNameToIssueList;

		final JiraRestClient restClient = getJiraRestClient(integration);

		try {
			SearchRestClient searchRestClient = restClient.getSearchClient();

			try {

				Promise<Iterable<BasicProject>> projects = restClient.getProjectClient().getAllProjects();
				for (BasicProject project : projects.claim()) {
					Map<String, List<Issue>> issueTypeToIssueList = new HashMap<>();
					SearchResult issueResult = searchRestClient.searchJql("project=" + project.getKey(), 1000, 0, null).claim();
					for (Issue issue : issueResult.getIssues()) {
						if (statesToIgnore.contains(issue.getStatus().getName().toLowerCase())) {
							continue;
						}
						List<Issue> issueList = issueTypeToIssueList.get(issue.getIssueType().getName());
						if (issueList == null) {
							issueList = new LinkedList<Issue>();
							issueTypeToIssueList.put(issue.getIssueType().getName(), issueList);
						}
						issueList.add(issue);
					}

					projectNameToIssueList.put(project.getName(), issueTypeToIssueList);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

			return projectNameToIssueList;
		} finally {
			if (restClient != null) {
				try {
					restClient.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

	}

	/**
	 * Sync project and jira user in statusnap side. This adds new users if not
	 * there and sends an invite to join the app
	 * 
	 * @param user
	 * @param integration
	 * @throws PurpleIntegrationException
	 * @throws PurpleException
	 */
	public void syncProjectToJiraUser(Integration integration, User user) throws PurpleIntegrationException, PurpleException {
		Map<String, List<User>> projectToUsersMap = getJiraProjectWithUserList(integration);
		// iterating project to users map
		for (Map.Entry<String, List<User>> entry : projectToUsersMap.entrySet()) {
			// checking project from JIRA is present in db
			Project projectFromDb = projectService.findByProjectName(entry.getKey());
			if (projectFromDb != null) {
				// looping all user of project
				for (User jiraUser : entry.getValue()) {
					User userFromDB = userService.getUserByEmail(jiraUser.getEmail());
					if (userFromDB != null) {
						// fetching user from db and adding to project
						projectFromDb.addUser(userFromDB);
						projectService.save(projectFromDb);
					} else {

						// creating random generated password string
						String generatePassword = StringUtils.nextSessionId();

						// saving jira user to statusnap user list
						Organization org = user.getOrganization();
						jiraUser.setOrganization(org);
						jiraUser.setPassword(passwordHashService.encodePassword(generatePassword));
						jiraUser.setStatus(User.PENDING);
						jiraUser.setTimePreference(User.DEFAULT_EMAIL_REMINDER_TIME);
						if (!tenantResolverService.emailExist(jiraUser.getEmail())) {
							User savedUser = userService.addUsers(jiraUser);
							// after saving user to db, this user is getting
							// added to project
							projectFromDb.addUser(savedUser);
							projectService.save(projectFromDb);

							// since user is getting created, sending
							// invitation
							// email to activate account
							final String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(savedUser.getEmail(), 720);
							String postDate = dateFormat.format(new Date());
							List<String> emailList = new ArrayList<String>();
							emailList.add(savedUser.getEmail());

							Map<String, Object> emailBody = new HashMap<>();
							emailBody.put(GlobalConstants.USER_NAME, savedUser.getName());
							emailBody.put(GlobalConstants.SUBSCRIPTION_DATE, postDate);
							emailBody.put(GlobalConstants.PASSWORD, generatePassword);
							emailBody.put(GlobalConstants.ACTIVATION_LINK, baseUrl + GlobalConstants.TOKEN_URL + xauthToken);

							notificationService.sendTemplateEmail(emailList, inviteSubject, template, emailBody);
						}
					}

				}
			}
		}
	}

	private Map<String, List<User>> getJiraProjectWithUserList(Integration integration) throws PurpleIntegrationException {
		Map<String, List<User>> projectNameToUserList = new LinkedHashMap<String, List<User>>();

		if (integration == null)
			return projectNameToUserList;

		JiraRestClient restClient = getJiraRestClient(integration);

		try {

			ProjectRolesRestClient projectRolesRestClient = restClient.getProjectRolesRestClient();
			UserRestClient userClient = restClient.getUserClient();

			Promise<Iterable<BasicProject>> projects = restClient.getProjectClient().getAllProjects();

			for (BasicProject project : projects.claim()) {
				List<User> projectUserList = new LinkedList<User>();
				Promise<Iterable<ProjectRole>> projectRoles = projectRolesRestClient.getRoles(project.getSelf());
				for (ProjectRole projectRole : projectRoles.claim()) {
					for (RoleActor roleActor : projectRole.getActors()) {
						if (TYPE_ATLASSIAN_USER_ROLE.equals(roleActor.getType())) {
							com.atlassian.jira.rest.client.api.domain.User jiraUser = null;
							try {
								jiraUser = userClient.getUser(roleActor.getName()).claim();
							} catch (RestClientException e) {
								// ignore : The user does not exist exception
							}
							if (jiraUser != null) {
								User user = new User(jiraUser.getDisplayName(), jiraUser.getEmailAddress().toLowerCase());
								projectUserList.add(user);
							}

						} else if (TYPE_ATLASSIAN_GROUP_ROLE.equals(roleActor.getType())) {
							projectUserList = getUserForJiraGroup(integration, roleActor.getName());
						}
					}
				}

				projectNameToUserList.put(project.getName(), projectUserList);
			}

			return projectNameToUserList;

		} finally {
			if (restClient != null) {
				try {
					restClient.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

	}

	private List<User> getUserForJiraGroup(Integration integration, String groupName) {
		List<User> userList = new ArrayList<User>();

		final URI baseUri = UriBuilder.fromUri(integration.getJiraBaseURL()).path("/rest/api/latest").build();
		final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri).path("group");
		uriBuilder.queryParam("groupname", groupName);
		uriBuilder.queryParam("expand", "users");
		try {

			MultiValueMap<String, String> headers = new HttpHeaders();
			headers.add(HttpHeaders.AUTHORIZATION, integration.getJiraBasicAuthHeader());

			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> request = new HttpEntity<String>(headers);
			ResponseEntity<Map> response = restTemplate.exchange(uriBuilder.build().toURL().toURI(), HttpMethod.GET, request, Map.class);
			Map<String, Map<String, Object>> map = response.getBody();

			if (map != null) {
				Map<String, Object> userMap = map.get("users");
				if (userMap != null) {
					List<Map<String, Object>> userMapList = (List<Map<String, Object>>) userMap.get("items");

					if (userList != null) {
						for (Map<String, Object> userData : userMapList) {
							if ("true".equals(userData.get("active").toString())) {
								User user = new User(userData.get("displayName").toString(), userData.get("emailAddress").toString());
								userList.add(user);
							}

						}
					}
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return userList;
	}

}
