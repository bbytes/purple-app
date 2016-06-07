package com.bbytes.purple.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Integration;
import com.bbytes.purple.domain.User;

public interface IntegrationRepository extends MongoRepository<Integration, String> {

	Integration findByUser(User user);
}
