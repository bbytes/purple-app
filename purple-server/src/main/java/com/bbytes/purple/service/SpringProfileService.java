package com.bbytes.purple.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class SpringProfileService {

	@Autowired
	private Environment env;

	public boolean isDevMode() {
		if (Arrays.asList(env.getActiveProfiles()).contains("dev")) {
			return true;
		}
		return false;
	}

	public boolean isProdMode() {
		if (Arrays.asList(env.getActiveProfiles()).contains("prod")
				|| Arrays.asList(env.getActiveProfiles()).contains("default")
				|| Arrays.asList(env.getActiveProfiles()).isEmpty()) {
			return true;
		}
		return false;
	}

}
