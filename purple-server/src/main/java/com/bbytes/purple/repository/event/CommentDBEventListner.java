package com.bbytes.purple.repository.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;

import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.service.CommentService;
import com.bbytes.purple.service.EmailService;
import com.bbytes.purple.service.StatusService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.GlobalConstants;

@Component
@Profile("saas")
public class CommentDBEventListner extends AbstractMongoEventListener<Comment> {

	@Autowired
	private StatusService statusService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private UserService userService;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	private EmailService emailService;

	/**
	 * Update commentCount in status object after saving comment.
	 */
	@Override
	public void onAfterSave(AfterSaveEvent<Comment> event) {

		final String subject = GlobalConstants.EMAIL_STATUS_COMMENT_SUBJECT;
		final String template = GlobalConstants.COMMENT_EMAIL_TEMPLATE;

		Comment commentSaved = event.getSource();
		Status status = statusService.findOne(commentSaved.getStatus().getStatusId());
		long count = commentService.getCountByStatus(status);
		status.setCommentCount(count);
		statusService.save(status);

		String userName = userService.getLoggedinUser().getName();

		List<String> emailList = new ArrayList<String>();
		emailList.add(status.getUser().getEmail());

		Map<String, Object> emailBody = new HashMap<>();
		emailBody.put(GlobalConstants.USER_NAME, status.getUser().getName());
		emailBody.put(GlobalConstants.SUBSCRIPTION_DATE, status.getDateTime());
		emailBody.put("userName", userName);

		emailService.sendEmail(emailList, emailBody, subject, template);
	}

}
