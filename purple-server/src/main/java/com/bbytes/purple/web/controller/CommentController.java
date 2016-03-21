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
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.CommentDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.CommentService;
import com.bbytes.purple.utils.SuccessHandler;

@RestController
public class CommentController {

	private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

	@Autowired
	private CommentService commentService;

	@RequestMapping(value = "/api/v1/comment/save", method = RequestMethod.POST)
	public RestResponse saveComment(@RequestBody CommentDTO commentDTO) throws PurpleException {

		Comment addComment = new Comment(commentDTO.getComment(), commentDTO.getUser(), commentDTO.getStatus());
		Comment comment = commentService.addComment(addComment);
		logger.debug(commentDTO.getComment() + "are added successfully");

		RestResponse commentResponse = new RestResponse(RestResponse.SUCCESS, comment,
				SuccessHandler.ADD_COMMENT_SUCCESS);

		return commentResponse;

	}

	@RequestMapping(value = "/api/v1/comment/update/{commentid}", method = RequestMethod.DELETE)
	public RestResponse deleteComment(@PathVariable("commentid") String commentId, Comment comment) {

		commentService.delete(commentId);
		logger.debug(comment.getCommentId() + "are deleted successfully");

		RestResponse commentResponse = new RestResponse(RestResponse.SUCCESS, commentId,
				SuccessHandler.DELETE_COMMENT_SUCCESS);

		return commentResponse;
	}

	@RequestMapping(value = "/api/v1/comment/{commentId}", method = RequestMethod.GET)
	public RestResponse readComment(@PathVariable("commentId") String commentId) {
		commentService.findOne(commentId);

		RestResponse commentResponse = new RestResponse(RestResponse.SUCCESS, commentId,
				SuccessHandler.READ_COMMENT_SUCCESS);

		return commentResponse;
	}

	@RequestMapping(value = "/api/v1/comment/update/{commentid}", method = RequestMethod.PUT)
	public RestResponse updateComment(@PathVariable("commentId") String commentId, @RequestBody CommentDTO commentDTO)
			throws PurpleException {

		Comment updateComment = new Comment(commentDTO.getComment(), commentDTO.getUser(), commentDTO.getStatus());

		Comment comment = commentService.updateComment(commentId, updateComment);

		logger.debug("Comments are fetched successfully");
		RestResponse commentReponse = new RestResponse(RestResponse.SUCCESS, comment,
				SuccessHandler.UPDATE_COMMENT_SUCCESS);
		return commentReponse;
	}
}
