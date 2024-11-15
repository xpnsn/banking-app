package com.safevault.accounts.controller;

import com.safevault.accounts.dto.AccountCreationRequest;
import com.safevault.accounts.dto.AccountDeletionRequest;
import com.safevault.accounts.dto.AccountDto;
import com.safevault.accounts.service.AccountServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/accounts")
public class AccountController {

    private final AccountServiceImp service;

    public AccountController(AccountServiceImp service) {
        this.service = service;
    }

    @PostMapping()
    public ResponseEntity<?> createAccount(@RequestBody AccountCreationRequest accountCreationRequest) {
        return service.addAccount(accountCreationRequest);
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteAccount(@RequestBody AccountDeletionRequest accountDeletionRequest) {
        return service.removeAccount(accountDeletionRequest);
    }

    @GetMapping("test")
    public ResponseEntity<?> test() {
        return new ResponseEntity<>("test passed", HttpStatus.OK);
    }

    @PostMapping("credit")
    public ResponseEntity<?> creditAccount(@RequestParam Long id, @RequestParam Double amount) {
        return service.creditAccount(id, amount);
    }


}
