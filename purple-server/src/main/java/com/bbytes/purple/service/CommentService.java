package com.bbytes.purple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.CommentRepository;
import com.bbytes.purple.rest.dto.models.CommentDTO;
import com.bbytes.purple.utils.ErrorHandler;

@Service
public class CommentService extends AbstractService<Comment, String> {

	private CommentRepository commentRepository;

	@Autowired
	private StatusService statusService;

	@Autowired
	public CommentService(CommentRepository commentRepository) {
		super(commentRepository);
		this.commentRepository = commentRepository;
	}

	public boolean commentIdExist(String commentId) {
		boolean isExist = commentRepository.findOne(commentId) == null ? false : true;
		return isExist;
	}

	public Comment addComment(CommentDTO commentDTO, User user) throws PurpleException {

		Comment addComment = null;
		if (commentDTO != null) {
			if (!statusService.statusIdExist(commentDTO.getStatusId()))
				throw new PurpleException("Error while adding comment", ErrorHandler.STATUS_NOT_FOUND);
			try {
				Status status = statusService.getStatusbyId(commentDTO.getStatusId());
				Comment comment = new Comment(commentDTO.getCommentDesc(), user, status);
				addComment = commentRepository.save(comment);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_COMMENT_FAILED);
			}
		}
		return addComment;
	}

	public void deleteComment(String commentId) throws PurpleException {
		if (!commentId.equals(null)) {
			if (!commentIdExist(commentId))
				throw new PurpleException("Error while deleting comment", ErrorHandler.COMMENT_NOT_FOUND);
			try {
				Comment comment = commentRepository.findOne(commentId);
				commentRepository.delete(comment);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.GET_COMMENT_FAILED);
			}
		} else
			throw new PurpleException("Can not delete empty comment", ErrorHandler.COMMENT_NOT_FOUND);
	}

}
