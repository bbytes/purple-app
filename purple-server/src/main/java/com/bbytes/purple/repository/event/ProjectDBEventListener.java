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
import com.bbytes.purple.domain.TaskItem;
import com.bbytes.purple.domain.TaskList;
import com.bbytes.purple.repository.ProjectRepository;
import com.bbytes.purple.service.CommentService;
import com.bbytes.purple.service.StatusService;
import com.bbytes.purple.service.TaskItemService;
import com.bbytes.purple.service.TaskListService;
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

	@Autowired
	private TaskListService taskListService;

	@Autowired
	private TaskItemService taskItemService;

	/**
	 * Deleting user's statuses, comments, taskList and taskItems when the
	 * project is deleted - Cascade delete
	 */
	@Override
	public void onBeforeDelete(BeforeDeleteEvent<Project> event) {
		final DBObject projectDeleted = event.getSource();
		Project project = projectRepository.findOne(projectDeleted.get("projectId").toString());

		List<Status> statusFromDB = statusService.getStatusByProject(project);
		List<Comment> commentFromDB = commentService.getCommentByStatus(statusFromDB);
		List<TaskList> taskListFromDB = taskListService.findByProject(project);
		List<TaskItem> taskItemFromDB = taskItemService.findByProject(project);

		commentService.delete(commentFromDB);
		statusService.delete(statusFromDB);
		taskItemService.delete(taskItemFromDB);
		taskListService.delete(taskListFromDB);
	}

}
