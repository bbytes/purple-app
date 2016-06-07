package com.bbytes.purple.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Organization;

public interface OrganizationRepository extends MongoRepository<Organization, String>{

	Organization findByOrgId(String orgId);
}
