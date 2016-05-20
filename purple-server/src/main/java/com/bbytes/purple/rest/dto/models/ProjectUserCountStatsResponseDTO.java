package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * ProjectUserCountStats DTO Object
 * 
 * @author akshay
 *
 */
@Data
public class ProjectUserCountStatsResponseDTO implements Serializable {

	private static final long serialVersionUID = -4283248621429252655L;

	private String projectName;

	private List<ProjectUserCountStatsDTO> projectUserCountStatsDTOList;
}
