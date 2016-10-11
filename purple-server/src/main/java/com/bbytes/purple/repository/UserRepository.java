package com.bbytes.purple.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;

public interface UserRepository extends MongoRepository<User, String> {

	User findOneByEmail(String email);

	List<User> findByEmailIn(List<String> emails);

	User findOneByName(String name);

	List<User> findByNameIn(List<String> names);

	List<User> findByUserRole(UserRole role);
	
	List<User> findByUserRoleIn(List<UserRole> role);

}
