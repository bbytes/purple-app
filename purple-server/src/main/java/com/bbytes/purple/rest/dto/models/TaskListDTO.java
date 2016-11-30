package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class TaskListDTO implements Serializable {

	private static final long serialVersionUID = 5187445361068841595L;

	private String taskListId;

	private String name;

	private String projectId;

	private String projectName;

	private double spendHours;

	private double estimatedHours;

	private List<TaskItemDTO> taskItems;

	private int taskItemsForGivenState;

	private String ownerEmail;
}
