package com.bbytes.purple.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.CommentDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.CommentService;
import com.bbytes.purple.service.DataModelToDTOConversionService;
import com.bbytes.purple.service.EmailService;
import com.bbytes.purple.service.StatusService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.SuccessHandler;

/**
 * Comment Controller
 * 
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

	@Autowired
	private StatusService statusService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private DataModelToDTOConversionService dataModelToDTOConversionService;

	@RequestMapping(value = "/api/v1/comment/add", method = RequestMethod.POST)
	public RestResponse saveComment(@RequestBody CommentDTO commentDTO) throws PurpleException {

		final String subject = GlobalConstants.EMAIL_COMMENT_SUBJECT;
		final String template = GlobalConstants.COMMENT_EMAIL_TEMPLATE;

		User user = userService.getLoggedInUser();
		Comment comment = commentService.addComment(commentDTO, user);
		Status status = statusService.findOne(comment.getStatus().getStatusId());

		List<String> emailList = new ArrayList<String>();
		emailList.add(status.getUser().getEmail());

		Map<String, Object> emailBody = new HashMap<>();
		emailBody.put(GlobalConstants.USER_NAME, status.getUser().getName());
		emailBody.put(GlobalConstants.SUBSCRIPTION_DATE, status.getDateTime());
		emailBody.put("userName", user.getName());

		emailService.sendEmail(emailList, emailBody, subject, template);

		CommentDTO commentResponse = dataModelToDTOConversionService.convertComment(comment);

		logger.debug(comment.getCommentDesc() + "' is added successfully");
		RestResponse commentReponse = new RestResponse(RestResponse.SUCCESS, commentResponse,
				SuccessHandler.ADD_COMMENT_SUCCESS);

		return commentReponse;
	}

	@RequestMapping(value = "/api/v1/comment/delete/{commentId}", method = RequestMethod.DELETE)
	public RestResponse deleteComment(@PathVariable("commentId") String commentId) throws PurpleException {
		final String COMMENT_DELETE_SUCCESS_MSG = "Successfully deleted comment";
		commentService.deleteComment(commentId);

		logger.debug("Comment id  " + commentId + " is deleted successfully");
		RestResponse commentReponse = new RestResponse(RestResponse.SUCCESS, COMMENT_DELETE_SUCCESS_MSG,
				SuccessHandler.DELETE_COMMENT_SUCCESS);

		return commentReponse;
	}

	@RequestMapping(value = "/api/v1/comment/update/{commentId}", method = RequestMethod.PUT)
	public RestResponse updateComment(@PathVariable("commentId") String commentId, @RequestBody CommentDTO commentDTO)
			throws PurpleException {

		Comment comment = commentService.updateComment(commentId, commentDTO);
		CommentDTO commentResponse = dataModelToDTOConversionService.convertComment(comment);

		logger.debug("Comment is updated successfully");

		RestResponse commentReponse = new RestResponse(RestResponse.SUCCESS, commentResponse,
				SuccessHandler.UPDATE_COMMENT_SUCCESS);

		return commentReponse;
	}

	@RequestMapping(value = "/api/v1/comments", method = RequestMethod.GET)
	public RestResponse getAllComments(@RequestParam String statusId) throws PurpleException {

		List<Comment> commentList = commentService.getAllComment(statusId);
		Map<String, Object> commentMap = dataModelToDTOConversionService
				.getResponseMapWithGridDataAndComment(commentList);

		logger.debug("Comments are fetched successfully");

		RestResponse commentReponse = new RestResponse(RestResponse.SUCCESS, commentMap,
				SuccessHandler.GET_COMMENT_SUCCESS);

		return commentReponse;

	}
}
