package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Notification DTO Object is used for request
 * 
 * @author akshay
 *
 */
@Data
public class ConfigSettingDTO implements Serializable {

	private static final long serialVersionUID = -7055911823042192272L;

	private boolean captureHours;

	private boolean weekendNotification;

	private String statusEnable;

	private List<String> holidayName;

	private List<String> holidayDate;

}
