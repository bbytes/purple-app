package com.bbytes.purple.web.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Reply;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.ReplyDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.ReplyService;
import com.bbytes.purple.service.StatusService;
import com.bbytes.purple.utils.SuccessHandler;

@RestController
public class ReplyController {

	public static final Logger logger = LoggerFactory.getLogger(ReplyController.class);

	@Autowired
	ReplyService replyService;

	@Autowired
	StatusService statusService;

	@Autowired
	Comment comment;

	List<Reply> replies;

	/**
	 * Adding Reply To Comment
	 * 
	 * @param commentId
	 * @param replyDTO
	 * @return replyReponse
	 * @throws PurpleException
	 */

	@RequestMapping(value = "/api/v1/comment/{commentid}/reply/", method = RequestMethod.POST)
	public RestResponse addReply(@PathVariable("commentId") String commentId, @RequestBody ReplyDTO replyDTO)
			throws PurpleException {

		replyService.getCommentId(commentId);
		replyService.getUserId(replyDTO.getUser().getUserId());
		statusService.getStatusbyId(replyDTO.getStatus().getStatusId());

		Reply reply = new Reply(replyDTO.getReply());
		reply.setUser(replyDTO.getUser());
		replyService.create(reply);

		logger.debug(replyDTO.getReply() + "is added successfully");
		RestResponse replyReponse = new RestResponse(RestResponse.SUCCESS, reply, SuccessHandler.ADD_REPLY_SUCCESS);
		return replyReponse;
	}

	/**
	 * Getting Replies of Comment
	 * 
	 * 
	 * @param commentId
	 * @param replyId
	 * @param replyDTO
	 * @return replyReponse
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/comment/{commentid}/reply/{replyid}", method = RequestMethod.GET)
	public RestResponse getAllReply(@PathVariable("commentId") String commentId,
			@PathVariable("replyId") String replyId, @RequestBody ReplyDTO replyDTO) throws PurpleException {

		replyService.getCommentId(commentId);
		List<Reply> replyList = replyService.getAllReply();

		logger.debug("All replies are fetched successfully");
		RestResponse replyReponse = new RestResponse(RestResponse.SUCCESS, replyList, SuccessHandler.GET_REPLY_SUCCESS);

		return replyReponse;
	}

	/**
	 * Delete Reply of Comment
	 * 
	 * @param commentId
	 * @param replyId
	 * @param replyDTO
	 * @return replyReponse
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/comment/{commentid}/reply/{replyid}", method = RequestMethod.DELETE)
	public RestResponse deleteReply(@PathVariable("commentId") String commentId,
			@PathVariable("replyId") String replyId, @RequestBody ReplyDTO replyDTO) throws PurpleException {
		replyService.getCommentId(commentId);
		replyService.delete(replyId);

		logger.debug("Comment with reply id  '" + replyId + "' is deleted successfully");
		RestResponse replyResponse = new RestResponse(RestResponse.SUCCESS, SuccessHandler.DELETE_REPLY_SUCCESS);

		return replyResponse;
	}

}
