package com.bbytes.purple.domain;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

/**
 * Comment Domain Object
 * @author akshay
 */

@Data
@Document
public class Comment {

	@Id
	private String commentId;
	
	@Field("comment_desc")
	private String commentDesc;
	
	@DBRef
	private User user;
	
	@DBRef(lazy=true)
	private Status status;
	
	// embedded list 
	private List<Reply> replies;
	
	@CreatedDate
	private DateTime creationDate;
	
	@LastModifiedDate
	private DateTime lastModified;

	public Comment(String commentDesc, User user, Status status) {
		
		this.commentDesc = commentDesc;
		this.user = user;
		this.status = status;
	}

	
	
}
