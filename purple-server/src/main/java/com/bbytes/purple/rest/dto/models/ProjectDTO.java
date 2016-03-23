package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.bbytes.purple.domain.User;

import lombok.Data;

/**
 * Project DTO Object
 * 
 * @author akshay
 *
 */
@Data
public class ProjectDTO implements Serializable {

	private static final long serialVersionUID = -4283248621429252655L;

	private String projectId;

	private String projectName;

	private String timePreference;

	private List<String> users = Collections.<String> emptyList();

	private List<User> userList = Collections.<User> emptyList();

	private long usersCount = 0;
}
