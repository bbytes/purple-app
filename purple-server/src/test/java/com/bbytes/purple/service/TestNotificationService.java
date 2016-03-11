package com.bbytes.purple.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
		Map<String,Object> ctx = new HashMap<>();
		ctx.put("name", "Thanneer");
		ctx.put("subscriptionDate", new Date());
		ctx.put("hobbies", Arrays.asList("Cinema", "Sports", "Music"));

		Assert.assertTrue(notificationService.sendTemplateEmail(toEmailList, subject, "email-text-only.html", ctx));
	}

	@Test
	public void testSlackMessaging() {
		String message = "Test sample message from purple app";
		Assert.assertTrue(
				notificationService.sendSlackMessage("xoxp-3105667121-3107455865-8187187190-11282c", message));
	}

}
