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
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

/**
 * Task List Domain Object
 * 
 */

@Data
@Document
public class TaskList implements Comparable<TaskList> {

	@Id
	private String taskListId;

	@Field("state")
	private TaskState state;

	@Field("name")
	private String name;

	@Field("estimated_hours")
	private double estimatedHours;

	@Field("spent_hours")
	private double spentHours;

	@Field("due_date")
	private Date dueDate;

	@JsonManagedReference
	@DBRef
	private Set<TaskItem> taskItems = new HashSet<>();

	@DBRef
	private Set<User> users = new HashSet<>();

	@DBRef
	private Project project;

	@DBRef
	private User owner;

	@CreatedDate
	private Date creationDate;

	@LastModifiedDate
	private Date lastModified;

	public TaskList(String name) {
		this.name = name;
	}

	public void addTaskItem(TaskItem taskItem) {
		taskItems.add(taskItem);
		taskItem.setTaskList(this);
	}

	public void removeTaskItem(TaskItem taskItem) {
		taskItems.remove(taskItem);
	}

	public void addUsers(User user) {
		users.add(user);
	}

	public void removeUsers(User user) {
		users.remove(user);
	}

	public void setOwner(User owner) {
		this.owner = owner;
		addUsers(owner);
	}

	@Override
	public int compareTo(TaskList taskList) {
		return getDueDate().compareTo(taskList.getDueDate());
	}

}
