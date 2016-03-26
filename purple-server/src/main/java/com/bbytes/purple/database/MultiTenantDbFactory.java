package com.bbytes.purple.database;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver.IndexDefinitionHolder;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.util.Assert;

import com.bbytes.purple.service.SpringProfileService;
import com.bbytes.purple.utils.TenancyContextHolder;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MultiTenantDbFactory extends SimpleMongoDbFactory {

	private static final Logger logger = LoggerFactory.getLogger(MultiTenantDbFactory.class);
	
	private final String defaultName;

	private MongoTemplate mongoTemplate;

	private static final HashMap<String, Object> databaseIndexMap = new HashMap<String, Object>();

	public MultiTenantDbFactory(final MongoClient mongoClient, final String defaultDatabaseName) {
		super(mongoClient, defaultDatabaseName);
		logger.debug("Instantiating " + MultiTenantDbFactory.class.getName() + " with default database name: "
				+ defaultDatabaseName);
		this.defaultName = defaultDatabaseName;
	}

	// dirty but ... what can I do?
	public void setMongoTemplate(final MongoTemplate mongoTemplate) {
		Assert.isNull(this.mongoTemplate, "You can set MongoTemplate just once");
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public DB getDb() {
		if (!SpringProfileService.runningSaasMode()) {
			// always return default db if app running in enterprise mode
			return super.getDb(this.defaultName); 
		}
		// it will come to this block only if app running in saas mode
		final String tenantName = TenancyContextHolder.getTenant();
		final String dbToUse = (tenantName != null ? tenantName : this.defaultName);
		logger.debug("Acquiring database: " + dbToUse);
		createIndexIfNecessaryFor(dbToUse);
		return super.getDb(dbToUse);
	}

	private void createIndexIfNecessaryFor(final String database) {
		if (this.mongoTemplate == null) {
			logger.error("MongoTemplate is null, will not create any index.");
			return;
		}
		// sync and init once
		boolean needsToBeCreated = false;
		synchronized (MultiTenantDbFactory.class) {
			final Object syncObj = databaseIndexMap.get(database);
			if (syncObj == null) {
				databaseIndexMap.put(database, new Object());
				needsToBeCreated = true;
			}
		}
		// make sure only one thread enters with needsToBeCreated = true
		synchronized (databaseIndexMap.get(database)) {
			if (needsToBeCreated) {
				logger.debug("Creating indices for database name=[" + database + "]");
				createIndexes();
				logger.debug("Done with creating indices for database name=[" + database + "]");
			}
		}
	}

	private void createIndexes() {
		final MongoMappingContext mappingContext = (MongoMappingContext) this.mongoTemplate.getConverter()
				.getMappingContext();
		final MongoPersistentEntityIndexResolver indexResolver = new MongoPersistentEntityIndexResolver(mappingContext);
		for (BasicMongoPersistentEntity<?> persistentEntity : mappingContext.getPersistentEntities()) {
			checkForAndCreateIndexes(indexResolver, persistentEntity);
		}
	}

	private void checkForAndCreateIndexes(final MongoPersistentEntityIndexResolver indexResolver,
			final MongoPersistentEntity<?> entity) {
		// make sure its a root document
		if (entity.findAnnotation(Document.class) != null) {
			for (IndexDefinitionHolder indexDefinitionHolder : indexResolver.resolveIndexForEntity(entity)) {
				// work because of javas reentered lock feature
				this.mongoTemplate.indexOps(entity.getType()).ensureIndex(indexDefinitionHolder);
			}
		}
	}
}