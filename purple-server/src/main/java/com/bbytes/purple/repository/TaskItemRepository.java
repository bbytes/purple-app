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

	List<TaskItem> findByProject(Project project);

	List<TaskItem> findByProjectIn(List<Project> project);

	List<TaskItem> findByOwner(User user);

	List<TaskItem> findByUsersIn(List<User> user);
	
	List<TaskItem> findByStateAndUsers(TaskState state, User user);

	List<TaskItem> findByProjectAndUsers(Project project, User user);

	List<TaskItem> findByProjectInAndUsersIn(List<Project> project, List<User> user);

	List<TaskItem> findByDueDateBetween(Date startDate, Date endDate);

	List<TaskItem> findByDueDateBetweenAndUsers(Date startDate, Date endDate, User user);

	List<TaskItem> findByDueDateBetweenAndUsersIn(Date startDate, Date endDate, List<User> user);

	List<TaskItem> findByDueDateBetweenAndProjectIn(Date startDate, Date endDate, List<Project> project);

	List<TaskItem> findByDueDateBetweenAndProjectInAndUsersIn(Date startDate, Date endDate, List<Project> project, List<User> user);

	List<TaskItem> findByTaskListAndState(TaskList taskList, TaskState taskState);

	List<TaskItem> findByProjectAndStateAndUsers(Project project, TaskState taskState, User user);

}
