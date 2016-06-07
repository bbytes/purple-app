package com.bbytes.purple.domain;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class ProjectUserCountStats {

	private Double hours;

	@Field("status_count")
	private Integer statusCount;

	private Integer dayOfYear;
	
	private Integer month;

	private String date;

	private Project project;

	private User user;

	public String getDate() {
		if (dayOfYear != null)
			date = DateTime.now().withDayOfYear(dayOfYear).toString("MM/dd/YYYY");
		return date;
	}

}