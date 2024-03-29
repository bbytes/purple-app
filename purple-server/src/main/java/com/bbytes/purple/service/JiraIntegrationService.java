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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
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
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
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
import com.bbytes.purple.exception.PurpleNoResultException;
import com.bbytes.purple.scheduler.SyncJiraProjectAndUserAndIssueExecutor;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.JiraHttpClientFactory;
import com.bbytes.purple.utils.RestTemplateUtil;
import com.bbytes.purple.utils.StringUtils;

@Service
public class JiraIntegrationService {

	private static final Logger logger = LoggerFactory.getLogger(JiraIntegrationService.class);

	private static final String JIRA_TASK = " jira ";

	private static final String TYPE_ATLASSIAN_USER_ROLE = "atlassian-user-role-actor";

	private static final String TYPE_ATLASSIAN_GROUP_ROLE = "atlassian-group-role-actor";

	final String template = GlobalConstants.EMAIL_INVITE_TEMPLATE;

	final static String SYNC_JIRA_USER_SUBJECT = "Statusnap - JIRA sync users";

	final static String SYNC_JIRA_TASK_SUBJECT = "Statusnap - JIRA sync tasks";

	final List<String> statesToIgnore = new ArrayList<String>(Arrays.asList("done", "closed", "resolved"));

	final DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

	@Value("${base.url}")
	private String baseUrl;

	@Value("${ssl.cert.validation.disable}")
	private String disableCertificateValidation;

	@Value("${email.invite.subject}")
	private String inviteSubject;

	@Value("${jira.issue.query.page.size}")
	private String jiraIssueQueryPageSize;

	@Autowired
	private TaskListService taskListService;

	@Autowired
	private TaskItemService taskItemService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserService userService;

	@Autowired
	private IntegrationService integrationService;

	@Autowired
	private PasswordHashService passwordHashService;

	@Autowired
	private TenantResolverService tenantResolverService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	protected TokenAuthenticationProvider tokenAuthenticationProvider;

	@Autowired
	protected SpringProfileService springProfileService;

	private ExecutorService executorService;

	@PostConstruct
	private void init() {
		executorService = Executors.newSingleThreadExecutor();
	}

	public JiraRestClient getJiraRestClient(final Integration integration) throws PurpleIntegrationException {
		try {

			final URI jiraServerUri = new URI(integration.getJiraBaseURL());
			final JiraHttpClientFactory factory = new JiraHttpClientFactory();

			final DisposableHttpClient httpClient = factory.createClient(jiraServerUri, new AuthenticationHandler() {

				@Override
				public void configure(Builder builder) {
					builder.setHeader(HttpHeaders.AUTHORIZATION, integration.getJiraBasicAuthHeader());

				}
			});

			JiraRestClient restClient = new AsynchronousJiraRestClient(jiraServerUri, httpClient);
			return restClient;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new PurpleIntegrationException(e);
		}

	}

	public void syncJiraProjectsAndUserAndIssues(final Integration integration, User loggedInUser)
			throws PurpleIntegrationException, PurpleException {
		List<Project> jiraProjects = getJiraProjects(integration);
		for (Project projectFromJira : jiraProjects) {
			Project projectFromDB = projectService.findByProjectKey(projectFromJira.getProjectKey());
			// not there in db
			if (projectFromDB == null) {
				projectFromDB = projectService.save(projectFromJira);
			}

			final JiraRestClient restClient = getJiraRestClient(integration);
			try {
				SearchRestClient searchRestClient = restClient.getSearchClient();

				int startAt = 0;
				int pageSizeChuck = 20;
				Promise<com.atlassian.jira.rest.client.api.domain.Project> jiraProjectPromise = restClient.getProjectClient()
						.getProject(projectFromDB.getProjectKey());
				com.atlassian.jira.rest.client.api.domain.Project jiraProject = jiraProjectPromise.claim();

				logger.debug("Task Sync :- Current jira project  : " + jiraProject.getName());
				// loop 50 time to cover totally 20 * 50 = 1000 issues form jira
				for (int i = 0; i < 50; i++) {

					Map<String, Map<String, List<Issue>>> projectNameToIssueList = new LinkedHashMap<String, Map<String, List<Issue>>>();
					Map<String, List<Issue>> issueTypeToIssueList;
					try {
						issueTypeToIssueList = getIssueListForProject(jiraProject, startAt, pageSizeChuck, searchRestClient);
					} catch (PurpleNoResultException e) {
						break;
					}
					projectNameToIssueList.put(jiraProject.getName(), issueTypeToIssueList);

					logger.debug("Task Sync :- Current statusnap project  : " + projectFromDB.getProjectName());

					for (String issueType : issueTypeToIssueList.keySet()) {
						String taskListName = projectFromDB.getProjectName() + JIRA_TASK + issueType.toLowerCase();
						List<Issue> issues = issueTypeToIssueList.get(issueType);
						for (Issue issue : issues) {
							taskListService.addJiraIssueToTaskList(taskListName, projectFromDB, issue,loggedInUser);
						}
					}

					startAt = startAt + pageSizeChuck;
					logger.debug("Task Sync :- Current task sync page chuck start number : " + startAt);
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
		Map<String, String> jiraProjectKeyToName = new HashMap<String, String>();
		Map<String, String> finaljiraProjectKeyToNameToBeSaved = new HashMap<String, String>();

		try {
			for (Project jiraProject : jiraProjects) {
				jiraProjectKeyToName.put(jiraProject.getProjectKey(), jiraProject.getProjectName());
			}
			List<Project> list = projectService.findAll();
			List<String> projectListFromDB = new ArrayList<String>();
			for (Project project : list) {
				projectListFromDB.add(project.getProjectName().toLowerCase());
			}

			for (String jiraProjectKey : jiraProjectKeyToName.keySet()) {
				// make sure we dont add project with same name but different
				// case Eg : ReCruiz and recruiz are same
				if (!projectListFromDB.contains(jiraProjectKeyToName.get(jiraProjectKey).toLowerCase()))
					finaljiraProjectKeyToNameToBeSaved.put(jiraProjectKey, jiraProjectKeyToName.get(jiraProjectKey));
			}

			for (String projectKey : finaljiraProjectKeyToNameToBeSaved.keySet()) {
				Project addProject = new Project(finaljiraProjectKeyToNameToBeSaved.get(projectKey), projectKey);
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
				Project coreProject = new Project(project.getName().toLowerCase(), project.getKey());
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
				if (taskItem.getSpendHours() > 0
						&& taskItem.getDirty() /*
												 * && !TaskState.YET_TO_START.
												 * equals(taskItem.getState())
												 */) {
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
						logger.error(e.getMessage(), e);
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
	public void updateProjectWithJiraTask(Integration integration, User loggedInUser) throws PurpleIntegrationException {
		if (integration == null)
			return;

		final JiraRestClient restClient = getJiraRestClient(integration);
		try {
			SearchRestClient searchRestClient = restClient.getSearchClient();

			int startAt = 0;
			int pageSizeChuck = 20;
			Promise<Iterable<BasicProject>> projects = restClient.getProjectClient().getAllProjects();
			for (BasicProject project : projects.claim()) {
				logger.debug("Task Sync :- Current jira project  : " + project.getName());
				// loop 50 time to cover totally 20 * 50 = 1000 issues form jira
				for (int i = 0; i < 50; i++) {

					Map<String, Map<String, List<Issue>>> projectNameToIssueList = new LinkedHashMap<String, Map<String, List<Issue>>>();
					Map<String, List<Issue>> issueTypeToIssueList;
					try {
						issueTypeToIssueList = getIssueListForProject(project, startAt, pageSizeChuck, searchRestClient);
					} catch (PurpleNoResultException e) {
						break;
					}
					projectNameToIssueList.put(project.getName(), issueTypeToIssueList);

					Project projectFromDb = projectService.findByProjectName(project.getName());
					logger.debug("Task Sync :- Current statusnap project  : " + projectFromDb.getProjectName());
					if (projectFromDb != null) {
						for (String issueType : issueTypeToIssueList.keySet()) {
							String taskListName = projectFromDb.getProjectName() + JIRA_TASK + issueType.toLowerCase();
							List<Issue> issues = issueTypeToIssueList.get(issueType);
							for (Issue issue : issues) {
								taskListService.addJiraIssueToTaskList(taskListName, projectFromDb, issue,loggedInUser);
							}
						}
					}

					startAt = startAt + pageSizeChuck;
					logger.debug("Task Sync :- Current task sync page chuck start number : " + startAt);
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
	 * Get issue type to issue list for given project
	 * 
	 * @param project
	 * @param startAt
	 *            pag start point
	 * @param pageSize
	 *            page size of pageable
	 * @param searchRestClient
	 * @return
	 * @throws PurpleNoResultException
	 */
	private Map<String, List<Issue>> getIssueListForProject(BasicProject project, int startAt, int pageSize,
			SearchRestClient searchRestClient) throws PurpleNoResultException {
		Map<String, List<Issue>> issueTypeToIssueList = new HashMap<>();

		SearchResult issueResult = searchRestClient.searchJql("project=" + project.getKey(), pageSize, startAt, null).claim();
		if (!issueResult.getIssues().iterator().hasNext())
			throw new PurpleNoResultException();

		for (Issue issue : issueResult.getIssues()) {
			logger.debug("Current issue no   : " + issue.getKey());
			if (statesToIgnore.contains(issue.getStatus().getName().toLowerCase())) {
				logger.debug("Issue ignored because of status : " + issue.getStatus().getName());
				continue;
			}

			logger.debug("Current issue added to statusnap   : " + issue.getKey());
			List<Issue> issueList = issueTypeToIssueList.get(issue.getIssueType().getName());
			if (issueList == null) {
				issueList = new LinkedList<Issue>();
				issueTypeToIssueList.put(issue.getIssueType().getName(), issueList);
			}
			issueList.add(issue);
		}

		return issueTypeToIssueList;
	}

	/**
	 * This method is used to send JIRA sync users job and send email for
	 * success or failure
	 * 
	 * @param loggedInUser
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void syncJIRAUsers(User loggedInUser) throws InterruptedException, ExecutionException {

//		List<String> emailList = new ArrayList<String>();
//		emailList.add(loggedInUser.getEmail());
//
//		Map<String, Object> emailBody = new HashMap<>();
//		emailBody.put(GlobalConstants.USER_NAME, loggedInUser.getName());
//		emailBody.put(GlobalConstants.CURRENT_DATE, dateFormat.format(new Date()));
//		emailBody.put(GlobalConstants.STRING_TEXT, "users");
//
//		// sending job to executor
//		if (loggedInUser != null)
//			executorService.execute(new SyncUserJobExecutor(loggedInUser, integrationService, this, emailBody, emailList,
//					notificationService, springProfileService));
	}

	/**
	 * This method is used to send JIRA sync tasks job and send email for
	 * success or failure
	 * 
	 * @param loggedInUser
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void syncJIRATasks(User loggedInUser) throws InterruptedException, ExecutionException {

		List<String> emailList = new ArrayList<String>();
		emailList.add(loggedInUser.getEmail());

		Map<String, Object> emailBody = new HashMap<>();
		emailBody.put(GlobalConstants.USER_NAME, loggedInUser.getName());
		emailBody.put(GlobalConstants.CURRENT_DATE, dateFormat.format(new Date()));
		emailBody.put(GlobalConstants.STRING_TEXT, "tasks");

		// sending job to executor
		if (loggedInUser != null)
			executorService.execute(new SyncJiraProjectAndUserAndIssueExecutor(loggedInUser, integrationService, this, emailBody, emailList,
					notificationService, springProfileService));
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
	public void syncProjectToJiraUser(Integration integration, User user) throws Exception {
		final JiraRestClient restClient = getJiraRestClient(integration);

		try {

			Promise<Iterable<BasicProject>> projects = restClient.getProjectClient().getAllProjects();

			for (BasicProject project : projects.claim()) {
				// checking project from JIRA is present in db
				Project projectFromDb = projectService.findByProjectKey(project.getKey());
				if (projectFromDb != null) {
					List<User> projectUserList = getJiraUserListForProject(integration, projectFromDb.getProjectKey());
					logger.debug("User sync :- Current project in loop : " + projectFromDb.getProjectName());
					// looping all user of project
					for (User jiraUser : projectUserList) {
						logger.debug(
								"User sync :- Jira user in loop, name and email : " + jiraUser.getName() + " , " + jiraUser.getEmail());
						User userFromDB = userService.getUserByEmail(jiraUser.getEmail().toLowerCase());
						if (userFromDB != null) {
							logger.debug("User sync :- Statusnap user :  " + userFromDB.getName() + " , " + userFromDB.getEmail());
							// fetching user from db and adding to project
							projectFromDb.addUser(userFromDB);
							projectService.save(projectFromDb);
						} else {
							logger.debug("User sync :- User not found so creating it in statusnap :  " + jiraUser.getName() + " , "
									+ jiraUser.getEmail());
							// creating random generated password string
							String generatePassword = StringUtils.nextSessionId();

							// saving jira user to statusnap user list
							Organization org = user.getOrganization();
							jiraUser.setOrganization(org);
							jiraUser.setPassword(passwordHashService.encodePassword(generatePassword));
							jiraUser.setStatus(User.PENDING);
							jiraUser.setTimePreference(User.DEFAULT_EMAIL_REMINDER_TIME);
							if (!tenantResolverService.emailExist(jiraUser.getEmail().toLowerCase())) {
								User savedUser = userService.addUsers(jiraUser);
								// after saving user to db, this user is getting
								// added to project
								projectFromDb.addUser(savedUser);
								projectService.save(projectFromDb);
							}
						}

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

	

	private List<User> getJiraUserListForProject(Integration integration, String projectKey) throws PurpleIntegrationException {
		List<User> projectUserList = new ArrayList<>();

		if (integration == null)
			return projectUserList;

		final JiraRestClient restClient = getJiraRestClient(integration);

		try {
			ProjectRolesRestClient projectRolesRestClient = restClient.getProjectRolesRestClient();
			UserRestClient userClient = restClient.getUserClient();
			BasicProject project = restClient.getProjectClient().getProject(projectKey).claim();

			if (project == null)
				return projectUserList;

			Promise<Iterable<ProjectRole>> projectRoles = projectRolesRestClient.getRoles(project.getSelf());
			for (ProjectRole projectRole : projectRoles.claim()) {
				for (RoleActor roleActor : projectRole.getActors()) {
					if (TYPE_ATLASSIAN_USER_ROLE.equals(roleActor.getType())) {
						com.atlassian.jira.rest.client.api.domain.User jiraUser = null;
						try {
							jiraUser = userClient.getUser(roleActor.getName()).claim();
						} catch (RestClientException e) {
							// ignore : The user does not exist
							// exception
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
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (restClient != null) {
				try {
					restClient.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		return projectUserList;

	}

	// private Map<String, List<User>> getJiraProjectWithUserList(Integration
	// integration, String projectKey)
	// throws PurpleIntegrationException {
	// Map<String, List<User>> projectNameToUserList = new LinkedHashMap<String,
	// List<User>>();
	//
	// if (integration == null)
	// return projectNameToUserList;
	//
	// JiraRestClient restClient = getJiraRestClient(integration);
	//
	// try {
	//
	// ProjectRolesRestClient projectRolesRestClient =
	// restClient.getProjectRolesRestClient();
	// UserRestClient userClient = restClient.getUserClient();
	//
	// Promise<Iterable<BasicProject>> projects =
	// restClient.getProjectClient().getAllProjects();
	// List<User> projectUserList = new LinkedList<User>();
	// for (BasicProject project : projects.claim()) {
	// try {
	// Promise<Iterable<ProjectRole>> projectRoles =
	// projectRolesRestClient.getRoles(project.getSelf());
	// for (ProjectRole projectRole : projectRoles.claim()) {
	// for (RoleActor roleActor : projectRole.getActors()) {
	// if (TYPE_ATLASSIAN_USER_ROLE.equals(roleActor.getType())) {
	// com.atlassian.jira.rest.client.api.domain.User jiraUser = null;
	// try {
	// jiraUser = userClient.getUser(roleActor.getName()).claim();
	// } catch (RestClientException e) {
	// // ignore : The user does not exist
	// // exception
	// }
	// if (jiraUser != null) {
	// User user = new User(jiraUser.getDisplayName(),
	// jiraUser.getEmailAddress().toLowerCase());
	// projectUserList.add(user);
	// }
	//
	// } else if (TYPE_ATLASSIAN_GROUP_ROLE.equals(roleActor.getType())) {
	// projectUserList = getUserForJiraGroup(integration, roleActor.getName());
	// }
	// }
	// }
	// } catch (Exception e) {
	// logger.error(e.getMessage(), e);
	// }
	//
	// projectNameToUserList.put(project.getName(), projectUserList);
	// }
	//
	// return projectNameToUserList;
	//
	// } finally {
	// if (restClient != null) {
	// try {
	// restClient.close();
	// } catch (IOException e) {
	// logger.error(e.getMessage(), e);
	// }
	// }
	// }
	//
	// }

	private List<User> getUserForJiraGroup(Integration integration, String groupName) {
		List<User> userList = new ArrayList<User>();

		final URI baseUri = UriBuilder.fromUri(integration.getJiraBaseURL()).path("/rest/api/latest").build();
		final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri).path("group");
		uriBuilder.queryParam("groupname", groupName);
		uriBuilder.queryParam("expand", "users");
		try {

			MultiValueMap<String, String> headers = new HttpHeaders();
			headers.add(HttpHeaders.AUTHORIZATION, integration.getJiraBasicAuthHeader());

			RestTemplate restTemplate = RestTemplateUtil.getSSLNoCheckRestTemplate();
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
								User user = new User(userData.get("displayName").toString(),
										userData.get("emailAddress").toString().toLowerCase());
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
