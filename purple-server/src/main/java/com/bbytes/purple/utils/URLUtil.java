package com.bbytes.purple.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class URLUtil {

	public static String getBaseURL(String fullURL) {

		try {
			URL url = new URL(fullURL);
			String baseUrl = url.getProtocol() + "://" + url.getHost();
			return baseUrl;
		} catch (MalformedURLException e) {
			// do something
		}
		return "";
	}
	
	public static String getJiraIssueURL(String baseURL ,String issueKey) {
		return baseURL+"/browse/"+issueKey;
	}
	
	public static String getHTMLHref(String url ,String urlDisplayName) {
		return "<a href=\""+ url+"\" target=\"_blank\">"+urlDisplayName+"</a>";
	}
	
	
}
