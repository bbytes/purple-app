package com.bbytes.purple.domain;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Reply Domain Object
 * @author akshay
 */

@Data
@Document
public class Reply {

	@Id
	private ObjectId replyId; //afterModification
	
	private String replyDesc;

	@CreatedDate
	private Date creationDate;
	
	@LastModifiedDate
	private Date lastModified;
	
	@DBRef
	private User user;

	public Reply(String replyDesc) {
		this.replyDesc = replyDesc;
		replyId = ObjectId.get(); //afterModification
	}	
	
}
