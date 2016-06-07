package com.bbytes.purple.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

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

		notificationService.sendTemplateEmail(emailList, subject, template, emailBody);
	}
}
