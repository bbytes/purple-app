package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bbytes.purple.domain.User;

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

	private long commentCount;

	private Map<String, Map<String, String>> taskDataMap;

	private Set<User> mentionUser = new HashSet<User>();

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
