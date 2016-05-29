package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;

import lombok.Data;

/**
 * User DTO Object
 * 
 * @author akshay
 *
 */
@Data
public class UserDTO implements Serializable {

	private static final long serialVersionUID = -2190129400799015471L;

	private String id;

	private String email;

	private String userName;

	private String status;

	private boolean accountInitialise;

	private BaseDTO userRole;

	private String timePreference;

}
