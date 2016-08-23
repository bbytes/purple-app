package com.bbytes.purple.social;

import java.util.Date;

import com.bbytes.purple.domain.SocialConnection;
import com.google.common.base.Function;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;

public class MongoConnectionTransformers {

    private final ConnectionFactoryLocator connectionFactoryLocator;
    private final TextEncryptor textEncryptor;

    public MongoConnectionTransformers(final ConnectionFactoryLocator connectionFactoryLocator, final TextEncryptor textEncryptor) {
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.textEncryptor = textEncryptor;
    }

    public Function<SocialConnection, String> toUserId() {
        return new Function<SocialConnection, String>() {
            @Override
            public String apply(final SocialConnection input) {
                if (input == null) {
                    return null;
                }
                return input.getUserId();
            }
        };
    }
    
    public Function<SocialConnection, Connection<?>> toConnection() {
        return new Function<SocialConnection, Connection<?>>() {
            @Override
            public Connection<?> apply(final SocialConnection input) {
                if (input == null) {
                    return null;
                }
                final ConnectionData cd = new ConnectionData(
                        input.getProviderId(),
                        input.getProviderUserId(),
                        input.getDisplayName(),
                        input.getProfileUrl(),
                        input.getImageUrl(),
                        decrypt(input.getAccessToken()),
                        decrypt(input.getSecret()),
                        decrypt(input.getRefreshToken()),
                        input.getExpireTime()
                );
                final ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(input.getProviderId());
                return connectionFactory.createConnection(cd);
            }
        };
    }
    
    public Function<Connection<?>, SocialConnection> fromConnection(final String userId) {
        return new Function<Connection<?>, SocialConnection>() {
            @Override
            public SocialConnection apply(final Connection<?> input) {
                if (input == null) {
                    return null;
                }
                final ConnectionData cd = input.createData();
                final SocialConnection mongoConnection = new SocialConnection();
                mongoConnection.setCreated(new Date());
                mongoConnection.setUserId(userId);
                mongoConnection.setProviderId(cd.getProviderId());
                mongoConnection.setProviderUserId(cd.getProviderUserId());
                mongoConnection.setDisplayName(cd.getDisplayName());
                mongoConnection.setProfileUrl(cd.getProfileUrl());
                mongoConnection.setImageUrl(cd.getImageUrl());
                mongoConnection.setAccessToken(encrypt(cd.getAccessToken()));
                mongoConnection.setSecret(encrypt(cd.getSecret()));
                mongoConnection.setRefreshToken(encrypt(cd.getRefreshToken()));
                mongoConnection.setExpireTime(cd.getExpireTime());
                return mongoConnection;
            }
        };
    }
    
    private String encrypt(final String decrypted) {
        if (decrypted == null) {
            return null;
        }
        return textEncryptor.encrypt(decrypted);
    }
    
    private String decrypt(final String encrypted) {
        if (encrypted == null) {
            return null;
        }
        return textEncryptor.decrypt(encrypted);
    }
}
