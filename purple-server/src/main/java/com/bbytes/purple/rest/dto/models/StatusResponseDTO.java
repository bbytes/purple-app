package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Status Response DTO Object
 * 
 * @author akshay
 *
 */
@Data
public class StatusResponseDTO implements Serializable {

	private static final long serialVersionUID = -7648998965372355788L;

	private String date;

	private List<StatusDTO> statusList;

}
