package com.bbytes.purple.utils;

public class GlobalConstants {

	public static final String HEADER_TENANT_ID = "tenantId";

	public static final String USER_ROLE = "role";

	public static final String URL_PARAM_EMAIL_ID = "emailId";

	public static final String HEADER_AUTH_TOKEN = "X-AUTH-TOKEN";

	public static final String URL_AUTH_TOKEN = "token";

	public static final String EMAIL_SIGNUP_TEMPLATE = "email-text-signup.html";

	public static final String EMAIL_INVITE_TEMPLATE = "email-text-invite-user.html";

	public static final String EMAIL_REGISTER_TENANT_TEMPLATE = "email-text-register_tenant.html";

	public static final String EMAIL_INVITE_PROJECT_TEMPLATE = "email-text-invite-project.html";

	public static final String SCHEDULER_EMAIL_TEMPLATE = "email-text-scheduler.html";
	
	public static final String SCHEDULER_SLACK_TEMPLATE = "slack-scheduler.html";

	public static final String COMMENT_EMAIL_TEMPLATE = "email-text-comment.html";

	public static final String MENTION_EMAIL_TEMPLATE = "email-text-mention.html";

	public static final String UPDATE_COMMENT_EMAIL_TEMPLATE = "email-text-update-comment.html";

	public static final String FEEDBACK_SEND_EMAIL_TEMPLATE = "email-text-feedback-send.html";

	public static final String FEEDBACK_RESPONSE_EMAIL_TEMPLATE = "email-text-feedback-response.html";

	public static final String REPLY_EMAIL_TEMPLATE = "email-text-reply.html";

	public static final String ASSOCIATES_EMAIL_TEMPLATE = "email-text-associate-checklist.html";

	public static final String EMAIL_FORGOT_PASSWORD_TEMPLATE = "email-text-forgot-password.html";

	public static final String USER_NAME = "name";

	public static final String PROJECT_NAME = "projectName";

	public static final String CURRENT_DATE = "current_date";

	public static final String SUBSCRIPTION_DATE = "subscriptionDate";

	public static final String COMMENT_DESC = "comment_desc";

	public static final String WORKED_ON = "workedOn";

	public static final String WORKING_ON = "workingOn";

	public static final String BLOCKERS = "blockers";

	public static final String REPLY_DESC = "reply_desc";

	public static final String ACTIVATION_LINK = "activationLink";

	public static final String SETTING_LINK = "settingLink";

	public static final String TOKEN_URL = "/activateAccount?token=";

	public static final String STATUS_URL = "/updatestatus?token=";

	public static final String SETTING_URL = "/updateSetting?token=";

	public static final String STATUS_SNIPPET_URL = "/status-snippet?pk=";
	
	public static final String COMMENT_SNIPPET_URL = "/comment-snippet?pk=";
	
	public static final String REPLY_SNIPPET_URL = "/reply-snippet?pk=";

	public static final String STATUS_ID_PARAM = "&sid=";
	
	public static final String COMMENT_ID_PARAM = "&cid=";
	
	public static final String REPLY_ID_PARAM = "&rid=";

	public static final String STATUS_DATE = "&sd=";

	public static final String FORGOT_PASSWORD_URL = "/forgotPassword?token=";

	public static final String TIME_FORMAT = "h:mm a";

	public static final String DATE_FORMAT = "MMM d, yyyy";

	public static final String DATE_TIME_FORMAT = "dd-M-yyyy hh:mm a";

	public static final String DATE_HOLIDAY_FORMAT = "dd/MM/yyyy";

	public static final String SCHEDULER_TIME_FORMAT = "HH:mm";

	public static final String SCHEDULER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

	public static final String DEFAULT_PASSWORD = "purple123";

	public static final String PASSWORD = "default_password";

	public static final String EMAIL_ADDRESS = "email";

	public static final String CATEGORY = "category";

	public static final String FEEDBACK = "feedback";

	public static final String VALID_HOURS = "hours";

	public static final String EMAIL_STRING_TEXT = "emailStringText";

	public static final String MENTIONED_EMAIL_TEXT = "mentioned you on Statusnap";

	public static final String COMMENT_EMAIL_TEXT = "commented on your Statusnap status";

	public static final String REPLY_EMAIL_TEXT = "replied on your Statusnap comment";

	public static final String JIRA_GETPROJECTS_API_URL = "/rest/api/2/project";

	public static final String STATUSNAP_EMAIL_ADDRESS = "statusnap@beyondbytes.co.in";

	public static final String SALES_EMAIL_ADDRESS = "sales@beyondbytes.co.in";

	public static final String MENTION_REGEX_PATTERN = "@\\<!--(.*?)\\-->";

	public static final String TASKLIST_REGEX_PATTERN = "#\\{<!--(.*?)\\-->(.*?)\\}";

}
