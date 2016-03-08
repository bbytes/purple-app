package com.bbytes.purple.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Permission;

public interface PermissionRepository extends MongoRepository<Permission, String>{

}
