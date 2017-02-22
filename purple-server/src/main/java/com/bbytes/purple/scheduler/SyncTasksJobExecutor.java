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
 * Sync JIRA task and issue Job Executor
 * 
 * @author Akshay
 *
 */
@Data
public class SyncTasksJobExecutor implements Runnable {

	private IntegrationService integrationService;

	private JiraIntegrationService jiraIntegrationService;

	private User loggedInUser;

	private String tenantId;

	private Map<String, Object> emailBody;

	private NotificationService notificationService;

	private User user;

	private List<String> emailList;

	DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

	public SyncTasksJobExecutor(User loggedInUser, IntegrationService integrationService,
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
	 * Method is used to check JIRA sync task failed or success and send the
	 * email for result
	 */
	@Override
	public void run() {
		String JiraSyncSubject = null;

		try {
			JiraSyncSubject = GlobalConstants.JIRA_SYNC_TASKS_SUCCESS_SUBJECT;
			emailBody.put(GlobalConstants.JIRA_SYNC_RESULT, "successful");
			emailBody.put(GlobalConstants.JIRA_SYNC_FAILED_STRING, "");
			// setting tenant for the current thread
			TenancyContextHolder.setTenant(loggedInUser.getOrganization().getOrgId());
			Integration integration = integrationService.getIntegrationForUser(loggedInUser);
			jiraIntegrationService.updateProjectWithJiraTask(integration);

			// sending email once JIRA sync user for success
			notificationService.sendTemplateEmail(emailList, JiraSyncSubject, GlobalConstants.EMAIL_JIRA_SYNC_TEMPLATE,
					emailBody);
		} catch (Throwable e) {
			JiraSyncSubject = GlobalConstants.JIRA_SYNC_TASKS_FAILED_SUBJECT;
			emailBody.put(GlobalConstants.JIRA_SYNC_RESULT, "failed");
			emailBody.put(GlobalConstants.JIRA_SYNC_FAILED_STRING, GlobalConstants.JIRA_SYNC_TASKS_FAILED_REASON);

			// sending email once JIRA sync user for failure
			notificationService.sendTemplateEmail(emailList, JiraSyncSubject, GlobalConstants.EMAIL_JIRA_SYNC_TEMPLATE,
					emailBody);
		} finally {
			// clearing tenant for the current thread
			TenancyContextHolder.clearContext();
		}
	}
}
