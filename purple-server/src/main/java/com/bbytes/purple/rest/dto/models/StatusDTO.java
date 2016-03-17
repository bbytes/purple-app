package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;

import com.bbytes.purple.domain.Project;

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

	private Project project;

	private int hours;

	private String workedOn;

	private String workingOn;

	private String blockers;

}
