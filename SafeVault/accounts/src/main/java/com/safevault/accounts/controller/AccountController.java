package com.safevault.accounts.controller;

import com.safevault.accounts.dto.*;
import com.safevault.accounts.dto.transactions.TransactionDto;
import com.safevault.accounts.service.AccountServiceImp;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Account details")
    @GetMapping("id/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {
        return service.getAccountById(id);
    }

    @Operation(summary = "Create Account")
    @PostMapping()
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody AccountCreationRequest accountCreationRequest,
            @RequestHeader("X-User-Id") String userId
    ) {
        return service.addAccount(accountCreationRequest, userId);
    }

    @Operation(summary = "Delete Account")
    @DeleteMapping()
    public ResponseEntity<?> deleteAccount(@Valid @RequestBody AccountDeletionRequest accountDeletionRequest) {
        return service.removeAccount(accountDeletionRequest);
    }

    @Hidden
    @PostMapping("test")
    public ResponseEntity<?> test() {
        return service.test();
    }

    @Hidden
    @PostMapping("credit")
    public ResponseEntity<?> creditAccount(@RequestBody CreditDebitRequest request) {
        return service.creditAccount(request, false);
    }

    @Hidden
    @PostMapping("debit")
    public ResponseEntity<?> debitAccount(@RequestBody CreditDebitRequest request) {
        return service.debitAccount(request);
    }

    @Hidden
    @GetMapping()
    public ResponseEntity<?> getAllAccounts(@RequestHeader("X-User-Id") String userId) {
        return service.getAccountsByUserId(userId);
    }

    @Hidden
    @PostMapping("transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        return service.transfer(request);
    }

    @Hidden
    @PostMapping("add-transaction")
    public ResponseEntity<?> addTransaction(@RequestBody TransactionDto transactionDto) {
        return service.addTransactionToAccount(transactionDto);
    }
}
