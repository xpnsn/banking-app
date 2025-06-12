package com.safevault.transactions.feignclient;

import com.safevault.transactions.dto.AddTransactionRequest;
import com.safevault.transactions.dto.TransactionDto;
import com.safevault.transactions.dto.accounts.AccountDto;
import com.safevault.transactions.dto.TransactionRequest;
import com.safevault.transactions.dto.accounts.CreditDebitRequest;
import com.safevault.transactions.dto.accounts.TransferRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ACCOUNTS")
public interface AccountsFeignClient {

    @PostMapping("api/v1/accounts/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request);

    @GetMapping("api/v1/accounts/id/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id);

    @PostMapping("api/v1/accounts/credit")
    public ResponseEntity<?> creditAccount(@RequestBody CreditDebitRequest request);

    @PostMapping("api/v1/accounts/debit")
    public ResponseEntity<?> debitAccount(@RequestBody CreditDebitRequest request);

    @PostMapping("api/v1/accounts/add-transaction")
    public ResponseEntity<?> addTransaction(@RequestBody TransactionDto transactionDto);
}
