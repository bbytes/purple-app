package com.bbytes.purple.utils;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.httpclient.apache.httpcomponents.DefaultHttpClientFactory;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.factory.HttpClientOptions;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AtlassianHttpClientDecorator;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.executor.ThreadLocalContextManager;

/**
 * Factory for asynchronous http clients.
 *
 * @since v2.0
 */
public class JiraHttpClientFactory {

	@SuppressWarnings("unchecked")
	public DisposableHttpClient createClient(final URI serverUri, final AuthenticationHandler authenticationHandler) {
		final HttpClientOptions options = new HttpClientOptions();
		options.setTrustSelfSignedCertificates(true);
		options.setRequestTimeout(30, TimeUnit.SECONDS);
		options.setMaxConnectionsPerHost(500);
		options.setMaxCallbackThreadPoolSize(200);

		final DefaultHttpClientFactory defaultHttpClientFactory = new DefaultHttpClientFactory(new NoOpEventPublisher(),
				new RestClientApplicationProperties(serverUri), new ThreadLocalContextManager() {
					@Override
					public Object getThreadLocalContext() {
						return null;
					}

					@Override
					public void setThreadLocalContext(Object context) {
					}

					@Override
					public void clearThreadLocalContext() {
					}
				});

		final HttpClient httpClient = defaultHttpClientFactory.create(options);

		return new AtlassianHttpClientDecorator(httpClient, authenticationHandler) {
			@Override
			public void destroy() throws Exception {
				defaultHttpClientFactory.dispose(httpClient);
			}
		};
	}

	public DisposableHttpClient createClient(final HttpClient client) {
		return new AtlassianHttpClientDecorator(client, null) {

			@Override
			public void destroy() throws Exception {
				// This should never be implemented. This is simply creation of
				// a wrapper
				// for AtlassianHttpClient which is extended by a destroy
				// method.
				// Destroy method should never be called for AtlassianHttpClient
				// coming from
				// a client! Imagine you create a RestClient, pass your own
				// HttpClient there
				// and it gets destroy.
			}
		};
	}

	private static class NoOpEventPublisher implements EventPublisher {
		@Override
		public void publish(Object o) {
		}

		@Override
		public void register(Object o) {
		}

		@Override
		public void unregister(Object o) {
		}

		@Override
		public void unregisterAll() {
		}
	}

	/**
	 * These properties are used to present JRJC as a User-Agent during http
	 * requests.
	 */
	@SuppressWarnings("deprecation")
	private static class RestClientApplicationProperties implements ApplicationProperties {

		private final String baseUrl;

		private RestClientApplicationProperties(URI jiraURI) {
			this.baseUrl = jiraURI.getPath();
		}

		@Override
		public String getBaseUrl() {
			return baseUrl;
		}

		/**
		 * We'll always have an absolute URL as a client.
		 */
		@Nonnull
		@Override
		public String getBaseUrl(UrlMode urlMode) {
			return baseUrl;
		}

		@Nonnull
		@Override
		public String getDisplayName() {
			return "Atlassian JIRA Rest Java Client";
		}

		@Nonnull
		@Override
		public String getPlatformId() {
			return ApplicationProperties.PLATFORM_JIRA;
		}

		@Nonnull
		@Override
		public String getVersion() {
			return "1.0.0";
		}

		@Nonnull
		@Override
		public Date getBuildDate() {
			// TODO implement using MavenUtils, JRJC-123
			throw new UnsupportedOperationException();
		}

		@Nonnull
		@Override
		public String getBuildNumber() {
			// TODO implement using MavenUtils, JRJC-123
			return String.valueOf(0);
		}

		@Override
		public File getHomeDirectory() {
			return new File(".");
		}

		@Override
		public String getPropertyValue(final String s) {
			throw new UnsupportedOperationException("Not implemented");
		}
	}

}
