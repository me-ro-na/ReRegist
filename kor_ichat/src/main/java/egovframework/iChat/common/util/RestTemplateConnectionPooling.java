package egovframework.iChat.common.util;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class RestTemplateConnectionPooling {

	private final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
	
	private static class Holder {
		private static final RestTemplateConnectionPooling INSTANCE = 
				new RestTemplateConnectionPooling();
	}
	
	public static RestTemplateConnectionPooling getInstance() {
		return Holder.INSTANCE;
	}
	
	private RestTemplateConnectionPooling() {
	
		RequestConfig requestConfig = RequestConfig.custom()
									.setSocketTimeout(3000)
									.setConnectTimeout(3000)
									.setConnectionRequestTimeout(3000)
									.build();
		
		PoolingHttpClientConnectionManager poolingManager = new PoolingHttpClientConnectionManager();
		poolingManager.setMaxTotal(200);
		poolingManager.setDefaultMaxPerRoute(30);
		
		CloseableHttpClient httpClientBuilder = HttpClientBuilder.create().setConnectionManager(poolingManager).setDefaultRequestConfig(requestConfig).build();
		requestFactory.setHttpClient(httpClientBuilder);
		
//		requestFactory.setReadTimeout(5000); 	//읽기시간초과, ms
//		requestFactory.setConnectTimeout(3000); //연결시간초과, ms
//		
//		HttpClient httpClient = HttpClientBuilder.create()
//								.setMaxConnTotal(200)   // 최대 오픈되는 커넥션 수
//								.setMaxConnPerRoute(30) // IP,Port 한쌍에 대해 수행 할 연결 수를 제한
//								.build();
//		requestFactory.setHttpClient(httpClient);
		
	}

	
	public HttpComponentsClientHttpRequestFactory getRequestFactory() {
		return requestFactory;
	}
}
