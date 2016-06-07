package com.bbytes.purple.rest.dto.models;

import lombok.Data;

/**
 * Sign up dto Object
 * 
 * @author akshay
 */
@Data
public class SignUpRequestDTO {

	private String orgName;

	private String email;

	private String password;

}
