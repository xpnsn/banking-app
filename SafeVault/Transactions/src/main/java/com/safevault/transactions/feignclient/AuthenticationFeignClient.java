package com.safevault.transactions.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "SECURITY")
public interface AuthenticationFeignClient {

    @PostMapping("api/v1/security/validate")
    public ResponseEntity<?> validate();
}
