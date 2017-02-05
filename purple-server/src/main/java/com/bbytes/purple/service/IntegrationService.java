package com.bbytes.purple.service;

import static java.util.Arrays.asList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.bitbucket.api.BitBucket;
import org.springframework.social.bitbucket.api.BitBucketChangeset;
import org.springframework.social.bitbucket.api.BitBucketChangesets;
import org.springframework.social.bitbucket.api.BitBucketRepository;
import org.springframework.social.connect.Connection;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubCommit;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.social.slack.api.Slack;
import org.springframework.social.slack.api.impl.SlackTemplate;
import org.springframework.stereotype.Service;

import com.bbytes.purple.auth.jwt.TokenAuthenticationProvider;
import com.bbytes.purple.domain.Integration;
import com.bbytes.purple.domain.SocialConnection;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.IntegrationRepository;
import com.bbytes.purple.repository.SocialConnectionRepository;
import com.bbytes.purple.social.MongoConnectionTransformers;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.GlobalConstants;

@Service
public class IntegrationService extends AbstractService<Integration, String> {

	private static final Logger logger = LoggerFactory.getLogger(IntegrationService.class);

	final String template = GlobalConstants.EMAIL_INVITE_TEMPLATE;

	final DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

	private IntegrationRepository integrationRepository;

	@Autowired
	private SocialConnectionRepository socialConnectionRepository;

	@Autowired
	private MongoConnectionTransformers mongoConnectionTransformers;

	@Autowired
	private UserService userService;

	@Autowired
	protected TokenAuthenticationProvider tokenAuthenticationProvider;

	@Value("${slack.api.token}")
	private String slackApiToken;

	@Autowired
	public IntegrationService(IntegrationRepository integrationRepository) {
		super(integrationRepository);
		this.integrationRepository = integrationRepository;
	}

	public Integration getIntegrationByUser(User user) {
		return integrationRepository.findByUser(user);
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
				integration.setJiraBaseURL(jiraBaseURL);
				integration.setJiraUserName(jiraUserName);
				integration = integrationRepository.save(integration);
			}
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.JIRA_CONNECTION_FAILED);
		}
		return integration;

	}

	public Integration getIntegrationForUser(User user) throws PurpleException {
		Integration integration = null;

		try {
			integration = getIntegrationByUser(user);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.JIRA_CONNECTION_FAILED);
		}
		return integration;
	}

	public boolean integrationExist(User user) {
		boolean state = integrationRepository.findByUser(user) == null ? false : true;
		return state;
	}

	private Integration getIntegrationForCurrentUser() {
		User loggedUser = userService.getLoggedInUser();
		return integrationRepository.findByUser(loggedUser);
	}

	public String getSlackUserName() {
		Integration integration = getIntegrationForCurrentUser();
		if (integration == null)
			return null;

		String slackUserName = integration.getSlackUsername();
		return slackUserName;
	}

	public void clearSlackUserName() {
		Integration integration = getIntegrationForCurrentUser();
		if (integration != null) {
			integration.setSlackUsername(null);
			integrationRepository.save(integration);
		}
	}

	public void postURLToSlack(String linkText, String url) {
		Slack slack = getSlackApi();
		String textToBePosted = linkText + "<" + url + ">";
		sendSlackMessage(textToBePosted, getSlackUserName(), slack);
	}

	public void postMessageToSlack(String message) {
		Slack slack = getSlackApi();
		sendSlackMessage(message, getSlackUserName(), slack);
	}

	public void postMessageToSlack(User user, String message) {
		Slack slack = getSlackApi(user);
		sendSlackMessage(message, getSlackUserName(), slack);
	}

	private void sendSlackMessage(String message, String slackUsername, Slack slack) {
		if (slack != null) {
			String userName = "@" + slack.userProfileOperations().getUserProfile().getName();
			if (slackUsername != null && !slackUsername.trim().isEmpty())
				userName = slackUsername;

			slack.chatOperations().postMessage(message, userName, "Statusnap");
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
		return getSlackApi(userService.getLoggedInUser());
	}

	private Slack getSlackApi(User user) {
		if (slackApiToken != null && !slackApiToken.trim().isEmpty()) {
			Slack slack = new SlackTemplate(slackApiToken);
			return slack;
		}

		if (user == null)
			return null;
		// SocialConnectionRepository socialConnectionRepository =
		// appContext.getBean(SocialConnectionRepository.class);
		List<SocialConnection> socialConnections = socialConnectionRepository.findByUserIdAndProviderId(user.getEmail(), "slack");
		if (socialConnections != null && !socialConnections.isEmpty()) {
			Connection<?> connection = mongoConnectionTransformers.toConnection().apply(socialConnections.get(0));
			Slack slack = (Slack) connection.getApi();
			return slack;
		}
		return null;
	}

	private BitBucket getBitBucketApi() {
		String userId = userService.getLoggedInUserEmail();
		// SocialConnectionRepository socialConnectionRepository =
		// appContext.getBean(SocialConnectionRepository.class);
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
		// SocialConnectionRepository socialConnectionRepository =
		// appContext.getBean(SocialConnectionRepository.class);
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
		// SocialConnectionRepository socialConnectionRepository =
		// appContext.getBean(SocialConnectionRepository.class);
		List<SocialConnection> socialCnnections = socialConnectionRepository.findByUserIdAndProviderId(userId, connectionType);
		if (socialCnnections != null && !socialCnnections.isEmpty()) {
			socialConnectionRepository.delete(socialCnnections);
		}
	}
}
