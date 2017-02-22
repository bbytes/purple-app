package com.bbytes.purple.scheduler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.bbytes.purple.domain.Integration;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.service.IntegrationService;
import com.bbytes.purple.service.JiraIntegrationService;
import com.bbytes.purple.service.NotificationService;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.TenancyContextHolder;

import lombok.Data;

/**
 * Sync JIRA users Job Executor
 * 
 * @author Akshay
 *
 */

@Data
public class SyncUserJobExecutor implements Runnable {

	private IntegrationService integrationService;

	private JiraIntegrationService jiraIntegrationService;

	private User loggedInUser;

	private String tenantId;

	private Map<String, Object> emailBody;

	private NotificationService notificationService;

	private User user;

	private List<String> emailList;

	DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

	public SyncUserJobExecutor(User loggedInUser, IntegrationService integrationService,
			JiraIntegrationService jiraIntegrationService, Map<String, Object> emailBody, List<String> emailList,
			NotificationService notificationService) {

		this.loggedInUser = loggedInUser;
		this.integrationService = integrationService;
		this.jiraIntegrationService = jiraIntegrationService;
		this.emailList = emailList;
		this.notificationService = notificationService;
		this.emailBody = emailBody;
	}

	/**
	 * Method is used to check JIRA sync users failed or success and send the
	 * email for result
	 */
	@Override
	public void run() {
		String JiraSyncSubject = null;

		try {
			JiraSyncSubject = GlobalConstants.JIRA_SYNC_USER_SUCCESS_SUBJECT;
			emailBody.put(GlobalConstants.JIRA_SYNC_RESULT, "successful");
			emailBody.put(GlobalConstants.JIRA_SYNC_FAILED_STRING, "");
			// setting tenant for the current thread
			TenancyContextHolder.setTenant(loggedInUser.getOrganization().getOrgId());
			Integration integration = integrationService.getIntegrationForUser(loggedInUser);
			jiraIntegrationService.syncProjectToJiraUser(integration, loggedInUser);

			notificationService.sendTemplateEmail(emailList, JiraSyncSubject, GlobalConstants.EMAIL_JIRA_SYNC_TEMPLATE,
					emailBody);
		} catch (Throwable e) {
			JiraSyncSubject = GlobalConstants.JIRA_SYNC_USER_FAILED_SUBJECT;
			emailBody.put(GlobalConstants.JIRA_SYNC_RESULT, "failed");
			emailBody.put(GlobalConstants.JIRA_SYNC_FAILED_STRING, GlobalConstants.JIRA_SYNC_USER_FAILED_REASON);

			notificationService.sendTemplateEmail(emailList, JiraSyncSubject, GlobalConstants.EMAIL_JIRA_SYNC_TEMPLATE,
					emailBody);
		} finally {
			TenancyContextHolder.clearContext();
		}
	}
}
