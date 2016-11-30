package com.bbytes.purple.utils;

import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

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

	/**
	 * Obtain the current active <code>Authentication</code>
	 *
	 * @return the authentication object or <code>null</code>
	 */
	private static Authentication getAuthentication() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {
			return auth;
		}

		return null;
	}

	/**
	 * Will always return <code>false</code> if the
	 * <code>SecurityContextHolder</code> contains an
	 * <code>Authentication</code> with <code>null</code>
	 * <code>GrantedAuthority[]</code> objects.
	 * 
	 * @param role
	 *            <code>GrantedAuthority</code><code>String</code>
	 *            representation to check for
	 * @return <code>true</code> if an <b>exact</b> (case sensitive) matching
	 *         granted authority is located, <code>false</code> otherwise
	 */
	public static boolean isUserInRole(String role) {
		Authentication auth = getAuthentication();

		if ((auth == null)) {
			return false;
		}

		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

		if (authorities == null) {
			return false;
		}

		for (GrantedAuthority grantedAuthority : authorities) {
			if (role.equals(grantedAuthority.getAuthority())) {
				return true;
			}
		}

		return false;
	}
}
