package com.bbytes.purple.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;

@Configuration
public class ThymeleafEmailTemplateConfiguration {

	@Bean
	public SpringResourceTemplateResolver emailTemplateResolver() {
		SpringResourceTemplateResolver emailTemplateResolver = new SpringResourceTemplateResolver();
		emailTemplateResolver.setPrefix("classpath:/mail-templates/");
		emailTemplateResolver.setSuffix(".html");
		emailTemplateResolver.setTemplateMode("HTML5");
		emailTemplateResolver.setCharacterEncoding("UTF-8");
		emailTemplateResolver.setOrder(1);

		return emailTemplateResolver;
	}
}