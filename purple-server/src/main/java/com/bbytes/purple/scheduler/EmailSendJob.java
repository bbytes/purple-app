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

	public EmailSendJob(Map<String, Object> emailBody, List<String> emailList,
			NotificationService notificationService) {
		this.emailList = emailList;
		this.notificationService = notificationService;
		this.emailBody = emailBody;
	}

	/**
	 * Method is used to distribute email according to scheduler per user
	 */
	@Override
	public void run() {

		notificationService.sendTemplateEmail(emailList, GlobalConstants.SCHEDULER_SUBJECT,
				GlobalConstants.SCHEDULER_EMAIL_TEMPLATE, emailBody);
	}
}
