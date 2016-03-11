package com.bbytes.purple.domain;

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
 * Organization Domain Object
 * @author akshay
 */ 

@Data
@Document
public class Organization {
	
	@Id
	private String id;
	
	@Indexed(unique = true)
	@Field("org_id")
	private String orgId;
	
	@Field("org_name")
	private String orgName;
	
	@Field("business_area")
	private String businessArea;
	
	@Field("time_preference")
	private String timePreference;
	
	@DBRef(lazy=true)
	private List<Holiday> holidays;
	
	@CreatedDate
	private DateTime creationDate;
	
	@LastModifiedDate
	private DateTime lastModified;

	public Organization(String orgId, String orgName) {
		this.orgId = orgId;
		this.orgName = orgName;
	}

}
