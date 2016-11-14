package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bbytes.purple.domain.User;

import lombok.Data;

/**
 * @author aditya
 *
 */

@Data
public class TaskItemDTO implements Serializable {

	private static final long serialVersionUID = 1990692949942837646L;
	
	private String taskItemId;

	private String name;

	private String desc;

	private double estimatedHours;

	private double spendHours;

	private Date dueDate;
	
	private List<String> userIds = new ArrayList<String>();

	private List<User> users = new ArrayList<User>();

}
