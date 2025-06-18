package com.safevault.accounts.feignClient;

import com.safevault.accounts.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "SECURITY")
public interface SecurityFeignClient {

    @PostMapping("api/v1/security/validate")
    public ResponseEntity<?> validate();

    @PostMapping("api/v1/security/add-account-to-user")
    public ResponseEntity<?> addAccountToUser(@RequestParam String accountId);

    @PostMapping("api/v1/security/remove-account-from-user")
    public ResponseEntity<?> removeAccountFromUser(@RequestParam String accountId, @RequestParam String password);

    @GetMapping(value = "api/v1/security/get-phone-number/{userId}")
    public ResponseEntity<?> getPhoneNumber(@PathVariable String userId);
}
