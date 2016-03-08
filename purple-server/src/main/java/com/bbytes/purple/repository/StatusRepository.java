package com.bbytes.purple.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Status;

public interface StatusRepository extends MongoRepository<Status, String>{

}
