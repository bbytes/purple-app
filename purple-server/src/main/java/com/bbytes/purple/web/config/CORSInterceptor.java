//package com.bbytes.purple.web.config;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import com.bbytes.purple.utils.GlobalConstants;
//
///**
// * This CORSInterceptor is called only in dev profile mode
// * 
// * @author Thanneer
// *
// */
//public class CORSInterceptor extends HandlerInterceptorAdapter {
//
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//			throws Exception {
//
//		String accessControlAllowHeaders = GlobalConstants.HEADER_AUTH_TOKEN + "," + GlobalConstants.HEADER_TENANT_ID
//				+ "," + "Origin, X-Requested-With, Content-Type, Accept";
//		response.setHeader("Access-Control-Allow-Credentials", "true");
//		response.addHeader("Access-Control-Allow-Origin", "*");
//		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
//		response.addHeader("Access-Control-Allow-Headers", accessControlAllowHeaders);
//		response.addHeader("Access-Control-Expose-Headers", accessControlAllowHeaders);
//		response.addHeader("Access-Control-Max-Age", "3600");
//		return true;
//	}
//
//}