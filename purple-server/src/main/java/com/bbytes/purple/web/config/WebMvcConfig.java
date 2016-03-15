package com.bbytes.purple.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Cors filter is added only for dev purpose , in prod the angu;ar and spring backedn will be under same domain
 * @author Thanneer
 *
 */
@Configuration
@Profile("dev")
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CORSInterceptor());
	}

}