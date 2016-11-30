package com.bbytes.purple.auth.jwt;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SocialIntergrationFilter implements Filter {

	private RequestMatcher acceptedRequestMatcher;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (acceptedRequestMatcher.matches((HttpServletRequest) request)) {
			SocialSessionUtils.storeEmailIdToSession((HttpServletRequest) request);
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		acceptedRequestMatcher = new AntPathRequestMatcher("/social/**");
	}

	@Override
	public void destroy() {
	}

}
