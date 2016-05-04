package com.bbytes.purple.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.ConfigSetting;
import com.bbytes.purple.domain.Organization;

public interface ConfigSettingRepository extends MongoRepository<ConfigSetting, String>{
	
	ConfigSetting findByOrganization(Organization org);
	
}
