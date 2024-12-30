package com.safevault.accounts.controller;

import com.safevault.accounts.dto.*;
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

    @GetMapping("id/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {
        return service.getAccountById(id);
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
    public ResponseEntity<?> creditAccount(@RequestBody CreditDebitRequest request) {
        return service.creditAccount(request);
    }

    @PostMapping("debit")
    public ResponseEntity<?> debitAccount(@RequestBody CreditDebitRequest request) {
        return service.debitAccount(request);
    }

    @PostMapping("transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        return service.transfer(request);
    }
}
