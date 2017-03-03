package com.bbytes.purple.utils;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public final class RestTemplateUtil {

	public static RestTemplate getSSLNoCheckRestTemlate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		HttpClient httpClient = getHttpClient();

		HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
				httpClient);

		RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
		return restTemplate;
	}

	private static HttpClient getHttpClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		org.apache.http.ssl.SSLContextBuilder sslContextBuilder = SSLContextBuilder.create();
		sslContextBuilder.loadTrustMaterial(new org.apache.http.conn.ssl.TrustSelfSignedStrategy());
		SSLContext sslContext = sslContextBuilder.build();
		org.apache.http.conn.ssl.SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
				new org.apache.http.conn.ssl.DefaultHostnameVerifier());

		Registry<ConnectionSocketFactory> connectionSocketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslSocketFactory).build();

		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(connectionSocketFactoryRegistry);
		connManager.setMaxTotal(10);
		connManager.setDefaultMaxPerRoute(5);

		HttpClient httpclient = HttpClients.custom().setConnectionManager(connManager).build();
		return httpclient;
	}

}