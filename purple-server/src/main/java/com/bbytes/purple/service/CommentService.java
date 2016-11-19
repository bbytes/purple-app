package com.bbytes.purple.service;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bbytes.purple.auth.jwt.TokenAuthenticationProvider;
import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.CommentRepository;
import com.bbytes.purple.rest.dto.models.CommentDTO;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.GlobalConstants;

/**
 * Comment Service
 * 
 * @author aditya
 *
 */

@Service
public class CommentService extends AbstractService<Comment, String> {

	private CommentRepository commentRepository;

	@Autowired
	private StatusService statusService;

	@Autowired
	private UserService userService;

	@Autowired
	protected TokenAuthenticationProvider tokenAuthenticationProvider;

	@Value("${base.url}")
	private String baseUrl;

	@Autowired
	public CommentService(CommentRepository commentRepository) {
		super(commentRepository);
		this.commentRepository = commentRepository;
	}

	public Comment findByCommentId(String commentId) {
		return commentRepository.findOne(commentId);
	}

	public List<Comment> getCommentByStatus(Status status) {
		return commentRepository.findByStatus(status);
	}

	public List<Comment> getCommentByStatus(List<Status> statuses) {
		return commentRepository.findByStatusIn(statuses);
	}

	public long getCountByStatus(Status status) {
		return commentRepository.countByStatus(status);
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
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_COMMENT_FAILED, e);
			}
		}
		return addComment;
	}

	public void deleteComment(String commentId) throws PurpleException {
		if (!commentIdExist(commentId))
			throw new PurpleException("Error while deleting comment", ErrorHandler.COMMENT_NOT_FOUND);
		try {
			Comment comment = commentRepository.findOne(commentId);
			commentRepository.delete(comment);

		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.DELETE_COMMENT_FAILED);
		}
	}

	/**
	 * Return comment object
	 * 
	 * @param commentId
	 * @return
	 * @throws PurpleException
	 */
	public Comment getComment(String commentId) throws PurpleException {
		Comment comment = null;
		if (!commentIdExist(commentId))
			throw new PurpleException("Error while getting comment", ErrorHandler.COMMENT_NOT_FOUND);
		try {
			comment = commentRepository.findOne(commentId);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_COMMENT_FAILED);
		}
		return comment;
	}

	public Comment updateComment(String commentId, CommentDTO comment) throws PurpleException {
		Comment updateComment = null;
		if (!commentIdExist(commentId))
			throw new PurpleException("Error while update comment", ErrorHandler.COMMENT_NOT_FOUND);
		try {
			updateComment = findByCommentId(commentId);
			updateComment.setCommentDesc(comment.getCommentDesc());
			commentRepository.save(updateComment);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.UPDATE_COMMENT_FAILED);
		}
		return updateComment;
	}

	public List<Comment> getAllComment(String statusId) throws PurpleException {
		List<Comment> comments = new LinkedList<Comment>();
		if (!statusId.isEmpty() && statusId != null) {
			if (!statusService.statusIdExist(statusId))
				throw new PurpleException("Error while getting comment", ErrorHandler.STATUS_NOT_FOUND);
			try {
				Status status = statusService.getStatusbyId(statusId);
				comments = commentRepository.findByStatus(status);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.GET_COMMENT_FAILED);
			}
		}

		return comments;
	}

	public Map<String, Object> checkMentionUser(String stringText) {

		Matcher matcher = null;
		Map<String, Object> responseMap = new LinkedHashMap<>();

		String mentionRegexPattern = GlobalConstants.MENTION_REGEX_PATTERN;

		// Create a Pattern object
		Pattern mentionPatternObj = Pattern.compile(mentionRegexPattern);

		Set<String> emailTagList = new LinkedHashSet<String>();

		if (stringText != null && !stringText.isEmpty()) {
			// Now create matcher object.
			matcher = mentionPatternObj.matcher(stringText);
			// looping all @mention users, adding into emailList
			while (matcher.find()) {
				emailTagList.add(matcher.group(1));
				User mentionUser = userService.getUserByEmail(matcher.group(1));
				if (mentionUser != null) {
					// replacing @mention pattern with @username
					String str = stringText.replaceFirst(GlobalConstants.MENTION_REGEX_PATTERN,
							"<a>@" + mentionUser.getName() + "</a>").trim();
					stringText = str;
				}
			}
			responseMap.put("desc", stringText);
			responseMap.put("mentionEmailList", emailTagList);
		}

		return responseMap;
	}

	/**
	 * Return the snippet url with xAuthToken and commentId along with baseUrl
	 * 
	 * @param user
	 * @param comment
	 * @return
	 */
	public String commentSnippetUrl(User user, Comment comment) {
		final String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 24);
		String snippetUrl = baseUrl + GlobalConstants.COMMENT_SNIPPET_URL + xauthToken
				+ GlobalConstants.COMMENT_ID_PARAM + comment.getCommentId();
		return snippetUrl;
	}
}
