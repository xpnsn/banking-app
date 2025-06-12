package com.safevault.transactions.service;

import com.safevault.transactions.dto.TransactionFilterRequest;
import com.safevault.transactions.dto.TransactionRequest;
import com.safevault.transactions.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;

public interface TransactionService {
    ResponseEntity<?> initiateTransaction(TransactionRequest request) throws Exception;
    ResponseEntity<?> getAccount(Long id);

    ResponseEntity<?> getTransactions(Long id, String type, Pageable pageable);

    ResponseEntity<?> getTransactionFilter(TransactionFilterRequest request, Pageable pageable);
}
