package com.bbytes.purple;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ActiveProfile implements EnvironmentAware, ApplicationListener<EmbeddedServletContainerInitializedEvent> {
	private static final Logger logger = LoggerFactory.getLogger(ActiveProfile.class);

	@Override
	public void setEnvironment(Environment environment) {
		logger.info("\n#### Active Profiles #### \n" +
					  "# " + StringUtils.join(environment.getActiveProfiles(), ",") + " # \n"  + 
					  "######################### \n");
	}

	@Override
	public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
		logger.info("\n####Tomcat port #### \n" +
					  "# " + event.getEmbeddedServletContainer().getPort() + " # \n"  + 
					  "#################### \n");
	}
	
}