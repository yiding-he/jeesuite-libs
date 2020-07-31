package com.jeesuite.springweb.client;

import com.jeesuite.springweb.utils.WebUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.Map;

public class RestTemplateAutoHeaderInterceptor implements ClientHttpRequestInterceptor {


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        try {
            Map<String, String> customHeaders = WebUtils.getCustomHeaders();
            request.getHeaders().setAll(customHeaders);
        } catch (Exception e) {
        }
        return execution.execute(request, body);
    }


}
