package com.bbytes.purple.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	private NotificationService notificationService;

	/**
	 * Method is used to send email in asynchronous way i.e. parallel execution
	 * 
	 * @param emailList
	 * @param emailBody
	 * @param subject
	 * @param template
	 */
	@Async
	public void sendEmail(List<String> emailList, Map<String, Object> emailBody, String subject, String template) {

		try {
			notificationService.sendTemplateEmail(emailList, subject, template, emailBody);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}
}
