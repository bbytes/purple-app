package com.bbytes.purple.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.User;

public interface UserRepository extends MongoRepository<User, String>{

	User findOneByEmail(String email);
}
