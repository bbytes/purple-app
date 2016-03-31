package com.bbytes.purple.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.User;

public interface ProjectRepository extends MongoRepository<Project, String> {

	Project findOneByProjectName(String projectName);

	List<Project> findByUser(User user);

}
