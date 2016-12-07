package com.bbytes.purple.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.TaskItem;
import com.bbytes.purple.domain.TaskList;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.enums.TaskState;

public interface TaskItemRepository extends MongoRepository<TaskItem, String> {

	List<TaskItem> findByState(TaskState state);

	List<TaskItem> findByTaskList(TaskList taskList);
	
	List<TaskItem> findByTaskListAndUsers(TaskList taskList,User user);
	
	List<TaskItem> findByTaskListAndOwner(TaskList taskList,User owner);

	List<TaskItem> findByProject(Project project);

	List<TaskItem> findByProjectIn(List<Project> project);

	List<TaskItem> findByOwner(User user);

	List<TaskItem> findByUsers(User user);

	List<TaskItem> findByUsersIn(List<User> user);

	List<TaskItem> findByStateAndUsers(TaskState state, User user);
	
	List<TaskItem> findByStateAndOwner(TaskState state, User owner);
	
	List<TaskItem> findByProjectAndOwner(Project project, User owner);

	List<TaskItem> findByProjectAndUsers(Project project, User user);

	List<TaskItem> findByProjectAndUsersAndStateIn(Project project, User user, List<TaskState> states);
	
	List<TaskItem> findByProjectAndOwnerAndStateIn(Project project, User user, List<TaskState> states);

	List<TaskItem> findByProjectInAndUsersIn(List<Project> project, List<User> user);

	List<TaskItem> findByDueDateBetween(Date startDate, Date endDate);

	List<TaskItem> findByDueDateBetweenAndUsers(Date startDate, Date endDate, User user);

	List<TaskItem> findByDueDateBetweenAndUsersIn(Date startDate, Date endDate, List<User> user);

	List<TaskItem> findByDueDateBetweenAndProjectIn(Date startDate, Date endDate, List<Project> project);

	List<TaskItem> findByDueDateBetweenAndProjectInAndUsersIn(Date startDate, Date endDate, List<Project> project,
			List<User> user);

	List<TaskItem> findByTaskListAndState(TaskList taskList, TaskState taskState);
	
	List<TaskItem> findByTaskListAndStateAndUsers(TaskList taskList, TaskState taskState,User user);
	
	List<TaskItem> findByTaskListAndStateAndOwner(TaskList taskList, TaskState taskState,User owner);

	List<TaskItem> findByProjectAndStateAndUsers(Project project, TaskState taskState, User user);
	
	List<TaskItem> findByProjectAndStateAndOwner(Project project, TaskState taskState, User owner);

}
