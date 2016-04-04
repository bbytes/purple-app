package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;

import lombok.Data;

/**
 * @author aditya
 *
 */

@Data
public class ReplyDTO implements Serializable {

	private static final long serialVersionUID = 4846222951455253656L;

	private String replyId;

	private String replyDesc;
	
	private String userName;
}
