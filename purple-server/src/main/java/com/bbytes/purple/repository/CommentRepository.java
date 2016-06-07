package com.bbytes.purple.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Status;

public interface CommentRepository extends MongoRepository<Comment, String>{
	
	List<Comment> findByStatus(Status status);
	
	long countByStatus(Status status);

}
