package com.bbytes.purple.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.bbytes.purple.enums.TaskState;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Task List Domain Object
 * 
 */

@Data
@EqualsAndHashCode(exclude = { "taskItems", "owner", "users" })
@Document
public class TaskList implements Comparable<TaskList> {

	@Id
	private String taskListId;

	@Field("state")
	private TaskState state = TaskState.YET_TO_START;

	@Field("name")
	private String name;

	@Field("estimated_hours")
	private double estimatedHours;

	@Field("spent_hours")
	private double spendHours;

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
		calculateProperties();
	}

	public void removeTaskItem(TaskItem taskItem) {
		taskItems.remove(taskItem);
		calculateProperties();
	}

	public void setTaskItem(Set<TaskItem> taskItems) {
		this.taskItems = taskItems;
		calculateProperties();
	}

	public void addUsers(User user) {
		users.add(user);
	}

	public void addUsers(Set<User> users) {
		users.addAll(users);
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

	private void calculateProperties() {
		state = null;
		estimatedHours = 0;
		spendHours = 0;
		dueDate = DateTime.now().toDate();

		for (TaskItem taskItem : taskItems) {
			estimatedHours = estimatedHours + taskItem.getEstimatedHours();
			spendHours = spendHours + taskItem.getSpendHours();
			if (taskItem.getDueDate() != null && dueDate.compareTo(taskItem.getDueDate()) < 0)
				dueDate = taskItem.getDueDate();
		}

		for (TaskItem taskItem : taskItems) {
			if (taskItem.getState().equals(TaskState.YET_TO_START)) {
				state = TaskState.YET_TO_START;
				break;
			}
			if (state == null) {
				if (taskItem.getState().equals(TaskState.IN_PROGRESS)) {
					state = TaskState.IN_PROGRESS;
					break;
				}
			}
			if (state == null) {
				if (taskItem.getState().equals(TaskState.COMPLETED)) {
					state = TaskState.COMPLETED;
					break;
				}
			}

		}

	}

}
