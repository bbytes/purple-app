package com.bbytes.purple.auth.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.web.filter.GenericFilterBean;

public class TenantResolverFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		// TODO: Need to implement the filter . The filter has to read the
		// header and get the user id ie email and set the correct tenant id to
		// thread local for connecting to correct mongo db
	}
}
