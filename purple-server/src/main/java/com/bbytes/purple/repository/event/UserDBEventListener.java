package com.bbytes.purple.repository.event;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;

import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.StatusTaskEvent;
import com.bbytes.purple.domain.TaskItem;
import com.bbytes.purple.domain.TaskList;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.repository.ProjectRepository;
import com.bbytes.purple.repository.UserRepository;
import com.bbytes.purple.service.CommentService;
import com.bbytes.purple.service.SpringProfileService;
import com.bbytes.purple.service.StatusService;
import com.bbytes.purple.service.StatusTaskEventService;
import com.bbytes.purple.service.TaskItemService;
import com.bbytes.purple.service.TaskListService;
import com.bbytes.purple.service.TenantResolverService;
import com.bbytes.purple.service.UserService;
import com.mongodb.DBObject;

/**
 * Equivalent of a domain method annotated by <code>PrePersist</code>.
 * <p/>
 * This handler shows how to implement your custom UUID generation.
 * 
 */
@Component
public class UserDBEventListener extends AbstractMongoEventListener<User> {

	@Autowired
	private TenantResolverService tenantResolverService;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SpringProfileService springProfileService;

	@Autowired
	private UserService userService;

	@Autowired
	private StatusService statusService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private StatusTaskEventService statusTaskEventService;

	@Autowired
	private TaskListService taskListService;

	@Autowired
	private TaskItemService taskItemService;

	/**
	 * Remove uses from tenant resolver after user delete command
	 */
	@Override
	public void onAfterDelete(AfterDeleteEvent<User> event) {
		final DBObject userDeleted = event.getSource();
		if (springProfileService.isSaasMode()) {
			tenantResolverService.deleteTenantResolverForUserId(userDeleted.get("userId").toString());
		}

	}

	/**
	 * Update tenant resolver in tenant management db when a user is saved in
	 * tenant db , the user email has to be unique across all tenant db
	 */
	@Override
	public void onBeforeSave(BeforeSaveEvent<User> event) {
		User userToBeSaved = event.getSource();
		if (springProfileService.isSaasMode()) {
			if (!tenantResolverService.doesTenantResolverExistForUser(userToBeSaved)) {
				tenantResolverService.saveTenantResolverForUser(userToBeSaved);
			} else {
				if (userToBeSaved.getUserId() == null) {
					throw new DuplicateKeyException("Trying to save user with same email address");
				}
			}
		}
	}

	/**
	 * Update user id after user save in tenant resolver , we need the user id
	 * to remove the tenant resolver entry when the user is deleted from tenant
	 * db
	 */
	@Override
	public void onAfterSave(AfterSaveEvent<User> event) {
		User userToBeSaved = event.getSource();
		if (springProfileService.isSaasMode()) {
			tenantResolverService.updateUserIdInTenantResolverForUser(userToBeSaved);
		}

	}

	/**
	 * Remove the user from the project list, taskList and taskItems and
	 * deleting user's statuses, comments, statusTaskEvents, taskLists and
	 * taskItems when the user is deleted - Cascade delete
	 */
	@Override
	public void onBeforeDelete(BeforeDeleteEvent<User> event) {
		final DBObject userDeleted = event.getSource();
		User user = userRepository.findOne(userDeleted.get("userId").toString());
		// getting all projectList from db by user
		List<Project> projectListOfUser = new ArrayList<Project>();
		try {
			projectListOfUser = userService.getProjects(user);
		} catch (Throwable e) {
			e.getMessage();
		}
		// removing ref of user from userList of project
		List<Project> projectsToBeSaved = new ArrayList<>();
		if (user != null && projectListOfUser != null) {
			for (Project project : projectListOfUser) {
				project.getUsers().remove(user);
				projectsToBeSaved.add(project);
			}
		}
		List<Status> statusFromDB = statusService.getStatusByUser(user);
		List<Comment> commentFromDB = commentService.getCommentByStatus(statusFromDB);
		List<StatusTaskEvent> statusTaskEvents = statusTaskEventService.findByUser(user);
		List<TaskList> taskListByOwner = taskListService.findByOwner(user);
		List<TaskItem> taskItemByOwner = taskItemService.findByOwner(user);

		commentService.delete(commentFromDB);
		statusService.delete(statusFromDB);
		projectRepository.save(projectsToBeSaved);
		statusTaskEventService.delete(statusTaskEvents);
		taskItemService.delete(taskItemByOwner);
		taskListService.delete(taskListByOwner);

		// getting all taskList from db by user
		List<TaskList> taskListFromDB = taskListService.findByUsers(user);

		// removing ref of user from userList of taskList
		List<TaskList> taskListToBeSaved = new ArrayList<TaskList>();
		if (user != null && taskListFromDB != null) {
			for (TaskList taskList : taskListFromDB) {
				taskList.getUsers().remove(user);
				taskListToBeSaved.add(taskList);
			}
		}
		taskListService.save(taskListToBeSaved);

		// getting all taskItem from db by user
		List<TaskItem> taskItemFromDB = taskItemService.findByUsers(user);

		// removing ref of user from userList of taskItem
		List<TaskItem> taskItemToBeSaved = new ArrayList<TaskItem>();
		if (user != null && taskItemFromDB != null) {
			for (TaskItem taskItem : taskItemFromDB) {
				taskItem.getUsers().remove(user);
				taskItemToBeSaved.add(taskItem);
			}
		}
		taskItemService.save(taskItemToBeSaved);
	}
}