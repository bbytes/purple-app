package com.bbytes.purple.utils;

public class GlobalConstants {

	public static final String HEADER_TENANT_ID = "tenantId";

	public static final String USER_ROLE = "role";

	public static final String HEADER_AUTH_TOKEN = "X-AUTH-TOKEN";

	public static final String URL_AUTH_TOKEN = "token";

	public static final String EMAIL_ACTIVATION_SUBJECT = "Activation Link for Purple App";

	public static final String EMAIL_INVITE_SUBJECT = "Invitation for Purple App";

	public static final String SCHEDULER_SUBJECT = "Stand-up Reminder for purple App";

	public static final String FORGOT_PASSWORD_SUBJECT = "Forgot password for Purple APP";

	public static final String EMAIL_ACTIVATION_TEMPLATE = "email-text-only.html";

	public static final String EMAIL_INVITE_TEMPLATE = "email-text-invite-user.html";

	public static final String SCHEDULER_EMAIL_TEMPLATE = "email-text-scheduler.html";

	public static final String EMAIL_FORGOT_PASSWORD_TEMPLATE = "email-text-forgot-password.html";

	public static final String USER_NAME = "name";

	public static final String PROJECT_NAME = "projectName";

	public static final String SUBSCRIPTION_DATE = "subscriptionDate";

	public static final String ACTIVATION_LINK = "activationLink";

	public static final String TOKEN_URL = "/activateAccount?token=";

	public static final String FORGOT_PASSWORD_URL = "/forgotPassword?token=";

	public static final String TIME_FORMAT = "h:mm a";

	public static final String DATE_FORMAT = "MMM d, yyyy";

	public static final String DATE_HOLIDAY_FORMAT = "dd/MM/yyyy";

	public static final String SCHEDULER_TIME_FORMAT = "HH:mm";

	public static final String SCHEDULER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

	public static final String DEFAULT_PASSWORD = "purple123";

	public static final String PASSWORD = "default_password";
}
