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
	
	List<TaskItem> findByTaskList(TaskList taskList){
		return taskItemRepository.findByTaskList(taskList);
	}

	List<TaskItem> findByState(TaskState state) {
		return taskItemRepository.findByState(state);
	}

	List<TaskItem> findByProject(Project project) {
		return taskItemRepository.findByProject(project);
	}

	List<TaskItem> findByProjectIn(List<Project> projects) {
		return taskItemRepository.findByProjectIn(projects);
	}

	List<TaskItem> findByOwner(User user) {
		return taskItemRepository.findByOwner(user);
	}

	List<TaskItem> findByUsersIn(List<User> users) {
		return taskItemRepository.findByUsersIn(users);
	}

	List<TaskItem> findByProjectAndUsers(Project project, User user) {
		return taskItemRepository.findByProjectAndUsers(project, user);
	}

	List<TaskItem> findByProjectInAndUsersIn(List<Project> project, List<User> user) {
		return taskItemRepository.findByProjectInAndUsersIn(project, user);
	}

	List<TaskItem> findByDueDateBetween(Date startDate, Date endDate) {
		return taskItemRepository.findByDueDateBetween(startDate, endDate);
	}

	List<TaskItem> findByDueDateBetweenAndUsers(Date startDate, Date endDate, User user) {
		return taskItemRepository.findByDueDateBetweenAndUsers(startDate, endDate, user);
	}

	List<TaskItem> findByDueDateBetweenAndUsersIn(Date startDate, Date endDate, List<User> users) {
		return taskItemRepository.findByDueDateBetweenAndUsersIn(startDate, endDate, users);
	}

	List<TaskItem> findByDueDateBetweenAndProjectIn(Date startDate, Date endDate, List<Project> projects) {
		return taskItemRepository.findByDueDateBetweenAndProjectIn(startDate, endDate, projects);
	}

	List<TaskItem> findByDueDateBetweenAndProjectInAndUsersIn(Date startDate, Date endDate, List<Project> projects, List<User> users) {
		return taskItemRepository.findByDueDateBetweenAndProjectInAndUsersIn(startDate, endDate, projects, users);
	}

}
