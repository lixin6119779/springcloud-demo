package org.springcloud.gateway.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.netflix.hystrix.exception.HystrixTimeoutException;

//熔断处理
@Component
public class ProducerFallback implements FallbackProvider {
	private Logger logger = LoggerFactory.getLogger(ProducerFallback.class);
	 @Override
	 //指定所有路由
	    public String getRoute() {
	        return "*";
	    }

	    @Override
	    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
	        logger.error(String.format("route:%s,exceptionType:%s,stackTrace:%s", route, cause.getClass().getName(), cause.getStackTrace()));
	        cause.printStackTrace();
	        String message = "";
	        if (cause instanceof HystrixTimeoutException) {
	            message = "Timeout";
	        } else {
	            message = "Service exception";
	        }
	        return fallbackResponse(message);
	    }

	    public ClientHttpResponse fallbackResponse(String message) {

	        return new ClientHttpResponse() {
	            @Override
	            public HttpStatus getStatusCode() throws IOException {
	                return HttpStatus.OK;
	            }

	            @Override
	            public int getRawStatusCode() throws IOException {
	                return 200;
	            }

	            @Override
	            public String getStatusText() throws IOException {
	                return "OK";
	            }

	            @Override
	            public void close() {

	            }

	            @Override
	            public InputStream getBody() throws IOException {
	                String bodyText = String.format("{\"msg\":"+message+",\"success\":"+false+",\"data\":"+null+"}");
	                return new ByteArrayInputStream(bodyText.getBytes());
	            }

	            @Override
	            public HttpHeaders getHeaders() {
	                HttpHeaders headers = new HttpHeaders();
	                headers.setContentType(MediaType.APPLICATION_JSON);
	                return headers;
	            }
	        };

	    }

}
