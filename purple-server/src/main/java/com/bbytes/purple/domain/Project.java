package com.bbytes.purple.domain;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project Domain Object
 * 
 * @author akshay
 */

@Data
@Document
@NoArgsConstructor
public class Project {

	@Id
	private String projectId;

	@Field("project_name")
	@Indexed(unique = true)
	private String projectName;

	@Field("project_key")
	private String projectKey;

	@Field("project_owner")
	@DBRef
	private User projectOwner;

	@DBRef
	private Organization organization;

	@DBRef(lazy = true)
	private Set<User> user = new HashSet<User>();

	@CreatedDate
	private Date creationDate;

	@LastModifiedDate
	private Date lastModified;

	public Project(String projectName) {
		this.projectName = projectName;
		this.projectKey = projectName;
	}

	public Project(String projectName, String projectKey) {
		this.projectName = projectName;
		this.projectKey = projectKey;
	}

	public void addUser(User userToBeAdded) {
		if (user != null) {
			user.add(userToBeAdded);
			// userToBeAdded.addProject(this);
		}
	}

	public Set<User> getUsers() {
		return this.user;
	}

	public void setUsers(Set<User> users) {
		this.user = users;
	}

	public void addUser(Collection<User> userList) {
		// clear old user list
		// removeUser(getUser());

		// add new user list
		for (User user : userList) {
			addUser(user);
		}
	}

	// public void removeUser(User userToBeRemove) {
	//
	// if (getUser() != null) {
	// getUser().remove(userToBeRemove);
	// userToBeRemove.removeProject(this);
	// }
	// }
	//
	// public void removeUser(Collection<User> userList) {
	//
	// for (User user : userList) {
	// removeUser(user);
	// }
	// }
}
