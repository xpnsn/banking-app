package com.safevault.accounts.feignClient;

import com.safevault.accounts.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "SECURITY")
public interface SecurityFeignClient {

    @PostMapping("api/v1/security/validate")
    public ResponseEntity<?> validate();

    @PostMapping("api/v1/security/add-account-to-user")
    public ResponseEntity<String> addAccountToUser(@RequestParam String accountId);
}
