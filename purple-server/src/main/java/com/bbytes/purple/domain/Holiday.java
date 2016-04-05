package com.bbytes.purple.domain;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

/**
 * Holidays Domain Object
 * @author akshay
 */

@Data
@Document
public class Holiday {

	@Id
	private ObjectId holidayId;
	
	@Field("holiday_name")
	private String holidayName;
	
	@Field("holiday_date")
	private Date holidayDate;

	public Holiday(Date holidayDate) {
		holidayId = ObjectId.get();
		this.holidayDate = holidayDate;
	}
}
