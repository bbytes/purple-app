package com.bbytes.purple.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

/**
 * Domain Object for Notification Setting
 * 
 * @author akshay
 *
 */

@Data
@Document
public class NotificationSetting {

	@Id
	private String notificationId;

	@Field("capture_hours")
	private boolean captureHours = true;

	@Field("weekend_notification")
	private boolean weekendNotification = false;

	@Field("statusEnable")
	private String statusEnable;

	@DBRef
	private Organization organization;

	// embedded list
	private List<Holiday> holidays = new ArrayList<Holiday>();
}
