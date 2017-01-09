package com.bbytes.purple.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
	private MongoTemplate mongoTemplate;


	@Autowired
	public TaskItemService(TaskItemRepository taskItemRepository) {
		super(taskItemRepository);
		this.taskItemRepository = taskItemRepository;
	}

	public TaskItem findByJiraIssueKey(String jiraIssueKey){
		return taskItemRepository.findByJiraIssueKey(jiraIssueKey);
	}
	
	public List<TaskItem> findByTaskList(TaskList taskList) {
		return taskItemRepository.findByTaskList(taskList);
	}

	public List<TaskItem> findByTaskListAndUsers(TaskList taskList, User user) {
		return taskItemRepository.findByTaskListAndUsers(taskList, user);
	}
	
	public List<TaskItem> findByTaskListAndUsersOrOwner(TaskList taskList, User user, User owner) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		
		Criteria criteriaOR = new Criteria();
		criteriaOR.orOperator(Criteria.where("users").is(user),Criteria.where("owner").is(owner));
		
		criteria.andOperator(Criteria.where("taskList").is(taskList),criteriaOR);
		query.addCriteria(criteria);
	
		List<TaskItem> items = mongoTemplate.find(query,TaskItem.class);
		return items;
	}
	
	public List<TaskItem> findByTaskListAndOwner(TaskList taskList, User user) {
		return taskItemRepository.findByTaskListAndOwner(taskList, user);
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

	public List<TaskItem> findByProjectAndUsersOrOwner(Project project, User user,User owner) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		
		Criteria criteriaOR = new Criteria();
		criteriaOR.orOperator(Criteria.where("users").is(user),Criteria.where("owner").is(owner));
		
		criteria.andOperator(Criteria.where("project").is(project),criteriaOR);
		query.addCriteria(criteria);
		List<TaskItem> items = mongoTemplate.find(query,TaskItem.class);
		return items;
	}
	
	public List<TaskItem> findByProjectAndUsers(Project project, User user) {
		return taskItemRepository.findByProjectAndUsers(project, user);
	}
	
	public List<TaskItem> findByProjectAndOwner(Project project, User owner) {
		return taskItemRepository.findByProjectAndOwner(project, owner);
	}

	public List<TaskItem> findByProjectAndUsersAndStateIn(Project project, User user, List<TaskState> states) {
		return taskItemRepository.findByProjectAndUsersAndStateIn(project, user, states);
	}
	
	public List<TaskItem> findByProjectAndOwnerAndStateIn(Project project, User user, List<TaskState> states) {
		return taskItemRepository.findByProjectAndOwnerAndStateIn(project, user, states);
	}

	public List<TaskItem> findByProjectInAndUsersIn(List<Project> project, List<User> user) {
		return taskItemRepository.findByProjectInAndUsersIn(project, user);
	}

	public List<TaskItem> findByDueDateBetween(Date startDate, Date endDate) {
		return taskItemRepository.findByDueDateBetween(startDate, endDate);
	}

	public List<TaskItem> findByStateAndUsersOrOnwer(TaskState state, User user,User owner) {
		
		Query query = new Query();
		Criteria criteria = new Criteria();
		
		Criteria criteriaOR = new Criteria();
		criteriaOR.orOperator(Criteria.where("users").is(user),Criteria.where("owner").is(owner));
		
		criteria.andOperator(Criteria.where("state").is(state),criteriaOR);
		query.addCriteria(criteria);
		List<TaskItem> items = mongoTemplate.find(query,TaskItem.class);
		return items;
	}
	
	public List<TaskItem> findByStateAndUsers(TaskState state, User user) {
		return taskItemRepository.findByStateAndUsers(state, user);
	}
	
	public List<TaskItem> findByStateAndOwner(TaskState state, User user) {
		return taskItemRepository.findByStateAndOwner(state, user);
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

	public List<TaskItem> findByDueDateBetweenAndProjectInAndUsersIn(Date startDate, Date endDate, List<Project> projects,
			List<User> users) {
		return taskItemRepository.findByDueDateBetweenAndProjectInAndUsersIn(startDate, endDate, projects, users);
	}

	public List<TaskItem> findByTaskListAndState(TaskList taskList, TaskState taskState) {
		return taskItemRepository.findByTaskListAndState(taskList, taskState);
	}

	public List<TaskItem> findByTaskListAndStateAndUsersOrOwner(TaskList taskList, TaskState taskState, User user,User owner) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		
		Criteria criteriaOR = new Criteria();
		criteriaOR.orOperator(Criteria.where("users").is(user),Criteria.where("owner").is(owner));
		
		criteria.andOperator(Criteria.where("taskList").is(taskList),Criteria.where("state").is(taskState),criteriaOR);
		query.addCriteria(criteria);
		List<TaskItem> items = mongoTemplate.find(query,TaskItem.class);
		return items;
	}
	
	public List<TaskItem> findByTaskListAndStateAndUsers(TaskList taskList, TaskState taskState, User user) {
		return taskItemRepository.findByTaskListAndStateAndUsers(taskList, taskState, user);
	}
	
	public List<TaskItem> findByTaskListAndStateAndOwner(TaskList taskList, TaskState taskState, User user) {
		return taskItemRepository.findByTaskListAndStateAndOwner(taskList, taskState, user);
	}

	public List<TaskItem> findByProjectAndStateAndUsersOrOwner(Project project, TaskState taskState, User user,User owner) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		
		Criteria criteriaOR = new Criteria();
		criteriaOR.orOperator(Criteria.where("users").is(user),Criteria.where("owner").is(owner));
		
		criteria.andOperator(Criteria.where("project").is(project),Criteria.where("state").is(taskState),criteriaOR);
		query.addCriteria(criteria);
		List<TaskItem> items = mongoTemplate.find(query,TaskItem.class);
		return items;
	}
	
	public List<TaskItem> findByProjectAndStateAndUsers(Project project, TaskState taskState, User user) {
		return taskItemRepository.findByProjectAndStateAndUsers(project, taskState, user);
	}
	
	public List<TaskItem> findByProjectAndStateAndOwner(Project project, TaskState taskState, User user) {
		return taskItemRepository.findByProjectAndStateAndOwner(project, taskState, user);
	}

}
