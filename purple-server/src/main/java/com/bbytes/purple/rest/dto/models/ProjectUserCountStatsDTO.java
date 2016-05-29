package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;

import lombok.Data;

/**
 * ProjectUserCountStats DTO Object
 * 
 * @author akshay
 *
 */
@Data
public class ProjectUserCountStatsDTO implements Serializable {

	private static final long serialVersionUID = -4283248621429252655L;

	private String[] labels ;

	private String[] series;

	private String[][] data;
	
}
