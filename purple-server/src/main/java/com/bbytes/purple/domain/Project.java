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

/**
 * Project Domain Object
 * 
 * @author akshay
 */

@Data
@Document
public class Project {

	@Id
	private String projectId;

	@Field("project_name")
	@Indexed(unique = true)
	private String projectName;

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
	}

	public void addUser(User userToBeAdded) {
		if (user != null) {
			user.add(userToBeAdded);
			// userToBeAdded.addProject(this);
		}
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
