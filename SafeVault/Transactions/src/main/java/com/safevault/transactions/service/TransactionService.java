package com.safevault.transactions.service;

import com.safevault.transactions.dto.AccountDto;
import com.safevault.transactions.dto.TransferRequest;
import org.springframework.http.ResponseEntity;

public interface TransactionService {
    public ResponseEntity<?> initiateTransaction(TransferRequest request) throws Exception;
    public ResponseEntity<?> getAccount(Long id);
}
