package com.bbytes.purple.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.TaskItem;
import com.bbytes.purple.domain.TaskList;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.enums.TaskState;
import com.bbytes.purple.repository.TaskItemRepository;

@Service
public class TaskItemService extends AbstractService<TaskItem, String> {

	private TaskItemRepository taskItemRepository;

	@Autowired
	public TaskItemService(TaskItemRepository taskItemRepository) {
		super(taskItemRepository);
		this.taskItemRepository = taskItemRepository;
	}

	public List<TaskItem> findByTaskList(TaskList taskList) {
		return taskItemRepository.findByTaskList(taskList);
	}
	
	public List<TaskItem> findByTaskListAndUsers(TaskList taskList,User user) {
		return taskItemRepository.findByTaskListAndUsers(taskList, user);
	}

	public List<TaskItem> findByState(TaskState state) {
		return taskItemRepository.findByState(state);
	}

	public List<TaskItem> findByProject(Project project) {
		return taskItemRepository.findByProject(project);
	}

	public List<TaskItem> findByProjectIn(List<Project> projects) {
		return taskItemRepository.findByProjectIn(projects);
	}

	public List<TaskItem> findByOwner(User user) {
		return taskItemRepository.findByOwner(user);
	}

	public List<TaskItem> findByUsers(User user) {
		return taskItemRepository.findByUsers(user);
	}

	public List<TaskItem> findByUsersIn(List<User> users) {
		return taskItemRepository.findByUsersIn(users);
	}

	public List<TaskItem> findByProjectAndUsers(Project project, User user) {
		return taskItemRepository.findByProjectAndUsers(project, user);
	}

	public List<TaskItem> findByProjectAndUsersAndStateIn(Project project, User user, List<TaskState> states) {
		return taskItemRepository.findByProjectAndUsersAndStateIn(project, user, states);
	}

	public List<TaskItem> findByProjectInAndUsersIn(List<Project> project, List<User> user) {
		return taskItemRepository.findByProjectInAndUsersIn(project, user);
	}

	public List<TaskItem> findByDueDateBetween(Date startDate, Date endDate) {
		return taskItemRepository.findByDueDateBetween(startDate, endDate);
	}

	public List<TaskItem> findByStateAndUsers(TaskState state, User user) {
		return taskItemRepository.findByStateAndUsers(state, user);
	}

	public List<TaskItem> findByDueDateBetweenAndUsers(Date startDate, Date endDate, User user) {
		return taskItemRepository.findByDueDateBetweenAndUsers(startDate, endDate, user);
	}

	public List<TaskItem> findByDueDateBetweenAndUsersIn(Date startDate, Date endDate, List<User> users) {
		return taskItemRepository.findByDueDateBetweenAndUsersIn(startDate, endDate, users);
	}

	public List<TaskItem> findByDueDateBetweenAndProjectIn(Date startDate, Date endDate, List<Project> projects) {
		return taskItemRepository.findByDueDateBetweenAndProjectIn(startDate, endDate, projects);
	}

	public List<TaskItem> findByDueDateBetweenAndProjectInAndUsersIn(Date startDate, Date endDate,
			List<Project> projects, List<User> users) {
		return taskItemRepository.findByDueDateBetweenAndProjectInAndUsersIn(startDate, endDate, projects, users);
	}

	public List<TaskItem> findByTaskListAndState(TaskList taskList, TaskState taskState) {
		return taskItemRepository.findByTaskListAndState(taskList, taskState);
	}
	
	public List<TaskItem> findByTaskListAndStateAndUsers(TaskList taskList, TaskState taskState,User user) {
		return taskItemRepository.findByTaskListAndStateAndUsers(taskList, taskState,user);
	}

	public List<TaskItem> findByProjectAndStateAndUsers(Project project, TaskState taskState, User user) {
		return taskItemRepository.findByProjectAndStateAndUsers(project, taskState, user);
	}

}
