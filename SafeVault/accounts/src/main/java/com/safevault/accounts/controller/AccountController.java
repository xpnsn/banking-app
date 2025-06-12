package com.safevault.accounts.controller;

import com.safevault.accounts.dto.*;
import com.safevault.accounts.dto.transactions.TransactionDto;
import com.safevault.accounts.service.AccountServiceImp;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/accounts")
public class AccountController {

    private final AccountServiceImp service;

    public AccountController(AccountServiceImp service) {
        this.service = service;
    }

    @GetMapping("id/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {
        return service.getAccountById(id);
    }

    @PostMapping()
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody AccountCreationRequest accountCreationRequest,
            @RequestHeader("X-User-Id") String userId
    ) {
        return service.addAccount(accountCreationRequest, userId);
    }


    @DeleteMapping()
    public ResponseEntity<?> deleteAccount(@Valid @RequestBody AccountDeletionRequest accountDeletionRequest) {
        return service.removeAccount(accountDeletionRequest);
    }

    @PostMapping("test")
    public ResponseEntity<?> test() {
        return service.test();
    }

    @PostMapping("credit")
    public ResponseEntity<?> creditAccount(@RequestBody CreditDebitRequest request) {
        return service.creditAccount(request, false);
    }

    @PostMapping("debit")
    public ResponseEntity<?> debitAccount(@RequestBody CreditDebitRequest request) {
        return service.debitAccount(request);
    }

    @GetMapping()
    public ResponseEntity<?> getAllAccounts(@RequestHeader("X-User-Id") String userId) {
        return service.getAccountsByUserId(userId);
    }

    @PostMapping("transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        return service.transfer(request);
    }

    @PostMapping("add-transaction")
    public ResponseEntity<?> addTransaction(@RequestBody TransactionDto transactionDto) {
        return service.addTransactionToAccount(transactionDto);
    }
}
