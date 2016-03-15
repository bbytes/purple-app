package com.bbytes.purple.service;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.NotImplementedException;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.bbytes.purple.utils.StringUtils;

import flowctrl.integration.slack.SlackClientFactory;
import flowctrl.integration.slack.webapi.SlackWebApiClient;

@Service
public class NotificationService {

	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private VelocityEngine templateEngine;

	@Value("${spring.mail.from}")
	private String fromEmail;

	@Value("${slack.channel.name}")
	private String slackChannelName;

	/**
	 * Send simple mail api. TODO: Need to implement html email template logic
	 * to send email .
	 * 
	 * @param toEmailList
	 * @param subject
	 * @param body
	 * @return
	 */
	public boolean sendEmail(List<String> toEmailList, String subject, String body, boolean emailBodyIsHTML) {

		MimeMessage mail = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mail, true);
			helper.setTo(StringUtils.commaSeparate(toEmailList));
			helper.setFrom(fromEmail);
			helper.setSubject(subject);
			if (emailBodyIsHTML)
				mail.setContent(body, "text/html");
			else
				helper.setText(body);
			javaMailSender.send(mail);
		} catch (MessagingException e) {
			logger.error(e.getMessage(), e);
			return false;
		} catch (MailException e) {
			logger.error(e.getMessage(), e);
			return false;
		}

		return true;
	}

	public boolean sendTemplateEmail(List<String> toEmailList, String subject, String emailTemplateName, Map<String, Object> templateVariableMap) {
		String emailHTMLContent = VelocityEngineUtils.mergeTemplateIntoString(this.templateEngine,emailTemplateName, "UTF-8", templateVariableMap);
		return sendEmail(toEmailList, subject, emailHTMLContent,true);
	}

	/**
	 * Send slack message reminder to fill status, The message has to be
	 * rendered thru slack message template
	 * 
	 * @param token
	 * @param message
	 * @return
	 */
	public boolean sendSlackMessage(String token, String message) {
		try {
			SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient(token);
			webApiClient.postMessage(slackChannelName, message);
			webApiClient.shutdown();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * Send hipchat message reminder to fill status, The message has to be
	 * rendered thru hipchat message template
	 * 
	 * @param hipChatKey
	 * @param message
	 * @return
	 */
	public boolean sendHipChatMessage(String hipChatKey, String message) {
		throw new NotImplementedException();
	}

}
