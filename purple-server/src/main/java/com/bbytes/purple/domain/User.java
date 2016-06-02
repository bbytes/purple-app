package com.bbytes.purple.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

/**
 * User Domain Object
 * 
 * @author akshay
 */

@Data
@Document
public class User {

	@Id
	private String userId;

	@Field("name")
	private String name;

	@Field("email")
	@Indexed(unique = true)
	private String email;

	@Field("status")
	private String status;

	@Field("password")
	private String password;

	@Field("account_initialise")
	private boolean accountInitialise;

	@Field("time_zone")
	private String timeZone;

	@Field("time_preference")
	private String timePreference;
	
	@Field("email_reminder")
	private boolean emailReminder = true;

	@DBRef
	private Organization organization;

	@DBRef(lazy = true)
	private List<Project> projects = new ArrayList<>();

	// embedded
	private UserRole userRole = UserRole.NORMAL_USER_ROLE;

	@CreatedDate
	private Date creationDate;

	@LastModifiedDate
	private Date lastModified;

	public User(String name, String email) {
		this.name = name;
		this.email = email;
	}

	public static String PENDING = "Pending";
	public static String JOINED = "Joined";
	public static String DEFAULT_EMAIL_REMINDER_TIME = "1970-01-01T12:30:00.000Z";
}
