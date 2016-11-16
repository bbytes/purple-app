package com.bbytes.purple.scheduler;

import java.util.List;
import java.util.Map;

import com.bbytes.purple.domain.User;
import com.bbytes.purple.service.NotificationService;
import com.bbytes.purple.utils.GlobalConstants;

import lombok.Data;

@Data
public class EmailAndSlackSendJob implements Runnable {

	private List<String> emailList;

	private Map<String, Object> emailBody;

	private NotificationService notificationService;

	private String subject;

	private User user;

	public EmailAndSlackSendJob(User user, Map<String, Object> emailBody, List<String> emailList, NotificationService notificationService,
			String subject) {
		this.user = user;
		this.emailList = emailList;
		this.notificationService = notificationService;
		this.emailBody = emailBody;
		this.subject = subject;
	}

	/**
	 * Method is used to distribute email according to scheduler per user
	 */
	@Override
	public void run() {
		
		notificationService.sendSlackMessage(user,"Statusnap reminder", emailBody.get("activationLink").toString());
		notificationService.sendTemplateEmail(emailList, subject,
				GlobalConstants.SCHEDULER_EMAIL_TEMPLATE, emailBody);
	}
}
