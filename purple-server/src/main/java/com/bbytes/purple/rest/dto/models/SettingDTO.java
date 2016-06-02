package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class SettingDTO implements Serializable {

	private static final long serialVersionUID = 4846222951455253656L;

	private String timeZone;

	private String timePreference;
	
	private String emailNotificationState;
}