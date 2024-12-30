package com.safevault.transactions.service;

import com.safevault.transactions.dto.TransactionRequest;
import org.springframework.http.ResponseEntity;

public interface TransactionService {
    public ResponseEntity<?> initiateTransaction(TransactionRequest request) throws Exception;
    public ResponseEntity<?> getAccount(Long id);
}
