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
import com.bbytes.purple.rest.dto.models.ReplyDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.ReplyService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.SuccessHandler;

/**
 * Reply Controller
 * 
 * @author akshay
 *
 */
@RestController
public class ReplyController {

	private static final Logger logger = LoggerFactory.getLogger(ReplyController.class);

	@Autowired
	private ReplyService replyService;

	@Autowired
	private UserService userService;

	/**
	 * The addReply method is used to add reply to comment
	 * 
	 * @param commentId
	 * @param replyDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/comment/{commentid}/reply", method = RequestMethod.POST)
	public RestResponse addReply(@PathVariable("commentid") String commentId, @RequestBody ReplyDTO replyDTO)
			throws PurpleException {

		// We will get current logged in user
		User user = userService.getLoggedinUser();

		Comment comment = replyService.postReply(commentId, replyDTO, user);

		logger.debug("Reply for comment Id  '" + commentId + "' is added successfully");
		RestResponse replyReponse = new RestResponse(RestResponse.SUCCESS, comment, SuccessHandler.ADD_REPLY_SUCCESS);

		return replyReponse;
	}

	/**
	 * The deleteReply method is used to delete particular reply of comment
	 * 
	 * @param commentId
	 * @param replyId
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/comment/{commentid}/reply/{replyid}", method = RequestMethod.DELETE)
	public RestResponse deleteReply(@PathVariable("commentid") String commentId,
			@PathVariable("replyid") String replyId) throws PurpleException {

		// We will get current logged in user
		User user = userService.getLoggedinUser();
		Comment comment = replyService.deleteReply(commentId, replyId, user);

		logger.debug("Reply for comment Id  '" + commentId + "' is deleted successfully");
		RestResponse replyReponse = new RestResponse(RestResponse.SUCCESS, comment,
				SuccessHandler.DELETE_REPLY_SUCCESS);

		return replyReponse;
	}

}
