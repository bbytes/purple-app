package com.bbytes.purple.web.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.MultiTenantUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The filter that checks for url that is not allowed for normal user. If the
 * link request is not allowed to be viewed by normal user then we throw access
 * denied error
 *
 * @author akshay
 */

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class NormalUserLinkFilter implements Filter {

	private static Logger logger = LoggerFactory.getLogger(NormalUserLinkFilter.class);

	private List<RequestMatcher> requestMatchers;

	private OrRequestMatcher normalUserNotAllowedUrls;

	private final String NORMAL_USER_PROHIBITED_LINK_PROPERTY = "notallowed.normal.user.links";

	@Autowired
	private Environment environment;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String notAllowedURLs = environment.getProperty(NORMAL_USER_PROHIBITED_LINK_PROPERTY);
		if (notAllowedURLs != null) {
			requestMatchers = new ArrayList<>();
			List<String> notAllowedURLLinks = Arrays.asList(notAllowedURLs.split(","));
			for (String urlPattern : notAllowedURLLinks) {
				AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher(urlPattern.trim());
				requestMatchers.add(antPathRequestMatcher);
			}
			normalUserNotAllowedUrls = new OrRequestMatcher(requestMatchers);
		} else {
			throw new ServletException("'notallowed.normal.user.links' property not set");
		}

	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest request = (HttpServletRequest) req;

		if ("OPTIONS".equals(((HttpServletRequest) request).getMethod()))
			return;

		if (MultiTenantUtils.isUserInRole(UserRole.NORMAL_USER_ROLE.getRoleName())) {
			logger.debug("Normal user url request : " + request.getServletPath());
			if (normalUserNotAllowedUrls.matches(request)) {
				forwardToAccessDeniedPage(request, response);
			} else {
				chain.doFilter(req, res);
			}
		} else {
			// allow user access
			chain.doFilter(req, res);
		}
	}

	@Override
	public void destroy() {
		normalUserNotAllowedUrls = null;
		if (requestMatchers != null) {
			requestMatchers.clear();
			requestMatchers = null;
		}
	}

	/**
	 * forwarding to access denied url with 403 error response.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void forwardToAccessDeniedPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		RestResponse result = new RestResponse(false, "Normal User not allowed",
				ErrorHandler.NORMAL_USER_URL_ACCESS_DENIED);
		ObjectMapper mapper = new ObjectMapper();
		String responseObject = mapper.writeValueAsString(result);
		((HttpServletResponse) response).setContentType("application/json");
		((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
		((HttpServletResponse) response).getWriter().append(responseObject);

	}

}