package com.bbytes.purple.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.NotificationSetting;

public interface NotificationSettingRepository extends MongoRepository<NotificationSetting, String>{
	
}
