package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Users And Project DTO Object
 * 
 * @author akshay
 *
 */
public class UsersAndProjectsDTO implements Serializable {

	private static final long serialVersionUID = -8445449919059016201L;

	List<String> projectList = Collections.<String> emptyList();

	List<String> userList = Collections.<String> emptyList();

}
