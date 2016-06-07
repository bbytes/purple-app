package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

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

	private List<String> users = Collections.<String> emptyList();

	private List<UserDTO> userList = Collections.<UserDTO> emptyList();

	private long usersCount = 0;
}
