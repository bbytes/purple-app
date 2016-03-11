package com.bbytes.purple.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Part of tenant management DB .It stores the link between tenant id and user
 * email. This is used to resolve tenant id given email
 * 
 * @author akshay
 *
 */
@Data
@Document
public class TenantResolver {

	@Id
	private String id;

	@NotNull
	@Indexed(unique = true)
	private String email;
	
	@Indexed
	private String userId;

	@NotNull
	private String orgId;

}
