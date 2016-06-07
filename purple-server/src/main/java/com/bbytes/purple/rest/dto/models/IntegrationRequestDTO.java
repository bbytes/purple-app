package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;

import lombok.Data;

/**
 * Integration DTO
 * 
 * @author akshay
 *
 */

@Data
public class IntegrationRequestDTO implements Serializable {

	private static final long serialVersionUID = -5299087285098567662L;

	private String userName;

	private String password;

	private String jiraBaseUrl;

}
