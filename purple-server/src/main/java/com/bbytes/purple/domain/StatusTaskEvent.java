package com.bbytes.purple.domain;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.bbytes.purple.enums.TaskState;

import lombok.Data;

/**
 * Status Task Event Domain Object
 * 
 * @author Akshay
 */

@Data
@Document
public class StatusTaskEvent {

	@Id
	private String statusTaskEventId;

	@DBRef
	private TaskItem taskItem;

	@DBRef
	private Status status;

	@Field("spend_hours")
	private double spendHours;

	@Field("remaining_hours")
	private double remainingHours;

	@Field("state")
	private TaskState state;

	@DBRef
	private User eventOwner;

	@CreatedDate
	private Date creationDate;

	public StatusTaskEvent(TaskItem taskItem, Status status,  User eventOwner) {
		super();
		this.taskItem = taskItem;
		this.status = status;
		this.eventOwner = eventOwner;
	}
	
}
