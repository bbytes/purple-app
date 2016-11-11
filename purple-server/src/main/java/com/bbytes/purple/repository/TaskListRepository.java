package com.bbytes.purple.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.TaskList;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.enums.TaskState;

public interface TaskListRepository extends MongoRepository<TaskList, String> {

	List<TaskList> findByStateAndUsers(TaskState state, User user);

	List<TaskList> findByProjectAndStateAndUsers(Project project, TaskState state, User user);

	List<TaskList> findByProjectAndState(Project project, TaskState state);

	List<TaskList> findByProject(Project project);

	List<TaskList> findByProjectIn(List<Project> projects);

	List<TaskList> findByOwner(User user);

	List<TaskList> findByUsersIn(List<User> users);

	List<TaskList> findByProjectAndUsers(Project project, User user);

	List<TaskList> findByProjectInAndUsersIn(List<Project> project, List<User> users);

	List<TaskList> findByDueDateBetween(Date startDate, Date endDate);

	List<TaskList> findByDueDateBetweenAndUsers(Date startDate, Date endDate, User user);

	List<TaskList> findByDueDateBetweenAndUsersIn(Date startDate, Date endDate, List<User> users);

	List<TaskList> findByDueDateBetweenAndProjectIn(Date startDate, Date endDate, List<Project> projects);

	List<TaskList> findByDueDateBetweenAndProjectInAndUsersIn(Date startDate, Date endDate, List<Project> projects, List<User> users);

}
