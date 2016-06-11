package com.bbytes.purple.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.ReplyDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.DataModelToDTOConversionService;
import com.bbytes.purple.service.EmailService;
import com.bbytes.purple.service.ReplyService;
import com.bbytes.purple.service.StatusService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.GlobalConstants;
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

	@Autowired
	private EmailService emailService;

	@Autowired
	private StatusService statusService;

	@Autowired
	private DataModelToDTOConversionService dataModelToDTOConversionService;
	
	@Value("${email.reply.subject}")
	private String replySubject;

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

		final String template = GlobalConstants.REPLY_EMAIL_TEMPLATE;
		DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

		// We will get current logged in user
		User user = userService.getLoggedInUser();

		Comment comment = replyService.postReply(commentId, replyDTO, user);
		Status status = statusService.findOne(comment.getStatus().getStatusId());
		
		int replySize = comment.getReplies().size();
		String postDate = dateFormat.format(comment.getCreationDate());;

		List<String> emailList = new ArrayList<String>();
		emailList.add(status.getUser().getEmail());
		emailList.add(comment.getUser().getEmail());

		Map<String, Object> emailBody = new HashMap<>();
		emailBody.put(GlobalConstants.USER_NAME, user.getName());
		emailBody.put(GlobalConstants.SUBSCRIPTION_DATE, postDate);
		emailBody.put(GlobalConstants.REPLY_DESC, comment.getReplies().get(replySize-1).getReplyDesc());

		emailService.sendEmail(emailList, emailBody, replySubject, template);

		Map<String, Object> replyMap = dataModelToDTOConversionService.getResponseMapWithGridDataAndReply(comment);

		logger.debug("Reply for comment Id  '" + commentId + "' is added successfully");
		RestResponse replyReponse = new RestResponse(RestResponse.SUCCESS, replyMap, SuccessHandler.ADD_REPLY_SUCCESS);

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

		final String DELETE_REPLY_SUCCESS_MSG = "Successfully deleted reply";
		// We will get current logged in user
		replyService.deleteReply(commentId, replyId);

		logger.debug("Reply for comment Id  '" + commentId + "' is deleted successfully");
		RestResponse replyReponse = new RestResponse(RestResponse.SUCCESS, DELETE_REPLY_SUCCESS_MSG,
				SuccessHandler.DELETE_REPLY_SUCCESS);

		return replyReponse;
	}

	/**
	 * The updateReply method is used to update the reply for a comment.
	 * 
	 * @param commentId
	 * @param replyId
	 * @param replyDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/comment/{commentid}/reply/update/{replyid}", method = RequestMethod.PUT)
	public RestResponse updateReply(@PathVariable("commentid") String commentId,
			@PathVariable("replyid") String replyId, @RequestBody ReplyDTO replyDTO) throws PurpleException {

		Comment comment = replyService.updateReply(commentId, replyId, replyDTO);

		Map<String, Object> replyMap = dataModelToDTOConversionService.getResponseMapWithGridDataAndReply(comment);

		logger.debug("Reply for comment Id  '" + commentId + "' is updated successfully");
		RestResponse replyReponse = new RestResponse(RestResponse.SUCCESS, replyMap,
				SuccessHandler.UPDATE_REPLY_SUCCESS);

		return replyReponse;
	}

	/**
	 * The getAllReply method is used to get all replies for a comment
	 * 
	 * @param commentId
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/comment/{commentid}/reply/all", method = RequestMethod.GET)
	public RestResponse getAllReply(@PathVariable("commentid") String commentId) throws PurpleException {

		Comment comment = replyService.getAllReplies(commentId);
		Map<String, Object> replyMap = dataModelToDTOConversionService.getResponseMapWithGridDataAndReply(comment);

		logger.debug("All Replies for comment Id  '" + commentId + "' is fetched successfully");
		RestResponse replyReponse = new RestResponse(RestResponse.SUCCESS, replyMap, SuccessHandler.GET_REPLY_SUCCESS);

		return replyReponse;
	}
}
