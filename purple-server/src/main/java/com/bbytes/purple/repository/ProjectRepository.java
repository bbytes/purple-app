package com.bbytes.purple.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Project;

public interface ProjectRepository extends MongoRepository<Project, String>{

}
