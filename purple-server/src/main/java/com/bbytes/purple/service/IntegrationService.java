package com.bbytes.purple.service;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.bitbucket.api.BitBucket;
import org.springframework.social.bitbucket.api.BitBucketChangeset;
import org.springframework.social.bitbucket.api.BitBucketChangesets;
import org.springframework.social.bitbucket.api.BitBucketRepository;
import org.springframework.social.connect.Connection;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubCommit;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.social.slack.api.Slack;
import org.springframework.social.slack.api.impl.model.SlackChannel;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Integration;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.SocialConnection;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.integration.JiraBasicCredentials;
import com.bbytes.purple.repository.IntegrationRepository;
import com.bbytes.purple.repository.SocialConnectionRepository;
import com.bbytes.purple.social.MongoConnectionTransformers;
import com.bbytes.purple.utils.ErrorHandler;

import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.Role;
import net.rcarz.jiraclient.RoleActor;

@Service
public class IntegrationService extends AbstractService<Integration, String> {

	private static final Logger logger = LoggerFactory.getLogger(IntegrationService.class);

	private IntegrationRepository integrationRepository;

	@Autowired
	private SocialConnectionRepository socialConnectionRepository;

	@Autowired
	private MongoConnectionTransformers mongoConnectionTransformers;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserService userService;

	@Autowired
	public IntegrationService(IntegrationRepository integrationRepository) {
		super(integrationRepository);
		this.integrationRepository = integrationRepository;
	}

	public Integration getIntegrationByUser(User user) {
		return integrationRepository.findByUser(user);
	}

	public boolean integrationExist(User user) {
		boolean state = integrationRepository.findByUser(user) == null ? false : true;
		return state;
	}

	public Integration connectToJIRA(User user, String jiraUserName, String basicAuth, String jiraBaseURL) throws PurpleException {
		Integration integration = null;
		try {
			if (!integrationExist(user)) {
				integration = new Integration();
				integration.setJiraBasicAuthHeader(basicAuth);
				integration.setJiraBaseURL(jiraBaseURL);
				integration.setJiraUserName(jiraUserName);
				integration.setUser(user);
				integrationRepository.save(integration);
			} else {
				integration = getIntegrationByUser(user);
				integration.setJiraBasicAuthHeader(basicAuth);
				integration = integrationRepository.save(integration);
			}
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.JIRA_CONNECTION_FAILED);
		}
		return integration;

	}

	public Integration getJIRAConnection(User user) throws PurpleException {
		Integration integration = null;

		try {
			integration = getIntegrationByUser(user);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.JIRA_CONNECTION_FAILED);
		}
		return integration;
	}

	public List<net.rcarz.jiraclient.Project> syncJiraProjectWithUser(Integration integration) throws JiraException {
		JiraBasicCredentials creds = new JiraBasicCredentials(integration.getJiraUserName(), integration.getJiraBasicAuthHeader());
		JiraClient jira = new JiraClient(integration.getJiraBaseURL(), creds);
		List<net.rcarz.jiraclient.Project> jiraProjects = jira.getProjects();
		try {
			for (net.rcarz.jiraclient.Project project : jiraProjects) {
				net.rcarz.jiraclient.Project projectDetail = jira.getProject(project.getKey());
				for (String role : projectDetail.getRoles().keySet()) {
					Role roleObj = jira.getProjectRole(projectDetail.getRoles().get(role));
					for (RoleActor roleActor : roleObj.getRoleActors()) {
						if (roleActor.isUser())
							System.out.println(roleActor.getUser());
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return jiraProjects;
	}

	public void addJiraProjects(List<net.rcarz.jiraclient.Project> jiraProjects, User loggedInUser) throws PurpleException {
		List<String> jiraProjectList = new LinkedList<String>();
		List<String> finalProjectListToBeSaved = new LinkedList<String>();

		try {
			for (net.rcarz.jiraclient.Project jiraProject : jiraProjects) {
				jiraProjectList.add(jiraProject.getName());
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

	public List<Map<String, String>> getSlackChannels() {
		Slack slack = getSlackApi();
		if (slack == null)
			return null;

		List<SlackChannel> lists = slack.channelOperations().getAllChannels();
		List<Map<String, String>> channelInfo = new ArrayList<>();

		for (SlackChannel slackChannel : lists) {
			Map<String, String> channel = new HashMap<>();
			channel.put("id", slackChannel.getId());
			channel.put("name", "#" + slackChannel.getName());
			channelInfo.add(channel);
		}

		return channelInfo;
	}

	public Integration setSlackChannel(String slackChannelId) {
		Integration integration = getIntegrationForCurrentUser();
		integration.setSlackChannelId(slackChannelId);
		return save(integration);
	}

	private Integration getIntegrationForCurrentUser() {
		User loggedUser = userService.getLoggedInUser();
		return integrationRepository.findByUser(loggedUser);
	}

	public void postURLToSlack(String linkText, String url) {
		Slack slack = getSlackApi();
		String textToBePosted = linkText + "<" + url + ">";
		Integration integration = getIntegrationForCurrentUser();
		if (integration != null && slack != null) {
			String slackChannelId = integration.getSlackChannelId();
			SlackChannel slackChannel = slack.channelOperations().findChannelById(slackChannelId);
			if (slackChannel != null) {
				slack.chatOperations().postMessage(textToBePosted, slackChannel.getId());
			}
		}
	}

	public void postMessageToSlack(String message) {
		Slack slack = getSlackApi();
		Integration integration = getIntegrationForCurrentUser();
		if (integration != null && slack != null) {
			String slackChannelId = integration.getSlackChannelId();
			SlackChannel slackChannel = slack.channelOperations().findChannelById(slackChannelId);
			if (slackChannel != null) {
				slack.chatOperations().postMessage(message, slackChannel.getId());
			}
		}
	}

	public List<String> getGithubLatestCommits() {
		List<String> result = new ArrayList<>();
		GitHub github = getGithubApi();
		Integration integration = getIntegrationForCurrentUser();
		if (integration != null && github != null) {
			String user = github.userOperations().getProfileId();
			List<GitHubRepo> repos = asList(
					github.restOperations().getForObject("https://api.github.com/users/" + user + "/repos", GitHubRepo.class));
			for (GitHubRepo gitHubRepo : repos) {
				List<GitHubCommit> commits = github.repoOperations().getCommits(user, gitHubRepo.getName());
				for (GitHubCommit gitHubCommit : commits) {
					result.add(gitHubCommit.getMessage());
				}
			}

		}

		return result;
	}

	public List<String> getBitbucketLatestCommits() {
		List<String> result = new ArrayList<>();
		BitBucket bitBucket = getBitBucketApi();
		Integration integration = getIntegrationForCurrentUser();
		if (integration != null && bitBucket != null) {
			List<BitBucketRepository> repositories = bitBucket.repoOperations().getUserRepositories();

			for (BitBucketRepository bitBucketRepository : repositories) {
				BitBucketChangesets bitBucketChangesets = bitBucket.repoOperations().getChangesets(
						bitBucket.userOperations().getUserWithRepositories().getUser().getUsername(), bitBucketRepository.getSlug());

				for (BitBucketChangeset bitBucketChangeset : bitBucketChangesets.getChangesets()) {
					result.add(bitBucketChangeset.getMessage());
				}
			}

		}

		return result;
	}

	private Slack getSlackApi() {
		String userId = userService.getLoggedInUserEmail();
		List<SocialConnection> socialCnnections = socialConnectionRepository.findByUserIdAndProviderId(userId, "slack");
		if (socialCnnections != null && !socialCnnections.isEmpty()) {
			Connection<?> connection = mongoConnectionTransformers.toConnection().apply(socialCnnections.get(0));
			Slack slack = (Slack) connection.getApi();
			return slack;
		}
		return null;
	}

	private BitBucket getBitBucketApi() {
		String userId = userService.getLoggedInUserEmail();
		List<SocialConnection> socialCnnections = socialConnectionRepository.findByUserIdAndProviderId(userId, "bitbucket");
		if (socialCnnections != null && !socialCnnections.isEmpty()) {
			Connection<?> connection = mongoConnectionTransformers.toConnection().apply(socialCnnections.get(0));
			BitBucket bitBucket = (BitBucket) connection.getApi();
			return bitBucket;
		}
		return null;
	}

	private GitHub getGithubApi() {
		String userId = userService.getLoggedInUserEmail();
		List<SocialConnection> socialCnnections = socialConnectionRepository.findByUserIdAndProviderId(userId, "github");
		if (socialCnnections != null && !socialCnnections.isEmpty()) {
			Connection<?> connection = mongoConnectionTransformers.toConnection().apply(socialCnnections.get(0));
			GitHub github = (GitHub) connection.getApi();
			return github;
		}
		return null;
	}

	public void deleteSlackConnection() {
		deleteConnection("slack");
	}

	public void deleteGithubConnection() {
		deleteConnection("github");
	}

	public void deleteBitbucketConnection() {
		deleteConnection("bitbucket");
	}

	public void deleteHipChatConnection() {
		deleteConnection("hipchat");
	}

	private void deleteConnection(String connectionType) {
		String userId = userService.getLoggedInUserEmail();
		List<SocialConnection> socialCnnections = socialConnectionRepository.findByUserIdAndProviderId(userId, connectionType);
		if (socialCnnections != null && !socialCnnections.isEmpty()) {
			socialConnectionRepository.delete(socialCnnections);
		}
	}
}
