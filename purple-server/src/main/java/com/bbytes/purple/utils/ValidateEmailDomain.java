package com.bbytes.purple.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class ValidateEmailDomain {

	public static String disposableEmailDomain;

	@PostConstruct
	public void init() {
		disposableEmailDomain = domainList;
	}

	@Value("${invalid.domains}")
	private String domainList;

	public static boolean isEmailDomainNotValid(String email) {
		List<String> invalidDomainList = new ArrayList<String>(Arrays.asList(disposableEmailDomain.split(",")));
		int index = email.indexOf("@");
		String domainName = email.substring(index + 1);
		return invalidDomainList.contains(domainName);
	}
}
