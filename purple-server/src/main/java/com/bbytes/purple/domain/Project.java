package com.bbytes.purple.domain;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

/**
 * Project Domain Object
 * 
 * @author akshay
 */

@Data
@Document
public class Project {

	@Id
	private String projectId;

	@Field("project_name")
	@Indexed(unique = true)
	private String projectName;

	@DBRef
	private Organization organization;

	@DBRef(lazy = true)
	private List<User> user = new ArrayList<>();

	@CreatedDate
	private DateTime creationDate;

	@LastModifiedDate
	private DateTime lastModified;

	public Project(String projectName) {

		this.projectName = projectName;
	}

}
