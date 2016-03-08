package com.bbytes.purple.domain;

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
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
	@Indexed
	private String email;
	
	@Field("status")
	private String status;
	
	@Field("password")
	private String password;
	
	@DBRef
	private Organization organization;
	
	@DBRef(lazy=true)
	private List<Project> projects = Collections.<Project>emptyList();
	
	@DBRef
	private UserRole userRole;

	@CreatedDate
	private DateTime creationDate;
	
	@LastModifiedDate
	private DateTime lastModified;

	public User(String name, String email) {
		this.name = name;
		this.email = email;
	}	
	
}
