package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class TaskListDTO implements Serializable {

	private static final long serialVersionUID = 5187445361068841595L;

	private String taskListId;

	private String name;

	private String projectId;
	
	private String timeData;
}
