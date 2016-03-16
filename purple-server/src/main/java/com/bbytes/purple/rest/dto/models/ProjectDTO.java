package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;
import java.util.List;

import com.bbytes.purple.domain.User;

import lombok.Data;

@Data
public class ProjectDTO implements Serializable{

	private static final long serialVersionUID = -4283248621429252655L;
	
	private String projectName;
	
	private String timePreference;
	
	private List<User> users;

}
