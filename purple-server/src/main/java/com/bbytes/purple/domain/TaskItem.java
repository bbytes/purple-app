package com.bbytes.purple.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.bbytes.purple.enums.TaskState;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Task Item Domain Object
 * 
 */

@Data
@EqualsAndHashCode(exclude = { "taskList", "owner", "users", "project" })
@ToString(exclude = { "taskList", "owner", "users", "project" })
@Document
public class TaskItem implements Comparable<TaskItem> {

	@Id
	private String taskItemId;

	@Field("state")
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

	@DBRef
	private Project project;

	@JsonBackReference
	@DBRef
	private TaskList taskList;

	@DBRef
	private User owner;

	@DBRef
	private Set<User> users;

	@CreatedDate
	private Date creationDate;

	@LastModifiedDate
	private Date lastModified;

	public TaskItem(String name, String desc, double estimatedHours, Date dueDate) {
		this.name = name;
		this.desc = desc;
		this.estimatedHours = estimatedHours;
		this.dueDate = dueDate;
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
		taskList.addUsers(user);
	}

	public void setOwner(User owner) {
		this.owner = owner;
		addUsers(owner);
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

	public void addSpendHours(double spendHours) {

		this.spendHours = this.spendHours + spendHours;
	}
	
	public void removeSpendHours(double spendHours) {

		this.spendHours = this.spendHours - spendHours;
	}

}
