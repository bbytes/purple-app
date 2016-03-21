package com.bbytes.purple.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Reply;

public interface ReplyRepository extends MongoRepository<Reply, String> {

}
