package com.bbytes.purple.domain;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;

/**
 * Reply Domain Object
 * @author akshay
 */

@Data
public class Reply {

	private String replyDesc;
	
	@CreatedDate
	private DateTime creationDate;
	
	@LastModifiedDate
	private DateTime lastModified;

	public Reply(String replyDesc) {

		this.replyDesc = replyDesc;
	}	
	
}
