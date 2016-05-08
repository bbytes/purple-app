package com.bbytes.purple.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;

public interface StatusRepository extends MongoRepository<Status, String> {

	List<Status> findByProject(Project project);

	List<Status> findByUser(User user);
	
	List<Status> findByProjectIn(List<Project> project);

	List<Status> findByUserIn(List<User> user);

	List<Status> findByProjectAndUser(Project project, User user);
	
	List<Status> findByProjectInAndUserIn(List<Project> project, List<User> user);
	
	List<Status> findByDateTimeBetween(Date startDate, Date endDate);
	
	List<Status> findByDateTimeBetweenAndUserIn(Date startDate, Date endDate , List<User> user);
	
	List<Status> findByDateTimeBetweenAndUser(Date startDate, Date endDate , User user);
	
}
