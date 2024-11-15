package com.safevault.transactions.service;

import org.springframework.http.ResponseEntity;

public interface TransactionService {
    public ResponseEntity<?> initiateTransaction(Long accountFrom, Long accountTo, Double amount);
}
