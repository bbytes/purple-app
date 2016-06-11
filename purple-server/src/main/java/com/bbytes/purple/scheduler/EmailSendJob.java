package com.bbytes.purple.scheduler;

import java.util.List;
import java.util.Map;

import com.bbytes.purple.service.NotificationService;
import com.bbytes.purple.utils.GlobalConstants;

import lombok.Data;

@Data
public class EmailSendJob implements Runnable {

	private List<String> emailList;

	private Map<String, Object> emailBody;

	private NotificationService notificationService;
	
	private String subject;

	public EmailSendJob(Map<String, Object> emailBody, List<String> emailList,
			NotificationService notificationService, String subject) {
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

		notificationService.sendTemplateEmail(emailList, subject,
				GlobalConstants.SCHEDULER_EMAIL_TEMPLATE, emailBody);
	}
}
