package com.bbytes.purple.domain;

import java.util.Date;

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
 * 
 * @author akshay
 */

@Data
@Document
public class Status implements Comparable<Status> {
	
	@Id
	private String statusId;
	
	@Field("working_on")
	private String workingOn;
	
	@Field("worked_on")
	private String workedOn;
	
	@Field("blockers")
	private String blockers;
	
	@Field("hours")
	private double hours;
	
	@Field("date_time")
	private Date dateTime;
	
	@DBRef
	private Project project;
	
	@DBRef
	private User user;
	
	@CreatedDate
	private DateTime creationDate;
	
	@LastModifiedDate
	private DateTime lastModified;

	public Status(String workingOn, String workedOn, double hours, Date dateTime) {

		this.workingOn = workingOn;
		this.workedOn = workedOn;
		this.hours = hours;
		this.dateTime = dateTime;
	}

	@Override
	public int compareTo(Status status) {
		return getDateTime().compareTo(status.getDateTime());
	}

}
