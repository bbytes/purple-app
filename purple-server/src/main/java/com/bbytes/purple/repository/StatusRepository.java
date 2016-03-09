package com.bbytes.purple.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;

public interface StatusRepository extends MongoRepository<Status, String>{
	
	Status findByProject(Project project);
	
	Status findByUser(User user);

}
