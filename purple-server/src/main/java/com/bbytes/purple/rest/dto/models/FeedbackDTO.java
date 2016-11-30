package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;

import lombok.Data;

/**
 * Feedback DTO Object
 * 
 * @author Akshay
 *
 */
@Data
public class FeedbackDTO implements Serializable {

	private static final long serialVersionUID = -5669897055587942225L;

	private String category;

	private String suggestions;

}
