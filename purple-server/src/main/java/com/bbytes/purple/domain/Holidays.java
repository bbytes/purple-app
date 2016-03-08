package com.bbytes.purple.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

/**
 * Holidays Domain Object
 * @author akshay
 */

@Data
@Document
public class Holidays {

	@Id
	private String holidayId;
	
	@Field("holiday_name")
	private String holidayName;
	
	@Field("holiday_date")
	private Date holidayDate;
	
	@DBRef(lazy=true)
	private Organization organization;

	public Holidays(String holidayName, Date holidayDate, Organization organization) {
		
		this.holidayName = holidayName;
		this.holidayDate = holidayDate;
		this.organization = organization;
	}

	
	
}
