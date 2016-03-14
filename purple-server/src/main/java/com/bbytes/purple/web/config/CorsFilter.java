package com.bbytes.purple.web.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bbytes.purple.utils.GlobalConstants;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Profile("dev")
public class CorsFilter implements Filter {

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest request = (HttpServletRequest) req;
			
		String accessControlAllowHeaders = GlobalConstants.HEADER_AUTH_TOKEN + "," + GlobalConstants.HEADER_TENANT_ID
				+ "," + "Origin, X-Requested-With, Content-Type, Accept";
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
		response.addHeader("Access-Control-Allow-Headers", accessControlAllowHeaders);
		response.addHeader("Access-Control-Expose-Headers", accessControlAllowHeaders);
		response.addHeader("Access-Control-Max-Age", "3600");
		if (request.getMethod() != "OPTIONS") {
			chain.doFilter(req, res);
		} else {
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}