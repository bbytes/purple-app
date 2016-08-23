package com.bbytes.purple.social;

import static com.google.common.collect.Lists.transform;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.bbytes.purple.domain.SocialConnection;
import com.bbytes.purple.repository.SocialConnectionRepository;
import com.bbytes.purple.service.UserService;
import com.google.common.collect.ImmutableList;

public class MongoConnectionRepository implements ConnectionRepository {

	private final SocialConnectionRepository socialConnectionRepository;
	private final ConnectionFactoryLocator connectionFactoryLocator;
	private final MongoConnectionTransformers mongoConnectionTransformers;
	private final UserService userService;

	public MongoConnectionRepository(final UserService userService, final SocialConnectionRepository socialConnectionRepository,
			final ConnectionFactoryLocator connectionFactoryLocator,
			final MongoConnectionTransformers mongoConnectionTransformers) {
		this.userService = userService;
		this.socialConnectionRepository = socialConnectionRepository;
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.mongoConnectionTransformers = mongoConnectionTransformers;
	}

	@Override
	public MultiValueMap<String, Connection<?>> findAllConnections() {
		String userId = userService.getLoggedInUserEmail();
		List<Connection<?>> results = transform(socialConnectionRepository.findByUserId(userId),
				mongoConnectionTransformers.toConnection());
		final MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<>();
		for (String registeredProviderId : connectionFactoryLocator.registeredProviderIds()) {
			connections.put(registeredProviderId, ImmutableList.<Connection<?>> of());
		}
		for (Connection<?> connection : results) {
			final String providerId = connection.getKey().getProviderId();
			if (connections.get(providerId).isEmpty()) {
				connections.put(providerId, new LinkedList<Connection<?>>());
			}
			connections.add(providerId, connection);
		}
		return connections;
	}

	@Override
	public List<Connection<?>> findConnections(final String providerId) {
		String userId = userService.getLoggedInUserEmail();
		List<Connection<?>> results = transform(
				socialConnectionRepository.findByUserIdAndProviderId(userId, providerId),
				mongoConnectionTransformers.toConnection());
		return ImmutableList.copyOf(results);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A> List<Connection<A>> findConnections(final Class<A> apiType) {
		final List<?> connections = findConnections(getProviderId(apiType));
		return (List<Connection<A>>) connections;
	}

	@Override
	public MultiValueMap<String, Connection<?>> findConnectionsToUsers(
			final MultiValueMap<String, String> providerUsers) {
		if (providerUsers == null || providerUsers.isEmpty()) {
			throw new IllegalArgumentException("Unable to execute find: no providerUsers provided");
		}

		MultiValueMap<String, Connection<?>> connectionsForUsers = new LinkedMultiValueMap<String, Connection<?>>();

		for (String providerId : providerUsers.keySet()) {
			List<String> provideUserIds = providerUsers.get(providerId);
			List<Connection<?>> resultList = transform(
					socialConnectionRepository.findByProviderIdAndProviderUserIdIn(providerId, provideUserIds),
					mongoConnectionTransformers.toConnection());

			for (Connection<?> connection : resultList) {
				List<String> userIds = providerUsers.get(providerId);
				List<Connection<?>> connections = connectionsForUsers.get(providerId);
				if (connections == null) {
					connections = new ArrayList<Connection<?>>(userIds.size());
					for (int i = 0; i < userIds.size(); i++) {
						connections.add(null);
					}
					connectionsForUsers.put(providerId, connections);
				}
				String providerUserId = connection.getKey().getProviderUserId();
				int connectionIndex = userIds.indexOf(providerUserId);
				connections.set(connectionIndex, connection);
			}
		}

		return connectionsForUsers;
	}

	@Override
	public Connection<?> getConnection(final ConnectionKey connectionKey) {
		String userId = userService.getLoggedInUserEmail();
		final Connection<?> connection = mongoConnectionTransformers.toConnection()
				.apply(socialConnectionRepository.findByUserIdAndProviderIdAndProviderUserId(userId,
						connectionKey.getProviderId(), connectionKey.getProviderUserId()));

		if (connection == null) {
			throw new NoSuchConnectionException(connectionKey);
		} else {
			return connection;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A> Connection<A> getConnection(final Class<A> apiType, final String providerUserId) {
		final String providerId = getProviderId(apiType);
		return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A> Connection<A> getPrimaryConnection(final Class<A> apiType) {
		final String providerId = getProviderId(apiType);
		final Connection<A> connection = (Connection<A>) findPrimaryConnection(providerId);
		if (connection == null) {
			throw new NotConnectedException(providerId);
		} else {
			return connection;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A> Connection<A> findPrimaryConnection(final Class<A> apiType) {
		final String providerId = getProviderId(apiType);
		return (Connection<A>) findPrimaryConnection(providerId);
	}

	@Override
	public void addConnection(final Connection<?> connection) {
		try {
			String userId = userService.getLoggedInUserEmail();
			final SocialConnection mongoConnection = mongoConnectionTransformers.fromConnection(userId)
					.apply(connection);
			SocialConnection mongoConnectionFromDB = socialConnectionRepository
					.findByUserIdAndProviderIdAndProviderUserId(mongoConnection.getUserId(),
							mongoConnection.getProviderId(), mongoConnection.getProviderUserId());
			if (mongoConnectionFromDB == null) {
				socialConnectionRepository.save(mongoConnection);
			}
		} catch (DuplicateKeyException ex) {
			throw new DuplicateConnectionException(connection.getKey());
		}
	}

	@Override
	public void updateConnection(final Connection<?> connection) {
		String userId = userService.getLoggedInUserEmail();
		final SocialConnection mongoConnection = mongoConnectionTransformers.fromConnection(userId).apply(connection);
		socialConnectionRepository.save(mongoConnection);
	}

	@Override
	public void removeConnections(final String providerId) {
		String userId = userService.getLoggedInUserEmail();
		socialConnectionRepository.deleteByUserIdAndProviderId(userId, providerId);
	}

	@Override
	public void removeConnection(final ConnectionKey connectionKey) {
		String userId = userService.getLoggedInUserEmail();
		socialConnectionRepository.deleteByUserIdAndProviderIdAndProviderUserId(userId, connectionKey.getProviderId(),
				connectionKey.getProviderUserId());
	}

	private Connection<?> findPrimaryConnection(String providerId) {
		String userId = userService.getLoggedInUserEmail();
		return mongoConnectionTransformers.toConnection()
				.apply(socialConnectionRepository.findByUserIdAndProviderId(userId, providerId).get(0));
	}

	private <A> String getProviderId(Class<A> apiType) {
		return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
	}
}
