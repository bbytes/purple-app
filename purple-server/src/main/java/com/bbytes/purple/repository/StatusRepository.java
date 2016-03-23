package com.bbytes.purple.repository;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;

public interface StatusRepository extends MongoRepository<Status, String>{
	
	Status findByDateTime(DateTime dateTime); 
	
	Status findByProject(Project project);
	
	List<Status> findByUser(User user);
	

}
