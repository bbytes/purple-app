package com.bbytes.purple.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.TaskList;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.enums.TaskState;
import com.bbytes.purple.repository.TaskListRepository;

@Service
public class TaskListService extends AbstractService<TaskList, String> {

	private TaskListRepository taskListRepository;

	@Autowired
	public TaskListService(TaskListRepository taskListRepository) {
		super(taskListRepository);
		this.taskListRepository = taskListRepository;
	}

	public List<TaskList> findByStateAndUsers(TaskState state, User user) {
		return taskListRepository.findByStateAndUsers(state, user);
	}

	public List<TaskList> findByProject(Project project) {
		return taskListRepository.findByProject(project);
	}

	public List<TaskList> findByProjectIn(List<Project> projects) {
		return taskListRepository.findByProjectIn(projects);
	}

	public List<TaskList> findByOwner(User user) {
		return taskListRepository.findByOwner(user);
	}

	public List<TaskList> findByUsersIn(List<User> users) {
		return taskListRepository.findByUsersIn(users);
	}

	public List<TaskList> findByProjectAndUsers(Project project, User user) {
		return taskListRepository.findByProjectAndUsers(project, user);
	}

	public List<TaskList> findByProjectInAndUsersIn(List<Project> project, List<User> user) {
		return taskListRepository.findByProjectInAndUsersIn(project, user);
	}

	public List<TaskList> findByDueDateBetween(Date startDate, Date endDate) {
		return taskListRepository.findByDueDateBetween(startDate, endDate);
	}

	public List<TaskList> findByDueDateBetweenAndUsers(Date startDate, Date endDate, User user) {
		return taskListRepository.findByDueDateBetweenAndUsers(startDate, endDate, user);
	}

	public List<TaskList> findByDueDateBetweenAndUsersIn(Date startDate, Date endDate, List<User> users) {
		return taskListRepository.findByDueDateBetweenAndUsersIn(startDate, endDate, users);
	}

	public List<TaskList> findByDueDateBetweenAndProjectIn(Date startDate, Date endDate, List<Project> projects) {
		return taskListRepository.findByDueDateBetweenAndProjectIn(startDate, endDate, projects);
	}

	public List<TaskList> findByDueDateBetweenAndProjectInAndUsersIn(Date startDate, Date endDate, List<Project> projects,
			List<User> users) {
		return taskListRepository.findByDueDateBetweenAndProjectInAndUsersIn(startDate, endDate, projects, users);
	}

}
