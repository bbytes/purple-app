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

import lombok.Data;

/**
 * Task Item Domain Object
 * 
 */

@Data
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
	private double spentHours;

	@Field("due_date")
	private Date dueDate;

	@DBRef
	private Project project;

	@DBRef
	private TaskList taskList;

	@DBRef
	private User owner;

	@DBRef
	private Set<User> users = new HashSet<>();

	@CreatedDate
	private Date creationDate;

	@LastModifiedDate
	private Date lastModified;

	public TaskItem(TaskList taskList, String name, String desc, double estimatedHours, Date dueDate) {
		this.taskList = taskList;
		taskList.addTaskItem(this);
		
		this.name = name;
		this.desc = desc;
		this.estimatedHours = estimatedHours;
		this.dueDate = dueDate;
	}

	public void addUsers(User user) {
		users.add(user);
		taskList.addUsers(user);
	}

	public void removeUsers(User user) {
		users.remove(user);
		taskList.addUsers(user);
	}

	@Override
	public int compareTo(TaskItem taskList) {
		return getDueDate().compareTo(taskList.getDueDate());
	}

}
