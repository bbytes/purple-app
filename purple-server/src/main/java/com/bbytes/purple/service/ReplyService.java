package com.bbytes.purple.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Reply;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.CommentRepository;
import com.bbytes.purple.repository.UserRepository;
import com.bbytes.purple.utils.ErrorHandler;

/**
 * @author aditya
 *
 */

@Service
public class ReplyService extends AbstractService<Reply, Serializable> {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	Comment comment;

	List<Reply> replies;

	public ReplyService(MongoRepository<Reply, Serializable> mongoRepository) {
		super(mongoRepository);
	}

	public Comment getCommentId(String commentId) throws PurpleException {
		Comment getCommentId = null;
		if (commentId != null && !commentId.isEmpty()) {
			if (!userRepository.findOne(commentId).equals(commentId))
				throw new PurpleException("Error while getting commentId", ErrorHandler.COMMENT_NOT_FOUND);
			try {
				getCommentId = commentRepository.findOne(commentId);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.GET_COMMENT_FAILED);
			}
		}
		return getCommentId;
	}

	public User getUserId(String userId) throws PurpleException {
		User getUserId = null;
		if (userId != null && !userId.isEmpty()) {
			if (!userRepository.findOne(userId).equals(userId))
				throw new PurpleException("Error while getting userId", ErrorHandler.USER_NOT_FOUND);
			try {
				getUserId = userRepository.findOne(userId);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.GET_USER_FAILED);
			}
		}
		return getUserId;
	}

	public void create(Reply reply) throws PurpleException {

		if (reply != null) {
			if (reply.getReplyDesc() == null && reply.getReplyDesc().isEmpty())
				throw new PurpleException("Error while adding reply", ErrorHandler.COMMENT_NOT_FOUND);
			try {
				replies.add(reply);
				comment.setReplies(replies);
				commentRepository.save(comment);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_REPLY_FAILED);
			}
		}
	}

	public List<Reply> getAllReply() throws PurpleException {
		List<Reply> replies = new ArrayList<Reply>();
		try {
			replies = comment.getReplies();
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_COMMENT_FAILED);
		}

		return replies;
	}

}
