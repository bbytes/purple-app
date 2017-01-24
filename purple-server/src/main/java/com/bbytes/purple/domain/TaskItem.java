package com.bbytes.purple.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.bbytes.purple.enums.TaskState;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Task Item Domain Object
 * 
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = { "taskList", "owner", "users", "project" })
@ToString(exclude = { "taskList", "owner", "users", "project" })
@Document
public class TaskItem implements Comparable<TaskItem> {

	@Id
	private String taskItemId;

	@Field("state")
	@Indexed
	private TaskState state = TaskState.YET_TO_START;

	@Field("name")
	private String name;

	@Field("desc")
	private String desc;

	@Field("estimated_hours")
	private double estimatedHours;

	@Field("spent_hours")
	private double spendHours;

	@Field("due_date")
	private Date dueDate;

	@Field("jira_issue_key")
	@Indexed
	private String jiraIssueKey;

	@DBRef
	@Indexed
	private Project project;

	@JsonBackReference
	@DBRef
	@Indexed
	private TaskList taskList;

	@DBRef
	@Indexed
	private User owner;

	@DBRef
	@Indexed
	private Set<User> users;
	
	@Field("dirty")
	private Boolean dirty = true;

	@CreatedDate
	private Date creationDate;

	@LastModifiedDate
	private Date lastModified;

	public TaskItem(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
	
	public TaskItem(String name, String desc, double estimatedHours, Date dueDate) {
		this(name, desc);
		this.dueDate = dueDate;
		this.estimatedHours = estimatedHours;
	}
	

	public void addUsers(User user) {
		if (users == null) {
			users = new HashSet<User>();
		}
		users.add(user);
		taskList.addUsers(user);
	}

	public void removeUsers(User user) {
		users.remove(user);
		taskList.removeUsers(user);
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Override
	public int compareTo(TaskItem taskList) {
		return getDueDate().compareTo(taskList.getDueDate());
	}

	public void addUsers(Set<User> users) {
		if (users == null) {
			users = new HashSet<User>();
		}
		this.users.addAll(users);
		taskList.addUsers(users);

	}

	public void setState(TaskState state){
		this.state = state;
		this.dirty = true;
	}
	public void setSpendHours(double spendHours){
		this.spendHours =spendHours;
		this.dirty = true;
	}
	
	public void addSpendHours(double spendHours) {
		this.spendHours = this.spendHours + spendHours;
		this.dirty = true;
	}

	public void removeSpendHours(double spendHours) {
		this.spendHours = (this.spendHours - spendHours) <= 0 ? 0 : (this.spendHours - spendHours);
		this.dirty = true;
	}

	public boolean isJiraIssueTaskItem() {
		return jiraIssueKey == null ? false : true;
	}

}
