package com.safevault.accounts.controller;

import com.safevault.accounts.model.Account;
import com.safevault.accounts.service.AccountService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/accounts/admin")
public class AdminController {

    private final AccountService accountService;

    public AdminController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Hidden
    @GetMapping
    public String test() {
        return "test";
    }

    @Operation(summary = "Get unverified accounts")
    @GetMapping("unverified-accounts")
    public ResponseEntity<?> getUnverifiedAccounts() {
        return accountService.getUnverifiedAccounts();
    }

    @Operation(summary = "Verify a account")
    @PostMapping("verify-account/{accountId}")
    public ResponseEntity<?> verifyAccount(@PathVariable Long accountId) {
        return accountService.verifyAccount(accountId);
    }
}
