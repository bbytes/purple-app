package com.bbytes.purple.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.UserRole;

public interface UserRoleRepository extends MongoRepository<UserRole, String>{
	
	UserRole findByRoleName(String roleName);

}
