package com.bbytes.purple.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.TaskItem;
import com.bbytes.purple.domain.TaskList;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.enums.TaskState;
import com.bbytes.purple.repository.TaskListRepository;
import com.bbytes.purple.utils.URLUtil;

@Service
public class TaskListService extends AbstractService<TaskList, String> {

	private TaskListRepository taskListRepository;

	@Autowired
	private TaskItemService taskItemService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserService userService;

	@Autowired
	public TaskListService(TaskListRepository taskListRepository) {
		super(taskListRepository);
		this.taskListRepository = taskListRepository;
	}

	public List<TaskList> findByStateAndUsers(TaskState state, User user) {
		return taskListRepository.findByStateAndUsers(state, user);
	}

	public List<TaskList> findByStateAndOwner(TaskState state, User user) {
		return taskListRepository.findByStateAndOwner(state, user);
	}

	public List<TaskList> findByNameAndProject(String tasklistName, Project project) {
		return taskListRepository.findByNameAndProject(tasklistName, project);
	}

	public List<TaskList> findByProjectAndStateAndUsers(Project project, TaskState state, User user) {
		return taskListRepository.findByProjectAndStateAndUsers(project, state, user);
	}

	public List<TaskList> findByProjectAndStateAndOwner(Project project, TaskState state, User user) {
		return taskListRepository.findByProjectAndStateAndOwner(project, state, user);
	}

	public List<TaskList> findByProjectAndState(Project project, TaskState state) {
		return taskListRepository.findByProjectAndState(project, state);
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

	public List<TaskList> findByUsers(User user) {
		return taskListRepository.findByUsers(user);
	}

	public List<TaskList> findByOwnerOrUsers(User user, User owner) {
		return taskListRepository.findByOwnerOrUsers(user, owner);
	}

	public List<TaskList> findByProjectAndUsers(Project project, User user) {
		return taskListRepository.findByProjectAndUsers(project, user);
	}

	public List<TaskList> findByProjectAndOwnerAndStateIn(Project project, User user, List<TaskState> states) {
		return taskListRepository.findByProjectAndOwnerAndStateIn(project, user, states);
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

	public void addJiraIssueToTaskList(String taskListName, Project project, Issue issue) {
		TaskList taskList = null;
		List<TaskList> taskLists = findByNameAndProject(taskListName, project);
		if (taskLists != null && !taskLists.isEmpty()) {
			taskList = taskLists.get(0);
		} else {
			taskList = new TaskList(taskListName);
			taskList.setProject(project);
			save(taskList);
		}

		String jiraIssueURLHref = "";

		String baseURL = URLUtil.getBaseURL(issue.getSelf().toString());
		if (baseURL != null && !baseURL.isEmpty()) {
			String jiraURL = URLUtil.getJiraIssueURL(baseURL, issue.getKey());
			jiraIssueURLHref = URLUtil.getHTMLHref(jiraURL, issue.getKey() + " - " + issue.getSummary());
		}

		TaskItem itemFromDb = taskItemService.findOne(issue.getKey());
		TaskItem item;
		if (itemFromDb == null) {

			item = new TaskItem(jiraIssueURLHref, issue.getDescription());
			if (issue.getTimeTracking() != null) {
				double estimatedHours = issue.getTimeTracking().getOriginalEstimateMinutes() / 60;
				item.setEstimatedHours(estimatedHours);
				double spendHours = issue.getTimeTracking().getTimeSpentMinutes() / 60;
				item.setSpendHours(spendHours);
			}
			
			if (issue.getDueDate() != null)
				item.setDueDate(issue.getDueDate().toDate());
			
			taskList.addTaskItem(item);
			item.setTaskItemId(issue.getId().toString());
			item.setJiraIssueKey(issue.getKey());
		} else {
			item = itemFromDb;

		}

		item.setName(jiraIssueURLHref);
		item.setDesc(issue.getDescription());

		if (issue.getAssignee() != null && issue.getAssignee().getEmailAddress() != null) {
			User userAssignee = userService.getUserByEmail(issue.getAssignee().getEmailAddress());
			project.addUser(userAssignee);
			item.addUsers(userAssignee);
		}

		if (issue.getReporter() != null && issue.getReporter().getEmailAddress() != null) {
			User userReporter = userService.getUserByEmail(issue.getReporter().getEmailAddress());
			project.addUser(userReporter);
			item.setOwner(userReporter);
			taskList.setOwner(userReporter);
		}

		projectService.save(project);
		taskItemService.save(item);
		save(taskList);
	}

}
