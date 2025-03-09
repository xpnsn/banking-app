package com.safevault.transactions.interceptor;

import com.safevault.transactions.config.SecurityConfig;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
    @Autowired
    private SecurityConfig securityConfig;
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("X-Secret-Key", securityConfig.getSecretKey());
    }
}
