package com.bbytes.purple.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;

public interface UserRepository extends MongoRepository<User, String> {

	User findOneByEmail(String email);

	User findOneByName(String name);

	List<User> findByUserRole(UserRole role);
}
