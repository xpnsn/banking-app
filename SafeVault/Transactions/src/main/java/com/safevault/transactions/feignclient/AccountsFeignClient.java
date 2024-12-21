package com.safevault.transactions.feignclient;

import com.safevault.transactions.dto.AccountDto;
import com.safevault.transactions.dto.TransferRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ACCOUNTS")
public interface AccountsFeignClient {

    @PostMapping("api/v1/accounts/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request);

    @GetMapping("api/v1/accounts/{id}")
    public AccountDto getAccountById(@PathVariable Long id);

}
