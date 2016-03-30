package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;

import lombok.Data;

/**
 * Status DTO Object
 * 
 * @author akshay
 *
 */
@Data
public class StatusDTO implements Serializable {

	private static final long serialVersionUID = 8499276346409176127L;

	private String statusId;

	private String projectId;

	private String projectName;

	private String userName;

	private double hours;

	private String workedOn;

	private String workingOn;

	private String blockers;
	
	private String dateTime;
	
	private String time;

}
