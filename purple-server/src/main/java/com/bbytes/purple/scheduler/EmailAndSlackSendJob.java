package com.bbytes.purple.scheduler;

import java.util.List;
import java.util.Map;

import com.bbytes.purple.domain.User;
import com.bbytes.purple.service.NotificationService;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.TenancyContextHolder;

import lombok.Data;

/**
 * Email and slack send for daily reminder scheduler
 * 
 * @author Akshay
 *
 */
@Data
public class EmailAndSlackSendJob implements Runnable {

	private List<String> emailList;

	private Map<String, Object> emailBody;

	private NotificationService notificationService;

	private String subject;

	private User user;

	private String tenantId;

	public EmailAndSlackSendJob(String tenantId, User user, Map<String, Object> emailBody, List<String> emailList,
			NotificationService notificationService, String subject) {

		this.tenantId = tenantId;
		this.user = user;
		this.emailList = emailList;
		this.notificationService = notificationService;
		this.emailBody = emailBody;
		this.subject = subject;
	}

	/**
	 * Method is used to distribute email and slack message according to
	 * scheduler per user
	 */
	@Override
	public void run() {
		try {
			// setting tenant for the current thread
			TenancyContextHolder.setTenant(tenantId);
			notificationService.sendSlackMessage(user, "Statusnap - It's time to update your daily status ",
					emailBody.get("activationLink").toString());
			notificationService.sendTemplateEmail(emailList, subject, GlobalConstants.SCHEDULER_EMAIL_TEMPLATE,
					emailBody);
		} finally {
			// clearing tenant for the current thread
			TenancyContextHolder.clearContext();
		}

	}
}
