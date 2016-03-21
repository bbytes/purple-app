package com.bbytes.purple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.CommentRepository;
import com.bbytes.purple.utils.ErrorHandler;

@Service
public class CommentService extends AbstractService<Comment, String> {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	public CommentService(CommentRepository commentRepository) {
		super(commentRepository);
	}

	public boolean commentIdExist(String commentId) {
		boolean isExist = commentRepository.findOne(commentId) == null ? false : true;
		return isExist;
	}

	public Comment addComment(Comment comment) throws PurpleException {
		if (comment != null) {
			try {
				save(comment);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_COMMENT_FAILED);
			}
		}
		return comment;
	}

	public Comment updateComment(String commentId,Comment updateComment) throws PurpleException {

		
		if (commentId != null && !commentId.isEmpty()) {
			if (commentIdExist(commentId))
				throw new PurpleException("Error while updating Comment", ErrorHandler.COMMENT_NOT_FOUND);
			try {
				updateComment = commentRepository.findOne(commentId);
				updateComment.setCommentDesc(updateComment.getCommentDesc());
				updateComment.setStatus(updateComment.getStatus());
				updateComment.setUser(updateComment.getUser());
				updateComment = commentRepository.save(updateComment);

			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.UPDATE_COMMENT_FAILED);
			}
		}
		return updateComment;
	}

}
