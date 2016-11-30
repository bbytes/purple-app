package com.bbytes.purple.social;

import static com.google.common.collect.Lists.transform;

import java.util.List;
import java.util.Set;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;

import com.bbytes.purple.domain.SocialConnection;
import com.bbytes.purple.repository.SocialConnectionRepository;
import com.bbytes.purple.service.UserService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class MongoUsersConnectionRepository implements UsersConnectionRepository {

	private final SocialConnectionRepository socialConnectionRepository;
	private final ConnectionFactoryLocator connectionFactoryLocator;
	private final MongoConnectionTransformers mongoConnectionTransformers;
	private  ConnectionSignUp connectionSignUp;
	private final UserService userService;

	public MongoUsersConnectionRepository(final SocialConnectionRepository socialConnectionRepository,
			final ConnectionFactoryLocator connectionFactoryLocator,
			final MongoConnectionTransformers mongoConnectionTransformers, final UserService userService) {
		this.socialConnectionRepository = socialConnectionRepository;
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.mongoConnectionTransformers = mongoConnectionTransformers;
		this.userService=userService;
	}

	public void setConnectionSignUp(final ConnectionSignUp connectionSignUp) {
		this.connectionSignUp = connectionSignUp;
	}

	@Override
	public List<String> findUserIdsWithConnection(final Connection<?> connection) {
		ConnectionKey key = connection.getKey();
		List<SocialConnection> result = socialConnectionRepository
				.findByProviderIdAndProviderUserId(key.getProviderId(), key.getProviderUserId());
		List<String> localUserIds = ImmutableList.copyOf(transform(result, mongoConnectionTransformers.toUserId()));
		if (localUserIds.isEmpty() && connectionSignUp != null) {
			String newUserId = connectionSignUp.execute(connection);
			if (newUserId != null) {
				createConnectionRepository(newUserId).addConnection(connection);
				return ImmutableList.of(newUserId);
			}
		}
		return localUserIds;
	}

	@Override
	public Set<String> findUserIdsConnectedTo(final String providerId, final Set<String> providerUserIds) {
		List<SocialConnection> result = socialConnectionRepository.findByProviderIdAndProviderUserIdIn(providerId,
				providerUserIds);
		return ImmutableSet.copyOf(transform(result, mongoConnectionTransformers.toUserId()));
	}

	@Override
	public ConnectionRepository createConnectionRepository(final String userId) {
		if (userId == null)
			throw new IllegalArgumentException("userId must be defined");
		return new MongoConnectionRepository(userId, socialConnectionRepository, connectionFactoryLocator,
				mongoConnectionTransformers);
	}
}
