package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;
import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import lombok.Data;

/**
 * @author aditya
 *
 */

@Data
public class ReplyDTO implements Serializable {

	private static final long serialVersionUID = 4846222951455253656L;

	private Comment comment;

	private User user;

	private Status status;

	private String reply;
}
