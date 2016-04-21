package com.bbytes.purple.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.service.NotificationService;
import com.bbytes.purple.utils.GlobalConstants;

import lombok.Data;

@Data
public class EmailSendJob implements Runnable {

	private Project project;

	private List<String> emailList;

	private NotificationService notificationService;

	public EmailSendJob(Project project, List<String> emailList, NotificationService notificationService) {
		this.project = project;
		this.emailList = emailList;
		this.notificationService = notificationService;
	}

	/**
	 * Method is used to distribute email according to scheduler
	 */
	@Override
	public void run() {

		if (project != null && (!emailList.isEmpty() && emailList != null)) {
			Map<String, Object> emailBody = new HashMap<>();
			emailBody.put(GlobalConstants.PROJECT_NAME, project.getProjectName());
			notificationService.sendTemplateEmail(emailList, GlobalConstants.SCHEDULER_SUBJECT,
					GlobalConstants.SCHEDULER_EMAIL_TEMPLATE, emailBody);
		}
	}

}
