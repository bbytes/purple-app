package com.bbytes.purple.service;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class SpringProfileService {

	private static boolean saasMode;
	
	private static boolean prodMode;
	
	@Autowired
	private Environment env;
	
	@PostConstruct
	public void initProfileInfo(){
		saasMode = isSaasMode();
		prodMode = isProdMode();
	}

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
	
	public boolean isSaasMode() {
		if (Arrays.asList(env.getActiveProfiles()).contains("saas")
				|| Arrays.asList(env.getActiveProfiles()).contains("default")
				|| Arrays.asList(env.getActiveProfiles()).isEmpty()) {
			return true;
		}
		return false;
	}
	
	public boolean isEnterpriseMode() {
		if (Arrays.asList(env.getActiveProfiles()).contains("enterprise")) {
			return true;
		}
		return false;
	
	}

	
	public static boolean runningSaasMode(){
		return saasMode;
	}
	
	public static boolean runningProdMode(){
		return prodMode;
	}
}
