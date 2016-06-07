package com.bbytes.purple.utils;

public class GlobalConstants {

	public static final String HEADER_TENANT_ID = "tenantId";

	public static final String USER_ROLE = "role";

	public static final String HEADER_AUTH_TOKEN = "X-AUTH-TOKEN";

	public static final String URL_AUTH_TOKEN = "token";

	public static final String EMAIL_SIGNUP_SUBJECT = "Statusnap - Activation Link";

	public static final String EMAIL_INVITE_SUBJECT = "Invitation to join Statusnap";

	public static final String EMAIL_FEEDBACK_SEND_SUBJECT = " has sent the feedback on Statusnap";

	public static final String EMAIL_FEEDBACK_RESPONSE_SUBJECT = "Thank you for providing your feedback!";

	public static final String EMAIL_COMMENT_SUBJECT = "Statusnap - You have a new comment on your status";

	public static final String EMAIL_REPLY_SUBJECT = "Statusnap - You have a new reply on your comment";

	public static final String SCHEDULER_SUBJECT = "Statusnap - Its time to update your daily status";

	public static final String ASSOCIATE_LIST_SUBJECT = "Statusnap - Associate Checklist - Pending Status update";

	public static final String FORGOT_PASSWORD_SUBJECT = "Statusnap - Reset Password";

	public static final String EMAIL_SIGNUP_TEMPLATE = "email-text-signup.html";

	public static final String EMAIL_INVITE_TEMPLATE = "email-text-invite-user.html";

	public static final String SCHEDULER_EMAIL_TEMPLATE = "email-text-scheduler.html";

	public static final String COMMENT_EMAIL_TEMPLATE = "email-text-comment.html";

	public static final String FEEDBACK_SEND_EMAIL_TEMPLATE = "email-text-feedback-send.html";

	public static final String FEEDBACK_RESPONSE_EMAIL_TEMPLATE = "email-text-feedback-response.html";

	public static final String REPLY_EMAIL_TEMPLATE = "email-text-reply.html";

	public static final String ASSOCIATES_EMAIL_TEMPLATE = "email-text-associate-checklist.html";

	public static final String EMAIL_FORGOT_PASSWORD_TEMPLATE = "email-text-forgot-password.html";

	public static final String USER_NAME = "name";

	public static final String CURRENT_DATE = "current_date";

	public static final String SUBSCRIPTION_DATE = "subscriptionDate";

	public static final String COMMENT_DESC = "comment_desc";

	public static final String REPLY_DESC = "reply_desc";

	public static final String ACTIVATION_LINK = "activationLink";

	public static final String SETTING_LINK = "settingLink";

	public static final String TOKEN_URL = "/activateAccount?token=";

	public static final String STATUS_URL = "/updatestatus?token=";

	public static final String SETTING_URL = "/updateSetting?token=";

	public static final String STATUS_DATE = "&sd=";

	public static final String FORGOT_PASSWORD_URL = "/forgotPassword?token=";

	public static final String TIME_FORMAT = "h:mm a";

	public static final String DATE_FORMAT = "MMM d, yyyy";

	public static final String DATE_HOLIDAY_FORMAT = "dd/MM/yyyy";

	public static final String SCHEDULER_TIME_FORMAT = "HH:mm";

	public static final String SCHEDULER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

	public static final String DEFAULT_PASSWORD = "purple123";

	public static final String PASSWORD = "default_password";

	public static final String CATEGORY = "category";

	public static final String FEEDBACK = "feedback";

	public static final String VALID_HOURS = "hours";

	public static final String JIRA_GETPROJECTS_API_URL = "/rest/api/2/project";

	public static final String FEEDBACK_EMAIL_ADDRESS = "statusnap@beyondbytes.co.in";
}
