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
public class CommentDTO implements Serializable {

	private static final long serialVersionUID = -4406245538051984706L;

	private String comment;

	private User user;

	private Status status;

}
