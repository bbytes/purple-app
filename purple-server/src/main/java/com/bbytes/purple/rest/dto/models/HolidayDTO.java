package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;

import lombok.Data;

/**
 * Holiday DTO Object
 * 
 * @author Akshay
 *
 */
@Data
public class HolidayDTO implements Serializable {

	private static final long serialVersionUID = -5669897055587942225L;

	private String holidayId;

	private String holidayName;

	private String holidayDate;

}
