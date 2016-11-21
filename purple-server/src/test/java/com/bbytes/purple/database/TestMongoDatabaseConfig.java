//package com.bbytes.purple.database;
//
//import java.io.IOException;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.mongo.MongoProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.env.Environment;
//import org.springframework.data.mongodb.config.EnableMongoAuditing;
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
//
//import com.mongodb.Mongo;
//import com.mongodb.MongoClientOptions;
//
//import de.flapdoodle.embed.mongo.MongodExecutable;
//import de.flapdoodle.embed.mongo.MongodProcess;
//import de.flapdoodle.embed.mongo.MongodStarter;
//import de.flapdoodle.embed.mongo.config.IMongodConfig;
//import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
//import de.flapdoodle.embed.mongo.config.Net;
//import de.flapdoodle.embed.mongo.distribution.Version;
//
//@Configuration
//@EnableMongoRepositories(basePackages = "com.bbytes.purple")
//@EnableMongoAuditing
//@Profile("test")
//public class TestMongoDatabaseConfig {
//
//	private static final MongodStarter starter = MongodStarter.getDefaultInstance();
//
//	@Autowired
//	private MongoProperties properties;
//
//	@Autowired
//	private Environment env;
//
//	@Autowired(required = false)
//	private MongoClientOptions options;
//
//	@Value("${spring.data.mongodb.host}")
//	private String host;
//
//	@Value("${spring.data.mongodb.port}")
//	private Integer port;
//
//	@Value("${spring.data.mongodb.database}")
//	private String database;
//
//	@Value("${spring.data.mongodb.username:}")
//	private String username;
//
//	@Value("${spring.data.mongodb.password:}")
//	private String password;
//
//	@Value("${spring.data.mongodb.auth.database:}")
//	private String authDatabase;
//
//	@Bean(destroyMethod = "close")
//	public Mongo mongo() throws IOException {
//		Net net = mongod().getConfig().net();
//		properties.setHost(net.getServerAddress().getHostName());
//		properties.setPort(net.getPort());
//		return properties.createMongoClient(this.options, env);
//	}
//
//	@Bean(destroyMethod = "stop")
//	public MongodProcess mongod() throws IOException {
//		return mongodExe().start();
//	}
//
//	@Bean(destroyMethod = "stop")
//	public MongodExecutable mongodExe() throws IOException {
//		return starter.prepare(mongodConfig());
//	}
//
//	@Bean
//	public IMongodConfig mongodConfig() throws IOException {
//		return new MongodConfigBuilder().version(Version.Main.PRODUCTION).build();
//	}
//
//}