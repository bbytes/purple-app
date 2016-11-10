package com.bbytes.purple.domain;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

	@Field("comment_count")
	private long commentCount;

	@Field("date_time")
	private Date dateTime;

	@DBRef
	private Project project;

	@DBRef
	private User user;

	@CreatedDate
	private Date creationDate;

	@LastModifiedDate
	private Date lastModified;

	@DBRef(lazy = true)
	private Set<User> mentionUser = new HashSet<User>();

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

	public void addMentionUser(User userToBeAdded) {
		if (mentionUser != null) {
			mentionUser.add(userToBeAdded);
		}
	}

	public void addMentionUser(Collection<User> userList) {

		for (User user : userList) {
			addMentionUser(user);
		}
	}

}
