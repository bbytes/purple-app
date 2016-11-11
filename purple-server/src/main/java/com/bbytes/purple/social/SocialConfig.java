package com.bbytes.purple.social;

import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.bitbucket.connect.BitBucketConnectionFactory;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.social.slack.connect.SlackConnectionFactory;
import org.springframework.web.context.request.RequestContextHolder;

import com.bbytes.purple.auth.jwt.SocialSessionUtils;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.repository.SocialConnectionRepository;
import com.bbytes.purple.service.TenantResolverService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.TenancyContextHolder;

@Configuration
@EnableSocial
public class SocialConfig {

	@Autowired
	private SocialConnectionRepository socialConnectionRepository;

	@Autowired
	private Environment environment;

	@Autowired
	private TenantResolverService tenantResolverService;

	@Autowired
	private UserService userService;

	@Bean
	public ConnectionFactoryLocator connectionFactoryLocator() {
		ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
		registry.addConnectionFactory(new GitHubConnectionFactory(environment.getProperty("spring.social.github.appId"),
				environment.getProperty("spring.social.github.appSecret")));

		registry.addConnectionFactory(new BitBucketConnectionFactory(environment.getProperty("spring.social.bitbucket.appId"),
				environment.getProperty("spring.social.bitbucket.appSecret")));

		registry.addConnectionFactory(new SlackConnectionFactory(environment.getProperty("spring.social.slack.appId"),
				environment.getProperty("spring.social.slack.appSecret")));
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
		MongoUsersConnectionRepository mongoUsersConnectionRepository = new MongoUsersConnectionRepository(socialConnectionRepository,
				connectionFactoryLocator(), mongoConnectionTransformers(), userService);
		mongoUsersConnectionRepository.setConnectionSignUp(new SocialConnectionUserIdHelper());
		return mongoUsersConnectionRepository;
	}

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	public ConnectionRepository connectionRepository() {
		String emailId = userService.getLoggedInUserEmail();

		if (emailId == null)
			emailId = SocialSessionUtils.geEmailId(RequestContextHolder.currentRequestAttributes());

		TenancyContextHolder.setTenant(tenantResolverService.findTenantIdForUserEmail(emailId));

		ConnectionRepository connectionRepository = usersConnectionRepository().createConnectionRepository(emailId);
		return connectionRepository;
	}

}
