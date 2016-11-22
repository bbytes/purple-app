package com.bbytes.purple.repository.event;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Component;

import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.repository.ProjectRepository;
import com.bbytes.purple.service.CommentService;
import com.bbytes.purple.service.StatusService;
import com.mongodb.DBObject;

@Component
@Profile("saas")
public class ProjectDBEventListener extends AbstractMongoEventListener<Project> {

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private StatusService statusService;

	@Autowired
	private CommentService commentService;

	/**
	 * Remove the project from the user list and and deleting user's statuses
	 * and comments when the project is deleted - Cascade delete
	 */
	@Override
	public void onBeforeDelete(BeforeDeleteEvent<Project> event) {
		final DBObject projectDeleted = event.getSource();
		Project project = projectRepository.findOne(projectDeleted.get("projectId").toString());

		List<Status> statusFromDB = statusService.getStatusByProject(project);
		List<Comment> commentFromDB = commentService.getCommentByStatus(statusFromDB);
		commentService.delete(commentFromDB);
		statusService.delete(statusFromDB);
	}

}
