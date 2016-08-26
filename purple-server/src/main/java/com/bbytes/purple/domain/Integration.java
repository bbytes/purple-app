
package com.bbytes.purple.domain;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

/**
 * Integration Domain Object
 * 
 * @author akshay
 */

@Data
@Document
public class Integration {

	@Id
	private String integrationId;

	@Field("jira_basic_authHeader")
	private String jiraBasicAuthHeader;

	@Field("jira_base_url")
	private String jiraBaseURL;
	
	@Field("slack_channel_id")
	private String slackChannelId;

	@DBRef
	private User user;

	@CreatedDate
	private Date creationDate;

	@LastModifiedDate
	private Date lastModified;

}
