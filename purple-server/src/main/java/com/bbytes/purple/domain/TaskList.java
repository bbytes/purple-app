package com.bbytes.purple.domain;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.bbytes.purple.enums.TaskState;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Task List Domain Object
 * 
 */

@Data
@EqualsAndHashCode(exclude = { "taskItems", "owner", "users", "project" })
@ToString(exclude = { "taskItems", "owner", "users", "project" })
@Document
public class TaskList implements Comparable<TaskList> {

	@Id
	private String taskListId;

	@Field("state")
	@Indexed
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
	@DBRef(lazy=true)
	private Set<TaskItem> taskItems = new HashSet<>();

	@DBRef(lazy=true)
	@Indexed
	private Set<User> users = new HashSet<>();

	@DBRef
	@Indexed
	private Project project;

	@DBRef
	@Indexed
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
		taskItem.setProject(getProject());
		calculateProperties();
	}

	public void removeTaskItem(TaskItem taskItem) {
		taskItems.remove(taskItem);
		calculateProperties();
	}

	public void setProject(Project project) {
		this.project = project;
		for (TaskItem taskItem : taskItems) {
			if (taskItem != null) {
				taskItem.setProject(project);
			}
		}
	}

	public void setTaskItem(Set<TaskItem> taskItems) {
		this.taskItems = taskItems;
		calculateProperties();
	}

	public Set<TaskItem> getTaskItems() {
		this.taskItems.removeAll(Collections.singleton(null));
		return this.taskItems;

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
			if (taskItem != null) {
				estimatedHours = estimatedHours + taskItem.getEstimatedHours();
				spendHours = spendHours + taskItem.getSpendHours();
				if (taskItem.getDueDate() != null && dueDate.compareTo(taskItem.getDueDate()) < 0)
					dueDate = taskItem.getDueDate();
			}
		}

		for (TaskItem taskItem : taskItems) {
			if (taskItem != null) {
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

	public void addSpendHours(double spendHours) {
		this.spendHours = this.spendHours + spendHours;
	}

	public void removeSpendHours(double spendHours) {
		this.spendHours = (this.spendHours - spendHours) <= 0 ? 0 : (this.spendHours - spendHours);
	}

}
