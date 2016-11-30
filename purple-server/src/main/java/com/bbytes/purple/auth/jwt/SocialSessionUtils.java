package com.bbytes.purple.auth.jwt;

import javax.servlet.http.HttpServletRequest;

import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import com.bbytes.purple.utils.GlobalConstants;

public class SocialSessionUtils {

	public static String geEmailId(RequestAttributes request) {
		if (request == null)
			return null;

		SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();
		String tenantId = (String) sessionStrategy.getAttribute(request, GlobalConstants.URL_PARAM_EMAIL_ID);

		return tenantId;

	}

	public static void clearEmailIdInRequest(RequestAttributes request) {
		if (request == null)
			return;

		SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();
		sessionStrategy.removeAttribute(request, GlobalConstants.URL_PARAM_EMAIL_ID);
	}

	public static boolean storeEmailIdToSession(HttpServletRequest request) {
		SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();
		if (request.getParameter(GlobalConstants.URL_PARAM_EMAIL_ID) != null) {
			sessionStrategy.setAttribute(new ServletWebRequest(request), GlobalConstants.URL_PARAM_EMAIL_ID,
					request.getParameter(GlobalConstants.URL_PARAM_EMAIL_ID));
			return true;
		}

		if (sessionStrategy.getAttribute(new ServletWebRequest(request), GlobalConstants.URL_PARAM_EMAIL_ID) != null)
			return true;

		return false;
	}

}
