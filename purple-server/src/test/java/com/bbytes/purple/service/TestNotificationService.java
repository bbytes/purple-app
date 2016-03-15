package com.bbytes.purple.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.context.Context;

import com.bbytes.purple.PurpleApplicationTests;

public class TestNotificationService extends PurpleApplicationTests {

	@Autowired
	private NotificationService notificationService;

	@Test
	@Ignore
	public void testSendEmail() {
		List<String> toEmailList = new ArrayList<>();
		toEmailList.add("tm@beyondbytes.co.in");
		String subject = "Test subject - purple app";
		String body = "Test sample email body for purple app";
		Assert.assertTrue(notificationService.sendEmail(toEmailList, subject, body,false));
	}

	@Test
	public void testSendHTMLEmail() {
		List<String> toEmailList = new ArrayList<>();
		toEmailList.add("tm@beyondbytes.co.in");
		String subject = "HTML EMAIL - Test subject - purple app";
		Context ctx = new Context();
		ctx.setVariable("name", "Thanneer");
		ctx.setVariable("subscriptionDate", new Date());
		ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));

		Assert.assertTrue(notificationService.sendTemplateEmail(toEmailList, subject, "email-simple", ctx));
	}

	@Test
	public void testSlackMessaging() {
		String message = "Test sample email body for purple app";
		Assert.assertTrue(
				notificationService.sendSlackMessage("xoxp-3105667121-3107455865-8187187190-11282c", message));
	}

}
