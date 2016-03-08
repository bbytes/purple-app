package com.bbytes.purple.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Comment;

public interface CommentRepository extends MongoRepository<Comment, String>{

}
