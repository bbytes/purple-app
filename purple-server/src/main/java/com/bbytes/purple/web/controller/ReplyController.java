package com.bbytes.purple.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.bbytes.purple.domain.Reply;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.ReplyDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.CommentService;
import com.bbytes.purple.service.DataModelToDTOConversionService;
import com.bbytes.purple.service.NotificationService;
import com.bbytes.purple.service.ReplyService;
import com.bbytes.purple.service.StatusService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.SuccessHandler;

/**
 * Reply Controller
 * 
 * @author Akshay
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
	private NotificationService notificationService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private StatusService statusService;

	@Autowired
	private DataModelToDTOConversionService dataModelToDTOConversionService;

	@Value("${email.reply.subject}")
	private String replySubject;

	@Value("${email.tag.subject}")
	private String tagSubject;

	/**
	 * The addReply method is used to add reply to comment
	 * 
	 * @param commentId
	 * @param replyDTO
	 * @return
	 * @throws PurpleException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/v1/comment/{commentid}/reply", method = RequestMethod.POST)
	public RestResponse addReply(@PathVariable("commentid") String commentId, @RequestBody ReplyDTO replyDTO)
			throws PurpleException {

		final String template = GlobalConstants.REPLY_EMAIL_TEMPLATE;

		User user = userService.getLoggedInUser();
		final String subject = user.getName() + " " + tagSubject;
		Map<String, Object> replyMap = commentService.checkMentionUser(replyDTO.getReplyDesc());
		replyDTO.setReplyDesc((String) replyMap.get("desc"));
		Comment comment = replyService.postReply(commentId, replyDTO, user);
		Status status = statusService.findOne(comment.getStatus().getStatusId());
		Set<String> mentioneEmailSet = (Set<String>) replyMap.get("mentionEmailList");
		List<String> mentioneEmailList = new ArrayList<String>();
		mentioneEmailList.addAll(mentioneEmailSet);

		int replySize = comment.getReplies().size();

		List<String> emailList = new ArrayList<String>();
		emailList.add(status.getUser().getEmail());
		emailList.add(comment.getUser().getEmail());

		Map<String, Object> emailBody = replyEmailBody(user, comment, status, replySize,
				GlobalConstants.REPLY_EMAIL_TEXT, comment.getUser().getName());

		notificationService.sendTemplateEmail(emailList, replySubject, template, emailBody);

		if (mentioneEmailList != null && !mentioneEmailList.isEmpty()) {
			Map<String, Object> mentionEmailBody = replyEmailBody(user, comment, status, replySize,
					GlobalConstants.MENTIONED_EMAIL_TEXT, "");
			notificationService.sendTemplateEmail(mentioneEmailList, subject, template, mentionEmailBody);
		}

		if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
			replySize = comment.getReplies().size();
			Reply reply = comment.getReplies().get(replySize - 1);
			notificationService.sendSlackMessage(status.getUser(), "Statusnap comment reply notification",
					replyService.replySnippetUrl(status.getUser(), comment, reply));
			notificationService.sendSlackMessage(comment.getUser(), "Statusnap comment reply notification ",
					replyService.replySnippetUrl(comment.getUser(), comment, reply));
		}

		Map<String, Object> replyResponseMap = dataModelToDTOConversionService
				.getResponseMapWithGridDataAndReply(comment);

		logger.debug("Reply for comment Id  '" + commentId + "' is added successfully");
		RestResponse replyReponse = new RestResponse(RestResponse.SUCCESS, replyResponseMap,
				SuccessHandler.ADD_REPLY_SUCCESS);

		return replyReponse;
	}

	/**
	 * Return email body for reply email template
	 * 
	 * @param user
	 * @param comment
	 * @param status
	 * @param replySize
	 * @return
	 */
	private Map<String, Object> replyEmailBody(User user, Comment comment, Status status, int replySize,
			String emailText, String userName) {

		DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);
		String postDate = dateFormat.format(comment.getCreationDate());
		Map<String, Object> emailBody = new HashMap<>();
		emailBody.put(GlobalConstants.USER_NAME, userName);
		emailBody.put(GlobalConstants.SUBSCRIPTION_DATE, postDate);
		emailBody.put(GlobalConstants.REPLY_DESC, comment.getReplies().get(replySize - 1).getReplyDesc());
		emailBody.put(GlobalConstants.COMMENT_DESC, comment.getCommentDesc());
		emailBody.put(GlobalConstants.WORKED_ON, status.getWorkedOn() == null ? "" : status.getWorkedOn());
		emailBody.put(GlobalConstants.WORKING_ON, status.getWorkingOn() == null ? "" : status.getWorkingOn());
		emailBody.put(GlobalConstants.BLOCKERS, status.getBlockers() == null ? "" : status.getBlockers());
		emailBody.put(GlobalConstants.EMAIL_STRING_TEXT, emailText);
		emailBody.put("userName", user.getName());
		return emailBody;
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

	/**
	 * The getReply method is used to get particular reply of comment
	 * 
	 * @param commentId
	 * @param replyId
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/comment/{commentId}/reply/{replyId}", method = RequestMethod.GET)
	public RestResponse getReply(@PathVariable("commentId") String commentId, @PathVariable("replyId") String replyId)
			throws PurpleException {

		Map<String, Object> replyMap = replyService.getReply(commentId, replyId);

		logger.debug("Reply for comment Id  '" + commentId + "' is fetched successfully");
		RestResponse replyReponse = new RestResponse(RestResponse.SUCCESS, replyMap, SuccessHandler.GET_REPLY_SUCCESS);

		return replyReponse;
	}
}
