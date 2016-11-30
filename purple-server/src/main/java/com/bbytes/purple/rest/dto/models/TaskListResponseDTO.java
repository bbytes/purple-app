package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class TaskListResponseDTO implements Serializable {

	private static final long serialVersionUID = 4846222951455253656L;

	private String taskItemId;

	private String taskListId;

	private String taskListName;

	private String taskItemName;

	private String desc;

	private double estimatedHours;

	private double spendHours;

	private Date dueDate;

	private String state;

}