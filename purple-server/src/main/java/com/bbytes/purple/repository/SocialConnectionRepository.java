package com.bbytes.purple.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.SocialConnection;

public interface SocialConnectionRepository extends MongoRepository<SocialConnection, String> {

	List<SocialConnection> findByUserId(String userId);

	List<SocialConnection> findByUserIdAndProviderId(String userId, String providerId);

	List<SocialConnection> findByProviderIdAndProviderUserId(String providerId, String providerUserId);

	List<SocialConnection> findByProviderIdAndProviderUserIdIn(String providerId, Set<String> providerUserIds);

	List<SocialConnection> findByProviderIdAndProviderUserIdIn(String providerId, List<String> providerUserIds);

	List<SocialConnection> findByUserIdAndProviderUserIdIn(String userId, Set<String> providerUserIds);

	SocialConnection findByUserIdAndProviderIdAndProviderUserId(String userId, String providerId,
			String providerUserId);

	void deleteByUserIdAndProviderId(String userId, String providerId);

	void deleteByUserIdAndProviderIdAndProviderUserId(String userId, String providerId, String providerUserId);

}
