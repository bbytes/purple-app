package com.bbytes.purple.repository.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Component;

import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.service.CommentService;
import com.bbytes.purple.service.StatusService;
import com.mongodb.DBObject;

@Component
@Profile("saas")
public class CommentDBEventListner extends AbstractMongoEventListener<Comment> {

	@Autowired
	private StatusService statusService;

	@Autowired
	private CommentService commentService;

	@Autowired
	MongoTemplate mongoTemplate;
	
	/**
	 * After removing comment, update commentCount in status object.
	 */
	
	@Override
	public void onBeforeDelete(BeforeDeleteEvent<Comment> event) {
		final DBObject commentDeleted = event.getSource();
		Comment comment = commentService.findOne(commentDeleted.get("commentId").toString());
		Status status = statusService.findOne(comment.getStatus().getStatusId());
		long count = commentService.getCountByStatus(status);
		count--;
		status.setCommentCount(count);
		statusService.save(status);

	}

	/**
	 * Update commentCount in status object after saving comment.
	 */
	@Override
	public void onAfterSave(AfterSaveEvent<Comment> event) {

		Comment commentSaved = event.getSource();
		Status status = statusService.findOne(commentSaved.getStatus().getStatusId());
		long count = commentService.getCountByStatus(status);
		status.setCommentCount(count);
		statusService.save(status);

	}

	
}
