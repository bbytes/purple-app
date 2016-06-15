/*package com.bbytes.purple.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.ContentVersionStrategy;
import org.springframework.web.servlet.resource.VersionResourceResolver;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	private static final int SEVEN_DAYS_IN_SECONDS = 604800;
	
	private static final int NO_CACHE = 0;
	 
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		registry.addResourceHandler("/index.html").addResourceLocations("classpath:static/index.html")
				.setCachePeriod(NO_CACHE);

		registry.addResourceHandler("/app/**").addResourceLocations("classpath:static")
				.setCachePeriod(SEVEN_DAYS_IN_SECONDS);
		
		registry.addResourceHandler("/assets/**").addResourceLocations("classpath:static")
		.setCachePeriod(SEVEN_DAYS_IN_SECONDS);

	}

}*/