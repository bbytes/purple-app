package com.bbytes.purple.database;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
@EnableMongoRepositories(basePackages = "com.bbytes.purple")
@EnableMongoAuditing
@Profile({ "saas", "!test" })
public class MongoDatabaseSaasConfig extends AbstractMongoConfiguration {

	@Value("${spring.data.mongodb.host}")
	private String host;

	@Value("${spring.data.mongodb.port}")
	private Integer port;

	@Value("${spring.data.mongodb.database}")
	private String database;

	@Value("${spring.data.mongodb.username}")
	private String username;

	@Value("${spring.data.mongodb.password}")
	private String password;

	@Value("${spring.data.mongodb.auth.database}")
	private String authDatabase;

	@Override
	protected String getMappingBasePackage() {
		return "com.bbytes.purple.domain";
	}

	@Bean
	public ValidatingMongoEventListener validatingMongoEventListener() {
		return new ValidatingMongoEventListener(validator());
	}

	@Bean
	public LocalValidatorFactoryBean validator() {
		return new LocalValidatorFactoryBean();
	}

	@Override
	protected String getDatabaseName() {
		return database;
	}

	@Override
	@Bean
	public Mongo mongo() throws Exception {
		if (username == null || username.isEmpty() || authDatabase == null || authDatabase.isEmpty() || password == null
				|| password.isEmpty()) {
			return new MongoClient(Collections.singletonList(new ServerAddress(host, port)));
		} else {
			MongoCredential credential = MongoCredential.createCredential(username, authDatabase,
					password.toCharArray());
			return new MongoClient(Collections.singletonList(new ServerAddress(host, port)), Arrays.asList(credential));
		}
	}

	@Bean
	public MultiTenantDbFactory mongoDbFactory(final Mongo mongo) throws Exception {
		return new MultiTenantDbFactory((MongoClient) mongo, database);
	}

	@Bean
	public MongoTemplate mongoTemplate(final Mongo mongo) throws Exception {
		MultiTenantDbFactory mongoDbFactory = mongoDbFactory(mongo);
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);
		mongoDbFactory.setMongoTemplate(mongoTemplate);
		return mongoTemplate;
	}

}
