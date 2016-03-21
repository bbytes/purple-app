package com.bbytes.purple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.repository.CommentRepository;


@Service
public class CommentService extends AbstractService<Comment, String> {

	
	@Autowired
	public CommentService(CommentRepository commentRepository) {
		super(commentRepository);
	}

	

}
