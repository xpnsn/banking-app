package com.safevault.api_gateway.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "SECURITY")
public interface UserClient {

    @PostMapping("api/v1/security/validate")
    public ResponseEntity<?> validate();
}
