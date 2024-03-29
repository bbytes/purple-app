package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;

import lombok.Data;

/**
 * Comment DTO Object
 * 
 * @author aditya
 *
 */
@Data
public class CommentDTO implements Serializable {

	private static final long serialVersionUID = -4406245538051984706L;

	private String commentId;

	private String commentDesc;

	private String statusId;

	private String userName;
	
	private long replyCount = 0;

}
