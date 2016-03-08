package com.bbytes.purple.domain;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

/**
 * Reply Domain Object
 * @author akshay
 */

@Data
@Document
public class Reply {

	@Id
	private String replyId;
	
	@Field("reply_desc")
	private String replyDesc;
	
	@DBRef
	private User user;
	
	@DBRef
	private Comment comment;
	
	@CreatedDate
	private DateTime creationDate;
	
	@LastModifiedDate
	private DateTime lastModified;

	public Reply(String replyDesc) {

		this.replyDesc = replyDesc;
	}	
	
}
