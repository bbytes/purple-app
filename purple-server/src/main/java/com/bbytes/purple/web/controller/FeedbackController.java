package com.bbytes.purple.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.FeedbackDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.NotificationService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.SuccessHandler;

/**
 * Feedback Controller
 * 
 * @author akshay
 *
 */
@RestController
public class FeedbackController {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private UserService userService;
	
	@Value("${email.feedback.send.subject}")
	private String feedbackSendSubject;
	
	@Value("${email.feedback.response.subject}")
	private String feedbackResponseSubject;

	@RequestMapping(value = "/api/v1/feedback", method = RequestMethod.POST)
	public RestResponse sendFeedback(@RequestBody FeedbackDTO feedbackDTO) throws PurpleException {

		final String FEEDBACK_SUCCESS_MSG = "Feedback has been sent succeessfully";
		User user = userService.getLoggedInUser();
		DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

		final String feedBackSendSubject = user.getName() + " " + feedbackSendSubject;
		final String feedbackSendTemplate = GlobalConstants.FEEDBACK_SEND_EMAIL_TEMPLATE;

		final String feedbackResponseTemplate = GlobalConstants.FEEDBACK_RESPONSE_EMAIL_TEMPLATE;

		String postDate = dateFormat.format(new Date());
		List<String> feedBackSendEmailList = new ArrayList<String>();
		feedBackSendEmailList.add(GlobalConstants.STATUSNAP_EMAIL_ADDRESS);

		Map<String, Object> feedBackSendEmailBody = new HashMap<>();
		feedBackSendEmailBody.put(GlobalConstants.USER_NAME, user.getName());
		feedBackSendEmailBody.put(GlobalConstants.EMAIL_ADDRESS, user.getEmail());
		feedBackSendEmailBody.put(GlobalConstants.SUBSCRIPTION_DATE, postDate);
		feedBackSendEmailBody.put(GlobalConstants.CATEGORY, feedbackDTO.getCategory());
		feedBackSendEmailBody.put(GlobalConstants.FEEDBACK, feedbackDTO.getSuggestions());

		List<String> feedbackResponseEmailList = new ArrayList<String>();
		feedbackResponseEmailList.add(user.getEmail());

		Map<String, Object> feedbackResponseEmailBody = new HashMap<>();
		feedbackResponseEmailBody.put(GlobalConstants.USER_NAME, user.getName());

		notificationService.sendTemplateEmail(feedBackSendEmailList, feedBackSendSubject, feedbackSendTemplate, feedBackSendEmailBody);
		notificationService.sendTemplateEmail(feedbackResponseEmailList, feedbackResponseSubject, feedbackResponseTemplate, feedbackResponseEmailBody);
		
		logger.debug("User with email '" + user.getEmail() + "' is sent feedback successfully");
		RestResponse feedbackResponse = new RestResponse(RestResponse.SUCCESS, FEEDBACK_SUCCESS_MSG, SuccessHandler.FEEDBACK_SUCCESS);

		return feedbackResponse;
	}
}