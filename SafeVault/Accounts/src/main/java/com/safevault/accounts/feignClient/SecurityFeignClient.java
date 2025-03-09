package com.safevault.accounts.feignClient;

import com.safevault.accounts.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "SECURITY")
public interface SecurityFeignClient {

    @PostMapping("api/v1/security/validate")
    public ResponseEntity<?> validate(@RequestHeader("X-User-Id") String userId);
}
