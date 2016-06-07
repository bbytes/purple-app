package com.bbytes.purple.utils;

import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import com.google.common.net.InternetDomainName;

public class MultiTenantUtils {

	public static String getTenantId(HttpServletRequest request) {
		if (request == null)
			return null;

		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			return null;
		}

		String tenantId = request.getHeader(GlobalConstants.HEADER_TENANT_ID);
		if (tenantId != null && !tenantId.trim().isEmpty())
			return tenantId;

		return getTenantId(request.getRequestURL().toString());
	}

	private static String getTenantId(String url) {
		LinkedList<String> result = getSubDomainParts(url);
		if (!result.isEmpty())
			return result.getLast();

		return null;
	}

	private static LinkedList<String> getSubDomainParts(String url) {
		InternetDomainName fullDomainName = InternetDomainName.from(url);
		InternetDomainName privateDomainName = fullDomainName.topPrivateDomain();
		LinkedList<String> nonePublicParts = new LinkedList<String>(fullDomainName.parts());
		nonePublicParts.removeAll(privateDomainName.parts());
		return nonePublicParts;
	}
}
