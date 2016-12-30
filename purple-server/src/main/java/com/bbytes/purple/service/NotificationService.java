package com.bbytes.purple.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.NotImplementedException;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.bbytes.mailgun.api.MailOperations;
import com.bbytes.mailgun.api.ResponseCallback;
import com.bbytes.mailgun.client.MailgunClient;
import com.bbytes.mailgun.model.MailgunSendResponse;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.utils.GlobalConstants;

@Service
public class NotificationService {

	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

	@Autowired
	protected Environment env;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private VelocityEngine templateEngine;

	@Autowired
	private IntegrationService integrationService;

	@Value("${spring.mail.from}")
	private String fromEmail;

	private MailgunClient client;

	private String domain;

	@PostConstruct
	private void init() {
		String mailgunAPIKey = env.getProperty("mailgun.api.key");
		domain = env.getProperty("mailgun.domain");
		client = MailgunClient.create(mailgunAPIKey);
	}

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
			String[] recipients = new String[toEmailList.size()];
			toEmailList.toArray(recipients);

			MimeMessageHelper helper = new MimeMessageHelper(mail, true);
			helper.setTo(recipients);
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

	public boolean mail(String subject, String body, String from, String... to) {
		MailOperations mailOperations = client.mailOperations(domain);
		mailOperations.sendHtmlMailAsync(from, to, subject, body, new ResponseCallback<MailgunSendResponse>() {

			@Override
			public void onSuccess(MailgunSendResponse result) {
				// do nothing
			}

			@Override
			public void onFailure(Throwable ex) {
				logger.error(ex.getMessage(),ex);
			}
		});
		return true;
	}

	@SuppressWarnings("deprecation")
	public boolean sendTemplateEmail(List<String> toEmailList, String subject, String emailTemplateName,
			Map<String, Object> templateVariableMap) {
		String emailHTMLContent = VelocityEngineUtils.mergeTemplateIntoString(this.templateEngine, emailTemplateName, "UTF-8",
				templateVariableMap);
		return mail(subject, emailHTMLContent, fromEmail, toEmailList.toArray(new String[toEmailList.size()]));
	}

	/**
	 * Send slack message reminder to fill status, The message has to be
	 * rendered thru slack message template
	 * 
	 * @param token
	 * @param message
	 * @return
	 */
	public boolean sendSlackMessage(String message) {
		try {
			integrationService.postMessageToSlack(message);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	public boolean sendSlackMessage(User user, String message) {
		try {
			integrationService.postMessageToSlack(user, message);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	public boolean sendSlackMessage(User user, String type, String link) {
		try {
			Map<String, Object> templateVariableMap = new HashMap<String, Object>();
			templateVariableMap.put("type", type);
			templateVariableMap.put("link", link);

			String slackMessageContent = VelocityEngineUtils.mergeTemplateIntoString(this.templateEngine,
					GlobalConstants.SCHEDULER_SLACK_TEMPLATE, "UTF-8", templateVariableMap);

			integrationService.postMessageToSlack(user, slackMessageContent);
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
