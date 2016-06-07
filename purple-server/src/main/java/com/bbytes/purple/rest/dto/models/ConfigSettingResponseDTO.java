package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * NotificationResponse DTO Object
 * 
 * @author akshay
 *
 */
@Data
public class ConfigSettingResponseDTO implements Serializable {

	private static final long serialVersionUID = 3260691808422324878L;

	private boolean captureHours;
	
	private boolean weekendNotification;
	
	private String statusEnable;
	
	private List<HolidayDTO> holidayList;

}
