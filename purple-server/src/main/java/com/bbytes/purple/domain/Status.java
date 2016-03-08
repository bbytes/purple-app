package com.bbytes.purple.domain;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

/**
 * Status Domain Object
 * @author akshay
 */

@Data
@Document
public class Status {
	
	@Id
	private String statusId;
	
	@Field("working_on")
	private String workingOn;
	
	@Field("worked_on")
	private String workedOn;
	
	@Field("hours")
	private int hours;
	
	@Field("blockers")
	private String blockers;
	
	@Field("date_time")
	private DateTime dateTime;
	
	@DBRef
	private Project project;
	
	@DBRef
	private User user;
	
	@CreatedDate
	private DateTime creationDate;
	
	@LastModifiedDate
	private DateTime lastModified;

	public Status(String workingOn, String workedOn, int hours, DateTime dateTime) {

		this.workingOn = workingOn;
		this.workedOn = workedOn;
		this.hours = hours;
		this.dateTime = dateTime;
	}	
	
}
