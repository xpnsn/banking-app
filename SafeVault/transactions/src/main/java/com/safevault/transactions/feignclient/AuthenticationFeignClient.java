package com.safevault.transactions.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "SECURITY")
public interface AuthenticationFeignClient {

    @PostMapping("api/v1/security/validate")
    public ResponseEntity<?> validate();

    @PostMapping("api/v1/security/get-phone-number/{userId}")
    public ResponseEntity<?> getPhoneNumber(@PathVariable("userId") String userId);
}
