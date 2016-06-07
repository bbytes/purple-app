package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class BaseDTO implements Serializable {

	private static final long serialVersionUID = -7751461039510283289L;
	
	String id;
	
	String value;

}
