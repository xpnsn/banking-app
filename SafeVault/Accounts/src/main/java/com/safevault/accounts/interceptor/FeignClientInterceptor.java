package com.safevault.accounts.interceptor;

import com.safevault.accounts.config.SecurityConfig;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private final SecurityConfig securityConfig;

    public FeignClientInterceptor(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("X-Secret-Key", securityConfig.getSecretKey());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(attributes != null) {
            HttpServletRequest currRequest = attributes.getRequest();
            String userId = currRequest.getHeader("X-User-Id");
            if(userId != null) {
                requestTemplate.header("X-User-Id", userId);
            }

            String authorities = currRequest.getHeader("X-Authorities");
            if(authorities != null) {
                requestTemplate.header("X-Authorities", authorities);
            }

            String username = currRequest.getHeader("X-Username");
            if(username != null) {
                requestTemplate.header("X-Username", username);
            }

        }
    }
}
