package com.safevault.transactions.service;

import org.springframework.http.ResponseEntity;

public class TransactionServiceImp implements TransactionService {

    @Override
    public ResponseEntity<?> initiateTransaction(Long accountFrom, Long accountTo, Double amount) {
        
    }
}
