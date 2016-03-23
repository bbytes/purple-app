package com.bbytes.purple.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.CommentDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.CommentService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.SuccessHandler;

/**
 * @author aditya
 *
 */

@RestController
public class CommentController {
	private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

	@Autowired
	private CommentService commentService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/api/v1/comment/add", method = RequestMethod.POST)
	public RestResponse saveComment(@RequestBody CommentDTO commentDTO) throws PurpleException {

		User user = userService.getLoggedinUser();
		Comment comment = commentService.addComment(commentDTO, user);

		logger.debug(comment.getCommentDesc() + "' is added successfully");
		RestResponse commentReponse = new RestResponse(RestResponse.SUCCESS, comment,
				SuccessHandler.ADD_COMMENT_SUCCESS);

		return commentReponse;
	}

	@RequestMapping(value = "/api/v1/comment/delete/{commentId}", method = RequestMethod.DELETE)
	public RestResponse deleteComment(@PathVariable("commentId") String commentId) throws PurpleException {
		commentService.deleteComment(commentId);

		logger.debug("Comment id  " + commentId + " is deleted successfully");
		RestResponse commentReponse = new RestResponse(RestResponse.SUCCESS, SuccessHandler.DELETE_COMMENT_SUCCESS);

		return commentReponse;
	}

	@RequestMapping(value = "/api/v1/comment/update/{commentid}", method = RequestMethod.PUT)
	public RestResponse updateComment(@PathVariable("commentId") String commentId, @RequestBody CommentDTO commentDTO)
			throws PurpleException {

		Comment comment = commentService.updateComment(commentId, commentDTO);

		logger.debug("Comment are fetched successfully");

		RestResponse commentReponse = new RestResponse(RestResponse.SUCCESS, comment,
				SuccessHandler.UPDATE_STATUS_SUCCESS);

		return commentReponse;
	}

	@RequestMapping(value = "/api/v1/comment/{commentId}", method = RequestMethod.GET)
	public RestResponse getComment(@PathVariable("commentId") String commentId) throws PurpleException {

		Comment comment = commentService.getComment(commentId);

		logger.debug("Comment are fetched successfully");

		RestResponse commentReponse = new RestResponse(RestResponse.SUCCESS, comment,
				SuccessHandler.GET_COMMENT_SUCCESS);

		return commentReponse;

	}

}
