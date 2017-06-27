package com.bbytes.purple.domain;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.ToString;

/**
 * User Domain Object
 * 
 * @author akshay
 */

@Data
@Document
@ToString
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

	@Field("email_notification_state")
	private boolean emailNotificationState = true;

	@Field("disable_state")
	private boolean disableState = false;

	@Field("mark_delete")
	private boolean markDelete = false;

	@Field("device_token")
	private String deviceToken;

	@Field("mark_delete_date")
	private Date markDeleteDate;

	@Field("view_type")
	private String viewType = TIMELINE_VIEW;

	@DBRef
	private Organization organization;

	// embedded
	private UserRole userRole = UserRole.NORMAL_USER_ROLE;

	@CreatedDate
	private Date creationDate;

	@LastModifiedDate
	private Date lastModified;

	public User(String name, String email) {
		this.name = name;
		this.email = email;
		
		if (this.email != null)
			this.email = this.email.toLowerCase();
	}

	public static String PENDING = "Pending";
	public static String JOINED = "Joined";
	public static String DEFAULT_EMAIL_REMINDER_TIME = "1970-01-01T12:30:00.000Z";

	public static String TIMELINE_VIEW = "Timeline";
	public static String TABLE_VIEW = "Table";

}
