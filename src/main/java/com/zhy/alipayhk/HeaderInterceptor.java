package com.zhy.alipayhk;

import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * @author Liushengping
 * @since 2021/12/2
 */
public class HeaderInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution requestExecution) throws IOException {
        request.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        return requestExecution.execute(request, bytes);
    }

}
