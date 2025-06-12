package com.safevault.accounts.controller;

import com.safevault.accounts.model.Account;
import com.safevault.accounts.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/accounts/admin")
public class AdminController {

    private final AccountService accountService;

    public AdminController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public String test() {
        return "test";
    }

    @GetMapping("unverified-accounts")
    public ResponseEntity<?> getUnverifiedAccounts() {
        return accountService.getUnverifiedAccounts();
    }

    @PostMapping("verify-account/{accountId}")
    public ResponseEntity<?> verifyAccount(@PathVariable Long accountId) {
        return accountService.verifyAccount(accountId);
    }
}
