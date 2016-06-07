package com.bbytes.purple.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.bbytes.purple.PurpleBaseApplicationTests;

public class TestNotificationService extends PurpleBaseApplicationTests {


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
		ctx.put("activationLink", "google.com");

		Assert.assertTrue(notificationService.sendTemplateEmail(toEmailList, subject, "email-text-only.html", ctx));
	}

	@Test
	public void testSlackMessaging() {
		String message = "Test sample message from purple app";
		Assert.assertTrue(
				notificationService.sendSlackMessage("xoxp-3105667121-3107455865-8187187190-11282c", message));
	}

}
