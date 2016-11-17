package com.bbytes.purple.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bbytes.purple.auth.jwt.TokenAuthenticationProvider;
import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Reply;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.ReplyRepository;
import com.bbytes.purple.rest.dto.models.ReplyDTO;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.GlobalConstants;

/**
 * Reply Service
 * 
 * @author Akshay
 *
 */

@Service
public class ReplyService extends AbstractService<Reply, String> {

	private ReplyRepository replyRepository;

	@Autowired
	private CommentService commentService;

	@Autowired
	protected TokenAuthenticationProvider tokenAuthenticationProvider;

	@Value("${base.url}")
	private String baseUrl;

	@Autowired
	public ReplyService(ReplyRepository replyRepository) {
		super(replyRepository);
		this.replyRepository = replyRepository;
	}

	public Reply getReplybyId(String replyId) {
		return replyRepository.findOne(replyId);
	}

	public Comment postReply(String commentId, ReplyDTO replyDTO, User user) throws PurpleException {
		Comment comment = null;
		List<Reply> replyList = new ArrayList<Reply>();
		if (!commentService.commentIdExist(commentId))
			throw new PurpleException("Error while posting reply", ErrorHandler.COMMENT_NOT_FOUND);
		if (replyDTO.getReplyDesc() != null) {
			try {
				comment = commentService.findByCommentId(commentId);
				replyList = comment.getReplies();
				Reply reply = new Reply(replyDTO.getReplyDesc());
				reply.setUser(user);
				replyList.add(reply);
				comment.setReplies(replyList);
				comment = commentService.save(comment);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_REPLY_FAILED);
			}
		}
		return comment;
	}

	public Comment updateReply(String commentId, String replyId, ReplyDTO replyDTO) throws PurpleException {
		Comment comment = null;
		List<Reply> replyList = new ArrayList<Reply>();
		if (!commentService.commentIdExist(commentId))
			throw new PurpleException("Error while updating reply", ErrorHandler.COMMENT_NOT_FOUND);
		if (replyDTO.getReplyDesc() != null) {
			try {
				comment = commentService.findByCommentId(commentId);

				replyList = comment.getReplies();
				for (Reply currentReply : replyList) {
					if (currentReply.getReplyId().toString().equals(replyId)) {
						currentReply.setReplyDesc(replyDTO.getReplyDesc());
					}
				}
				comment.setReplies(replyList);
				comment = commentService.save(comment);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.UPDATE_REPLY_FAILED);
			}
		}
		return comment;
	}

	public void deleteReply(String commentId, String replyId) throws PurpleException {
		Comment comment = null;
		List<Reply> replyList = new ArrayList<Reply>();
		if (!commentService.commentIdExist(commentId))
			throw new PurpleException("Error while deleting reply", ErrorHandler.COMMENT_NOT_FOUND);
		try {
			comment = commentService.findByCommentId(commentId);
			replyList = comment.getReplies();
			List<Reply> tobeRemoved = new ArrayList<Reply>();
			for (Reply reply : replyList) {
				if (reply.getReplyId().toString().equals(replyId))
					tobeRemoved.add(reply);
			}
			replyList.removeAll(tobeRemoved);
			comment.setReplies(replyList);
			comment = commentService.save(comment);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.DELETE_REPLY_FAILED);
		}
	}

	public Comment getAllReplies(String commentId) throws PurpleException {
		Comment comment = null;
		if (!commentService.commentIdExist(commentId))
			throw new PurpleException("Error while getting reply", ErrorHandler.COMMENT_NOT_FOUND);
		try {
			comment = commentService.findByCommentId(commentId);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_COMMENT_FAILED);
		}

		return comment;
	}

	/**
	 * Return reply object of comment
	 * 
	 * @param commentId
	 * @param replyId
	 * @throws PurpleException
	 */
	public Map<String,Object> getReply(String commentId, String replyId) throws PurpleException {
		Reply reply = null;
		Comment comment = null;
		Map<String,Object> replyMap = new LinkedHashMap<>();
		if (!commentService.commentIdExist(commentId))
			throw new PurpleException("Error while getting reply", ErrorHandler.COMMENT_NOT_FOUND);
		try {
			comment = commentService.findByCommentId(commentId);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_REPLY_FAILED);
		}
		List<Reply> replyList = comment.getReplies();
		boolean flag = false;
		for (Reply replyFromDb : replyList) {
			if (replyFromDb.getReplyId().toString().equals(replyId)) {
				flag = true;
				reply = replyFromDb;
				replyMap.put("comment", comment);
				replyMap.put("reply", reply);
				break;
			}
		}
		if (flag)
			return replyMap;
		else
			throw new PurpleException("Error while getting reply", ErrorHandler.REPLY_NOT_FOUND);

	}

	/**
	 * Return the snippet url with xAuthToken and commentId and replyId along
	 * with baseUrl
	 * 
	 * @param user
	 * @param comment
	 * @param reply
	 * @return
	 */
	public String replySnippetUrl(User user, Comment comment, Reply reply) {
		final String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 24);
		String snippetUrl = baseUrl + GlobalConstants.REPLY_SNIPPET_URL + xauthToken + GlobalConstants.COMMENT_ID_PARAM
				+ comment.getCommentId() + GlobalConstants.REPLY_ID_PARAM + reply.getReplyId();
		return snippetUrl;
	}
}
