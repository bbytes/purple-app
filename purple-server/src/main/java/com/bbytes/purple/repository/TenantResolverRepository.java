package com.bbytes.purple.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.TenantResolver;

public interface TenantResolverRepository extends MongoRepository<TenantResolver, String>{
	
	TenantResolver findOneByEmail(String email);
	
	TenantResolver findOneByOrgId(String email);
	
	TenantResolver findOneByUserId(String userId);

}
