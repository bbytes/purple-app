package com.bbytes.purple.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.github.connect.GitHubConnectionFactory;

import com.bbytes.purple.repository.SocialConnectionRepository;
import com.bbytes.purple.service.UserService;

@Configuration
@EnableSocial
public class SocialConfig {

	@Autowired
	private SocialConnectionRepository socialConnectionRepository;

	@Autowired
	private Environment environment;
	
	@Autowired
	private UserService userService;

	@Bean
	public ConnectionFactoryLocator connectionFactoryLocator() {
		ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
		registry.addConnectionFactory(new GitHubConnectionFactory(environment.getProperty("spring.social.github.appId"),
				environment.getProperty("spring.social.github.appSecret")));
		return registry;
	}

	@Bean
	public TextEncryptor textEncryptor() {
		return Encryptors.noOpText();
	}

	@Bean
	public MongoConnectionTransformers mongoConnectionTransformers() {
		return new MongoConnectionTransformers(connectionFactoryLocator(), textEncryptor());
	}

	@Bean
	public UsersConnectionRepository usersConnectionRepository() {
		MongoUsersConnectionRepository mongoUsersConnectionRepository = new MongoUsersConnectionRepository(
				socialConnectionRepository, connectionFactoryLocator(), mongoConnectionTransformers(),userService);
		mongoUsersConnectionRepository.setConnectionSignUp(new SocialConnectionUserIdHelper());
		return mongoUsersConnectionRepository;
	}

	
	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	public ConnectionRepository connectionRepository() {
		ConnectionRepository connectionRepository = usersConnectionRepository().createConnectionRepository(null);
		return connectionRepository;

	}

//	@Bean
//	public SocialConnectController connectController() {
//		SocialConnectController socialConnectController = new SocialConnectController(connectionFactoryLocator(),
//				connectionRepository(), usersConnectionRepository());
//		socialConnectController.addInterceptor(new GithubConnectInterceptor());
//		socialConnectController.setApplicationUrl("http://localhots:9999");
//		return socialConnectController;
//	}

	// @Bean
	// public ConnectController connectController() {
	// ConnectController socialConnectController = new
	// ConnectController(connectionFactoryLocator(),
	// connectionRepository());
	// socialConnectController.addInterceptor(new GithubConnectInterceptor());
	// socialConnectController.setApplicationUrl("http://localhots:9999");
	// return socialConnectController;
	// }

}
